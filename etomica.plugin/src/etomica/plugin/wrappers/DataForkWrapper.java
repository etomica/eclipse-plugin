package etomica.plugin.wrappers;

import etomica.data.DataPipeForked;
import etomica.data.DataSink;

public class DataForkWrapper extends PropertySourceWrapper {

    public DataForkWrapper(DataPipeForked object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        DataPipeForked fork = (DataPipeForked)object;
        PropertySourceWrapper[] wrappers = new PropertySourceWrapper[fork.getDataSinkCount()];
        for (int i=0; i<wrappers.length; i++) {
            wrappers[i] = PropertySourceWrapper.makeWrapper(fork.getDataSink(i));
        }
        return wrappers;
    }
    
    public boolean removeChild(Object child) {
        if (!(child instanceof DataSink)) {
            return false;
        }
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        ((DataPipeForked)object).removeDataSink((DataSink)child);
        return true;
    }
    
    public boolean canRemoveChild(Object child) {
        return (child instanceof DataSink);
    }
}
