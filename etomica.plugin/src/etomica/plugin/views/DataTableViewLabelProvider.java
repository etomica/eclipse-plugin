package etomica.plugin.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import etomica.data.DataSinkTable;
import etomica.data.types.DataTable;

/**
 * Provides table elements for each row objects and column number for a 
 * DataSourceView's Table.  The row objects are expected to be Integers.
 * The data returned for 0th column are the row headers.
 */
public class DataTableViewLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    public DataTableViewLabelProvider(DataTableViewContentProvider dataSourceVLP) {
        super();
        vlp = dataSourceVLP;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof Integer)) {
            return null;
        }
        DataSinkTable dataSinkTable = vlp.getDataSinkTable();
        if (columnIndex == 0) {
            return dataSinkTable.getRowHeader(((Integer)element).intValue());
        }
        if (columnIndex > dataSinkTable.getColumnCount()) {
            return null;
        }
        DataTable.Column column = dataSinkTable.getColumn(columnIndex-1);
        int i = ((Integer)element).intValue();
        double[] x = column.getData();
        if (i > x.length-1) {
            return null;
        }
        return new Double(x[i]).toString();
    }
    
    private final DataTableViewContentProvider vlp;

}
