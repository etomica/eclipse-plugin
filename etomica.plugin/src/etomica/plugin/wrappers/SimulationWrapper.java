package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.atom.Atom;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;
import etomica.phase.Phase;
import etomica.plugin.wizards.NewDataStreamWizard;
import etomica.plugin.wizards.NewSpeciesWizard;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.species.Species;

public class SimulationWrapper extends PropertySourceWrapper {

    public SimulationWrapper(Simulation sim) {
        super(sim,sim);
    }

    public PropertySourceWrapper[] getChildren() {
        Simulation sim = (Simulation)object;
        Phase[] phases = sim.getPhases();
        Species[] species = sim.getSpecies();
        DataStreamHeader[] streams = sim.getDataStreams();
        return PropertySourceWrapper.wrapArrayElements(new Object[]{sim.getController(),sim.potentialMaster,phases,species,streams,sim.getDefaults()},(Simulation)object);
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
            return ((Simulation)object).speciesRoot.removeSpecies((Species)obj);
        }
        if (obj instanceof DataStreamHeader) {
            Object client = ((DataStreamHeader)obj).getClient();
            if (!removeDataStream(object,client)) {
                System.out.println("couldn't find "+client+" in "+object);
            }
            ((Simulation)object).unregister(((DataStreamHeader)obj).getDataSource(),client);
            return true;
        }
        return false;
    }

    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        Object[] objs = new Object[0];
        if (obj instanceof Phase) {
            objs = ((Simulation)object).getPhases();
        }
        else if (obj instanceof Species) {
            objs = ((Simulation)object).speciesRoot.getSpecies();
        }
        else if (obj instanceof DataStreamHeader) {
            objs = ((Simulation)object).getDataStreams();
        }
        for (int i=0; i<objs.length; i++) {
            if (objs[i] == obj) {
                return true;
            }
        }
        return false;
    }

    public Class[] getAdders() {
        return new Class[]{Phase.class,Species.class,DataStreamHeader.class};
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == Phase.class) {
            new Phase((Simulation)object);
            return true;
        }
        if (newObjectClass == Species.class) {
            NewSpeciesWizard wizard = new NewSpeciesWizard((Simulation)object);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        if (newObjectClass == DataStreamHeader.class) {
            NewDataStreamWizard wizard = new NewDataStreamWizard((Simulation)object);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
    
    public boolean removeDataStream(Object obj, Object client) {
        if (obj instanceof Simulation) {
            return removeDataStream(((Simulation)obj).getController(),client);
        }
        if (obj instanceof ActivityGroup) {
            Action[] actions = ((ActivityGroup)obj).getAllActions();
            for (int i=0; i<actions.length; i++) {
                if (removeDataStream(actions[i],client)) {
                    return true;
                }
            }
        }
        else if (obj instanceof ActivityIntegrate) {
            return removeDataStream(((ActivityIntegrate)obj).getIntegrator(),client);
        }
        else if (obj instanceof Integrator) {
            IntegratorIntervalListener[] listeners = ((Integrator)obj).getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] instanceof IntervalActionAdapter) {
                    Action action = ((IntervalActionAdapter)listeners[i]).getAction();
                    if (action == client) {
                        ((Integrator)obj).removeListener(listeners[i]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
