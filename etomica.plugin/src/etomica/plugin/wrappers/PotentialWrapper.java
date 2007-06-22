package etomica.plugin.wrappers;

import java.util.ArrayList;

import etomica.plugin.editors.SimulationObjects;
import etomica.potential.IPotential;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialMaster;

public class PotentialWrapper extends PropertySourceWrapper {

    public PotentialWrapper(Potential object, SimulationObjects simObjects) {
        super(object,simObjects);
        ArrayList potentialMasters = simObjects.potentialMasters;
        
        for (int i=0; i<potentialMasters.size(); i++) {
            IPotential[] potentials = ((PotentialMaster)simObjects.potentialMasters.get(i)).getPotentials();
            for (int j=0; j<potentials.length; j++) {
                if (lookForMe(potentials[j])) {
                    myPotentialMaster = (PotentialMaster)simObjects.potentialMasters.get(i);
                }
            }
        }
    }

    public boolean lookForMe(IPotential potential) {
        if (potential == object) {
            return true;
        }
        if (potential instanceof PotentialGroup) {
            IPotential[] childPotentials = ((PotentialGroup)potential).getPotentials();
            for (int i=0; i<childPotentials.length; i++) {
                if (lookForMe(childPotentials[i])) {
                    return true;
                }
            }                
        }
        return false;
    }
    
    public PotentialMaster getMyPotentialMaster() {
        return myPotentialMaster;
    }
    
    protected PotentialMaster myPotentialMaster;
}
