/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

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
import etomica.simulation.Simulation;
import etomica.space.ICoordinate;
import etomica.space.Space;
import etomica.space.Vector;
import etomica.util.Arrays;
import etomica.util.EnumeratedType;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationViewContentProvider implements ITreeContentProvider {

    public SimulationViewContentProvider() {
    }

    /**
     * Simulation is root.
     * Controller is child of simulation.
     * ActivityGroups are parents of actions/activities
     */
    public Object[] getChildren(Object wrappedElement) {
        PropertySourceWrapper wrapper = (PropertySourceWrapper)wrappedElement;
        if (wrapper instanceof ArrayWrapper) {
            return ((ArrayWrapper)wrappedElement).getChildren();
        }
        if (wrapper.getObject() instanceof ActivityGroup) {
            // ActivityGroup has (get){Completed,Pending,Current}Action, which we don't care about
            // the wrapper will give us AllActions, which is what we want
            return wrapper.getChildren();
        }
        IPropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
        int count = 0;
        PropertySourceWrapper[] childWrappers = new PropertySourceWrapper[0];
        for (int i=0; i<descriptors.length; i++) {
            Object pd = descriptors[i].getId();
            Object value = wrapper.getPropertyValue(pd);
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
            if (wrapper instanceof AtomTypeWrapper
                    && (descriptors[i].getDisplayName().equals("parentType") ||
                            descriptors[i].getDisplayName().equals("species"))) {
                continue;
            }
            if (!(wrapper instanceof ArrayWrapper) &&
                    obj instanceof Phase) {
                // we only want to show Phases at the top level
                continue;
            }
            childWrappers = (PropertySourceWrapper[])Arrays.resizeArray(childWrappers,++count);
            if (value instanceof PropertySourceWrapper) {
                childWrappers[count-1] = (PropertySourceWrapper)value;
            }
            else {
                childWrappers[count-1] = PropertySourceWrapper.makeWrapper(obj,simulation,wrapper.getEditor());
            }
        }
        return childWrappers;
    }
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    public Object[] getElements(Object inputElement) {
        //the call to viewer.setInput in createPartControl causes the list of
        //simulation instances to be the input element in this method
        //we'll save it for later
        simulation = (Simulation)((PropertySourceWrapper)inputElement).getObject();
        return ((PropertySourceWrapper)inputElement).getChildren();
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object wrappedElement) {
        return getChildren(wrappedElement).length > 0;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
    private Simulation simulation;
    private static final Class[] excludedClasses = new Class[]{Number.class,Boolean.class,
            Color.class,Vector.class,DataInfo.class,EnumeratedType.class,AtomAddressManager.class,
            String.class,FeatureSet.class,LinkedList.class,Space.class,Polytope.class,Class.class,
            AtomsetIterator.class,Space.class,DataTag.class,AtomTreeNode.class,ICoordinate.class};
}
