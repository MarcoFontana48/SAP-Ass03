package sap.ass02.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistanceFromMainSequenceTest {
    private static final Logger LOGGER = LogManager.getLogger(DistanceFromMainSequenceTest.class);
    
    @Test
    public void evaluate_distance_from_main_sequence() {
        double abstractness = this.evaluateAbstractness();
        double instability = this.evaluateInstability();
        double distanceFromMainSequence = abstractness + instability - 1;
        LOGGER.info("Distance from main sequence: {}", distanceFromMainSequence);
        
        // between (-1 and -1/3) or (1/3 and 1) --> fails   (1.333 delta)
        // between -1/3 and 1/3                 --> passes  (0.666 delta)
        assertEquals(0.0, distanceFromMainSequence, (double) 1 / 3);
    }
    
    private double evaluateAbstractness() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass02");
        
        Set<JavaClass> allClasses = new HashSet<>(importedClasses);
        Set<JavaClass> abstractClasses = allClasses.stream()
                .filter(javaClass -> Modifier.isAbstract(javaClass.reflect().getModifiers()) || Modifier.isInterface(javaClass.reflect().getModifiers()))
                .collect(Collectors.toSet());
        
        return (double) abstractClasses.size() / allClasses.size();
    }
    
    private double evaluateInstability() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass02");
        
        Set<JavaClass> allClasses = new HashSet<>(importedClasses);
        
        double totalFanIn = allClasses.stream()
                .mapToLong(javaClass -> javaClass.getDirectDependenciesToSelf().size())
                .sum();
        
        double totalFanOut = allClasses.stream()
                .mapToLong(javaClass -> javaClass.getDirectDependenciesFromSelf().size())
                .sum();
        
        return totalFanOut / (totalFanIn + totalFanOut);
    }
    
}
