/*
 * History
 * Created on Oct 13, 2004 by kofke
 */
package etomica.ide.swt;

import org.eclipse.swt.SWT;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Color {

	/**
	 * Private construct to prevent instantiation.
	 */
	private Color() {
	}

	/**
	 * Takes one of the java.awt color constants and returns
	 * the corresponding swt index for the color.  This index
	 * can be passed to getDisplay().getSystemColor(index) to
	 * obtain a color that can be passed to a swt GC instance.
	 * If the given color doesn't correspond to a swt system
	 * color, a value of -1 is returned (which will result in
	 * black if used to get an swt color).
	 */
	public static int awt2swt(java.awt.Color color) {
		if(color == java.awt.Color.BLACK) return SWT.COLOR_BLACK;
		if(color == java.awt.Color.BLUE) return SWT.COLOR_BLUE;
		if(color == java.awt.Color.RED) return SWT.COLOR_RED;
		if(color == java.awt.Color.YELLOW) return SWT.COLOR_YELLOW;
		if(color == java.awt.Color.GREEN) return SWT.COLOR_GREEN;
		if(color == java.awt.Color.CYAN) return SWT.COLOR_CYAN;
		if(color == java.awt.Color.DARK_GRAY) return SWT.COLOR_DARK_GRAY;
		if(color == java.awt.Color.GRAY) return SWT.COLOR_GRAY;
		if(color == java.awt.Color.MAGENTA) return SWT.COLOR_MAGENTA;
		if(color == java.awt.Color.WHITE) return SWT.COLOR_WHITE;
		return SWT.COLOR_BLACK;
	}
}
