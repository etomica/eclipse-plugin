/*
 * History
 * Created on Oct 10, 2004 by kofke
 */
package etomica.ide.ui.propertiesview;

import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertySheet extends ViewPart {

	/**
	 * 
	 */
	public PropertySheet() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableTreeViewer(parent, SWT.NONE);
//		TableTree viewer = new TableTree(parent);
//		contentProvider = new PropertySheetContentProvider();
//		viewer.setContentProvider(contentProvider);

		tableTree = viewer.getTableTree();
		Table table = tableTree.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn properties = new TableColumn(table, SWT.LEFT);
		TableColumn values = new TableColumn(table, SWT.LEFT);
		TableColumn dimensions = new TableColumn(table, SWT.LEFT);
		
		properties.setText("Property");
		values.setText("Value");
		dimensions.setText("Dimension");
		TableTreeItem item = new TableTreeItem(tableTree, SWT.NONE);
		item.setText(0,"first");
		item.setText(1,"2.0");
		item.setText(2,"inches");
		TableTreeItem item2 = new TableTreeItem(tableTree, SWT.NONE);
		item2.setText(0,"second");
		item2.setText(1,"3.0");
		item2.setText(2,"cm");
		TableColumn[] columns = table.getColumns();
		for(int i=0; i<columns.length; i++) {
			columns[i].pack();
		}
//		viewer.setLabelProvider(new LabelProvider());
//		hookPageSelection();
//		createActions();
//		createToolBarButtons();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private TableTreeViewer viewer;
	private TableTree tableTree;
//	private PropertySheetContentProvider contentProvider;

}
