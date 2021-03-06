package etomica.plugin.wizards;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import etomica.EtomicaInfo;
import etomica.plugin.Registry;
import etomica.simulation.ISimulation;
/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpaceDimensionSelector extends Composite {

	public Label project_selection_label = null;
	public Combo space_list = null;
	public Text container_name = null;
	public Button browse_button = null;
	private Label label1 = null;
	public Text file_name = null;
	private Label label2 = null;
	public Combo sim_types = null;
	private Label label3 = null;
	private HashMap simtypemap = new HashMap();
	private HashMap spacemap = new HashMap();
	
	private Label label5 = null;
    
    public Class getSimulationClass() {
        int item = sim_types.getSelectionIndex();
        return (Class)simtypemap.get(sim_types.getItem(item));
    }
    
    public Class getSpaceClass() {
        return (Class)spacemap.get(space_list.getText());
    }
    
    /**
	 * @param parent
	 * @param style
	 */
	public SpaceDimensionSelector(Composite parent, int style) {
		super(parent, style);
		
		initialize();
		
//		 Add all spaces from registry
		Collection spaces_from_registry = Registry.queryWhoExtends( etomica.space.Space.class );
		Iterator item = spaces_from_registry.iterator(); 
		while( item.hasNext() )
		{
			Class spaceclass = (Class) item.next();
			EtomicaInfo info = EtomicaInfo.getInfo( spaceclass );
			space_list.add( info.getShortDescription() );
			spacemap.put( info.getShortDescription(), spaceclass );
		}
		int default_selection = space_list.indexOf( "etomica.spaces.Space2D");
		space_list.select( default_selection );

		// Add all types of stock simulations from registry
		Collection stocksims = Registry.queryWhoExtends( ISimulation.class );
		item = stocksims.iterator();
		while ( item.hasNext() )
		{
			Class sim = (Class) item.next();
			EtomicaInfo info = EtomicaInfo.getInfo( sim );
			sim_types.add( info.getShortDescription() );
			simtypemap.put( info.getShortDescription(), sim );
		}
		sim_types.add( "Custom..." );
		default_selection = 0;//sim_types.indexOf( "Custom...");
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
	 * This method initializes combo	
	 *
	 */    
	private void createCombo2() {
		GridData gridData12 = new org.eclipse.swt.layout.GridData();
		sim_types = new Combo(this, SWT.READ_ONLY);		   
		gridData12.horizontalSpan = 1;
		gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData12.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		sim_types.setLayoutData(gridData12);
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
		label1 = new Label(this, SWT.NONE);
		file_name = new Text(this, SWT.BORDER);
		label5 = new Label(this, SWT.NONE);
		project_selection_label = new Label(this, SWT.NONE);
		container_name = new Text(this, SWT.BORDER);
		browse_button = new Button(this, SWT.NONE);
		label3 = new Label(this, SWT.NONE);
		createCombo2();
		label2 = new Label(this, SWT.NONE);
		createCombo();
		this.setLayout(gridLayout2);
		gridLayout2.numColumns = 3;
		gridLayout2.makeColumnsEqualWidth = false;
		project_selection_label.setText("Select a project:");
		project_selection_label.setLayoutData(gridData3);
		gridData3.horizontalSpan = 1;
		gridData4.horizontalSpan = 1;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		container_name.setLayoutData(gridData4);
		browse_button.setText("Browse...");
		browse_button.setLayoutData(gridData7);
		label1.setText("Simulation name:");
		label1.setLayoutData(gridData5);
		gridData5.horizontalSpan = 1;
		gridData6.horizontalSpan = 1;
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
		gridData13.horizontalSpan = 1;
		label5.setText("");
		setSize(new org.eclipse.swt.graphics.Point(364,162));
	}
}  //  @jve:decl-index=0:visual-constraint="38,24"
