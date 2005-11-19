package etomica.plugin.wizards;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import etomica.EtomicaInfo;
import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.Activity;
import etomica.action.AtomAction;
import etomica.action.IntegratorAction;
import etomica.action.PhaseAction;
import etomica.action.SimulationAction;
import etomica.action.activity.ActivityIntegrate;
import etomica.action.activity.Controller;
import etomica.plugin.ClassDiscovery;
import etomica.plugin.Registry;

/**
 * Selector for Potential class and the Species it will be act on.
 */
public class ActionSelector extends Composite {

	public Combo actionClassCombo;
    public Combo actionTypeCombo;
	public Text actionName;
    private Label actionDescription;
	private HashMap actionClassMap = new HashMap();
    private LinkedHashMap actionTypeMap = new LinkedHashMap();
    private final static Object[][] actionTypes = new Object[][]{
        {"Action",Action.class},{"Activity",Activity.class},{"Phase Action",PhaseAction.class},
        {"Integrator Action",IntegratorAction.class},{"Simulation Action",SimulationAction.class},
        {"Atom Action",AtomAction.class}};
    
	private Class[] excludedClasses = new Class[0];
    
	public Action createAction()
	{
		Class actionClass = getActionClass();
		if ( actionClass!=null )
		{
			try {
                return (Action)actionClass.newInstance();
			} catch (InstantiationException e) {
				System.err.println( "Could not instantiate Action: " + e.getMessage() );
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println( "Illegal access while creating Action: " + e.getMessage() );
				e.printStackTrace();
			}
		}
        return null;
	}
    
    public Class getActionType() {
        int item = actionTypeCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return (Class)actionTypeMap.get(actionTypeCombo.getItem(item));
    }
    
    public Class getActionClass() {
        int item = actionClassCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return (Class)actionClassMap.get(actionClassCombo.getItem(item));
    }
    
	/**
	 * @param parent
	 * @param style
	 */
	public ActionSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
        // exclude AtomAction by default.
		setExcludedClasses(new Class[]{AtomAction.class});
        rebuildActionList();
        
        actionClassCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = actionClassCombo.getSelectionIndex();
                if (item == -1) {
                    return;
                }
                Class actionClass = (Class)actionClassMap.get(actionClassCombo.getItem(item));
                String str = actionClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo(actionClass);
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ": \n"+longDesc;
                }
                actionDescription.setText(str);
                ActionSelector.this.layout();
            }
        });
	}
    
    public void setExcludedClasses(Class[] classes) {
        if (classes == null) {
            excludedClasses = new Class[0];
        }
        else {
            excludedClasses = classes;
        }

        actionTypeMap.clear();
        actionTypeCombo.removeAll();
        // if one of the Action types is a subclass of an excluded
        // class, don't show it as an option
        for (int i=0; i<actionTypes.length; i++) {
            boolean excluded = false;
            for (int j=0; j<excludedClasses.length; j++) {
                if (excludedClasses[j].isAssignableFrom((Class)actionTypes[i][1])) {
                    excluded = true;
                    break;
                }
            }
            if (!excluded) {
                actionTypeMap.put(actionTypes[i][0],actionTypes[i][1]);
                actionTypeCombo.add((String)actionTypes[i][0]);
            }
        }

        actionTypeCombo.select(0);
    }

    /**
     * Builds the list of potentials in the combo box based on the selections
     * in the numBody and Hard/Soft combo boxes.
     */
    public void rebuildActionList() {
        actionClassMap.clear();
        actionClassCombo.removeAll();
        Collection actions_from_registry;

        Class actionType = getActionType();
        if (actionType == null) {
            return;
        }
        actions_from_registry = Registry.queryWhoExtends(etomica.action.Action.class);

        Iterator iterator = actions_from_registry.iterator(); 
        while( iterator.hasNext() )
        {
            Class actionClass = (Class)iterator.next();
            boolean excluded = false;
            for (int j=0; j<excludedClasses.length; j++) {
                if (excludedClasses[j].isAssignableFrom(actionClass)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            }
            if (actionType == Action.class) {
                if (AtomAction.class.isAssignableFrom(actionClass)) {
                    continue;
                }
                // if we're looking for generic Actions, exclude the others
                Iterator typeIterator = actionTypeMap.keySet().iterator();
                boolean skipMe = false;
                while (typeIterator.hasNext()) {
                    Class otherActionType = (Class)actionTypeMap.get(typeIterator.next());
                    if (otherActionType == Action.class) {
                        continue;
                    }
                    if (otherActionType.isAssignableFrom(actionClass)) {
                        skipMe = true;
                        break;
                    }
                }
                if (skipMe) {
                    continue;
                }
            }
            else {
                // user wants a specific type of Action.
                if (!actionType.isAssignableFrom(actionClass)) {
                    continue;
                }
                if (actionType == Activity.class) {
                    if (Controller.class.isAssignableFrom(actionClass)) {
                        // user should never make a Controller
                        continue;
                    }
                    if (ActivityIntegrate.class.isAssignableFrom(actionClass)) {
                        // ActivityIntegrate is handled separately
                        continue;
                    }
                }
            }
            EtomicaInfo info = EtomicaInfo.getInfo(actionClass);
            String str = ClassDiscovery.chopClassName(actionClass.getName())+": "+info.getShortDescription();
            actionClassCombo.add(str);
            actionClassMap.put(str, actionClass );
        }

    }

    /**
     * This method initializes the combo box for the Potential classes  
     */
    private void createActionActivityCombo() {
        GridData gridData = new org.eclipse.swt.layout.GridData();
        actionTypeCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);           
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        actionTypeCombo.setLayoutData(gridData);
    }

	/**
	 * This method initializes the combo box for the Potential classes	
	 */
	private void createActionTypeCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		actionClassCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		actionClassCombo.setLayoutData(gridData9);
	}

	private void initialize() {
		GridData gridData13 = new org.eclipse.swt.layout.GridData();
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
        GridData gridData1 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
        this.setLayout(gridLayout2);
        gridLayout2.numColumns = 2;
        gridLayout2.makeColumnsEqualWidth = false;

        Label label = new Label(this, SWT.NONE);
        label.setText("Action name:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
		actionName = new Text(this, SWT.BORDER);
        gridData6.horizontalSpan = 1;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        actionName.setLayoutData(gridData6);
        
        label = new Label(this, SWT.NONE);
        label.setText("Activity or Actions:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
        createActionActivityCombo();
        
        label = new Label(this, SWT.NONE);
        label.setText("Select a action type");
        gridData8.horizontalSpan = 2;
        label.setLayoutData(gridData8);
        createActionTypeCombo();
        
        actionDescription = new Label(this, SWT.WRAP);
        gridData1.horizontalSpan = 2;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        actionDescription.setLayoutData(gridData1);
        actionDescription.setText("");

//        setSize(new org.eclipse.swt.graphics.Point(364,462));
	}
}
