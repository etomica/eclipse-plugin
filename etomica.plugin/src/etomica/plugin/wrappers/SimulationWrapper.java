package etomica.plugin.wrappers;

import java.util.LinkedList;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.data.DataPump;
import etomica.data.DataSource;
import etomica.integrator.Integrator;
import etomica.phase.Phase;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewDataStreamWizard;
import etomica.plugin.wizards.NewPotentialMasterWizard;
import etomica.plugin.wizards.NewSpeciesWizard;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.potential.PotentialMaster;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.species.Species;

public class SimulationWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public SimulationWrapper(Simulation sim, SimulationObjects simObjects) {
        super(sim,simObjects);
    }

    public PropertySourceWrapper[] getChildren(LinkedList parentList) {
        // we're not really supposed to override this, but the list we want
        // is so different from the list we get from reflection
        Simulation sim = (Simulation)object;
        // cheat a bit more and use the explicit Simulation field
        childWrappers = new PropertySourceWrapper[]{
                makeWrapper(sim.getController(),simObjects,etomicaEditor),
                makeWrapper(simObjects.potentialMasters.toArray(new PotentialMaster[0]),simObjects,etomicaEditor),
                makeWrapper(sim.getPhases(),simObjects,etomicaEditor),
                makeWrapper(sim.getSpeciesManager().getSpecies(),simObjects,etomicaEditor),
                makeWrapper(simObjects.dataStreams.toArray(new DataStreamHeader[0]),simObjects,etomicaEditor)};
        return childWrappers;
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Phase) {
            ((Simulation)object).removePhase((Phase)obj);
            return true;
        }
        if (obj instanceof Species) {
            return ((Simulation)object).getSpeciesManager().removeSpecies((Species)obj);
        }
        if (obj instanceof DataPump) {
            DataPump pump = (DataPump)obj;
            DataSource dataSource = pump.getDataSource();
            if (!removeDataStream(object, pump, dataSource)) {
                System.out.println("couldn't find "+pump+" in "+object);
            }
            simObjects.dataStreams.remove(obj);
            return true;
        }
        if (obj instanceof PotentialMaster) {
            simObjects.potentialMasters.remove(obj);
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
        else if (obj instanceof DataStreamHeader && simObjects.dataStreams.contains(obj)) {
            return true;
        }
        else if (obj instanceof PotentialMaster && simObjects.potentialMasters.contains(obj)) {
            return true;
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

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(PotentialMaster.class, this));
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Phase.class, this));
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Species.class, this));
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(DataStreamHeader.class, this));

        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{addItemWrapper}, super.getMenuItemWrappers(parentWrapper));
    }
    
    public boolean addObjectClass(Class newObjectClass, Shell shell) {
        if (newObjectClass == Phase.class) {
            simObjects.simulation.addPhase(new Phase((Simulation)object));
            return true;
        }
        if (newObjectClass == PotentialMaster.class) {
            NewPotentialMasterWizard wizard = new NewPotentialMasterWizard(simObjects);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        if (newObjectClass == Species.class) {
            NewSpeciesWizard wizard = new NewSpeciesWizard(simObjects);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        if (newObjectClass == DataStreamHeader.class) {
            NewDataStreamWizard wizard = new NewDataStreamWizard(simObjects);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
    
    protected boolean removeDataStream(Object obj, DataPump pump, DataSource dataSource) {
        if (obj instanceof Simulation) {
            return removeDataStream(((Simulation)obj).getController(), pump, dataSource);
        }
        if (obj instanceof ActivityGroup) {
            Action[] actions = ((ActivityGroup)obj).getAllActions();
            for (int i=0; i<actions.length; i++) {
                if (removeDataStream(actions[i], pump, dataSource)) {
                    return true;
                }
            }
        }
        else if (obj instanceof ActivityIntegrate) {
            return removeDataStream(((ActivityIntegrate)obj).getIntegrator(), pump, dataSource);
        }
        else if (obj instanceof Integrator) {
            Action[] listeners = ((Integrator)obj).getIntervalActions();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == dataSource) {
                    ((Integrator)obj).removeIntervalAction(listeners[i]);
                    return true;
                }
                else if (listeners[i] == pump) {
                    ((Integrator)obj).removeIntervalAction(pump);
                    return true;
                }
            }
        }
        return false;
    }

    protected PotentialMaster[] potentialMasters;
}
