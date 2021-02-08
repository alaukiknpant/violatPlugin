package de.thl.violat.model;

import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class SpecificationInitializer {
    private static final Logger log = Logger.getInstance(de.thl.violat.model.SpecificationInitializer.class);
    private String path;

    public SpecificationInitializer() {}

    private SpecificationInitializer(String path) {
        setPath(path);
    }

    @Nullable
    public static SpecificationInitializer createSpecificationInitializer(String path) {
        SpecificationInitializer ii = new SpecificationInitializer(path);
        if(ii.confirm(path)) return ii;
        return null;
    }

    /**
     * Checks if the Infer Installation at the given path is valid.
     * @param path Full path to the infer binary
     * @return The Version if the installation is valid, otherwise null
     */
    @Nullable
    public Boolean confirm(@NotNull String path) {
        if(path.endsWith(".json")) {
            return true;
        } else {
            return false;
        }
    }

    public void setPath(String path) {
        if(path.endsWith(".json")) this.path = path;
    }

}
