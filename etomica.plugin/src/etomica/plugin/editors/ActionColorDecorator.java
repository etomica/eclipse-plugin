/**
 * 
 */
package etomica.plugin.editors;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.internal.decorators.DecoratorManager;

import etomica.action.Action;
import etomica.action.activity.Controller;
import etomica.action.activity.ControllerEvent;
import etomica.action.activity.ControllerListener;
import etomica.action.activity.Controller.ActionStatus;

/**
 * This informs the ActionViewer how to color the Controller's actions
 */
public class ActionColorDecorator extends DecoratorManager implements ControllerListener {
    public ActionColorDecorator(Controller controller) {
        this.controller = controller;
        controller.addListener( this );
    }
    
    public Color decorateForeground(Object element) {
        Controller.ActionStatus status = controller.getActionStatus((Action)element);
        if (status == ActionStatus.CURRENT) {
            return red;
        }
        if (status == ActionStatus.PENDING) {
            return black;
        }
        // must be COMPLETED
        return green;
    }
    
    public Color decorateBackground(Object element) {
        Controller.ActionStatus status = controller.getActionStatus((Action)element);
        if (status == ActionStatus.COMPLETED || status == ActionStatus.CURRENT || status == ActionStatus.PENDING) {
            return white;
        }
        if (status == ActionStatus.STOPPED) {
            return yellow;
        }
        // must be FAILED
        return red;
    }
    
    public void actionPerformed(ControllerEvent event) {
        ControllerEvent.Type type = event.getType();
        // if an action's color might have changed, notify the listeners
        if ( type==ControllerEvent.START_ACTION ||
             type==ControllerEvent.END_ACTION ||
             type==ControllerEvent.RESET) {
            // we really want to do this, but the method is private!
            //fireListenersInUIThread(new LabelProviderChangedEvent(this));
            // this gets the job done
            updateForEnablementChange();
        }
    }
    
    private final Color black = new Color(null,0,0,0);
    private final Color green = new Color(null,0,255,0);
    private final Color white = new Color(null,255,255,255);
    private final Color red = new Color(null,255,0,0);
    private final Color yellow = new Color(null,255,255,0);
    private final Controller controller;
}