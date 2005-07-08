package etomica.plugin.realtimegraphics;

import org.eclipse.swt.graphics.Color;

import etomica.Atom;
/**
* Simplest color scheme - colors all atoms with baseColor. 
* @author Henrique
*/
public class ColorSchemeSimple extends ColorScheme {
    public ColorSchemeSimple(Color color) {super(color);}
    public Color atomColor(Atom a) {return baseColor;}
}//end of Simple