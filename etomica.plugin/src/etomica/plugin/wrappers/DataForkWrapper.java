package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.data.DataPipeForked;
import etomica.data.DataSink;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewDataSinkWizard;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;

public class DataForkWrapper extends InterfaceWrapper implements RemoverWrapper, AdderWrapper {

    public DataForkWrapper(DataPipeForked object, SimulationObjects simObjects) {
        super(object,simObjects);
    }

    public boolean removeChild(Object child) {
        if (!(child instanceof DataSink)) {
            return false;
        }
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        ((DataPipeForked)object).removeDataSink((DataSink)child);
        return true;
    }
    
    public boolean canRemoveChild(Object child) {
        DataPipeForked fork = (DataPipeForked)object;
        DataSink[] dataSinks = fork.getDataSinks();
        for (int i=0; i<dataSinks.length; i++) {
            if (child == dataSinks[i]) {
                return true;
            }
        }
        return false;
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        AddItemWrapper addItemWrapper = new AddItemWrapper();

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(DataSink.class, this));
        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{addItemWrapper}, super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Class newObjectClass, Shell shell) {
        if (newObjectClass == DataSink.class) {
            NewDataSinkWizard wizard = new NewDataSinkWizard((DataPipeForked)object,simObjects);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
    
}
