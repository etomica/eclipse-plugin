package etomica.plugin.wrappers;

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
}
