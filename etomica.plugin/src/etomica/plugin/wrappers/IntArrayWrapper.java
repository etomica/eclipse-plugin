package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public class IntArrayWrapper extends PropertySourceWrapper {

    public IntArrayWrapper(int[] obj) {
        super(obj);
        setDisplayName("integer array");
    }

    public Object getEditableValue() {
        return this;
    }

    protected void generateDescriptors() {
        int[] intArray = (int[])object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[intArray.length];
        for (int i=0; i<intArray.length; i++) {
            descriptors[i] = makeDescriptor(new Integer(i),new Integer(((int[])object)[i]),int.class,Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Integer(((int[])object)[index]);
    }
    
    public void setPropertyValue(Object key, Object value) {
        int index = ((Integer)key).intValue();
        ((int[])object)[index] = ((Integer)value).intValue();
        if (etomicaEditor != null) {
            etomicaEditor.markDirty();
        }
    }
}
