package etomica.plugin.wizards;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ExceptionHandler;

import etomica.EtomicaInfo;
import etomica.plugin.ClassDiscovery;
import etomica.plugin.Registry;
import etomica.potential.P2SoftSphericalTruncated;
import etomica.potential.Potential;
import etomica.potential.Potential2SoftSpherical;
import etomica.potential.PotentialHard;
import etomica.potential.PotentialSoft;
import etomica.simulation.Simulation;
import etomica.species.Species;

/**
 * Selector for Potential class and the Species it will be act on.
 */
public class PotentialSpeciesSelector extends Composite {

	protected Combo potentialCombo;
    protected Combo potentialBodyCombo;
    protected Combo potentialHardSoftCombo;
	protected Text potentialName;
	protected Combo speciesCombo;
    protected Combo speciesCombo2;
    protected Button truncatedCheckbox;
    protected Label potentialDescription;
	protected HashMap potentialsMap = new HashMap();
	private HashMap speciesMap = new HashMap();
    private Simulation simulation;
	
    /**
     * Returns an instance of the potential the user selected
     */
	public Potential createPotential()
	{
		Class potentialClass = getPotentialClass();
		if ( potentialClass!=null )
		{
            if (potentialClass == etomica.potential.PotentialGroup.class) {
                return simulation.potentialMaster.makePotentialGroup(getPotenialNumBody());
            }
            Potential potential = null;
            Constructor[] constructors = potentialClass.getDeclaredConstructors();
            try {
                for (int i=0; i<constructors.length; i++) {
                    Class[] parameterTypes = constructors[i].getParameterTypes();
                    if (parameterTypes.length != 1) {
                        continue;
                    }
                    if (parameterTypes[0].isInstance(simulation)) {
                        potential = (Potential)constructors[i].newInstance(new Object[]{simulation});
                    }
                    else if (parameterTypes[0].isInstance(simulation.space)) {
                        potential = (Potential)constructors[i].newInstance(new Object[]{simulation.space});
                    }
                    else {
                        continue;
                    }
                    break;
                }
            }
            catch (InstantiationException e) {
                ExceptionHandler.getInstance().handleException(e);
			}
            catch (IllegalAccessException e) {
                ExceptionHandler.getInstance().handleException(e);
			}
            catch (InvocationTargetException e) {
                ExceptionHandler.getInstance().handleException(e);
            }
            if (potential != null && getIsPotentialTruncated()) {
                // use 0 as initial truncation.  user must set an appropriate value
                potential = new P2SoftSphericalTruncated((Potential2SoftSpherical)potential,0);
            }
            return potential;
		}
        return null;
	}

    /**
     * Returns the number of bodies the user selected, or -1 if 
     * no selection was made.
     */
    public int getPotenialNumBody() {
        String str = potentialBodyCombo.getText();
        if (str.length() == 0) {
            return -1;
        }
        return Integer.parseInt(str);
    }
    
    /**
     * Returns true if the user selected to use a hard potential
     */
    public boolean getPotentialIsHard() {
        String str = potentialHardSoftCombo.getText();
        return str.equals("Hard");
    }
    
    /**
     * Returns true if the user selected to use a soft potential
     */
    public boolean getPotenialIsSoft() {
        String str = potentialHardSoftCombo.getText();
        return str.equals("Soft");
    }
    
    /**
     * Returns the Potential class the user chose, or null if the user did not
     * select a Potential class.
     */
    public Class getPotentialClass() {
        int item = potentialCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        Class potentialClass = (Class) potentialsMap.get( potentialCombo.getItem( item ) );
        return potentialClass;
            
    }
    
    /**
     * Returns of Species the user selected for the potential to apply to.
     * The array might have one or two null entries if one or both of the 
     * Species are not selected.  The length of the array is equal to the 
     * number of bodies for the potential (return value of 
     * getPotentialNumBody()) 
     */
    public Species[] getSpecies() {
        Species[] speciesArray = new Species[getPotenialNumBody()];
        int item = speciesCombo.getSelectionIndex();
        if (item == -1) {
            return speciesArray;
        }
        speciesArray[0] = (Species) speciesMap.get( speciesCombo.getItem( item ) );
        if (speciesArray.length == 2) {
            item = speciesCombo2.getSelectionIndex();
            if (item != -1) {
                speciesArray[1] = (Species) speciesMap.get( speciesCombo2.getItem( item ) );
            }
        }
        return speciesArray;
    }
    
    public boolean getIsPotentialTruncated() {
        return truncatedCheckbox.isEnabled() && truncatedCheckbox.getSelection();
    }
    
	public PotentialSpeciesSelector(Simulation sim, Composite parent, int style) {
		super(parent, style);
		
        simulation = sim;
        
		initialize();
		
        potentialBodyCombo.add("1");
        potentialBodyCombo.add("2");
        potentialBodyCombo.select(1); // 2-body
        
        potentialHardSoftCombo.add("Hard");
        potentialHardSoftCombo.add("Soft");
        potentialHardSoftCombo.add("Other");
        potentialHardSoftCombo.select(0); // hard
        
        truncatedCheckbox.setEnabled(false);

        rebuildPotentialList();
        
        potentialCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = potentialCombo.getSelectionIndex();
                if (item == -1) {
                    truncatedCheckbox.setEnabled(false);
                    return;
                }
                Class potentialClass = (Class)potentialsMap.get(potentialCombo.getItem(item));
                String str = potentialClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo(potentialClass);
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ":\n"+longDesc;
                }
                potentialDescription.setText(str);
                PotentialSpeciesSelector.this.layout();
                
                truncatedCheckbox.setEnabled(Potential2SoftSpherical.class.isAssignableFrom(potentialClass));
            }
        });
        
        //add the Species from the simulation
        Species[] speciesArray = simulation.speciesRoot.getSpecies();
        for (int i=0; i<speciesArray.length; i++) {
			Species species = speciesArray[i];
			speciesCombo.add(species.getName());
            speciesCombo2.add(species.getName());
			speciesMap.put(species.getName(), species);
		}
	}
    
    /**
     * Builds the list of potentials in the combo box based on the selections
     * in the numBody and Hard/Soft combo boxes.
     */
    public void rebuildPotentialList() {
        potentialsMap.clear();
        potentialCombo.removeAll();
        Collection potentials_from_registry;
        int nBody = getPotenialNumBody();
        if (nBody == -1) {
            return;
        }
        if (nBody == 1) {
            potentials_from_registry = Registry.queryWhoExtends(etomica.potential.Potential1.class);
        }
        else {
            potentials_from_registry = Registry.queryWhoExtends(etomica.potential.Potential2.class);
        }

        Class hardSoftClass = null;
        if (getPotenialIsSoft()) {
            hardSoftClass = PotentialSoft.class;
        }
        else if (getPotentialIsHard()) {
            hardSoftClass = PotentialHard.class;
        }
        else {
            hardSoftClass = Potential.class;
        }

        Iterator iterator = potentials_from_registry.iterator(); 
        while(iterator.hasNext())
        {
            Class potentialClass = (Class) iterator.next();

            if (P2SoftSphericalTruncated.class == potentialClass) {
                // we handle truncation ourselves
                continue;
            }
            // pick potentials that implement hard/soft interface
            if (hardSoftClass.isAssignableFrom(potentialClass)) {
                if (hardSoftClass == Potential.class &&
                    (PotentialHard.class.isAssignableFrom(potentialClass) ||
                     PotentialSoft.class.isAssignableFrom(potentialClass))) {
                    // if looking for "other" skip hard and soft
                    continue;
                }
                EtomicaInfo info = EtomicaInfo.getInfo(potentialClass);
                String str = ClassDiscovery.chopClassName(potentialClass.getName())+": "+info.getShortDescription();
                potentialCombo.add(str);
                potentialsMap.put(str, potentialClass );
            }
        }
        potentialCombo.add(nBody+"-body Potential Group");
        potentialsMap.put(nBody+"-body Potential Group",etomica.potential.PotentialGroup.class);

    }

    /**
     * This method initializes the combo box for the Potential classes  
     */
    private void createPotentialBodyCombo() {
        GridData gridData = new org.eclipse.swt.layout.GridData();
        potentialBodyCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);           
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        potentialBodyCombo.setLayoutData(gridData);
    }

    /**
     * This method initializes the combo box for the Potential classes  
     */
    private void createPotentialHardSoftCombo() {
        GridData gridData = new org.eclipse.swt.layout.GridData();
        potentialHardSoftCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);           
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        potentialHardSoftCombo.setLayoutData(gridData);
    }

	/**
	 * This method initializes the combo box for the Potential classes	
	 */
	private void createPotentialTypeCombo() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		potentialCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		potentialCombo.setLayoutData(gridData);
	}

    /**
	 * This method initializes the combo box for the first Species
	 */    
	private void createSpeciesCombo1() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		speciesCombo = new Combo(this, SWT.READ_ONLY);		   
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		speciesCombo.setLayoutData(gridData);
	}

    /**
	 * This method initializes the combo box for the second Species
	 */    
	private void createSpeciesCombo2() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		speciesCombo2 = new Combo(this, SWT.READ_ONLY);		   
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		speciesCombo2.setLayoutData(gridData);
	}

	private void initialize() {
        GridData simpleGridData = new org.eclipse.swt.layout.GridData();
        simpleGridData.horizontalSpan = 1;

		GridData wideGridData = new org.eclipse.swt.layout.GridData();
        wideGridData.horizontalSpan = 3;

        GridData tallGridData = new org.eclipse.swt.layout.GridData();
        simpleGridData.horizontalSpan = 1;
        tallGridData.verticalSpan = 2;

        GridData textGridData = new org.eclipse.swt.layout.GridData();
        textGridData.horizontalSpan = 1;
        textGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        textGridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        textGridData.grabExcessHorizontalSpace = true;

        GridData doubleTextGridData = new org.eclipse.swt.layout.GridData();
        doubleTextGridData.horizontalSpan = 2;
        doubleTextGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        doubleTextGridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        doubleTextGridData.grabExcessHorizontalSpace = true;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = false;
        this.setLayout(gridLayout);

        Label label = new Label(this, SWT.NONE);
        label.setText("Potential name:");
        label.setLayoutData(simpleGridData);
		potentialName = new Text(this, SWT.BORDER);
        potentialName.setLayoutData(doubleTextGridData);
        
        label = new Label(this, SWT.NONE);
        label.setText("Number of bodies:");
        label.setLayoutData(simpleGridData);
        label = new Label(this, SWT.NONE);
        label.setText("Hard or soft:");
        label.setLayoutData(simpleGridData);
        
        truncatedCheckbox = new Button(this, SWT.CHECK);
        truncatedCheckbox.setText("Truncated");
        truncatedCheckbox.setLayoutData(tallGridData);
        
        createPotentialBodyCombo();
        createPotentialHardSoftCombo();

        label = new Label(this, SWT.NONE);
        label.setText("Species");
        label.setLayoutData(wideGridData);
		createSpeciesCombo1();
        createSpeciesCombo2();

        label = new Label(this, SWT.NONE);
        label.setText("Select a potential type");
        label.setLayoutData(wideGridData);
        createPotentialTypeCombo();
        
        potentialDescription = new Label(this, SWT.WRAP);
        wideGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        potentialDescription.setLayoutData(wideGridData);
        potentialDescription.setText("");

        setSize(new org.eclipse.swt.graphics.Point(364,462));
	}
}
