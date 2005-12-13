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

import etomica.simulation.Simulation;

/**
 * The "New" wizard page allows the user to select a class for a new object
 * from a SimpleClassSelector.  The Wizard is expected to configure the 
 * selector via fixupSelector.
 */
public class NewObjectSimplePage extends WizardPage {
    private final Simulation simulation;
    public SimpleClassSelector classSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean objectNameModified = false;
    private boolean classModified = false;
    private boolean categoryModified = false;
    private final String name;
    private final SimpleClassWizard wizard;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewObjectSimplePage(SimpleClassWizard wizard, Simulation sim, String name) {
        super("wizardPage");
        simulation = sim;
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

    /** Creates simulation based on user's settings 
     * 
     * @return new Simulation based on user's choices 
     */
    public Object createObject() {
        Object obj = classSelector.createObject(simulation);
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
	
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    classSelector = new SimpleClassSelector(root_container, org.eclipse.swt.SWT.NONE, name);
	    wizard.fixupSelector(classSelector);
        
	    classSelector.objectName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            objectNameModified = true;
	            dialogChanged();
	        }
		});

        classSelector.categoryCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                categoryModified = true;
                dialogChanged();
            }
        });

        classSelector.classCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                classModified = true;
                dialogChanged();
            }
        });

        setPageComplete(false);
		dialogChanged();
		setControl(root_container);
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
        if (categoryModified) {
            categoryModified = false;
            classSelector.rebuildClassList();
        }
        if (!checkObjectName()) {
            return;
        }
        if (classSelector.hasCategories() && !checkCategory()) {
            return;
        }
        if (!checkClass()) {
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

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getObjectName() {
		return classSelector.objectName.getText();
	}

    
    public interface SimpleClassWizard {
        public void fixupSelector(SimpleClassSelector selector);
    }
}