package etomica.plugin.wrappers;

import etomica.action.Action;
import etomica.data.DataAccumulator;
import etomica.simulation.Simulation;

public class DataAccumulatorWrapper extends PropertySourceWrapper {

    public DataAccumulatorWrapper(DataAccumulator object, Simulation sim) {
        super(object, sim);
    }
    
    public Action[] getActions() {
        return new Action[]{new ResetAction((DataAccumulator)object)};
    }
    
    protected static class ResetAction implements Action {
        public ResetAction(DataAccumulator accumulator) {
            this.accumulator = accumulator;
        }
        
        public void actionPerformed() {
            accumulator.reset();
        }
        
        public String getLabel() {
            return "Reset";
        }
        
        private final DataAccumulator accumulator;
    }

}
