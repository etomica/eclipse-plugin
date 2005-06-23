/*
 * History
 * Created on Oct 11, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * PropertyDescriptor for an enumerated type.  Method createPropertyEditor
 * gives an EnumeratedTypeCellEditor. 
 */
public class IntegerPropertyDescriptor extends PropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public IntegerPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	
	public CellEditor createPropertyEditor(Composite parent) {
		return new IntegerCellEditor(parent);
	}
	
}
