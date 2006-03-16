package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.action.Action;
import etomica.action.Activity;
import etomica.action.AtomAction;
import etomica.action.IntegratorAction;
import etomica.action.PhaseAction;
import etomica.action.SimulationAction;
import etomica.data.DataPump;
import etomica.data.DataSource;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;
import etomica.nbr.list.PotentialMasterList;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.util.Arrays;

/**
 * This wizard allows the user to create a new IntegratorIntervalListener.  
 * The user can choose the Action class and it is wrapped in an 
 * IntervalActionAdapter and added to the given Integrator.  Alternatively,
 * the user can also select a NeighborListManager or a DataPump from a 
 * DataStream registered with the simulation.
 */
public class NewIntervalListenerWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewIntervalListenerWizard(Integrator integrator, Simulation sim) {
        super();
        this.integrator = integrator;
        simulation = sim;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        intervalListenerPage = new NewObjectSimplePage(this,simulation,"Interval Listener");
        addPage(intervalListenerPage);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.addBaseClass(IntegratorIntervalListener.class);
        selector.addCategory("Interval Listener",IntegratorIntervalListener.class);
        selector.addBaseClass(Action.class);
        selector.addCategory("Action",Action.class);
        selector.addCategory("Phase Action",PhaseAction.class);
        selector.addCategory("Integrator Action",IntegratorAction.class);
        selector.addCategory("Simulation Action",SimulationAction.class);
        selector.setExcludedClasses(new Class[]{AtomAction.class,Activity.class});
        
        IntegratorIntervalListener[] extras = new IntegratorIntervalListener[0];
        if (simulation.potentialMaster instanceof PotentialMasterList) {
            extras = new IntegratorIntervalListener[]{((PotentialMasterList)simulation.potentialMaster).getNeighborManager()};
        }
        DataStreamHeader[] dataStreams = simulation.getDataStreams();
        for (int i=0; i<dataStreams.length; i++) {
            boolean alreadyAdded = false;
            if (!(dataStreams[i].getClient() instanceof DataPump)) {
                continue;
            }
            IntegratorIntervalListener[] listeners = integrator.getIntervalListeners();
            for (int j=0; j<listeners.length; j++) {
                if (listeners[j] instanceof IntervalActionAdapter) {
                    if (((IntervalActionAdapter)listeners[j]).getAction() == dataStreams[i].getClient()) {
                        alreadyAdded = true;
                        break;
                    }
                }
            }
            
            if (!alreadyAdded) {
                IntervalActionAdapter adapter = new IntervalActionAdapter((DataPump)dataStreams[i].getClient());
                extras = (IntegratorIntervalListener[])Arrays.addObject(extras,adapter);
            }
        }
        selector.setExtraObjects(extras);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        IntegratorIntervalListener listener = null;
        Object obj = intervalListenerPage.createObject();
        
        if (obj==null)
            return false;
        
        if (obj instanceof Action) {
            listener = new IntervalActionAdapter((Action)obj);
        }
        else if (!(obj instanceof IntegratorIntervalListener)) {
            return false;
        }
        else {
            listener = (IntegratorIntervalListener)obj;
            if (obj instanceof DataSource) {
                // we don't need the pump here, but it will probably be 
                // needed somewhere, and it's required in order for us 
                // to consider this a data stream
                DataPump pump = new DataPump((DataSource)obj,null);
                simulation.register((DataSource)obj,pump);
            }
        }
        
        if (integrator != null) {
            integrator.addListener(listener);
        }
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Integrator integrator;
    private final Simulation simulation;
    private NewObjectSimplePage intervalListenerPage;
    private boolean success = false;
}