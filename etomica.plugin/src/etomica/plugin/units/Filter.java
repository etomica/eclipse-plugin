package etomica.plugin.units;
import java.util.LinkedList;

/*
 * Created on Jan 22, 2006
 *  This is the filter interface, It requires 3 things, A "filter" function, 
 * which requires a linkedlist of DimensionProducts which the Seive will use the filter
 * to filter out, additionally, the two lists from the filter itself, the good list and 
 * the Bad list how do I work with generic filters in the sieve class?
 */


/**
 * @author mjm35
 *
 *  This is the filter interface, It requires 3 things, A "filter" function, 
 * which requires a linkedlist of DimensionProducts which the Seive will use the filter
 * to filter out, additionally, the two lists from the filter itself, the good list and 
 * the Bad list how do I work with generic filters in the sieve class?
 */
public interface Filter {
	
	public void filter(LinkedList linkedList);
	public LinkedList goodList();
	public LinkedList badList();
}
