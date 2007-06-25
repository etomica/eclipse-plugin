package etomica.plugin.wrappers;

import java.util.LinkedList;

import etomica.nbr.cell.PotentialMasterCell;
import etomica.plugin.editors.SimulationObjects;

public class PotentialMasterCellWrapper extends PotentialMasterWrapper {

    public PotentialMasterCellWrapper(PotentialMasterCell object,
            SimulationObjects simObjects) {
        super(object, simObjects);
    }

    public EtomicaStatus getStatus(LinkedList parentList) {
        double range = ((PotentialMasterCell)object).getRange();
        if (range == 0) {
            return new EtomicaStatus("Range must be positive", EtomicaStatus.ERROR);
        }
        if (range < ((PotentialMasterCell)object).getMaxPotentialRange()) {
            return new EtomicaStatus("Range must be greater than longest-range potential", EtomicaStatus.ERROR);
        }
        return super.getStatus(parentList);
    }

}
