
package etomica.plugin.realtimegraphics;

public class RenderObject
{
	RenderObject( Object obj ) { realm=obj; }
	void setVisible( boolean on ) { enabled = on; }
	
	private final Object realm;
	private boolean enabled = true;
};