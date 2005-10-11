package etomica.plugin.wrappers;

import etomica.data.DataPipeForked;

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
}
