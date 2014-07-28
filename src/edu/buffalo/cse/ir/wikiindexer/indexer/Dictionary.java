/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author nikhillo An abstract class that represents a dictionary object for a
 *         given index
 */
public abstract class Dictionary implements Writeable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5639049240245810336L;
	protected INDEXFIELD type = null;
	private File dicFile, dir = null;
	private Properties props = null;
	protected Map<String,Integer> dict = null;
	protected Map<Integer,String> reverseDict = null;
	private int pnum;
	
	public Dictionary(Properties props, INDEXFIELD field) {
		type = field;
		this.props = props;
		// TODO Implement this method
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		if (dict != null) {
			OutputStreamWriter os = null;
			FileOutputStream dicFout = null;
			try {
				dir = new File(props.getProperty("tmp.dir") + File.separator
						+ "dic" + File.separator + type);
				boolean createDir = dir.mkdirs();

				// System.out.println(dir.getAbsolutePath());
				if (createDir || dir.exists()) {

					Properties indexProperties = new Properties() {

						@Override
						public Set<Object> keySet() {
							return Collections
									.unmodifiableSet(new TreeSet<Object>(super
											.keySet()));
						}

						public synchronized Enumeration<Object> keys() {
							return Collections.enumeration(new TreeSet<Object>(
									super.keySet()));
						}
					};
					dicFile = new File(dir.getAbsolutePath() + File.separator
							+ type + "Dic"+(pnum)+".txt");
					dicFout = new FileOutputStream(dicFile);
					os = new OutputStreamWriter(dicFout);
					Iterator<String> it = dict.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						indexProperties.put(key, dict.get(key).toString());
					}
					// indexProperties.putAll(index);
					indexProperties.store(os, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					os.close();
				} catch (Exception e) {

				}
				try {
					dicFout.close();
				} catch (Exception e) {

				}
			}
		}
		// TODO Implement this method

	}

	protected void setPartitionNumber(int pnum){
		this.pnum = pnum;
	}
	
	public void loadData(File dictFile) {
		if (dictFile.exists()) {

			BufferedReader br = null;
			InputStreamReader isr = null;
			FileInputStream fis = null;
			try {
				
				Properties indexProperties = new Properties() {

					@Override
					public Set<Object> keySet() {
						return Collections
								.unmodifiableSet(new TreeSet<Object>(super
										.keySet()));
					}

					public synchronized Enumeration<Object> keys() {
						return Collections.enumeration(new TreeSet<Object>(
								super.keySet()));
					}
				};

				fis = new FileInputStream(dictFile);
				isr = new InputStreamReader(fis, "UTF-8");
				indexProperties.load(isr);
				
				//br = new BufferedReader(isr);

				dict = new TreeMap<String, Integer>();
				reverseDict = new TreeMap<Integer,String>();
				
				Iterator it = indexProperties.keySet().iterator();
				
				while(it.hasNext()){
					String key = (String)it.next();
					String value = indexProperties.getProperty(key).trim();
					dict.put(key,Integer.parseInt(value));
					reverseDict.put(Integer.parseInt(value), key); 
				}
				
				/*String line = new String();
				while (((line = br.readLine()) != null)) {
					// Line is of format key = [ dicId||occ , docId||occ ];
					if(line.startsWith("#")){
						continue;
					}
					String tokens[] = line.split("=");

					String key = tokens[0];
					String value = tokens[1].trim();

					dict.put(key,Integer.parseInt(value));
					reverseDict.put(Integer.parseInt(value), key); */

				//}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("File not found::"
						+ dictFile.getAbsolutePath());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}

	/**
	 * Method to check if the given value exists in the dictionary or not Unlike
	 * the subclassed lookup methods, it only checks if the value exists and
	 * does not change the underlying data structure
	 * 
	 * @param value
	 *            : The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		// TODO Implement this method
		if (dict != null) {
			return dict.containsKey(value);
		} else {
			return false;
		}
		// return false;
	}

	/**
	 * MEthod to lookup a given string from the dictionary. The query string can
	 * be an exact match or have wild cards (* and ?) Must be implemented ONLY
	 * AS A BONUS
	 * 
	 * @param queryStr
	 *            : The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 *         null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		// TODO: Implement this method (FOR A BONUS)
		ArrayList<String> result = new ArrayList<String>();
		
		for(Map.Entry<String,Integer> entry : this.dict.entrySet()) {
			  String key = entry.getKey();
			  
			  String temp = wild(key,queryStr); 
			  if(temp != null && !("".equalsIgnoreCase(temp))){
				  result.add(temp);
			  }
			 // System.out.println(key + " => " + value);
			}
			//wild(this.dict.get,queryStr);
		if(result.isEmpty()){
			return null;
		}
		System.out.println(result);
		return result;
	}
	
	public String wild(String text, String pattern)
    {
        // Create the cards by splitting using a RegEx. If more speed 
        // is desired, a simpler character based splitting can be done.
		String result = "";
		if(pattern.contains("*"))
		{
				ArrayList<String> sb =  new ArrayList<String>();
	
			String [] cards = pattern.split("\\*");
			for(int i=0;i<cards.length;i++)
			{
				if(!"".equalsIgnoreCase(cards[i]))
					sb.add(cards[i]);
			}
			boolean st=false,en=false;
	
			for (String card : cards)
			{
				st = text.startsWith(card);
	
				en = text.endsWith(card);
			}
	
			if(st == true && sb.size() == 1)
				en = true;
			if(en == true && sb.size() == 1)
				st = true;
			if(st && en)
				//System.out.println(text);
				result = text;
		}
		else if(pattern.contains("?"))
		{
			
			//System.out.println("???");
			int i=0;
			char[] temp = pattern.toCharArray();
			String temp1="",temp3="";
			
			for(char c : temp)
			{
				
				if(c == '?')
					break;
					i++;
				
			}
			
			if(i>0)
			{
				temp1 = pattern.substring(0,i-1);
				temp3 = pattern.charAt(i-1)+"";
					//System.out.println(temp1+" ff "+temp3);
					
					if(text.startsWith(temp1))
					{
						if(!"".equalsIgnoreCase(temp3))
							//System.out.println(text);
							result = text;
						else
						//System.out.println(text);
							result = text;
					}
			}
			else
			{
				
				temp3 = pattern.charAt(i+1)+"";
				if((i+2)<pattern.length())
					temp1 = pattern.substring(i+2,pattern.length());
				//System.out.println(temp1+" "+temp3);
				if(text.startsWith(temp1))
				{
					if(!"".equalsIgnoreCase(temp3))
						//System.out.println(text);
						result = text;
					else
						result = text;
					//System.out.println(text);
				}
				//System.out.println(temp1);
			}
		}
			
			else
			{
				if(text.equalsIgnoreCase(pattern))
					//System.out.println(text);
					result = text;
				
			}
			
		return result;
		}

	/**
	 * Method to get the total number of terms in the dictionary
	 * 
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		if (dict != null) {
			return dict.size();
		}
		// TODO: Implement this method
		return 0;
	}
}
