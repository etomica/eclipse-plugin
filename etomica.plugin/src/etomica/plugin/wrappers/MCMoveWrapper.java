package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.integrator.mcmove.MCMove;
import etomica.plugin.editors.SimulationObjects;

public class MCMoveWrapper extends PropertySourceWrapper {

    public MCMoveWrapper(MCMove move, SimulationObjects simObjects) {
        super(move,simObjects);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        // hide the getA and getB methods in the property sheet.
        if (property.getDisplayName().equals("a")) {
            return null;
        }
        if (property.getDisplayName().equals("b")) {
            return null;
        }
        
        return super.makeDescriptor(property);
    }
}
