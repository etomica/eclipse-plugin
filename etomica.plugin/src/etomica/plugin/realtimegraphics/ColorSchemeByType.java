package etomica.plugin.realtimegraphics;

import etomica.Atom;
import etomica.AtomType;
import etomica.Parameter;
import etomica.Species;
import etomica.SpeciesSpheresMono;
import etomica.atom.AtomFactoryMono;
import org.eclipse.swt.graphics.Color;
import java.util.HashMap;

/**
 * Colors the atom according to the color given by its type field.  
 *
 * @author David Kofke
 * @author Henrique Bucher
 */

public final class ColorSchemeByType 
implements ColorScheme, java.io.Serializable 
{
    
	public ColorSchemeByType() 	{}

 /**
  * Initialize atom color to the color of its type
  */
    public final Color atomColor(Atom a) 
    {
    	return (Color) colormap.get( a.type );
    }

    public void setColor(AtomType type, Color c) 
    {
    	colormap.put( type, c );
    }


	protected HashMap colormap = new HashMap();
   
}
