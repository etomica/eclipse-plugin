package etomica.plugin.realtimegraphics;

import org.eclipse.swt.graphics.Color;
import etomica.Atom;

/**
 * Does nothing at any time to set atom's color, leaving color to be set to default value.
 * @author David Kofke
 *
 */

public final class ColorSchemeNull implements ColorScheme {
    
    public ColorSchemeNull() {}
        
 /**
  * Return without changing atom's color.
  */
    public final Color atomColor(Atom a) {return null;}

}
