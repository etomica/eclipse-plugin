package etomica.plugin.wrappers;

import etomica.data.DataProcessor;
import etomica.data.DataSink;
import etomica.simulation.Simulation;

public class DataProcessorWrapper extends PropertySourceWrapper {

    public DataProcessorWrapper(DataProcessor object, Simulation sim) {
        super(object,sim);
    }

    public PropertySourceWrapper[] getChildren() {
        DataSink sink = ((DataProcessor)object).getDataSink();
        if (sink == null) {
            return new PropertySourceWrapper[0];
        }
        return new PropertySourceWrapper[]{PropertySourceWrapper.makeWrapper(sink,simulation)};
    }

    public boolean removeChild(Object child) {
        if (!(child instanceof DataSink)) {
            return false;
        }
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (((DataProcessor)object).getDataSink() == child) {
            ((DataProcessor)object).setDataSink(null);
        }
        return true;
    }
    
    public boolean canRemoveChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        return (((DataProcessor)object).getDataSink() == child);
    }
}
