package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.NUMBERS)

public class NumberTokenizer implements TokenizerRule{

	static Pattern floatingPointPattern = Pattern.compile("[-+]?[0-9]*,\\.?[0-9]+([eE][-+]?[0-9]+)?");
	static Pattern alphaPattern = Pattern.compile("[^0-9]+$");

	public NumberTokenizer(){
		
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		
		System.out.println("Number Tokenizer"+Thread.currentThread().getId());

		while(stream.hasNext()){
			String s = stream.next();
			Matcher m = floatingPointPattern.matcher(s);
			if(m.matches()){
				// Contains number only
			}
			
			m = alphaPattern.matcher(s);
			if(m.matches()){
				
			}else{
				if(s.contains(" ")){
					//System.out.println("contains space");
				}else if(s.length() == 8){
					//System.out.println("Seems like date");
				}
				else{
				s = s.replaceAll("[0-9,\\.eE[+|-]]","");
				}
				
			}
			
			if("".equalsIgnoreCase(s)){
				stream.previous();
				stream.remove();
			}else{
				stream.previous();
				stream.set(s);
				stream.next();
			}
		}
		
		stream.reset();
	}

}
