package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.PUNCTUATION)

public class PunctuationTokenizer implements TokenizerRule{

	public PunctuationTokenizer(){
		
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Punctuation Tokenizer"+Thread.currentThread().getId());
		
		while(stream.hasNext()){
			stream.next();
			String s = stream.previous();
			
			s = s.replaceAll("([!?.,]\\s)"," ");
			s= s.replaceAll("(([!?.])+$)","");
			stream.set(s);
			stream.next();
		}
		
		stream.reset();
	}

}
