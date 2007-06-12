package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.data.DataPipe;
import etomica.data.DataPipeForked;
import etomica.data.DataSink;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;

/**
 * This wizard allows the user to create a new DataSink.  The user can choose
 * the DataSink class and it is added as the DataSink for the DataPipe if given.
 */
public class NewDataSinkWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewDataSinkWizard(DataPipe parentPipe, SimulationObjects simObjects) {
        super();
        parent = parentPipe;
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        wizardPage = new NewObjectSimplePage(this,simObjects,"Data Sink");
        addPage(wizardPage);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(DataSink.class);
        selector.addCategory("Data Sink",DataSink.class);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        DataSink dataSink = (DataSink)wizardPage.createObject();
        if (dataSink==null)
            return false;
	  	
        if (parent != null) {
            if (parent instanceof DataPipeForked) {
                ((DataPipeForked)parent).addDataSink(dataSink);
            }
            else {
                parent.setDataSink(dataSink);
            }
        }
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final DataPipe parent;
    private final SimulationObjects simObjects;
    private NewObjectSimplePage wizardPage;
    private boolean success = false;
}