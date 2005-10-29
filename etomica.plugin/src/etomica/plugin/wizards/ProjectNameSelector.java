/*
 * Created on May 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProjectNameSelector extends Composite {

	public Label project_selection_label = null;
	public Text container_name = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;
	
	private Label label5 = null;
	
	/**
	 * @param parent
	 * @param style
	 */
	public ProjectNameSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
	}

	private void initialize() {
		GridData gridData4 = new org.eclipse.swt.layout.GridData();
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
		project_selection_label = new Label(this, SWT.NONE);
		container_name = new Text(this, SWT.BORDER);
		this.setLayout(gridLayout2);
		gridLayout2.numColumns = 3;
		gridLayout2.makeColumnsEqualWidth = false;
		project_selection_label.setText("Enter a project name:");
		project_selection_label.setLayoutData(gridData3);
		gridData3.horizontalSpan = 1;
		gridData4.horizontalSpan = 1;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		container_name.setLayoutData(gridData4);
		setSize(new org.eclipse.swt.graphics.Point(364,162));
	}
}  //  @jve:decl-index=0:visual-constraint="38,24"
