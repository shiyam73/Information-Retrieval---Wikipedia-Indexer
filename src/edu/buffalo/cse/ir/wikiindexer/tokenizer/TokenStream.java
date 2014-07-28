/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 * @author nikhillo
 *
 */
public class TokenStream implements Iterator<String>{
	
	Collection<String> unSortedCollection = null;
	TreeMap<String,Integer> sortedCollection = null;
	private int index=-1;
	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		this(bldr.toString());
		//TODO: Implement this method
	}
	
	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		
		if(string!=null && !("".equalsIgnoreCase(string))){
			if(unSortedCollection == null){
				index = 0;
				unSortedCollection = new LinkedList<String>();
				sortedCollection = new TreeMap<String, Integer>();
			}
			
		//	String[] tokens = string.split("\\s");
		//	unSortedCollection.addAll(Arrays.asList(tokens));
			unSortedCollection.add(string);
		}
		//TODO: Implement this method
	}
	
	/**
	 * Method to append tokens to the stream
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		if(unSortedCollection == null){
			return;
		}
		if(tokens != null){
			for(int i=0;i<tokens.length;i++){
				if(tokens[i] != null && !("".equalsIgnoreCase(tokens[i].trim()))){
					unSortedCollection.add(tokens[i]);
				}
			}
		}
		//TODO: Implement this method
	}
	
	public int size(){
		if(unSortedCollection == null){
			return -1;
		}
		
		return this.unSortedCollection.size();
	}
	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
		if(unSortedCollection != null){
			sortedCollection = new TreeMap<String, Integer>();
			for(String key : unSortedCollection){
				if(sortedCollection.containsKey(key)){
					int val = sortedCollection.get(key);
					sortedCollection.put(key,++val);
				}else{
					sortedCollection.put(key, 1);
				}
			}
			return sortedCollection;
		}
		//TODO: Implement this method
		return null;
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		return unSortedCollection;
		//TODO: Implement this method
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		int count = 0;
		if(token == null || "".equalsIgnoreCase(token)|| unSortedCollection == null){
			return count;
		}
		
		for(String val:unSortedCollection){
			if(val.equals(token)){
				count++;
			}
		}
		return count;
		//TODO: Implement this method
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		if(unSortedCollection == null){
			return false;
		}
		if((index) == unSortedCollection.size()){
			return false;
		}
		// TODO: Implement this method
		return true;
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		if(unSortedCollection == null){
			return false;
		}
		if((index) == 0){
			return false;
		}
		//TODO: Implement this method
		return true;
	}
	
	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		synchronized (this) {
			if(unSortedCollection == null){
				return null;
			}
			if(index == unSortedCollection.size()){
				return null;
			}else{
				return ((LinkedList<String>) unSortedCollection).get(index++);
			}
		}		// TODO: Implement this method
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		synchronized (this) {
			if((index) < 0){
				return null;
			}else if(index==0){
				return null;
			}else{
				return ((LinkedList<String>) unSortedCollection).get(--index);
			}
		}		// TODO: Implement this method
	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		if(index>=0&&index<unSortedCollection.size()){
			((LinkedList<String>)unSortedCollection).remove(index);
		}
		// TODO: Implement this method
	}
	
	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		synchronized (this) {
			if(unSortedCollection == null){
				return false;
			}
			if(this.hasPrevious()){
				this.previous();
				return this.mergeWithNext();
			}
			return false;
		}		//TODO: Implement this method
	}
	
	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		synchronized(this){
			try{
				if(unSortedCollection == null){
					return false;
				}
				if(!this.hasNext()){
					return false;
				}
				String current = ((LinkedList<String>)unSortedCollection).get(index);
				String next = ((LinkedList<String>)unSortedCollection).get(index+1);
				StringBuilder s = new StringBuilder(current);
				s.append(" ");
				s.append(next);
				this.next();
				this.remove();
				this.previous();
				this.set(s.toString());
				return true;
			}catch(Exception e){
				return false;
			}
		}
		//TODO: Implement this method
	}
	
	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		synchronized (this) {
			if(newValue.length == 1 &&( (newValue[0] == null) || ("".equalsIgnoreCase(newValue[0])) )){
				return;
			}
			if(unSortedCollection == null || index>=unSortedCollection.size()){
				return;
			}
			this.remove();
			for(int i=0;i<newValue.length;i++){
					if(newValue[i] != null && !("".equalsIgnoreCase(newValue[i]))){
						((LinkedList<String>)unSortedCollection).add(index++,newValue[i]);
					}
			}
			index--;
		}
		//TODO: Implement this method
	}
	
	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		synchronized (this) {
			this.index=0;
		}
		//TODO: Implement this method
	}
	
	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 */
	public void seekEnd() {
		synchronized (this) {
			if(unSortedCollection != null){
			index=unSortedCollection.size();
			}
		}
	}
	
	public void addAll(String... tokens){
		if(unSortedCollection != null){
			if(index>=0 && index<=unSortedCollection.size()){
				for(int i =0;i<tokens.length;i++){
				((LinkedList<String>)unSortedCollection).add(index++,tokens[i]);
				}
			}
		}	
	}

	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	public void merge(TokenStream other) {
		if(other!=null){
			if(other.unSortedCollection!= null) {
				if(this.unSortedCollection == null){
					this.unSortedCollection = new LinkedList<String>();
				}
			this.unSortedCollection.addAll(other.unSortedCollection);
			}
		}
		//TODO: Implement this method
	}
	
	public String toString(){
		if(this != null){
			if(this.unSortedCollection != null){
				return (String)this.unSortedCollection.toString();
			}
		}
		return null;
	}
}