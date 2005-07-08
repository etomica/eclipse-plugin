package etomica.plugin.realtimegraphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import etomica.Atom;
import etomica.Species;
import etomica.atom.AtomTypeSphere;
import etomica.space.Vector;

    /* History of changes
     * 7/16/02 (DAK) Modified for AtomType.Sphere diameter and radius method to take atom as argument.
     * 8/18/02 (DAK) Modified drawing of Wall to shift image by drawShift value given in type
     * 9/07/02 (DAK) Added atomFilter
     */

//Class used to define canvas onto which configuration is drawn
public class GCRenderer implements Renderable {
    
    private int[] shiftOrigin = new int[2];     //work vector for drawing overflow images
        
    public GCRenderer( GC agc ) {
    	gc = agc;
    }
    
    public void drawAtom(Atom a, boolean selected ) {
        Vector r = a.coord.position();
        int sigmaP, xP, yP, baseXP, baseYP;

//        g.setColor(displayPhase.getColorScheme().atomColor(a));
//	   		gcImage.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
   		int swtColor = etomica.plugin.views.Color.awt2swt(colorScheme.atomColor(a));
   		for(int i=0; i<selectedAtoms.length; i++) if(a.node.isDescendedFrom(selectedAtoms[i])) swtColor = SWT.COLOR_GREEN;
   		gc.setBackground(getDisplay().getSystemColor(swtColor));
   	             
        baseXP = phaseOrigin[0] + (int)(toPixels*r.x(0));
        baseYP = phaseOrigin[1] + (int)(toPixels*r.x(1));
        if(a.type instanceof AtomTypeSphere) {
            /* Draw the core of the atom, specific to the dimension */
            sigmaP = (int)(toPixels*((AtomTypeSphere)a.type).diameter(a));
            sigmaP = (sigmaP == 0) ? 1 : sigmaP;
            xP = baseXP - (sigmaP>>1);
            yP = baseYP - (sigmaP>>1);
            canvas.fillOval(xP, yP, sigmaP, sigmaP);
        }
    }
            
      
    private GC gc;
    
    private final int[] phaseOrigin = new int[2];
    private double toPixels;

    
}  //end of DisplayPhase.Canvas
