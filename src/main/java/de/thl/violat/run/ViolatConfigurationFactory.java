package de.thl.violat.run;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.project.Project;

import de.thl.violat.config.GlobalSettings;
import de.thl.violat.model.buildtool.BuildToolFactory;



import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViolatConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Violat configuration factory";

    ViolatConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    @NotNull
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ViolatRunConfiguration(project, this, "Violat");
    }

    @Override
    @NotNull
    public String getName() {
        return FACTORY_NAME;
    }

    /**
     * Creates a working Infer Run Configuration
     * @param runManager The RunManager of the project where it should be generated
     * @param name The name of the run configuration
     * @return A valid Run Configuration, or null if it could not be created
     */
    @Nullable
    public static RunnerAndConfigurationSettings createValidConfiguration(RunManagerImpl runManager, String name) {
        if(!GlobalSettings.getInstance().hasValidInstallation()) return null;
        final ConfigurationFactory inferFactory = runManager.getFactory("ViolatRunConfiguration", "Violat configuration factory");
        if(inferFactory != null) {
            RunnerAndConfigurationSettings rcs = runManager.createConfiguration(name, inferFactory);
            ViolatRunConfiguration inferRC = (ViolatRunConfiguration) rcs.getConfiguration();
//            inferRC.getLaunchOptions().setUsingBuildTool(BuildToolFactory.getPreferredBuildTool(runManager.getProject()));
            inferRC.getLaunchOptions().setSelectedInstallation(GlobalSettings.getInstance().getAnyValidInstallation());
            return rcs;
        }
        return null;
    }

}
