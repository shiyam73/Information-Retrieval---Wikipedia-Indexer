/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nikhillo THis class is responsible for assigning a partition to a
 *         given term. The static methods imply that all instances of this class
 *         should behave exactly the same. Given a term, irrespective of what
 *         instance is called, the same partition number should be assigned to
 *         it.
 */
public class Partitioner {
	/**
	 * Method to get the total number of partitions THis is a pure design choice
	 * on how many partitions you need and also how they are assigned.
	 * 
	 * @return: Total number of partitions
	 */
	public static Map<String,Integer> valMap = new HashMap<String, Integer>();

	public static int getNumPartitions() {
		// TODO: Implement this method
		return 6;
	}

	/**
	 * Method to fetch the partition number for the given term. The partition
	 * numbers should be assigned from 0 to N-1 where N is the total number of
	 * partitions.
	 * 
	 * @param term
	 *            : The term to be looked up
	 * @return The assigned partition number for the given term
	 */
	public static int getPartitionNumber(String term) {
		String te = term.substring(0, 1).toLowerCase();
		if(te.equalsIgnoreCase("a") || te.equalsIgnoreCase("b") || te.equalsIgnoreCase("c"))
			return 0;
		else if(te.equalsIgnoreCase("d") || te.equalsIgnoreCase("e") || te.equalsIgnoreCase("g") || te.equalsIgnoreCase("h"))
			return 1;
		else if(te.equalsIgnoreCase("i") || te.equalsIgnoreCase("j") || te.equalsIgnoreCase("k") || te.equalsIgnoreCase("l") || te.equalsIgnoreCase("m") || te.equalsIgnoreCase("n"))
			return 2;
		else if(te.equalsIgnoreCase("o") || te.equalsIgnoreCase("p") || te.equalsIgnoreCase("q") || te.equalsIgnoreCase("r"))
			return 3;
		else if(te.equalsIgnoreCase("s") || te.equalsIgnoreCase("t") || te.equalsIgnoreCase("u"))
			return 4;
		else
			return 5;
	}
}
