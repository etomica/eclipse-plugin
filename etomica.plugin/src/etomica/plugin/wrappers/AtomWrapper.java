package etomica.plugin.wrappers;

import java.beans.PropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.Atom;
import etomica.atom.AtomTreeNodeGroup;
import etomica.simulation.Simulation;
import etomica.util.Arrays;

public class AtomWrapper extends PropertySourceWrapper implements RemoverWrapper {

    public AtomWrapper(Atom object, Simulation sim) {
        super(object,sim);
    }
    
    protected void generateDescriptors() {
        super.generateDescriptors();
        if (((Atom)object).getNode().childAtomCount() > 0) {
            descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("children","children"));
        }
    }

    protected IPropertyDescriptor makeDescriptor(PropertyDescriptor property) {
        if (property.getDisplayName().equals("node")) {
            return null;
        }
        return super.makeDescriptor(property);
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof String)) {
            return super.getPropertyValue(key);
        }
        String keyString = (String)key;
        PropertySourceWrapper wrapper = null;
        if (keyString.equals("children")) {
            Atom[] childAtoms = ((AtomTreeNodeGroup)((Atom)object).getNode()).getChildList().toArray();
            wrapper = PropertySourceWrapper.makeWrapper(childAtoms, simulation, etomicaEditor);
            wrapper.setDisplayName("Child Atoms");
        }
        return wrapper;
    }
    
    // allow individual molecules to be removed.  molecules should be added via
    // the property sheet for the SpeciesAgent
    public boolean removeChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (child instanceof Atom && ((Atom)child).getNode().parentSpeciesAgent() == object) {
            ((Atom)child).getNode().setParent(null);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object child) {
        // we can only remove molecules
        if (child instanceof Atom && ((Atom)child).getNode().parentGroup() == object) {
            return true;
        }
        return false;
    }
}
