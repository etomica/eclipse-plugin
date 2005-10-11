package etomica.plugin.editors;

import etomica.action.Action;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.data.DataPump;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;
import etomica.simulation.Simulation;

public class DataStreamRegister {

    public DataStreamRegister(Simulation sim) {
        simulation = sim;
    }

    public void registerDataStreams(Object obj) {
        if (obj instanceof Simulation) {
            registerDataStreams(((Simulation)obj).getController());
        }
        if (obj instanceof ActivityGroup) {
            Action[] actions = ((ActivityGroup)obj).getAllActions();
            for (int i=0; i<actions.length; i++) {
                registerDataStreams(actions[i]);
            }
        }
        if (obj instanceof ActivityIntegrate) {
            registerDataStreams(((ActivityIntegrate)obj).getIntegrator());
        }
        if (obj instanceof Integrator) {
            IntegratorIntervalListener[] listeners = ((Integrator)obj).getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                registerDataStreams(listeners[i]);
            }
        }
        if (obj instanceof IntervalActionAdapter) {
            registerDataStreams(((IntervalActionAdapter)obj).getAction());
        }
        if (obj instanceof DataPump) {
            if (((DataPump)obj).getDataSource() != null) {
                simulation.register(((DataPump)obj).getDataSource(),obj);
            }
        }
    }
    
    private final Simulation simulation;
}
