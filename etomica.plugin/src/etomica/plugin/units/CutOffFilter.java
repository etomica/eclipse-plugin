package etomica.plugin.units;
/*
 * Created on Jan 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.util.LinkedList;

/**
 * @author mjm35
 *  The ideal organization for this filter is it is given a list of "good demensions" 
 * This List is a Linked List, it runs through the listing, and spits back a linkedList of "bad dimenions"
 *  which is added to the ever lenghting larger linkedList of bad dimensions 
 * 
 */
public class CutOffFilter implements Filter{
	private LinkedList goodList;	// linked list of Dimension Products
	private LinkedList badList; 	// linked list of Dimension Products
	private double value;
	
	public CutOffFilter( double val ){
		goodList = new LinkedList();
		badList = new LinkedList();
		value = val;
	}

public void filter(LinkedList DimensionProductList){
		boolean remove = false;   // All Dimension products are considered Good by default. 
		for(int j = 0; j != DimensionProductList.size(); ++j){
			DimensionProduct A = (DimensionProduct)DimensionProductList.get(j); 
			
			for(int i = 0; i != A.CoefficentList().length; ++i){
				if((A.CoefficentList()[i] > value) ||(A.CoefficentList()[i] < -value)){
					remove = true;		
					break; // Where does this break statement take me?
		}
	}
	if(remove){
		badList.add(A);
		remove = false;
	}else{
		goodList.add(A);
	}
}
	}
	public LinkedList goodList(){
		return goodList;
	}
	public LinkedList badList(){
		return badList;
	}
}
	
