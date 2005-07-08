package etomica.plugin.realtimegraphics;

import org.eclipse.swt.graphics.Color;

import etomica.Atom;

/**
 * Defines an interface to retrieve the Atom color from an Atom object
 * @author Henrique Bucher
 */
 
public interface ColorScheme {
    public Color atomColor(Atom a);
}//end of ColorScheme
