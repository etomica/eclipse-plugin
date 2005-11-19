/*
 * History
 * Created on Oct 11, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 */
public class ComboClassPropertyDescriptor extends ComboPropertyDescriptor {

	/**
	 * @param id
	 * @param displayName
	 */
	public ComboClassPropertyDescriptor(Object id, String displayName, Object[] choices,
            Object[] constructorParameters) {
		super(id,displayName,choices);
        parameters = constructorParameters;
	}

	
	public CellEditor createPropertyEditor(Composite parent) {
        ComboClassCellEditor editor = new ComboClassCellEditor(parent, choices);
        editor.setConstructorParameters(parameters);
		return editor;
	}
    
    protected final Object[] parameters;
}
