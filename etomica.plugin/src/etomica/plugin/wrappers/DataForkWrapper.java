package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.data.DataPipeForked;
import etomica.data.DataSink;
import etomica.data.DataSource;
import etomica.plugin.wizards.NewDataSinkWizard;
import etomica.simulation.Simulation;

public class DataForkWrapper extends PropertySourceWrapper {

    public DataForkWrapper(DataPipeForked object, Simulation sim) {
        super(object,sim);
        if (object instanceof DataSource) {
            dataSourceWrapper = new DataSourceWrapper((DataSource)object,sim);
        }
        else {
            dataSourceWrapper = null;
        }
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        if (property.getName().equals("data") || property.getName().equals("dataAsScalar")) {
            // exclude getData, getDataAsScalar for forks that are also DataSources
            return null;
        }
        return super.makeDescriptor(property);
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
    
    public Class[] getAdders() {
        return new Class[]{DataSink.class};
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == DataSink.class) {
            NewDataSinkWizard wizard = new NewDataSinkWizard((DataPipeForked)object,simulation);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
    
    public boolean canBeOpened() {
        return dataSourceWrapper != null && dataSourceWrapper.canBeOpened();
    }
    
    public void open(IWorkbenchPage page) {
        if (dataSourceWrapper == null) {
            return;
        }
        dataSourceWrapper.open(page);
    }

    private final DataSourceWrapper dataSourceWrapper;
}
