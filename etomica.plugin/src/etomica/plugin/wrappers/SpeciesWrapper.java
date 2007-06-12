package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.plugin.editors.SimulationObjects;
import etomica.species.Species;
import etomica.species.SpeciesSignature;

public class SpeciesWrapper extends PropertySourceWrapper {

    public SpeciesWrapper(Species species, SimulationObjects simObjects) {
        super(species,simObjects);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        if (property.getPropertyType() == SpeciesSignature.class) {
            return null;
        }
        
        return super.makeDescriptor(property);
    }
}
