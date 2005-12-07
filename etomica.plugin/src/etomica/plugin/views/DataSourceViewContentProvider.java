/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import etomica.data.DataPump;
import etomica.data.DataSinkTable;
import etomica.data.DataSource;

/**
 * Provides row objects for a DataSourceView's Table.  This class actually
 * sets up the DataPump and DataSinkTable used to pull the data from the
 * DataSource.
 */
public class DataSourceViewContentProvider implements IStructuredContentProvider {

    public DataSourceViewContentProvider(DataSource dataSource) {
        this.dataSource = dataSource;
        dataSinkTable = new DataSinkTable();
        pump = new DataPump(dataSource,dataSinkTable);
    }
    
    public DataSinkTable getDataSinkTable() {
        return dataSinkTable;
    }

    public Object[] getElements(Object inputElement) {
        if (!(inputElement instanceof DataSinkTable)) {
            return new Object[0];
        }
        pump.actionPerformed();
        int dataColumnCount = dataSinkTable.getColumnCount();
        Table table = viewer.getTable();
        int tableColumnCount = viewer.getTable().getColumnCount();
        if (tableColumnCount != dataColumnCount+1) {
            while (tableColumnCount > dataColumnCount+1) {
                table.getColumn(tableColumnCount-1).dispose();
                tableColumnCount--;
            }
            while (tableColumnCount < dataColumnCount+1) {
                TableColumn tableColumn = new TableColumn(table, SWT.NULL);
                tableColumn.setWidth(20);
                if (tableColumnCount > 0) {
                    tableColumn.setText(dataSinkTable.getColumn(tableColumnCount-1).getHeading());
                }
                tableColumnCount++;
            }
        }
        Object[] rows = new Object[((DataSinkTable)inputElement).getRowCount()];
        for (int i=0; i<rows.length; i++) {
            rows[i] = new Integer(i);
        }
        return rows;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
        //Simulation.instantiationEventManager.removeListener(this);
		viewer = null;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) {
        viewer = (TableViewer)newViewer;
        viewer.getTable().setHeaderVisible(true);
        currentSelection = newInput;
    }
    
    Object currentSelection;
    private final DataSource dataSource;
    private final DataSinkTable dataSinkTable;
    private final DataPump pump;
	private TableViewer viewer;
}
