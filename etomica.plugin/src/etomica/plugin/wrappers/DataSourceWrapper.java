package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.data.DataSource;
import etomica.data.meter.Meter;
import etomica.plugin.views.DataPlotView;
import etomica.plugin.views.DataTableView;
import etomica.plugin.views.DataTableViewContentProvider;
import etomica.plugin.views.DataTableViewLabelProvider;
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
    
    public String[] getOpenViews() {
        return new String[]{DISPLAY_TABLE, DISPLAY_PLOT};
    }
    
    public boolean open(String openView, IWorkbenchPage page, Shell shell) {
        try {
            if (openView == DISPLAY_TABLE) {
                DataTableView view = (DataTableView)page.showView("etomica.plugin.views.DataTableView",toString(),IWorkbenchPage.VIEW_VISIBLE);
                DataTableViewContentProvider vcp = new DataTableViewContentProvider((DataSource)object);
                view.getViewer().setContentProvider(vcp);
                view.getViewer().setLabelProvider(new DataTableViewLabelProvider(vcp));
                view.getViewer().setInput(vcp.getDataSinkTable());
                return true;
            }
            else if (openView == DISPLAY_PLOT) {
                DataPlotView view = (DataPlotView)page.showView("etomica.plugin.views.DataPlotView",toString(),IWorkbenchPage.VIEW_VISIBLE);
                view.setDataSource((DataSource)object);
                return true;
            }
        }
        catch (PartInitException e ) {
            ExceptionHandler.getInstance().handleException(e);
        }
        return false;
    }
    
    protected static final String DISPLAY_TABLE = "Table";
    protected static final String DISPLAY_PLOT = "Plot";
}
