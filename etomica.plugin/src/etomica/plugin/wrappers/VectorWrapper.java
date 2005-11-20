package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.plugin.views.DecimalPropertyDescriptor;
import etomica.space.Vector;

public class VectorWrapper extends PropertySourceWrapper {

    public VectorWrapper(Vector obj) {
        super(obj);
        setDisplayName("Vector");
    }

    protected void generateDescriptors() {
        Vector vector = (Vector)object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[vector.D()];
        for (int i=0; i<descriptors.length; i++) {
            descriptors[i] = new DecimalPropertyDescriptor(new Integer(i),xyz[i]);
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Double(((Vector)object).x(index));
    }
    
    public void setPropertyValue(Object arg0, Object arg1) {
        int index = ((Integer)arg0).intValue();
        ((Vector)object).setX(index,((Double)arg1).doubleValue());
    }

    
    private static String[] xyz = new String[]{"x","y","z"};
}
