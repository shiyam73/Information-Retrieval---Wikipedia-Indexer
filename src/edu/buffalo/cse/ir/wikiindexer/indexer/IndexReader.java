/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.TreeSet;

/**
 * @author nikhillo This class is used to introspect a given index The
 *         expectation is the class should be able to read the index and all
 *         associated dictionaries.
 */
public class IndexReader {
	/**
	 * Constructor to create an instance
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The index field whose index is to be read
	 */

	private Properties props = null;
	private INDEXFIELD keyField = null;
	private INDEXFIELD valueField = null;
	private String indexPath = null;
	private String dicPath = null;

	private Map<Integer, LocalDictionary> keyDicMap = null;
	private LocalDictionary valueDictionary = null;
	private Map<Integer, Map<Integer, String>> keyReverseMap = null;
	// private Map<Integer, Map<Integer, String>> valueReverseMap = null;
	// private Map<Integer, LocalDictionary> valueDicMap = null;
	private Map<Integer, Map<Integer, BufferedPostingsList>> indexMap = null;
	private Map<Integer, BufferedPostingsList> sortedMap = null;

	public IndexReader(Properties props, INDEXFIELD field) {

		this.props = props;
		this.keyField = field;
		this.valueField = INDEXFIELD.LINK;
		this.indexPath = props.getProperty("tmp.dir") + File.separator
				+ "index" + File.separator + "merged" + keyField + "to"
				+ valueField;
		this.dicPath = props.getProperty("tmp.dir") + File.separator + "dic"
				+ File.separator;

		initialize();
		// TODO: Implement this method
	}

	public void initialize() {
		loadKeyDictionary();
		loadValueDictionary();
		loadIndex();
	}

	public void loadKeyDictionary() {
		System.out.println("Loading key dictionary");
		File keyDicFolder = new File(dicPath + File.separator + keyField);
		if (keyDicFolder.exists() && keyDicFolder.isDirectory()) {
			File[] fileList = keyDicFolder.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String fileName = fileList[i].getName();
				if (fileName.matches(".*\\d.*")) {
					Dictionary dict = new LocalDictionary(props, keyField);
					dict.loadData(fileList[i]);

					// System.out.println(dict.getTotalTerms());
					// System.out.println(fileName);
					String key = fileName.replaceAll(keyField + "Dic", "");
					key = key.replaceAll(".txt", "");
					if (keyDicMap == null) {
						keyDicMap = new HashMap<Integer, LocalDictionary>();
					}
					
					keyDicMap
							.put(Integer.parseInt(key), (LocalDictionary) dict);
				}
			}
		}
	}

	public void loadValueDictionary() {
		File valueDicFolder = new File(dicPath + File.separator + valueField);
		if (valueDicFolder.exists() && valueDicFolder.isDirectory()) {
			File[] fileList = valueDicFolder.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String fileName = fileList[i].getName();
				if (fileName.matches(".*\\d.*")) {
					valueDictionary = new LocalDictionary(props, valueField);
					valueDictionary.loadData(fileList[i]);

					/*
					 * String key = fileName.replaceAll(valueField + "Dic", "");
					 * key = key.replaceAll(".txt", ""); if (valueDicMap ==
					 * null) { valueDicMap = new HashMap<Integer,
					 * LocalDictionary>(); } valueDicMap.put((Integer)
					 * Integer.parseInt(key), (LocalDictionary) dict);
					 */
				}
			}
		}
	}

	public void loadIndex() {

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		File indexFolder = new File(indexPath);
		File[] indexFiles = indexFolder.listFiles();
		for (int i = 0; i < indexFiles.length; i++) {
			String fileName = indexFiles[i].getName();
			if (fileName.matches(".*\\d.*")) {
				try {
					fis = new FileInputStream(indexFiles[i]);
					isr = new InputStreamReader(fis, "UTF-8");
					br = new BufferedReader(isr);
					Map sortedMap = new TreeMap<Integer, BufferedPostingsList>();
					String line;
					while (((line = br.readLine()) != null)) {
						// Line is of format key = [ dicId||occ , docId||occ ];
						String tokens[] = line.split("=");

						String key = tokens[0];
						String value = tokens[1].trim().substring(1,
								tokens[1].length() - 2);

						sortedMap.put(Integer.parseInt(key.trim()),
								new BufferedPostingsList(value));
					}

					String key = fileName.replaceAll(keyField + "to"
							+ valueField, "");
					key = key.replaceAll(".dat", "");
					if (indexMap == null) {
						indexMap = new HashMap<Integer, Map<Integer, BufferedPostingsList>>();
					}
					// System.out.println(sortedMap);
					indexMap.put(Integer.parseInt(key), sortedMap);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("File does not match::" + fileName);
			}
		}
	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {

		if (keyDicMap != null) {
			int termCount = 0;
			for (Entry<Integer, LocalDictionary> entry : keyDicMap.entrySet()) {
				Dictionary dict = (Dictionary) (entry.getValue());
				termCount += dict.getTotalTerms();
			}
			return termCount;
		}
		// TODO: Implement this method
		return -1;
	}

	/**
	 * Method to get the total number of terms in the value dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		if (valueDictionary != null) {
			/*
			 * int termCount = 0; for (Entry<Integer, LocalDictionary> entry :
			 * valueDicMap.entrySet()) { Dictionary dict = (Dictionary)
			 * (entry.getValue()); termCount += valueDictionary.getTotalTerms();
			 * }
			 */
			return valueDictionary.getTotalTerms();
		}
		// TODO: Implement this method
		return -1;

		// TODO: Implement this method
	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * 
	 * @param key
	 *            : The dictionary term to be queried
	 * @return The postings list with the value term as the key and the number
	 *         of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {

		int pnum = 0;
		if (keyField == INDEXFIELD.TERM) {
			pnum = Partitioner.getPartitionNumber(key);
		}
		LocalDictionary keyDictionary = (LocalDictionary) keyDicMap.get(pnum);

		Map<Integer, BufferedPostingsList> sortedMap = null;
		if (keyField == INDEXFIELD.TERM) {
			sortedMap = (Map<Integer, BufferedPostingsList>) indexMap.get(pnum);
		} else {
			sortedMap = (Map<Integer, BufferedPostingsList>) indexMap.get(-1);
		}
		if (keyDictionary.exists(key)) {
			Integer id = keyDictionary.lookup(key);
			BufferedPostingsList bpl = sortedMap.get(id);
			// Map<String, Integer> postingsList = (Map<String, Integer>) bpl
			// .getPostingsEntry();

			
			Map<Integer, String> reverseMap = valueDictionary.reverseDict;
			/*
			 * Map<String, Integer> valueMap = (Map<String, Integer>)
			 * valueDicMap .get(pnum).dict;
			 * 
			 * if (valueReverseMap != null) { if
			 * (valueReverseMap.containsKey(pnum)) { reverseMap =
			 * valueReverseMap.get(pnum); } } else { reverseMap = new
			 * HashMap<Integer, String>();
			 * 
			 * Iterator<String> valueIt = valueMap.keySet().iterator(); while
			 * (valueIt.hasNext()) { String value = (String) valueIt.next();
			 * Integer valueId = (Integer) valueMap.get(key);
			 * reverseMap.put(valueId, value); } }
			 * 
			 * if (valueReverseMap == null) { valueReverseMap = new
			 * HashMap<Integer, Map<Integer, String>>(); }
			 * 
			 * valueReverseMap.put(pnum, reverseMap);
			 */
			Iterator<PostingsEntry> it = bpl.getPostingsEntry().iterator();
			Map<String, Integer> postingsMap = new TreeMap<String, Integer>();
			while (it.hasNext()) {
				PostingsEntry pe = (PostingsEntry) it.next();
				postingsMap.put(reverseMap.get(pe.getDocId()),
						pe.getNumOccurences());
			}
			return postingsMap;
		}
		// TODO: Implement this method
		return null;
	}

	/**
	 * Method to get the top k key terms from the given index The top here
	 * refers to the largest size of postings.
	 * 
	 * @param k
	 *            : The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the
	 *         requirement If k is more than the total size of the index, return
	 *         the full index and don't pad the collection. Return null in case
	 *         of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {

		Iterator<Integer> it = indexMap.keySet().iterator();

		Map<String, Integer> termToSizeMap = new HashMap<String, Integer>();

		PostingSizeComparator psc = new PostingSizeComparator(termToSizeMap);

		TreeMap<String, Integer> topSet = new TreeMap<String, Integer>(psc);

		while (it.hasNext()) {
			Integer pnum = (Integer) it.next();
			Map<Integer, BufferedPostingsList> sortedMap = indexMap.get(pnum);

			if(keyField != INDEXFIELD.TERM){
				pnum = 0;
			}
			Map<String, Integer> keyMap = (Map<String, Integer>) keyDicMap
					.get(pnum).dict;

			Map<Integer, String> reverseMap = (Map<Integer, String>) keyDicMap
					.get(pnum).reverseDict;

			Iterator<Integer> termIt = sortedMap.keySet().iterator();
			while (termIt.hasNext()) {

				Integer termId = (Integer) termIt.next();
				BufferedPostingsList bpl = sortedMap.get(termId);

				// System.out.println("Size::" + topSet.size());
				if (reverseMap.containsKey(termId)) {
					termToSizeMap.put(reverseMap.get(termId), bpl.size());
				} else {

					//System.out.println("Term Id::" + termId + "PNUM::" + pnum);
					//System.out.println("No Mapping found for term Id!!!");
				}

			}
		}

		topSet.putAll(termToSizeMap);
		// TODO: Implement this method
		if (topSet.isEmpty()) {
			return null;
		} else if (topSet.size() > k) {
			List<String> entryList = new ArrayList<String>();
			entryList.addAll(topSet.keySet());
			return entryList.subList(0, k);
		} else if (topSet.size() <= k) {
			return topSet.keySet();
		}
		return null;
	}

	/**
	 * Method to execute a boolean AND query on the index
	 * 
	 * @param terms
	 *            The terms to be queried on
	 * @return An ordered map containing the results of the query The key is the
	 *         value field of the dictionary and the value is the sum of
	 *         occurrences across the different postings. The value with the
	 *         highest cumulative count should be the first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {

		// List<BufferedPostingsList,>
		TreeMap<BufferedPostingsList, Integer> termPostings = null;
		for (int i = 0; i < terms.length; i++) {
			// getPostings(terms[i]);

			int pnum = Partitioner.getPartitionNumber(terms[i]);
			
			if(keyField != INDEXFIELD.TERM){
				pnum = 0;
			}
			
			LocalDictionary keyDictionary = (LocalDictionary) keyDicMap
					.get(pnum);
			if(keyField != INDEXFIELD.TERM){
				pnum = -1;
			}
			Map<Integer, BufferedPostingsList> sortedMap = (Map<Integer, BufferedPostingsList>) indexMap
					.get(pnum);
			if (keyDictionary.exists(terms[i])) {
				Integer id = keyDictionary.lookup(terms[i]);
				BufferedPostingsList bpl = sortedMap.get(id);
				if (termPostings == null) {
					termPostings = new TreeMap<BufferedPostingsList, Integer>();
				}
				termPostings.put(bpl, bpl.size());
			}
		}

		// Operate AND
		if (termPostings != null) {
			while (termPostings.size() > 1) {
				BufferedPostingsList listA = termPostings.pollFirstEntry()
						.getKey();
				BufferedPostingsList listB = termPostings.pollFirstEntry()
						.getKey();
				if ((listA.size() == -1 || listA.size() == 0)
						|| (listB.size() == -1 || listB.size() == 0)) {
					termPostings = null;
					break;
				}
				BufferedPostingsList mergedList = listA.and(listB);
				termPostings.put(mergedList, mergedList.size());
			}
		}

		if (termPostings != null) {

			Set<PostingsEntry> finalList = (Set<PostingsEntry>) termPostings
					.pollFirstEntry().getKey().getPostingsEntry();
			// PostingSizeComparator psc = new
			// PostingSizeComparator(finalList.getPostingsEntry());

			Map<String, Integer> map = new HashMap<String, Integer>();

			Iterator<PostingsEntry> it = finalList.iterator();

			while (it.hasNext()) {
				PostingsEntry pe = (PostingsEntry) it.next();
				map.put(valueDictionary.reverseDict.get(pe.getDocId()),
						pe.getNumOccurences());
			}

			PostingSizeComparator psc = new PostingSizeComparator(map);
			TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(psc);
			sorted.putAll(map);

			// TODO: Implement this method (FOR A BONUS)
			return sorted;
		} else {
			return null;
		}

	}

	static class PostingSizeComparator implements Comparator<String> {

		Map<String, Integer> unSorted;

		PostingSizeComparator(Map<String, Integer> unSorted) {
			this.unSorted = unSorted;
		}

		@Override
		public int compare(String tempA, String tempB) {
			Integer tmp1 = unSorted.get(tempA);
			Integer tmp2 = unSorted.get(tempB);
			if (tmp1.equals(tmp2)) {
				return tempB.compareTo(tempA);
			}
			return tmp2.compareTo(tmp1);
		}
	}
}
