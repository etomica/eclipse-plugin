/*
 * Created on May 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.wizards;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

import etomica.EtomicaInfo;
import etomica.Potential;
import etomica.PotentialMaster;
import etomica.Simulation;
import etomica.Space;
import etomica.plugin.Registry;
/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpaceDimensionSelector extends Composite {

	private Label label = null;
	public Combo space_list = null;
	public Text container_name = null;
	public Button browse_button = null;
	private Label label1 = null;
	public Text file_name = null;
	private Label label2 = null;
	public Combo master_potential_list = null;
	public Combo sim_types = null;
	private Label label3 = null;
	private HashMap simtypemap = new HashMap();
	private HashMap spacemap = new HashMap();
	private HashMap potmap = new HashMap();
	
	public Simulation createSimulation()
	{
		int item = sim_types.getSelectionIndex();
		Class simclass = (Class) simtypemap.get( sim_types.getItem( item ) );
		if ( simclass!=null )
		{
			try {
				return (Simulation) simclass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new Simulation();
		}
		// It's a stock one
		Space space = (Space) spacemap.get( space_list.getText() );
		PotentialMaster pot = (PotentialMaster) potmap.get( master_potential_list.getText() );
		return new Simulation( space, pot );
	}
	
	protected EtomicaInfo getInfo( Class myclass )
	{
		etomica.EtomicaInfo info = null;
		if ( !etomica.EtomicaElement.class.isAssignableFrom( myclass ))
			return new EtomicaInfo( myclass.getName() );
		
        try {
            java.lang.reflect.Method method = myclass.getMethod("getEtomicaInfo",null);
            if ( method==null )
            	return new EtomicaInfo( myclass.getName() );
            info = (etomica.EtomicaInfo)method.invoke(myclass, null);
        }
        catch ( Exception se ) {
        	System.out.println("Exception retrieving info for class " + myclass.getName()+ ": " + se.getLocalizedMessage() );
        }
/*        catch(java.lang.SecurityException se){ System.out.println("Exception retrieving info for class " + myclass.getName()+ ": " + se.getLocalizedMessage() );}
        catch(java.lang.IllegalAccessException iae){System.out.println("Exception retrieving info for class " + myclass.getName()+ ": "+ se.getLocalizedMessage() );}
        catch(java.lang.IllegalArgumentException ia){System.out.println("Exception retrieving info for class " + myclass.getName()+ ": "+ se.getLocalizedMessage() );}
        catch(java.lang.reflect.InvocationTargetException ite){System.out.println("Exception retrieving info for class " + myclass.getName()+ ": "+ se.getLocalizedMessage() );}
        catch(java.lang.NoSuchMethodException nsme) {
        }
*/        return info;
    }
	
	/**
	 * @param parent
	 * @param style
	 */
	public SpaceDimensionSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
//		 Add all spaces from registry
		Collection spaces_from_registry = Registry.queryWhoExtends( etomica.Space.class );
		Iterator item = spaces_from_registry.iterator(); 
		while( item.hasNext() )
		{
			Class spaceclass = (Class) item.next();
			EtomicaInfo info = getInfo( spaceclass );
			space_list.add( info.getDescription() );
			spacemap.put( info.getDescription(), spaceclass );
		}
		int default_selection = space_list.indexOf( "etomica.spaces.Space2D");
		space_list.select( default_selection );

		// Add all master potentials from registry
		Collection pmaster_from_registry = Registry.queryWhoExtends( etomica.PotentialMaster.class );
		item = pmaster_from_registry.iterator(); 
		while( item.hasNext() )
		{
			Class pmasterclass = (Class) item.next();
			EtomicaInfo info = getInfo( pmasterclass );
			master_potential_list.add( info.getDescription() );
			potmap.put( info.getDescription(), pmasterclass );
		}

		// Add all types of stock simulations from registry
		Collection stocksims = Registry.queryWhoExtends( Simulation.class );
		sim_types.add( "Custom..." );
		item = stocksims.iterator();
		while ( item.hasNext() )
		{
			Class sim = (Class) item.next();
			EtomicaInfo info = getInfo( sim );
			sim_types.add( info.getDescription() );
			simtypemap.put( info.getDescription(), sim );
		}
		default_selection = sim_types.indexOf( "Custom...");
		sim_types.select( default_selection );
	}

	/**
	 * This method initializes combo	
	 *
	 */    
	private void createCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		space_list = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		space_list.setLayoutData(gridData9);
		
		
	}
	/**
	 * This method initializes combo1	
	 *
	 */    
	private void createCombo1() {
		GridData gridData11 = new org.eclipse.swt.layout.GridData();
		master_potential_list = new Combo(this, SWT.READ_ONLY);		   
		gridData11.horizontalSpan = 2;
		gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData11.grabExcessHorizontalSpace = true;
		master_potential_list.setLayoutData(gridData11);
	}
	/**
	 * This method initializes combo	
	 *
	 */    
	private void createCombo2() {
		GridData gridData12 = new org.eclipse.swt.layout.GridData();
		sim_types = new Combo(this, SWT.READ_ONLY);		   
		gridData12.horizontalSpan = 2;
		gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData12.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		sim_types.setLayoutData(gridData12);
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
		SpaceDimensionSelector thisClass = new SpaceDimensionSelector(shell, org.eclipse.swt.SWT.NONE);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
		display.dispose();		
	}

	private void initialize() {
		GridData gridData13 = new org.eclipse.swt.layout.GridData();
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData7 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
		GridData gridData4 = new org.eclipse.swt.layout.GridData();
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
		label = new Label(this, SWT.NONE);
		container_name = new Text(this, SWT.BORDER);
		browse_button = new Button(this, SWT.NONE);
		label1 = new Label(this, SWT.NONE);
		file_name = new Text(this, SWT.BORDER);
		label3 = new Label(this, SWT.NONE);
		createCombo2();
		label2 = new Label(this, SWT.NONE);
		createCombo();
		createCombo1();
		this.setLayout(gridLayout2);
		gridLayout2.numColumns = 3;
		gridLayout2.makeColumnsEqualWidth = false;
		label.setText("Select a Container");
		label.setLayoutData(gridData3);
		gridData3.horizontalSpan = 3;
		gridData4.horizontalSpan = 2;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		container_name.setLayoutData(gridData4);
		browse_button.setText("Browse...");
		browse_button.setLayoutData(gridData7);
		label1.setText("File name");
		label1.setLayoutData(gridData5);
		gridData5.horizontalSpan = 3;
		gridData6.horizontalSpan = 2;
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData6.grabExcessHorizontalSpace = true;
		file_name.setLayoutData(gridData6);
		gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData7.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		label2.setText("...or select a space and master potential");
		label2.setLayoutData(gridData8);
		gridData8.horizontalSpan = 3;
		label3.setText("Simulation Type");
		label3.setLayoutData(gridData13);
		gridData13.horizontalSpan = 3;
		setSize(new org.eclipse.swt.graphics.Point(380,308));
	}
}  //  @jve:decl-index=0:visual-constraint="38,24"
