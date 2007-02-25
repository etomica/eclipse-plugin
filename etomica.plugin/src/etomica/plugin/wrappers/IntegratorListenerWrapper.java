package etomica.plugin.wrappers;

import java.util.Iterator;

import etomica.action.Action;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorListener;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;
import etomica.simulation.Simulation;
import etomica.util.Arrays;

/**
 * Wrapper for IntegratorListeners.  The wrapper checks that the listener is is
 * listening to at least one interator and adds menu items to add itself to
 * each integrator it is not already listening to (allowing the user to make it
 * listen to more than one if they so desire).
 * 
 * @author Andrew Schultz
 */
public class IntegratorListenerWrapper extends InterfaceWrapper {

    public IntegratorListenerWrapper(IntegratorListener listener, Simulation sim) {
        super(listener, sim);
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemWrapper[] itemWrappers = new MenuItemWrapper[0];
        Integrator[] integrators = notListeningIntegrators();
        if (integrators.length > 0) {
            ActionListItemWrapper actionListItemWrapper = new ActionListItemWrapper();
            for (int i=0; i<integrators.length; i++) {
                AddToIntegratorAction action = new AddToIntegratorAction(integrators[i], (IntegratorListener)object);
                ActionItemWrapper itemWrapper = new ActionItemWrapper(action);
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
            new EtomicaStatus(object+" is an Integrator listener, but is not listening to any Integrator", EtomicaStatus.WARNING);
    }

    /**
     * Returns true if this listener is listening to any Integrator
     */
    protected boolean isListening() {
        boolean foundListener = false;
        Iterator integratorIterator = simulation.getIntegratorList().iterator();
        while (!foundListener && integratorIterator.hasNext()) {
            Integrator integrator = (Integrator)integratorIterator.next();
            Object[] listeners = integrator.getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == object) {
                    return true;
                }
            }

            listeners = integrator.getNonintervalListeners();
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
        Iterator integratorIterator = simulation.getIntegratorList().iterator();
        while (integratorIterator.hasNext()) {
            boolean isListening = false;
            Integrator integrator = (Integrator)integratorIterator.next();
            Object[] listeners = integrator.getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == object) {
                    isListening = true;
                }
            }

            listeners = integrator.getNonintervalListeners();
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
        public AddToIntegratorAction(Integrator integrator, IntegratorListener listener) {
            this.integrator = integrator;
            this.listener = listener;
        }
        
        public String getLabel() {
            return "Add to "+integrator.getName();
        }
        
        public void actionPerformed() {
            integrator.addListener(listener);
        }
        
        protected final Integrator integrator;
        protected final IntegratorListener listener;
    }
}
