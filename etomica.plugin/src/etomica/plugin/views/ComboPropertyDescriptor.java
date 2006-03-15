/*
 * History
 * Created on Oct 11, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * PropertyDescriptor for an object with multiple choices in a combo box.
 * Method createPropertyEditor gives a ComboCellEditor. 
 */
public class ComboPropertyDescriptor extends PropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public ComboPropertyDescriptor(Object id, String displayName, Object[] choices) {
		super(id, displayName);
		this.choices = choices;
	}
	
	public CellEditor createPropertyEditor(Composite parent) {
        if (choices.length > 0) {
            return new ComboCellEditor(parent, choices);
        }
        return super.createPropertyEditor(parent);
	}
	
	protected Object[] choices;
}
