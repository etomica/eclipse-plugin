package etomica.plugin.units;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;


/*
 * Created on Nov 22, 2005
 * 1-13-06
 * It does not make allot of sense to have something generated and worked with as a linkedList
 * and then convert it to an array to be accessed outside the class, and then have that same
 * data type the "solution list" come here and be converted back into a linked List for use
 * however that is exactly what I am doing here, I ran into a slew of problems trying to get
 * the linkedlist outside the class that i was using, so I was forced to do this instead. 
 */

/**
 * @author mjm35
 *	This class takes a array of dimensionProducts and sifts through them throwing away 
 *any dimension products which do not belong. and seperates them into "good" and "bad", good
 *being kosher demensions and the bad being other, An idea has been suggested that the
 * seive should take statistics on which dimensions the user chooses and reorder the  
 * demensions based upon that order, ideally that would be saved as a field in the 
 * Dimension product class, but It would require a data file infile/outfile setup to work...
 */

public class Sieve {
	
	private DimensionProduct[] goodList;
	private DimensionProduct[] badList;	

	
	public DimensionProduct[] goodList(){
		return goodList;
	}
	public DimensionProduct[] badList(){
		return badList;
	}
	
	public Sieve(DimensionProduct[] orginalList, double cutoff){

		LinkedList bList = new LinkedList(); 
		LinkedList gList = new LinkedList(); 
		LinkedList oList = convert(orginalList);
//		How do I convert something from An Array to a LinkedList ?????????
		
// 		Filter 1
		BadCoefficentFilter A = new BadCoefficentFilter();
		A.filter(oList);
		bList = A.badList();
		gList = A.goodList();
		
//		Filter 2
		CutOffFilter B = new CutOffFilter(cutoff);
		B.filter(gList);
		bList.addAll(B.badList());
		gList = B.goodList();
		
		badList = convert(bList);
		goodList = convert(gList);	
// This is a Merge Sort, and it only works because Dimension Product implements the Comparable interface
		Arrays.sort(goodList);
		Arrays.sort(badList);
		//print();
	
	
	}
/*	
public void sort(double value){
		
		boolean remove = false; 
		LinkedList bList = new LinkedList(); 
		LinkedList gList = new LinkedList(); 
		
	for(int i = 0; i != goodList.length; ++i){
		for(int j = 0; j != goodList[i].CoefficentList().length; ++j){
			

			
// This should round off any and all deciminals coefficents that make no sense...?		
if((goodList[i].CoefficentList()[j]%1 != 0.0) ||(goodList[i].CoefficentList()[j]%1 != -0.0)){
			double working = (goodList[i].CoefficentList()[j]);
			working = (Math.abs(working%1.));
				
			for(double denominator = 1.0; denominator != 10.; ++denominator){
				working = Math.abs(working - (1./denominator));

				if(( working < 0.00001) || ((Math.abs(1-working)) < 0.00001)) {  
						//	if this is true the number needs to be replaced
						goodList[i].CoefficentList()[j] = adjust((goodList[i].CoefficentList()[j]),denominator);
						break;
					}else{
						//Then we know we have a bad data point
						remove = true; 
					}
				}
			}
		
// This removes demensions over and under a certain value
if((goodList[i].CoefficentList()[j] > value) ||(goodList[i].CoefficentList()[j] < -value)){
					remove = true;		
					break;
				}

			}
		if(remove){
				bList.add(goodList[i]);
				remove = false;
			}else{
				gList.add(goodList[i]);
			}

		}
// This should reorganize the goodlist dependent upon the average coefficent.
// The Average Coefficent is a data field inside the DimensionProduct Class. 
	//	Reorganize(gList);
}
*/
	
	// the purpose of this function is to adjust the double value so that it fits within 
	// Number.00000, or Number.500000, it replaces the a with an decimal closest to 1/a;
	// example: 6.3333333222 ---> 6.3333333
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
	
	public void printGood(){
		
			System.out.println(" ");
			System.out.println(" The Good List:  ");
			for(int j = 0; j != goodList.length; ++j){
				goodList[j].print();
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