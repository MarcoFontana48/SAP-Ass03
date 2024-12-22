package sap.ass02.infrastructure.presentation.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Custom class loader for loading classes from a JAR file.
 */
public final class PluginClassLoader extends ClassLoader {
    private final JarFile jarFile;

    /**
     * Creates a new PluginClassLoader for the given JAR file.
     *
     * @param jarFilePath the path to the JAR file
     * @throws Exception if an error occurs while opening the JAR file
     */
    public PluginClassLoader(String jarFilePath) throws Exception {
        this.jarFile = new JarFile(jarFilePath);
    }

    /**
     * Loads the class with the specified name.
     *
     * @param name the name of the class
     * @return the Class object representing the loaded class
     * @throws ClassNotFoundException if the class could not be found
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	// Convert the class name to the expected path in the JAR
        String classFile = name.replace('.', '/') + ".class";
        JarEntry entry = this.jarFile.getJarEntry(classFile);

        if (entry == null) {
            throw new ClassNotFoundException(name);
        }

        try (InputStream input = this.jarFile.getInputStream(entry)) {
            // Read the class bytes
            byte[] classData = input.readAllBytes();
            // Define the class using the byte array
            return this.defineClass(name, classData, 0, classData.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
