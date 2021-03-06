package de.thl.intellijinfer.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class InferRunState extends CommandLineState {
    private static final Logger log = Logger.getInstance(InferRunState.class);

    private ExecutionEnvironment ee;
    private InferRunConfiguration runCfg;

    InferRunState(InferRunConfiguration runCfg, ExecutionEnvironment environment) {
        super(environment);
        this.ee = environment;
        this.runCfg = runCfg;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {


        final String runCmd = runCfg.getInferLaunchCmd();

        log.info("Running Infer with Command: " + runCmd);

        GeneralCommandLine commandLine = new GeneralCommandLine("/bin/sh", "-c", runCmd);

        if(runCfg.getProject().getBasePath() == null) throw new ExecutionException("Could not acquire the project base path");
        commandLine.setWorkDirectory(new File(runCfg.getProject().getBasePath()));

        ProcessHandler ph = new ColoredProcessHandler(commandLine);
        ph.addProcessListener(new InferProcessListener(runCfg.getProject()));
        return ph;
    }
}
