package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.activity.ActivityGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wizards.NewActionWizard;
import etomica.plugin.wizards.NewIntegratorWizard;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.simulation.Simulation;

public class ActionGroupWrapper extends InterfaceWrapper implements RemoverWrapper, AdderWrapper {

    public ActionGroupWrapper(ActionGroup object, Simulation sim) {
        super(object,sim);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (!(obj instanceof Action)) {
            return false;
        }
        boolean success = ((ActionGroup)object).removeAction((Action)obj);
        if (success && obj instanceof ActivityIntegrate) {
            simulation.unregister(((ActivityIntegrate)obj).getIntegrator());
        }
        return success;
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemCascadeWrapper addItemWrapper = new AddItemWrapper();
        
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Action.class, this));
        if (object instanceof ActivityGroup) {
            addItemWrapper.addSubmenuItem(new AddClassItemWrapper(ActivityIntegrate.class, this));
        }
        return new MenuItemWrapper[]{addItemWrapper};
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
        Action[] actions = ((ActionGroup)object).getAllActions();
        for (int i=0; i<actions.length; i++) {
            if (actions[i] == obj) {
                return true;
            }
        }
        return false;
    }

}
