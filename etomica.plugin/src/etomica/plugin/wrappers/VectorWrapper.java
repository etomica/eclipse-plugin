package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.views.DecimalPropertyDescriptor;
import etomica.space.IVector;

public class VectorWrapper extends InterfaceWrapper {

    public VectorWrapper(IVector obj, SimulationObjects simObjects) {
        super(obj, simObjects);
    }

    public IPropertyDescriptor[] generateDescriptors() {
        IVector vector = (IVector)object;

        IPropertyDescriptor[] descriptors = new IPropertyDescriptor[vector.getD()];
        for (int i=0; i<descriptors.length; i++) {
            descriptors[i] = new DecimalPropertyDescriptor(new Integer(i),xyz[i]);
        }
        return descriptors;
    }

    protected IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name) {
        // veto any introspected property -- d, naN, zero
        // perhaps it should be limited to those.
        return PropertySourceWrapper.PROPERTY_VETO;
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Double(((IVector)object).x(index));
    }
    
    public boolean setPropertyValue(Object arg0, Object arg1) {
        int index = ((Integer)arg0).intValue();
        ((IVector)object).setX(index,((Double)arg1).doubleValue());
        return true;
    }

    private static String[] xyz = new String[]{"x","y","z"};
}
