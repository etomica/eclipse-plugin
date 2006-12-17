package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.Atom;
import etomica.atom.AtomLeaf;
import etomica.atom.AtomTreeNodeGroup;
import etomica.simulation.Simulation;
import etomica.space.ICoordinateKinetic;
import etomica.util.Arrays;

public class AtomWrapper extends PropertySourceWrapper {

    public AtomWrapper(Atom object, Simulation sim) {
        super(object,sim);
    }
    
    protected void generateDescriptors() {
        super.generateDescriptors();
        descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("type","type"));
        if (((Atom)object).getNode().childAtomCount() > 0) {
            descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("children","children"));
        }
        if (object instanceof AtomLeaf) {
            descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("position","position"));
            if (((AtomLeaf)object).getCoord() instanceof ICoordinateKinetic) {
                descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("velocity","velocity"));
            }
        }
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof String)) {
            return super.getPropertyValue(key);
        }
        String keyString = (String)key;
        PropertySourceWrapper wrapper = null;
        if (keyString.equals("type")) {
            return ((Atom)object).getType();
        }
        if (keyString.equals("children")) {
            Atom[] childAtoms = ((AtomTreeNodeGroup)((Atom)object).getNode()).getChildList().toArray();
            wrapper = PropertySourceWrapper.makeWrapper(childAtoms, simulation, etomicaEditor);
            wrapper.setDisplayName("Child Atoms");
        }
        if (keyString.equals("position")) {
            wrapper = PropertySourceWrapper.makeWrapper(((AtomLeaf)object).getCoord().position(), 
                    simulation, etomicaEditor);
        }
        if (keyString.equals("velocity")) {
            wrapper = PropertySourceWrapper.makeWrapper(((ICoordinateKinetic)((AtomLeaf)object).getCoord()).velocity(),
                    simulation, etomicaEditor);
        }
        return wrapper;
    }
    
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
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        // we can only remove molecules
        if (child instanceof Atom && ((Atom)child).getNode().parentGroup() == object) {
            return true;
        }
        return false;
    }
}
