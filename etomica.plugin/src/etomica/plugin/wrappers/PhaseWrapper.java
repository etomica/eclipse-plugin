package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;

import etomica.action.Action;
import etomica.action.PhaseActionAdapter;
import etomica.config.Configuration;
import etomica.config.ConfigurationLattice;
import etomica.config.ConfigurationSequential;
import etomica.lattice.LatticeCubicFcc;
import etomica.phase.Phase;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.views.ConfigurationView;
import etomica.plugin.views.ConfigurationViewDP;
import etomica.plugin.views.PhaseView;
import etomica.simulation.Simulation;

public class PhaseWrapper extends PropertySourceWrapper {

    public PhaseWrapper(Phase phase, Simulation sim) {
        super(phase,sim);
    }

    public Action[] getActions() {
        MakeMolecules makeMolecules = new MakeMolecules("Make Molecules");
        makeMolecules.setPhase((Phase)object);
        InitializeMolecules initializeMolecules = new InitializeMolecules("Initialize");
        initializeMolecules.setPhase((Phase)object);
        if (((Phase)object).space().D() == 3) {
            initializeMolecules.setConfiguration(new ConfigurationLattice(new LatticeCubicFcc()));
        }
        else {
            initializeMolecules.setConfiguration(new ConfigurationSequential(((Phase)object).space()));
        }
        return new Action[]{makeMolecules,initializeMolecules};
    }
    
    public String[] getOpenViews() {
        return new String[]{CONFIGURATION_OSG, CONFIGURATION_DP, PHASE};
    }

    public boolean open(String viewName, IWorkbenchPage page, Shell shell) {
        try {
            if (viewName == CONFIGURATION_OSG) {
                ConfigurationView view = (ConfigurationView)page.showView("etomica.plugin.views.ConfigurationView",null,IWorkbenchPage.VIEW_VISIBLE);
                view.setPhase((Phase)object);
                return true;
            }
            else if (viewName == CONFIGURATION_DP) {
                ConfigurationViewDP view = (ConfigurationViewDP)page.showView("etomica.plugin.views.ConfigurationViewDP",null,IWorkbenchPage.VIEW_VISIBLE);
                view.setPhase((Phase)object);
                return true;
            }
            else if (viewName == PHASE) {
                PhaseView view = (PhaseView)page.showView("etomica.plugin.views.PhaseView",null,IWorkbenchPage.VIEW_VISIBLE);
                view.setPhase((Phase)object);
                return true;
            }
        }
        catch (PartInitException e ) {
            ExceptionHandler.getInstance().handleException(e);
        }
        return false;
    }
    
    protected static final String CONFIGURATION_OSG = "ConfigurationOSG";
    protected static final String CONFIGURATION_DP = "ConfigurationDisplayPhase";
    protected static final String PHASE ="Phase";

    private static class MakeMolecules extends PhaseActionAdapter {
        public MakeMolecules(String label) {
            super(label);
        }

        public void actionPerformed() {
            phase.makeMolecules();
        }
    }

    private static class InitializeMolecules extends PhaseActionAdapter {
        public InitializeMolecules(String label) {
            super(label);
        }

        public void actionPerformed() {
            config.initializeCoordinates(phase);
        }
        
        public void setConfiguration(Configuration newConfig) {
            config = newConfig;
        }
        
        private Configuration config;
    }
}
