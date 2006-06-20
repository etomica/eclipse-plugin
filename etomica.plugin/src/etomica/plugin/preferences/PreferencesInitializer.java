package etomica.plugin.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferencesInitializer extends AbstractPreferenceInitializer {
    public static final String P_RASMOL_PATH = "rasmolPath";

    public PreferencesInitializer() {
        super();
    }

    public void initializeDefaultPreferences() {
        IEclipsePreferences node = new DefaultScope().getNode("etomica.plugin");
        String defaultValue = "";
        if (new File("/usr/X11R6/bin/rasmol").exists()) {
            defaultValue = "/usr/X11R6/bin/rasmol";
        }
        node.put(P_RASMOL_PATH, defaultValue);
    }

}
