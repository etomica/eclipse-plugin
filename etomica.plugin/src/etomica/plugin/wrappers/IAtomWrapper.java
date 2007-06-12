package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.IAtom;
import etomica.plugin.editors.SimulationObjects;

public class IAtomWrapper extends InterfaceWrapper {

    public IAtomWrapper(IAtom object, SimulationObjects simObjects) {
        super(object,simObjects);
    }
    
    public IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name) {
        if (name.startsWith("parent")) {
            // we don't want parentGroup since we want children only.
            return PropertySourceWrapper.PROPERTY_VETO;
        }
        return null;
    }

}
