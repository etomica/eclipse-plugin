package etomica.plugin.realtimegraphics;

import org.eclipse.swt.graphics.Color;

import etomica.Atom;
/**
* Simplest color scheme - colors all atoms with baseColor. 
* @author Henrique
*/
public class ColorSchemeSimple implements ColorScheme, java.io.Serializable {
    public ColorSchemeSimple(Color color) { setBaseColor(color); }
    public Color atomColor(Atom a) {return baseColor;}
    
    public final void setBaseColor(Color c) {baseColor = c;}
    public final Color getBaseColor() {return baseColor;}
    protected Color baseColor;
}//end of Simple