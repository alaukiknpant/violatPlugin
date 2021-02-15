package de.thl.violat.run;


import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;


import de.thl.violat.config.GlobalSettings;
import de.thl.violat.model.Checker;
import de.thl.violat.model.ViolatInstallation;
import de.thl.violat.model.ViolatLaunchOptions;
import de.thl.violat.model.buildtool.BuildToolFactory;
import de.thl.violat.model.buildtoolchecker.BuildToolChecker;
import de.thl.violat.ui.RunConfigurationEditor;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViolatRunConfiguration extends RunConfigurationBase {
    private static final String PREFIX = "INTELLIJ_VIOLAT-";
    private static final String INSTALLATION = PREFIX + "INSTALLATION";
    private static final String BUILD_TOOL = PREFIX + "BUILD_TOOL";
    private static final String ADDITIONAL_ARGUMENTS = PREFIX + "ADDITIONAL_ARGUMENTS";
    private static final String ADDITIONAL_ARGUMENTS_PATH = PREFIX + "ADDITIONAL_ARGUMENTS_PATH";
    private static final String CHECKERS = PREFIX + "CHECKERS";
//    private static final String REACTIVE_MODE = PREFIX + "REACTIVE_MODE";

    private ViolatLaunchOptions launchOptions;
    private Project project;


    ViolatRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.project = project;
        this.launchOptions = new ViolatLaunchOptions(project);


    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RunConfigurationEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

//        if(launchOptions.getUsingBuildTool() == null) throw new RuntimeConfigurationException("No Build Tool Selected"); if(launchOptions.getUsingBuildTool() == null) throw new RuntimeConfigurationException("No Build Tool Selected");
        if (!BuildToolChecker.returnInvalidInstallations()) throw new RuntimeConfigurationException("Java, maven or gradle might be missing");
        if(launchOptions.getSelectedCheckers() == null || launchOptions.getSelectedCheckers().isEmpty()) throw new RuntimeConfigurationException("No Checker selected");
        if(launchOptions.getSelectedInstallation() == null || !launchOptions.getSelectedInstallation().isConfirmedWorking()) throw new RuntimeConfigurationException("No selected Installation or the Installation is invalid");
        if (launchOptions.getSelectedCheckers().size() > 1) throw new RuntimeConfigurationException("We can have a maximum of 1 Checker");
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new ViolatRunState(this, executionEnvironment);
    }
    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
//        final String buildToolName = JDOMExternalizerUtil.readField(element, BUILD_TOOL);
//
//        if(buildToolName != null) {
//            this.launchOptions.setUsingBuildTool(BuildToolFactory.getInstanceFromName(buildToolName));
//        }

        final ViolatInstallation installation = GlobalSettings.getInstance().getInstallationFromPath(JDOMExternalizerUtil.readField(element, INSTALLATION));
        if(installation != null) this.launchOptions.setSelectedInstallation(installation);

//        this.launchOptions.setAdditionalArgs(JDOMExternalizerUtil.readField(element, ADDITIONAL_ARGUMENTS));
//        this.launchOptions.setReactiveMode(Boolean.valueOf(JDOMExternalizerUtil.readField(element, REACTIVE_MODE)));

        final String checkerString = JDOMExternalizerUtil.readField(element, CHECKERS);
        if(checkerString != null) {
            List<Checker> newCheckerList = new ArrayList<>();
            Pattern p = Pattern.compile( "\\w+" );
            Matcher m = p.matcher(checkerString);
            while(m.find()) {
                newCheckerList.add(Checker.valueOf(m.group()));
            }
            this.launchOptions.setSelectedCheckers(newCheckerList);
        }
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
//        if(this.launchOptions.getUsingBuildTool() != null) JDOMExternalizerUtil.writeField(element, BUILD_TOOL, this.launchOptions.getUsingBuildTool().getName());
        if(this.launchOptions.getSelectedInstallation() != null) JDOMExternalizerUtil.writeField(element, INSTALLATION, this.launchOptions.getSelectedInstallation().getPath());
//        JDOMExternalizerUtil.writeField(element, ADDITIONAL_ARGUMENTS, this.launchOptions.getAdditionalArgs());
//        JDOMExternalizerUtil.writeField(element, REACTIVE_MODE, this.launchOptions.isReactiveMode().toString());

        StringBuilder sb = new StringBuilder();
        for(Checker checker : this.launchOptions.getSelectedCheckers()) {
            sb.append(checker.getName()).append(" ");
        }
        JDOMExternalizerUtil.writeField(element, CHECKERS, sb.toString());
    }

    @NotNull
    String getViolatLaunchCmd() throws ExecutionException {
        return this.launchOptions.buildViolatLaunchCmd(this.project);
    }

    public ViolatLaunchOptions getLaunchOptions() {
        return this.launchOptions;
    }

}
