package etomica.plugin.wrappers;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.ActionGroupSeries;
import etomica.action.activity.ActivityGroupParallel;
import etomica.action.activity.ActivityGroupSeries;

public class ActionGroupWrapper extends PropertySourceWrapper {

    public ActionGroupWrapper(ActionGroup object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((ActionGroup)object).getAllActions());
    }
    
    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (!(obj instanceof Action)) {
            return false;
        }
        if (object instanceof ActionGroupSeries) {
            ((ActionGroupSeries)object).removeAction((Action)obj);
            return true;
        }
        if (object instanceof ActivityGroupSeries) {
            ((ActivityGroupSeries)object).removeAction((Action)obj);
            return true;
        }
        if (object instanceof ActivityGroupParallel) {
            ((ActivityGroupParallel)object).removeAction((Action)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        Action[] actions = ((ActionGroup)object).getAllActions();
        for (int i=0; i<actions.length; i++) {
            if (actions[i] == obj) {
                return true;
            }
        }
        return false;
    }

}
