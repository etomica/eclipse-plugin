package etomica.plugin.wrappers;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;

import etomica.action.Action;
import etomica.action.PDBWriter;
import etomica.action.PhaseActionAdapter;
import etomica.config.Configuration;
import etomica.config.ConfigurationLattice;
import etomica.config.ConfigurationSequential;
import etomica.lattice.LatticeCubicFcc;
import etomica.phase.Phase;
import etomica.plugin.EtomicaPlugin;
import etomica.plugin.views.ConfigurationView;
import etomica.plugin.views.ConfigurationViewDP;
import etomica.plugin.views.PhaseView;
import etomica.simulation.Simulation;

public class PhaseWrapper extends PropertySourceWrapper {

    public PhaseWrapper(Phase phase, Simulation sim) {
        super(phase,sim);
    }

    public Action[] getActions() {
        InitializeMolecules initializeMolecules = new InitializeMolecules();
        initializeMolecules.setPhase((Phase)object);
        if (((Phase)object).space().D() == 3) {
            initializeMolecules.setConfiguration(new ConfigurationLattice(new LatticeCubicFcc()));
        }
        else {
            initializeMolecules.setConfiguration(new ConfigurationSequential(((Phase)object).space()));
        }
        return new Action[]{initializeMolecules};
    }
    
    public String[] getOpenViews() {
        return new String[]{CONFIGURATION_DP, CONFIGURATION_RASMOL, PHASE};
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
            else if (viewName == CONFIGURATION_RASMOL) {
                try {
                    
                    final String rasmolPath = EtomicaPlugin.getDefault().getPreferenceStore().getString("rasmolPath");
                    PDBWriter pdbWriter = new PDBWriter((Phase)object);
                    final File pdbFile = File.createTempFile("etomica",".pdb");
                    pdbWriter.setFile(pdbFile);
                    pdbWriter.actionPerformed();
                    final File scrFile = File.createTempFile("etomica",".scr");
                    pdbWriter.setFile(scrFile);
                    pdbWriter.writeRasmolScript();
                    Thread thread = new Thread(new Runnable() {
                    
                        public void run() {
                            try {
                                Process proc;
                                if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
                                    proc = Runtime.getRuntime().exec(new String[]{rasmolPath, pdbFile.getAbsolutePath(),"-script",scrFile.getAbsolutePath()});
                                }
                                else {
                                    proc = Runtime.getRuntime().exec(new String[]{"xterm", "-e",rasmolPath, pdbFile.getAbsolutePath(),"-script",scrFile.getAbsolutePath()});
                                }
                                proc.waitFor();
                            }
                            catch (IOException e) {
                                ExceptionHandler.getInstance().handleException(e);
                            }
                            catch (InterruptedException e) {
                            }
                            pdbFile.delete();
                            scrFile.delete();
                        }
                    
                    });
                    thread.start();
                }
                catch (IOException e) {
                    ExceptionHandler.getInstance().handleException(e);
                }
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
    protected static final String CONFIGURATION_RASMOL = "ConfigurationRasmol";
    protected static final String PHASE ="Phase";

    private static class InitializeMolecules extends PhaseActionAdapter {
        public InitializeMolecules() {
            super("Initialize");
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
