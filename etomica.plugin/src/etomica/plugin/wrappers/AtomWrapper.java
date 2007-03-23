package etomica.plugin.wrappers;

import java.beans.PropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.Atom;
import etomica.atom.AtomGroup;
import etomica.atom.SpeciesAgent;
import etomica.simulation.Simulation;
import etomica.util.Arrays;

public class AtomWrapper extends PropertySourceWrapper implements RemoverWrapper {

    public AtomWrapper(Atom object, Simulation sim) {
        super(object,sim);
    }
    
    protected IPropertyDescriptor[] generateDescriptors() {
        IPropertyDescriptor[] newDescriptors = super.generateDescriptors();
        if (object instanceof AtomGroup && ((AtomGroup)object).getChildList().size() > 0) {
            // add the child list since that's actually a property of the node
            // and we hide the node
            newDescriptors = (IPropertyDescriptor[])Arrays.addObject(newDescriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("children","children"));
        }
        return newDescriptors;
    }

    protected IPropertyDescriptor makeDescriptor(PropertyDescriptor property) {
        if (property.getDisplayName().equals("childList") || property.getDisplayName().startsWith("parent")) {
            // we don't need to show the childList since we make explicit
            // descriptors for the individual children
            // we also don't want parent{Species,Molecule,Group} since we want children only.
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
            Atom[] childAtoms = ((AtomGroup)object).getChildList().toArray();
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
        if (object instanceof SpeciesAgent && child instanceof Atom && ((Atom)child).getParentGroup() == object) {
            ((Atom)child).setParent(null);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object child) {
        // we can only remove molecules
        if (object instanceof SpeciesAgent && child instanceof Atom && ((Atom)child).getParentGroup() == object) {
            return true;
        }
        return false;
    }
}
