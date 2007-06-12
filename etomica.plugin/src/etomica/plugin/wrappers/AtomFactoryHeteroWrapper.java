package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomFactory;
import etomica.atom.AtomFactoryHetero;
import etomica.atom.AtomFactoryMono;
import etomica.atom.AtomFactoryMonoDynamic;
import etomica.atom.AtomTypeGroup;
import etomica.atom.AtomTypeSphere;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;

public class AtomFactoryHeteroWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public AtomFactoryHeteroWrapper(AtomFactoryHetero object, SimulationObjects simObjects) {
        super(object,simObjects);
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

    public boolean addObjectClass(Class newObjectClass, Shell shell) {
        if (newObjectClass == AtomFactoryMono.class) {
            AtomTypeSphere leafType = new AtomTypeSphere(simObjects.simulation);
            leafType.setParentType((AtomTypeGroup)((AtomFactory)object).getType());
            AtomFactoryMono childFactory;
            if (simObjects.simulation.isDynamic()) {
                childFactory = new AtomFactoryMonoDynamic(simObjects.simulation.getSpace(),leafType);
            }
            else {
                childFactory = new AtomFactoryMono(simObjects.simulation.getSpace(),leafType);
            }
            ((AtomFactoryHetero)object).addChildFactory(childFactory);
        }
        return false;
    }
}
