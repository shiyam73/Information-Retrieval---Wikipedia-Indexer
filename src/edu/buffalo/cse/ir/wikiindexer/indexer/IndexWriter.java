/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author nikhillo This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8333064727889625062L;
	/**
	 * Constructor that assumes the underlying index is inverted Every index
	 * (inverted or forward), has a key field and the value field The key field
	 * is the field on which the postings are aggregated The value field is the
	 * field whose postings we are accumulating For term index for example: Key:
	 * Term (or term id) - referenced by TERM INDEXFIELD Value: Document (or
	 * document id) - referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 */

	Properties props = null;
	Map<Integer, BufferedPostingsList> index = null;
	boolean createDir = false;
	private File indexFile = null;
	private LocalDictionary keyDictionary = null;
	private LocalDictionary valueDictionary = null;
	private File dir = null;
	private INDEXFIELD keyField;
	private INDEXFIELD valueField;
	private int id = 0;
	
	public boolean isComplete = false;

	int pnum = -1;

	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}

	/**
	 * Overloaded constructor that allows specifying the index type as inverted
	 * or forward Every index (inverted or forward), has a key field and the
	 * value field The key field is the field on which the postings are
	 * aggregated The value field is the field whose postings we are
	 * accumulating For term index for example: Key: Term (or term id) -
	 * referenced by TERM INDEXFIELD Value: Document (or document id) -
	 * referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 * @param isForward
	 *            : true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField, boolean isForward) {
		this.keyField = keyField;
		this.valueField = valueField;
		keyDictionary = new LocalDictionary(props, keyField);
		valueDictionary = new LocalDictionary(props, valueField);
		index = new TreeMap<Integer, BufferedPostingsList>();
		this.props = props;
		// TODO: Implement this method
	}

	/**
	 * Method to make the writer self aware of the current partition it is
	 * handling Applicable only for distributed indexes.
	 * 
	 * @param pnum
	 *            : The partition number
	 */
	public void setPartitionNumber(int pnum) {
		this.pnum = pnum;
		keyDictionary.setPartitionNumber(pnum);
		// TODO: Optionally implement this method
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances)
			throws IndexerException {
		if (index.containsKey(keyId)) {
			BufferedPostingsList valueList = (BufferedPostingsList) index
					.get(keyId);
			valueList.addPostingEntry(valueId, numOccurances);

		} else {
			BufferedPostingsList valueList = new BufferedPostingsList(
					props, keyField, valueField);
			valueList.addPostingEntry(valueId, numOccurances);
			index.put(keyId, valueList);
		}
		// addToIndex(String.valueOf(keyId),String.valueOf(valueId),numOccurances);

		// TODO: Implement this method
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances)
			throws IndexerException {
		// System.out.println("Add to Index::" + keyId + "::Value::" + value
		// + "Occurences::" + numOccurances);
		Integer valueId = keyDictionary.lookup(value);
		addToIndex(keyId, valueId, numOccurances);

	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances)
			throws IndexerException {
		// System.out.println("Add to Index::" + key + "::Value::" + valueId
		// + "Occurences::" + numOccurances);

		Integer keyId = keyDictionary.lookup(key);
		addToIndex(keyId, valueId, numOccurances);
		/*
		 * if (index.containsKey(key)) {
		 * 
		 * @SuppressWarnings("unchecked") BufferedPostingsList<Integer,Integer>
		 * valueList = (BufferedPostingsList<Integer,Integer>) index.get(key);
		 * valueList.addPostingEntry(valueId, numOccurances);
		 * 
		 * } else { BufferedPostingsList<Integer,Integer> valueList = new
		 * BufferedPostingsList<Integer,Integer>(props,keyField,valueField);
		 * valueList.addPostingEntry(valueId, numOccurances); index.put(key,
		 * valueList); }
		 */

		// TODO: Implement this method
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances)
			throws IndexerException {
		// System.out.println("Add to Index::" + key + "::Value::" + value
		// + "Occurences::" + numOccurances);
		Integer keyId = keyDictionary.lookup(key);
		Integer valueId = valueDictionary.lookup(value);
		addToIndex(keyId, valueId, numOccurances);

		/*
		 * if (index.containsKey(key)) { BufferedPostingsList<String,Integer>
		 * valueList = (BufferedPostingsList<String,Integer>) index.get(key);
		 * valueList.addPostingEntry(value, numOccurances);
		 * 
		 * } else { BufferedPostingsList<String,Integer> valueList = new
		 * BufferedPostingsList<String,Integer>(props,keyField,valueField);
		 * valueList.addPostingEntry(value, numOccurances); index.put(key,
		 * valueList); }
		 */
		// addToIndex(key, value, numOccurances);
		// TODO: Implement this method
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// System.out.println(index);
		long start = System.currentTimeMillis();
		System.out.println("Start time for file write::" + start + "pnum::"
				+ pnum + "field::" + keyField);
		OutputStreamWriter os = null;
		FileOutputStream indexFout = null;
		try {

			dir = new File(props.getProperty("tmp.dir") + File.separator
					+ "index" + File.separator + "partioned" + File.separator
					+ keyField + "to" + valueField + File.separator + pnum);
			createDir = dir.mkdirs();

			// System.out.println(dir.getAbsolutePath());
			if (createDir || dir.exists()) {

				/*
				 * Properties indexProperties = new Properties() {
				 * 
				 * @Override public Set<Object> keySet() { return
				 * Collections.unmodifiableSet(new TreeSet<Object>(
				 * super.keySet())); }
				 * 
				 * public synchronized Enumeration<Object> keys() { return
				 * Collections.enumeration(new TreeSet<Object>(
				 * super.keySet())); } };
				 */
				indexFile = new File(dir.getAbsoluteFile() + File.separator
						+ keyField + "to" + valueField + "index" + (++id)
						+ ".dat");
				indexFout = new FileOutputStream(indexFile);
				os = new OutputStreamWriter(indexFout);
				
				int i = 0;
				for (Entry<Integer, BufferedPostingsList> entry : index
						.entrySet()) {
					StringBuilder sb = new StringBuilder(entry.getKey()
							.toString());
					sb.append(" = ");
					sb.append(entry.getValue().toString());
					sb.append("\n");
					os.write(sb.toString());
					i++;
					if (i % 200 == 0 && i > 1) {
						os.flush();
					}
				}

				os.flush();
				// indexProperties.putAll(index);
				// indexProperties.store(os, null);
				index.clear();
				long end = System.currentTimeMillis();
				System.out.println("Total time to write ::" + (end - start)
						+ "pnum::" + pnum + "field::" + keyField);
				// System.out.println("Partitioner::"+Partitioner.valMap.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				indexFout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// TODO Implement this method

		/*
		 * try { File dir = new File(props.get("tmp.dir")+File.separator+pnum);
		 * createDir = dir.mkdirs(); System.out.println(dir.getAbsolutePath());
		 * if (createDir || dir.exists()) { keyDicFile = new
		 * File(dir.getAbsolutePath() + File.separator + "keyDictionary.txt");
		 * if (!(keyDicFile.exists())) { keyDicFile.createNewFile(); } if
		 * (keyDicFile.exists()) {
		 * 
		 * System.out.println("Link File created"); FileOutputStream keyFout =
		 * null; ObjectOutputStream oos = null; try { keyFout = new
		 * FileOutputStream(keyDicFile); oos = new ObjectOutputStream(keyFout);
		 * oos.writeObject(keyDictionary); keyDicFile = null;
		 * 
		 * } catch (Exception e) { e.printStackTrace(); } finally { try {
		 * keyFout.close(); oos.close(); } catch (Exception e) {
		 * e.printStackTrace(); } } }
		 */

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
		// Merge Code with file list to be called
		try {
			dir = new File(props.getProperty("tmp.dir") + File.separator
					+ "index" + File.separator + "partioned" + File.separator
					+ keyField + "to" + valueField + File.separator + pnum);
			if (dir.exists() && dir.isDirectory()) {

				File[] fileList = dir.listFiles();
				NWayMerge nM = new NWayMerge(fileList,
						props.getProperty("tmp.dir") + File.separator + "index"
								+ File.separator + "merged" + keyField + "to"
								+ valueField, keyField + "to" + valueField
								+ pnum + ".dat");
				nM.initializeMerge();
				nM.merge();
				System.out.println("NwayMerge completed for field::" + keyField+ " pnum::"+this.pnum);
				isComplete = true;
				// NWayMerge.mergeFile(fileList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDict() {
		// TODO Auto-generated method stub
		try {
			keyDictionary.writeToDisk();
			valueDictionary.writeToDisk();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
