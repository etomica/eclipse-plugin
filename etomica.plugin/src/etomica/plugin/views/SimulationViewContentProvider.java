/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.action.activity.ActivityGroup;
import etomica.atom.AtomAddressManager;
import etomica.atom.AtomTreeNode;
import etomica.atom.iterator.AtomsetIterator;
import etomica.compatibility.FeatureSet;
import etomica.data.DataInfo;
import etomica.data.DataTag;
import etomica.math.geometry.Polytope;
import etomica.phase.Phase;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.AtomTypeWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.plugin.wrappers.SimulationWrapper;
import etomica.simulation.Simulation;
import etomica.space.IVector;
import etomica.space.Space;
import etomica.util.Arrays;
import etomica.util.EnumeratedType;

/**
 * This class determines the contents of the Simulation view (editor).
 */
public class SimulationViewContentProvider implements ITreeContentProvider {

    public SimulationViewContentProvider() {
        wrapperHash = new HashMap();
    }
    
    public void refresh() {
        wrapperHash.clear();
    }

    /**
     * Returns child elements of the given elements to be displayed in the 
     * Simulation view
     */
    public Object[] getChildren(Object wrappedElement) {
        PropertySourceWrapper parentWrapper = (PropertySourceWrapper)wrappedElement;
        if (parentWrapper instanceof ArrayWrapper || 
            parentWrapper.getObject() instanceof ActivityGroup ||
            parentWrapper instanceof SimulationWrapper) {
            PropertySourceWrapper[] childWrappers = parentWrapper.getChildren();
            for (int i=0; i<childWrappers.length; i++) {
                Object obj = childWrappers[i].getObject();
                WrapperWrapper hashedWrapperWrapper = (WrapperWrapper)wrapperHash.get(obj);
                if (hashedWrapperWrapper != null) {
                    if (recursionCheck(hashedWrapperWrapper.wrapper, parentWrapper)) {
                        childWrappers = (PropertySourceWrapper[])Arrays.removeObject(
                                childWrappers,childWrappers[i]);
                        i--;
                        continue;
                    }
                    childWrappers[i] = hashedWrapperWrapper.wrapper;
                }
                hashedWrapperWrapper = new WrapperWrapper(childWrappers[i]);
                hashedWrapperWrapper.parentWrapperList.add(parentWrapper);
                wrapperHash.put(obj, hashedWrapperWrapper);
            }
            return childWrappers;
        }
        IPropertyDescriptor[] descriptors = parentWrapper.getPropertyDescriptors();
        int count = 0;
        PropertySourceWrapper[] childWrappers = new PropertySourceWrapper[0];
        for (int i=0; i<descriptors.length; i++) {
            Object pd = descriptors[i].getId();
            Object value = parentWrapper.getPropertyValue(pd);
            if (value == null) {
                continue;
            }
            Object obj = value;
            if (obj instanceof PropertySourceWrapper) {
                obj = ((PropertySourceWrapper)value).getObject();
            }
            Class objClass = obj.getClass();
            if (objClass.isArray()) {
                if (!(obj instanceof Object[])) {
                    continue;
                }
                if (((Object[])obj).length == 0) {
                    continue;
                }
                objClass = objClass.getComponentType();
            }
            boolean excluded = false;
            for (int j=0; j<excludedClasses.length; j++) {
                if (excludedClasses[j].isAssignableFrom(objClass)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            }
            if (parentWrapper instanceof AtomTypeWrapper
                    && (descriptors[i].getDisplayName().equals("parentType") ||
                            descriptors[i].getDisplayName().equals("species"))) {
                continue;
            }
            if (!(parentWrapper instanceof ArrayWrapper) &&
                    obj instanceof Phase) {
                // we only want to show Phases at the top level
                continue;
            }
            childWrappers = (PropertySourceWrapper[])Arrays.resizeArray(childWrappers,++count);
            WrapperWrapper hashedWrapperWrapper = (WrapperWrapper)wrapperHash.get(obj);
            if (hashedWrapperWrapper != null) {
                if (recursionCheck(hashedWrapperWrapper.wrapper, parentWrapper)) {
                    continue;
                }
                childWrappers[count-1] = hashedWrapperWrapper.wrapper;
            }
            else {
                if (value instanceof PropertySourceWrapper) {
                    childWrappers[count-1] = (PropertySourceWrapper)value;
                }
                else {
                    childWrappers[count-1] = PropertySourceWrapper.makeWrapper(obj,simulation,parentWrapper.getEditor());
                }
                hashedWrapperWrapper = new WrapperWrapper(childWrappers[count-1]);
                wrapperHash.put(obj, hashedWrapperWrapper);
            }
            if (!hashedWrapperWrapper.parentWrapperList.contains(parentWrapper)) {
                hashedWrapperWrapper.parentWrapperList.add(parentWrapper);
            }

        }
        return childWrappers;
    }
    
    /**
     * Returns true if child wrapper appears in the ancestry of parent
     */
    protected boolean recursionCheck(PropertySourceWrapper child, PropertySourceWrapper parent) {
        if (child == parent) return true;
        WrapperWrapper parentWrapperWrapper = (WrapperWrapper)wrapperHash.get(parent.getObject());
        if (parentWrapperWrapper == null) return false;
        Iterator iterator = parentWrapperWrapper.parentWrapperList.iterator();
        while (iterator.hasNext()) {
            if (recursionCheck(child, (PropertySourceWrapper)iterator.next())) {
                System.out.println("recursion: "+child.getDisplayName()+" descended from "+parent.getDisplayName());
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    public Object[] getElements(Object inputElement) {
        //the call to viewer.setInput in createPartControl causes the 
        //simulation to be the input element in this method
        //we'll save it for later
        simulation = (Simulation)((PropertySourceWrapper)inputElement).getObject();
        return getChildren(inputElement);
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object wrappedElement) {
        return getChildren(wrappedElement).length > 0;
    }

    public void dispose() {
        simulation = null;
        wrapperHash.clear();
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
    private Simulation simulation;
    private static final Class[] excludedClasses = new Class[]{Number.class,Boolean.class,
            Color.class,IVector.class,DataInfo.class,EnumeratedType.class,AtomAddressManager.class,
            String.class,FeatureSet.class,LinkedList.class,Space.class,Polytope.class,Class.class,
            AtomsetIterator.class,Space.class,DataTag.class,AtomTreeNode.class};
    
    private HashMap wrapperHash;
    
    private static class WrapperWrapper {
        public final PropertySourceWrapper wrapper;
        public final LinkedList parentWrapperList;
        
        public WrapperWrapper(PropertySourceWrapper wrapper) {
            this.wrapper = wrapper;
            parentWrapperList = new LinkedList();
        }
    }
}
