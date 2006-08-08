package etomica.plugin.units;
/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import etomica.units.Dimension;
import etomica.units.Unit;
import etomica.units.systems.UnitSystem;

public class CompoundDimension extends Dimension {

	
	public CompoundDimension(DimensionProduct dProduct) {
		
		super("", new double[]{4.,4.,1.,4.,3.,2.,5.});
		// Here is my problem, I need some lines of code (I do this below) to calculate the signature and the name of this 
		// compoundDimension class, I cannot do this because the super consturctor must be the first thing in all the 
		// constructor classes, therefore I made up some dummy crap super consturctor that is clearly not the one we are dealing 
		//with to eliminate the problem 
		//super(Dimension(stringName, compoundDimensionSignature));
		dimensionArray = dProduct.DimensionList();
		double[] exponents = dProduct.CoefficentList();
		
		String string = "";
		double[] sum = new double[Dimension.N_BASE];

		if (exponents.length == dimensionArray.length) {
			for (int i = 0; i != dimensionArray.length; ++i) {
				sum = addArray(sum, multiplyArray(dimensionArray[i].signature(), exponents[i]));
				string += dimensionArray[i].toString() + "^(" + exponents[i] + ") ";
			}
			compoundDimensionSignature = sum;

			stringName = string;
		}
		
	}
	
	
	public CompoundDimension(Dimension[] arrayOfDimensions, double[] exponents) {
		super("", 1.,1.,1.,1.,1.,1.,1.);
		dimensionArray = arrayOfDimensions;
	
		String string = "";
		double[] sum = new double[Dimension.N_BASE];

		if (exponents.length == arrayOfDimensions.length) {
			for (int i = 0; i != arrayOfDimensions.length; ++i) {
				sum = addArray(sum, multiplyArray(arrayOfDimensions[i].signature(), exponents[i]));
				string += arrayOfDimensions[i].toString() + "^(" + exponents[i] + ") ";
			}
			compoundDimensionSignature = sum;

			stringName = string;
		}
	}
	public Unit getUnit(UnitSystem a){
		Unit[] C = new Unit[dimensionArray.length];
		for (int i = 0; i != dimensionArray.length; ++i) {
			C[i] = dimensionArray[i].getUnit(a);
		}
		CompoundUnit A = new CompoundUnit(C, compoundDimensionSignature);
		return A;
	}

	private void printArray2(double[] Marray) {
		System.out.print("{ ");
		for (int i = 0; i != Marray.length; ++i) {
			System.out.print(" ");
			System.out.print(Marray[i]);
			System.out.print(", ");
		}
		System.out.println(" }");
	}

	//returns the Addition of both arrays (note must be same lenght)
	private double[] addArray(double[] Aarray, double[] Barray) {
		double sum[] = new double[Aarray.length];
		for (int i = 0; i != sum.length; ++i) {
			sum[i] = Aarray[i] + Barray[i];
		}
		return sum;
	}

	//returns double array which is the Array times the Expodent
	private double[] multiplyArray(double[] Marray, double Expodent) {
		double sum[] = new double[Marray.length];
		for (int i = 0; i != sum.length; ++i) {
			sum[i] = Marray[i] * Expodent;
		}
		return sum;
	}

	/*public Unit defaultIOUnit() {
		Unit[] C = new Unit[dimensionArray.length];
		for (int i = 0; i != dimensionArray.length; ++i) {
			C[i] = dimensionArray[i].GetUnit();
		}
		CompoundUnit A = new CompoundUnit(C, compoundDimensionSignature);
		return A;
	}*/

	public String toString() {
		return stringName;
	}

	public Class baseUnit() {
		return CompoundUnit.class;
	}

	public void printEverything() {
		for (int i = 0; i != compoundDimensionSignature.length; ++i) {
			System.out.print("The ");
			System.out.print(i);
			System.out
					.print(" Element in the compound dimensional Signature is: ");
			System.out.println(compoundDimensionSignature[i]);
		}
		System.out.println(" ");
		for (int i = 0; i != dimensionArray.length; ++i) {
			System.out.print("The ");
			System.out.print(i);
			System.out.print(" Element in the dimensional Array is: ");
			System.out.println(dimensionArray[i].toString());
		}

	}

	public double[] signature() {
		return compoundDimensionSignature;
	}



	public void GenerateBase() {

		System.out.println("Base Dimensions for object are: ");
		System.out.println();
		System.out.print("1 Million ");
		System.out.print("     ");
		Top();
		System.out.println(" ");
		System.out.println("              -----------------------------------------------");
		System.out.print("              ");
		Bottom();
		System.out.println(" ");
		System.out.println(" ");
	}

	private void Top() {
		for (int i = 0; i != 4; ++i) {
			//Angstroms, Pico Second, and AMU's
			if (compoundDimensionSignature[i] > 0) // then the thing is positive
												   // and must go on the top.
			{
				if (i == 0) // then this is Mass
				{
					System.out.print("AMU");
					System.out.print(" ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
				if (i == 1) // then this is Length
				{
					System.out.print("Angstroms");
					System.out.print(" ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
				if (i == 2) // then this is time
				{
					System.out.print("Pico Second");
					System.out.print(" ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
				if (i == 3) // then this is time
				{
					System.out.print("Moles ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
			}
		}
	}

	private void Bottom() {
		for (int i = 0; i != 4; ++i) {

			if (compoundDimensionSignature[i] < 0) // then the thing is positive
												   // and must go on the top.
			{
				if (i == 0) // then this is Mass
				{
					System.out.print("AMU");
					System.out.print(" ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
				if (i == 1) // then this is Length
				{
					System.out.print("Angstroms");
					System.out.print(" ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
				if (i == 2) // then this is time
				{
					System.out.print("Pico Second");
					System.out.print(" ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
				if (i == 3) // then this is Moles
				{
					System.out.print("Moles ^");
					System.out.print(compoundDimensionSignature[i]);
					System.out.print("  ");
				}
			}
		}
	}

	private double[] compoundDimensionSignature;   
	//The array of coefficents that goes with the array of dimensions
	private Dimension[] dimensionArray;
	// The Array of deimensions that is part of this "compound Dimension"
	private String stringName;
	// "essentially the Name of this compound dimension
}
