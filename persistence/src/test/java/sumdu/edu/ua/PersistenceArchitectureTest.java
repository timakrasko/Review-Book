package sumdu.edu.ua;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class PersistenceArchitectureTest {

    @Test
    void repositoriesShouldResideInJdbcPackage() {
        JavaClasses imported = new ClassFileImporter()
                .importPackages("sumdu.edu.ua");

        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..jdbc")
                .check(imported);
    }
}
