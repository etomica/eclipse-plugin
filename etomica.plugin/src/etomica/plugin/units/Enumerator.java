package etomica.plugin.units;
/*
 * Created on Oct 13, 2005
 *
 * This class takes a signature (Mass, Lenght, Time, Quantity) and a list of dimensions 
 * and figures out all the different ways that you can combine those dimensions to get the 
 * orginal Signature, you must pass it a minimum of SOMETHING in the dimensional Array
 */

//import etomica.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import Jama.Matrix;
import etomica.math.discrete.CombinationIterator;
import etomica.units.Dimension;



/**
 * @author mjm35
 *
 * This class takes a  Target of Lenght the number of dimensions and an array of dimensions
 * and enumerates all the ways that those dimensions can be combined to reach that target, for example
 * if there were 3 fundemental dimensions Say: mass, lenght, time, then you can pass a target 
 * (2,3,4) which is Mass ^2, Lenght^3 and time^4, and pass the enumerator a list of dimensions
 * say: Debye, Charge, Quantity, voltage then the enumerator will come up with all possible 
 * ways to combine that list of dimensions to reach the target signature. Quantity had better be 
 * in that list or nothing will work
 */

public class Enumerator {
	
	private Dimension[] orginalDimensionPossible;
	private LinkedList dlist; 
	private double[] orginalTarget;
	private DimensionProduct[] solutionList;
	

public Enumerator(double target[], Dimension[] passedDimensions){
    
	
	if(target.length > passedDimensions.length){
		System.err.println("enumerator error: Target length longer than dimension list");
	}
	/*	double a; 
		//flips around the Target array so it works
		for(int i = 0; (i < target.length-1-i); ++i){
			a = target[i];
			target[i] = target[(target.length-1-i)];
			target[(target.length-1-i)] = a;
		}*/
		
		orginalDimensionPossible = passedDimensions;
		orginalTarget = target;

    // Sets up array of dimensions enmumerator will attempt as solution 
	Dimension[] canidateSetDimension = new Dimension[target.length];
	//This sets up the CombinatoricItorator in this case it is taking 8 things 7 at a time
	//This Itorator still works (It was tested below)
	CombinationIterator u = new CombinationIterator(passedDimensions.length,target.length);
	
	
	//System.out.println(u.getClass().toString());
	DimensionProduct possibleSolution;
	// This is the list of all possible solutions, successiful combinations are going to be 
	// Added to this "dlist"
	LinkedList dlist = new LinkedList();

	// this is going to choose the number of 
	u.reset();
	//System.out.println("");

	do{
	int[] h = u.next();
		for(int k = 0; k != h.length; ++k){ 
		//	System.out.print(h[k]);
			canidateSetDimension[k] = passedDimensions[h[k]];
		//System.out.println(canidateSetDimension[k].toString());
		}
		//System.out.println("");
		// Checks to see if possible dimension combination hits target
		possibleSolution = check(target, canidateSetDimension);
		if(possibleSolution != null){
			//System.out.println("A possible solution has been found");
			dlist.add(possibleSolution);
		}
	}while(u.hasNext() == true);
	
		solutionList = new DimensionProduct[dlist.size()];
		Iterator i = dlist.iterator();
		int j = 0;
	
		while(i.hasNext() == true){		
			DimensionProduct A = (DimensionProduct)i.next();
			solutionList[j] = A;
			++j;
		}
}

// It does not make sense to have a data type create a linked list
// and then convert it into a array to pass, and then have it be reconverted
// into a LinkedList to be worked on, thus this function returns the linkedlist of solutions
// so that the Sieve can operate on that list,  SHOULD THIS BE CLONED??? (It cannot be, 
// the cloning function only seems to work on objects...)
/*public LinkedList solutionList(){
	return dlist.clone();
}*/

// This function is given a combination of NDIM (number of Dimensions and asked to 
// figure out wither that combination can hit the target.
public DimensionProduct check(double target[], Dimension[] darray){
		
		boolean scrap = false;
		double[] exponents = new double[target.length]; 
		
		// Creating the Matrix we intend to Solve to get possible solution
		double matrixToSolve[][] = new double[darray.length][];
		for(int i = 0; i != darray.length; ++i){
			matrixToSolve[i] = (double[])darray[i].signature().clone();
		//	System.out.print(darray[i].toString());
		//	System.out.print("  ");
		}
		/*System.out.println(" ");
		System.out.println("length    mass    time    current   temperature   number   luminosity");
		for (int k = 0; k != orginalTarget.length; ++k){
			System.out.print(orginalTarget[k]);
			System.out.print("       ");
		}
		System.out.println(" ");*/
	
	/*	for(int i = 0; i != matrixToSolve.length; ++i){
		//	System.out.println(" ");
			for(int j = 0; j != matrixToSolve[0].length; ++j){
				System.out.print(matrixToSolve[i][j]);
				System.out.print("    ");
			}
		}
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");*/
		//System.out.println(" ");
        Matrix A = new Matrix(matrixToSolve);
		Jama.LUDecomposition foo = new Jama.LUDecomposition(A);

        // The LHS, B is a Nx1 matrix
        Matrix B = new Matrix(target, target.length);
        Matrix X;
        try {
            X = foo.solve(B);
        }
        catch (RuntimeException e) {
            // singular matrix
            return null;
        }
        
        exponents = X.getColumnPackedCopy();
		
		//dealing with solution from LUdecomposition solver, it checks to see we got a real solution
		for(int i = 0; i != exponents.length; ++i){
			if(Double.isNaN(exponents[i]) || Double.isInfinite(exponents[i])) {
				scrap = true;
				break;
			}else{
				scrap = false;
			}
		}
		if(scrap == false){ // The Array is an acceptable solution. 
			return new DimensionProduct(exponents, darray);
		}else{
			return null;
		}
	}


// This function prints out the problem	
public void problem(){
		
		System.out.println("Ways to enumerate the following thing: ");
		System.out.println(" ");
		for (int k = 0; k != orginalDimensionPossible.length; ++k){
			System.out.print(orginalDimensionPossible[k].toString());
			System.out.print("     ");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Into:  ");
		System.out.println("length    mass    time    current   temperature   number   luminosity");
		for (int k = 0; k != orginalTarget.length; ++k){
			System.out.print(orginalTarget[k]);
			System.out.print("       ");
		}
		System.out.println("  ");
		System.out.println("  ");
	}


public boolean noSolution(){
	if(solutionList.length == 0){
		return true;
	}else{
		return false;
	}
}

public DimensionProduct[] solution(){
		return solutionList;
}


// this function prints out the solution list
public void print(){
	for(int i = 0; i != solutionList.length; ++i){
		solutionList[i].print();
	}
}

public String toString(){
		String c = "";
		for(int i = 0; i != solutionList.length; ++i){
			c = c+(solutionList[i].toString());
		}
		return c;
	}
}
