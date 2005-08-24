/*
 * History
 * Created on Oct 11, 2004 by kofke
 */
package etomica.ide.ui.propertiesview;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.ide.viewers.EnumeratedTypeCellEditor;
import etomica.util.Constants;
import etomica.util.EnumeratedType;

/**
 * PropertyDescriptor for an enumerated type.  Method createPropertyEditor
 * gives an EnumeratedTypeCellEditor. 
 */
public class EnumeratedTypePropertyDescriptor extends PropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public EnumeratedTypePropertyDescriptor(Object id, String displayName, EnumeratedType[] choices) {
		super(id, displayName);
		this.choices = choices;
	}

	
	public CellEditor createPropertyEditor(Composite parent) {
		return new EnumeratedTypeCellEditor(parent, choices);
	}
	
	private EnumeratedType[] choices;
}
