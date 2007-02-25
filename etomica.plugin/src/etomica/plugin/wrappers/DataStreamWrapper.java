package etomica.plugin.wrappers;

import etomica.data.DataSource;
import etomica.data.meter.Meter;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;

public class DataStreamWrapper extends PropertySourceWrapper {

    public DataStreamWrapper(DataStreamHeader object, Simulation sim) {
        super(object,sim);
    }

    public String toString() {
        // pretend we're the data source
        DataSource dataSource = ((DataStreamHeader)object).getDataSource();
        if (dataSource instanceof Meter) {
            return ((Meter)dataSource).getName()+" stream";
        }
        return ((DataStreamHeader)object).getDataSource().getDataInfo().getLabel()+" stream";
    }
}
