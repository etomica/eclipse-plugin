package etomica.plugin.wrappers;

import etomica.data.DataPump;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.util.Arrays;

public class DataStreamWrapper extends PropertySourceWrapper {

    public DataStreamWrapper(DataStreamHeader object, Simulation sim) {
        super(object,sim);
    }

    public String toString() {
        // pretend we're the data source
        return ((DataStreamHeader)object).getDataSource().toString();
    }
    
    public PropertySourceWrapper[] getChildren() {
        Object client = ((DataStreamHeader)object).getClient();
        PropertySourceWrapper[] wrappers = new PropertySourceWrapper[0];
        if (client instanceof DataPump) {
            wrappers = (PropertySourceWrapper[])Arrays.addObject(wrappers,
                    PropertySourceWrapper.makeWrapper(((DataPump)client).getDataSink(),simulation));
        }
        return wrappers;
    }
}
