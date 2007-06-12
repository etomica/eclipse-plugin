package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.AtomSource;
import etomica.plugin.editors.SimulationObjects;

public class AtomSourceWrapper extends InterfaceWrapper {

    public AtomSourceWrapper(AtomSource atomSource, SimulationObjects simObjects) {
        super(atomSource,simObjects);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        // hide the getAtom method in the property sheet (since it alters the state)
        if (property.getDisplayName().equals("atom")) {
            return PropertySourceWrapper.PROPERTY_VETO;
        }
        
        return null;
    }
}
