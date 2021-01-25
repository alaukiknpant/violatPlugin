package de.thl.violat.actions;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;


import de.thl.violat.config.GlobalSettings;
import de.thl.violat.run.ViolatConfigurationFactory;
import de.thl.violat.run.ViolatConfigurationType;


import org.jetbrains.annotations.NotNull;

public class RunAction extends AnAction {
    private static final Logger log = Logger.getInstance(RunAction.class);
    private static final String GENERATED_CONFIG_NAME = "Generated Violat Config";
    public RunAction() {
        super();
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        if(!GlobalSettings.getInstance().hasValidInstallation()) {
            log.error("No valid Violat Installation: Go to the Violat Settings and add one");
            return;
        }

        if(event.getProject() == null) {
            log.error("Couldn't find open project");
            return;
        }

        final RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(event.getProject());

        RunnerAndConfigurationSettings rcs = null;

        //Find a already generated config
        for(RunnerAndConfigurationSettings rcSettings : runManager.getConfigurationSettingsList(ViolatConfigurationType.class)) {
            if(rcSettings.getConfiguration().getName().equals(GENERATED_CONFIG_NAME)) rcs = rcSettings;
        }
        //Generate a Config if we didn't find one
        if(rcs == null) {
            rcs = ViolatConfigurationFactory.createValidConfiguration(runManager, GENERATED_CONFIG_NAME);
            if(rcs == null) {
                log.error("Could not create Infer Run Configuration");
                return;
            }
            runManager.addConfiguration(rcs);
        }

        runManager.setSelectedConfiguration(rcs);
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = new RunnerAndConfigurationSettingsImpl(runManager, rcs.getConfiguration(), false);
        ProgramRunnerUtil.executeConfiguration(runnerAndConfigurationSettings, DefaultRunExecutor.getRunExecutorInstance());
    }

}