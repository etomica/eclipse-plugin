package etomica.plugin.wizards;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorMC;
import etomica.integrator.IntegratorMD;
import etomica.integrator.IntegratorManagerMC;
import etomica.plugin.ClassDiscovery;
import etomica.plugin.Registry;
import etomica.simulation.Simulation;

/**
 * Selector for Potential class and the Species it will be act on.
 */
public class IntegratorSelector extends Composite {

	public Combo integratorClassCombo;
    public Combo integratorTypeCombo;
	public Text integratorName;
    private Label integratorDescription;
	private HashMap integratorClassMap = new HashMap();
    private LinkedHashMap integratorTypeMap = new LinkedHashMap();
    private final static Object[][] integratorTypes = new Object[][]{
        {"Integrator",Integrator.class},{"Integrator Manager",IntegratorManagerMC.class},{"IntegratorMC",IntegratorMC.class},
        {"IntegratorMD",IntegratorMD.class}};
	
	public Integrator createIntegrator(Simulation simulation)
	{
		Class integratorClass = getIntegratorClass();
		if ( integratorClass!=null )
		{
			try {
                Constructor constructor = integratorClass.getDeclaredConstructor(new Class[]{Simulation.class});
                if (constructor != null) {
                    return (Integrator)constructor.newInstance(new Object[]{simulation});
                }
			} catch (InstantiationException e) {
				System.err.println( "Could not instantiate Action: " + e.getMessage() );
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println( "Illegal access while creating Action: " + e.getMessage() );
				e.printStackTrace();
			}
            catch (NoSuchMethodException e) {
                System.err.println( "Constructor didn't exist while creating Integrator: " + e.getMessage() );
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                System.err.println( "Exception creating Integrator: " + e.getMessage() );
                e.printStackTrace();
            }
		}
        return null;
	}
    
    public Class getIntegratorType() {
        int item = integratorTypeCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return (Class)integratorTypeMap.get(integratorTypeCombo.getItem(item));
    }
    
    public Class getIntegratorClass() {
        int item = integratorClassCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return (Class)integratorClassMap.get(integratorClassCombo.getItem(item));
    }
    
	/**
	 * @param parent
	 * @param style
	 */
	public IntegratorSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
        for (int i=0; i<integratorTypes.length; i++) {
            integratorTypeMap.put(integratorTypes[i][0],integratorTypes[i][1]);
            integratorTypeCombo.add((String)integratorTypes[i][0]);
        }

        integratorTypeCombo.select(0);
        
        rebuildIntegratorList();
        
        integratorClassCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = integratorClassCombo.getSelectionIndex();
                if (item == -1) {
                    return;
                }
                Class actionClass = (Class)integratorClassMap.get(integratorClassCombo.getItem(item));
                String str = actionClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo(actionClass);
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ": \n"+longDesc;
                }
                integratorDescription.setText(str);
                IntegratorSelector.this.layout();
            }
        });
	}

    /**
     * Builds the list of potentials in the combo box based on the selections
     * in the numBody and Hard/Soft combo boxes.
     */
    public void rebuildIntegratorList() {
        System.out.println("hey there");
        integratorClassMap.clear();
        integratorClassCombo.removeAll();
        System.out.println("nuking entries");
        Collection integrators_from_registry;

        Class integratorType = getIntegratorType();
        if (integratorType == null) {
            return;
        }
        integrators_from_registry = Registry.queryWhoExtends(etomica.integrator.Integrator.class);

        Iterator iterator = integrators_from_registry.iterator(); 
        while( iterator.hasNext() )
        {
            Class integratorClass = (Class)iterator.next();
            if (integratorType == Integrator.class) {
                // if we're looking for generic Actions, exclude the others
                Iterator typeIterator = integratorTypeMap.keySet().iterator();
                boolean skipMe = false;
                while (typeIterator.hasNext()) {
                    Class otherActionType = (Class)integratorTypeMap.get(typeIterator.next());
                    if (otherActionType == Integrator.class) {
                        continue;
                    }
                    if (otherActionType.isAssignableFrom(integratorClass)) {
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
                if (!integratorType.isAssignableFrom(integratorClass)) {
                    continue;
                }
            }
            EtomicaInfo info = EtomicaInfo.getInfo(integratorClass);
            String str = ClassDiscovery.chopClassName(integratorClass.getName())+": "+info.getShortDescription();
            integratorClassCombo.add(str);
            integratorClassMap.put(str, integratorClass );
        }
        System.out.println("integrator entries added");
    }

    /**
     * This method initializes the combo box for the Potential classes  
     */
    private void createActionActivityCombo() {
        GridData gridData = new org.eclipse.swt.layout.GridData();
        integratorTypeCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);           
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        integratorTypeCombo.setLayoutData(gridData);
    }

	/**
	 * This method initializes the combo box for the Potential classes	
	 */
	private void createActionTypeCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		integratorClassCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		integratorClassCombo.setLayoutData(gridData9);
	}

	private void initialize() {
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
		integratorName = new Text(this, SWT.BORDER);
        gridData6.horizontalSpan = 1;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        integratorName.setLayoutData(gridData6);
        
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
        
        integratorDescription = new Label(this, SWT.WRAP);
        gridData1.horizontalSpan = 2;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        integratorDescription.setLayoutData(gridData1);
        integratorDescription.setText("");

//        setSize(new org.eclipse.swt.graphics.Point(364,462));
	}
}
