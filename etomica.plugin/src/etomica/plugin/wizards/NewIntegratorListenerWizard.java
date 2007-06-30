package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.action.Action;
import etomica.action.Activity;
import etomica.action.AtomAction;
import etomica.action.IntegratorAction;
import etomica.action.BoxAction;
import etomica.action.SimulationAction;
import etomica.data.DataPump;
import etomica.data.DataSource;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorBox;
import etomica.nbr.list.PotentialMasterList;
import etomica.box.Box;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;

/**
 * This wizard allows the user to create a new IntegratorIntervalListener.  
 * The user can choose the Action class and it is wrapped in an 
 * IntervalActionAdapter and added to the given Integrator.  Alternatively,
 * the user can also select a NeighborListManager or a DataPump from a 
 * DataStream registered with the simulation.
 */
public class NewIntegratorListenerWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewIntegratorListenerWizard(Integrator integrator, SimulationObjects simObjects) {
        super();
        this.integrator = integrator;
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        intervalListenerPage = new NewObjectSimplePage(this,simObjects,"Integrator Listener");
        addPage(intervalListenerPage);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.addBaseClass(Action.class);
        selector.addCategory("Action",Action.class);
        selector.addCategory("Phase Action",BoxAction.class);
        selector.addCategory("Integrator Action",IntegratorAction.class);
        selector.addCategory("Simulation Action",SimulationAction.class);
        selector.setExcludedClasses(new Class[]{AtomAction.class,Activity.class});
        selector.setExtraParameterClasses(new Class[]{Integrator.class});
        
        if (integrator instanceof IntegratorBox && ((IntegratorBox)integrator).getPotential() instanceof PotentialMasterList) {
            Box[] boxes = simObjects.simulation.getBoxs();
            for (int i=0; i<boxes.length; i++) {
                selector.addExtraObject("Neighbor Manager",
                    ((PotentialMasterList)((IntegratorBox)integrator).getPotential()).getNeighborManager(boxes[i]));
            }
        }
        
        for (int i=0; i<simObjects.dataStreams.size(); i++) {
            DataPump dataStreamPump = (DataPump)simObjects.dataStreams.get(i);
            boolean streamAlreadyAdded = false;
            boolean listenerAlreadyAdded = false;
            Action[] listeners = integrator.getIntervalActions();
            for (int j=0; j<listeners.length; j++) {
                if (listeners[j] == dataStreamPump) {
                    streamAlreadyAdded = true;
                }
            }
            
            if (!streamAlreadyAdded) {
                DataSource dataSource = dataStreamPump.getDataSource();
                String string = dataSource.getDataInfo().getLabel();
                selector.addExtraObject(string+" stream",dataStreamPump);
            }
        }
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        Action listener = null;
        Object obj = intervalListenerPage.createObject();
        
        if (obj==null || !(obj instanceof Action))
            return false;

        listener = (Action)obj;
        
        if (integrator != null) {
            integrator.addIntervalAction(listener);
        }
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Integrator integrator;
    private final SimulationObjects simObjects;
    private NewObjectSimplePage intervalListenerPage;
    private boolean success = false;
}