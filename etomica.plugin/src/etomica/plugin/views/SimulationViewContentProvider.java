/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import java.util.LinkedList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.action.activity.ActivityGroup;
import etomica.compatibility.FeatureSet;
import etomica.data.DataInfo;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.AtomTypeWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.simulation.Simulation;
import etomica.space.Vector;
import etomica.util.Arrays;
import etomica.util.EnumeratedType;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationViewContentProvider implements ITreeContentProvider {

    public SimulationViewContentProvider() {
        //Simulation.instantiationEventManager.addListener(this); 
        
    }
	/**
	 * Simulation is root.
	 * Controller is child of simulation.
	 * ActivityGroups are parents of actions/activities
	 */
	public Object[] getChildren(Object wrappedElement) {
        if (wrappedElement instanceof ArrayWrapper) {
            return ((ArrayWrapper)wrappedElement).getChildren();
        }
        if (((PropertySourceWrapper)wrappedElement).getObject() instanceof ActivityGroup) {
            // ActivityGroup has (get){Completed,Pending,Current}Action, which we don't care about
            // the wrapper will give us AllActions, which is what we want
            return ((PropertySourceWrapper)wrappedElement).getChildren();
        }
        IPropertyDescriptor[] descriptors = ((PropertySourceWrapper)wrappedElement).getPropertyDescriptors();
        int count = 0;
        PropertySourceWrapper[] childWrappers = new PropertySourceWrapper[0];
        for (int i=0; i<descriptors.length; i++) {
            Object pd = descriptors[i].getId();
            Object value = ((PropertySourceWrapper)wrappedElement).getPropertyValue(pd);
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
            if ((wrappedElement instanceof AtomTypeWrapper)
                    && (descriptors[i].getDisplayName().equals("parentType") ||
                            descriptors[i].getDisplayName().equals("species"))) {
                continue;
            }
            childWrappers = (PropertySourceWrapper[])Arrays.resizeArray(childWrappers,++count);
            if (value instanceof PropertySourceWrapper) {
                childWrappers[count-1] = (PropertySourceWrapper)value;
            }
            else {
                childWrappers[count-1] = PropertySourceWrapper.makeWrapper(obj,simulation);
            }
        }
        return childWrappers;
	}
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    //the call to viewer.setInput in createPartControl causes the list of
    //simulation instances to be the input element in this method
    public Object[] getElements(Object inputElement) {
        simulation = (Simulation)((PropertySourceWrapper)inputElement).getObject();
        return ((PropertySourceWrapper)inputElement).getChildren();
    }


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object wrappedElement) {
        // this will return false positives for non-Object or null children
        return ((PropertySourceWrapper)wrappedElement).getPropertyDescriptors().length > 0;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
        //Simulation.instantiationEventManager.removeListener(this);
		viewer = null;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer)viewer;
        currentSelection = newInput;
    }
    
    Object currentSelection;

	private TreeViewer viewer;
    private Simulation simulation;
    private static final Class[] excludedClasses = new Class[]{Number.class,Boolean.class,
            Color.class,Vector.class,DataInfo.class,EnumeratedType.class,
            String.class,FeatureSet.class,LinkedList.class};
}
