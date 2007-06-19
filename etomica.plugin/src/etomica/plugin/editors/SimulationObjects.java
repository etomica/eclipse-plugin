package etomica.plugin.editors;

import java.util.ArrayList;

import etomica.simulation.ISimulation;

public class SimulationObjects implements java.io.Serializable {

    private static final long serialVersionUID = 2L;
    public ISimulation simulation;
    public ArrayList potentialMasters = new ArrayList();
    public ArrayList integrators = new ArrayList();
    public ArrayList dataStreams = new ArrayList();
}
