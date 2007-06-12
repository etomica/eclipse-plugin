package etomica.plugin.wrappers;

import etomica.data.DataProcessor;
import etomica.data.DataSink;
import etomica.plugin.editors.SimulationObjects;

public class DataProcessorWrapper extends PropertySourceWrapper implements RemoverWrapper {

    public DataProcessorWrapper(DataProcessor object, SimulationObjects simObjects) {
        super(object,simObjects);
    }
    
    // we allow removal of a data sink here.  DataSinks should be set via
    // the property sheet, which doesn't allow setting the data sink to null.
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
        return (((DataProcessor)object).getDataSink() == child);
    }
    
}
