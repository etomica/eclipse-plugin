package etomica.plugin.wrappers;

import etomica.action.ActionGroup;

public class ActionGroupWrapper extends PropertySourceWrapper {

    public ActionGroupWrapper(ActionGroup object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((ActionGroup)object).getAllActions());
    }
}
