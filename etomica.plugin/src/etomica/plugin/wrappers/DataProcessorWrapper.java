package etomica.plugin.wrappers;

import etomica.data.DataPipeForked;
import etomica.data.DataProcessor;
import etomica.data.DataSink;

public class DataProcessorWrapper extends PropertySourceWrapper {

    public DataProcessorWrapper(DataProcessor object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        DataSink sink = ((DataProcessor)object).getDataSink();
        if (sink == null) {
            return new PropertySourceWrapper[0];
        }
        return new PropertySourceWrapper[]{PropertySourceWrapper.makeWrapper(sink)};
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
        return (child instanceof DataSink);
    }
}
