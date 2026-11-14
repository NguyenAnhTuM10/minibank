package com.minibank;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Date;
import org.springframework.data.repository.Repository;

/**
 * Executable version of the rules in docs/ARCHITECTURE.md. A failing rule here means a layer or
 * module boundary was crossed: fix the code, never loosen the rule.
 *
 * <p>Rules allow empty matches while modules are still being built; they start checking as soon as
 * matching classes exist.
 */
@AnalyzeClasses(packages = "com.minibank", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String ROOT = "com.minibank.";
    private static final String WEB = "com.minibank..web..";
    private static final String SERVICE = "com.minibank..service..";
    private static final String DOMAIN = "com.minibank..domain..";
    private static final String INFRA = "com.minibank..infra..";
    private static final String COMMON = "com.minibank.common..";

    @ArchTest
    static final ArchRule layersAreRespected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .withOptionalLayers(true)
            .layer("Web")
            .definedBy(WEB)
            .layer("Service")
            .definedBy(SERVICE)
            .layer("Domain")
            .definedBy(DOMAIN)
            .layer("Infra")
            .definedBy(INFRA)
            .whereLayer("Web")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Infra")
            .mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule webAndInfraArePrivateToTheirModule = classes()
            .that()
            .resideInAnyPackage(WEB, INFRA)
            .should(onlyBeAccessedFromTheirOwnModule())
            .because("modules talk to each other through services, events and domain types")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllersDoNotUseRepositories = noClasses()
            .that()
            .resideInAPackage(WEB)
            .should()
            .dependOnClassesThat()
            .areAssignableTo(Repository.class)
            .because("controllers go through a service, which owns the transaction")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses()
            .that()
            .resideInAPackage(DOMAIN)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.springframework.web..", "jakarta.servlet..")
            .because("business rules must not depend on HTTP")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule commonDependsOnNoModule = noClasses()
            .that()
            .resideInAPackage(COMMON)
            .should()
            .dependOnClassesThat(resideInAPackage("com.minibank..").and(resideOutsideOfPackage(COMMON)))
            .because("every module depends on common, so common depending on a module is a cycle")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule modulesHaveNoCycles =
            slices().matching("com.minibank.(*)..").should().beFreeOfCycles().allowEmptyShould(true);

    @ArchTest
    static final ArchRule moneyIsNeverFloatingPoint = noFields()
            .should()
            .haveRawType(double.class)
            .orShould()
            .haveRawType(Double.class)
            .orShould()
            .haveRawType(float.class)
            .orShould()
            .haveRawType(Float.class)
            .because("floating point cannot represent money exactly; use BigDecimal")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule timeUsesJavaTime = noClasses()
            .should()
            .dependOnClassesThat()
            .areAssignableTo(Date.class)
            .because("timestamps are java.time.Instant in UTC; JWTs use Spring Security's JwtClaimsSet");

    @ArchTest
    static final ArchRule constructorInjectionOnly = NO_CLASSES_SHOULD_USE_FIELD_INJECTION.allowEmptyShould(true);

    @ArchTest
    static final ArchRule noSystemOutOrErr = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    private static ArchCondition<JavaClass> onlyBeAccessedFromTheirOwnModule() {
        return new ArchCondition<>("only be accessed from their own module") {
            @Override
            public void check(JavaClass target, ConditionEvents events) {
                String module = moduleOf(target);
                for (Dependency dependency : target.getDirectDependenciesToSelf()) {
                    if (!module.equals(moduleOf(dependency.getOriginClass()))) {
                        events.add(SimpleConditionEvent.violated(dependency, dependency.getDescription()));
                    }
                }
            }
        };
    }

    /** Module name is the first package segment after com.minibank, e.g. "account". */
    private static String moduleOf(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();
        if (!packageName.startsWith(ROOT)) {
            return "";
        }
        String rest = packageName.substring(ROOT.length());
        int dot = rest.indexOf('.');
        return dot < 0 ? rest : rest.substring(0, dot);
    }
}
