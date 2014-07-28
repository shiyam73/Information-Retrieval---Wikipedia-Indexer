package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.CAPITALIZATION)
public class CapitalizationTokenizer implements TokenizerRule {

	static Pattern capitalPattern = Pattern.compile("^[A-Z]([^0-9A-Z]*)\\b");

	public CapitalizationTokenizer() {

	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		System.out.println("Capitalization Rule");
		try {
			while (stream.hasNext()) {
				String value = stream.next();
				if (value.contains(" ")) {

					String[] words = value.split(" ");
					StringBuilder sb = new StringBuilder();
					if (words.length > 0) {
						Matcher capitalMatcher = capitalPattern
								.matcher(words[0]);
						if (capitalMatcher.matches()) {
							StringBuilder decap = new StringBuilder();
							decap.append(Character.toLowerCase(words[0]
									.charAt(0)));
							decap.append((words[0].length() > 1 ? words[0]
									.substring(1) : ""));
							words[0] = decap.toString();
						}
						for (int i = 0; i < words.length; i++) {
							if (i < words.length - 1) {
								sb.append(words[i]);
								sb.append(" ");
							} else {
								sb.append(words[i]);
							}
						}
						if ("".equals(sb.toString().trim())) {
							stream.previous();
							stream.remove();
						} else {
							stream.previous();
							stream.set(sb.toString().trim());
							stream.next();
						}
					}
				} else {
					Matcher capitalMatcher = capitalPattern.matcher(value);
					StringBuilder decap = new StringBuilder();
					if (capitalMatcher.matches()) {
						decap.append(Character.toLowerCase(value.charAt(0)));
						decap.append((value.length() > 1 ? value.substring(1)
								: ""));
						value = decap.toString();
					}
					stream.previous();
					if(!stream.hasPrevious()){
					stream.set(decap.toString().trim());
					}
					stream.next();
				}
			}

			// TODO Auto-generated method stub
			/*
			 * System.out.println("Apply Capitalization Rule"+Thread.currentThread
			 * (). getId()); while(stream.hasNext()){ stream.next(); String
			 * value = stream.previous(); if(value.contains(" ")){ String[]
			 * words = value.split(" "); StringBuilder sb = new StringBuilder();
			 * 
			 * for(int i=0;i<words.length;i++){ Matcher capitalMatcher =
			 * capitalPattern.matcher(words[i]); if(capitalMatcher.find()){
			 * words[i] = words[i].toLowerCase(); } if(i<words.length - 1){
			 * sb.append(words[i]); sb.append(" "); }else{ sb.append(words[i]);
			 * } } stream.set(sb.toString()); }else{ Matcher capitalMatcher =
			 * capitalPattern.matcher(value); if(capitalMatcher.matches()){
			 * value = value.toLowerCase(); } } stream.next(); } stream.reset();
			 */
			stream.reset();

		} catch (Exception e) {
			System.out.println("Stream next::" + stream.next());
			System.out.println("Stream previous::" + stream.previous());
			System.out.println("Capitalization");
			e.printStackTrace();
		}
	}
}
