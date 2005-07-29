/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.editors;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
/**
 * @author Henrique
 *
 */
public class EtomicaEditorInnerPanel extends Composite {

	private TabFolder tabFolder = null;
	private Composite compositeSpecies = null;
	private Composite compositePhases = null;
	private Composite compositeActions = null;
	private Composite compositeConfiguration = null;
	private Group group3 = null;
	private Composite composite = null;
	private List list = null;
	private Label label = null;
	private Group group2 = null;
private Button radioButton = null;
	private Button radioButton1 = null;
	private Button radioButton2 = null;
	private Composite composite1 = null;
	private Composite composite2 = null;
	private Label label1 = null;
	public Tree objectList = null;
	private Button button = null;
	private Button button1 = null;
	private Button button2 = null;
	private Button button3 = null;
	private Button button4 = null;
	private Composite composite3 = null;
	private Composite composite4 = null;
	private List list1 = null;
	private Label label2 = null;
	private Button button5 = null;
	/**
	 * @param parent
	 * @param style
	 */
	public EtomicaEditorInnerPanel(Composite parent, int style) {
		super(parent, style);
		
		initialize();
	}

	public Composite getPhasePanel()
	{
		return composite4;
	}
	/**
	 * This method initializes tabFolder	
	 *
	 */    
	private void createTabFolder() {
		tabFolder = new TabFolder(this, SWT.NONE);		   
		createComposite3();
		createComposite();
		createComposite1();
		TabItem tabItem6 = new TabItem(tabFolder, SWT.NONE);
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		createComposite2();
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NONE);
		TabItem tabItem4 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setControl(compositeSpecies);
		tabItem2.setText("Species");
		tabItem2.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/etomica/plugin/icons/types.gif")));
		tabItem3.setControl(compositePhases);
		tabItem3.setText("Phases");
		tabItem3.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/etomica/plugin/icons/type.gif")));
		tabItem4.setControl(compositeActions);
		tabItem4.setText("Activities");
		tabItem4.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/etomica/plugin/icons/simulations.gif")));
		tabItem6.setControl(compositeConfiguration);
			tabItem6.setText("Summary");
		tabItem6.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/etomica/plugin/icons/watch_exp.gif")));
}
	/**
	 * This method initializes composite	
	 *
	 */    
	private void createComposite() {
		GridLayout gridLayout4 = new GridLayout();
		compositeSpecies = new Composite(tabFolder, SWT.NONE);		   
		createComposite4();
		createGroup3();
		compositeSpecies.setLayout(gridLayout4);
		gridLayout4.numColumns = 3;
	}
	/**
	 * This method initializes composite1	
	 *
	 */    
	private void createComposite1() {
		GridLayout gridLayout11 = new GridLayout();
		compositePhases = new Composite(tabFolder, SWT.NONE);		   
		createComposite32();
		createComposite42();
		compositePhases.setLayout(gridLayout11);
		gridLayout11.numColumns = 2;
	}
	/**
	 * This method initializes composite	
	 *
	 */    
	private void createComposite2() {
		compositeActions = new Composite(tabFolder, SWT.NONE);		   
	}
	
	/**
	 * This method initializes composite	
	 *
	 */    
	private void createComposite3() {
		GridLayout gridLayout1 = new GridLayout();
		compositeConfiguration = new Composite(tabFolder, SWT.NONE);		   
		createComposite12();
		createComposite22();
		compositeConfiguration.setLayout(gridLayout1);
		gridLayout1.numColumns = 2;
		gridLayout1.makeColumnsEqualWidth = false;
	}
	/**
	 * This method initializes group3	
	 *
	 */    
	private void createGroup3() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		group3 = new Group(compositeSpecies, SWT.NONE);		   
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.grabExcessVerticalSpace = true;
		group3.setLayoutData(gridData9);
	}
	/**
	 * This method initializes composite	
	 *
	 */    
	private void createComposite4() {
		GridData gridData11 = new GridData();
		FillLayout fillLayout10 = new FillLayout();
		composite = new Composite(compositeSpecies, SWT.NONE);		   
		label = new Label(composite, SWT.NONE);
		list = new List(composite, SWT.NONE);
		composite.setLayout(fillLayout10);
		composite.setLayoutData(gridData11);
		fillLayout10.type = org.eclipse.swt.SWT.VERTICAL;
		fillLayout10.spacing = 2;
		label.setText("Species in use:");
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.grabExcessVerticalSpace = true;
	}
	/**
	 * This method initializes group2	
	 *
	 */    
	private void createGroup2() {
			
			
		FillLayout fillLayout41 = new FillLayout();
		group2 = new Group(composite2, SWT.SHADOW_OUT);		   
		radioButton = new Button(group2, SWT.RADIO);
		radioButton1 = new Button(group2, SWT.RADIO);
		radioButton2 =	new Button(group2, SWT.RADIO);
		group2.setText("Unit System");
		group2.setLayout(fillLayout41);
		radioButton.setText("MKS");
		fillLayout41.type = org.eclipse.swt.SWT.VERTICAL;
		radioButton1.setText("SI");
		radioButton2.setText("LJ");
		radioButton2.setSelection(true);
	}
	/**
	 * This method initializes composite1	
	 *
	 */    
	private void createComposite12() {
		GridData gridData10 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		composite1 = new Composite(compositeConfiguration, SWT.NONE);		   
		label1 = new Label(composite1, SWT.NONE);
		createTree();
		composite1.setLayoutData(gridData6);
		composite1.setLayout(new GridLayout());
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = true;
		label1.setText("All Objects in the simulation");
		label1.setLayoutData(gridData10);
		gridData10.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData10.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
	}
	/**
	 * This method initializes composite2	
	 *
	 */    
	private void createComposite22() {
		GridData gridData7 = new org.eclipse.swt.layout.GridData();
		composite2 = new Composite(compositeConfiguration, SWT.NONE);		   
		createGroup2();
		button = new Button(composite2, SWT.NONE);
		button2 = new Button(composite2, SWT.NONE);
		button3 = new Button(composite2, SWT.NONE);
		button4 = new Button(composite2, SWT.NONE);
		button1 = new Button(composite2, SWT.NONE);
		composite2.setLayout(new GridLayout());
		composite2.setLayoutData(gridData7);
		gridData7.grabExcessVerticalSpace = true;
		gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData7.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		button.setText("Add Phase...");
		button1.setText("Add Activity...");
		button2.setText("Add Species...");
		button3.setText("Add Meter...");
		button4.setText("Add Device...");
	}
	/**
	 * This method initializes tree	
	 *
	 */    
	private void createTree() {
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		objectList = new Tree(composite1, SWT.BORDER);		   
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.grabExcessHorizontalSpace = true;
		objectList.setLayoutData(gridData2);
	}
	/**
	 * This method initializes composite3	
	 *
	 */    
	private void createComposite32() {
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData71 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
		GridData gridData21 = new org.eclipse.swt.layout.GridData();
		composite3 = new Composite(compositePhases, SWT.NONE);		   
		label2 = new Label(composite3, SWT.NONE);
		list1 = new List(composite3, SWT.NONE);
		button5 = new Button(composite3, SWT.NONE);
		gridData21.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData21.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData21.grabExcessVerticalSpace = true;
		composite3.setLayoutData(gridData21);
		composite3.setLayout(new GridLayout());
		gridData5.grabExcessVerticalSpace = true;
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		list1.setLayoutData(gridData5);
		label2.setText("Label");
		label2.setLayoutData(gridData71);
		gridData71.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData71.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData8.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		button5.setLayoutData(gridData8);
		button5.setText("New...");
	}
	/**
	 * This method initializes composite4	
	 *
	 */    
	private void createComposite42() {
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		composite4 = new Composite(compositePhases, SWT.NONE);		   
		gridData3.grabExcessVerticalSpace = true;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		composite4.setLayoutData(gridData3);
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
		EtomicaEditorInnerPanel thisClass = new EtomicaEditorInnerPanel(shell, org.eclipse.swt.SWT.NONE);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
		display.dispose();		
	}

	private void initialize() {
		FillLayout fillLayout4 = new FillLayout();
		createTabFolder();
		this.setLayout(fillLayout4);
		fillLayout4.type = org.eclipse.swt.SWT.HORIZONTAL;
		setSize(new org.eclipse.swt.graphics.Point(762,433));
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
