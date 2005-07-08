package etomica.plugin.realtimegraphics;
import etomica.Parameter;
import org.eclipse.swt.graphics.Color;

public abstract class ParameterGraphic extends Parameter {
    
    public interface Color {
        public Color getColor();
        public void setColor(Color c);
    }
}
    