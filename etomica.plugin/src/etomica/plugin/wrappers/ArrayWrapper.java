package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public class ArrayWrapper extends PropertySourceWrapper {

    public ArrayWrapper(Object[] obj) {
        super(obj);
        setDisplayName(object.getClass().getComponentType().getSimpleName()+" array");
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements((Object[])object);
    }

    public Object getEditableValue() {
        return this;
    }

    protected void generateDescriptors() {
        Object[] objArray = (Object[])object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[objArray.length];
        for (int i=0; i<objArray.length; i++) {
            descriptors[i] = new org.eclipse.ui.views.properties.PropertyDescriptor(new Integer(i),Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        Object value = ((Object[])object)[index];
        if (value != null && value.getClass().isArray()) {
            return PropertySourceWrapper.makeWrapper(value);
        }
        return value;
    }
}
