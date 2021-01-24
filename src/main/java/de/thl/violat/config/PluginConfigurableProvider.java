package de.thl.violat.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import de.thl.violat.config.GlobalSettings;
import de.thl.violat.config.PluginConfigurable;
import org.jetbrains.annotations.Nullable;

public class PluginConfigurableProvider extends ConfigurableProvider {
    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new PluginConfigurable(GlobalSettings.getInstance());
    }
}
