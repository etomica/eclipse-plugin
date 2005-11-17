/*
 * Created on May 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.wizards;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import etomica.EtomicaInfo;
import etomica.plugin.Registry;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialHard;
import etomica.potential.PotentialSoft;
import etomica.simulation.Simulation;
import etomica.species.Species;
/**
 * Selector for Potential class and the Species it will be act on.
 */
public class PotentialSpeciesSelector extends Composite {

	public Combo potentialCombo = null;
    public Combo potentialBodyCombo = null;
    public Combo potentialHardSoftCombo = null;
	private Label label1 = null;
	public Text potentialName = null;
	private Label label2 = null;
	public Combo speciesCombo = null;
    public Combo speciesCombo2 = null;
	private Label label3 = null;
	private HashMap potentialsMap = new HashMap();
	private HashMap speciesMap = new HashMap();
    private Simulation simulation;
	
	public Potential createPotential()
	{
		Class potentialClass = getPotentialClass();
		if ( potentialClass!=null )
		{
			try {
                if (potentialClass == etomica.potential.PotentialGroup.class) {
                    return new PotentialGroup(getPotenialNumBody(),simulation.space);
                }
                Constructor constructor = potentialClass.getDeclaredConstructor(new Class[]{Simulation.class});
                if (constructor != null) {
                    return (Potential)constructor.newInstance(new Object[]{simulation});
                }
			} catch (InstantiationException e) {
				System.err.println( "Could not instantiate Potential: " + e.getMessage() );
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println( "Illegal access while creating Potential: " + e.getMessage() );
				e.printStackTrace();
			}
            catch (NoSuchMethodException e) {
                System.err.println( "Constructor didn't exist while creating Potential: " + e.getMessage() );
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                System.err.println( "Exception creating Potential: " + e.getMessage() );
                e.printStackTrace();
            }
		}
        return null;
	}
    
    public int getPotenialNumBody() {
        String str = potentialBodyCombo.getText();
        if (str.length() == 0) {
            return -1;
        }
        return Integer.parseInt(str);
    }
    
    public boolean getPotenialIsSoft() {
        String str = potentialHardSoftCombo.getText();
        return str.equals("Soft");
    }
    
    public Class getPotentialClass() {
        int item = potentialCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        Class potentialClass = (Class) potentialsMap.get( potentialCombo.getItem( item ) );
        return potentialClass;
            
    }
    
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
	
	/**
	 * @param parent
	 * @param style
	 */
	public PotentialSpeciesSelector(Simulation sim, Composite parent, int style) {
		super(parent, style);
		
        simulation = sim;
        
		initialize();
		
//		 Add all spaces from registry
        
        potentialBodyCombo.add("1");
        potentialBodyCombo.add("2");
        potentialBodyCombo.select(1); // 2-body
        
        potentialHardSoftCombo.add("Hard");
        potentialHardSoftCombo.add("Soft");
        potentialHardSoftCombo.select(0); // hard

        rebuildPotentialList();
        
        //add the Species from the simulation
        Species[] speciesArray = simulation.speciesRoot.getSpecies();
        for (int i=0; i<speciesArray.length; i++) {
			Species species = speciesArray[i];
			speciesCombo.add(species.getName());
            speciesCombo2.add(species.getName());
			speciesMap.put(species.getName(), species);
		}
	}
    
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
        else {
            hardSoftClass = PotentialHard.class;
        }
        Iterator iterator = potentials_from_registry.iterator(); 
        while( iterator.hasNext() )
        {
            Class potentialClass = (Class) iterator.next();
            // start out with hard potentials
            if (hardSoftClass.isAssignableFrom(potentialClass)) {
                EtomicaInfo info = EtomicaInfo.getInfo( potentialClass );
                potentialCombo.add( info.getShortDescription() );
                potentialsMap.put( info.getShortDescription(), potentialClass );
            }
        }
        potentialCombo.add(nBody+"-body Potential Group");
        potentialsMap.put(nBody+"-body Potential Group",etomica.potential.PotentialGroup.class);
//      int default_selection = potentialList.indexOf( "something");
//      potentialList.select( default_selection );
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
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		potentialCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		potentialCombo.setLayoutData(gridData9);
	}

    /**
	 * This method initializes the combo box for the first Species
	 */    
	private void createSpeciesCombo1() {
		GridData gridData11 = new org.eclipse.swt.layout.GridData();
		speciesCombo = new Combo(this, SWT.READ_ONLY);		   
		gridData11.horizontalSpan = 2;
		gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData11.grabExcessHorizontalSpace = true;
		speciesCombo.setLayoutData(gridData11);
	}

    /**
	 * This method initializes the combo box for the second Species
	 */    
	private void createSpeciesCombo2() {
		GridData gridData12 = new org.eclipse.swt.layout.GridData();
		speciesCombo2 = new Combo(this, SWT.READ_ONLY);		   
		gridData12.horizontalSpan = 2;
		gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData12.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		speciesCombo2.setLayoutData(gridData12);
	}

	private void initialize() {
		GridData gridData13 = new org.eclipse.swt.layout.GridData();
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
        this.setLayout(gridLayout2);
        gridLayout2.numColumns = 2;
        gridLayout2.makeColumnsEqualWidth = false;

        Label label = new Label(this, SWT.NONE);
        label.setText("Potential name:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
		potentialName = new Text(this, SWT.BORDER);
        gridData6.horizontalSpan = 1;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        potentialName.setLayoutData(gridData6);
        
        label = new Label(this, SWT.NONE);
        label.setText("Number of bodies:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
        label = new Label(this, SWT.NONE);
        label.setText("Type of Potential:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
        createPotentialBodyCombo();
        createPotentialHardSoftCombo();
        
        label = new Label(this, SWT.NONE);
        label.setText("Select a potential type");
        gridData8.horizontalSpan = 2;
        label.setLayoutData(gridData8);
		createPotentialTypeCombo();

        label = new Label(this, SWT.NONE);
        label.setText("Species");
        gridData13.horizontalSpan = 2;
        label.setLayoutData(gridData13);
		createSpeciesCombo1();
        createSpeciesCombo2();

        setSize(new org.eclipse.swt.graphics.Point(364,400));
	}
}  //  @jve:decl-index=0:visual-constraint="38,24"
