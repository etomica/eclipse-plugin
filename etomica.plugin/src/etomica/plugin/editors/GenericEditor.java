/*
 * Created on May 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.editors;


import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.layout.GridLayout;
/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenericEditor extends Composite {

	/**
	 * @param parent
	 * @param style
	 */
	public GenericEditor(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public static void main(String[] args) {
		/* Before this is run, be sure to set up the following in the launch configuration 
		 * (Arguments->VM Arguments) for the correct SWT library path. 
		 * The following is a windows example:
		 * -Djava.library.path="installation_directory\plugins\org.eclipse.swt.win32_3.0.0\os\win32\x86"
		 */
		org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();		
		org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell(display);
		shell.setLayout(new org.eclipse.swt.layout.FillLayout());
		shell.setSize(new org.eclipse.swt.graphics.Point(300,200));
		GenericEditor thisClass = new GenericEditor(shell, org.eclipse.swt.SWT.NONE);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
		display.dispose();		
	}

	private void initialize() {
		this.setLayout(new GridLayout());
		setSize(new org.eclipse.swt.graphics.Point(300,200));
	}
}
