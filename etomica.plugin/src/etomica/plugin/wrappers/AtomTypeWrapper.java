package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.AtomType;
import etomica.plugin.editors.SimulationObjects;

public class AtomTypeWrapper extends PropertySourceWrapper {

    public AtomTypeWrapper(AtomType object, SimulationObjects simObjects) {
        super(object,simObjects);
    }
    
    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        String name = property.getDisplayName();  //Localized display name
        if(name.equals("indexManager") || name.equals("speciesIndex")) {
            return null;
        }
        
        return super.makeDescriptor(property);
    }
    
    public boolean isChildExcluded(IPropertyDescriptor descriptor, PropertySourceWrapper childWrapper, Object child) {
        if (descriptor.getDisplayName().equals("parentType")) {
            // recursion check usually excludes parentType, but sometimes
            // something holds a child AtomType directly, defeating the
            // recursion check
            return true;
        }
        return super.isChildExcluded(descriptor, childWrapper, child);
    }

}
