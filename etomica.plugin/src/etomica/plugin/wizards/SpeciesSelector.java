/*
 * Created on May 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

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
import etomica.plugin.ClassDiscovery;
import etomica.plugin.Registry;
import etomica.simulation.Simulation;
import etomica.species.Species;

/**
 * Sets up GUI elements to select a new Species
 */
public class SpeciesSelector extends Composite {

	public Combo speciesCombo = null;
	private Label label1 = null;
	public Text speciesName = null;
	private Label label3 = null;
	private HashMap speciesTypeMap = new HashMap();
	private Label speciesDescription = null;

    public Species createSpecies(Simulation sim) {
		int item = speciesCombo.getSelectionIndex();
		Class speciesClass = (Class) speciesTypeMap.get( speciesCombo.getItem( item ) );
		if ( speciesClass==null ) {
            return null;
        }
        Species species = null;
		try {
			species = (Species)speciesClass.getDeclaredConstructor(new Class[]{Simulation.class}).newInstance(new Object[]{sim});
		} catch (InstantiationException e) {
			System.err.println( "Could not instantiate class: " + e.getMessage() );
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println( "Illegal access while creating class: " + e.getMessage() );
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
            System.err.println( "Constructor didn't exist while creating class: " + e.getMessage() );
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            System.err.println( "Exception creating class: " + e.getMessage() );
            e.printStackTrace();
        }
		return species;
	}
	
	public SpeciesSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
//		 Add all spaces from registry
		Collection species_from_registry = Registry.queryWhoExtends( etomica.species.Species.class );
		Iterator iterator = species_from_registry.iterator(); 
		while( iterator.hasNext() )
		{
			Class speciesClass = (Class)iterator.next();
			EtomicaInfo info = EtomicaInfo.getInfo(speciesClass);
            String str = ClassDiscovery.chopClassName(speciesClass.getName())+": "+info.getShortDescription();
			speciesCombo.add(str);
			speciesTypeMap.put(str, speciesClass);
		}

        speciesCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = speciesCombo.getSelectionIndex();
                if (item == -1) {
                    return;
                }
                Class speciesClass = (Class) speciesTypeMap.get( speciesCombo.getItem( item ) );
                String str = speciesClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo( speciesClass );
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ": \n"+longDesc;
                }
                speciesDescription.setText(str);
                SpeciesSelector.this.layout();
            }
        });

	}

	/**
	 * This method initializes combo	
	 *
	 */    
	private void createCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		speciesCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 1;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		speciesCombo.setLayoutData(gridData9);
	}

    public static void main(String[] args) {
		/* Before this is run, be sure to set up the following in the launch configuration 
		 * (Arguments->VM Arguments) for the correct SWT library path. 
		 * The following is a windows example:
		 * -Djava.library.path="installation_directory\plugins\org.eclipse.swt.win32_3.0.0\os\win32\x86"
		 */
		org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();		
		org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell(display);
		shell.setLayout(new org.eclipse.swt.layout.FillLayout());
		shell.setSize(new org.eclipse.swt.graphics.Point(300,200));
		SpeciesSelector thisClass = new SpeciesSelector(shell, org.eclipse.swt.SWT.NONE);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
		display.dispose();		
	}

	private void initialize() {
		GridData gridData13 = new org.eclipse.swt.layout.GridData();
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 2;
        gridLayout2.makeColumnsEqualWidth = false;
        this.setLayout(gridLayout2);

        label1 = new Label(this, SWT.NONE);
        label1.setText("Species name:");
        gridData5.horizontalSpan = 1;
        label1.setLayoutData(gridData5);
		speciesName = new Text(this, SWT.BORDER);
        gridData6.horizontalSpan = 1;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        speciesName.setLayoutData(gridData6);

        label3 = new Label(this, SWT.NONE);
        label3.setText("Species Type");
        gridData13.horizontalSpan = 1;
        label3.setLayoutData(gridData13);
		createCombo();

        speciesDescription = new Label(this, SWT.WRAP);
        gridData8.horizontalSpan = 2;
        gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        speciesDescription.setLayoutData(gridData8);
	}
}
