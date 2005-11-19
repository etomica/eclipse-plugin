package etomica.plugin.views;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Composite;

/**
 * Special EnumeratedTypeCellEditor that gives the user the option to keep
 * the current object or to select the class of a new object to create. 
 *
 * @author Andrew Schultz
 */
public class ComboClassCellEditor extends ComboCellEditor {

    public ComboClassCellEditor() {
        super();
    }

    public ComboClassCellEditor(Composite parent, Object[] choices) {
        super(parent, choices);
    }

    public ComboClassCellEditor(Composite parent, Object[] choices,
            int style) {
        super(parent, choices, style);
    }

    protected Object doGetValue() {
        if (selection instanceof Class) {
            Class selectedClass = (Class)selection;
            Class[] parameterClasses = new Class[parameters.length];
            for (int i=0; i<parameters.length; i++) {
                parameterClasses[i] = parameters[i].getClass();
            }
            try {
                Constructor constructor = selectedClass.getConstructor(parameterClasses);
                if (constructor != null) {
                    return constructor.newInstance(parameters);
                }
            }
            catch (NoSuchMethodException e) {
                System.out.println("Class "+selectedClass+" did not have a constructor that took "+parameterClasses);
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                System.out.println("Could not inoke constructor for class "+selectedClass);
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                System.out.println("Could not instantiate object of class "+selectedClass);
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                System.out.println("Could not instantiate object of class "+selectedClass);
                e.printStackTrace();
            }
            return null;
        }
        return super.doGetValue();
    }
  
    /**
     * Sets the constructor parameters if the user selects to create a new
     * instance of a class.
     */
    public void setConstructorParameters(Object[] constructorParameters) {
        parameters = constructorParameters;
    }
    
    private Object[] parameters;
}
