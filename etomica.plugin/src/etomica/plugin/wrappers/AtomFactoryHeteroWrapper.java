package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomFactory;
import etomica.atom.AtomFactoryHetero;
import etomica.atom.AtomFactoryMono;
import etomica.atom.AtomFactoryMonoDynamic;
import etomica.atom.AtomTypeGroup;
import etomica.atom.AtomTypeSphere;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.simulation.Simulation;

public class AtomFactoryHeteroWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

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
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        AddItemWrapper addItemWrapper = new AddItemWrapper();

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(AtomFactoryMono.class, this));
        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{addItemWrapper}, super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == AtomFactoryMono.class) {
            AtomTypeSphere leafType = new AtomTypeSphere(sim);
            leafType.setParentType((AtomTypeGroup)((AtomFactory)object).getType());
            AtomFactoryMono childFactory;
            if (sim.isDynamic()) {
                childFactory = new AtomFactoryMonoDynamic(sim.getSpace(),leafType);
            }
            else {
                childFactory = new AtomFactoryMono(sim.getSpace(),leafType);
            }
            ((AtomFactoryHetero)object).addChildFactory(childFactory);
        }
        return false;
    }
}
