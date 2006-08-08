package etomica.plugin.units;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
;
/*
 * Created on Jan 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author mjm35
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Sieve2 {
	
	private DimensionProduct[] goodList;
	private DimensionProduct[] badList;	
/*
	
	public DimensionProduct[] goodList(){
		return goodList;
	}
	public DimensionProduct[] badList(){
		return badList;
	}*/
	
	public Sieve2(DimensionProduct[] orginalList, double value){
		
		boolean remove = false; 
		LinkedList bList = new LinkedList(); 
		LinkedList gList = new LinkedList(); 
		
	for(int i = 0; i != orginalList.length; ++i){
		for(int j = 0; j != orginalList[i].CoefficentList().length; ++j){
			

			
// This should round off any and all deciminals coefficents that make no sense...?		
if((orginalList[i].CoefficentList()[j]%1 != 0.0) ||(orginalList[i].CoefficentList()[j]%1 != -0.0)){
			double working = (orginalList[i].CoefficentList()[j]);
			working = (Math.abs(working%1.));
				
			for(double denominator = 1.0; denominator != 10.; ++denominator){
				working = Math.abs(working - (1./denominator));

				if(( working < 0.00001) || ((Math.abs(1-working)) < 0.00001)) {  
						//	if this is true the number needs to be replaced
					orginalList[i].CoefficentList()[j] = adjust((orginalList[i].CoefficentList()[j]),denominator);
						break;
					}else{
						//Then we know we have a bad data point
						remove = true; 
					}
				}
			}
		
// This removes demensions over and under a certain value
if((orginalList[i].CoefficentList()[j] > value) ||(orginalList[i].CoefficentList()[j] < -value)){
					remove = true;		
					break;
				}

			}
		if(remove){
				bList.add(orginalList[i]);
				remove = false;
			}else{
				gList.add(orginalList[i]);
			}

		}
	goodList = convert(gList);
	badList = convert(bList);
	Arrays.sort(goodList);
	Arrays.sort(badList);
	
}

	
	// the purpose of this function is to adjust the double value so that it fits within 
	// Number.00000, or Number.500000, it replaces the a with an decimal closest to 1/a;
	// example: 6.3333333222 ---> 6.3333333
	private double adjust(double a, double b){
/*		System.out.print("orginal: ");
		System.out.print(a);*/
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
/*		System.out.print("  Adjusted: ");
		System.out.println(c);*/
		return c;
	}
	

	public void print(){
	
		System.out.println(" ");
		System.out.println(" The Good List:  ");
		for(int j = 0; j != goodList.length; ++j){
			System.out.println(goodList[j].toString());
		}
		System.out.println(" ");
		System.out.println(" The Bad List:  ");
		for(int j = 0; j != badList.length; ++j){
			System.out.println(badList[j].toString());
		}
	}
	
// This function converts a linkedList to an array of Dimensional Products 
// Since ToArray does'nt work..
	private DimensionProduct[] convert(LinkedList AnyList){
			DimensionProduct[] list = new DimensionProduct[AnyList.size()];
			Iterator i = AnyList.iterator();
			int j = 0;
			while(i.hasNext()){		
				DimensionProduct A = (DimensionProduct)i.next();
				list[j] = A;
			//	System.out.println(list[j].toString());
				++j;
			}
			return list;
	}

	private LinkedList convert(DimensionProduct[] Array){
		LinkedList newList = new LinkedList();
		for(int i = 0; i != Array.length; ++i){
			newList.add(Array[i]);
		}
	return newList;
}
}
