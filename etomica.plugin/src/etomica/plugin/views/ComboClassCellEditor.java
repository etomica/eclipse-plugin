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
        Object selection = super.doGetValue();
        if (selection instanceof Class) {
            Class selectedClass = (Class)selection;
            Constructor[] constructors = selectedClass.getConstructors();
            if (constructors.length == 0) {
                // class has no constructors!
                return null;
            }
            Object[] constructorParameters = null;
            for (int j=0; j<constructors.length; j++) {
                Class[] parameterClasses = constructors[j].getParameterTypes();
                constructorParameters = new Object[parameterClasses.length];
                boolean[] foundParameter = new boolean[parameterClasses.length];
                boolean foundConstructor = true;
                for (int i=0; i<parameterClasses.length; i++) {
                    for (int k=0; k<parameters.length; k++) {
                        if (parameterClasses[i].isInstance(parameters[k])) {
                            constructorParameters[i] = parameters[k];
                            foundParameter[i] = true;
                            break;
                        }
                    }
                    if (!foundParameter[i]) {
                        //we don't have the parameters appropriate for this constructor
                        foundConstructor = false;
                        break;
                    }
                }
                if (foundConstructor) {
                    try {
                        return constructors[j].newInstance(constructorParameters);
                    } catch (InstantiationException e) {
                        System.err.println( "Could not instantiate class: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.err.println( "Illegal access while creating class: " + e.getMessage() );
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e) {
                        System.err.println( "Exception creating class: " + e.getMessage() );
                        e.printStackTrace();
                    }
                    return null;
                }
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
