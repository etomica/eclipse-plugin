package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.simulation.Simulation;

public class ArrayWrapper extends PropertySourceWrapper {

    public ArrayWrapper(Object[] obj, Simulation sim) {
        super(obj,sim);
        setDisplayName(object.getClass().getComponentType().getName()+" array");
    }

    public boolean isChildExcluded(IPropertyDescriptor descriptor, PropertySourceWrapper childWrapper, Object child) {
        // If something decided we (the array) was their child, they're going
        // to get our elements.
        return false;
    }

    public Object getEditableValue() {
        return this;
    }

    protected IPropertyDescriptor[] generateDescriptors() {
        //don't call superclass method because we don't care about the actual array props
        Object[] objArray = (Object[])object;
        //Introspection to get array of all properties
        IPropertyDescriptor[] newDescriptors = new PropertyDescriptor[objArray.length];
        for (int i=0; i<objArray.length; i++) {
            newDescriptors[i] = PropertySourceWrapper.makeDescriptor(new Integer(i),((Object[])object)[i],
                    object.getClass().getComponentType(),Integer.toString(i), simulation);
        }
        return newDescriptors;
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        Object value = ((Object[])object)[index];
        if (value != null && value.getClass().isArray()) {
            return PropertySourceWrapper.makeWrapper(value, simulation, etomicaEditor);
        }
        return value;
    }
    
    public void setPropertyValue(Object key, Object value) {
        int index = ((Integer)key).intValue();
        ((Object[])object)[index] = value;
        if (etomicaEditor != null) {
            etomicaEditor.markDirty();
        }
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        
        MenuItemWrapper[] itemWrappers = new MenuItemWrapper[0];
        
        // only our parent can add more elements to us.  try that.
        MenuItemWrapper[] parentAddItems = parentWrapper.getMenuItemWrappers(null);
        for (int i=0; i<parentAddItems.length; i++) {
            if (parentAddItems[i] instanceof AddItemWrapper) {
                MenuItemWrapper[] addItems = ((MenuItemCascadeWrapper)parentAddItems[i]).getSubmenuWrapperItems();
                boolean found = false;
                for (int j=0; j<addItems.length; j++) {
                    if (((AddClassItemWrapper)addItems[j]).addClass == object.getClass().getComponentType()) {
                        itemWrappers = new MenuItemWrapper[]{new AddItemWrapper()};
                        // steal the one that relates to us, discard the rest
                        found = true;
                        ((AddItemWrapper)itemWrappers[0]).addSubmenuItem(addItems[j]);
                    }
                }
                if (found) break;
            }
        }
            
        return PropertySourceWrapper.combineMenuItemWrappers(
                itemWrappers, super.getMenuItemWrappers(parentWrapper));
    }
}
