package etomica.plugin.wrappers;

import etomica.integrator.IntegratorMC;
import etomica.integrator.mcmove.MCMove;
import etomica.simulation.Simulation;

public class IntegratorMCWrapper extends IntegratorWrapper {

    public IntegratorMCWrapper(IntegratorMC object, Simulation sim) {
        super(object,sim);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((IntegratorMC)object).getMoveManager().getMCMoves(), 
                simulation, etomicaEditor);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (super.removeChild(obj)) {
            return true;
        }
        if (obj instanceof MCMove) {
            return ((IntegratorMC)object).getMoveManager().removeMCMove((MCMove)obj);
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof MCMove) {
            MCMove[] moves = ((IntegratorMC)object).getMoveManager().getMCMoves();
            for (int i=0; i<moves.length; i++) {
                if (moves[i] == obj) {
                    return true;
                }
            }
        }
        return false;
    }
}
