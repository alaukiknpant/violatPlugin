package plugin.linearizability.violat.model;



import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;


import plugin.linearizability.violat.config.GlobalSettings;
import plugin.linearizability.violat.model.buildtool.BuildTool;
import plugin.linearizability.violat.model.buildtool.BuildToolFactory;

import plugin.linearizability.violat.model.buildtool.BuildToolChecker;
import plugin.linearizability.violat.model.tester.Testers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViolatLaunchOptions {
    private static final Logger log = Logger.getInstance(ViolatLaunchOptions.class);

    //Tracks changed files for use with the reactive mode
    private FileDocumentManagerListener changeListener;
    private List<String> changedFiles = new ArrayList<>();

    private List<BuildTool> availableBuildTools;
    private List<Testers> availableTesters;
//    private Boolean fullAnalysis; //if a full analysis was already done after loading the current project/creating the run config

    //Saved Infer Configuration Options
    private ViolatInstallation selectedInstallation;
//    private BuildTool usingBuildTool;
//    private String additionalArgs;
    private List<Checker> selectedCheckers;
    private Testers selectedTester;
    private Boolean reactiveMode;


    public ViolatLaunchOptions(Project project) {
        this.selectedInstallation = GlobalSettings.getInstance().getDefaultInstallation();
//        this.additionalArgs = "";
        this.selectedCheckers = Checker.getDefaultCheckers();
//        this.reactiveMode = false;
//        this.fullAnalysis = false;
//        createChangeFileListener();
        this.availableBuildTools = BuildToolFactory.getApplicableBuildTools(project);
        this.availableTesters = Testers.getTesters();
    }

    /**
     * Constructs the final infer launch command
     * @return Infer Launch Command
     */
    @NotNull
    public String buildViolatLaunchCmd(Project project) throws ExecutionException {
//        if(this.usingBuildTool == null) throw new ExecutionException("Violat Execution failed: No Build Tool selected");
        if(this.selectedInstallation == null) throw new ExecutionException("Violat Execution failed: No Installation selected");
        if(!BuildToolChecker.returnInvalidInstallations()) throw new ExecutionException("Violat Execution failed: You do not have all the software pre-reqs(Java, Maven, Gradle)");
        if(this.selectedCheckers == null || this.selectedCheckers.isEmpty()) throw new ExecutionException("Violat Execution failed: No Checkers selected");
        if(this.selectedCheckers.size() > 1) throw new ExecutionException("Violat Execution failed: More than 1 Checkers Selected");

        StringBuilder sb = new StringBuilder(this.selectedInstallation.getPath());


        //Checkers
        for(Checker checker : selectedCheckers) {
            sb.append(checker.getActivationArgument()).append(" ");
        }

        //Specs
        final String pathToSpecs = GlobalSettings.getInstance().getJsonSpecs();
        if(pathToSpecs == null || pathToSpecs.isEmpty()) throw new ExecutionException("Violat Execution failed: Could not find the JSON specs");
        sb.append(pathToSpecs).append(" ");

        // JAR
        final String pathToArtifact = GlobalSettings.getInstance().getArtifactSpecs();
//        if(pathToArtifact == null || pathToSpecs.isEmpty()) throw new ExecutionException("Violat Execution failed: Could not find the JSON specs");
        if (!(pathToArtifact == null) && !pathToSpecs.isEmpty()) {
            sb.append("--jar").append(" ").append(pathToArtifact).append(" ");
        }

        // Tester
        final String selectedTester = GlobalSettings.getInstance().getTester();
        if(selectedTester == null || selectedTester.isEmpty()) throw new ExecutionException("Violat Execution failed: Tester not selected Correctly");
        if (!(selectedTester == null) && !selectedTester.isEmpty()) {
            sb.append("--tester").append(" ").append(selectedTester);
        }



        //Additional Arguments - might not need this because -validaor and histories are plugged in as activation arguments below
//        sb.append(" ").append(additionalArgs);
//        sb.append(additionalArgs);

        // There is no de-activation argument for Violat
//        for(Checker checker : Checker.getMissingCheckers(selectedCheckers, this.selectedInstallation.getVersion())) {
//            sb.append(checker.getDeactivationArgument()).append(" ");
//        }

        //Reactive Mode
//        if(this.reactiveMode && this.fullAnalysis) {
//            if(!changedFiles.isEmpty()) {
//                try {
//                    final Path file = Paths.get(project.getBasePath() + "/changedfiles.txt");
//                    Files.write(file, changedFiles, StandardCharsets.UTF_8);
//                    sb.append("--reactive --changed-files-index changedfiles.txt ");
//                } catch (IOException ioe) {
//                    log.error(ioe);
//                }
//            }
//
//        }

//        this.fullAnalysis = true;
//        changedFiles.clear();

        //Build Tool
//        final String buildCmd = this.usingBuildTool.getBuildCmd(project);
//        if(buildCmd == null || buildCmd.isEmpty()) throw new ExecutionException("Violat Execution failed: Could not create a build tool command");
//        sb.append(buildCmd);
        String result = sb.toString() + " >&1 | tee result.txt";

        return result;
    }

    /**
     * Creates a Listener (if one doesn't already exists), which collects all changed files, which are of a compilable type
     * @see BuildTool#FILE_EXTENSIONS
     */

    public List<Checker> getSelectedCheckers() {
        return selectedCheckers;
    }
    public void setSelectedCheckers(List<Checker> selectedCheckers) {
        this.selectedCheckers = selectedCheckers;
    }
    public void setSelectedTester(Testers selectedTester) {
        GlobalSettings.getInstance().addTester(selectedTester);
        this.selectedTester = selectedTester;
    }

    public Testers getSelectedTester() {
        return this.selectedTester;
    }
    public Boolean isReactiveMode() {
        return reactiveMode;
    }
//    public void setReactiveMode(Boolean reactiveMode) {
//        this.reactiveMode = reactiveMode;
//    }
    public ViolatInstallation getSelectedInstallation() {
        return selectedInstallation;
    }
    public void setSelectedInstallation(ViolatInstallation selectedInstallation) {
        this.selectedInstallation = selectedInstallation;
    }


    public List<BuildTool> getAvailableBuildTools() {
        return availableBuildTools;
    }

    public List<Testers> getAvailableTesters() {
        return availableTesters;
    }
}
