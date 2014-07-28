package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.util.Collection;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

public class WikiHandler extends DefaultHandler {
	Logger logger = Logger.getLogger(WikiHandler.class.getName());
	Stack<String> nodeStack = new Stack<String>();
	String timeStamp = null;
	String title = null;
	String username = null;
	long id = 0;
	StringBuilder sb = new StringBuilder();
	Collection<WikipediaDocument> docs = null;
	WikipediaParser wikiParser = null;
	
	public WikiHandler(Collection<WikipediaDocument> docs){
		this.docs = docs; 
	}

	public void startDocument(){
//		logger.log(Level.INFO,"Document Parsing Started");
	}
	
	public void endDocument(){
	//	logger.log(Level.INFO,"Document parsing completed");
	}
	
	public void startElement(String uri, String localName,String qName, 
			Attributes attributes) throws SAXException {
		nodeStack.push(qName);
	//	logger.log(Level.INFO,"Start Element :" + qName);
	}

	public void endElement(String uri, String localName,String qName) throws SAXException {
		try{
		//	logger.log(Level.FINE,"URI::" + uri);
		//	logger.log(Level.FINE,"Local Name::" + localName);
		//	logger.log(Level.FINE,"End Element :" + qName);
		//	logger.log(Level.FINE,"Element Contents : " + sb.toString());
			nodeStack.pop();
			if("timestamp".equalsIgnoreCase(qName)){
				timeStamp = sb.toString().trim();
			}
			if("title".equalsIgnoreCase(qName)){
				title = sb.toString().trim();
			}
			if("username".equalsIgnoreCase(qName) || "ip".equalsIgnoreCase(qName)){
				username = sb.toString().trim();
			}
			if("id".equalsIgnoreCase(qName)){
				String parent = nodeStack.peek();
				if("page".equalsIgnoreCase(parent)){
					id = Long.parseLong(sb.toString().trim());
				}
			}
			if("text".equalsIgnoreCase(qName)){
			//	logger.log(Level.INFO,"Creating a wiki document");
			//	logger.log(Level.INFO,"WikiDocument ID::" +id);
			//	logger.log(Level.INFO,"WikiDocument Timestamp::"+timeStamp);
			//	logger.log(Level.INFO,"WikiDocument Username::"+username);
			//	logger.log(Level.INFO,"WikiDocument Title::"+title);
				WikipediaDocument wikiDoc = new WikipediaDocument(id, timeStamp, username, title);
				// TODO :: Need to add sections , categories , links and language links
				wikiParser = new WikipediaParser(sb.toString().trim(),wikiDoc);
				wikiParser.parse();
				docs.add(wikiDoc);
			//	logger.log(Level.INFO,"Added the Wiki Document to Docs Collection");
			}
			sb = new StringBuilder();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		sb.append(ch, start, length);
	} 
}