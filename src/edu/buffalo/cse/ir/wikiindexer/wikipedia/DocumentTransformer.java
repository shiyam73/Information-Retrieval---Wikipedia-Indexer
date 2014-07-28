/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerFactory;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument
 * object into an IndexableDocument object using the given Tokenizer
 * 
 * @author nikhillo
 * 
 */
public class DocumentTransformer implements Callable<IndexableDocument> {

	Map<INDEXFIELD, Tokenizer> tkoMap = null;
	WikipediaDocument doc = null;

	private static int linkSize = 0;
	/**
	 * Default constructor, DO NOT change
	 * 
	 * @param tknizerMap
	 *            : A map mapping a fully initialized tokenizer to a given field
	 *            type
	 * @param doc
	 *            : The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap,
			WikipediaDocument doc) {
		this.tkoMap = tknizerMap;
		this.doc = doc;
		/*
		 * try { this.call(); } catch (TokenizerException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// TODO: Implement this method
	}

	/**
	 * Method to trigger the transformation
	 * 
	 * @throws TokenizerException
	 *             Inc ase any tokenization error occurs
	 * @throws InterruptedException
	 */
	public IndexableDocument call() throws TokenizerException,
			InterruptedException {

		System.out.println("Title::" + this.doc.getTitle() + " "
				+ Thread.currentThread().getId());

		IndexableDocument idDoc = new IndexableDocument(doc.getTitle());
		// System.out.println("Author::");
		String author = doc.getAuthor();
		TokenStream authorTokenStream = new TokenStream(author);
		Tokenizer authorTokenizer = TokenizerFactory.getInstance(null)
				.getTokenizer(INDEXFIELD.AUTHOR);
		authorTokenizer.tokenize(authorTokenStream);

		idDoc.addField(INDEXFIELD.AUTHOR, authorTokenStream);

		// System.out.println("Category::");
		ArrayList<String> categories = (ArrayList<String>)doc.getCategories();
				
		if (categories != null && !categories.isEmpty()) {
			String[] catArray = new String[categories.size()];
			categories.toArray(catArray);
			
		//	System.out.println("Categories::"+categories);
			
			TokenStream categoryTokenStram = new TokenStream(catArray[0]);
			if(catArray.length > 1){
				categoryTokenStram.set(catArray);
			}
			Tokenizer categoryTokenizer = TokenizerFactory.getInstance(null)
					.getTokenizer(INDEXFIELD.CATEGORY);
			categoryTokenizer.tokenize(categoryTokenStram);
			idDoc.addField(INDEXFIELD.CATEGORY, categoryTokenStram);
		}else{
			System.out.println("No categories present for doc::"+doc.getTitle());
			idDoc.addField(INDEXFIELD.CATEGORY, null);
		}

		
		HashSet<String> links = (HashSet<String>)doc.getLinks();
		
		if(links != null && !links.isEmpty()){

			String[] linkArray = new String[links.size()];
	//		System.out.println("Links::"+links.toString());
	//		linkSize += links.size();
	//		System.out.println("LinkSize::"+linkSize);
			links.toArray(linkArray);

			TokenStream linksTokenStream = new TokenStream(linkArray[0]);
			if(linkArray.length > 1){
				linksTokenStream.set(linkArray);
			}

			Tokenizer linksTokenizer = TokenizerFactory.getInstance(null)
					.getTokenizer(INDEXFIELD.LINK);
			linksTokenizer.tokenize(linksTokenStream);

			idDoc.addField(INDEXFIELD.LINK, linksTokenStream);
		}else{
			idDoc.addField(INDEXFIELD.LINK, null);
			System.out.println("No links present for doc::"+doc.getTitle());
		}
		// System.out.println("Links::");

		// Convert sections and section text to
		// TokenStream
		Tokenizer termTokenizer = TokenizerFactory.getInstance(null)
				.getTokenizer(INDEXFIELD.TERM);

		System.out.println("Section Count::" + doc.getSections().size());

		for (int i = 0; i < doc.getSections().size(); i++) {
			Section s = doc.getSections().get(i);
		//	System.out.println("Section Name::" + s.getTitle());
		//	System.out.println("Section Text::" + s.getText());
			if (s.getText() != null && !("".equalsIgnoreCase(s.getText()))) {
				TokenStream stream = new TokenStream(s.getText());
				termTokenizer.tokenize(stream);
				idDoc.addField(INDEXFIELD.TERM, stream);
		//		System.out.println("stream::"+stream);
			} else {
				System.out.println("Section Name::" + s.getTitle()
						+ "Title :: " + doc.getTitle());
			}
		}
		// ;

		//doc.cleanup();

		System.out.println("Test");

		// TODO Implement this method
		return idDoc;
	}

}
