package etomica.plugin.units;
/*
 * Created on Sep 2, 2005
 *
 * what is the new order of the dimensions? Mass, lenght, Time, Quantity, 
 * length, mass, time, current, temperature, number, luminosity.
 */


import etomica.units.*;


/**
 * @author mjm35
 *  
 */
public class TESTUNITS {
	
	public static void main(String[] args) {
		//unitStuff();
		//DemosionalStuff();
		//conversion();
		//enumeratorStuff();
	    guiStuff();
		
		//Debug();
		
	}
	public static void guiStuff(){
		Dimension[] dimensionArray2 = new Dimension[]{Length.DIMENSION, Mass.DIMENSION, Time.DIMENSION,
		Current.DIMENSION, Temperature.DIMENSION, Force.DIMENSION, ElectricPotential.DIMENSION, Current.DIMENSION, Quantity.DIMENSION, LuminousIntensity.DIMENSION};
		double[] sum3 = new double[]{1.,-1.,3.,1.,2.,1.,0.};
		Enumerator enum3 = new Enumerator(sum3, dimensionArray2);
		DimensionProduct[] sol = new DimensionProduct[enum3.solution().length];
		sol = enum3.solution();
		Sieve S = new Sieve(enum3.solution(), 3.);
		UnitPackage f = new UnitPackage(S);
	}
	public static void Debug(){
		Dimension[] dimensionArray = new Dimension[]{Dipole.DIMENSION,Charge.DIMENSION, Length.DIMENSION, Quantity.DIMENSION, 
	    Mass.DIMENSION, Pressure.DIMENSION, Pressure2D.DIMENSION,Time.DIMENSION};
		double[] sum = new double[]{0.,1.,0.,0.,4.,3.,2.};
		DimensionProduct dProduct = new DimensionProduct(sum, dimensionArray);
		CompoundDimension d = new CompoundDimension(dProduct);
	}
	public static void enumeratorStuff(){
		
		// Enumerator must take things 4 at a time
/*		Dimension[] dimensionArray = new Dimension[]{Dipole.DIMENSION,Charge.DIMENSION, Length.DIMENSION, 
		Quantity.DIMENSION, Mass.DIMENSION, Pressure.DIMENSION, Pressure2D.DIMENSION,
		Time.DIMENSION};
		double[] sum = new double[]{1.,1.,1.,1.,1.,1.,1.};
		Enumerator enum = new Enumerator(sum, dimensionArray);
		
		enum.problem();
		enum.print();
		
		double[] sum2 = new double[]{4.,6.,8.,2.,0.,2.,0.};
		Enumerator enum2 = new Enumerator(sum2, dimensionArray);
		
		enum2.problem();
		enum2.print();
	*/
		
		Dimension[] dimensionArray2 = new Dimension[]{Length.DIMENSION, Mass.DIMENSION, Time.DIMENSION,
		Current.DIMENSION, Temperature.DIMENSION, Force.DIMENSION, ElectricPotential.DIMENSION, Current.DIMENSION, Quantity.DIMENSION, LuminousIntensity.DIMENSION};
	// length, mass, time, current, temperature, number, luminosity.
		double[] sum3 = new double[]{1.,-1.,3.,1.,2.,1.,0.};
		Enumerator enum3 = new Enumerator(sum3, dimensionArray2);
	
	
		DimensionProduct[] sol = new DimensionProduct[enum3.solution().length];
		sol = enum3.solution();
		/*for(int i = 0; i != sol.length; ++i){
			sol[i].print();
		}
		System.out.println("");*/
		//enum3.solutionList();
		Sieve S = new Sieve(enum3.solution(), 3.);
		S.printGood();
	
// 		The set of all dimension products that work for that combination	
//		DimensionProduct[] sol = new DimensionProduct[enum.solution().length];
//		sol = enum.solution();
		
		/*Dimension[] dimensionArray2 = new Dimension[]{Dimension.MASS, Dimension.LENGTH, Dimension.TIME, Dimension.QUANTITY};
		double[] sum2 = new double[]{1.,4.,4.,4.};
				
		CompoundDimension A = new CompoundDimension(dimensionArray2, sum2);
		A.GenerateBase();
		for(int i = 0; i != enum.solution().length; ++i){
			CompoundDimension B = new CompoundDimension(sol[i]);
			B.GenerateBase();
		}*/
	
/*double[][]mat = new double[][]{{0.5, 2.5,-1.,5.},{2.5, 3.,6.,2.},{7.,-3.,5.,10.},{0.5,2.,-1.,3.0}};
double[] tar = new double[]{29.,9.5,21.,54.};
*/
	}

	
	public static void conversion(){
		Unit meterunit = Meter.UNIT;
		Unit Angstromunit = Angstrom.UNIT;
		Unit Gramunit = Gram.UNIT;
		Unit Secondunit = Second.UNIT;
		Unit Coulombunit = Coulomb.UNIT;
		Unit Debyeunit = Debye.UNIT;
		
		double[] signature = new double[]{3.0};
		Unit[] C = new Unit[]{meterunit};
	
		
		CompoundUnit A = new CompoundUnit(C,signature);
		
		A.generateBase();
		A.multiply(meterunit,-1);
		A.generateBase();
		A.multiply(Kelvin.UNIT,-1);
		A.generateBase();
		
	}

	public static void unitStuff(){
		Unit meterunit = Meter.UNIT;
		Unit Angstromunit = Angstrom.UNIT;
		Unit Gramunit = Gram.UNIT;
		Unit Secondunit = Second.UNIT;
		
		double[] signature = new double[]{1.0, 10.0, 1., -2.};
		Unit[] C = new Unit[]{meterunit, Angstromunit, Gramunit, Secondunit};
		
		CompoundUnit A = new CompoundUnit(C,signature);

		A.generateBase();
		System.out.println(A.dimension().toString());
		
	}
	
	/* With the Addition of the new unit system, we will need to select one of the three unit systems 
	 * to run all of our tests on, selecting a unit system sets the name of all the units that are used for the 
	 * different Dimensions. What is the Unit system that is used in the simulation? 
	 */
	public static void DemosionalStuff(){

		Dimension TimeDimension = Time.DIMENSION;
		Dimension LenghtDimension = Length.DIMENSION;
		
		double[] signature = new double[]{10.0, -1.0,1.0,1.0};
		Dimension[] C = new Dimension[]{LenghtDimension, TimeDimension, LenghtDimension, LenghtDimension};

		CompoundDimension B = new CompoundDimension(C, signature);
		
		B.GenerateBase();
		B.baseUnit();
		System.out.println(B.toString());

	}

}
