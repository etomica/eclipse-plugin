package etomica.plugin.wrappers;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.atom.Atom;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;
import etomica.phase.Phase;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.species.Species;

public class SimulationWrapper extends PropertySourceWrapper {

    public SimulationWrapper(Simulation sim) {
        super(sim);
    }

    public PropertySourceWrapper[] getChildren() {
        Simulation sim = (Simulation)object;
        Phase[] phases = sim.getPhases();
        Species[] species = sim.getSpecies();
        DataStreamHeader[] streams = sim.getDataStreams();
        return PropertySourceWrapper.wrapArrayElements(new Object[]{sim.getController(),sim.potentialMaster,phases,species,streams,sim.getDefaults()});
//        PropertySourceWrapper[] elements = new PropertySourceWrapper[2+phases.length+species.length+streams.length];
//        int i=0;
//        elements[i++] = PropertySourceWrapper.makeWrapper(sim.getController());
//        elements[i++] = PropertySourceWrapper.makeWrapper(sim.potentialMaster);
//        System.arraycopy(PropertySourceWrapper.wrapArrayElements(phases),0,elements,i,phases.length);
//        i+=phases.length;
//        System.arraycopy(PropertySourceWrapper.wrapArrayElements(species),0,elements,i,species.length);
//        i+=species.length;
//        System.arraycopy(PropertySourceWrapper.wrapArrayElements(streams),0,elements,i,streams.length);
//        return elements;
    }
    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Phase) {
            ((Phase)obj).getSpeciesMaster().node.setParent((Atom)null);
            return true;
        }
        if (obj instanceof Species) {
            ((Simulation)object).speciesRoot.removeSpecies((Species)obj);
            return true;
        }
        if (obj instanceof DataStreamHeader) {
            for (int i=((DataStreamHeader)obj).getClients().length-1; i>0; i--) {
                Object client = ((DataStreamHeader)obj).getClients()[i];
                if (!removeDataStream(object,client)) {
                    System.out.println("couldn't find "+client+" in "+object);
                }
                ((Simulation)object).unregister(((DataStreamHeader)obj).getDataSource(),client);
            }
            return true;
        }
        return false;
    }

    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Phase || obj instanceof Species || obj instanceof DataStreamHeader) {
            return true;
        }
        return false;
    }

    public boolean removeDataStream(Object obj, Object client) {
        if (obj instanceof Simulation) {
            return removeDataStream(((Simulation)obj).getController(),client);
        }
        if (obj instanceof ActivityGroup) {
            Action[] actions = ((ActivityGroup)obj).getAllActions();
            boolean found = false;
            for (int i=0; i<actions.length; i++) {
                if (removeDataStream(actions[i],client)) {
                    found = true;
                }
            }
            return found;
        }
        if (obj instanceof ActivityIntegrate) {
            return removeDataStream(((ActivityIntegrate)obj).getIntegrator(),client);
        }
        if (obj instanceof Integrator) {
            IntegratorIntervalListener[] listeners = ((Integrator)obj).getIntervalListeners();
            boolean found = false;
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] instanceof IntervalActionAdapter) {
                    Action action = ((IntervalActionAdapter)obj).getAction();
                    if (action == client) {
                        ((Integrator)obj).removeListener(listeners[i]);
                        found = true;
                    }
                }
            }
            return found;
        }
        return false;
    }

}
