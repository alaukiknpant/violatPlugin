package de.thl.violat.config;

import com.intellij.openapi.components.BaseComponent;
import de.thl.violat.config.GlobalSettings;
import org.jetbrains.annotations.NotNull;

public class DefaultConfigComponent implements BaseComponent {
    @NotNull
    @Override
    public String getComponentName() {
        return "ConfigComponent";
    }

    @Override
    public void initComponent() {
        //adds the default installation (the one which is used if you just type 'infer' into the shell)
        GlobalSettings.getInstance().addInstallation("violat", true);
    }
}
