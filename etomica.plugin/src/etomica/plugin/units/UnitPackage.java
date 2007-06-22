package etomica.plugin.units;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import etomica.graphics.DeviceCheckBox;
import etomica.graphics.SimulationGraphic;

/**
 * @author mjm35
 * This is a unitPackage, the idea is for it to encompass the sieve, enumerator ect..
 *  eventually leading to something that can be passed to UnitPackageWindow which will
 * Generate a GUI window based off of what this class is
 * 
 */
public class UnitPackage {
 
	
	public UnitPackage(Sieve S){
		
		window3(S);
	
	    
	    
	}
	private void window3(Sieve F){
		
		// Top Level Object
		JFrame f = new JFrame();
		// Arranged things on the Top level Object
		JPanel panel = new JPanel();
		JPanel panelTop = new JPanel();
		JPanel panelCenter = new JPanel();
		JPanel panelBottom = new JPanel();
	
	
		// Cut off Filter can go on the top
		ModiferCheckBox CutOffModifier = new ModiferCheckBox(true);
		DeviceCheckBox CutOff = new DeviceCheckBox(CutOffModifier);
		panelTop.add(CutOff.graphic());
		panelTop.setBounds(0,0,1200,400);
		
		// Cut off Filter can go on the middle
		ModiferCheckBox Coefficentmodifier = new ModiferCheckBox(false);
		DeviceCheckBox Coefficent = new DeviceCheckBox(Coefficentmodifier);
		panelCenter.add(Coefficent.graphic());
		panelCenter.setBounds(400,0,1200,400);
		
		// Selector Will Go on bottom
		JComboBox selector = new javax.swing.JComboBox(new Object[] {new Object()});
		selector.setEditable(false);
		selector.addItem("Hello");
		for(int i = 0; i != F.goodList().length; ++i){
			selector.addItem(F.goodList()[i]);
		}
		panelBottom.add(selector);
		panelBottom.setBounds(800,0,1200,400);
		
		f.setSize(1200,1200);
		


		//panel.setBounds()
		panel.add(panelTop);
		panel.add(panelCenter);
		panel.add(panelBottom);
		f.getContentPane().add(panel);
		f.pack();
	    f.show();
	    f.addWindowListener(SimulationGraphic.WINDOW_CLOSER);   
	}
	
	private void window2(Sieve F){
		
		
		JPanel panel = new JPanel();
		JPanel panelRight = new JPanel();
		JComboBox selector = new javax.swing.JComboBox(new Object[] {new Object()});
		selector.setEditable(false);
		selector.addItem("Hello");
		for(int i = 0; i != F.goodList().length; ++i){
			selector.addItem(F.goodList()[i]);
		}
		
		
		JFrame f = new JFrame();
		ModiferCheckBox CutOffModifier = new ModiferCheckBox(true);
		DeviceCheckBox CutOff = new DeviceCheckBox(CutOffModifier);
		
		ModiferCheckBox Coefficentmodifier = new ModiferCheckBox(false);
		DeviceCheckBox Coefficent = new DeviceCheckBox(Coefficentmodifier);
		
		f.setSize(700,500);
		panel.add(Coefficent.graphic());
		panel.add(CutOff.graphic());
		panel.add(selector);
	
	
		f.getContentPane().add(panel);
		f.pack();
	    f.show();
	    f.addWindowListener(SimulationGraphic.WINDOW_CLOSER);   
	}
	
	// Window 1 Works, 
	private void window1(){
		JPanel panel = new JPanel();
		JPanel panelRight = new JPanel();
		JComboBox selector = new javax.swing.JComboBox(new Object[] {new Object()});
		
		JFrame f = new JFrame();
		ModiferCheckBox CutOffModifier = new ModiferCheckBox(true);
		DeviceCheckBox CutOff = new DeviceCheckBox(CutOffModifier);
		
		ModiferCheckBox Coefficentmodifier = new ModiferCheckBox(false);
		DeviceCheckBox Coefficent = new DeviceCheckBox(Coefficentmodifier);
		
		f.setSize(700,500);
		panel.add(Coefficent.graphic());
		panel.add(CutOff.graphic());
		f.getContentPane().add(panel);
		f.pack();
	    f.show();
	    f.addWindowListener(SimulationGraphic.WINDOW_CLOSER);   
	}
}
