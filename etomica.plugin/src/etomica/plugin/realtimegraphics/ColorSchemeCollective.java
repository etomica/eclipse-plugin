package etomica.plugin.realtimegraphics;

import etomica.plugin.realtimegraphics.ColorScheme;
import org.eclipse.swt.graphics.Color;

import etomica.Atom;
import etomica.Phase;

/**
 * Parent class for color schemes that are best implemented by attaching colors
 * to all atoms at once, rather than determining the color of each as it is drawn.
 * The colorAllAtoms method is called by the display if it determines that the
 * ColorScheme is a subclass of this one.
 */
    
public abstract class ColorSchemeCollective implements ColorScheme, Atom.AgentSource {
    
    protected static int agentIndex = -1;
    
    public ColorSchemeCollective() {
        super();
        if(agentIndex < 0) agentIndex = Atom.requestAgentIndex(this);
    }
    
    public abstract void colorAllAtoms(Phase phase);
        //determine color
        //then assign it to atom like this: atom.allatomAgents[agentIndex] = color
        
    public Color atomColor(Atom a) {return (Color)a.allatomAgents[agentIndex];}
   
    //set aside a agent index entry to store the color with the atom 
    public Object makeAgent(Atom a) {return null;}
}

