package etomica.plugin.wrappers;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.plugin.editors.SimulationObjects;
import etomica.util.Default;

public class DefaultWrapper extends PropertySourceWrapper {

    public DefaultWrapper(Default object, SimulationObjects simObjects) {
        super(object,simObjects);
        setDisplayName("Defaults");
    }
    
    protected IPropertyDescriptor[] generateDescriptors() {
        IPropertyDescriptor[] superDescriptors = super.generateDescriptors();
        Field[] fields = Default.class.getFields();
        //loop through fields and generate descriptors
        LinkedList list = new LinkedList();
        for (int i = 0; i < fields.length; i++) {
            IPropertyDescriptor pd = makeDescriptor(fields[i],null,fields[i].getType(),fields[i].getName(),simObjects);
            if(pd != null) list.add(pd);
        }
        
        //make array of descriptors from list
        IPropertyDescriptor[] newDescriptors = (IPropertyDescriptor[])list.toArray(new IPropertyDescriptor[superDescriptors.length+list.size()]);
        //combine field descriptors with getters from superclass
        System.arraycopy(superDescriptors,0,newDescriptors,list.size(),superDescriptors.length);
        return newDescriptors;
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof Field)) {
            return super.getPropertyValue(key);
        }
        Field field = (Field)key;
        try {
            Object value = field.get(object);
            if (value != null && value.getClass().isArray()) {
                return PropertySourceWrapper.makeWrapper(value, simObjects, etomicaEditor);
            }
            if (value instanceof Double) {
                value = getDisplayValue((Double)value, "get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1));
            }
            return value;
        }
        catch (IllegalAccessException e) {
            System.err.println("illegal access exception trying to get "+field.getName()+" from Default");
            e.printStackTrace();
        }
        return null;
    }

    public void setPropertyValue(Object key, Object value) {
        if (key instanceof java.beans.PropertyDescriptor) {
            super.setPropertyValue(key,value);
            return;
        }
        Field field = (Field)key;
        if (value instanceof Double) {
            value = getSimValue((Double)value, "get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1));
        }
        try {
            field.set(object,value);
            if (etomicaEditor != null) {
                etomicaEditor.markDirty();
            }
            return;
        }
        catch (IllegalAccessException e) {
            System.err.println("illegal access exception trying to set "+field.getName()+" from Default");
            e.printStackTrace();
        }
        return;
    }
}
