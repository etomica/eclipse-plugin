package etomica.plugin.editors;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.data.DataPump;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorBox;
import etomica.potential.PotentialMaster;
import etomica.simulation.ISimulation;

/**
 * The purpose of this class is to probe the Simulation, looking for data
 * streams, integrators and PotentialMasters.  If we are loading a Simulation
 * we created in the IDE, those will already be in simObjects, but we might
 * be loading a Simulation we didn't create ourselves.
 * 
 * @author Andrew Schultz
 */
public class SimulationRegister {

    public SimulationRegister(SimulationObjects simObjects) {
        this.simObjects = simObjects;
    }

    public void registerElements() {
        registerElements(simObjects.simulation);
    }

    public void registerElements(Object obj) {
        if (obj instanceof ISimulation) {
            registerElements(((ISimulation)obj).getController());
        }
        if (obj instanceof PotentialMaster) {
            if (!simObjects.potentialMasters.contains(obj)) {
                simObjects.potentialMasters.add(obj);
            }
        }
        if (obj instanceof ActivityGroup) {
            Action[] actions = ((ActivityGroup)obj).getAllActions();
            for (int i=0; i<actions.length; i++) {
                registerElements(actions[i]);
            }
        }
        if (obj instanceof ActivityIntegrate) {
            registerElements(((ActivityIntegrate)obj).getIntegrator());
        }
        if (obj instanceof Integrator) {
            if (!simObjects.integrators.contains(obj)) {
                simObjects.integrators.add(obj);
            }
            Action[] intervalActions = ((Integrator)obj).getIntervalActions();
            for (int i=0; i<intervalActions.length; i++) {
                registerElements(intervalActions[i]);
            }
            if (obj instanceof IntegratorBox) {
                registerElements(((IntegratorBox)obj).getPotential());
            }
        }
        if (obj instanceof DataPump) {
            if (!simObjects.dataStreams.contains(obj)) {
                simObjects.dataStreams.add(obj);
            }
        }
    }
    
    private final SimulationObjects simObjects;
}
