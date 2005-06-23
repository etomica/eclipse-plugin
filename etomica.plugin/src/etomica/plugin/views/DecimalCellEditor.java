/*
 * History
 * Created on Oct 12, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Cell editor that allows input of double values.  Derived
 * from a TextCellEditor, and overrides methods involved
 * with getting and setting value to do parsing to/from
 * a Double instance.
 */
public class DecimalCellEditor extends TextCellEditor {

	public DecimalCellEditor() {
		super();
	}

	public DecimalCellEditor(Composite parent) {
		super(parent);
	}

	public DecimalCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Overrides superclass method to add VerifyListener that
	 * ensures only +/- integer values are entered in cell.
	 */
	protected Control createControl(Composite parent) {
		final Text text = (Text)super.createControl(parent);
		//permit only digits or leading '-' or a 
		//single '.' to be entered
		text.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				event.doit = 
					(event.text.length() == 0)
				|| Character.isDigit(event.text.charAt(0))
				|| (event.text.charAt(0) == '-' && (text.getCaretPosition() == 0))
				|| (event.text.charAt(0) == '.' && (text.getSelectionText().indexOf('.') > 0 || text.getText().length() == 0 || text.getText().indexOf('.') == -1));
			}
		});
		return text;
	}
	
	/**
	 * Returns an Integer obtained by parsing the text in the cell.
	 */
	protected Object doGetValue() {
		String value = (String)super.doGetValue();
		return new Double(value);
	}
	
	/**
	 * Takes an Integer and passes it in String form to
	 * superclass method.
	 */
	protected void doSetValue(Object value) {
		Assert.isTrue(value instanceof Double);
		super.doSetValue(((Double)value).toString());
	}
}
