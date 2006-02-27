package etomica.plugin.wrappers;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.simulation.Simulation;
import etomica.util.Default;

public class ParamObjectWrapper extends PropertySourceWrapper {

    public ParamObjectWrapper(Object object) {
        this(object,null);
    }

    public ParamObjectWrapper(Object object, Simulation sim) {
        super(object,sim);
        setDisplayName("Input parameter");
    }
    
    
    protected void generateDescriptors() {
        Field[] fields = object.getClass().getFields();
        //loop through fields and generate descriptors
        LinkedList list = new LinkedList();
        for (int i = 0; i < fields.length; i++) {
            IPropertyDescriptor pd = makeDescriptor(fields[i],null,fields[i].getType(),fields[i].getName());
            if(pd != null) list.add(pd);
        }
        
        //make array of descriptors from list
        descriptors = (IPropertyDescriptor[])list.toArray(new IPropertyDescriptor[list.size()]);
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof Field)) {
            return null;
        }
        Field field = (Field)key;
        try {
            Object value = field.get(object);
            if (value != null && value.getClass().isArray()) {
                return PropertySourceWrapper.makeWrapper(value, simulation, etomicaEditor);
            }
            return value;
        }
        catch (IllegalAccessException e) {
            System.err.println("illegal access exception trying to get "+field.getName()+" from parameter object "+object);
            e.printStackTrace();
        }
        return null;
    }

    public void setPropertyValue(Object arg0, Object arg1) {
        Field field = (Field)arg0;
        try {
            field.set(object,arg1);
            if (etomicaEditor != null) {
                etomicaEditor.markDirty();
            }
            return;
        }
        catch (IllegalAccessException e) {
            System.err.println("illegal access exception trying to set "+field.getName()+" from parameter object "+object);
            e.printStackTrace();
        }
        return;
    }
}
