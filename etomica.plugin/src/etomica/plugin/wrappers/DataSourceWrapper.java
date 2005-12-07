package etomica.plugin.wrappers;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.data.DataSource;
import etomica.data.meter.Meter;
import etomica.plugin.views.DataSourceView;
import etomica.plugin.views.DataSourceViewContentProvider;
import etomica.plugin.views.DataSourceViewLabelProvider;
import etomica.simulation.Simulation;

public class DataSourceWrapper extends PropertySourceWrapper {

    public DataSourceWrapper(DataSource object, Simulation sim) {
        super(object,sim);
    }

    public String toString() {
        if (displayName == null) {
            if (object instanceof Meter) {
                return ((Meter)object).getName();
            }
            return ((DataSource)object).getDataInfo().getLabel()+" source";
        }
        return displayName;
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        if (property.getName().startsWith("data") && !property.getName().equals("dataInfo")) {
            // exclude getData, getDataDouble, getDataAsScalar, maybe others
            return null;
        }
        return super.makeDescriptor(property);
    }
    
    public boolean canBeOpened() {
        return true;
    }
    
    public void open(IWorkbenchPage page) {
        try {
            DataSourceView view = (DataSourceView)page.showView("etomica.plugin.views.DataSourceView",toString(),IWorkbenchPage.VIEW_VISIBLE);
            DataSourceViewContentProvider vcp = new DataSourceViewContentProvider((DataSource)object);
            view.getViewer().setContentProvider(vcp);
            view.getViewer().setLabelProvider(new DataSourceViewLabelProvider(vcp));
            view.getViewer().setInput(vcp.getDataSinkTable());
        }
        catch (PartInitException e ) {
            ExceptionHandler.getInstance().handleException(e);
        }
    }
}
