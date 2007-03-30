package etomica.plugin.wrappers;

import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;

public class DataStreamWrapper extends PropertySourceWrapper {

    public DataStreamWrapper(DataStreamHeader object, Simulation sim) {
        super(object,sim);
    }

    public String toString() {
        // pretend we're the data source
        return ((DataStreamHeader)object).getDataSource().getDataInfo().getLabel()+" stream";
    }
}
