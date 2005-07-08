package etomica.plugin.realtimegraphics;
import org.eclipse.swt.graphics.Color;

public class ParameterColor 
extends ParameterGraphic 
implements ParameterGraphic.Color 
{
    public ParameterColor( Color c ) { color = c; }
    public Color getColor() {return color;}
    public void setColor(Color c) {color = c;}
    
    private Color color;    
}
    