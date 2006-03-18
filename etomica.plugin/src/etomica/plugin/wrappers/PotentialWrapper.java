package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomType;
import etomica.plugin.wizards.NewInterPotential;
import etomica.plugin.wizards.NewIntraPotential;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.simulation.Simulation;

public class PotentialWrapper extends PropertySourceWrapper {

    public PotentialWrapper(Potential object, Simulation sim) {
        super(object,sim);
    }

    public String toString() {
        return ((Potential)object).getName();
    }
}
