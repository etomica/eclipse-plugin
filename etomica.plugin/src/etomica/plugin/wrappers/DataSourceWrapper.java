package etomica.plugin.wrappers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ExceptionHandler;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.data.DataSource;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.views.DataPlotView;
import etomica.plugin.views.DataTableView;
import etomica.plugin.views.DataTableViewContentProvider;
import etomica.plugin.views.DataTableViewLabelProvider;
import etomica.plugin.wrappers.OpenItemWrapper.OpenViewItemWrapper;
import etomica.simulation.Simulation;

public class DataSourceWrapper extends InterfaceWrapper implements OpenerWrapper {

    public DataSourceWrapper(DataSource object, Simulation sim) {
        super(object,sim);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        if (property.getName().startsWith("data") && !property.getName().equals("dataInfo")) {
            // exclude getData, getDataDouble, getDataAsScalar, maybe others
            return PropertySourceWrapper.PROPERTY_VETO;
        }
        return null;
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemCascadeWrapper openItemWrapper = new OpenItemWrapper();

        openItemWrapper.addSubmenuItem(new OpenViewItemWrapper(DISPLAY_TABLE, this));
        openItemWrapper.addSubmenuItem(new OpenViewItemWrapper(DISPLAY_PLOT, this));
        return new MenuItemWrapper[]{openItemWrapper};
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
    
    public EtomicaStatus getStatus() {
        EtomicaStatus superStatus = super.getStatus();
        if (superStatus.type == EtomicaStatus.ERROR) {
            return superStatus;
        }
        try {
            Method phaseGetter = object.getClass().getMethod("getPhase", null);
            if (phaseGetter.invoke(object, null) == null) {
                return new EtomicaStatus("The DataSource requires a Phase",EtomicaStatus.ERROR);
            }
        }
        catch (NoSuchMethodException e) {
            // datasource had no getPhase method
        }
        catch (IllegalAccessException e) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
        }
        catch (InvocationTargetException e) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
        }
        return superStatus;
    }
    
    protected static final String DISPLAY_TABLE = "Table";
    protected static final String DISPLAY_PLOT = "Plot";
}
