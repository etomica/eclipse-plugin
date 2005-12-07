package etomica.plugin.wrappers;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;

import etomica.data.DataProcessor;
import etomica.data.DataSink;
import etomica.data.DataSource;
import etomica.plugin.views.DataSourceView;
import etomica.plugin.views.DataSourceViewContentProvider;
import etomica.plugin.views.DataSourceViewLabelProvider;
import etomica.simulation.Simulation;

public class DataProcessorWrapper extends PropertySourceWrapper {

    public DataProcessorWrapper(DataProcessor object, Simulation sim) {
        super(object,sim);
    }

    public boolean removeChild(Object child) {
        if (!(child instanceof DataSink)) {
            return false;
        }
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (((DataProcessor)object).getDataSink() == child) {
            ((DataProcessor)object).setDataSink(null);
        }
        return true;
    }
    
    public boolean canRemoveChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        return (((DataProcessor)object).getDataSink() == child);
    }
    
}
