package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DELIM)
public class DelimTokenizer implements TokenizerRule {

	public DelimTokenizer() {

	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Apply Delim Tokenizer"
				+ Thread.currentThread().getId());
		try {
			while (stream.hasNext()) {
				String s = stream.next();
				String lines[] = s.split("\\r?\\n");

				stream.previous();

				if (lines == null || lines.length == 0) {
					stream.remove();
				} else {
					stream.set(lines);
				}
				stream.next();
			}
			stream.reset();
		} catch (Exception e) {
			System.out.println("Thread id::" + Thread.currentThread().getId());
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
