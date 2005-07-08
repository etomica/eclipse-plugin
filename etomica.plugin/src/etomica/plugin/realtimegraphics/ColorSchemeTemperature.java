//includes a main method to demonstrate use and to test
package etomica.plugin.realtimegraphics;
import etomica.plugin.realtimegraphics.ColorScheme;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import etomica.Atom;
import etomica.AtomTypeLeaf;
import etomica.space.ICoordinateKinetic;
import etomica.units.Dimension;
import etomica.units.Kelvin;
/**
 * Colors atoms according to their kinetic energy.
 * Atoms with high KE are colored red, and those with low KE are colored blue.
 * Range of low..high is adjustable.
 *
 * @author David Kofke
 *
 */
 
public class ColorSchemeTemperature implements ColorScheme {
    
    double TLow, THigh;
    protected double KEMin, KEMax, range;
    
    /**
     * Constructs with default low of 200K and high of 400K.
     */
    public ColorSchemeTemperature( Device adevice ) {
        this(adevice, Kelvin.UNIT.toSim(200.), Kelvin.UNIT.toSim(400.));
    }
    public ColorSchemeTemperature( Device adevice, double TLow, double THigh ) {
        setTLow(TLow);
        setTHigh(THigh);
    }
      
    public double getTLow() {return TLow;}
    public void setTLow(double t) {
        TLow = t;
        KEMin = t;
        range = 1.0/(KEMax-KEMin);
    }
    public Dimension getTLowDimension() {return Dimension.TEMPERATURE;}
    public double getTHigh() {return THigh;}
    public void setTHigh(double t) {
        THigh = t;
        KEMax = t;
        range = 1.0/(KEMax-KEMin);
    }
        
    public Color atomColor(Atom a) {
        float blueness = 0.0f;
        double ke = ((AtomTypeLeaf)a.type).getMass()*((ICoordinateKinetic)a.coord).velocity().squared();
        if(ke > KEMax) {blueness = 0.0f;}
        else if(ke < KEMin) {blueness = 1.0f;}
        else {blueness = (float)((KEMax-ke)*range);}

        int cblue = (int)( 256*blueness );
        int cred = 255-cblue;
        return new Color(device, cred, 0, cblue);
    }
    
    protected Device device;
}
