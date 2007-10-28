package etomica.plugin.wrappers;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;

import etomica.action.PDBWriter;
import etomica.action.BoxActionAdapter;
import etomica.atom.AtomSet;
import etomica.config.Configuration;
import etomica.config.ConfigurationLattice;
import etomica.lattice.BravaisLattice;
import etomica.lattice.LatticeCubicFcc;
import etomica.lattice.LatticeCubicSimple;
import etomica.lattice.LatticeOrthorhombicHexagonal;
import etomica.box.Box;
import etomica.plugin.EtomicaPlugin;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.views.ConfigurationViewDP;
import etomica.plugin.views.BoxView;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;
import etomica.plugin.wrappers.OpenItemWrapper.OpenViewItemWrapper;

public class BoxWrapper extends PropertySourceWrapper implements OpenerWrapper {

    public BoxWrapper(Box box, SimulationObjects simObjects) {
        super(box,simObjects);
    }
    
    public PropertySourceWrapper[] getChildren(LinkedList parentList) {
        PropertySourceWrapper[] children = super.getChildren(parentList);
        AtomSet agentList = ((Box)object).getSpeciesMaster().getAgentList();
        PropertySourceWrapper[] newChildren = new PropertySourceWrapper[children.length+agentList.getAtomCount()];
        System.arraycopy(children,0,newChildren,0,children.length);
        for (int i=children.length; i<newChildren.length; i++) {
            newChildren[i] = PropertySourceWrapper.makeWrapper(agentList.getAtom(i-children.length),
                    simObjects, etomicaEditor);
        }
        return newChildren;
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemCascadeWrapper openItemWrapper = new OpenItemWrapper();

        openItemWrapper.addSubmenuItem(new OpenViewItemWrapper(CONFIGURATION_DP, this));
        openItemWrapper.addSubmenuItem(new OpenViewItemWrapper(CONFIGURATION_RASMOL, this));
        openItemWrapper.addSubmenuItem(new OpenViewItemWrapper(PHASE, this));

        InitializeMolecules initializeMolecules = new InitializeMolecules();
        initializeMolecules.setBox((Box)object);
        BravaisLattice lattice = null;
        if (((Box)object).getSpace().D() == 3) {
            lattice = new LatticeCubicFcc();
        }
        else if (((Box)object).getSpace().D() == 2) {
            lattice = new LatticeOrthorhombicHexagonal();
        }
        else {
            lattice = new LatticeCubicSimple(simObjects.simulation.getSpace(), 1.0);
        }
        initializeMolecules.setConfiguration(new ConfigurationLattice(lattice));
        MenuItemCascadeWrapper actionItemWrapper = new ActionListItemWrapper();
        actionItemWrapper.addSubmenuItem(new ActionItemWrapper(initializeMolecules, "Initialize"));
        
        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{openItemWrapper,actionItemWrapper}, 
                super.getMenuItemWrappers(parentWrapper));
    }

    public boolean open(String viewName, IWorkbenchPage page, Shell shell) {
        try {
            if (viewName == CONFIGURATION_DP) {
                ConfigurationViewDP view = (ConfigurationViewDP)page.showView("etomica.plugin.views.ConfigurationViewDP",null,IWorkbenchPage.VIEW_VISIBLE);
                view.setBox((Box)object);
                return true;
            }
            else if (viewName == CONFIGURATION_RASMOL) {
                try {
                    
                    final String rasmolPath = EtomicaPlugin.getDefault().getPreferenceStore().getString("rasmolPath");
                    PDBWriter pdbWriter = new PDBWriter((Box)object);
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
                BoxView view = (BoxView)page.showView("etomica.plugin.views.BoxView",null,IWorkbenchPage.VIEW_VISIBLE);
                view.setBox((Box)object);
                return true;
            }
        }
        catch (PartInitException e ) {
            ExceptionHandler.getInstance().handleException(e);
        }
        return false;
    }
    
    protected static final String CONFIGURATION_OSG = "ConfigurationOSG";
    protected static final String CONFIGURATION_DP = "ConfigurationDisplayBox";
    protected static final String CONFIGURATION_RASMOL = "ConfigurationRasmol";
    protected static final String PHASE ="Box";

    protected static class InitializeMolecules extends BoxActionAdapter {

        private static final long serialVersionUID = 1L;

        public void actionPerformed() {
            config.initializeCoordinates(box);
        }
        
        public void setConfiguration(Configuration newConfig) {
            config = newConfig;
        }
        
        private Configuration config;
    }
}
