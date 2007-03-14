package etomica.plugin.wrappers;

import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.action.activity.ActivityIntegrate;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.simulation.Simulation;

public class ActivityIntegrateWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public ActivityIntegrateWrapper(ActivityIntegrate object, Simulation sim) {
        super(object,sim);
        integratorWrapper = (IntegratorWrapper)PropertySourceWrapper.makeWrapper(object.getIntegrator(),sim);
        setDisplayName(integratorWrapper.toString());
        descriptorHash = new HashMap();
    }
    
    public void setEditor(EtomicaEditor editor) {
        super.setEditor(editor);
        integratorWrapper.setEditor(editor);
    }

    protected IPropertyDescriptor[] generateDescriptors() {
        // get our own bean descriptors
        IPropertyDescriptor[] activityDescriptors = super.generateDescriptors();
        
        for (int i=0; i<activityDescriptors.length; i++) {
            descriptorHash.put(activityDescriptors[i].getId(),activityDescriptors[i].getId());
        }

        // now grab the integrators and combine them in one big array
        int nActivityDescriptors = activityDescriptors.length;
        IPropertyDescriptor[] integratorDescriptors = integratorWrapper.getPropertyDescriptors();
        IPropertyDescriptor[] allDescriptors = new IPropertyDescriptor[activityDescriptors.length+integratorDescriptors.length];
        System.arraycopy(activityDescriptors,0,allDescriptors,0,nActivityDescriptors);
        System.arraycopy(integratorDescriptors,0,allDescriptors,nActivityDescriptors,allDescriptors.length-nActivityDescriptors);

        return allDescriptors;
    }
    
    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        if(property.getDisplayName().equals("integrator")) return null;//skip the integrator since we're pretending to be it
        return super.makeDescriptor(property);
    }


    public Object getPropertyValue(Object key) {

        if (descriptorHash.get(key) != null) {
            return super.getPropertyValue(key);
        }
        return integratorWrapper.getPropertyValue(key);
    }

    public void setPropertyValue(Object key, Object value) {
        if (descriptorHash.get(key) != null) {
            super.setPropertyValue(key, value);
            return;
        }
        integratorWrapper.setPropertyValue(key,value);
//        if (etomicaEditor != null) {
//            etomicaEditor.getInnerPanel().getViewer().refresh(this);
//        }
    }

    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        // we have no menu items of our own.  just reuturn the integratorWrapper's menu items.
        MenuItemWrapper[] integratorWrappers = integratorWrapper.getMenuItemWrappers(parentWrapper);
        
        return PropertySourceWrapper.combineMenuItemWrappers(
                integratorWrappers, super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        return integratorWrapper.addObjectClass(sim,newObjectClass,shell);
    }
    
    public boolean canRemoveChild(Object obj) {
        return integratorWrapper.canRemoveChild(obj);
    }
    
    public boolean removeChild(Object obj) {
        return integratorWrapper.removeChild(obj);
    }
    
    public EtomicaStatus getStatus(LinkedList parentList) {
        EtomicaStatus superStatus = super.getStatus(parentList);
        if (superStatus.type != EtomicaStatus.OK) {
            return superStatus;
        }
        return integratorWrapper.getStatus(parentList);
    }
    
    protected IntegratorWrapper integratorWrapper;
    protected final HashMap descriptorHash;
}
