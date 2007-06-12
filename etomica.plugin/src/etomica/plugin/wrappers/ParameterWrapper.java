package etomica.plugin.wrappers;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.plugin.editors.SimulationObjects;
import etomica.util.ParameterBase;

/**
 * To be functional, there must exist an actual class for Parameters -- it
 * can't just be an Object
 */
public class ParameterWrapper extends PropertySourceWrapper {

    /**
     * Make constructors private to cripple PropertySourceWrapper's reflection
     * @param object
     */
    public ParameterWrapper(ParameterBase object) {
        this(object,null);
    }

    public ParameterWrapper(ParameterBase object, SimulationObjects simObjects) {
        super(object,simObjects);
    }
    
    
    protected IPropertyDescriptor[] generateDescriptors() {
        //we just want the fields (parameters)
        Field[] fields = object.getClass().getFields();
        //loop through fields and generate descriptors
        LinkedList list = new LinkedList();
        for (int i = 0; i < fields.length; i++) {
            IPropertyDescriptor pd = makeDescriptor(fields[i],null,fields[i].getType(),fields[i].getName(),simObjects);
            if(pd != null) list.add(pd);
        }
        
        //make array of descriptors from list
        return (IPropertyDescriptor[])list.toArray(new IPropertyDescriptor[list.size()]);
    }

    public Object getPropertyValue(Object key) {
        if (!(key instanceof Field)) {
            return null;
        }
        Field field = (Field)key;
        try {
            Object value = field.get(object);
            if (value != null && value.getClass().isArray()) {
                return PropertySourceWrapper.makeWrapper(value, simObjects, etomicaEditor);
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
