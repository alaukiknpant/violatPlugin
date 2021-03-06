package de.thl.intellijinfer.run;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.PlatformUtils;
import de.thl.intellijinfer.config.GlobalSettings;
import de.thl.intellijinfer.model.Checker;
import de.thl.intellijinfer.model.InferInstallation;
import de.thl.intellijinfer.model.InferLaunchOptions;
import de.thl.intellijinfer.model.buildtool.BuildToolFactory;
import de.thl.intellijinfer.ui.RunConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InferRunConfiguration extends RunConfigurationBase {
    private static final String PREFIX = "INTELLIJ_INFER-";
    private static final String INSTALLATION = PREFIX + "INSTALLATION";
    private static final String BUILD_TOOL = PREFIX + "BUILD_TOOL";
    private static final String ADDITIONAL_ARGUMENTS = PREFIX + "ADDITIONAL_ARGUMENTS";
    private static final String CHECKERS = PREFIX + "CHECKERS";
    private static final String REACTIVE_MODE = PREFIX + "REACTIVE_MODE";

    private InferLaunchOptions launchOptions;
    private Project project;


    InferRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.project = project;
        this.launchOptions = new InferLaunchOptions(project);


    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RunConfigurationEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if(launchOptions.getUsingBuildTool() == null) throw new RuntimeConfigurationException("No Build Tool Selected");
        if(launchOptions.getSelectedCheckers() == null || launchOptions.getSelectedCheckers().isEmpty()) throw new RuntimeConfigurationException("No Checker selected");
        if(launchOptions.getSelectedInstallation() == null || !launchOptions.getSelectedInstallation().isConfirmedWorking()) throw new RuntimeConfigurationException("No selected Installation or the Installation is invalid");
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new InferRunState(this, executionEnvironment);
    }
    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        final String buildToolName = JDOMExternalizerUtil.readField(element, BUILD_TOOL);

        if(buildToolName != null) {
            this.launchOptions.setUsingBuildTool(BuildToolFactory.getInstanceFromName(buildToolName));
        }

        final InferInstallation installation = GlobalSettings.getInstance().getInstallationFromPath(JDOMExternalizerUtil.readField(element, INSTALLATION));
        if(installation != null) this.launchOptions.setSelectedInstallation(installation);
        
        this.launchOptions.setAdditionalArgs(JDOMExternalizerUtil.readField(element, ADDITIONAL_ARGUMENTS));
        this.launchOptions.setReactiveMode(Boolean.valueOf(JDOMExternalizerUtil.readField(element, REACTIVE_MODE)));

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
        if(this.launchOptions.getUsingBuildTool() != null) JDOMExternalizerUtil.writeField(element, BUILD_TOOL, this.launchOptions.getUsingBuildTool().getName());
        if(this.launchOptions.getSelectedInstallation() != null) JDOMExternalizerUtil.writeField(element, INSTALLATION, this.launchOptions.getSelectedInstallation().getPath());
        JDOMExternalizerUtil.writeField(element, ADDITIONAL_ARGUMENTS, this.launchOptions.getAdditionalArgs());
        JDOMExternalizerUtil.writeField(element, REACTIVE_MODE, this.launchOptions.isReactiveMode().toString());

        StringBuilder sb = new StringBuilder();
        for(Checker checker : this.launchOptions.getSelectedCheckers()) {
            sb.append(checker.getName()).append(" ");
        }
        JDOMExternalizerUtil.writeField(element, CHECKERS, sb.toString());
    }

    @NotNull
    String getInferLaunchCmd() throws ExecutionException {
        return this.launchOptions.buildInferLaunchCmd(this.project);
    }

    public InferLaunchOptions getLaunchOptions() {
        return this.launchOptions;
    }

}
