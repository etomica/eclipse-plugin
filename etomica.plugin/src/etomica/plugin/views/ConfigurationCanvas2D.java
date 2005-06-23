package etomica.plugin.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
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
public class ConfigurationCanvas2D extends ConfigurationCanvas {
    
    private int[] shiftOrigin = new int[2];     //work vector for drawing overflow images
        
    public ConfigurationCanvas2D(Composite parent) {
    	super(parent);
    }
    
    private void drawAtom(GC gcImage, Atom a) {
        if(!atomFilter.accept(a)) return;
        Vector r = a.coord.position();
        int sigmaP, xP, yP, baseXP, baseYP;

//        g.setColor(displayPhase.getColorScheme().atomColor(a));
//	   		gcImage.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
   		int swtColor = etomica.plugin.views.Color.awt2swt(colorScheme.atomColor(a));
   		for(int i=0; i<selectedAtoms.length; i++) if(a.node.isDescendedFrom(selectedAtoms[i])) swtColor = SWT.COLOR_GREEN;
   		gcImage.setBackground(parent.getDisplay().getSystemColor(swtColor));
   	             
        baseXP = phaseOrigin[0] + (int)(toPixels*r.x(0));
        baseYP = phaseOrigin[1] + (int)(toPixels*r.x(1));
        if(a.type instanceof AtomTypeSphere) {
            /* Draw the core of the atom, specific to the dimension */
            sigmaP = (int)(toPixels*((AtomTypeSphere)a.type).diameter(a));
            sigmaP = (sigmaP == 0) ? 1 : sigmaP;
            xP = baseXP - (sigmaP>>1);
            yP = baseYP - (sigmaP>>1);
            gcImage.fillOval(xP, yP, sigmaP, sigmaP);
            /* Draw the surrounding well, if any, and specific to the dimension */
//            if(drawWell) {
//                sigmaP = (int)(displayPhase.getToPixels()*((AtomType.Well)a.type).wellDiameter());
//                xP = baseXP - (sigmaP>>1);
//                yP = baseYP - (sigmaP>>1);
//                g.setColor(wellColor);
//                g.drawOval(xP, yP, sigmaP, sigmaP);
//            }
//            /* Draw the orientation line, if any */
//            if(drawOrientation) {
//                double theta = ((Space.Coordinate.Angular)a.coord).orientation().angle()[0];
//                int dxy = (int)(displayPhase.getToPixels()*((AtomType.OrientedSphere)a.type).radius(a));
//                int dx = (int)(dxy*Math.cos(theta));
//                int dy = (int)(dxy*Math.sin(theta));
//                g.setColor(Color.red);
//                xP += dxy; yP += dxy;
//                g.drawLine(xP-dx, yP-dy, xP+dx, yP+dy);
//            }
//            a.type.electroType().draw(g, origin, displayPhase.getToPixels(), r);
        } else { // Not a sphere, wall, or one of their derivatives...
            // Do nothing (how do you draw an object of unknown shape?)
        }
    }
            
      
    /**
    * doPaint is the method that handles the drawing of the phase to the screen.
    * Several variables and conditions affect how the image is drawn.  First,
    * the Unit.Length.Sim class variable <code>TO_PIXELS</code> performs the conversion between simulation
    * length units (Angstroms) and pixels.  The default value is 10 pixels/Angstrom
    * reflecting the default size of the phase (300 pixels by 300 pixels) and the
    * default phase size (30 by 30 A).  
    * The field <code>scale</code> is a multiplicative factor that directly
    * scales up or down the size of the image; this value is adjusted automatically
    * whenever shells of periodic images are drawn, to permit the central image and all 
    * of the specified periodic images to fit in the drawing of the phase.  
    *
    * @param g The graphic object to which the image of the phase is drawn
    * @see Species
    */
    double t = 0;
//    public void doPaint(GC gcImage) {
//		t+=0.1;
//		gcImage.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
//		gcImage.fillOval((int)(100*(Math.sin(t)+1)),(int)(100*(Math.cos(2*t)+1)),50,50);
//	}

    public void doPaint(GC gcImage) {
        if(!isVisible() || getPhase() == null) {return;}
        Rectangle rect = gcImage.getClipping();
        int drawWidth = rect.width;
        int drawHeight = rect.height;
        Vector dimensions = getPhase().boundary().dimensions();
        double phaseWidth = dimensions.x(0);
        double phaseHeight = dimensions.x(1);
        toPixels = 0.9*scale*Math.min(drawWidth/phaseWidth, drawHeight/phaseHeight);
        int iPhaseWidth = (int)(phaseWidth*toPixels)-1;
        int iPhaseHeight = (int)(phaseHeight*toPixels);
        phaseOrigin[0] = (drawWidth - iPhaseWidth) >> 1;
        phaseOrigin[1] = (drawHeight - iPhaseHeight) >> 1;
        //Draw other features if indicated
        if(drawBoundary>DRAW_BOUNDARY_NONE) {
    		gcImage.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
            gcImage.drawRectangle(phaseOrigin[0], phaseOrigin[1], iPhaseWidth, iPhaseHeight);
        }
        //do drawing of all drawing objects that have been added to the display
//        for(Iterator iter=displayPhase.getDrawables().iterator(); iter.hasNext(); ) {
//            Drawable obj = (Drawable)iter.next();
//            obj.draw(g, displayPhase.getOrigin(), displayPhase.getScale());
//        }
            
        //Color all atoms according to colorScheme in DisplayPhase
//        displayPhase.getColorScheme().colorAllAtoms();
            
        //Draw all atoms
//        Space.Boundary boundary = displayPhase.getPhase().boundary();
//        if(displayPhase.getColorScheme() instanceof ColorSchemeCollective) {
//            ((ColorSchemeCollective)displayPhase.getColorScheme()).colorAllAtoms(displayPhase.getPhase());
//        }
        atomIterator.reset();
        while(atomIterator.hasNext()) {
            drawAtom(gcImage, atomIterator.nextAtom());
        }
            
        //Draw overflow images if so indicated
//        if(displayPhase.getDrawOverflow()) {
//            atomIterator.reset();
//            while(atomIterator.hasNext()) {
//                Atom a = atomIterator.nextAtom();
//                if(!(a.type instanceof AtomType.Sphere)) continue;
//                float[][] shifts = boundary.getOverflowShifts(a.coord.position(),((AtomType.Sphere)a.type).radius(a));  //should instead of radius have a size for all AtomC types
//                for(int i=shifts.length-1; i>=0; i--) {
//                    shiftOrigin[0] = displayPhase.getOrigin()[0] + (int)(displayPhase.getToPixels()*shifts[i][0]);
//                    shiftOrigin[1] = displayPhase.getOrigin()[1] + (int)(displayPhase.getToPixels()*shifts[i][1]);
//                    drawAtom(g, shiftOrigin, a);
//                }
//            }
//        }
//
//        //Draw periodic images if indicated
//        if(displayPhase.getImageShells() > 0) {
//            double[][] origins = displayPhase.getPhase().boundary().imageOrigins(displayPhase.getImageShells());  //more efficient to save rather than recompute each time
//            for(int i=0; i<origins.length; i++) {
//                g.copyArea(displayPhase.getOrigin()[0],displayPhase.getOrigin()[1],displayPhase.getDrawSize()[0],displayPhase.getDrawSize()[1],(int)(displayPhase.getToPixels()*origins[i][0]),(int)(displayPhase.getToPixels()*origins[i][1]));
//            }
//        }
//        //Draw bar showing scale if indicated
//        if(writeScale) {
//            g.setColor(Color.lightGray);
//            g.fillRect(0,getSize().height-annotationHeight,getSize().width,annotationHeight);
//            g.setColor(Color.black);
//            g.setFont(font);
//            g.drawString("Scale: "+Integer.toString((int)(100*displayPhase.getScale()))+"%", 0, getSize().height-3);
//        }
    }//end of doPaint
    
    private final int[] phaseOrigin = new int[2];
    private double toPixels;
    
}  //end of DisplayPhase.Canvas
