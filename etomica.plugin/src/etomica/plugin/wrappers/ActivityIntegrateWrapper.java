package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.action.activity.ActivityIntegrate;
import etomica.integrator.IntegratorPhase;
import etomica.plugin.editors.EtomicaEditor;
import etomica.simulation.Simulation;

public class ActivityIntegrateWrapper extends PropertySourceWrapper {

    public ActivityIntegrateWrapper(ActivityIntegrate object, Simulation sim) {
        super(object,sim);
        integratorWrapper = (IntegratorWrapper)PropertySourceWrapper.makeWrapper(object.getIntegrator(),sim);
        setDisplayName(integratorWrapper.toString());
    }
    
    public void setEditor(EtomicaEditor editor) {
        super.setEditor(editor);
        integratorWrapper.setEditor(editor);
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (descriptors == null) {
            generateDescriptors();
            nActivityDescriptors = descriptors.length;
            IPropertyDescriptor[] integratorDescriptors = integratorWrapper.getPropertyDescriptors();
            IPropertyDescriptor[] allDescriptors = new IPropertyDescriptor[descriptors.length+integratorDescriptors.length];
            System.arraycopy(descriptors,0,allDescriptors,0,nActivityDescriptors);
            System.arraycopy(integratorDescriptors,0,allDescriptors,nActivityDescriptors,allDescriptors.length-nActivityDescriptors);
            descriptors = allDescriptors;
        }
        return descriptors;
    }
    
    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        String name = property.getDisplayName();  //Localized display name 
        if(name.equals("integrator")) return null;//skip the integrator since we're pretending to be it
        return super.makeDescriptor(property);
    }


    public Object getPropertyValue(Object key) {
        java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)key;
        for (int i=0; i<descriptors.length; i++) {
            if (pd == descriptors[i].getId()) {
                if (i < nActivityDescriptors) {
                    return super.getPropertyValue(key);
                }
                return integratorWrapper.getPropertyValue(key);
            }
        }
        // couldn't find the property descriptor in our own list.  :(
        return null;
    }

    public void setPropertyValue(Object key, Object arg1) {
        java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)key;
        for (int i=0; i<descriptors.length; i++) {
            if (pd == descriptors[i].getId()) {
                if (i < nActivityDescriptors) {
                    super.setPropertyValue(key,arg1);
                    return;
                }
                integratorWrapper.setPropertyValue(key,arg1);
                return;
            }
        }
        // couldn't find the property descriptor in our own list.  :(
    }

    public Class[] getAdders() {
        return integratorWrapper.getAdders();
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        return integratorWrapper.addObjectClass(sim,newObjectClass,shell);
    }
    
    public EtomicaStatus getStatus() {
        return integratorWrapper.getStatus();
    }
    
    private IntegratorWrapper integratorWrapper;
    private int nActivityDescriptors;
}
