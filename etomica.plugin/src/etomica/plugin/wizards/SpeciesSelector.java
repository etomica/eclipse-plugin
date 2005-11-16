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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import etomica.EtomicaInfo;
import etomica.plugin.Registry;
import etomica.simulation.Simulation;
import etomica.species.Species;
/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpeciesSelector extends Composite {

	public Combo species_list = null;
	private Label label1 = null;
	public Text species_name = null;
	private Label label3 = null;
	private HashMap speciesTypeMap = new HashMap();
	
	private Label label5 = null;
	public Species createSpecies(Simulation sim) {
		int item = species_list.getSelectionIndex();
		Class speciesClass = (Class) speciesTypeMap.get( species_list.getItem( item ) );
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
	
	
	/**
	 * @param parent
	 * @param style
	 */
	public SpeciesSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
//		 Add all spaces from registry
		Collection species_from_registry = Registry.queryWhoExtends( etomica.species.Species.class );
		Iterator item = species_from_registry.iterator(); 
		while( item.hasNext() )
		{
			Class speciesClass = (Class) item.next();
			EtomicaInfo info = EtomicaInfo.getInfo( speciesClass );
			species_list.add( info.getShortDescription() );
			speciesTypeMap.put( info.getShortDescription(), speciesClass );
		}
		int default_selection = species_list.indexOf( "etomica.species.SpeciesSpheresMono");
		species_list.select( default_selection );

	}

	/**
	 * This method initializes combo	
	 *
	 */    
	private void createCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		species_list = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		species_list.setLayoutData(gridData9);
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
		label1 = new Label(this, SWT.NONE);
		species_name = new Text(this, SWT.BORDER);
		label5 = new Label(this, SWT.NONE);
        label3 = new Label(this, SWT.NONE);
		createCombo();
		this.setLayout(gridLayout2);
		gridLayout2.numColumns = 3;
		gridLayout2.makeColumnsEqualWidth = false;
		label1.setText("Species name:");
		label1.setLayoutData(gridData5);
		gridData5.horizontalSpan = 1;
		gridData6.horizontalSpan = 1;
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData6.grabExcessHorizontalSpace = true;
		species_name.setLayoutData(gridData6);
		gridData8.horizontalSpan = 3;
		label3.setText("Species Type");
		label3.setLayoutData(gridData13);
		gridData13.horizontalSpan = 1;
		label5.setText("");
		setSize(new org.eclipse.swt.graphics.Point(364,162));
	}
}  //  @jve:decl-index=0:visual-constraint="38,24"
