package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

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
import etomica.simulation.ISimulation;
import etomica.simulation.SpeciesManager;
import etomica.species.Species;

public class SimulationWrapper extends InterfaceWrapper implements RemoverWrapper, AdderWrapper {

    public SimulationWrapper(ISimulation sim, SimulationObjects simObjects) {
        super(sim,simObjects);
    }

    public PropertySourceWrapper[] getChildren() {
        PropertySourceWrapper[] childWrappers = new PropertySourceWrapper[]{
//                PropertySourceWrapper.makeWrapper(sim.getController(),simObjects,editor),
                PropertySourceWrapper.makeWrapper(simObjects.potentialMasters.toArray(new PotentialMaster[0]),simObjects,editor),
//                PropertySourceWrapper.makeWrapper(sim.getPhases(),simObjects,editor),
                PropertySourceWrapper.makeWrapper(((ISimulation)object).getSpeciesManager().getSpecies(),simObjects,editor),
                PropertySourceWrapper.makeWrapper(simObjects.dataStreams.toArray(new DataStreamHeader[0]),simObjects,editor)};
        return childWrappers;
    }

    
    public boolean isChildExcluded(IPropertyDescriptor descriptor, PropertySourceWrapper childWrapper, Object child) {
        if (child instanceof SpeciesManager) {
            return true;
        }
        return super.isChildExcluded(descriptor, childWrapper, child);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Phase) {
            ((ISimulation)object).removePhase((Phase)obj);
            return true;
        }
        if (obj instanceof Species) {
            return ((ISimulation)object).getSpeciesManager().removeSpecies((Species)obj);
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
            objs = ((ISimulation)object).getPhases();
        }
        else if (obj instanceof Species) {
            objs = ((ISimulation)object).getSpeciesManager().getSpecies();
        }
        else if (simObjects.dataStreams.contains(obj)) {
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
            simObjects.simulation.addPhase(new Phase((ISimulation)object));
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
        if (obj instanceof ISimulation) {
            return removeDataStream(((ISimulation)obj).getController(), pump, dataSource);
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
