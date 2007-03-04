package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class DoubleArrayWrapper extends PropertySourceWrapper {

    public DoubleArrayWrapper(double[] obj) {
        super(obj);
        setDisplayName("double array");
    }

    public Object getEditableValue() {
        return this;
    }

    protected IPropertyDescriptor[] generateDescriptors() {
        double[] doubleArray = (double[])object;
       //Introspection to get array of all properties
        IPropertyDescriptor[] newDescriptors = new IPropertyDescriptor[doubleArray.length];
        for (int i=0; i<doubleArray.length; i++) {
            newDescriptors[i] = makeDescriptor(new Integer(i),new Double(((double[])object)[i]),double.class,Integer.toString(i),simulation);
        }
        return newDescriptors;
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Double(((double[])object)[index]);
    }

    public void setPropertyValue(Object key, Object value) {
        int index = ((Integer)key).intValue();
        ((double[])object)[index] = ((Double)value).doubleValue();
        if (etomicaEditor != null) {
            etomicaEditor.markDirty();
        }
    }
}
