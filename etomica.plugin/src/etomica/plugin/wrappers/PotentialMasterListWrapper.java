package etomica.plugin.wrappers;

import java.util.LinkedList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.nbr.list.NeighborListManager;
import etomica.nbr.list.PotentialMasterList;
import etomica.phase.Phase;
import etomica.plugin.editors.SimulationObjects;

public class PotentialMasterListWrapper extends PotentialMasterWrapper {

    public PotentialMasterListWrapper(PotentialMasterList object,
            SimulationObjects simObjects) {
        super(object, simObjects);
    }

    protected IPropertyDescriptor[] generateDescriptors() {
        IPropertyDescriptor[] descriptors = super.generateDescriptors();
        
        // add in all of the PotentialMasterList's NeighborListManagers
        IPropertyDescriptor newDescriptor = makeDescriptor("neighborListManagers", null, NeighborListManager[].class, "NeighborList managers", simObjects);
        IPropertyDescriptor[] newDescriptors = new IPropertyDescriptor[descriptors.length+1];
        System.arraycopy(descriptors, 0, newDescriptors, 0, descriptors.length);
        newDescriptors[descriptors.length] = newDescriptor;
        
        return newDescriptors;
    }

    public Object getPropertyValue(Object key) {
        if (key instanceof String && ((String)key).equals("neighborListManagers")) {
            Phase[] phases = simObjects.simulation.getPhases();
            NeighborListManager[] neighborListManagers = new NeighborListManager[phases.length];
            for (int i=0; i<phases.length; i++) {
                neighborListManagers[i] = ((PotentialMasterList)object).getNeighborManager(phases[i]);
            }
            return neighborListManagers; //PropertySourceWrapper.wrapArrayElements(neighborListManagers, simObjects, etomicaEditor);
        }
        return super.getPropertyValue(key);
    }

    public EtomicaStatus getStatus(LinkedList parentList) {
        double range = ((PotentialMasterList)object).getRange();
        if (range == 0) {
            return new EtomicaStatus("Range must be positive", EtomicaStatus.ERROR);
        }
        if (range < ((PotentialMasterList)object).getMaxPotentialRange()) {
            return new EtomicaStatus("Range must be greater than longest-range potential", EtomicaStatus.ERROR);
        }
        return super.getStatus(parentList);
    }

}
