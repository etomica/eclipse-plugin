package etomica.plugin.wrappers;

import java.util.LinkedList;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.data.DataSource;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;
import etomica.phase.Phase;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wizards.NewDataStreamWizard;
import etomica.plugin.wizards.NewSpeciesWizard;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.species.Species;

public class SimulationWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public SimulationWrapper(Simulation sim) {
        super(sim,sim);
    }

    public PropertySourceWrapper[] getChildren(LinkedList parentList) {
        // we're not really supposed to override this, but the list we want
        // is so different from the list we get from reflection
        Simulation sim = (Simulation)object;
        childWrappers = new PropertySourceWrapper[]{
                makeWrapper(sim.getController(),sim,etomicaEditor),
                makeWrapper(sim.getPotentialMaster(),sim,etomicaEditor),
                makeWrapper(sim.getPhases(),sim,etomicaEditor),
                makeWrapper(sim.getSpeciesManager().getSpecies(),sim,etomicaEditor),
                makeWrapper(sim.getDataStreams(),sim,etomicaEditor),
                makeWrapper(sim.getDefaults(),sim,etomicaEditor)};
        return childWrappers;
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Phase) {
            ((Phase)obj).getSpeciesMaster().dispose();
            return true;
        }
        if (obj instanceof Species) {
            return ((Simulation)object).getSpeciesManager().removeSpecies((Species)obj);
        }
        if (obj instanceof DataStreamHeader) {
            Object client = ((DataStreamHeader)obj).getClient();
            DataSource dataSource = ((DataStreamHeader)obj).getDataSource();
            if (!removeDataStream(object, client, dataSource)) {
                System.out.println("couldn't find "+client+" in "+object);
            }
            ((Simulation)object).unregister(dataSource,client);
            return true;
        }
        return false;
    }

    public boolean canRemoveChild(Object obj) {
        Object[] objs = new Object[0];
        if (obj instanceof Phase) {
            objs = ((Simulation)object).getPhases();
        }
        else if (obj instanceof Species) {
            objs = ((Simulation)object).getSpeciesManager().getSpecies();
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

    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        AddItemWrapper addItemWrapper = new AddItemWrapper();

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Phase.class, this));
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Species.class, this));
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(DataStreamHeader.class, this));

        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{addItemWrapper}, super.getMenuItemWrappers(parentWrapper));
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
    
    protected boolean removeDataStream(Object obj, Object client, DataSource dataSource) {
        if (obj instanceof Simulation) {
            return removeDataStream(((Simulation)obj).getController(), client, dataSource);
        }
        if (obj instanceof ActivityGroup) {
            Action[] actions = ((ActivityGroup)obj).getAllActions();
            for (int i=0; i<actions.length; i++) {
                if (removeDataStream(actions[i], client, dataSource)) {
                    return true;
                }
            }
        }
        else if (obj instanceof ActivityIntegrate) {
            return removeDataStream(((ActivityIntegrate)obj).getIntegrator(), client, dataSource);
        }
        else if (obj instanceof Integrator) {
            IntegratorIntervalListener[] listeners = ((Integrator)obj).getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == dataSource) {
                    ((Integrator)obj).removeListener(listeners[i]);
                    return true;
                }
                else if (listeners[i] instanceof IntervalActionAdapter) {
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
