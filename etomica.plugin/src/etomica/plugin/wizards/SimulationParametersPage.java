package etomica.plugin.wizards;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

import etomica.util.EnumeratedType;
import etomica.util.ParameterBase;

/**
 * Wizard page that allows the user to review and change simulation parameters.
 * @author Andrew Schultz
 */
public class SimulationParametersPage extends WizardPage implements IPageChangedListener {

    public SimulationParametersPage() {
        super("Simulation Parameters");
        setTitle("Simulation Parameters");
        setDescription("This page set the Simulation Parameters.");
    }

    public void createControl(Composite parent) {
        parameters = ((NewSimulationPage)getPreviousPage()).getSimulationParam();
        if (parameters == null) {
            //user is still on the first page or selected a simulation 
            //without parameters
            return;
        }

        ((WizardDialog)getWizard().getContainer()).addPageChangedListener(this);

        // dump name/value pairs into a table
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;
        container.setLayout(gridLayout);
        GridData gridData = new org.eclipse.swt.layout.GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;

        Field[] fields = parameters.getClass().getFields();
        parameterControls = new Control[fields.length];
        
        //make an input widget for each field
        for (int i=0; i<fields.length; i++) {
            Label fieldLabel = new Label(container,SWT.NONE);
            fieldLabel.setText(fields[i].getName());
            fieldLabel.setLayoutData(gridData);
            Control control = makeInputWidget(container, parameters.getValueString(fields[i]), fields[i].getType());
            control.setLayoutData(gridData);
            parameterControls[i] = control;
        }
        
        //default parameters are always OK
        setPageComplete(true);
        setControl(container);
    }
    
    /**
     * Update the wizard error message and decide if it's OK to finish.
     */
    public void updateStatus() {
        try {
            //try to extract values from the input widgets and assign them to
            //the parameter fields.  If that doesn't throw, we're OK.
            getSimulationParameters();
        }
        catch (RuntimeException e) {
            setErrorMessage(e.getMessage());
            setPageComplete(false);
            return;
        }
        setErrorMessage(null);
        setPageComplete(true);
    }
    
    /**
     * Returns the simulation parameter object, with the user's values set.
     * If any user-entered value is invalid (doesn't match the type), an
     * IllegalArgumentException is thrown.
     */
    public ParameterBase getSimulationParameters() throws IllegalArgumentException {
        Field[] fields = parameters.getClass().getFields();
        for (int i=0; i<fields.length; i++) {
            String value = "";
            if (parameterControls[i] instanceof Button) { //boolean
                value = ((Button)parameterControls[i]).getSelection() ? "true" : "false";
            }
            else if (parameterControls[i] instanceof Text) {
                value = ((Text)parameterControls[i]).getText();
            }
            else if (parameterControls[i] instanceof Combo) {
                value = ((Combo)parameterControls[i]).getText();
            }
            else {
                throw new RuntimeException("Don't know how we managed to get a "+parameterControls[i].getClass().getName());
            }
            // might throw an exception, which we'll propogate on up
            parameters.setValue(fields[i], value);
        }
        return parameters;
    }
    
    public void pageChanged(PageChangedEvent event) {
        if (event.getSelectedPage() == getPreviousPage()) {
            // if the user goes back, nuke ourselves so we get recreated with
            // appropriate controls
            setControl(null);
        }
    }
    
    /**
     * Creates a PropertyDescriptor for the given |property| of Class |type|,
     * having name |name|. For properties which have a list of choices, the
     * |value| (if not null) is used as the current choice.
     */
    protected Control makeInputWidget(Composite parent, String value, Class type) {

        if(type == boolean.class) {
            Button button = new Button(parent, SWT.CHECK);
            button.setSelection(Boolean.valueOf(value).booleanValue());
            button.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    updateStatus();
                }
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            return button;
        }
        else if(EnumeratedType.class.isAssignableFrom(type)) {
            EnumeratedType[] choices = null;
            try {
                try {
                    Method method = type.getMethod("choices",null);
                    if (method != null) {
                        choices = (EnumeratedType[])method.invoke(null,null);
                    }
                }
                catch (InvocationTargetException e) {
                    // choices threw an excpetion
                    throw new RuntimeException(e);
                }
                catch (NoSuchMethodException e) {
                    // choices doesn't exist
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            catch (RuntimeException e) {
                WorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
            }
            if (choices == null || choices.length == 0) {
                WorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "No choices for "+type.getName(), null));
            }
            Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
            int selectedIndex = -1;
            for (int i=0; i<choices.length; i++) {
                if (choices[i].toString().equals(value)) {
                    selectedIndex = i;
                }
                combo.add(choices[i].toString());
            }
            if (selectedIndex != -1) {
                combo.select(selectedIndex);
            }
            combo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    updateStatus();
                }
            });
            return combo;
        }
        Text inputBox = new Text(parent, SWT.BORDER);
        inputBox.setText(value);
        inputBox.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateStatus();
            }
        });
        return inputBox;
    }
    
    protected ParameterBase parameters;
    protected Control[] parameterControls;
}
