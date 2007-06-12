package etomica.plugin.wrappers;

import etomica.action.Action;
import etomica.data.DataAccumulator;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;

public class DataAccumulatorWrapper extends PropertySourceWrapper {

    public DataAccumulatorWrapper(DataAccumulator object, SimulationObjects simObjects) {
        super(object, simObjects);
        addInterfaceWrapper(new DataSourceWrapper(object, simObjects));
    }
    
    public Action[] getActions() {
        return new Action[]{new ResetAction((DataAccumulator)object)};
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {

        
        MenuItemCascadeWrapper actionItemWrapper = new ActionListItemWrapper();
        actionItemWrapper.addSubmenuItem(new ActionItemWrapper(new ResetAction((DataAccumulator)object), "Restart"));
        
        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{actionItemWrapper}, 
                super.getMenuItemWrappers(parentWrapper));
    }
    
    protected static class ResetAction implements Action {
        public ResetAction(DataAccumulator accumulator) {
            this.accumulator = accumulator;
        }
        
        public void actionPerformed() {
            accumulator.reset();
        }
        
        public String getLabel() {
            return "Reset";
        }
        
        private final DataAccumulator accumulator;
    }
}
