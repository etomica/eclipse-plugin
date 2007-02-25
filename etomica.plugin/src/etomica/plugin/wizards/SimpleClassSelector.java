package etomica.plugin.wizards;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

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
    protected Label classDescription;
    private Label categoryLabel;
	private HashMap classMap = new HashMap();
    private LinkedHashMap categoryMap = new LinkedHashMap();
    private Object[][] categories = new Object[0][2];
    private Class[] baseClass;
    
	private Class[] excludedClasses = new Class[0];
    private Object[][] extraChoices = new Object[0][2];
    private Class[] extraParameterClasses = new Class[0];
    private final Simulation sim;
    
    /**
     * @param parent
     * @param style
     */
    public SimpleClassSelector(Composite parent, int style, String name, Simulation sim) {
        super(parent, style);
        
        baseClass = new Class[0];
        
        initialize(name);
        
        setExcludedClasses(new Class[0]);
        
        categoryCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                objectName.setEnabled(getCategory() != Object.class);
                rebuildClassList();
            }
        });
        
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
        
        this.sim = sim;
    }
    
    public void setExtraParameterClasses(Class[] newExtraParameterClasses) {
        extraParameterClasses = newExtraParameterClasses;
        rebuildClassList();
    }
    
	public Object createObject(Object[] extraParameters) {
        if (extraParameterClasses.length < extraParameters.length) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Extra parameters passed that do not match expectations", null));
            return null;
        }
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

        Constructor[] constructors = objectClass.getConstructors();
        if (constructors.length == 0) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Selected class had no constructor", null));
            return null;
        }
        for (int i=0; i<constructors.length; i++) {
            boolean found = true;
            Class[] parameterClasses = constructors[i].getParameterTypes();
            Object[] parameters = new Object[parameterClasses.length];
            for (int j=0; j<parameters.length; j++) {
                if (sim != null) {
                    if (parameterClasses[j] == Simulation.class) {
                        parameters[j] = sim;
                    }
                    else if (parameterClasses[j] == PotentialMaster.class) {
                        parameters[j] = sim.getPotentialMaster();
                    }
                    else if (parameterClasses[j] == Space.class) {
                        parameters[j] = sim.getSpace();
                    }
                    else {
                        found = false;
                        break;
                    }
                }
                else {
                    found = false;
                    for (int k=0; k<extraParameters.length; k++) {
                        if (parameterClasses[j].isInstance(extraParameters[k])) {
                            parameters[j] = extraParameters[k];
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        break;
                    }
                }
            }
            if (!found) {
                continue;
            }
            try {
                return constructors[i].newInstance(parameters);
            }
            catch (InstantiationException e) {
                System.err.println("Could not instantiate class: " + e.getMessage() );
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                System.err.println("Illegal access while creating class: " + e.getMessage() );
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                System.err.println("Exception creating class: " + e.getMessage() );
                e.printStackTrace();
            }
            return null;
        }
        return null;
	}
    
    protected Constructor getConstructor(Class objectClass) {
        Constructor[] constructors = objectClass.getConstructors();
        if (constructors.length == 0) {
            return null;
        }
        for (int i=0; i<constructors.length; i++) {
            boolean found = true;
            Class[] parameterClasses = constructors[i].getParameterTypes();
            Object[] parameters = new Object[parameterClasses.length];
            for (int j=0; j<parameters.length; j++) {
                if (sim != null) {
                    if (parameterClasses[j] != Simulation.class && 
                        parameterClasses[j] != PotentialMaster.class &&
                        parameterClasses[j] != Space.class) {
                        found = false;
                        break;
                    }
                }
                else {
                    found = false;
                    for (int k=0; k<extraParameterClasses.length; k++) {
                        if (parameterClasses[j].isAssignableFrom(extraParameterClasses[k])) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        break;
                    }
                }
            }
            if (!found) {
                continue;
            }
            return constructors[i];
        }
        System.out.println("no constructor for "+objectClass.getName());
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
    
    public void addBaseClass(Class newBaseClass) {
        baseClass = (Class[])Arrays.addObject(baseClass,newBaseClass);
    }
    
    public void setBaseClass(Class newBaseClass) {
        baseClass = new Class[]{newBaseClass};
        rebuildClassList();
    }
    
    public void addExtraObject(String displayName, Object extraObject) {
        extraChoices = (Object[][])Arrays.addObject(extraChoices, new Object[]{displayName,extraObject});
        // force reconstruction of combo boxes
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
    
    public void addExcludedClass(Class newExcludedClass) {
        excludedClasses = (Class[])Arrays.addObject(excludedClasses,newExcludedClass);
        setExcludedClasses(excludedClasses);
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

        for (int i=0; i<categories.length; i++) {
            categoryMap.put(categories[i][0],categories[i][1]);
            categoryCombo.add((String)categories[i][0]);
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
        if (objectCategory == null && categories.length > 0) {
            // categories exist, but none have been selected
            return;
        }
        if (objectCategory == Object.class) {
            // this is the signal that the user has
            // selected the Existing Objects
            for (int i=0; i<extraChoices.length; i++) {
                String str = (String)extraChoices[i][0];
                if (str == null) {
                    Class extraClass = extraChoices[i][1].getClass();
                    EtomicaInfo info = EtomicaInfo.getInfo(extraClass);
                    str = extraChoices[i][1].toString()+": "+info.getShortDescription();
                }
                classCombo.add(str);
                classMap.put(str, extraChoices[i][1]);
            }
            return;
        }

        Collection classesFromRegistry = new LinkedList();
        for (int i=0; i<baseClass.length; i++) {
            classesFromRegistry.addAll(Registry.queryWhoExtends(baseClass[i]));
        }
    
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

            // if we're looking for Objects of a superclass of one of the 
            // other categories, then exclude objects of the other category
            boolean skipMe = false;
            for (int i=0; i<categories.length; i++) {
                Class otherCategory = (Class)categories[i][1];
                // if the "other" category is a sublcass of the current category
                if (otherCategory == objectCategory || !objectCategory.isAssignableFrom(otherCategory)) {
                    continue;
                }
                // and the current class is a subclass of the "other" category
                if (otherCategory.isAssignableFrom(objectClass)) {
                    skipMe = true;
                    break;
                }
            }
            if (skipMe) {
                continue;
            }
            // if we have no categories don't need to check
            if (objectCategory != null && !objectCategory.isAssignableFrom(objectClass)) {
                // user wants a specific type of Class.
                continue;
            }
            //look for a constructor we can use.  if there isn't one, there's no point in showing the class
            Constructor objConstructor = getConstructor(objectClass);
            if (objConstructor == null) {
                continue;
            }
            //now we drop the constructor on the floor.  :(
            
            EtomicaInfo info = EtomicaInfo.getInfo(objectClass);
            String str = ClassDiscovery.chopClassName(objectClass.getName())+": "+info.getShortDescription();
            if (classMap.get(str) != null) {
                // class was included twice due to multiple desired interfaces
                continue;
            }
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
