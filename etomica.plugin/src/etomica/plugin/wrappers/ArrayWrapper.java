package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.simulation.Simulation;

public class ArrayWrapper extends PropertySourceWrapper {

    public ArrayWrapper(Object[] obj, Simulation sim) {
        super(obj,sim);
        setDisplayName(object.getClass().getComponentType().getSimpleName()+" array");
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements((Object[])object,simulation);
    }

    public Object getEditableValue() {
        return this;
    }

    protected void generateDescriptors() {
        Object[] objArray = (Object[])object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[objArray.length];
        for (int i=0; i<objArray.length; i++) {
            descriptors[i] = super.makeDescriptor(new Integer(i),((Object[])object)[i],object.getClass().getComponentType(),Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        Object value = ((Object[])object)[index];
        if (value != null && value.getClass().isArray()) {
            return PropertySourceWrapper.makeWrapper(value,simulation);
        }
        return value;
    }
    
    public void setPropertyValue(Object key, Object value) {
        int index = ((Integer)key).intValue();
        ((Object[])object)[index] = value;
    }

}
