package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.Atom;
import etomica.atom.AtomTreeNodeGroup;
import etomica.atom.iterator.AtomIteratorListSimple;
import etomica.space.ICoordinateKinetic;
import etomica.util.Arrays;

public class AtomWrapper extends PropertySourceWrapper {

    public AtomWrapper(Atom object) {
        super(object);
    }
    
    protected void generateDescriptors() {
        super.generateDescriptors();
        descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("type","type"));
        if (((Atom)object).node.childAtomCount() > 0) {
            descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("children","children"));
        }
        if (((Atom)object).coord != null) {
            descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("position","position"));
            if (((Atom)object).coord instanceof ICoordinateKinetic) {
                descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("velocity","velocity"));
            }
        }
        if (((Atom)object).allatomAgents.length > 0) {
            descriptors = (IPropertyDescriptor[])Arrays.addObject(descriptors,new org.eclipse.ui.views.properties.PropertyDescriptor("allAtomAgents","allAtomAgents"));
        }
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof String)) {
            return super.getPropertyValue(key);
        }
        String keyString = (String)key;
        PropertySourceWrapper wrapper = null;
        if (keyString.equals("type")) {
            wrapper = PropertySourceWrapper.makeWrapper(((Atom)object).type);
        }
        if (keyString.equals("children")) {
            iterator.setList(((AtomTreeNodeGroup)((Atom)object).node).childList);
            iterator.reset();
            Atom[] children = new Atom[iterator.size()];
            int i=0;
            while (iterator.hasNext()) {
                children[i++] = iterator.nextAtom();
            }
            wrapper = PropertySourceWrapper.makeWrapper(children);
            wrapper.setDisplayName("Child Atoms");
        }
        if (keyString.equals("position")) {
            wrapper = PropertySourceWrapper.makeWrapper(((Atom)object).coord.position());
        }
        if (keyString.equals("velocity")) {
            wrapper = PropertySourceWrapper.makeWrapper(((ICoordinateKinetic)((Atom)object).coord).velocity());
        }
        if (keyString.equals("allAtomAgents")) {
            wrapper = PropertySourceWrapper.makeWrapper(((Atom)object).allatomAgents);
            wrapper.setDisplayName("Atom Agents");
        }
        return wrapper;
    }
    
    public boolean removeChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (child instanceof Atom && ((Atom)child).node.parentSpeciesAgent() == object) {
            ((Atom)child).node.setParent((Atom)null);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (child instanceof Atom && ((Atom)child).node.parentSpeciesAgent() == object) {
            return true;
        }
        return false;
    }

    private static final AtomIteratorListSimple iterator = new AtomIteratorListSimple();
}
