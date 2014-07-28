package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.STOPWORDS)

public class StopwordTokenizer implements TokenizerRule {

	// List of Strings to be tokenized
	//"a", "an", "and", "are", "as", "at", "be", "but", 
	//"by", "for", "if", "in", "into", "is", "it", "no", 
	//"not", "of", "on", "or", "s", "such", "t", "that", 
	//"the", "their", "then", "there", "these", "they", 
	//"this", "to", "was", "will", "with"
	
public StopwordTokenizer(){
		
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Stopword Tokenizer"+Thread.currentThread().getId());
		System.out.println("Stopword Tokenizer"+Thread.currentThread().getId());
		//Collection<String> tokens = stream.getAllTokens();

		String stopWords[] = {"a","an","and","as", "at","am", "be", "but","by","do","dont", "for", "if", "in", "into", "is", "it", "no","not", "of", "on", "or", "s", "such", "t", "that","the", "their", "then", "there", "these", "they","this", "to", "was", "will", "with","you","your"};
		while(stream.hasNext())
		{
			String token = stream.next();
			for(int i=0;i<stopWords.length;i++)
			{

				if(token.equalsIgnoreCase(stopWords[i]))
				{
					stream.previous();
					stream.remove();

				}
			}
		}
		stream.reset();
	}

}
