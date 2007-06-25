package etomica.plugin.wrappers;

import java.util.ArrayList;

import etomica.action.Action;
import etomica.integrator.Integrator;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;
import etomica.util.Arrays;

/**
 * Wrapper for Actions.  The wrapper checks if the action is an Integrator's
 * interval action and (if it's not) adds a menu item to add the Action to each
 * integrator it is not already listening to (allowing the user to make it
 * listen to more than one if they so desire).
 * 
 * @author Andrew Schultz
 */
public class ActionWrapper extends InterfaceWrapper {

    public ActionWrapper(Action action, SimulationObjects simObjects) {
        super(action, simObjects);
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemWrapper[] itemWrappers = new MenuItemWrapper[0];
        Integrator[] integrators = notListeningIntegrators();
        if (integrators.length > 0) {
            ActionListItemWrapper actionListItemWrapper = new ActionListItemWrapper();
            for (int i=0; i<integrators.length; i++) {
                AddToIntegratorAction action = new AddToIntegratorAction(integrators[i], (Action)object);
                ActionItemWrapper itemWrapper = new ActionItemWrapper(action, "Add to "+integrators[i]);
                actionListItemWrapper.addSubmenuItem(itemWrapper);
            }
            itemWrappers = new MenuItemWrapper[]{actionListItemWrapper};
        }

        return PropertySourceWrapper.combineMenuItemWrappers(itemWrappers, 
                super.getMenuItemWrappers(parentWrapper));
    }

    /**
     * Returns true if this listener is listening to any Integrator
     */
    protected boolean isListening() {
        boolean foundListener = false;
        ArrayList integratorList = simObjects.integrators;
        for (int j=0; !foundListener && j<integratorList.size(); j++) {
            Integrator integrator = (Integrator)integratorList.get(j);
            Action[] listeners = integrator.getIntervalActions();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == object) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns an array containing all Integrators in the Simulation that this
     * listener is not listening to.
     */
    protected Integrator[] notListeningIntegrators() {
        Integrator[] integrators = new Integrator[0];
        ArrayList integratorList = simObjects.integrators;
        for (int j=0; j<integratorList.size(); j++) {
            Integrator integrator = (Integrator)integratorList.get(j);
            boolean isListening = false;
            Action[] listeners = integrator.getIntervalActions();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == object) {
                    isListening = true;
                }
            }
            
            if (!isListening) {
                integrators = (Integrator[])Arrays.addObject(integrators, integrator);
            }
        }
        return integrators;
    }

    /**
     * Action that adds an IntegratorListener to an Integrator
     */
    public static class AddToIntegratorAction implements Action {
        public AddToIntegratorAction(Integrator integrator, Action action) {
            this.integrator = integrator;
            this.action = action;
        }
        
        public void actionPerformed() {
            integrator.addIntervalAction(action);
        }
        
        protected final Integrator integrator;
        protected final Action action;
    }
}
