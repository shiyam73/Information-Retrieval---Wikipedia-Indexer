package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.APOSTROPHE)
public class ApostropheTokenizer implements TokenizerRule {

	public ApostropheTokenizer() {

	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		try{
		if (stream != null) {
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				String[] splitTokens = token.split(" ");
				StringBuilder sb = new StringBuilder();
				if (splitTokens.length > 1) {
					for (int i = 0; i < splitTokens.length; i++) {
						String[] splitonApo = splitTokens[i].split("'");
						if (splitonApo.length > 1) {
							processApostrophePrefix(splitonApo);
							processApostropheSuffix(splitonApo);
							StringBuilder apoRemoved = new StringBuilder();
							for (int j = 0; j < splitonApo.length; j++) {
								apoRemoved.append(splitonApo[j]);
								if (j < splitonApo.length - 1) {
									apoRemoved.append(" ");
								}
							}
							splitTokens[i] = apoRemoved.toString();
						}
						sb.append(splitTokens[i]);
						if (i < splitTokens.length - 1) {
							sb.append(" ");
						}
					}
					stream.previous();
					stream.set(sb.toString());
					stream.next();
				} else {
					if (token != null) {
						String[] splitonApo = token.split("'");
						if (splitonApo.length > 0) {
							boolean prefixRuleApplied = false;
							boolean suffixRuleApplied = false;
							if (splitonApo.length > 1) {
								prefixRuleApplied = processApostrophePrefix(splitonApo);
								suffixRuleApplied = processApostropheSuffix(splitonApo);
								StringBuilder modifiedValue = new StringBuilder();
								if (!prefixRuleApplied && !suffixRuleApplied) {
									for (int j = 0; j < splitonApo.length; j++) {
										if (!("".equalsIgnoreCase(splitonApo[j]))) {
											modifiedValue.append(splitonApo[j]);
										}
									}
									stream.previous();
									stream.set(modifiedValue.toString());
									stream.next();
								} else {
									stream.previous();
									stream.set(splitonApo);
									stream.next();
								}

								prefixRuleApplied = false;
								suffixRuleApplied = false;
							} else {
								stream.previous();
								stream.set(splitonApo);
								stream.next();
							}
						} else {
							stream.previous();
							stream.remove();
						}
					}
				}
			}
		}
		stream.reset();
		}catch(Exception e){
			System.out.println("Apostrophe::"+stream.toString());
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private boolean processApostrophePrefix(String[] tokenized) {
		if ("l".equalsIgnoreCase(tokenized[0])) {
			tokenized[0] = "le";
			return true;
		} else if ("d".equalsIgnoreCase(tokenized[0])) {
			tokenized[0] = "de";
			return true;
		}
		return false;
	}

	private boolean processApostropheSuffix(String[] tokenized) {
		if ("d".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			tokenized[tokenized.length - 1] = "would";
			return true;
		} else if ("m".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			tokenized[tokenized.length - 1] = "am";
			return true;
		} else if ("s".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			if (tokenized[0].equalsIgnoreCase("let")) {
				tokenized[tokenized.length - 1] = "us";
				return true;
			} else if(tokenized[0].equalsIgnoreCase("he") || tokenized[0].equalsIgnoreCase("she")){
				
				tokenized[tokenized.length - 1] = "is";
				return true;
			}else{
				tokenized[tokenized.length - 1] = "";
			}
		} else if ("ve".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			tokenized[tokenized.length - 1] = "have";
			return true;
		} else if ("re".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			tokenized[tokenized.length - 1] = "are";
			return true;
		} else if ("ll".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			tokenized[tokenized.length - 1] = "will";
			return true;
		} else if ("t".equalsIgnoreCase(tokenized[tokenized.length - 1])) {
			tokenized[tokenized.length - 1] = "not";
			String trimString = tokenized[tokenized.length - 2];
			if ((trimString.endsWith("n"))) {
				if ((trimString.equalsIgnoreCase("shan"))) {
					tokenized[tokenized.length - 2] = "shall";
				} else if ((trimString.equalsIgnoreCase("won"))) {
					tokenized[tokenized.length - 2] = "will";
				}else if((trimString.equalsIgnoreCase("ain"))){
					tokenized[tokenized.length - 2] = "am";
				}
				else if (!(trimString.equalsIgnoreCase("can"))) {
					tokenized[tokenized.length - 2] = trimString.substring(0,
							trimString.length() - 1);
				}
			}
			return true;
		}else if("em".equalsIgnoreCase(tokenized[tokenized.length - 1])){
			tokenized[tokenized.length - 1] = "them";
		}
		return false;
	}
}