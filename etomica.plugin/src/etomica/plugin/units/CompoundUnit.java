package etomica.plugin.units;
/*
 * Created on Sep 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import etomica.units.*;
import etomica.units.Dimension;



/**
 * Compound Unit, a Sub Class, of the interface or Super class Units, This Object called 
 * compound unit, Takes an Array of units called Array of Units, and takes the expodents associated
 * with that Array, so for Example if The object (compound Unit) That you wanted to represent 
 * was Velocity then you would make a compound Unit, The Following Way:
 *  Velocity = CompoundUnit(    new Unit[]{meter,second},     new double[]{1.0,-1.0} );
 * Notice that with in this declaration you defined two arrays, one of doubles, one of units
 *  The first thing is the Meters, and the expodent is, 1 so it is meters on top
 *  The second thing is Seconds, and the expodent is -1 so it is Seconds on the bottom
 * So the compound Unit is Meters/Second, or Meters^1 * Seconds^(-1)
 */
public class CompoundUnit implements Unit{

	public CompoundUnit(Unit[] arrayOfUnits, double[] exponents){
		unitArray = arrayOfUnits;
		toSim = 1.0;
	
	// This Calculates the Combined Final Unit Signature in terms of Mass, Leght, Time, Quanity
		if(unitArray.length != exponents.length){
			throw new IllegalArgumentException("length mismatch");
		}
		
			double MassLenghtTimeForUnitInArray[];
			double sum[] = new double[Dimension.N_BASE];
			
			for(int i =0; i != unitArray.length; ++i){ 
				toSim *= Math.pow(unitArray[i].toSim(1.0),exponents[i]);
				//string += unitArray[i].symbol() + "^(" + exponents[i] + ") ";
				
				MassLenghtTimeForUnitInArray = unitArray[i].dimension().signature();
				for (int j=0; j != sum.length;  ++j){  
					sum[j] = sum[j] + (MassLenghtTimeForUnitInArray[j]*exponents[i]);
				}
			}
			compoundSignature = sum;
			
			
			
		//	dimension = new CompoundDimension(compoundSignature);
			
		my_mass = "AMU";
		my_time = "Picosecond";
		my_lenght = "Angstroms";
		my_quantity = "moles";
		symbol = my_mass + "^("+ compoundSignature[0] + ") " + my_lenght + "^("+ compoundSignature[1]
	    + ") " + my_time + "^(" + compoundSignature[2] + ") " + my_quantity + "^("+ compoundSignature[3]+")";
		
	}
	
	private void calculateSymbol(){
		symbol = my_mass + "^("+ compoundSignature[0] + ") " + my_lenght + "^("+ compoundSignature[1] +
	 ") " + my_time + "^(" + compoundSignature[2] + ") " + my_quantity + "^("+ compoundSignature[3]+")";
	}
	
// *** Things inheirented from the Fact that Compound unit Extends The Unit interface
	public Dimension dimension() {
		Dimension[] dimensionArray = new Dimension[4];

		dimensionArray[0] = Mass.DIMENSION;
		dimensionArray[1] = Length.DIMENSION;
		dimensionArray[2] = Time.DIMENSION;
		dimensionArray[3] = Quantity.DIMENSION;

		CompoundDimension B = new CompoundDimension(dimensionArray, compoundSignature);
		return B;
	}

	// This Preforms the conversion between the simulation Units and the Compound unit
	public double fromSim(double x) {return x/toSim;}
	
	//This indictates wheither the Compound Unit will allow prefixs, which is false	
	public boolean prefixAllowed() {return false;}
	
	// This Symbol is a String which shows what the Coumpound Unit entails
	public String symbol() {return symbol;}
	
	// This To Sim function takes what the compound unit is, and converts it to Simulation units
	public double toSim(double x) {return (toSim*x);}
	
	// This getsignature Function Returns the Signature of the Coumpound Unit 
	public double[] getSignature(){return compoundSignature;}
		
	public String toString(){return symbol;}
	
	private void printArray(String[] Marray){
		System.out.print("{ ");
	for(int i =0; i != Marray.length; ++i){
		System.out.print(" ");
		System.out.print(Marray[i]);
		System.out.print(", ");
	}
	System.out.println(" }");
}
	
	public void printArray(double[] Marray){
			System.out.print("{ ");
		for(int i =0; i != Marray.length; ++i){
			System.out.print(" ");
			System.out.print(Marray[i]);
			System.out.print(", ");
		}
		System.out.println(" }");
	}
	
	private void printValues(){
		for(int i =0; i != unitArray.length; ++i){
			System.out.print("Looping through Unit Array: in spot ");
			System.out.print(i);
			System.out.print(" We have: ");
			System.out.println(unitArray[i].toSim(1.0));
			}
	}
	
	private void printArray(char[] charsToPrint){
		for(int i =0; i != charsToPrint.length; ++i){
			System.out.print(charsToPrint[i]);
			}
		System.out.println();
	}

	
	private void printSignature(){
		for(int i = 0; i != compoundSignature.length; ++i){
			System.out.print("Looping through Signature: in spot ");
			System.out.print(i);
			System.out.print(" We have: ");
			System.out.println(compoundSignature[i]);
		}
		
	}


	// This is like adjusting the toSim value for Units OTHER than base units
	// for example if you made a compound unit, and then wanted to change from 
	// angstroms on the top to Meters on the bottom you would call 
	// CompoundUnit.multiply(Meter, -2)  The numerical value calculated here, if 
	// multiplied by 1 of the compound units would be the value in simulation units. 
	public void multiply(Unit unitToMultiply, double Expodent){
		

		double multiplerNumericalValue; 
		double newSignature[] = unitToMultiply.dimension().signature();
		String sString[] = new String[4];
		
		newSignature = multiplyArray(newSignature, Expodent);
		newSignature = addArray(newSignature, compoundSignature);

		multiplerNumericalValue = Math.pow(unitToMultiply.toSim(1.0),Expodent);
		multiplerNumericalValue = multiplerNumericalValue * toSim(1.0);
		
		sString = findUnit(unitToMultiply);
	
		
		assigner(sString);
		toSim = multiplerNumericalValue;
		compoundSignature = newSignature;
		calculateSymbol();
		

	}
	
	// This Function Takes a array of Strings with the Units in them and assigns them to myMass, myTime Ect..
	// You should only be calling this if you are dealing with a single dimension * a compound Dimension. 
	private void assigner (String unitNamesToAssign[]){
		for(int i = 0; i != unitNamesToAssign.length; ++i){
			if (unitNamesToAssign[i] != null){
				if(i == 0){		my_mass   	= unitNamesToAssign[i];		} 	// this is Mass
				if(i == 1){		my_lenght	= unitNamesToAssign[i];		} 	// this is lenght
				if(i == 2){		my_time 	= unitNamesToAssign[i];		} 	// this is time
				if(i == 3){		my_quantity = unitNamesToAssign[i];		} 	// this is Quanity
			}
		}
	}
	

	// This returns a double array which is the array sent times the expodent.
	private double[] multiplyArray( double[] Marray, double Expodent){
		double sum[] = new double[Marray.length];
		for(int i = 0; i != sum.length; ++i){
			sum[i] = Marray[i]*Expodent;
		}
		return sum;
	}
	
	// This Unit finds the UNit of interest from a string that it is Past, The string that the 
	// function is past is the symbol of the unit that you want, it picks through the string, 
	// and will eventually return an array of strings with the Order Lenght Mass Time Qunatity. 
	private String[] findUnit(Unit Munit){
		String cstring = Munit.toString();
		
		char chars[] = new char[cstring.length()];
		String Strings[] = new String[4];
		double signature[] = new double[4];
		
	/*	System.out.print(Munit.toString());
		System.out.print("   ");
		printArray2(Munit.dimension().signature());
		*/
	
		// in this Situation the UnitTo multiply is NOT a comp Now It will Arrange the Symbols
		// in the String Array By Mass Lenght Time, Quanitity, and then return this String Array
		// The place where this function is Used Above will know that When the Sring IS Null, 
		// That means that There is nothing there. 

// *******33******* UNITS		
//		6.022e22; conversion from kg-m^2/s^2 to Dalton-A^2/ps^2 JOULES
// Constants.AVOGADRO, //conversion from grams to Daltons 	GRAMS
// Constants.AVOGADRO*1000.*1e20*1e-24*(1.6e-19), // conversion from eV to Dalton-A^2/ps^2
//        	1e+27, //conversion from liters to Angstroms^3
//	Constants.BOLTZMANN_K,//convert to simulation energy units by multiplying by Boltzmann's constant Kelvin
//		1e+12, //conversion from seconds to picoseconds		SECONDS
//	Constants.AVOGADRO, //6.022e22; conversion from moles to count (number) Mole
// 	        	1e+10, //conversion from meters to Angstroms   	Meter
//		*****ESU*****
//	Math.sqrt(Constants.AVOGADRO*1e24*1e-24), 
//	7.76e11; conversion from (g-cm^3/s^2)^(1/2) to (amu-A^3/ps^2)^(1/2)	
	
		
		if(siever(Munit) == 2){		// 2 for Single dimension Units
		
				for(int i = 0; i != Munit.dimension().signature().length; ++i){
					if(Munit.dimension().signature()[i] != 0.0){	Strings[i] = Munit.toString();	}
				}
				return Strings;
		}
		
	/*	if(siever(Munit) == 5){		// 5 for Single Dimensions, multi-- ex (Meter^3, 0, 0 ,0)
		
			
			return Strings;
		}
		
		*/
		if(siever(Munit) == 4){		// 4 for compound Units
		
			// This is the case where there is a compound unit, In here, we take the "toString of the 
			// Unit we are multiplying and Splice it up so that we can find the Mass, Lenght, Time, Quanity 
			// Words, and Make a String Array (Called Strings where these are stored, so that they maybe swapped
			// With myMass, myTime, myLength and myQuanity, so that you can multiply these two Units togethter
			// As of right now this function works.
			
			String bString;
			int start = 0, finish;
			chars = cstring.toCharArray();
			finish = cstring.indexOf('^', start);
			bString = getString(chars,start,finish);
			Strings[0] = bString;
			
			for(int u= 0; u != 3; ++u){
				start = cstring.indexOf(' ', finish);
				++start;
				finish = cstring.indexOf('^', start);	
				bString = getString(chars,start,finish);
				Strings[u+1] = bString;
				}
				return Strings;
			}
			else{		// 1 for Precentages & Radians & 3 for MultiDemsional Units
			
				Strings[0] = my_mass;
				Strings[1] = my_lenght; 
				Strings[2] = my_time;
				Strings[3] = my_quantity;
				return Strings;
			}
	}
	
	//returns the Addition of both arrays (note must be same lenght)
	private double[] addArray( double[] Aarray, double[] Barray){
		double sum[] = new double[Aarray.length];
		for(int i = 0; i != sum.length; ++i){
			sum[i] = Aarray[i] + Barray[i];
		}
		return sum;
	}
	
	// This Function Finds Our what kind of unit you are dealing with 
	// it returns 4 for compoound, 3 for MultiDemsional Units 2 for Single dimension, and 
	// 1 for Precentages & Radians
	private int siever(Unit Munit){
		int check = 0;
		boolean greater = false;
		if(Munit.toString().indexOf('^') ==  -1){ // This maybe single or multidemensional 
			for(int i = 0; i != Munit.dimension().signature().length; ++i){
				if(Munit.dimension().signature()[i]!=0.0){	++check;	
				if(Munit.dimension().signature()[i]>1.0) {greater = true;}}
			}
			
			if(check == 0)	{	return 1; 	}	// Dealing with Percentages or Radians
			if(check == 1)	{	
			if(greater) {	
								return 5;		// Dealing with 0, Meter^3, 0 , 0 
				}else{
								return 2; 	}}	// Dealing with 0, Meter, 0 , 0
			else			{	return 3;	}	// Dealing with 0, Meter^1, AMU^2 , 0
		 }
			else			{   return 4;  	}	// Dealing with a CompoundUnit
	}	
	
	
// Pass it a Character array and it will extract out the String from start to Finish 
	private String getString(char[] cArray, int start, int finish){
		char[] aArray = new char[(finish-start)];
		String aString ="";
		int j = 0;
		
		for(int i = start; i != finish; ++i){
			aArray[j] = cArray[i];
			++j;
		}
		for(int i = 0; i != aArray.length; i++){
			aString = aString + aArray[i];
		}
		return aString;
	}
	
	public void generateBase(){
		
		System.out.println("Base Units for object are: ");
		System.out.println();
		System.out.print(toSim(1.0));
		System.out.print("     ");
		top();
		System.out.println(" ");
		System.out.println("              -----------------------------------------------");
		System.out.print("              ");
		bottom();
		System.out.println(" ");
		System.out.println(" ");
	}
	private void top(){
		for (int i = 0; i != 4; ++i)
		{
		//Angstroms, Pico Second, and AMU's
			if(compoundSignature[i] > 0) // then the thing is positive and must go on the top. 
			{
				if (i == 0) // then this is Mass
				{
					System.out.print(my_mass);
					System.out.print(" ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
				if (i == 1) // then this is Length
				{
					System.out.print(my_lenght);
					System.out.print(" ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
				if (i == 2) // then this is time
				{
					System.out.print(my_time);
					System.out.print(" ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
				if (i == 3) // then this is time
				{
					System.out.print("Moles ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
			}
		}
	}
	private void bottom(){
		for (int i = 0; i != 4; ++i)
		{
			
			if(compoundSignature[i] < 0) // then the thing is positive and must go on the top. 
			{
				if (i == 0) // then this is Mass
				{
					System.out.print(my_mass);
					System.out.print(" ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
				if (i == 1) // then this is Length
				{
					System.out.print(my_lenght);
					System.out.print(" ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
				if (i == 2) // then this is time
				{
					System.out.print(my_time);
					System.out.print(" ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
				if (i == 3) // then this is Moles
				{
					System.out.print("Moles ^");
					System.out.print(compoundSignature[i]);
					System.out.print("  ");
				}
			}
		}
	}	
	
	private double[] compoundSignature;	// This is the signature, it is calculated from the units
	private String symbol; 				// Ths symbol should not be touched that is why it is final
	private double toSim;				// This is the To Sim Value, used for conversions
	private Unit[] unitArray;			// This is an Array of the units contained with in Comp.Unit
	private String my_mass; 			// These are used for Labeling 
	private String my_lenght;
	private String my_time;
	private String my_quantity;
}