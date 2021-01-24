package de.thl.violat.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import de.thl.violat.config.PluginConfigurable;

import org.jetbrains.annotations.NotNull;

public class SettingsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println(PluginConfigurable.class);
        System.out.println("It is working \n \n\n\n\n");

        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), de.thl.violat.config.PluginConfigurable.class);
//        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), de.thl.intellijinfer.config.PluginConfigurable.class);
    }
}
