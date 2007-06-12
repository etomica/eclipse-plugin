package etomica.plugin.editors;

import java.util.ArrayList;

import etomica.simulation.Simulation;

public class SimulationObjects implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    public Simulation simulation;
    public ArrayList potentialMasters = new ArrayList();
    public ArrayList integrators = new ArrayList();
    public ArrayList dataStreams = new ArrayList();
}
