/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import java.util.LinkedList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.AtomFactory;
import etomica.atom.AtomType;
import etomica.atom.AtomTypeGroup;
import etomica.atom.ISpeciesAgent;
import etomica.atom.SpeciesAgent;
import etomica.phase.Phase;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.species.Species;
import etomica.util.Arrays;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpeciesViewContentProvider implements ITreeContentProvider {

    public SpeciesViewContentProvider() {
    }

    /**
     * Simulation is root.
     * Controller is child of simulation.
     * ActivityGroups are parents of actions/activities
     */
    public Object[] getChildren(Object wrappedElement) {
        if (wrappedElement instanceof ArrayWrapper) {
            return ((ArrayWrapper)wrappedElement).getChildren(new LinkedList());
        }
        Object element = ((PropertySourceWrapper)wrappedElement).getObject();
        if (element instanceof AtomType) {
            //we just want to return the child atom types, if they exist
            if (element instanceof AtomTypeGroup) {
                return PropertySourceWrapper.wrapArrayElements(((AtomTypeGroup)element).getChildTypes(), 
                        simObjects, null);
            }
            return new Object[0];
        }
        if (element instanceof SpeciesAgent) {
            return new Object[0];
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
            Class valueClass = null;
            if (value instanceof PropertySourceWrapper) {
                valueClass = ((PropertySourceWrapper)value).getObject().getClass();
            }
            else {
                valueClass = value.getClass();
            }
            if (valueClass.isArray()) {
                if (!(value instanceof Object[])) {
                    continue;
                }
                if (((Object[])value).length == 0) {
                    continue;
                }
                valueClass = valueClass.getComponentType();
            }
            if (!(SpeciesAgent.class.isAssignableFrom(valueClass)) &&
                    !(AtomFactory.class.isAssignableFrom(valueClass)) &&
                    !(AtomType.class.isAssignableFrom(valueClass))) {
                continue;
            }
            if (element instanceof AtomType &&
                    (descriptors[i].getDisplayName().equals("parentType") ||
                            descriptors[i].getDisplayName().equals("species"))) {
                continue;
            }
            childWrappers = (PropertySourceWrapper[])Arrays.resizeArray(childWrappers,++count);
            if (value instanceof PropertySourceWrapper) {
                childWrappers[count-1] = (PropertySourceWrapper)value;
            }
            else {
                childWrappers[count-1] = PropertySourceWrapper.makeWrapper(value,simObjects);
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
        if (simObjects == null) {
            return getChildren(inputElement);
        }
        Species species = (Species)((PropertySourceWrapper)inputElement).getObject();
        Phase[] phases = simObjects.simulation.getPhases();
        ISpeciesAgent[] agents = new SpeciesAgent[phases.length];
        for (int i=0; i<phases.length; i++) {
            agents[i] = phases[i].getAgent(species);
        }
        PropertySourceWrapper[] wrappers = new PropertySourceWrapper[3];
        wrappers[0] = PropertySourceWrapper.makeWrapper(agents,simObjects);
        wrappers[1] = PropertySourceWrapper.makeWrapper(species.getMoleculeFactory(),simObjects);
        wrappers[2] = PropertySourceWrapper.makeWrapper(species.getMoleculeType(),simObjects);
        return wrappers;
    }


    public Object getParent(Object element) {
        System.out.println("SimulationViewContentProvide.getParent");
        return null;
    }

    public boolean hasChildren(Object wrappedElement) {
        return getChildren(wrappedElement).length > 0;
    }

    public void dispose() {
    }

    public void setSimulationObjects(SimulationObjects newSimulationObjects) {
        simObjects = newSimulationObjects;
    }
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        currentSelection = newInput;
    }
    
    Object currentSelection;

    private SimulationObjects simObjects;
}
