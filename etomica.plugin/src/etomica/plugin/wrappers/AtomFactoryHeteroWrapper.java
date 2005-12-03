package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomFactory;
import etomica.atom.AtomFactoryHetero;
import etomica.atom.AtomFactoryMono;
import etomica.atom.AtomTypeGroup;
import etomica.atom.AtomTypeSphere;
import etomica.atom.iterator.AtomIteratorListSimple;
import etomica.simulation.Simulation;
import etomica.space.CoordinateFactorySphere;

public class AtomFactoryHeteroWrapper extends PropertySourceWrapper {

    public AtomFactoryHeteroWrapper(AtomFactoryHetero object, Simulation sim) {
        super(object,sim);
    }
    
    public boolean removeChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (child instanceof AtomFactory) {
            return ((AtomFactoryHetero)object).removeChildFactory((AtomFactory)child);
        }
        return false;
    }
    
    public boolean canRemoveChild(Object child) {
        if (child instanceof PropertySourceWrapper) {
            child = ((PropertySourceWrapper)child).getObject();
        }
        if (child instanceof AtomFactory) {
            AtomFactory[] childFactories = ((AtomFactoryHetero)object).getChildFactory();
            for (int i=0; i<childFactories.length; i++) {
                if (childFactories[i] == child) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Class[] getAdders() {
        return new Class[]{AtomFactoryMono.class};
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == AtomFactoryMono.class) {
            AtomTypeSphere leafType = new AtomTypeSphere(sim,(AtomTypeGroup)((AtomFactory)object).getType());
            AtomFactoryMono childFactory = new AtomFactoryMono(new CoordinateFactorySphere(sim),leafType,sim.potentialMaster.sequencerFactory());
            ((AtomFactoryHetero)object).addChildFactory(childFactory);
        }
        return false;
    }

    private static final AtomIteratorListSimple iterator = new AtomIteratorListSimple();
}
