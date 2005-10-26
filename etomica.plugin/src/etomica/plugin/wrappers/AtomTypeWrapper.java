package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.atom.AtomType;

public class AtomTypeWrapper extends PropertySourceWrapper {

    public AtomTypeWrapper(AtomType object) {
        super(object);
    }
    
    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        String name = property.getDisplayName();  //Localized display name
        if(name.equals("indexManager") || name.equals("speciesIndex")) {
            return null;
        }
        
        return super.makeDescriptor(property);
    }

}
