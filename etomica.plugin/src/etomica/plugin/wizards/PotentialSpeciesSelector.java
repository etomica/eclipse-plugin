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
import etomica.simulation.Simulation;
import etomica.species.Species;
/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PotentialSpeciesSelector extends Composite {

	public Combo potentialCombo = null;
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
    
    public Class getPotentialClass() {
        int item = potentialCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        Class potentialClass = (Class) potentialsMap.get( potentialCombo.getItem( item ) );
        return potentialClass;
            
    }
    
    public Species[] getSpecies() {
        Species[] speciesArray = new Species[2];
        int item = speciesCombo.getSelectionIndex();
        if (item == -1) {
            return speciesArray;
        }
        speciesArray[0] = (Species) speciesMap.get( speciesCombo.getItem( item ) );
        item = speciesCombo2.getSelectionIndex();
        if (item == -1) {
            return speciesArray;
        }
        speciesArray[1] = (Species) speciesMap.get( speciesCombo2.getItem( item ) );
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
		Collection potentials_from_registry = Registry.queryWhoExtends(etomica.potential.Potential1.class);
        potentials_from_registry.addAll(Registry.queryWhoExtends( etomica.potential.Potential2.class));
        potentials_from_registry.add(etomica.potential.PotentialGroup.class);
		Iterator iterator = potentials_from_registry.iterator(); 
		while( iterator.hasNext() )
		{
			Class potentialClass = (Class) iterator.next();
			EtomicaInfo info = EtomicaInfo.getInfo( potentialClass );
			potentialCombo.add( info.getShortDescription() );
			potentialsMap.put( info.getShortDescription(), potentialClass );
		}
//		int default_selection = potentialList.indexOf( "something");
//		potentialList.select( default_selection );
        
		// Add all master potentials from registry
        Species[] speciesArray = simulation.speciesRoot.getSpecies();
        for (int i=0; i<speciesArray.length; i++) {
			Species species = speciesArray[i];
			speciesCombo.add(species.getName());
            speciesCombo2.add(species.getName());
			speciesMap.put(species.getName(), species);
		}

//      int default_selection = speciesList.indexOf( "something");
//      speciesList.select( default_selection );
	}

	/**
	 * This method initializes combo	
	 *
	 */    
	private void createCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		potentialCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		potentialCombo.setLayoutData(gridData9);
	}
	/**
	 * This method initializes combo1	
	 *
	 */    
	private void createCombo1() {
		GridData gridData11 = new org.eclipse.swt.layout.GridData();
		speciesCombo = new Combo(this, SWT.READ_ONLY);		   
		gridData11.horizontalSpan = 2;
		gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData11.grabExcessHorizontalSpace = true;
		speciesCombo.setLayoutData(gridData11);
	}
	/**
	 * This method initializes combo	
	 *
	 */    
	private void createCombo2() {
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
		label1 = new Label(this, SWT.NONE);
        label1.setText("Potential name:");
        gridData5.horizontalSpan = 1;
        label1.setLayoutData(gridData5);
		potentialName = new Text(this, SWT.BORDER);
        gridData6.horizontalSpan = 1;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        potentialName.setLayoutData(gridData6);
//		label5 = new Label(this, SWT.NONE);
		label2 = new Label(this, SWT.NONE);
        label2.setText("Select a potential type");
        gridData8.horizontalSpan = 2;
        label2.setLayoutData(gridData8);
		createCombo();
        label3 = new Label(this, SWT.NONE);
		createCombo1();
        createCombo2();
		label3.setText("Species");
        gridData13.horizontalSpan = 2;
		label3.setLayoutData(gridData13);
//		label5.setText("");
		setSize(new org.eclipse.swt.graphics.Point(364,162));
	}
}  //  @jve:decl-index=0:visual-constraint="38,24"
