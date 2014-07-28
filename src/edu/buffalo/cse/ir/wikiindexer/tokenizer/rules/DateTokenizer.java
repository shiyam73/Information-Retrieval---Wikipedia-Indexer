package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RuleClass(className = RULENAMES.DATES)
public class DateTokenizer implements TokenizerRule {

	boolean isMonth = false;
	boolean isdate = false;
	boolean utc = false;
	boolean isPeriod = false;
	boolean isComma = false;
	boolean isdot = false;
	final String UNICODE = "\u00D0";
	final String PLAIN_ASCII = "|";

	/*
	 * File file = new File("F://tokenize.txt"); FileWriter fw; BufferedWriter
	 * bw = null;
	 */

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		// System.out.println("Apply Date Tokenizer"+Thread.currentThread().getId());
		/*
		 * try { if (!file.exists()) file.createNewFile();
		 * 
		 * fw = new FileWriter(file.getAbsoluteFile(),true); bw = new
		 * BufferedWriter(fw);
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		ArrayList<String> before = new ArrayList<String>();
		ArrayList<String> after = new ArrayList<String>();
		String result = "";
		String next = "", next1 = "", prev = "";
		String previous = "";
		String teDate[];
		String temp1 = "";

		while (stream.hasNext()) {
			try {
				String temp = stream.next();
				temp = isUniPresent(temp);

				temp = temp.replaceAll("\\(|\\)", "");
				isComma = iscomma(temp);
				temp = temp.replaceAll("\\,", "");
				isdot = isDot(temp);
				temp = temp.replaceAll("\\.", "");
				temp = temp.replaceAll("\\;", "");
				boolean a = isNumeric(temp);

				// //System.out.println(temp+" boolean "+a);

				// temp = temp.replaceAll("\\(|\\)", "");

				// if(temp.contains("ï¿½"))
				// temp1 = temp.replaceAll("ï¿½","^");

				// //System.out.println(temp);

				if (temp.contains("�")) {
					teDate = temp.split("\\�");
					// //System.out.println(teDate[1]);
					before.add("SS");
					result = dt(before, teDate[0]);
					// //System.out.println(result);
					result += "�" + dateAlone(teDate[1], "");
					if (isdot)
						result += ".";

					// result = result.replaceAll(":","ï¿½");
					// //System.out.println(result);
					stream.previous();
					stream.set(result);
					stream.next();
					continue;
				}

				if (temp.length() == 4 && a) {
					temp = apos(temp);

					int i;
					// year = temp;
					// ////System.out.println("year");
					stream.previous();
					for (i = 0; i < 6; i++) {
						if (stream.hasPrevious())
							before.add(stream.previous());
						else
							break;
					}

					// ////System.out.println(i);
					// result = dt(before,temp);

					for (int j = 0; j < i + 1; j++) {
						if (stream.hasNext()) {
							// String temp1 = stream.next();
							// if(temp1.equalsIgnoreCase(temp))
							// break;
							// stream.remove();
							// //System.out.println("FOR : "+stream.next());

							stream.next();
						}
					}
					// apos(temp);
					result = dt(before, temp);
					// //System.out.println(result);
					if (!result.equalsIgnoreCase("")
							&& !result.equalsIgnoreCase(null)) {
						// stream.set(result);
						// stream.next();
						// ////System.out.println(isMonth+" "+isdate);
						if (!isMonth && !isdate) {
							// stream.previous();
							if (!stream.hasNext()) {
								stream.append("ss");
								stream.set(result);

							}
							for (int j = 0; j < 2; j++) {

								// //System.out.println("POINT : "+stream.previous());
								stream.previous();
								stream.remove();
							}
							if (utc) {
								// //System.out.println("utc true");
								for (int j = 0; j < 5; j++) {

									// //System.out.println("POINT UTC : "+stream.previous());
									stream.previous();
									stream.remove();
								}
								utc = false;
								continue;
							}

						} else {
							stream.previous();
							isMonth = false;
							isdate = false;
							stream.set(result);
							continue;
						}
						// stream.next();

						stream.previous();
						stream.set(result);
						if (stream.hasNext())
							stream.next();
					}
					before.clear();
					// ////System.out.println("FINAL : "+stream.toString());
					a = false;
				}

				if (temp.equalsIgnoreCase("am") || temp.equalsIgnoreCase("pm")) {
					stream.previous();
					if (stream.hasPrevious())
						previous = stream.previous();
					// //System.out.println("Asas "+previous);

					a = isNumeric(previous);

					if (previous.contains(":"))
						a = true;
					// //System.out.println("p : "+a);
					if (a && previous.length() <= 5) {
						result = time(temp, previous);
						// ////System.out.println(time(temp,previous));
						stream.remove();
						stream.set(result);
					}
					stream.next();
					stream.next();
					continue;
				}

				if (temp.length() <= 3 && a) {

					if (stream.hasNext()) {
						if (stream.hasPrevious()) {

							stream.previous();
							if (stream.hasPrevious()) {
								prev = stream.previous();
								stream.next();
							}
							stream.next();
						}
						next = stream.next();
						next1 = next;
						next = next.replaceAll("\\.", "");

						isdot = isDot(next1);
						next1 = next1.replaceAll("\\.", "");
						if (next1.equalsIgnoreCase("am")
								|| next1.equalsIgnoreCase("pm")) {
							stream.previous();
							a = isNumeric(next1);
							// //System.out.println("p : "+a);
							if (a) {

								stream.remove();
								result = time(next1, temp);
								stream.previous();
								stream.set(result);
								isdot = false;
							}
							// //System.out.println(result);
							continue;
						}

						int check = isMonth(next);
						// //System.out.println("NEXT : "+next);
						if (check > 0 || next.length() > 2) {
							stream.previous();
							continue;
						}
					}
					if (next.equalsIgnoreCase("BC")
							|| next.equalsIgnoreCase("AD")) {
						// //System.out.println("jjj");
						isPeriod = true;
						result = period(temp, next1);

					}
					if (stream.hasPrevious() && temp.length() == 2 && !isPeriod) {
						stream.previous();
						stream.previous();
						// prev = stream.previous();
						// //System.out.println(temp+" "+prev);
						result = dateAlone(temp, prev);
						stream.set(result);
						stream.previous();
						stream.remove();

						continue;

					}
					if (stream.hasPrevious()) {
						stream.previous();
						stream.remove();
						stream.previous();
						// stream.remove();
						stream.set(result);
						// ////System.out.println("B$ : "+stream.previous());
						// stream.remove();
					}

					a = false;
					// //System.out.println(stream.toString());
					// stream.set(result);
				}

				// temp = temp.replaceAll("\\.","");

				/*
				 * if(temp.equalsIgnoreCase("am") ||
				 * temp.equalsIgnoreCase("pm")) { stream.previous();
				 * if(stream.hasPrevious()) previous = stream.previous();
				 * ////System.out.println("Asas "+previous); result =
				 * time(temp,previous);
				 * //////System.out.println(time(temp,previous));
				 * stream.remove(); stream.set(result); }
				 */

				// ////System.out.println(temp.indexOf("PM"));
				if (temp.indexOf("AM") > -1 || temp.indexOf("PM") > -1) {
					int pos = -1;
					// //System.out.println("temp "+temp);
					// //System.out.println("INSIDE");
					if (temp.indexOf("PM") > -1)
						pos = temp.indexOf("PM");
					if (temp.indexOf("AM") > -1)
						pos = temp.indexOf("AM");
					if (pos > 0) {
						char bf = temp.charAt(pos - 1);
						// //System.out.println(bf+"");
						if (bf >= '0' && bf <= '9') {
							stream.previous();
							result = time1(temp);
							// ////System.out.println(time1(temp));
							// stream.remove();
							stream.set(result);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Date error:: previous" + stream.previous());
				System.out.println("Date error:: next" + stream.next());
				System.out.println("Date error:: stream" + stream);
				e.printStackTrace();

			}
		}

		isPeriod = false;
		stream.reset();
		// //System.out.println("FINAL : "+stream.toString());

		/*
		 * try { bw.write("\n FINAL : "+stream.toString()+"\n"); bw.close(); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	public String isUniPresent(String temp) {
		char t = 0;
		String out = "";
		StringBuilder sb = new StringBuilder();

		for (char c : temp.toCharArray()) {
			// //System.out.println("C : "+c);
			int pos = UNICODE.indexOf(c);
			if (pos > -1) {
				// //System.out.println("its there");
				t = PLAIN_ASCII.charAt(pos);
				// //System.out.println(t);
				sb.append(t);

			} else
				sb.append(c);
		}

		/*
		 * if(temp.contains("ï¿½")) out = temp.replaceAll("ï¿½","|");
		 */

		out = sb.toString();
		// //System.out.println(out);
		return out;
	}

	public String time1(String time) {
		String tempTim[] = null;
		int a = 0;
		String tim = "";
		String mat = "";
		boolean num;
		Matcher m1 = Pattern.compile("[0-9\\:]+PM|[0-9\\:]+AM").matcher(time);
		while (m1.find()) {

			time = m1.group().toString();
			// //System.out.println("TI : "+time);

		}
		if (time.indexOf("AM") > -1) {
			mat = time.substring(0, time.indexOf("AM"));

			// mat = time.substring(0,time.indexOf("PM"));
			// //System.out.println("T2 : "+mat);
			if (mat.contains(":")) {

				tempTim = mat.split(":");
				// tim = tempTim[0];
				a = Integer.parseInt(tempTim[0]);
				tim = a + "";

				tim += ":" + tempTim[1] + ":00";
			} else {

				a = Integer.parseInt(mat);
				tim = a + "";

				tim += ":" + "00:00";
			}

			// //System.out.println(time);

		}
		if (time.indexOf("PM") > -1) {

			// time = time.substring(0,time.indexOf("PM"));

			/**/
			// //System.out.println(time);
			mat = time.substring(0, time.indexOf("PM"));
			// //System.out.println("T2 : "+mat);
			if (mat.contains(":")) {

				tempTim = mat.split(":");
				// tim = tempTim[0];
				a = Integer.parseInt(tempTim[0]);
				tim = (12 + a) + "";

				tim += ":" + tempTim[1] + ":00";
			} else {

				a = Integer.parseInt(mat);
				tim = (12 + a) + "";

				tim += ":" + "00:00";
			}
		}
		if (isdot)
			tim += ".";
		return tim;
	}

	public String time(String type, String previous) {
		String tim = "";
		String tempTim[] = null;
		int a = 0;
		// //System.out.println(previous+" "+type);
		// if(previous.contains(":"))
		// {
		if (type.equalsIgnoreCase("am")) {
			// tim = previous+":00";
			if (previous.contains(":")) {
				tempTim = previous.split(":");
				// tim = tempTim[0];
				a = Integer.parseInt(tempTim[0]);
				tim = a + "";

				tim += ":" + tempTim[1] + ":00";
			}

			else {

				a = Integer.parseInt(previous);
				tim = a + "";

				tim += ":" + "00:00";
			}

		}
		if (type.equalsIgnoreCase("pm")) {
			if (previous.contains(":")) {
				tempTim = previous.split(":");
				// tim = tempTim[0];
				a = Integer.parseInt(tempTim[0]);
				tim = (12 + a) + "";

				tim += ":" + tempTim[1] + ":00";
			}

			else {

				a = Integer.parseInt(previous);
				tim = (12 + a) + "";

				tim += ":" + "00:00";
			}
		}

		// }
		// //System.out.println("TIME : "+tim);
		if (isdot)
			tim += ".";
		// //System.out.println(tim);
		return tim;
	}

	public boolean isNumeric(String str) {
		if (str.contains("\'s")) {
			int index = str.indexOf('\'');
			str = str.substring(0, index);
			// ////System.out.println("apos : "+str);
		}
		if (str.contains(",")) {
			int index = str.indexOf(",");
			str = str.substring(0, index);
			// ////System.out.println("apos : "+str);
		}

		return str.matches("\\d+(\\.\\:\\d+)?"); // match a number with optional
													// '-' and decimal.
	}

	public String apos(String year) {
		// ////System.out.println(year);
		if (year.contains("\'s")) {
			// ////System.out.println("apos "+year);
			int index = year.indexOf('\'');
			year = year.substring(0, index);
			// ////System.out.println(year);
		}
		return year;
	}

	public boolean iscomma(String year) {
		// ////System.out.println(year);
		// //System.out.println("comma there");
		if (year.contains(","))
			return true;
		return false;

	}

	public boolean isDot(String year) {
		// ////System.out.println(year);
		if (year.contains("."))
			return true;
		return false;
	}

	public String commaR(String year) {
		// ////System.out.println("comma "+year);
		if (year.contains(",")) {
			// //System.out.println("comma "+year);
			year = year.replaceAll(",", "");
			// //System.out.println(year);
		}
		return year;
	}

	public String period(String year, String period)

	{
		String date = "01", month = "01";
		int length = 0;
		length = year.length();
		String out = "";
		boolean isDot1 = false;

		isDot1 = period.contains(".");
		period = period.replace(".", "");
		if (period.equalsIgnoreCase("BC")) {
			if (length == 2)
				year = "-00" + year;
			if (length == 3)
				year = "-0" + year;
		}
		if (period.equalsIgnoreCase("AD")) {
			if (length == 2)
				year = "00" + year;
			if (length == 3)
				year = "0" + year;
		}

		// //System.out.println(isDot);
		if (isdot)
			out = year + "" + month + "" + date + ".";
		else
			out = year + "" + month + "" + date;
		// //System.out.println("PERIOD : "+out);
		return out;
	}

	public String dt(ArrayList<String> before, String year) {
		int isDate = 0, isMon = 0;
		boolean containsDigit = false;
		String date = "", month = "";
		// boolean isComma=false;
		int com = 0;
		String temp = "", prev = "";
		String out = "";
		// ////System.out.println("year : "+year);
		// year = year.replaceAll("\\,", "");
		// isComma = iscomma(year);

		// ////System.out.println(before.size());
		for (int i = 0; i < before.size(); i++) {
			// ////System.out.println("BF : "+before.get(i));
			temp = before.get(i).replaceAll("\\(|\\)", "");
			/*
			 * temp = temp.replaceAll("\\.",""); temp =
			 * temp.replaceAll("\\,","");
			 */
			temp = temp.replaceAll("\\(|\\)", "");
			// //System.out.println("DT : "+temp);
			containsDigit = isNumeric(temp);
			isMon = isMonth(temp);

			if (temp.equalsIgnoreCase("UTC")) {
				if (i + 1 < before.size()) {
					prev = before.get(i + 1);
					utc = true;
				}
				// ////System.out.println("U : "+prev);

			}

			if (!containsDigit && isMon == 0)
				continue;
			temp = commaR(temp);

			// if(!date.equalsIgnoreCase(""))
			// {
			isMon = isMonth(temp);
			// //System.out.println(isMon);
			/*
			 * if(isMon == 0) { isMonth = true;
			 * 
			 * month = "01"; }
			 */
			// //System.out.println(isMon);
			if (isMon > 0) {
				// //System.out.println("Month : "+isMon+" Year : "+year);
				if (isMon < 10)
					month = "0" + isMon;
				else
					month = isMon + "";

			}
			/*
			 * if(isMon == 0) { isMonth = true;
			 * 
			 * month = "01"; }
			 */

			/*
			 * if(date.equalsIgnoreCase("")) { isdate = true; date = "01"; }
			 */
			isMon = 0;
			// }

			if (containsDigit) {

				if (temp.length() <= 2) {
					// //System.out.println(temp);
					temp = temp.replaceAll("\"", "");

					try {
						isDate = Integer.parseInt(temp);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						// //System.out.println("TEMP : "+temp);

					}

					if (isDate < 10)
						date = "0" + isDate;
					else
						date = isDate + "";

				}
			}
			// ////System.out.println("bool : "+isMonth+" "+isdate);

		}
		// ////System.out.println("DATE : "+date);

		out = commaR(year);
		if (isComma) {

			// //System.out.println("year "+year);
			// out = out.replaceAll(",","");
			out += "" + month + "" + date + ",";
		} else
			out += "" + month + "" + date;

		if (month.equalsIgnoreCase("") && date.equalsIgnoreCase("")) {
			isMonth = true;
			isdate = true;
			date = "a";
			out += "0101";
		}
		if (!prev.equalsIgnoreCase(""))
			out += " " + prev;
		// //System.out.println("DT OUT : "+out);
		if (date.equalsIgnoreCase("")) {
			date = "a";

			out += "01";
		}
		return out;
	}

	public String dateAlone(String date, String month) {
		String year = "1900";
		String out = "";
		int isMon = 0;

		isMon = isMonth(month);
		// //System.out.println(isMon);

		if ("".equals(month) || null == month)
			out = "20" + date + "0101";
		else
			out = year + "0" + isMon + "" + date;
		// //System.out.println(out);
		return out;

	}

	public int isMonth(String check) {
		int index = 0;
		String months[] = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };
		String smallMonths[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
				"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		for (int i = 0; i < months.length; i++)
			if (months[i].equalsIgnoreCase(check)
					|| smallMonths[i].equalsIgnoreCase(check))
				index = i + 1;
		// ////System.out.println(index);
		return index;
	}

}
