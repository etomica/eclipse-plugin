package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class BooleanArrayWrapper extends PropertySourceWrapper {

    public BooleanArrayWrapper(boolean[] obj) {
        super(obj);
        setDisplayName("boolean array");
    }

    protected IPropertyDescriptor[] generateDescriptors() {
        // don't call superclass method since we don't care about the actual array props
        boolean[] booleanArray = (boolean[])object;
        //Introspection to get array of all properties
        IPropertyDescriptor[] newDescriptors = new IPropertyDescriptor[booleanArray.length];
        for (int i=0; i<booleanArray.length; i++) {
            newDescriptors[i] = makeDescriptor(new Integer(i),new Boolean(((boolean[])object)[i]),boolean.class,Integer.toString(i),simObjects);
        }
        return newDescriptors;
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Boolean(((boolean[])object)[index]);
    }

    public void setPropertyValue(Object key, Object value) {
        int index = ((Integer)key).intValue();
        ((boolean[])object)[index] = ((Boolean)value).booleanValue();
        if (etomicaEditor != null) {
            etomicaEditor.markDirty();
        }
    }
}
