package de.thl.violat.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import de.thl.violat.model.ViolatInstallation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//@State(name = "ViolatApplicationSettings", storages = {@Storage("$APP_CONFIG$/violat.xml")})
@State(name = "InferApplicationSettings", storages = {@Storage("$APP_CONFIG$/infer.xml")})
public class GlobalSettings implements PersistentStateComponent<GlobalSettings> {

    @Property
    private List<ViolatInstallation> installations = new ArrayList<>();
    @Property
    private boolean showConsole = false;


    /**
     * Adds a ViolatInstallation to the global list, which is used by run configurations.
     * @param path The path of the installation
     * @param isDefault if the installation is a default installation
     * @return true, if the installation was added successfully, otherwise false
     */
    public boolean addInstallation(String path, boolean isDefault) {
        //check if a default installation already exists
        if(isDefault && this.getInstallations().stream().anyMatch(ViolatInstallation::isDefaultInstall)) return false;

        ViolatInstallation ii = ViolatInstallation.createViolatInstallation(path, isDefault);
        if(ii != null) {
            installations.add(ii);
            return true;
        }
        return false;
    }

    /**
     * Removes the given installation from the global list.
     * @param ii the given installation
     */
    public void removeInstallation(ViolatInstallation ii) {
        installations.remove(ii);
    }

    /**
     * Gets the default Installation
     * @return Default Installation. Returns null when there is none.
     */
    @Nullable
    public ViolatInstallation getDefaultInstallation() {
        return this.getInstallations().stream().filter(ViolatInstallation::isDefaultInstall).findFirst().orElse(null);
    }

    /**
     * Returns if at least one valid Installation exists
     * @return If a Installation exists, which is confirmed working
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasValidInstallation() {
        return this.getInstallations().stream().anyMatch(ViolatInstallation::isConfirmedWorking);
    }

    /**
     * Gets a valid Violat Installation
     * @return A valid Violat Installation, or null if there is none
     */
    @Nullable
    public ViolatInstallation getAnyValidInstallation() {
        return this.getInstallations().stream().filter(ViolatInstallation::isConfirmedWorking).findAny().orElse(null);
    }

    /**
     * Returns the Installation at the given path
     * @param path The given path
     * @return A Installation if it exists at that path, otherwise null
     */
    @Nullable
    public ViolatInstallation getInstallationFromPath(String path) {
        return this.getInstallations().stream().filter((x) -> x.getPath().equals(path)).findFirst().orElse(null);
    }

    public static GlobalSettings getInstance() {
        return ApplicationManager.getApplication().getComponent(GlobalSettings.class);
    }

    @Nullable
    @Override
    public GlobalSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull GlobalSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @NotNull
    public List<ViolatInstallation> getInstallations() {
        return installations;
    }

    public void setInstallations(List<ViolatInstallation> installations) {
        this.installations = installations;
    }
    public boolean isShowConsole() {
        return showConsole;
    }
    public void setShowConsole(boolean showConsole) {
        this.showConsole = showConsole;
    }
}
