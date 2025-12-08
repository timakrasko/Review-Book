package sumdu.edu.ua;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CoreArchitectureTest {

    @Test
    void coreShouldNotDependOnInfrastructure() {
        JavaClasses imported = new ClassFileImporter()
                .importPackages("sumdu.edu.ua.core");

        noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..jdbc..", "..web..")
                .check(imported);
    }
}
