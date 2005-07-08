package etomica.plugin.realtimegraphics;

import org.eclipse.swt.graphics.Color;

import etomica.Atom;

/**
 * Class that defines the algorithm used to determine atoms colors when drawn to DisplayPhase.
 * The atomColor method is called just before the atom is drawn to set the graphics color.
 *
 * @author David Kofke
 */
 
public abstract class ColorScheme implements java.io.Serializable {

    public static String getVersion() {return "01.07.13";}
 
    protected Color baseColor;
    
    public ColorScheme(Color color) {
        baseColor = color;
    }
    
    public abstract Color atomColor(Atom a);
    
    public final void setBaseColor(Color c) {baseColor = c;}
    public final Color getBaseColor() {return baseColor;}

}//end of ColorScheme
