package etomica.plugin.units;
import etomica.modifier.ModifierBoolean;

/*
 * Created on Jan 22, 2006
 *	This is a modifer for CheckBox, and it implements the modifer interface
 *  It is simply a boolean which is the current state of the checkBox, 
 */

/**
 * @author mjm35
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ModiferCheckBox implements ModifierBoolean{
	
	private boolean bool; 
	
	public ModiferCheckBox(boolean PassedBoolean){
		bool = PassedBoolean; 
	}

    public void setBoolean(boolean b){
    	bool = b;
    }

    public boolean getBoolean(){
    	return bool;
    }
}
