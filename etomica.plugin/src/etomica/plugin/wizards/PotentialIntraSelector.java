package etomica.plugin.wizards;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

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
import etomica.atom.iterator.ApiBuilder;
import etomica.atom.iterator.AtomIterator;
import etomica.atom.iterator.AtomPairIterator;
import etomica.atom.iterator.AtomsetIteratorBasisDependent;
import etomica.plugin.ClassDiscovery;
import etomica.plugin.Registry;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialHard;
import etomica.potential.PotentialSoft;
import etomica.simulation.Simulation;

/**
 * Selector for Potential class and the Species it will be act on.
 */
public class PotentialIntraSelector extends Composite {

	public Combo potentialCombo;
    public Combo potentialBodyCombo;
    public Combo potentialHardSoftCombo;
	public Text potentialName;
	public Combo iteratorCombo;
    private Label potentialDescription;
    private Label iteratorDescription;
	private HashMap potentialsMap = new HashMap();
	private HashMap iteratorMap = new HashMap();
    private LinkedList builderMethodsList = new LinkedList();
    private HashMap builderMethodsMap = new HashMap();
    private Simulation simulation;
	
	public Potential createPotential()
	{
		Class potentialClass = getPotentialClass();
		if ( potentialClass!=null )
		{
            if (potentialClass == etomica.potential.PotentialGroup.class) {
                return simulation.potentialMaster.makePotentialGroup(getPotenialNumBody());
            }
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
    
    public AtomsetIteratorBasisDependent createIterator()
    {
        Object iteratorSelection = getIteratorSelection();
        if (iteratorSelection == null) {
            return null;
        }
        if (iteratorSelection instanceof Class) {
            try {
                return (AtomsetIteratorBasisDependent)((Class)iteratorSelection).newInstance();
            } catch (InstantiationException e) {
                System.err.println( "Could not instantiate Iterator: " + e.getMessage() );
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println( "Illegal access while creating Iterator: " + e.getMessage() );
                e.printStackTrace();
            }
        }
        else {
            // must be an ApiBuilder method
            try {
                return (AtomsetIteratorBasisDependent)((Method)iteratorSelection).invoke(null,null);
            }
            catch (InvocationTargetException e) {
                System.err.println("Target Invocation exception invoking "+((Method)iteratorSelection).getName()+": " + e.getMessage() );
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                System.err.println( "Illegal access while creating Iterator: " + e.getMessage() );
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
        Class potentialClass = (Class)potentialsMap.get(potentialCombo.getItem(item));
        return potentialClass;
            
    }
    
    public Object getIteratorSelection() {
        int item = iteratorCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return iteratorMap.get(iteratorCombo.getItem(item));
            
    }
    
	public PotentialIntraSelector(Simulation sim, Composite parent, int style) {
		super(parent, style);
		
        simulation = sim;
        
		initialize();
        
        Method[] apiBuilderMethods = ApiBuilder.class.getMethods();
        for (int i=0; i<apiBuilderMethods.length; i++) {
            if ((apiBuilderMethods[i].getModifiers() & Modifier.STATIC) == 0) {
                continue;
            }
            Class type = apiBuilderMethods[i].getReturnType();
            String methodName = ClassDiscovery.chopClassName(apiBuilderMethods[i].getName());
            if (AtomsetIteratorBasisDependent.class.isAssignableFrom(type)
                    && methodName.startsWith("make")) {
                builderMethodsList.add(apiBuilderMethods[i]);
            }
        }
		
        potentialBodyCombo.add("1");
        potentialBodyCombo.add("2");
        potentialBodyCombo.select(1); // 2-body
        
        potentialHardSoftCombo.add("Hard");
        potentialHardSoftCombo.add("Soft");
        potentialHardSoftCombo.select(0); // hard

        rebuildPotentialList();
        rebuildIteratorList();
        
        potentialCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = potentialCombo.getSelectionIndex();
                if (item == -1) {
                    return;
                }
                Class potentialClass = (Class) potentialsMap.get( potentialCombo.getItem( item ) );
                String str = potentialClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo( potentialClass );
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ": \n"+longDesc;
                }
                potentialDescription.setText(str);
                PotentialIntraSelector.this.layout(false);
            }
        });
        
        iteratorCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = iteratorCombo.getSelectionIndex();
                if (item == -1) {
                    return;
                }
                Class iteratorClass = (Class) iteratorMap.get( iteratorCombo.getItem( item ) );
                String str = iteratorClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo( iteratorClass );
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ": \n"+longDesc;
                }
                iteratorDescription.setText(str);
                PotentialIntraSelector.this.layout(false);
            }
        });

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
        else {
            hardSoftClass = PotentialHard.class;
        }
        Iterator iterator = potentials_from_registry.iterator(); 
        while( iterator.hasNext() )
        {
            Class potentialClass = (Class) iterator.next();
            // pick potentials that implement hard/soft interface
            if (hardSoftClass.isAssignableFrom(potentialClass)) {
                EtomicaInfo info = EtomicaInfo.getInfo(potentialClass);
                String str = ClassDiscovery.chopClassName(potentialClass.getName())+": "+info.getShortDescription();
                potentialCombo.add(str);
                potentialsMap.put(str, potentialClass );
            }
        }
        potentialCombo.add(nBody+"-body Potential Group");
        potentialsMap.put(nBody+"-body Potential Group",etomica.potential.PotentialGroup.class);

    }

    public void rebuildIteratorList() {
        iteratorMap.clear();
        iteratorCombo.removeAll();
        Collection iteratorsFromRegistry;
        int nBody = getPotenialNumBody();
        if (nBody == -1) {
            return;
        }
        iteratorsFromRegistry = Registry.queryWhoExtends(etomica.atom.iterator.AtomsetIterator.class);

        Iterator iterator = iteratorsFromRegistry.iterator(); 
        while( iterator.hasNext() )
        {
            Class iteratorClass = (Class) iterator.next();
            if (!(AtomsetIteratorBasisDependent.class.isAssignableFrom(iteratorClass))
                    || (nBody == 2 && AtomIterator.class.isAssignableFrom(iteratorClass))
                    || (nBody == 1 && AtomPairIterator.class.isAssignableFrom(iteratorClass))) {
                continue;
            }
            EtomicaInfo info = EtomicaInfo.getInfo(iteratorClass);
            String str = ClassDiscovery.chopClassName(iteratorClass.getName())+": "+info.getShortDescription();
            iteratorCombo.add(str);
            iteratorMap.put(str, iteratorClass );
        }
        
        if (nBody == 2) {
            iterator = builderMethodsList.iterator();
            while (iterator.hasNext()) {
                Method builderMethod = (Method)iterator.next();
                Class iteratorClass = builderMethod.getReturnType();
                String methodName = ClassDiscovery.chopClassName(builderMethod.getName()).substring(4);
                String str = ClassDiscovery.chopClassName(iteratorClass.getName())+"("+methodName+"): "; //+ApiBuilder.getDescription(methodName);
                iteratorCombo.add(str);
                iteratorMap.put(str,builderMethod);
            }
        }
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
	private void createIteratorCombo() {
		GridData gridData11 = new org.eclipse.swt.layout.GridData();
		iteratorCombo = new Combo(this, SWT.READ_ONLY);		   
		gridData11.horizontalSpan = 2;
		gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData11.grabExcessHorizontalSpace = true;
		iteratorCombo.setLayoutData(gridData11);
	}

	private void initialize() {
		GridData gridData13 = new org.eclipse.swt.layout.GridData();
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
        GridData gridData2 = new org.eclipse.swt.layout.GridData();
        GridData gridData1 = new org.eclipse.swt.layout.GridData();
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
        label.setText("Hard or soft Potential:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
        createPotentialBodyCombo();
        createPotentialHardSoftCombo();
        
        label = new Label(this, SWT.NONE);
        label.setText("Iterator type");
        gridData13.horizontalSpan = 2;
        label.setLayoutData(gridData13);
		createIteratorCombo();

        iteratorDescription = new Label(this, SWT.WRAP);
        gridData2.horizontalSpan = 2;
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        iteratorDescription.setLayoutData(gridData2);
        iteratorDescription.setText("");

        label = new Label(this, SWT.NONE);
        label.setText("Select a potential type");
        gridData8.horizontalSpan = 2;
        label.setLayoutData(gridData8);
        createPotentialTypeCombo();
        
        potentialDescription = new Label(this, SWT.WRAP);
        gridData1.horizontalSpan = 2;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        potentialDescription.setLayoutData(gridData1);
        potentialDescription.setText("");

        setSize(new org.eclipse.swt.graphics.Point(364,462));
	}
}
