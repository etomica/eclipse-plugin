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
import etomica.plugin.ClassDiscovery;
import etomica.plugin.Registry;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.space.Space;
import etomica.util.Arrays;

/**
 * Selector for a class of new object to create.  The user is presented with 
 * all Classes that extend a base Class  and allowed to choose one.  The user
 * may optionally be presented with a choice to select a category (sub Class of
 * the base class) or from a list of objects that already exist in the object.
 */
public class SimpleClassSelector extends Composite {

	public Combo classCombo;
    public Combo categoryCombo;
	public Text objectName;
    private Label classDescription;
    private Label categoryLabel;
	private HashMap classMap = new HashMap();
    private LinkedHashMap categoryMap = new LinkedHashMap();
    private Object[][] categories = new Object[0][2];
    private Class baseClass;
    
	private Class[] excludedClasses = new Class[0];
    private Object[] extraChoices = new Class[0];
    
	public Object createObject(Simulation sim) {
        int item = classCombo.getSelectionIndex();
        Object selection = getSelection();
        if (selection == null) {
            return null;
        }
        
        if (!(selection instanceof Class)) {
            // an existing object was selected
            return selection;
        }

        Class objectClass = (Class)classMap.get(classCombo.getItem(item));
        boolean noConstructor = false;
        try {
            return objectClass.newInstance();
        } catch (InstantiationException e) {
            noConstructor = true;
            // no default constructor.  Try passing a Space
        } catch (IllegalAccessException e) {
            System.err.println( "Illegal access while creating class: " + e.getMessage() );
            e.printStackTrace();
        }
        if (!noConstructor || sim == null) {
            return null;
        }
        Constructor[] constructors = objectClass.getConstructors();
        if (constructors.length == 0) {
            return null;
        }
        for (int i=0; i<constructors.length; i++) {
            boolean found = true;
            Class[] parameterClasses = constructors[i].getParameterTypes();
            Object[] parameters = new Object[parameterClasses.length];
            for (int j=0; j<parameters.length; j++) {
                if (parameterClasses[i] == Simulation.class) {
                    parameters[j] = sim;
                }
                else if (parameterClasses[i] == PotentialMaster.class) {
                    parameters[j] = sim.potentialMaster;
                }
                else if (parameterClasses[i] == Space.class) {
                    parameters[j] = sim.space;
                }
                else {
                    found = false;
                    break;
                }
            }
            if (!found) {
                continue;
            }
            try {
                return constructors[i].newInstance(parameters);
            }
            catch (InstantiationException e) {
                System.err.println( "Could not instantiate class: " + e.getMessage() );
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                System.err.println( "Illegal access while creating class: " + e.getMessage() );
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                System.err.println( "Exception creating class: " + e.getMessage() );
                e.printStackTrace();
            }
            return null;
        }
        return null;
	}
    
    public Object getSelection() {
        int item = classCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return classMap.get(classCombo.getItem(item));
    }        
    
    public Class getCategory() {
        int item = categoryCombo.getSelectionIndex();
        if (item == -1) {
            return null;
        }
        return (Class)categoryMap.get(categoryCombo.getItem(item));
    }
    
    public boolean hasCategories() {
        return categories.length > 0;
    }
    
    public void setBaseClass(Class newBaseClass) {
        baseClass = newBaseClass;
        rebuildClassList();
    }
    
	/**
	 * @param parent
	 * @param style
	 */
	public SimpleClassSelector(Composite parent, int style, String name) {
		super(parent, style);
		
		initialize(name);
		
		setExcludedClasses(new Class[0]);
        
        classCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                int item = classCombo.getSelectionIndex();
                if (item == -1) {
                    return;
                }
                Object obj = getSelection();
                Class selectedClass;
                if (obj instanceof Class) {
                    selectedClass = (Class)obj;
                }
                else {
                    selectedClass = obj.getClass();
                }
                String str = selectedClass.getName();
                EtomicaInfo info = EtomicaInfo.getInfo(selectedClass);
                String longDesc = info.getDescription();
                if (!str.equals(longDesc)) {
                    str += ": \n"+longDesc;
                }
                classDescription.setText(str);
                SimpleClassSelector.this.layout();
            }
        });
	}
    
    public void setExtraObjects(Object[] extraObjects) {
        if (extraObjects == null) {
            extraChoices = new Object[0];
        }
        else {
            extraChoices = extraObjects;
        }
        setExcludedClasses(excludedClasses);
    }
    
    public void addCategory(String description, Class superClass) {
        if (superClass == Object.class) {
            throw new IllegalArgumentException("You can't really be serious!  superclass cannot be Object.class");
        }
        categories = (Object[][])Arrays.addObject(categories, new Object[]{description,superClass});
        setExcludedClasses(excludedClasses);
    }
    
    public boolean removeCategory(String description) {
        for (int i=0; i<categories.length; i++) {
            Object[] pair = categories[i];
            if (((String)pair[0]).equals(description)) {
                categories = (Object[][])Arrays.removeObject(categories, pair);
                setExcludedClasses(excludedClasses);
                return true;
            }
        }
        return false;
    }
    
    public void setExcludedClasses(Class[] classes) {
        if (classes == null) {
            excludedClasses = new Class[0];
        }
        else {
            excludedClasses = classes;
        }

        boolean shouldBeVisible = (extraChoices.length + categories.length > 0);
        if (!shouldBeVisible && (categoryLabel.isVisible() || !categoryLabel.getParent().isVisible())) {
            categoryLabel.setVisible(false);
            categoryCombo.setVisible(false);
        }
        else if (shouldBeVisible && !categoryLabel.isVisible()) {
            categoryLabel.setVisible(true);
            categoryCombo.setVisible(true);
        }
        
        categoryMap.clear();
        categoryCombo.removeAll();
        if (extraChoices.length > 0) {
            categoryMap.put("Existing Objects",Object.class);
            categoryCombo.add("Existing Objects");
        }
        // if one of the Action types is a subclass of an excluded
        // class, don't show it as an option
        for (int i=0; i<categories.length; i++) {
            boolean excluded = false;
            for (int j=0; j<excludedClasses.length; j++) {
                if (excludedClasses[j].isAssignableFrom((Class)categories[i][1])) {
                    excluded = true;
                    break;
                }
            }
            if (!excluded) {
                categoryMap.put(categories[i][0],categories[i][1]);
                categoryCombo.add((String)categories[i][0]);
            }
        }
        
        if (extraChoices.length > 0) {
            categoryCombo.select(0);
        }
        rebuildClassList();
    }

    /**
     * Builds the list of potentials in the combo box based on the selections
     * in the numBody and Hard/Soft combo boxes.
     */
    public void rebuildClassList() {
        classMap.clear();
        classCombo.removeAll();
        if (baseClass == null) {
            return;
        }
        
        Class objectCategory = getCategory();
        if (objectCategory == Object.class) {
            // objectCategory = null is the signal that the user has
            // selected the Existing Objects
            for (int i=0; i<extraChoices.length; i++) {
                Class extraClass = extraChoices[i].getClass();
                EtomicaInfo info = EtomicaInfo.getInfo(extraClass);
                String str = extraChoices[i].toString()+": "+info.getShortDescription();
                classCombo.add(str);
                classMap.put(str, extraChoices[i]);
            }
            return;
        }
        
        Collection classesFromRegistry = Registry.queryWhoExtends(baseClass);

        Iterator iterator = classesFromRegistry.iterator(); 
        while(iterator.hasNext())
        {
            Class objectClass = (Class)iterator.next();
            boolean excluded = false;
            for (int j=0; j<excludedClasses.length; j++) {
                if (excludedClasses[j].isAssignableFrom(objectClass)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            }
            if (objectCategory == baseClass) {
                // if we're looking for generic Objects, exclude the others
                Iterator typeIterator = categoryMap.keySet().iterator();
                boolean skipMe = false;
                while (typeIterator.hasNext()) {
                    Object obj = typeIterator.next();
                    Class otherCategory = (Class)categoryMap.get(obj);
                    if (otherCategory == baseClass || otherCategory == null) {
                        continue;
                    }
                    if (otherCategory.isAssignableFrom(objectClass)) {
                        skipMe = true;
                        break;
                    }
                }
                if (skipMe) {
                    continue;
                }
            }
            else if (objectCategory != null && !objectCategory.isAssignableFrom(objectClass)) {
                // user wants a specific type of Class.
                continue;
            }
            EtomicaInfo info = EtomicaInfo.getInfo(objectClass);
            String str = ClassDiscovery.chopClassName(objectClass.getName())+": "+info.getShortDescription();
            classCombo.add(str);
            classMap.put(str, objectClass );
        }

    }

    /**
     * This method initializes the combo box for the Potential classes  
     */
    private void createCategoryCombo() {
        GridData gridData = new org.eclipse.swt.layout.GridData();
        categoryCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);           
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        categoryCombo.setLayoutData(gridData);
    }

	/**
	 * This method initializes the combo box for the Potential classes	
	 */
	private void createClassCombo() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		classCombo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);		   
		gridData9.horizontalSpan = 2;
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData9.grabExcessHorizontalSpace = true;
		classCombo.setLayoutData(gridData9);
	}

	private void initialize(String name) {
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
        GridData gridData1 = new org.eclipse.swt.layout.GridData();
		GridLayout gridLayout2 = new GridLayout();
        this.setLayout(gridLayout2);
        gridLayout2.numColumns = 2;
        gridLayout2.makeColumnsEqualWidth = false;

        Label label = new Label(this, SWT.NONE);
        label.setText("Name:");
        gridData5.horizontalSpan = 1;
        label.setLayoutData(gridData5);
		objectName = new Text(this, SWT.BORDER);
        gridData6.horizontalSpan = 1;
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
        gridData6.grabExcessHorizontalSpace = true;
        objectName.setLayoutData(gridData6);
        
        categoryLabel = new Label(this, SWT.NONE);
        categoryLabel.setText(name+" category:");
        gridData5.horizontalSpan = 1;
        categoryLabel.setLayoutData(gridData5);
        createCategoryCombo();
        
        label = new Label(this, SWT.NONE);
        label.setText("Select a "+name);
        gridData8.horizontalSpan = 2;
        label.setLayoutData(gridData8);
        createClassCombo();
        
        classDescription = new Label(this, SWT.WRAP);
        gridData1.horizontalSpan = 2;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        classDescription.setLayoutData(gridData1);
        classDescription.setText("");

//        setSize(new org.eclipse.swt.graphics.Point(364,462));
	}
}
