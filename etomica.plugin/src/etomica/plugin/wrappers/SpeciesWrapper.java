package etomica.plugin.wrappers;

import etomica.species.Species;

public class SpeciesWrapper extends PropertySourceWrapper {

    public SpeciesWrapper(Species species) {
        super(species);
    }

    public PropertySourceWrapper[] getChildren() {
        return new PropertySourceWrapper[]{PropertySourceWrapper.makeWrapper(((Species)object).moleculeFactory())};
    }
}
