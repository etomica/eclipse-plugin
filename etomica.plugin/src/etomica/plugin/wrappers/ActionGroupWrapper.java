package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.plugin.wizards.NewActionWizard;
import etomica.plugin.wizards.NewIntegratorWizard;
import etomica.simulation.Simulation;

public class ActionGroupWrapper extends PropertySourceWrapper {

    public ActionGroupWrapper(ActionGroup object, Simulation sim) {
        super(object,sim);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((ActionGroup)object).getAllActions(),
                simulation, etomicaEditor);
    }
    
    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (!(obj instanceof Action)) {
            return false;
        }
        return ((ActionGroup)object).removeAction((Action)obj);
    }
    
    public Class[] getAdders() {
        Class[] actionClasses;
        if (object instanceof ActivityGroup) {
            actionClasses = new Class[2];
            actionClasses[1] = ActivityIntegrate.class;
        }
        else {
            actionClasses = new Class[1];
        }
        actionClasses[0] = Action.class;
        return actionClasses;
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == Action.class) {
            NewActionWizard wizard = new NewActionWizard((ActionGroup)object);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        if (object instanceof ActivityGroup && newObjectClass == ActivityIntegrate.class) {
            NewIntegratorWizard wizard = new NewIntegratorWizard((ActionGroup)object,sim);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }

    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        Action[] actions = ((ActionGroup)object).getAllActions();
        for (int i=0; i<actions.length; i++) {
            if (actions[i] == obj) {
                return true;
            }
        }
        return false;
    }

}
