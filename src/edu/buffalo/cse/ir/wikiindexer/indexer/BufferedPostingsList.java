package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class BufferedPostingsList implements Serializable,
		Comparable<BufferedPostingsList> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5973440455601079206L;
	private Set<PostingsEntry> bufferedEntry = null;
	private INDEXFIELD keyField = null;
	private INDEXFIELD valueField = null;
	// private static int id = 0;
	// private File dir = null;
	private Properties props = null;

	// private int MAX_SIZE = 10;

	public BufferedPostingsList(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField) {
		this.props = props;
		bufferedEntry = new TreeSet<PostingsEntry>();
		this.keyField = keyField;
		this.valueField = valueField;

		/*
		 * dir = new File(props.getProperty("tmp.dir") + File.separator +
		 * "index"); if (!dir.exists()) { dir.mkdirs(); }
		 */
	}

	public BufferedPostingsList(String value) {
		bufferedEntry = new TreeSet<PostingsEntry>();
		String entries[] = value.split(",");
		for (int i = 0; i < entries.length; i++) {
			bufferedEntry.add(new PostingsEntry(entries[i]));
		}
	}

	public void addPostingEntry(Integer value, Integer numOccurances) {
		synchronized (this) {
			PostingsEntry pe = new PostingsEntry(value, numOccurances);
			/*
			 * if(bufferedEntry.size() == MAX_SIZE){ ObjectOutputStream oos =
			 * null; FileOutputStream fos = null; try{ peFile = new
			 * File(props.getProperty("tmp.dir") + File.separator + "index" +
			 * File.separator + this.keyField + "to" + this.valueField + (++id)
			 * + ".txt"); fos = new FileOutputStream(peFile); oos = new
			 * ObjectOutputStream(fos); oos.writeObject(bufferedEntry);
			 * fileList.add(this.keyField + "to" + this.valueField+id+ ".txt");
			 * bufferedEntry = new LinkedList<PostingsEntry>(); }catch(Exception
			 * e){ e.printStackTrace(); }finally{ try{ oos.close();
			 * }catch(Exception e){ e.printStackTrace(); } try{ fos.close();
			 * }catch(Exception e){ e.printStackTrace(); } } }
			 */
			bufferedEntry.add(pe);
		}
	}

	public void append(BufferedPostingsList bufferedList) {
		// LinkedList<PostingsEntry<Integer,Integer>> postingList =
		// (LinkedList<PostingsEntry<K,V>>)bufferedList.bufferedEntry;

		this.bufferedEntry.addAll(bufferedList.bufferedEntry);

		// Iterator<E> it = postingList.iterator();

		/*
		 * while(it.hasNext()){ bufferedEntry.add(it.next()); }
		 */
	}

	public Set getPostingsEntry() {
		return bufferedEntry;
	}

	public int size() {
		if (this.bufferedEntry != null) {
			return this.bufferedEntry.size();
		} else {
			return -1;
		}
	}

	public String toString() {
		if (bufferedEntry != null) {
			return bufferedEntry.toString();
		} else {
			return null;
		}
	}

	public BufferedPostingsList and(BufferedPostingsList newList) {
		BufferedPostingsList mergedList = new BufferedPostingsList(this.props,
				this.keyField, this.valueField);
		if ((this.bufferedEntry == null) || this.bufferedEntry.isEmpty()
				|| (newList.bufferedEntry == null)
				|| newList.bufferedEntry.isEmpty()) {
			return null;
		}
		Iterator it = this.bufferedEntry.iterator();
		Iterator anIt = newList.bufferedEntry.iterator();

		PostingsEntry thisEntry = (PostingsEntry) it.next();
		PostingsEntry newEntry = (PostingsEntry) anIt.next();

		/*
		 * if(!it.hasNext() || !anIt.hasNext()){ if (thisEntry.getDocId() ==
		 * newEntry.getDocId()) {
		 * 
		 * Integer occurences = thisEntry.getNumOccurences() +
		 * newEntry.getNumOccurences();
		 * 
		 * mergedList.addPostingEntry(thisEntry.getDocId(), occurences); } }
		 */

		while (true) {

			if (thisEntry == null) {
				if (it.hasNext()) {
					thisEntry = (PostingsEntry) it.next();
				} else {
					break;
				}
			}

			if (newEntry == null) {
				if (anIt.hasNext()) {
					newEntry = (PostingsEntry) anIt.next();
				} else {
					break;
				}
			}


			Integer thisDocId = thisEntry.getDocId();
			Integer newDocId = newEntry.getDocId();
			if (thisDocId.compareTo(newDocId) == 0) {

				Integer occurences = thisEntry.getNumOccurences()
						+ newEntry.getNumOccurences();

				mergedList.addPostingEntry(thisEntry.getDocId(), occurences);

				thisEntry = null;
				newEntry = null;

			} else if (thisDocId.compareTo(newDocId) < 0) {
				thisEntry = null;

			} else if (thisDocId.compareTo(newDocId) > 0) {
				newEntry = null;
			}
		}

		return mergedList;
	}

	@Override
	public int compareTo(BufferedPostingsList arg0) {
		// TODO Auto-generated method stub
		if (this.bufferedEntry.size() < arg0.bufferedEntry.size())
			return -1;
		else if (this.bufferedEntry.size() >= arg0.bufferedEntry.size()) {
			return 1;
		}
		return 0;
	}
}
