package etomica.plugin.realtimegraphics;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Canvas;

import etomica.Atom;
import etomica.Phase;

public interface Renderable
{
	/** Set the canvas to be used */
	void setCanvas( Canvas canvas );
	/** Set the color pattern to be used */
	void setColorScheme( ColorScheme cscheme );
	/** Set the phase to be drawn */
	void setPhase( Phase phase );

	/** Called several times for each atom visible */
	RenderObject addAtom(Atom a);

	/** Called prior to any drawing function */
	void render( PaintEvent event );
};
