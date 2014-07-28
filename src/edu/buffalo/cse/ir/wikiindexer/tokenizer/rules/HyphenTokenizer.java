package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenTokenizer implements TokenizerRule {

	static Pattern hyphenPattern = Pattern.compile("[-]+");
	static Pattern numericPattern = Pattern.compile("\\d");

	public HyphenTokenizer() {

	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {

		boolean numericFlag = false;
		// TODO Auto-generated method stub
		System.out.println("Apply Hyphen Tokenizer"
				+ Thread.currentThread().getId());

		while (stream.hasNext()) {
			numericFlag = false;
			String s = stream.next();
			String[] splitTokens = s.trim().split(" ");
			StringBuilder sb = new StringBuilder();
			if (splitTokens.length > 1) {
				//System.out.println("split length > 1::"+s);
				for (int i = 0; i < splitTokens.length; i++) {
					numericFlag = false;
					Matcher hyphenMatcher = hyphenPattern
							.matcher(splitTokens[i]);
					if (isNumeric(splitTokens[i])) {
						numericFlag = true;
						continue;
					}

					if (hyphenMatcher.find()) {
						String[] hyphenatedTokens = splitTokens[i].split("-");
						StringBuilder hyphenRemoved = new StringBuilder();
						for (int j = 0; j < hyphenatedTokens.length; j++) {
							hyphenRemoved.append(hyphenatedTokens[j]);
							if (j < hyphenatedTokens.length - 1) {
								hyphenRemoved.append(" ");
							}
						}
						splitTokens[i] = hyphenRemoved.toString();
					}
					sb.append(splitTokens[i]);
					if (i < splitTokens.length - 1) {
						sb.append(" ");
					}
				}
				if (!numericFlag) {
					if ("".equals(sb.toString().trim())) {
						stream.previous();
						stream.remove();
					} else {
						stream.previous();
						stream.set(sb.toString());
						stream.next();
					}
				}
			} else if(splitTokens.length == 1){
				if (isNumeric(s)) {
					numericFlag = false;
					continue;
				}
				Matcher hyphenMatcher = hyphenPattern.matcher(s);
				if (hyphenMatcher.find()) {
					String[] hyphenatedTokens = s.split("-");
					StringBuilder hyphenRemoved = new StringBuilder();
					for (int j = 0; j < hyphenatedTokens.length; j++) {
						hyphenRemoved.append(hyphenatedTokens[j]);
						if (j < hyphenatedTokens.length - 1) {
							hyphenRemoved.append(" ");
						}
					}
					String temp = hyphenRemoved.toString().trim();
					if (!numericFlag) {
						if ("".equals(temp)) {
							stream.previous();
							stream.remove();
						} else {
							stream.previous();
							stream.set(temp);
							stream.next();
						}
					}
					// stream.addAll(hyphenatedTokens);
				}
			}else{
				stream.previous();
				stream.remove();
			}
		}
		stream.reset();
	}

	public boolean isNumeric(String value) {
		Matcher m = numericPattern.matcher(value);
		return m.find();
	}
}
