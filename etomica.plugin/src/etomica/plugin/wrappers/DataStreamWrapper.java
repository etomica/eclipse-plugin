package etomica.plugin.wrappers;

import etomica.data.DataPump;
import etomica.simulation.DataStreamHeader;
import etomica.util.Arrays;

public class DataStreamWrapper extends PropertySourceWrapper {

    public DataStreamWrapper(DataStreamHeader object) {
        super(object);
        // TODO Auto-generated constructor stub
    }

    public String toString() {
        // pretend we're the data source
        return ((DataStreamHeader)object).getDataSource().toString();
    }
    
    public PropertySourceWrapper[] getChildren() {
        Object[] clients = ((DataStreamHeader)object).getClients();
        PropertySourceWrapper[] wrappers = new PropertySourceWrapper[0];
        for (int i=0; i<clients.length; i++) {
            if (clients[i] instanceof DataPump) {
                wrappers = (PropertySourceWrapper[])Arrays.addObject(wrappers,
                        PropertySourceWrapper.makeWrapper(((DataPump)clients[i]).getDataSink()));
            }
        }
        return wrappers;
    }
}
