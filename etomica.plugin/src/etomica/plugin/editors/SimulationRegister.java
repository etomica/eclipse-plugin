package etomica.plugin.editors;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.data.DataPump;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;
import etomica.simulation.Simulation;

public class SimulationRegister {

    public SimulationRegister(Simulation sim) {
        simulation = sim;
    }

    public void registerElements() {
        registerElements(simulation);
    }

    public void registerElements(Object obj) {
        if (obj instanceof Simulation) {
            registerElements(((Simulation)obj).getController());
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
            simulation.register((Integrator)obj);
            IntegratorIntervalListener[] listeners = ((Integrator)obj).getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                registerElements(listeners[i]);
            }
        }
        if (obj instanceof IntervalActionAdapter) {
            registerElements(((IntervalActionAdapter)obj).getAction());
        }
        if (obj instanceof DataPump) {
            if (((DataPump)obj).getDataSource() != null) {
                simulation.register(((DataPump)obj).getDataSource(),obj);
            }
        }
    }
    
    private final Simulation simulation;
}
