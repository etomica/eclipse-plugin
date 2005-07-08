package etomica.plugin.realtimegraphics;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Color;
import java.util.Random;
import etomica.Atom;
import etomica.Phase;
import etomica.atom.iterator.AtomIteratorListTabbed;

public class ColorSchemeRandom extends ColorSchemeCollective {
    
    private final AtomIteratorListTabbed iterator = new AtomIteratorListTabbed();
    
    public ColorSchemeRandom( Device adevice ) {
        super();
        device = adevice;
    }
    
    public void colorAllAtoms(Phase phase) {
        iterator.setList(phase.speciesMaster.atomList);
        iterator.reset();
        while(iterator.hasNext()) {
            Atom a = iterator.nextAtom();
            if(a.allatomAgents[agentIndex] == null) {
                a.allatomAgents[agentIndex] = randomColor();
            }
        }
    }
    
    protected Color randomColor()
    {
    	int red = generator.nextInt( 256 );
    	int blue = generator.nextInt( 256 );
    	int green = generator.nextInt( 256 );
    	return new Color( device, red, green, blue );
    }
    
    protected Device device;
    protected Random generator = new java.util.Random(); 
}//end of ColorSchemeRandom