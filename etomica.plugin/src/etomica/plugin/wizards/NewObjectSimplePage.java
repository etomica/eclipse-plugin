package etomica.plugin.wizards;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.plugin.editors.SimulationObjects;

/**
 * The "New" wizard page allows the user to select a class for a new object
 * from a SimpleClassSelector.  The Wizard is expected to configure the 
 * selector via fixupSelector.
 * @author Andrew Schultz
 */
public class NewObjectSimplePage extends WizardPage {
    private final SimulationObjects simObjects;
    private Object[] extraParameters = new Object[0];
    public SimpleClassSelector classSelector;
	
    private final String name;
    private final SimpleClassWizard wizard;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewObjectSimplePage(SimpleClassWizard wizard, SimulationObjects simObjects, String name) {
        super("wizardPage");
        this.simObjects = simObjects;
        this.name = name;
        this.wizard = wizard;
        setTitle("Etomica "+name+" Wizard");
        setDescription("This wizard creates a new "+name+".");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ( (Class) element ).getName();
        }
    }

    /** 
     * Creates object based on user's settings
     */
    public Object createObject() {
        Object obj = classSelector.createObject(extraParameters);
        if (obj == null) {
            return null;
        }
        Method setter = null;
        try {
            setter = obj.getClass().getMethod("setName",new Class[]{String.class});
        }
        catch (NoSuchMethodException e) {
            // setName doesn't exist, which is ok
        }
        if (setter != null) {
            try {
                setter.invoke(obj,new Object[]{getObjectName()});
            }
            catch (IllegalAccessException e) {
                System.out.println("Illegal access to set name on "+obj);
            }
            catch (InvocationTargetException e) {
                System.out.println("Exception trying to set name on "+obj+": "+e.getTargetException());
            }
        }
        return obj;
    }
    
    public void setExtraParameters(Object[] moreParameters) {
        extraParameters = (Object[])moreParameters.clone();
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    classSelector = new SimpleClassSelector(root_container, org.eclipse.swt.SWT.NONE, name, simObjects);
	    wizard.fixupSelector(classSelector);

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        };
        
	    classSelector.objectName.addModifyListener(modifyListener); 
        classSelector.categoryCombo.addModifyListener(modifyListener);
        classSelector.classCombo.addModifyListener(modifyListener);
        classSelector.potentialMasterCombo.addModifyListener(modifyListener);
        
        setPageComplete(false);
		dialogChanged();
		setControl(root_container);
	}

	/**
	 * Ensures that both text fields are set.
	 */

	protected void dialogChanged() {
        if (classSelector.getCategory() != Object.class && !checkObjectName()) {
            return;
        }
        if (classSelector.hasCategories() && !checkCategory()) {
            return;
        }
        if (!checkClass()) {
            return;
        }
        if (!checkPotentialMaster()) {
            return;
        }
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkObjectName() {
		String objectName = getObjectName();
		if (objectName.length() == 0) {
			updateStatus("Name is empty");
			return false;
		}
		
		return true;
	}
	
    private boolean checkCategory() {
        if (classSelector.getCategory() == null) {
            updateStatus("You must select a category");
            return false;
        }
        return true;
    }
    
    private boolean checkClass() {
        if (classSelector.getSelection() == null) {
            updateStatus("You must select a "+name);
            return false;
        }
        return true;
    }

    private boolean checkPotentialMaster() {
        if (classSelector.potentialMasterCombo.isVisible() && classSelector.getPotentialMasterSelection() == null) {
            updateStatus("You must select a PotentialMaster");
            return false;
        }
        return true;
    }

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getObjectName() {
		return classSelector.objectName.getText();
	}


    /**
     * All Wizards using a this class as a WizardPage must implement this
     * interface, which allows the Wizard to configure the selector to its
     * liking (categories, extraChoices, excludedClasses, etc)
     * @author Andrew Schultz
     */
    public interface SimpleClassWizard {
    
        /**
         * This method asks the wizard to fix up the SimpleClassSelector to its
         * liking.  The SimpleClassSelector invokes this method from its 
         * createControl method and passes itself as the argument.
         */
        public void fixupSelector(SimpleClassSelector selector);
    }
}
