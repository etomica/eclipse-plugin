package etomica.plugin.wrappers;

import etomica.atom.IAtom;
import etomica.atom.IAtomGroup;
import etomica.atom.SpeciesAgent;
import etomica.plugin.editors.SimulationObjects;

public class SpeciesAgentWrapper extends PropertySourceWrapper implements RemoverWrapper {

    public SpeciesAgentWrapper(SpeciesAgent object, SimulationObjects simObjects) {
        super(object,simObjects);
        // SpeciesAgent extends AtomGroup, which implements IAtomGroup, so we
        // won't automagically pick up an IAtomGroupWrapper.  So add it
        // explicitly
        addInterfaceWrapper(new IAtomGroupWrapper(object, simObjects));
    }
    
    // allow individual molecules to be removed.  molecules should be added via
    // the property sheet for the SpeciesAgent (setNMolecules)
    public boolean removeChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (child instanceof IAtom && ((IAtom)child).getParentGroup() == object) {
            ((IAtomGroup)object).removeChildAtom((IAtom)child);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object child) {
        if (child instanceof IAtom && ((IAtom)child).getParentGroup() == object) {
            return true;
        }
        return false;
    }
}
