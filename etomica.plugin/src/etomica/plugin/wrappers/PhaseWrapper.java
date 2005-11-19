package etomica.plugin.wrappers;

import etomica.action.Action;
import etomica.action.PhaseActionAdapter;
import etomica.config.Configuration;
import etomica.config.ConfigurationLattice;
import etomica.config.ConfigurationSequential;
import etomica.lattice.LatticeCubicFcc;
import etomica.phase.Phase;
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
