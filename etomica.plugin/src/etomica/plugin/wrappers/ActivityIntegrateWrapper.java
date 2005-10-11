package etomica.plugin.wrappers;

import etomica.action.activity.ActivityIntegrate;

public class ActivityIntegrateWrapper extends PropertySourceWrapper {

    public ActivityIntegrateWrapper(ActivityIntegrate object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return new PropertySourceWrapper[]{
                PropertySourceWrapper.makeWrapper(((ActivityIntegrate)object).getIntegrator())};
    }
}
