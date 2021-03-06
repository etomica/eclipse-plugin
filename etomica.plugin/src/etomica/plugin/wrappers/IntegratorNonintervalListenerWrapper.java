package etomica.plugin.wrappers;

import java.util.ArrayList;

import etomica.action.Action;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorNonintervalListener;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;
import etomica.util.Arrays;

/**
 * Wrapper for IntegratorListeners.  The wrapper checks that the listener is is
 * listening to at least one interator and adds menu items to add itself to
 * each integrator it is not already listening to (allowing the user to make it
 * listen to more than one if they so desire).
 * 
 * @author Andrew Schultz
 */
public class IntegratorNonintervalListenerWrapper extends InterfaceWrapper {

    public IntegratorNonintervalListenerWrapper(IntegratorNonintervalListener listener, SimulationObjects simObjects) {
        super(listener, simObjects);
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemWrapper[] itemWrappers = new MenuItemWrapper[0];
        Integrator[] integrators = notListeningIntegrators();
        if (integrators.length > 0) {
            ActionListItemWrapper actionListItemWrapper = new ActionListItemWrapper();
            for (int i=0; i<integrators.length; i++) {
                AddToIntegratorAction action = new AddToIntegratorAction(integrators[i], (IntegratorNonintervalListener)object);
                ActionItemWrapper itemWrapper = new ActionItemWrapper(action, "Add to "+integrators[i]+" as non-interval listener");
                actionListItemWrapper.addSubmenuItem(itemWrapper);
            }
            itemWrappers = new MenuItemWrapper[]{actionListItemWrapper};
        }

        return PropertySourceWrapper.combineMenuItemWrappers(itemWrappers, 
                super.getMenuItemWrappers(parentWrapper));
    }

    /**
     * Returns the status of the underlying object (PEACHY, WARNING or ERROR).
     * An object might not be happy becuase of its own internal state or
     * because of its relationship with another object in the simulation.
     */
    public EtomicaStatus getStatus() {
        return isListening() ? EtomicaStatus.PEACHY : 
            new EtomicaStatus(object+" is an Integrator non-interval listener, but is not listening to non-interval events from an Integrator", EtomicaStatus.WARNING);
    }

    /**
     * Returns true if this listener is listening to any Integrator
     */
    protected boolean isListening() {
        boolean foundListener = false;
        ArrayList integratorList = simObjects.integrators;
        for (int j=0; !foundListener && j<integratorList.size(); j++) {
            Integrator integrator = (Integrator)integratorList.get(j);
            IntegratorNonintervalListener[] listeners = integrator.getNonintervalListeners();
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
            IntegratorNonintervalListener[] listeners = integrator.getNonintervalListeners();
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
        public AddToIntegratorAction(Integrator integrator, IntegratorNonintervalListener listener) {
            this.integrator = integrator;
            this.listener = listener;
        }
        
        public void actionPerformed() {
            integrator.addNonintervalListener(listener);
        }
        
        protected final Integrator integrator;
        protected final IntegratorNonintervalListener listener;
    }
}
