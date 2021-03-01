package de.thl.violat.model;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.diagnostic.Logger;
import de.thl.violat.run.ViolatProcessListener;
import de.thl.violat.run.ViolatRunConfiguration;
import de.thl.violat.specgenerator.GetSpecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassInitializer {
    private static final Logger log = Logger.getInstance(de.thl.violat.model.SpecificationInitializer.class);
    private String path;

    public ClassInitializer() {}

    private ClassInitializer(String path) {
        setPath(path);
    }

    @Nullable
    public static ClassInitializer createClassInitializer(String path) {
        ClassInitializer ii = new ClassInitializer(path);
        if(ii.confirm(path)) return ii;
        return null;
    }

    /**
     * Checks if the Infer Installation at the given path is valid.
     * @param path Full path to the infer binary
     * @return The Version if the installation is valid, otherwise null
     */
    @Nullable
    public Boolean confirm(@NotNull String path) {
        if(path.contains(".")) {
            return true;
        } else {
            return false;
        }
    }

    public void setPath(String path) {
         this.path = path;
    }

    public static void generateSpecs(String path) throws ClassNotFoundException {
        if (path != null) {
            JsonObject j = GetSpecs.getSpecs(path);

            try (FileWriter file = new FileWriter("Spec.json")) {
                file.write(j.toJson());
                file.flush();
            } catch (IOException e) {
                System.out.println("Noo canot generate Specs -- here\n\n\n\n\n");
                e.printStackTrace();
            }
        }
    }

//    public static void compileClassAndCreateJar(String path, String packages) throws IOException {
//        // Compile the java class
//        Process compileProcess = new ProcessBuilder("javac", path).start();
//
//        // Create a jar file through scripting
//        String mainPackage = packages.split("\\.")[0];
//        String jarName = mainPackage + ".jar";
//        String jarPath = path.substring(0, path.indexOf(mainPackage)) + mainPackage;
//        Process artifactProcess = new ProcessBuilder("jar", "cvf", jarName, path).start();
//
//        // Remove the compiled java class
//        String pathToClassFile = path.substring(0, path.indexOf(".java")) + ".class";
//        Process removeProcess = new ProcessBuilder("rm", pathToClassFile).start();
//    }



    public static String compileClassAndCreateJar(String path, String packages) throws IOException, InterruptedException {

        // Compile the java class
        Process compileProcess = new ProcessBuilder("javac", path).start();
        compileProcess.waitFor();

        // Create a jar file through scripting
        String mainPackage = packages.split("\\.")[0];

        String basePath = path.substring(0, path.indexOf(mainPackage));
        String mainPackagePath = basePath + mainPackage;
        String jarName = mainPackage + ".jar";
        String pathToJarFolder = basePath + "jarFiles/";
        String jarPath = pathToJarFolder + jarName;
        System.out.println(jarPath);
        System.out.println("\n\n\n\n\n");
        Process makeJarFolderProcess = new ProcessBuilder("mkdir", pathToJarFolder).start();
        makeJarFolderProcess.waitFor();
        Process artifactProcess = new ProcessBuilder("jar", "cvf", jarPath, mainPackagePath).start();
        artifactProcess.waitFor();

        // Remove the compiled java class
        String pathToClassFile = path.substring(0, path.indexOf(".java")) + ".class";
        Process removeProcess = new ProcessBuilder("rm", pathToClassFile).start();

        removeProcess.waitFor();
        return jarPath;
    }

}

