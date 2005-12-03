package etomica.plugin.views;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Composite;

import etomica.data.DataSource;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.space.Space;

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
            Constructor[] constructors = selectedClass.getConstructors();
            if (constructors.length == 0) {
                return null;
            }
            for (int j=0; j<constructors.length; j++) {
                Class[] parameterClasses = constructors[j].getParameterTypes();
                boolean found = true;
                for (int i=0; i<parameters.length; i++) {
                    if (!parameterClasses[i].isInstance(parameters[i])) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    try {
                        return constructors[j].newInstance(parameters);
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
            // no constructor found with the given parameters.  Try the default constructor
            try {
                return selectedClass.newInstance();
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
