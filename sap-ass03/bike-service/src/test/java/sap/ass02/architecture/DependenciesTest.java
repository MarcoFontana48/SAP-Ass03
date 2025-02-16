package sap.ass02.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class DependenciesTest {
    private static final Logger LOGGER = LogManager.getLogger(DependenciesTest.class);

    @Test
    public void layerDependenciesAreRespected() {
        layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("Infrastructure").definedBy("sap.ass02.infrastructure..")
                .layer("Application").definedBy("sap.ass02.application..")
                .layer("Domain").definedBy("sap.ass02.domain..")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .check(new ClassFileImporter().importPackages("sap.ass02"));
    }
}