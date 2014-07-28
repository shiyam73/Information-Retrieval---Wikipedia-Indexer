/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;

/**
 * A simple map based token view of the transformed document
 * 
 * @author nikhillo
 * 
 */
public class IndexableDocument {
	/**
	 * Default constructor
	 */
	Map<INDEXFIELD, TokenStream> docAsTokens = null;
	private String identifier;

	public IndexableDocument() {
		// TODO: Init state as needed
		docAsTokens = new HashMap<INDEXFIELD, TokenStream>();

		/*
		 * for (INDEXFIELD fld : INDEXFIELD.values()) { docAsTokens.put(fld,new
		 * TokenStream("")); }
		 */
	}

	public IndexableDocument(String title) {
		this();
		this.identifier = title;
	}

	/**
	 * MEthod to add a field and stream to the map If the field already exists
	 * in the map, the streams should be merged
	 * 
	 * @param field
	 *            : The field to be added
	 * @param stream
	 *            : The stream to be added.
	 */
	public void addField(INDEXFIELD field, TokenStream stream) {
		if(docAsTokens.containsKey(field)){
			TokenStream fieldStream = docAsTokens.get(field);
			fieldStream.merge(stream);
			docAsTokens.put(field, fieldStream);
		}else{
			if(stream != null){
				docAsTokens.put(field, stream);
			}
		}
		//TODO: Implement this method
	}

	/**
	 * Method to return the stream for a given field
	 * 
	 * @param key
	 *            : The field for which the stream is requested
	 * @return The underlying stream if the key exists, null otherwise
	 */
	public TokenStream getStream(INDEXFIELD key) {
		return docAsTokens.get(key);
		// TODO: Implement this method
	}

	/**
	 * Method to return a unique identifier for the given document. It is left
	 * to the student to identify what this must be But also look at how it is
	 * referenced in the indexing process
	 * 
	 * @return A unique identifier for the given document
	 */
	public String getDocumentIdentifier() {
		return identifier;
		// TODO: Implement this method
	}

}
