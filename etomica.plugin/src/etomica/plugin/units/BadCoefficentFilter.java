package etomica.plugin.units;
import java.util.LinkedList;

/*
 * Created on Jan 19, 2006
 *
 * This filter operates like the cutt off filter, It is passed a Linked List of "good" DimensionProdcuts 
 *  And Goes through and rounds off all the coefficents to their nearest rational numbers 1/1 through 1/10, 
 * and then this filter spits back two linkedLists, one that is a good list and one is a bad list, and the bad
 * List should be added to a ever growing list of Bad Dimensions to be converted to array and sorted by average
 * coefficent at the end. 
 */

/**
 * @author mjm35
 *  This filter operates like the cutt off filter, It is passed a Linked List of "good" DimensionProdcuts 
 *  And Goes through and rounds off all the coefficents to their nearest rational numbers 1/1 through 1/10, 
 * and then this filter spits back two linkedLists, one that is a good list and one is a bad list, and the bad
 * List should be added to a ever growing list of Bad Dimensions to be converted to array and sorted by average
 * coefficent at the end. 
 */
public class BadCoefficentFilter implements Filter{
	
	private LinkedList goodList;	// linked list of Dimension Products
	private LinkedList badList; 	// linked list of Dimension Products
	
	public BadCoefficentFilter(){
		goodList = new LinkedList();
		badList = new LinkedList();
	}
	
	public void filter(LinkedList DimensionProductList){
		boolean remove = false;   // All Dimension products are considered Good by default. 
		for(int j = 0; j != DimensionProductList.size(); ++j){
			DimensionProduct A = (DimensionProduct)DimensionProductList.get(j); 
	
			for(int i = 0; i != A.CoefficentList().length; ++i){
				
//This should round off any and all deciminals coefficents that make no sense...?		
if((A.CoefficentList()[i]%1 != 0.0) ||(A.CoefficentList()[i]%1 != -0.0)){
							double working = (A.CoefficentList()[i]);
							working = (Math.abs(working%1.));
								
							for(double denominator = 1.0; denominator != 10.; ++denominator){
								working = Math.abs(working - (1./denominator));

								if(( working < 0.00001) || ((Math.abs(1-working)) < 0.00001)) {  
										//	if this is true the number needs to be replaced
										A.CoefficentList()[i] = adjust((A.CoefficentList()[i]),denominator);
										break;
									}else{
										//Then we know we have a bad data point
										remove = true; 
									}
								}
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
	private double adjust(double a, double b){
			double c = Math.abs(a);
			if(b == 1){
					c = Math.round(c);
			}	
			if(b != 1){
				if((c - Math.round(c))< 0){	// negitive, round up
					c = ((Math.round(c) - 1) + (1/b));
				}
				if((c - Math.round(c))> 0){	// positive, round down
					c = (Math.round(c) + (1/b));
				}
			}
			if(a < 0){	// A was negitive
				c = -c;
			}
		return c;
	}
	
	public LinkedList goodList(){
		return goodList;
	}
	public LinkedList badList(){
		return badList;
	}
}

