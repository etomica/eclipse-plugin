/*
 * History
 * Created on Oct 11, 2004 by kofke
 */
package etomica.ide.ui.propertiesview;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.Constants;
import etomica.ide.viewers.EnumeratedTypeCellEditor;

/**
 * PropertyDescriptor for an enumerated type.  Method createPropertyEditor
 * gives an EnumeratedTypeCellEditor. 
 */
public class EnumeratedTypePropertyDescriptor extends PropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public EnumeratedTypePropertyDescriptor(Object id, String displayName, Constants.TypedConstant[] choices) {
		super(id, displayName);
		this.choices = choices;
	}

	
	public CellEditor createPropertyEditor(Composite parent) {
		return new EnumeratedTypeCellEditor(parent, choices);
	}
	
	private Constants.TypedConstant[] choices;
}
