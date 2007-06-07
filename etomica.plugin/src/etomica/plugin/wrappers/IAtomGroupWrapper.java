package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.AtomSet;
import etomica.atom.IAtom;
import etomica.atom.IAtomGroup;
import etomica.simulation.Simulation;
import etomica.util.Arrays;

public class IAtomGroupWrapper extends InterfaceWrapper {

    public IAtomGroupWrapper(IAtomGroup object, Simulation sim) {
        super(object,sim);
    }
    
    public IPropertyDescriptor[] generateDescriptors() {
        IPropertyDescriptor[] newDescriptors = super.generateDescriptors();
        if (((IAtomGroup)object).getChildList().getAtomCount() > 0) {
            // add the child list since that's actually a property of the node
            // and we hide the node
            newDescriptors = (IPropertyDescriptor[])Arrays.addObject(newDescriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("children","children"));
        }
        return newDescriptors;
    }

    public IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name) {
        if (name.equals("childList")) {
            // we don't need to show the childList since we make explicit
            // descriptors for the individual children
            return PropertySourceWrapper.PROPERTY_VETO;
        }
        return null;
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof String)) {
            return super.getPropertyValue(key);
        }
        String keyString = (String)key;
        PropertySourceWrapper wrapper = null;
        if (keyString.equals("children")) {
            AtomSet children = ((IAtomGroup)object).getChildList();
            int count = children.getAtomCount();
            IAtom[] childAtoms = new IAtom[count];
            for (int i=0; i<count; i++) {
                childAtoms[i] = children.getAtom(i);
            }
            wrapper = PropertySourceWrapper.makeWrapper(childAtoms, simulation, editor);
            wrapper.setDisplayName("Child Atoms");
        }
        return wrapper;
    }
}
