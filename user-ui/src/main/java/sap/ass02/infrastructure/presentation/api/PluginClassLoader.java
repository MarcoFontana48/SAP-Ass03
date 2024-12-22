package sap.ass02.infrastructure.presentation.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PluginClassLoader extends ClassLoader {
    
    private final JarFile jarFile;

    public PluginClassLoader(String jarFilePath) throws Exception {
        this.jarFile = new JarFile(jarFilePath);
    }

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
