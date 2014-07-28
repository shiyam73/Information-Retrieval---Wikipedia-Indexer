/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */
public class WikipediaParser_1 {
	
	String textStr = null;
	WikipediaDocument doc = null;
	
	static String extra="";
	static int a=0;
	static int b=0;
	
	public WikipediaParser_1(String textStr,WikipediaDocument doc){
		this.textStr = textStr;
		this.doc = doc;
	}
	/* TODO */
	/**
	 * Method to parse section titles or headings.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * @param titleStr: The string to be parsed
	 * @return The parsed string with the markup removed
	 * @throws IOException 
	 */
	
	public void parse() throws IOException{
		/** TODO : Implement Shiyam's parser code **/
		
		/*File file = new File("F://test.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		// if file doesnt exists, then create it
		
			try {
				if (!file.exists()) 
					file.createNewFile();
			
				 fw = new FileWriter(file.getAbsoluteFile(),true);
				 bw = new BufferedWriter(fw);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
		
		//Matcher m1 = Pattern.compile("[\\[]{2}[\\-a-zA-Z \\|\\,\\'\\)\\(\\:0-9]+[\\]]{2}|[=]{2,}(.*)[=]{2,}").matcher(textStr);
		Pattern categoryPattern = Pattern.compile("\\[\\[(.*):(.*)\\]\\]");
		Matcher m1 = Pattern.compile("[\\[]{2}(.*)[\\]]{2}|[=]{2,}(.*)[=]{2,}").matcher(textStr);
		//Pattern sectionPattern = Pattern.compile("^==([^=]*)==$");
		Matcher m3;
		Pattern langLink = Pattern.compile("[\\[]{2}[a-z][a-z](.*)[\\]]{2}");
		Pattern sectionPattern = Pattern.compile("[=]{2,}(.*)[=]{2,}");
		ArrayList<String> links = new ArrayList<String>();
		String dummy[];
		int sectionStart = 0;
		String sectionName = new String("Default");
		String sectionTxt = null;
		

		ArrayList<String> categories = new ArrayList<String>();
		
		HashMap<String, String> langLinks = new HashMap<String, String>();
		
		while(m1.find()) {
    	
			String matchedString = m1.group().toString();
			Matcher catMatcher = categoryPattern.matcher(matchedString);

			if(catMatcher.matches()){
				String type = catMatcher.group(1);
				if("Category".equalsIgnoreCase(type)){
					String category = catMatcher.group(2);
					
					category = category.replaceAll("\\|","");
					//System.out.println("cate : "+category);
					categories.add(category);
				}
				else{
					langLinks.put(catMatcher.group(1), catMatcher.group(2));
				}
			}
    
			
    	
			Matcher sectionMatcher = sectionPattern.matcher(matchedString);
			if(sectionMatcher.matches()){
				sectionTxt = textStr.substring(sectionStart,m1.start());
				
				
				
				// Need to parse the sectionTxt to remove wiki markup
				
				//System.out.println("Before Section : "+sectionTxt);
				//tempLinks =  parseLinks(sectionTxt);
				
				/*for(int j=0;j<tempLinks.length;j++)
				{
					//System.out.println("TempLinks : "+tempLinks[j]);
					
					links.add(tempLinks[j]);
					//links.add(tempLinks[1]);
				}*/
				
				//System.out.println("SECTIONTXT : "+sectionTxt);
				links = parseLinks1(sectionTxt);
				
				//dummy = parseLinks(sectionTxt);
				
				
				sectionTxt = parseTemplates(sectionTxt);
				
				sectionTxt = parseTagFormatting(sectionTxt);
				
				sectionTxt = parseListItem(sectionTxt);
				
				
				//System.out.println(sectionTxt);
				
				
				sectionTxt = parseTextFormatting(sectionTxt);
				
				//extra = parseLinks2(sectionTxt);
				//sectionTxt = sectionTxt.replaceAll("[\\[]{2}Category:(.*)[\\]]{2}", "");
				sectionTxt = sectionTxt.replaceAll("[\\[]{2}|[\\]]{2}", "");
				
				sectionTxt = sectionTxt.replaceAll("[\\{]{2}|[\\}]{2}", "");
				
				//sectionTxt = parseTemplates(sectionTxt);
				
				sectionTxt = sectionTxt.replaceAll("[\\|]", " ");
				
				sectionTxt = sectionTxt.replaceAll("[\\[]http(.*)[\\]]","");
				sectionTxt = sectionTxt.replaceAll("[\\[]{2}|[\\]]{2}","");
				
				
				for(int j=0;j<links.size();)
				{
					//System.out.println(links.get(j));
					sectionTxt += " "+ links.get(j);
					j=j+2;
				}
				//sectionTxt +=" "+extra;
				//System.out.println("SECTIONAFt: "+sectionTxt);
				//bw.write("\n AFTER PARSE : "+sectionTxt+"\n");
				//textLinks = "";
				if(sectionName.contains("{"))
					sectionName = sectionName.replaceAll("[\\{]{1,}(.*)[\\}]{1,}","");
				
				if(sectionName.contains("="))
				{
					sectionName = sectionName.replaceAll("=","");
					//bw.write("SEC: "+sectionName);
				}
				//bw.write("SEC1: "+sectionName);
			//	System.out.println("After Section : "+sectionTxt);
				//System.out.println(sectionTxt);
				
				extra = "";
				doc.addSection(sectionName, sectionTxt);
				sectionStart = m1.end();
				sectionName = sectionMatcher.group(1);
				doc.addLInks(links);
			}
		}
		
		sectionTxt = textStr.substring(sectionStart);
		//System.out.println("B4 : "+sectionTxt);
		links = parseLinks1(sectionTxt);
		
		for(int j=0;j<links.size();)
		{
			//System.out.println(links.get(j));
			sectionTxt += " "+ links.get(j);
			j=j+2;
		}
		//extra += parseLinks2(sectionTxt);
		//dummy = parseLinks(sectionTxt);
		sectionTxt = sectionTxt.replaceAll("\\[http(.*)", "");
		sectionTxt = sectionTxt.replaceAll("http(.*)", "");
		
		if(sectionTxt.contains("REDIRECT"))
			sectionTxt = parseRedirect(sectionTxt);
		
		//sectionTxt = sectionTxt.replaceAll("REDIRECT(.*)", "");
		
		sectionTxt = parseTagFormatting(sectionTxt);
		
		sectionTxt = parseTemplates(sectionTxt);
		
		sectionTxt = parseListItem(sectionTxt);
		sectionTxt = parseTextFormatting(sectionTxt);
		
		sectionTxt = sectionTxt.replaceAll("[\\[]{2}Category(.*)[\\]]{2}", "");
		//sectionTxt = sectionTxt.replaceAll("[\\[]{2}|[\\]]{2}", "");
		
		sectionTxt = sectionTxt.replaceAll("[\\{]{2}|[\\}]{2}", "");
		
		//sectionTxt = parseTemplates(sectionTxt);
		
		sectionTxt = sectionTxt.replaceAll("[\\|]", "");
		sectionTxt = sectionTxt.replaceAll("[\\[]http(.*)[\\]]","");
		sectionTxt = sectionTxt.replaceAll("[\\[]{2}|[\\]]{2}","");
		//System.out.println("B4 : "+sectionTxt);
		//System.out.println("OUTSIDE\n"+sectionTxt);
		//sectionTxt +=" "+extra;
		//bw.write("\n NOT SECTION : "+sectionTxt+"\n");
		//bw.close();
		extra="";
		doc.addSection(sectionName, sectionTxt);
		doc.addCategories(categories);
		
		//System.out.println("SECTION COUNT: "+doc.getSections().size());
		//System.out.println("CATEGORIES COUNT :"+doc.getCategories().size());
		
		doc.addLInks(links);
		
		//doc.writeToFile();
	}
	
	public static String parseRedirect(String text)
	{
		text = text.replaceAll("#","");
		text = text.replaceAll("REDIRECT","");
		text = text.replaceAll("[\\[]|[\\]]", "");
		return text;
	}
	
	
	public static String parseSectionTitle(String titleStr) {
		
		if(titleStr == null)
			return null;
		if("".equalsIgnoreCase(titleStr))
			return "";
		
		String temp = new String();
		int length=0;
		int s=0,i=0;
		char parse[];  
		String result = new String();
		Matcher m1 = Pattern.compile("[=]{2,}(.*)[=]{2,}").matcher(titleStr);
	
		/*while(m1.find()) 
		{
			temp = m1.group().toString();
			parse = temp.toCharArray();
			length = temp.length();
			
			//System.out.println("\nTEMP \n:"+temp);
			
			
			for(i=0; i<length; i++)
			{
				if(parse[i] == '=')
				{
					continue;
				}
				else
				{
					s = i;
					break;
				}
			}	
		}
		//System.out.println("I = "+i);

		if(i == length)
		{
			result = "No string";
		//System.out.println(result);
			return result;
		}*/
		
		while(m1.find()) 
		{
				temp = m1.group().toString();
				length = temp.length();
			i = count(temp);
			i = i/2;
			//System.out.println("I= "+i+"  "+temp);
		}
			result = temp.substring(i,length-i).trim();
			/*result = temp.substring(s,length-i);
			System.out.println("Result : "+result);*/
			return result;
		}
	
	/*Shiyam's Version*/
	public static int count(String s)
	{
		//System.out.println("Inside");
		if (s.length() == 0)
	        return 0;
	    else if (s.charAt(0) == '=')
	        return 1 + count(s.substring(1, s.length()));
	    else
	        return count(s.substring(1, s.length()));
		
	}
	
	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * @param itemText: The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		
		if(itemText == null)
			return null;
		if("".equalsIgnoreCase(itemText))
			return "";
		
		String result = itemText;
		result = result.replaceAll("[*]{1,}|[#]{1,}|[:]"," ").trim();
		//result.trim();
		
		return result;
	}
	
	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		
		if(text == null)
			return null;
		if("".equalsIgnoreCase(text))
			return "";
		
		String s = text.replaceAll("[']{2,3}|[']{5}", "").trim();
			//	s.trim();
		return s;
		}
	
	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		
		if(text == null)
			return null;
		if("".equalsIgnoreCase(text))
			return "";
		
		String s1 = text.replaceAll("&gt;", ">");
	       s1 = s1.replaceAll("&lt;", "<");
	       s1 = s1.replaceAll("&quot;", "'");
	      // s1 = s1.replaceAll("<ref>(.*)<[\\/]ref>","");
	       s1 = s1.replaceAll("<ref([^>])*>(([^>]))*<[\\/]ref>","");
	       s1 = s1.replaceAll("<ref[^>]*?/>", "");
	       s1 = s1.replaceAll("[<](.*)[\"\' ]+[>]", "");
	       s1 = s1.replaceAll("[<][a-z]+[>]", "");
	       s1 = s1.replaceAll("[\\/<]+[a-z]+[>]", "");
	       s1 = s1.replaceAll("[<][a-z]+[\\/>\\s]+", "");
	       
	       
	       s1 = s1.replaceAll("[\\<\\!]+(.*)[>]", "");
	       s1 = s1.replaceAll("[\\<\\!]+(.*)|[\\-\\>]+", "");
	       s1 = s1.replaceAll("|","");
	       s1 = s1.replaceAll("[\\<][\\/]*(nowiki)[\\>]", "").trim();
	       
	       
	       
	     //  s1.trim();
	return s1;
	}
	
	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String wikiText) {
		//String s1 = text.replaceAll("[\\{]{2}(.*)[\\}]{2}","");
				//System.out.println("Parse Tag : "+text);
				
						wikiText = wikiText.replaceAll("[\\{]{2}(.*)[\\}]{2}","");
		StringBuilder s2 = new StringBuilder(wikiText);
		String s = "{{";
		while(true)
		{
		       int i = wikiText.indexOf(s);
		      //System.out.println("index "+i);
		        if (i < 0) {
		            //System.out.println("NULL");
		            //return wikiText;
		            break;
		                }
		        int j = 2;
		        int k = i + s.length();
		        do {
		            if (k >= wikiText.length()) {
		                break;
		                    }
		            switch (wikiText.charAt(k)) {
		            case 125: // '}'
		                j--;
		                        break;
		            case 123: // '{'
		                j++;
		                        break;
		                    }
		            if (j == 0) {
		                break;
		                    }
		           k++;
		                } while (true);
		      // String s1 = wikiText.substring(i, k + 1);
		       // s1 = s1.replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
		        
		        
		        if(k>=wikiText.length())
				{
		        	wikiText = wikiText.substring(i, k ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
			      s2 = s2.replace(i, k,"");
				}
				else
				{
					wikiText = wikiText.substring(i, k+1 ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
				      s2 = s2.replace(i, k+1,"");
				}
		        
		        wikiText = s2.toString();
		        
		        //return wikiText;
		}
		
		while(true)
		{
		s = "{";
	       int i = wikiText.indexOf(s);
	       //System.out.println("index "+i);
	        if (i < 0) {
	            break;
	                }
	        int j = 1;
	        int k = i + s.length();
	        do {
	            if (k >= wikiText.length()) {
	                break;
	                    }
	            switch (wikiText.charAt(k)) {
	            case 125: // '}'
	                j--;
	                        break;
	            case 123: // '{'
	                j++;
	                        break;
	                    }
	            if (j == 0) {
	                break;
	                    }
	           k++;
	                } while (true);
	      // String s1 = wikiText.substring(i, k + 1);
	       // s1 = s1.replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
	        
	        
	        if(k>=wikiText.length())
			{
	        	wikiText = wikiText.substring(i, k ).replaceAll("[\\{](.*)[\\}]", "");
		      s2 = s2.replace(i, k,"");
			}
			else
			{
				wikiText = wikiText.substring(i, k+1 ).replaceAll("[\\{](.*)[\\}]", "");
			      s2 = s2.replace(i, k+1,"");
			}
	        s2 = s2.replace(i, k+1,"");
	        
	        wikiText = s2.toString();
		}
		return wikiText;
				
				
				/*StringBuilder s2 = new StringBuilder(text);
				String s = "{{Infobox";
			       int i = text.indexOf(s);
			       int j=0,k=0;
			      // System.out.println("index "+i);
			        if (!(i < 0))
			        {
			            
			        j = 2;
			         k = i + s.length();
			        do {
			            if (k >= text.length()) {
			                break;
			                    }
			            switch (text.charAt(k)) {
			            case 125: // '}'
			                j--;
			                        break;
			            case 123: // '{'
			                j++;
			                        break;
			                    }
			            if (j == 0) {
			                break;
			                    }
			           k++;
			                } while (true);
				
			       
			        if(k>=text.length())
					{
				      text = text.substring(i, k ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
				      s2 = s2.replace(i, k,"");
					}
					else
					{
						text = text.substring(i, k+1 ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
					      s2 = s2.replace(i, k+1,"");
					}
			      text = s2.toString();
			      
			        }
			        
			        s = "{{Persondata";
				        i = text.indexOf(s);
				       
				      // System.out.println("index "+i);
				        if (!(i < 0))
				        {
				            
				        j = 2;
				         k = i + s.length();
				        do {
				            if (k >= text.length()) {
				                break;
				                    }
				            switch (text.charAt(k)) {
				            case 125: // '}'
				                j--;
				                        break;
				            case 123: // '{'
				                j++;
				                        break;
				                    }
				            if (j == 0) {
				                break;
				                    }
				           k++;
				                } while (true);
					
				       
				        if(k>=text.length())
						{
					      text = text.substring(i, k ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
					      s2 = s2.replace(i, k,"");
						}
						else
						{
							text = text.substring(i, k+1 ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
						      s2 = s2.replace(i, k+1,"");
						}
				      text = s2.toString();
				     
				        }
			        
			       s = "{{cite";
			       
			       text = text.replaceAll("&amp;", "&");
			       text = text.replaceAll("&#124","|");
			        i = text.indexOf(s);
			       //System.out.println("index "+i);
			       if (!(i < 0))
			        {
			      j = 2;
			        k = i + s.length();
			        do {
			            if (k >= text.length()) {
			                break;
			                    }
			            switch (text.charAt(k)) {
			            case 125: // '}'
			                j--;
			                        break;
			            case 123: // '{'
			                j++;
			                        break;
			                    }
			            if (j == 0) {
			                break;
			                    }
			           k++;
			                } while (true);
				//System.out.println("Cite i="+i+" k= "+k+"length "+text.length());
			        if(k>=text.length())
				{
			      text = text.substring(i, k ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
			      s2 = s2.replace(i, k,"");
				}
				else
				{
					text = text.substring(i, k+1 ).replaceAll("[\\{]{2}(.*)[\\}]{2}", "");
				      s2 = s2.replace(i, k+1,"");
				}
			      text = s2.toString();
			      
			        }
			      String s1 = text.replaceAll("[\\{]{2}(.*)[\\}]{2}","");
			   
				return s1;	*/
		}
	
	
	/* TODO */
	/**
	 * Method to parse links and URLs.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * @param text: The text to be parsed
	 * @return 
	 * @return An array containing two elements as follows - 
	 *  The 0th element is the parsed text as visible to the user on the page
	 *  The 1st element is the link url
	 */
	public static String[] parseLinks(String test) {
			//[[link|text]] store as text,link in string
				String temp[] = new String[2];
				
				if("".equals(test)||test == null)
				{
					temp[0] = "";
					temp[1] = "";
					return temp;
				}
		
				Matcher m1 = Pattern.compile("[\\[]{2}[\\-a-zA-Z \\|\\,\\)\\(\\:\\.0-9\\=\\#]+[\\]]{2}|[=]{2,}(.*)[=]{2,}").matcher(test);
			    Matcher m2,m3;
				Pattern linkCheckPattern = Pattern.compile("^((?!:).)*$");
				Pattern linkRetrievePatter = Pattern.compile("\\[\\[(.*)\\]\\]");
				Pattern wikiPattern = Pattern.compile("^[\\[]{2}(Wikipedia|Wiktionary)[\\:](.*)[\\]]{2}$");
				Pattern media = Pattern.compile("[\\[]{2}(media)(.*)[\\|](.*)[\\]]{2}");
				Pattern file = Pattern.compile("[\\[]{2}(File)[:](.*)[\\]]{2}");
				Pattern categoryPattern = Pattern.compile("\\[\\[(.*):(.*)\\]\\]");
				Pattern extLink = Pattern.compile("^[\\[]([^\\[]*)[\\]]$");
				String text="";
				String link="";
				Pattern langLink = Pattern.compile("[\\[]{2}[a-z][a-z](.*)[\\]]{2}");
				ArrayList<String> temp1 = new ArrayList<String>();
				
				
				
				
				Matcher special = Pattern.compile("[a-zA-Z\\s]+[\\[]{2}(.*)[\\]]{2}(.*)").matcher(test);
				
				//special 
				/*while(special.find())
				{
						String matchedString = special.group().toString();
					
						Matcher linkMatcher2 = Pattern.compile("[\\[]{2}(.*)[\\]]{2}").matcher(matchedString);
						while(linkMatcher2.find()){	
						String matched = linkMatcher2.group().toString();
						//System.out.println("SPE OUT : "+matched);
							if(matched.contains("|"))
							{
								String temp3[] = matched.split("\\|");
								//System.out.println("SPE :"+temp3[0]);
								link = temp3[0].substring(2,temp3[0].length());
								//System.out.println("SPE LINK: "+link);
								matchedString = matchedString.replaceAll("[\\[]{2}(.*)[\\|]|[\\]]{2}","");
								link = link.replaceAll("[\\s]","_");
								link = link.substring(0, 1).toUpperCase() + link.substring(1);
								temp[0] = matchedString;
								temp[1] = link;
								
								return temp;
							}
							else
							{
								//System.out.println("inside");
								if(matchedString.contains("nowiki"))
								{
								///	System.out.println("wiki");
									int index1 = matchedString.indexOf("[");
									int index2 = matchedString.indexOf("]");
									matchedString = matchedString.substring(0,index1) +matchedString.substring(index1+2,index1+3)+matchedString.substring(index1+3);
									matchedString = matchedString.replaceAll("<nowiki />|[\\]]{2}","");
									//System.out.println(matchedString);
								}
								
						
								link = matched.substring(2,matched.length()-2);
								
								matchedString = matchedString.replaceAll("[\\[]{2}|[\\]]{2}","");
								link = link.replaceAll("[\\s]","_");
								link = link.substring(0, 1).toUpperCase() + link.substring(1);
								temp[0] = matchedString;
								temp[1] = link;
								
								return temp;
							}
							
						}
				}*/
					
				//}
				//}
				
				
				
				
				
				//Nowiki tag
				/*m2 = noWiki.matcher(test);
				while(m2.find()) {
					
					String matchedString = m1.group().toString();
					
				}*/
				
				
				
		while(m1.find()) {
			    	
			    	//System.out.println("Inside links");
			    	String matchedString = m1.group().toString();
			    	//System.out.println("MAI : "+matchedString);
			    	
			    	
			    	
			    	
			    	
			    	//File Links
					Matcher linkMatcher5 = file.matcher(matchedString);
					if(linkMatcher5.matches())
					{
						if(matchedString.contains("|"))
						{
							String temp2[] = matchedString.split("\\|");
							if(temp2[0]!=null)
							{
								link = temp2[temp2.length-1];
								link = link.substring(0,link.length()-2);
								temp[0] = link;
								temp[1] = "";
								
								//System.out.println("FILE : "+link);
							}
							
						}
						else
						{
							link = "";
							temp[0] = "";
							temp[1] = "";
							
							//System.out.println("FILE : "+link+"empty");
						}
						return temp;
						
					}
			    	
			    	//Media Links
					Matcher linkMatcher4 = media.matcher(matchedString);
					if(linkMatcher4.matches())
					{
						temp = matchedString.split("\\|");
						if(temp[0]!=null && temp[1]!=null)
						{
							link = temp[1].substring(0,temp[1].length()-2);
							temp[0] = link;
							temp[1] = "";
							//System.out.println("ML : "+link);
						}
						return temp;
					}
					
			    	
					//Wikipedia Links
					Matcher linkMatcher3 = wikiPattern.matcher(matchedString);
					if(linkMatcher3.matches())
					{
						if(matchedString.contains("|"))
						{
							temp = matchedString.split("\\|");
							if(temp[0]!=null && temp[1]!=null)
							{
								//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
								link = temp[0].substring(2,temp[0].length());
								text = temp[1].substring(0,temp[1].length()-2);
								//System.out.println("W : "+link+" "+text);
								if(link.contains("Wikipedia"))
								{
									if(!link.contains("#"))
									link = link.replaceAll("Wikipedia:", "");
									if(link.contains("("))
									{
										link = link.replaceAll("[\\s][\\(](.*)", "").trim();
										
									}
									temp[0] = link;
									temp[1] = "";
									//System.out.println("W1 : "+link);
									
								}
								if(matchedString.contains("Wiktionary"))
								{
									link = matchedString.replaceAll("Wiktionary:|[\\[]{2}|[\\]]{2}|[\\|]", "").trim();
									temp[0] = link;
									temp[1] = "";
									//System.out.println("W2 : "+link);
								}
								
							}
								
						}
						else
						{
							if(matchedString.contains("Wiktionary"))
							{
								link = matchedString.replaceAll("[\\[]{2}|[\\]]{2}", "").trim();
								temp[0] = link;
								temp[1] = "";
								//System.out.println("W3 : "+link);
							}
							if(matchedString.contains("Wikipedia"))
							{
								temp = matchedString.split(":");
								link = temp[1].substring(0,temp[1].length()-2);
								link = link.replaceAll("[\\s][\\(](.*)[\\)]", "");
								temp[0] = link;
								temp[1] = "";
								//System.out.println("W4 : "+link);
							}
							
						}
						return temp;
					}
			    	
			    	
			    	//Normal Links
			    	//System.out.println("Matched string : "+matchedString);
					Matcher linkMatcher = linkCheckPattern.matcher(matchedString);
					if(linkMatcher.matches()){
						
						
						
						Matcher linkMatcher2 = linkRetrievePatter.matcher(matchedString);
						if(linkMatcher2.matches()){
							if(matchedString.contains("|"))
							{
								//System.out.println("Inside NM if");
								temp = matchedString.split("\\|");
								if(temp[0]!=null && temp[1]!=null)
								{
									//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
									link = temp[0].substring(2,temp[0].length());
									text = temp[1].substring(0,temp[1].length()-2);
									//System.out.println("NM1 :"+link+" t "+text);
									
									if(text.equalsIgnoreCase(""))
									{
										System.out.println("inside");
										text = link.replaceAll("[,\\s]+[\\(](.*)|[\\,\\s]+(.*)", "");
									}
									
									//text+=" "+link;
									link = link.replaceAll("[\\s]","_");
									
									link = link.substring(0, 1).toUpperCase() + link.substring(1);
									temp[0] = text;
									temp[1] = link;
									//System.out.println("NM2 :"+"Text : "+text+" Link : "+link);
									
									//System.out.println("returning");
									//System.out.println("NM3 : "+temp[0]+" "+temp[1]);
									return temp;
								}
								
								
							}
							else
							{
								
								//System.out.println("Inside NM else");
									//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
									link = matchedString.substring(2,matchedString.length()-2);
									//text = matchedString.substring(0,temp[1].length()-2);
									//System.out.println("NM ELSE : "+link);
									matchedString = matchedString.replaceAll("[\\[]{2}|[\\]]{2}","");
									link = link.replaceAll("[\\s]","_");
									text = link.replaceAll("[,\\s]+[\\(](.*)|[\\,\\s]+(.*)", "");
									link = link.substring(0, 1).toUpperCase() + link.substring(1);
									temp[0] = link;
									temp[1] = link;
									//System.out.println("Link : "+matchedString+"\nLink : "+link.substring(0,link.length()-2));
									
									return temp;
								
							}
						}
						}
					
					
					
					//Categories
			    	Matcher catMatcher = categoryPattern.matcher(matchedString);

					if(catMatcher.matches()){
						//System.out.println("category");
						String type = catMatcher.group(1);
						if(type.contains(":"))
						{
							type = type.replaceAll(":", "");
							temp[0] = type+":"+catMatcher.group(2);
							temp[1] = "";
							return temp;
						}
						if("Category".equalsIgnoreCase(type)){
							String category = catMatcher.group(2);
							temp[0] = category;
							temp[1] = "";
							//System.out.println("C1 category : "+category);
						}
						else{
							
							temp[0] = catMatcher.group(1)+":"+catMatcher.group(2);
							temp[1] = "";
							//System.out.println("Lang : "+catMatcher.group(1)+":"+catMatcher.group(2));
						}
						return temp;
					}
					
					
			    }
		
		m3 = langLink.matcher(test);
		while(m3.find()) {
			
			String la = m3.group().toString();
			System.out.println(la);
			if(la.contains(":"))
				temp = la.split(":");
			temp[0] = temp[0].substring(2,temp[0].length())+":"+temp[1].substring(0,temp[1].length()-2);
			temp[1] = "";
			System.out.println("Lang : "+temp[0]+":"+temp[1]);
			return temp;
		}
		
		
		//Ext Link
				m2 = extLink.matcher(test);
				while(m2.find()) {
					
					
					String matchedString = m2.group().toString();
					System.out.println("EX : "+matchedString);
					//if(!matchedString.contains("Category") && !matchedString.contains("Wikipedia") && !matchedString.contains("Wiktionary") && !matchedString.contains("media") && !matchedString.contains("File"))
				//	{
						if(matchedString.contains(" "))
						{
							temp = matchedString.split(" ");
							if(!temp[1].equalsIgnoreCase(""))
							{
								text = temp[1];
								temp[0] = text.substring(0,text.length()-1);
								temp[1] = "";
								
								//System.out.println("EX1 : "+text.substring(0,text.length()-1));
								
							}
						}
						else
						{
							temp[0] = "";
							temp[1] = "";
							//System.out.println("EX2 : "+"No");
						}
					//}
					return temp;
					
				}
		
		
		return null;
	}
	
	public static ArrayList<String> parseLinks1(String test) throws IOException {
		//[[link|text]] store as text,link in string
				//String test = "[[File:wiki.png|frame|alt=Puzzle globe|Wikipedia logo]]";
				
		File file = new File("F://links.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		// if file doesnt exists, then create it
		
			try {
				if (!file.exists()) 
					file.createNewFile();
			
				 fw = new FileWriter(file.getAbsoluteFile(),true);
				 bw = new BufferedWriter(fw);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String test12 = test;
		//test12 = parseTagFormatting(test12);
				Matcher m1 = Pattern.compile("[\\[]{2}(.*)[\\]]{2}").matcher(test12);
			   
				Pattern linkCheckPattern = Pattern.compile("^((?!:).)*$");
				Pattern linkRetrievePatter = Pattern.compile("[\\[]{2}[\\-a-zA-Z \\|\\,\\'\\)\\(\\:0-9\\!]+[\\]]{2}");
				
				String temp[];
				ArrayList<String> temp1 = new ArrayList<String>();
				String text="";
				String link="";
				Pattern categoryPattern = Pattern.compile("\\[\\[(.*):(.*)\\]\\]");
				Matcher linkMatcher = null;
				Pattern wikiPattern = Pattern.compile("[\\[]{2}Wikipedia[\\:](.*)[\\]]{2}");
				Matcher ext = Pattern.compile("[\\[](http)[:](.*)[\\]]").matcher(test12);
				
				
		while(m1.find()) {
			    	
			    	
			    	String matchedString = m1.group().toString();
			    	//bw.write("MATCHED : "+matchedString+"\n\n");
			    	//bw.write(matchedString+"\n");
			    	
			    	Matcher catMatcher = categoryPattern.matcher(matchedString);
			    	if(matchedString.contains("Category"))
			    		if(catMatcher.matches()){
							String type = catMatcher.group(1);
							if("Category".equalsIgnoreCase(type)){
								String category = catMatcher.group(2);
								//System.out.println("cate : "+category);
								//temp1.add(category);
							}
							else{
								//temp1.add(catMatcher.group(1));
								//temp1.add(catMatcher.group(2));
							}
						}
			    
			    	//Normal Links
			    	//System.out.println("Matched string : "+matchedString);
					// linkMatcher = linkCheckPattern.matcher(matchedString);
					//if(linkMatcher.matches()){
						
						
						
						Matcher linkMatcher2 = linkRetrievePatter.matcher(matchedString);
						while(linkMatcher2.find()){
							String m2 = linkMatcher2.group().toString();
							//System.out.println("M: "+m2);
							//bw.write(m2+"\n");
							if(m2.contains("|"))
							{
								temp = m2.split("\\|");
								if(temp[0]!=null && temp[1]!=null)
								{
									//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
									//System.out.println("SSSSS : "+matchedString);
									link = temp[0].substring(2,temp[0].length());
									if(!"".equalsIgnoreCase(temp[1]))
									text = temp[1].substring(0,temp[1].length()-2);
									//System.out.println(link+" "+text);
									
									if(text.equalsIgnoreCase(""))
									{
										//System.out.println("inside");
										text = link.replaceAll("[,\\s]+[\\(](.*)|[\\,\\s]+(.*)", "");
									}
									
									//text+=" "+link;
									link = link.replaceAll("[\\s]","_");
									//System.out.println(" Link : "+link);
									try {
										if(!"".equalsIgnoreCase(link))
										link = link.substring(0, 1).toUpperCase() + link.substring(1);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										System.out.println("error : "+temp[0]);
									}
									//System.out.println("Text : "+text+" Link : "+link);
									temp1.add(text);
									temp1.add(link);
									//bw.write(link+"\n"+text+"\n");
									b=b+2;
									
									
								}
								
							}
							else
							{
								
									//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
									link = m2.substring(2,m2.length());
									//text = matchedString.substring(0,temp[1].length()-2);
									m2 = m2.replaceAll("[\\[]{2}|[\\]]{2}","");
									text = link.substring(0,link.length()-2);
									link = link.replaceAll("[\\s]","_");
								//	System.out.println("Link : "+matchedString+"\nLink : "+link.substring(0,link.length()-2));
									link = link.substring(0,link.length()-2);
									
									temp1.add(link);
									temp1.add(text);
									//bw.write(link+"\n"+text+"\n");
									b++;
									
								
							}
						}
						//}
					
					if(matchedString.contains("[[File:"))
					{
						//bw.write("TION : "+matchedString+"\n");
						String media="",st="";
						int index1=0,index2=0;
					
						//System.out.println(matchedString);
						 index1 = matchedString.indexOf("[[File:");
						 media = matchedString.substring(index1);
						// System.out.println(wiki);
						 index1 = media.indexOf("[[File:");
						 index2 = media.lastIndexOf("]]");
						//System.out.println(index1+" "+index2);
						media = media.substring(index1,index2+2);
						//bw.write("WIKI "+wiki+"\n");
						
						media = parseTagFormatting(media);
						//bw.write("F: "+media+"\n");
						
						Matcher linkMatcher7 = linkRetrievePatter.matcher(media);
						while(linkMatcher7.find())
						{
							st = linkMatcher7.group().toString();
							if(st.contains("|"))
							{
								temp = st.split("\\|");
								if(temp[0]!=null && temp[1]!=null)
								{
									//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
									//System.out.println("SSSSS : "+matchedString);
									link = temp[0].substring(2,temp[0].length());
									if(!"".equalsIgnoreCase(temp[1]))
									text = temp[1].substring(0,temp[1].length()-2);
									//System.out.println(link+" "+text);
									
									if(text.equalsIgnoreCase(""))
									{
										//System.out.println("inside");
										text = link.replaceAll("[,\\s]+[\\(](.*)|[\\,\\s]+(.*)", "");
									}
									
									//text+=" "+link;
									link = link.replaceAll("[\\s]","_");
									//System.out.println(" Link : "+link);
									try {
										if(!"".equalsIgnoreCase(link))
										link = link.substring(0, 1).toUpperCase() + link.substring(1);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										System.out.println("error : "+temp[0]);
									}
									//System.out.println("Text : "+text+" Link : "+link);
									temp1.add(text);
									temp1.add(link);
									//bw.write(link+"\n"+text+"\n");
									b=b+2;
									
									
								}
								
							}
							else
							{
								
									//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
									link = st.substring(2,st.length());
									//text = matchedString.substring(0,temp[1].length()-2);
									st = st.replaceAll("[\\[]{2}|[\\]]{2}","");
									text = link.substring(0,link.length()-2);
									link = link.replaceAll("[\\s]","_");
								//	System.out.println("Link : "+matchedString+"\nLink : "+link.substring(0,link.length()-2));
									link = link.substring(0,link.length()-2);
									
									temp1.add(link);
									//temp1.add(text);
									//bw.write(link+"\n"+text+"\n");
									b++;
									
								
							}
						}
						
						if(media.contains("|"))
						{
							temp = media.split("\\|");
							if(temp[0]!=null)
							{
								//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
								
								text = temp[temp.length-1].substring(0,temp[temp.length-1].length()-2);
								text = text.replaceAll("\\[|\\]","");
									/*try {
									//	bw.write(text+"\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
									//System.out.println("W1 : "+link);
									
								
							}
						}
						
						
					}
					
					//WiktionaryLinks
					
					if(matchedString.contains("Wiktionary"))
					{
						//bw.write("TION : "+matchedString+"\n");
						String wiki="";
						int index1=0,index2=0;
					
						//System.out.println(matchedString);
						 index1 = matchedString.indexOf("[[Wiktionary:");
						 wiki = matchedString.substring(index1);
						// System.out.println(wiki);
						 index1 = wiki.indexOf("[[Wiktionary:");
						 index2 = wiki.indexOf("]]");
						//System.out.println(index1+" "+index2);
						wiki = wiki.substring(index1,index2+2);
						//bw.write("WIKI "+wiki+"\n");
						if(wiki.contains("|"))
						{
							temp = wiki.split("\\|");
							if(temp[0]!=null && temp[1]!=null)
							{
								//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
								link = temp[0].substring(2,temp[0].length());
								text = temp[temp.length-1].substring(0,temp[temp.length-1].length()-2);
								if(link.contains("Wiktionary"))
								{
									
									link = link.replaceAll("Wiktionary:", "");
									
									temp1.add(link);
									/*try {
										//bw.write(link+"\n"+text+"\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
									//System.out.println("W1 : "+link);
									
								}
							}
						}
						else
						{
							
								link = wiki.replaceAll("[\\[]{2}|[\\]]{2}|Wiktionary:", "").trim();
								temp1.add(link);
								/*try {
									//bw.write(link+"\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
								//System.out.println("W3 : "+link);
						}
						
					}
					
					
					//WikipediaLinks
					Matcher linkMatcher3 = wikiPattern.matcher(matchedString);
					if(linkMatcher3.matches())
					{
						String pediaString="";
						int index1=0,index2=0;
						
						if(matchedString.contains("[[Wikipedia"))
						{
						 index1 = matchedString.indexOf("[[Wikipedia:");
						 index2 = matchedString.indexOf("]]");
						}
						
						pediaString = matchedString.substring(index1,index2+2);
						//bw.write("in "+matchedString+"\n");
						if(pediaString.contains("|"))
						{
							temp = pediaString.split("\\|");
							if(temp[0]!=null && temp[1]!=null)
							{
								//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
								link = temp[0].substring(2,temp[0].length());
								text = temp[temp.length-1].substring(0,temp[temp.length-1].length()-2);
							
								
								if(link.contains("Wikipedia"))
								{
									if(!link.contains("#"))
									link = link.replaceAll("Wikipedia:", "");
									if(link.contains("("))
									{
										link = link.replaceAll("[\\s][\\(](.*)", "").trim();
										
									}
									temp1.add(link);
									/*try {
										//bw.write(link+"\n"+text+"\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
									
									
								}
								
								
							}
								
						}
						else
						{
							
							if(matchedString.contains("Wikipedia"))
							{
								temp = matchedString.split(":");
								link = temp[1].substring(0,temp[1].length()-2);
								link = link.replaceAll("[\\s][\\(](.*)[\\)]", "");
								temp1.add(link);
								/*try {
									//bw.write(link+"\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
								
							}
							
						}
						
					}
					
			    }
		
		while(ext.find())
		{
			
			String ex = ext.group().toString();
			String ex2="",st="";
			
			int index1=0,index2=0;
			
			Matcher linkMatcher2 = linkRetrievePatter.matcher(ex);
			while(linkMatcher2.find())
			{
				st = linkMatcher2.group().toString();
				if(st.contains("|"))
				{
					temp = st.split("\\|");
					if(temp[0]!=null && temp[1]!=null)
					{
						
						link = temp[0].substring(2,temp[0].length());
						if(!"".equalsIgnoreCase(temp[1]))
						text = temp[1].substring(0,temp[1].length()-2);
						//System.out.println(link+" "+text);
						
						if(text.equalsIgnoreCase(""))
						{
							
							text = link.replaceAll("[,\\s]+[\\(](.*)|[\\,\\s]+(.*)", "");
						}
						
						
						link = link.replaceAll("[\\s]","_");
						
						try {
							if(!"".equalsIgnoreCase(link))
							link = link.substring(0, 1).toUpperCase() + link.substring(1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("error : "+temp[0]);
						}
						
						temp1.add(text);
						temp1.add(link);
						//bw.write(link+"\n"+text+"\n");
						b=b+2;
						
						
					}
					
				}
				else
				{
					
						
						link = st.substring(2,st.length());
						
						st = st.replaceAll("[\\[]{2}|[\\]]{2}","");
						text = link.substring(0,link.length()-2);
						link = link.replaceAll("[\\s]","_");
					
						link = link.substring(0,link.length()-2);
						
						temp1.add(link);
						//temp1.add(text);
						//bw.write(link+"\n"+text+"\n");
						b++;
						
					
				}
			}
			
			if(ex.contains("[http:"))
			{
				index1 = ex.indexOf("[http:");
				index2 = ex.indexOf("]");
				ex2 = ex.substring(index1,index2+1);
				
					
						temp = ex2.split(" ");
						
						for(int i=1;i<temp.length;i++)
							text += temp[i]+" ";
						text = text.replaceAll("\\]","");
						
						
				
						
						//bw.write(text+"\n");
						text="";
						ex = ex.substring(index2,ex.length());
						
			}
		}
		//bw.close();
		return temp1;
	}
	
	
	public static String parseLinks2(String test) {
		//[[link|text]] store as text,link in string
			String temp[] = new String[2];
			
			if("".equals(test)||test == null)
			{
				temp[0] = "";
				temp[1] = "";
				//return temp;
			}
	//System.out.println(test);
			Matcher m1 = Pattern.compile("[\\[]{2}[\\-a-zA-Z \\|\\,\\)\\(\\:\\.0-9\\=\\#]+[\\]]{2}|[=]{2,}(.*)[=]{2,}").matcher(test);
		    Matcher m2,m3;
			Pattern linkCheckPattern = Pattern.compile("^((?!:).)*$");
			Pattern linkRetrievePatter = Pattern.compile("\\[\\[(.*)\\]\\]");
			Pattern wikiPattern = Pattern.compile("^[\\[]{2}(Wikipedia|Wiktionary)[\\:](.*)[\\]]{2}$");
			Pattern media = Pattern.compile("[\\[]{2}(media)(.*)[\\|](.*)[\\]]{2}");
			Pattern image = Pattern.compile("[\\[]{2}(Image)(.*)[\\|](.*)[\\]]{2}");
			Pattern file = Pattern.compile("[\\[]{2}(File)(.*)[\\]]{2}");
			Pattern categoryPattern = Pattern.compile("\\[\\[(.*):(.*)\\]\\]");
			Pattern extLink = Pattern.compile("^[\\[]([^\\[]*)[\\]]");
			Pattern langLink = Pattern.compile("[\\[]{2}[a-z][a-z](.*)[\\]]{2}");
			String text="";
			String link="";
			
			ArrayList<String> temp1 = new ArrayList<String>();
			
			
			
			
			
			Matcher special = Pattern.compile("[a-zA-Z\\s]+[\\[]{2}(.*)[\\]]{2}(.*)").matcher(test);
			
			//special 
			/*while(special.find())
			{
					String matchedString = special.group().toString();
				
					Matcher linkMatcher2 = Pattern.compile("[\\[]{2}(.*)[\\]]{2}").matcher(matchedString);
					while(linkMatcher2.find()){	
					String matched = linkMatcher2.group().toString();
					//System.out.println("SPE OUT : "+matched);
						if(matched.contains("|"))
						{
							String temp3[] = matched.split("\\|");
							//System.out.println("SPE :"+temp3[0]);
							link = temp3[0].substring(2,temp3[0].length());
							//System.out.println("SPE LINK: "+link);
							matchedString = matchedString.replaceAll("[\\[]{2}(.*)[\\|]|[\\]]{2}","");
							//link = link.replaceAll("[\\s]","_");
							link = link.substring(0, 1).toUpperCase() + link.substring(1);
							extra +=link;
							temp[0] = matchedString;
							temp[1] = link;
							
							//return temp;
						}
						else
						{
							//System.out.println("inside");
							if(matchedString.contains("nowiki"))
							{
							///	System.out.println("wiki");
								int index1 = matchedString.indexOf("[");
								int index2 = matchedString.indexOf("]");
								matchedString = matchedString.substring(0,index1) +matchedString.substring(index1+2,index1+3)+matchedString.substring(index1+3);
								matchedString = matchedString.replaceAll("<nowiki />|[\\]]{2}","");
								//System.out.println(matchedString);
							}
							
					
							link = matched.substring(2,matched.length()-2);
							
							matchedString = matchedString.replaceAll("[\\[]{2}|[\\]]{2}","");
							//link = link.replaceAll("[\\s]","_");
							link = link.substring(0, 1).toUpperCase() + link.substring(1);
							extra +=link;
							temp[0] = matchedString;
							temp[1] = link;
							
							//return temp;
						}
						
					}
			}*/
				
			//}
			//}
			
			
			
			
			
			//Nowiki tag
			/*m2 = noWiki.matcher(test);
			while(m2.find()) {
				
				String matchedString = m1.group().toString();
				
			}*/
			
			//File Links
			Matcher linkMatcher5 = file.matcher(test);
			while(linkMatcher5.find())
			{
				String matchedString = linkMatcher5.group().toString();
				//System.out.println("ppp");
				if(matchedString.contains("|"))
				{
					String temp2[] = matchedString.split("\\|");
					if(temp2[0]!=null)
					{
						link = temp2[temp2.length-1];
						
						link = link.substring(0,link.length()-2).replaceAll("\\[|\\]","");
						extra += link;
						temp[0] = link;
						temp[1] = "";
						
						//System.out.println("FILE : "+link);
					}
					
				}
				else
				{
					link = "";
					temp[0] = "";
					temp[1] = "";
					//System.out.println("FILE : "+link+"empty");
				}
				//return temp;
				
			}
			
			//Media Links
			Matcher linkMatcher4 = image.matcher(test);
			while(linkMatcher4.find())
			{
				String matchedString = linkMatcher4.group().toString();
				temp = matchedString.split("\\|");
				if(temp[0]!=null)
				{
					link = temp[temp.length-1];
					//System.out.println("PP : "+link);
					link = link.substring(0,link.length()-2);
					extra += link;
					temp[0] = link;
					temp[1] = "";
					//extra +=temp[0]+" ";
					//System.out.println("ML : "+link);
				}
				//return temp;
			}
			
	while(m1.find()) {
		    	
		    	//System.out.println("Inside links");
		    	String matchedString = m1.group().toString();
		    	//System.out.println("MAI : "+matchedString);
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
				
		    	
				//Wikipedia Links
				Matcher linkMatcher3 = wikiPattern.matcher(matchedString);
				if(linkMatcher3.matches())
				{
					if(matchedString.contains("|"))
					{
						temp = matchedString.split("\\|");
						if(temp[0]!=null && temp[1]!=null)
						{
							//System.out.println("Text : "+temp[i].substring(2,temp[i].length()));
							link = temp[0].substring(2,temp[0].length());
							text = temp[1].substring(0,temp[1].length()-2);
							//System.out.println("W : "+link+" "+text);
							if(link.contains("Wikipedia"))
							{
								if(!link.contains("#"))
								link = link.replaceAll("Wikipedia:", "");
								if(link.contains("("))
								{
									link = link.replaceAll("[\\s][\\(](.*)", "").trim();
									
								}
								temp[0] = link;
								temp[1] = "";
								extra +=temp[0]+" ";
								//System.out.println("W1 : "+link);
								
							}
							if(matchedString.contains("Wiktionary"))
							{
								link = matchedString.replaceAll("Wiktionary:|[\\[]{2}|[\\]]{2}|[\\|]", "").trim();
								temp[0] = link;
								temp[1] = "";
								extra +=temp[0]+" ";
								//System.out.println("W2 : "+link);
							}
							
						}
							
					}
					else
					{
						if(matchedString.contains("Wiktionary"))
						{
							link = matchedString.replaceAll("[\\[]{2}|[\\]]{2}", "").trim();
							temp[0] = link;
							temp[1] = "";
							extra +=temp[0]+" ";
							//System.out.println("W3 : "+link);
						}
						if(matchedString.contains("Wikipedia"))
						{
							temp = matchedString.split(":");
							link = temp[1].substring(0,temp[1].length()-2);
							link = link.replaceAll("[\\s][\\(](.*)[\\)]", "");
							temp[0] = link;
							temp[1] = "";
							extra +=temp[0]+" ";
							//System.out.println("W4 : "+link);
						}
						
					}
					//return temp;
				}
		    	
		    	
		    
				
				
		    }
			
			
	//Ext Link
			m2 = extLink.matcher(test);
			while(m2.find()) {
				
				
				String matchedString = m2.group().toString();
				//System.out.println("EX : "+matchedString);
				//if(!matchedString.contains("Category") && !matchedString.contains("Wikipedia") && !matchedString.contains("Wiktionary") && !matchedString.contains("media") && !matchedString.contains("File"))
			//	{
					if(matchedString.contains(" "))
					{
						temp = matchedString.split(" ");
						if(!temp[2].equalsIgnoreCase(""))
						{
							for(int i=2;i<temp.length;i++)
								text += " "+temp[i];
							temp[0] = text.substring(0,text.length()-1);
							temp[1] = "";
							
							//System.out.println("EX1 : "+text.substring(0,text.length()-1));
							extra +=temp[0]+" ";
							
						}
					}
					else
					{
						temp[0] = "";
						temp[1] = "";
						//System.out.println("EX2 : "+"No");
					}
				//}
				//return temp;
				
			}
	//System.out.println("lll : "+extra);
	
	return extra;
}
}
