package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.simulation.Simulation;
import etomica.species.Species;
import etomica.species.SpeciesSignature;

public class SpeciesWrapper extends PropertySourceWrapper {

    public SpeciesWrapper(Species species, Simulation sim) {
        super(species,sim);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

        if (property.getPropertyType() == SpeciesSignature.class) {
            return null;
        }
        
        return super.makeDescriptor(property);
    }
}
