/*
 * History
 * Created on Oct 11, 2004 by kofke
 */
package etomica.ide.ui.propertiesview;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * PropertyDescriptor for a boolean value.  Method createPropertyEditor
 * gives a CheckboxCellEditor. 
 */
public class CheckboxPropertyDescriptor extends PropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public CheckboxPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	
	public CellEditor createPropertyEditor(Composite parent) {
		return new CheckboxCellEditor(parent);
	}
}
