package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.AtomSource;
import etomica.simulation.Simulation;

public class AtomSourceWrapper extends PropertySourceWrapper {

    public AtomSourceWrapper(AtomSource atomSource, Simulation sim) {
        super(atomSource,sim);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        // hide the getAtom method in the property sheet (since it alters the state)
        if (property.getDisplayName().equals("atom")) {
            return null;
        }
        
        return super.makeDescriptor(property);
    }
}
