package etomica.plugin.wizards;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import etomica.action.Action;
import etomica.action.activity.ActivityIntegrate;
import etomica.integrator.IIntegrator;
import etomica.integrator.Integrator;
import etomica.plugin.editors.SimulationObjects;

/**
 * Selector that allows the user to pick an Integrator from the simulation.
 */
public class DataSourceHookupSelector extends Composite {

	private Combo integratorCombo;
    private Label integratorLabel;
	private HashMap integratorMap = new HashMap();
    
    public Integrator getIntegrator() {
        int item = integratorCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return (Integrator)integratorMap.get(integratorCombo.getItem(item));
    }        
    
	/**
	 * @param parent
	 * @param style
	 */
	public DataSourceHookupSelector(Composite parent, SimulationObjects simObjects, int style) {
		super(parent, style);
		
		initialize();

        String none = "(none)";
        Action[] controllerActions = simObjects.simulation.getController().getAllActions();
        integratorCombo.add(none);
        integratorMap.put(none,null);
        for (int i=0; i<controllerActions.length; i++) {
            if (controllerActions[i] instanceof ActivityIntegrate) {
                IIntegrator integrator = ((ActivityIntegrate)controllerActions[i]).getIntegrator();
                String str = integrator.toString();
                integratorCombo.add(str);
                integratorMap.put(str,integrator);
            }
        }
        if (integratorCombo.getItemCount() == 1) {
            integratorCombo.setVisible(false);
            integratorLabel.setText("No integrators");
        }
	}

	/**
	 * This method initializes the combo box for the Potential classes	
	 */
	private void createIntegratorCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		integratorCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		integratorCombo.setLayoutData(gridData9);
	}

	private void initialize() {
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
        this.setLayout(gridLayout2);
        gridLayout2.numColumns = 2;
        gridLayout2.makeColumnsEqualWidth = false;

        integratorLabel = new Label(this, SWT.NONE);
        integratorLabel.setText("Select an Integrator:");
        gridData8.horizontalSpan = 2;
        integratorLabel.setLayoutData(gridData8);
        createIntegratorCombo();
	}
}
