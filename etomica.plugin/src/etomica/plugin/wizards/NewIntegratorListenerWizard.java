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
import etomica.integrator.IntegratorListener;
import etomica.integrator.IntegratorPhase;
import etomica.integrator.IntervalActionAdapter;
import etomica.nbr.list.PotentialMasterList;
import etomica.phase.Phase;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.DataStreamHeader;

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
        selector.addBaseClass(IntegratorListener.class);
        selector.addCategory("Integrator Listener",IntegratorListener.class);
        selector.addBaseClass(Action.class);
        selector.addCategory("Action",Action.class);
        selector.addCategory("Phase Action",PhaseAction.class);
        selector.addCategory("Integrator Action",IntegratorAction.class);
        selector.addCategory("Simulation Action",SimulationAction.class);
        selector.setExcludedClasses(new Class[]{AtomAction.class,Activity.class});
        selector.setExtraParameterClasses(new Class[]{Integrator.class});
        
        if (integrator instanceof IntegratorPhase && ((IntegratorPhase)integrator).getPotential() instanceof PotentialMasterList) {
            Phase[] phases = simObjects.simulation.getPhases();
            for (int i=0; i<phases.length; i++) {
                selector.addExtraObject("Neighbor Manager",
                    ((PotentialMasterList)((IntegratorPhase)integrator).getPotential()).getNeighborManager(phases[i]));
            }
        }
        
        for (int i=0; i<simObjects.dataStreams.size(); i++) {
            DataStreamHeader dataStream = (DataStreamHeader)simObjects.dataStreams.get(i);
            if (!(dataStream.getClient() instanceof DataPump)) {
                continue;
            }
            boolean streamAlreadyAdded = false;
            boolean listenerAlreadyAdded = false;
            IntegratorIntervalListener[] listeners = integrator.getIntervalListeners();
            for (int j=0; j<listeners.length; j++) {
                if (listeners[j] instanceof IntervalActionAdapter) {
                    if (((IntervalActionAdapter)listeners[j]).getAction() == dataStream.getClient()) {
                        streamAlreadyAdded = true;
                    }
                }
                else if (listeners[j] == dataStream.getDataSource()) {
                    listenerAlreadyAdded = true;
                }
            }
            
            if (!streamAlreadyAdded) {
                DataSource dataSource = dataStream.getDataSource();
                String string = dataSource.getDataInfo().getLabel();
                selector.addExtraObject(string+" stream",new IntervalActionAdapter((DataPump)dataStream.getClient()));
            }
            if (!listenerAlreadyAdded && dataStream.getDataSource() instanceof IntegratorIntervalListener) {
                selector.addExtraObject(null,dataStream.getDataSource());
            }
        }
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
    private final SimulationObjects simObjects;
    private NewObjectSimplePage intervalListenerPage;
    private boolean success = false;
}