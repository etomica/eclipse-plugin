package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.action.activity.ActivityGroup;
import etomica.simulation.Simulation;

/**
 * Wrapper for ActivityGroups.  The sole purpose is to exclude the specific
 * lists of actions from being listed as children.
 * 
 * @author Andrew Schultz
 */
public class ActivityGroupWrapper extends InterfaceWrapper {
    
    public ActivityGroupWrapper(ActivityGroup object, Simulation simulation) {
        super(object, simulation);
    }
    
    public boolean isChildExcluded(IPropertyDescriptor descriptor, PropertySourceWrapper childWrapper, Object child) {
        if (descriptor.getDisplayName().equals("completedActions") || 
            descriptor.getDisplayName().equals("pendingActions") ||
            descriptor.getDisplayName().equals("currrentActions")) {
            return true;
        }
        return false;
    }
}
