package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.WHITESPACE)
public class WhitespaceTokenizer implements TokenizerRule {

	public WhitespaceTokenizer() {

	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Whitespace tokenizer"
				+ Thread.currentThread().getId());
		try {
			while (stream.hasNext()) {
				String s = stream.next();
				String token[] = s.split("\\s");
				if (token.length > 0) {
					stream.previous();
					stream.set(token);
					stream.next();
				} else {
					stream.previous();
					stream.remove();
				}
			}

			stream.reset();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
