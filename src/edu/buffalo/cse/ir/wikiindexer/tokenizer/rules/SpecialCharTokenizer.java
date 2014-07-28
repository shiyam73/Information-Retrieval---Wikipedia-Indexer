package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.SPECIALCHARS)

public class SpecialCharTokenizer implements TokenizerRule{

	
	static Pattern numericPattern = Pattern.compile("\\d");
	static Pattern alphaPattern = Pattern.compile("[^0-9]+$");
	static Pattern middlePatter = Pattern
			.compile("(?<=\\w)([~`@#$%^&\"\\*()\\{\\}\\[\\]\\+=|><;:\\/^])+(?=\\w)");
	static Pattern startEndPattern = Pattern
			.compile("(?<!\\w)([\\*~`@#$%^&\"()\\{\\}\\[\\]\\+=|><;:\\/]+)(?=\\w)|([\\*~`@#$%^&\"()\\{\\}\\[\\]\\+=|><;:\\/])(?!\\w)");
	static Pattern underScorePattern = Pattern
			.compile("(?<!\\w)([_])+(?=\\w?)|([_])(?!\\w)|([_])");
	static Pattern endPattern = Pattern
			.compile("([\\*~`@#$%^&\"\\{\\}\\[\\]()\\-+=|><;:\\/])+(?!\\w)");
	
public SpecialCharTokenizer(){
		
	}
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Special Char Tokenizer"+Thread.currentThread().getId());
		
		while (stream.hasNext()) {
			String token = stream.next();

			Matcher m = underScorePattern.matcher(token);
			if (m.find()) {
				
				token = token.replaceAll(
						"(?<!\\w)([_])+(?=\\w?)|([_])(?!\\w^_)|([_])", " ");
				token = token.trim();
			}

			m = startEndPattern.matcher(token);

			if (m.find()) {
				
				token = token.replaceAll(
						"(?<!\\w)([\\*~`@#$%^&\"\\{\\}\\[\\]()\\+=|><;:\\\\/]+)(?=\\w)", "");
				token = token.replaceAll("([\\*~`@#$%^&\"\\{\\}\\[\\]()\\+=|><;:\\\\/])(?!\\w)",
						"");
			}

			m = middlePatter.matcher(token);

			if (m.find()) {
				
				/*Matcher hyphen = Pattern.compile("[-]").matcher(token);
				if (hyphen.find()) {
					if (!isAlpha(token)) {
						if ("".equalsIgnoreCase(token)) {
							stream.previous();
							stream.remove();
						} else {
							stream.previous();
							stream.set(token);
							stream.next();
						}
						continue;
					}
				}*/
				String[] tokens = token
						.split("(?<=\\w)([~`@#$%^&\"\\*()\\{\\}\\[\\]\\-+=|><;:\\/^])+(?=\\w)|(?<=\\w)([_])+(?=\\w)");
				if(tokens.length > 0){
				ArrayList<String> listItems = new ArrayList<String>();
				for (int i = 0; i < tokens.length; i++) {
					if (!("".equalsIgnoreCase(tokens[i]))) {
						listItems.add(tokens[i]);
					}
				}
				stream.previous();
				stream.set(listItems.toArray(new String[listItems.size()]));
				stream.next();
				}else{
					stream.previous();
					stream.remove();
				}
			} else {
				if ("".equalsIgnoreCase(token)) {
					stream.previous();
					stream.remove();
				} else {
					stream.previous();
					stream.set(token);
					stream.next();
				}
			}

			/*
			 * m = endPattern.matcher(token);
			 * 
			 * if(m.find()){ System.out.println("Token matches 2::" + token);
			 * token = token.replaceAll("([*~`@#$%^&()-+=|><;:\\/])(?!\\w)","");
			 * System.out.println("Token3::"+token); }
			 */

			/*
			 * m = middlePatter.matcher(token); if (m.find()) {
			 * System.out.println("Token matches 2::" + token); /*if
			 * (isAlpha(token)) { continue; } String[] tokens =
			 * token.split("[~`@#$^%&*()-+=|><;:\\/^]"); ArrayList<String>
			 * modified = new ArrayList<String>(); // modified.toArray(); for
			 * (int j = 0; j < tokens.length; j++) { if
			 * (!("".equalsIgnoreCase(tokens[j]))) { modified.add(tokens[j]); }
			 * } System.out.println("modified::" + modified.toString()); }
			 */
			// System.out.println(" ");
			// if()
		}

		stream.reset();
	}

	public boolean isAlpha(String value) {
		Matcher m = alphaPattern.matcher(value);
		return m.find();
	}

}
