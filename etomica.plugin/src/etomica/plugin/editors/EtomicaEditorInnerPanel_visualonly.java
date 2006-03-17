/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
/**
 * @author Henrique
 *
 */
public class EtomicaEditorInnerPanel_visualonly extends Composite {

	private TabFolder tabFolder = null;
	private Composite compositeActions = null;
	private Composite compositeConfiguration = null;
	private Composite composite1 = null;
	private Label label1 = null;
	public Tree objectTree = null;
    public Tree actionsTree = null;
	/**
	 * @param parent
	 * @param style
	 */
	public EtomicaEditorInnerPanel_visualonly(Composite parent, int style) {
		super(parent, style);
		
		initialize();
	}

    /**
	 * This method initializes tabFolder	
	 *
	 */
	private void createTabFolder() {
		tabFolder = new TabFolder(this, SWT.NONE);		   
		createComposite3();
		TabItem tabItem6 = new TabItem(tabFolder, SWT.NONE);
		createComposite2();
		TabItem actionsTab = new TabItem(tabFolder, SWT.NONE);
		actionsTab.setControl(compositeActions);
		actionsTab.setText("Activities");
		actionsTab.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/etomica/plugin/icons/simulations.gif")));
		tabItem6.setControl(compositeConfiguration);
			tabItem6.setText("Summary");
		tabItem6.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/etomica/plugin/icons/watch_exp.gif")));
	}

	/**
	 * This method initializes composite	
	 *
	 */    
	private void createComposite2() {
        GridData gridData10 = new org.eclipse.swt.layout.GridData();
        GridData gridData6 = new org.eclipse.swt.layout.GridData();
		compositeActions = new Composite(tabFolder, SWT.NONE);
        Label actionsLabel = new Label(composite1, SWT.NONE);
        createActionsTree();
        compositeActions.setLayoutData(gridData6);
        compositeActions.setLayout(new GridLayout());
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.grabExcessVerticalSpace = true;
        actionsLabel.setText("All Objects in the simulation");
        actionsLabel.setLayoutData(gridData10);
        gridData10.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData10.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
    }
	
	/**
	 * This method initializes composite	
	 *
	 */    
	private void createComposite3() {
		GridLayout gridLayout1 = new GridLayout();
		compositeConfiguration = new Composite(tabFolder, SWT.NONE);		   
		createComposite12();
		compositeConfiguration.setLayout(gridLayout1);
		gridLayout1.numColumns = 2;
		gridLayout1.makeColumnsEqualWidth = false;
	}
	/**
	 * This method initializes group3	
	 *
	 */    
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
	 * This method initializes tree	
	 *
	 */
	private void createTree() {
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		objectTree = new Tree(composite1, SWT.BORDER);		   
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.grabExcessHorizontalSpace = true;
		objectTree.setLayoutData(gridData2);
	}
    /**
     * This method initializes tree 
     *
     */
    private void createActionsTree() {
        GridData gridData2 = new org.eclipse.swt.layout.GridData();
        actionsTree = new Tree(compositeActions, SWT.BORDER);         
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.grabExcessVerticalSpace = true;
        gridData2.grabExcessHorizontalSpace = true;
        actionsTree.setLayoutData(gridData2);
    }

	private void initialize() {
		FillLayout fillLayout4 = new FillLayout();
		createTabFolder();
		this.setLayout(fillLayout4);
		fillLayout4.type = org.eclipse.swt.SWT.HORIZONTAL;
		setSize(new org.eclipse.swt.graphics.Point(762,433));
	}
}
