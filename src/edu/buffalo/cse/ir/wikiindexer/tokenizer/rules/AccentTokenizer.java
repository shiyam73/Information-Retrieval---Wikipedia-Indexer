package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.ACCENTS)

public class AccentTokenizer implements TokenizerRule{

	public AccentTokenizer(){
		
	}
	
	private static final String PLAIN_ASCII =
		      "AaEeIiOoUu"    // grave
		    + "AaEeIiOoUuYy"  // acute
		    + "AaEeIiOoUuYy"  // circumflex
		    + "AaOoNn"        // tilde
		    + "AaEeIiOoUuYy"  // umlaut
		    + "Aa"            // ring
		    + "Cc"            // cedilla
		    + "OoUu"          // double acute
		    + "a";

		    private static final String UNICODE =
		     "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"             
		    + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" 
		    + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" 
		    + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
		    + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" 
		    + "\u00C5\u00E5"                                                             
		    + "\u00C7\u00E7" 
		    + "\u0150\u0151\u0170\u0171" 
		    + "\u0430"
		    ;
	
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Apply Accent Rule"+Thread.currentThread().getId());
		String temp="";
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		int n=0,index=0;
		boolean is = false;
		char h = 0;
		int check=0;
		
		while(stream.hasNext())
		{
			    temp = stream.next();
					//System.out.println("TEMP : "+temp);
		         n = temp.length();
		         check = n;
		         
		         if(temp.contains("а̀"))

		         {
		        	 System.out.println("ppp");
		        	 temp = temp.replaceAll("\\а̀","a");
		        	 continue;
		         }
		         
		       for (int i = 0; i < n; i++) {
		    	   
		          char c = temp.charAt(i);
		          //System.out.print(c);
		          int pos = UNICODE.indexOf(c);
		          if (pos > -1){
		        	  //System.out.println("pos : "+pos);
		        	   h = PLAIN_ASCII.charAt(pos);
		        	   
		        	  /*System.out.println(h);
		        	  if(h == 'a')
		        		  System.out.println("asdka");*/
		              //sb1.append(h);
		              //System.out.println(sb1.toString());
		              is = true;
		              index = i;
		              
		          }
		          
		          if(is)
		          {
		        	  sb1.setLength(0);
		        	  sb1.append(temp);
		        	 // System.out.println(sb1.length());
		        	  sb1.setCharAt(index, h);
		        	  if(((index+1)<sb1.length()))
		        	  {
		        		  char t = sb1.charAt(index+1);
		        		 // System.out.println("T : "+t);
		        		  pos = UNICODE.indexOf(t);
		        		//  System.out.println("T1 :"+ pos);
		        		  if(!(t >= 'a' && t <= 'z') && t!='-')
		        		  {
			        		  sb1.deleteCharAt(index+1);
			        		  n--;
		        		  }
		        	  }
		        	  temp = sb1.toString();
		        	  //System.out.println("OUT : "+sb1.toString());
		        	  is = false;
		        	  stream.previous();
		        	  stream.set(temp);
		        	  if(stream.hasNext())
		        	  stream.next();
		        	  continue;
		        	  
		          }
		          
		          //else {
		        	  //System.out.println(c);
		             // sb.append(c);
		         // }
		       }
		       
		}
		stream.reset();	
		//System.out.println(sb.toString());
		
	}
}