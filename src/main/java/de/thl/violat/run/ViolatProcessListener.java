package de.thl.violat.run;


import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindowManager;

import de.thl.violat.config.GlobalSettings;
import de.thl.violat.service.ResultParser;


import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ViolatProcessListener implements ProcessListener {
    private static final Logger log = Logger.getInstance(ViolatProcessListener.class);
    private Project project;

    ViolatProcessListener(Project project) { this.project = project; }

    /**
     * Gets called when a Infer Process is terminated
     * @param event Contains information about the terminated process
     */
    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if(event.getExitCode() == 0) {
            log.info("Violat Process terminated successfully: Status Code 0");

            //Open the Infer Tool Window, which needs to be done in an AWT Event Dispatcher Thread
            if(!GlobalSettings.getInstance().isShowConsole()) { //if the user wants the console to stay in focus, we dont want to open the infer tool window


                ApplicationManager.getApplication().invokeAndWait(() -> ToolWindowManager.getInstance(project).getToolWindow("Violat").activate(null, true));
            }

            final Path reportPath = Paths.get(project.getBasePath() + "/result.txt");
            if(Files.exists(reportPath)) ResultParser.getInstance(project).parse(reportPath);
            else log.error("result.txt does not exist after Violat terminated successfully: Check the Violat log");

        } else {
            log.warn("Violat Process terminated unsuccessfully: Status Code " + event.getExitCode());
            Notifications.Bus.notify(new Notification("Violat", "Failure", "Violat terminated unsuccessfully", NotificationType.ERROR));
        }

        //remove the changedfiles.txt if it exists
//        try {
//            Files.deleteIfExists(Paths.get(project.getBasePath() + "/changedfiles.txt"));
//        } catch (IOException e) {
//            log.warn("Could not delete changedfiles.txt");
//        }
    }

    @Override public void startNotified(@NotNull ProcessEvent event) { }
    @Override public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) { }
    @Override public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) { }
}