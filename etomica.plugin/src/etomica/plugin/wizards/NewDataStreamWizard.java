package etomica.plugin.wizards;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

import etomica.data.DataPump;
import etomica.data.DataSink;
import etomica.data.DataSource;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntegratorPhase;
import etomica.integrator.IntervalActionAdapter;
import etomica.phase.Phase;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.Simulation;

/**
 * This wizard allows the user to create a new DataStream.  The user can choose
 * the DataSource class and the DataStream is added to the Simulation.  The user 
 * can then select to hook up the DataStream to an Integrator.
 */
public class NewDataStreamWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewDataStreamWizard(Simulation sim) {
        super();
        simulation = sim;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewObjectSimplePage(this,simulation,"Data Source");
        addPage(page);
        page2 = new IntegratorSelectionPage(simulation,"Data Source");
        addPage(page2);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(DataSource.class);
        selector.setExcludedClasses(new Class[]{DataSink.class});
        // the user can select an integrator to pass on page 2
        //FIXME hopefully they don't select a class that requires an Integrator
        // and then not select an Integrator!
        selector.setExtraParameterClasses(new Class[]{Integrator.class});
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // some DataSources are tied to an integrator
        Integrator integrator = page2.getIntegrator();
        if (integrator != null) {
            page.setExtraParameters(new Object[]{integrator});
        }

        // Create simulation based on user's choices
        DataSource dataSource = (DataSource)page.createObject();
        if (dataSource == null)
            return false;
	  	
        
        DataPump pump = new DataPump(dataSource,null);
        simulation.register(dataSource,pump);
        if (page2.getControl() != null && page2.isPageComplete()) {
            if (integrator != null) {
                if (dataSource instanceof IntegratorIntervalListener) {
                    integrator.addListener((IntegratorIntervalListener)dataSource);
                    // we still need the pump so a Data stream exists
                }
                else {
                    integrator.addListener(new IntervalActionAdapter(pump));
                }
                if (integrator instanceof IntegratorPhase) {
                    Phase phase = ((IntegratorPhase)integrator).getPhase();
                    if (phase != null) {
                        try {
                            Method phaseSetter = dataSource.getClass().getMethod("setPhase", new Class[]{Phase.class});
                            phaseSetter.invoke(dataSource, new Object[]{phase});
                        }
                        catch (NoSuchMethodException e) {
                            // datasource had no setPhase method
                        }
                        catch (IllegalAccessException e) {
                            WorkbenchPlugin.getDefault().getLog().log(
                                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
                        }
                        catch (InvocationTargetException e) {
                            WorkbenchPlugin.getDefault().getLog().log(
                                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
                        }
                    }
                }
                //FIXME we should call setIntegrator on DataSources that have 
                //such a method, but some will already have an Integrator (passed
                //to the constructor), so we need to audit the relevant classes to
                //ensure they handle the Integrator being set again
            }
        }
        
        success = true;
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    public void createPageControls(Composite pageContainer) {
        // the default behavior is to create all the pages controls
        IWizardPage[] pages = getPages();
        for (int i = 0; i < pages.length; i++) {
            pages[i].createControl(pageContainer);
        }
    }
    
    private final Simulation simulation;
    private NewObjectSimplePage page;
    private IntegratorSelectionPage page2;
    private boolean success = false;
}