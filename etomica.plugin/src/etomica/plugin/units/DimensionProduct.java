package etomica.plugin.units;
import etomica.units.Dimension;

/*
 * Created on Nov 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author mjm35
 *	This thing saves the product of the enumerator which is a list of dimensions and the 
 * powers that they are raised too, The list and the powers list should be the same 
 * lenght, I should write a too string function for this It also Has a print command
 *  Which prints out the result on a single line,
 * 
 * 1-18-06: I have added an Average coefficent field, which calculates and saves the Average
 * coefficent for the preticular dimension product. 
 */
public class DimensionProduct implements Comparable{
	
	DimensionProduct(double[] sum, Dimension[] darray){
		b = (Dimension[])darray.clone();
		a = (double[])sum.clone();
		c = average(a);
	}
	
	private double average(double[] a){
		double ave = 0.;
		for(int j = 0; j != a.length; ++j){
			ave = ave + a[j];
		}
		ave = (ave/((double)a.length));
		return ave;
	}
	public int compareTo(Object dimensionD){
		DimensionProduct thing = (DimensionProduct) dimensionD;
		if(thing.AverageCoefficent() > c){
			return -1;
		}
		if(thing.AverageCoefficent() == c){
			return 0;
		}
		else{
			return 1;
		}
	}
	public void print(){
		System.out.println();
		System.out.print("  ");
		top();
		System.out.println(" ");
		System.out.println("  _____________________________________________________________");
		System.out.print("  ");
		bottom();
		System.out.println(" ");
		System.out.println(" ");
	}
	private void top(){
		for (int i = 0; i != a.length; ++i){
			if(a[i] > 0){
				System.out.print(b[i].toString());
				System.out.print("^");
				System.out.print(a[i]);
				System.out.print("   ");
			}
		}
	}
	private void bottom(){
		for (int i = 0; i != a.length; ++i){
			if(a[i] < 0){
				System.out.print(b[i].toString());
				System.out.print("^");
				System.out.print(a[i]);
				System.out.print("   ");
			}
		}
	}	
	public String toString(){
		String c = "";
		for(int i = 0; i != b.length; ++i){
		c = c +(b[i].toString()+'^'+a[i]+' ');
		}
		return c;
	}
	public Dimension[] DimensionList(){return b;}
	public double[] CoefficentList(){return a;}
	public double AverageCoefficent(){return c;}
	
	private double[] a;
	private double c;
	private Dimension[]	b;
}
