package etomica.plugin.realtimegraphics;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

import etomica.plugin.realtimegraphics.ColorScheme;

import etomica.Atom;
import etomica.integrator.IntegratorHard;
import org.eclipse.swt.SWT;

/**
 * This colorScheme acts to color differently the two atoms that are scheduled to collide next.
 * Highlight colors are specified by the colliderColor and partnerColor fields; all other
 * atoms are colored with the baseColor.  Applies only to with a hard-potential MD integrator.
 */
public class ColorSchemeColliders implements ColorScheme {
    
    public ColorSchemeColliders(IntegratorHard integrator) 
    {
        super();
        this.integrator = integrator;
        colorsProvided = false;
    }
    /** Set colors to the default colors in the device - red, blue and gray */
    public void setColors( Device device )
    {
    	Color red = device.getSystemColor(SWT.COLOR_RED);
    	Color blue = device.getSystemColor(SWT.COLOR_BLUE);
    	Color gray = device.getSystemColor(SWT.COLOR_GRAY);
        setColors( red, blue, gray );    
    }
    /** Set arbitrary colors */
    public void setColors( Color collider, Color partner, Color others )
    {
    	colliderColor = collider;
    	partnerColor = partner;
    	defaultColor = others;
    	colorsProvided = true;
    }
    /**
     * Applies the special colors to the colliding pair while coloring all other atoms with baseColor.
     */ 
    public Color atomColor(Atom a) throws RuntimeException {
    	if ( !colorsProvided )
    		throw new RuntimeException( "Colors not yet provided to ColorSchemeColliders object - use SetColor(Device) or SetColor(Color,Color,Color) before calling atomColor()");
        IntegratorHard.Agent colliderAgent = integrator.colliderAgent();
        if(colliderAgent == null) return defaultColor;
        else if(a == colliderAgent.atom) return colliderColor;
        else if(a == colliderAgent.collisionPartner) return partnerColor;
        else return defaultColor;
    }
    /** Color applied to the downList atom of the colliding pair */
    protected Color colliderColor;
    /**Color applied to the upList atom of the colliding pair */
    protected Color partnerColor;
    /**Color applied to the atomns that do not have a colliderAgent */
    protected Color defaultColor;
    /**The integrator that has the collision information */
    IntegratorHard integrator;
    /**Indicate whether colors were provided - one of SetColors() functions must be called */
    protected boolean colorsProvided = false;
    
}

