package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.integrator.mcmove.MCMove;
import etomica.simulation.Simulation;

public class MCMoveWrapper extends PropertySourceWrapper {

    public MCMoveWrapper(MCMove move, Simulation sim) {
        super(move,sim);
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
