package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.data.DataSource;
import etomica.simulation.Simulation;

public class DataSourceWrapper extends PropertySourceWrapper {

    public DataSourceWrapper(DataSource object, Simulation sim) {
        super(object,sim);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        if (property.getName().startsWith("data") && !property.getName().equals("dataInfo")) {
            // exclude getData, getDataDouble, getDataAsScalar, maybe others
            return null;
        }
        return super.makeDescriptor(property);
    }
}
