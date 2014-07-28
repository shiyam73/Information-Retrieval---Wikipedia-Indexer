/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.test;

import static org.junit.Assert.fail;

import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

/**
 * @author nikhillo
 * 
 */
@RunWith(Parameterized.class)
public class DateRuleTest extends TokenizerRuleTest {

	public DateRuleTest(Properties props) {
		super(props, IndexerConstants.DATERULE);
	}

	@Test
	public void testRule() {
		if (rule == null) {
			fail("Rule not implemented");

		} else {
			try {
				if (isPreTokenization) {
					assertArrayEquals(
							new Object[] { "Vidya Balan born 19780101 is an Indian actress." },
							runtest("Vidya Balan born 1 January 1978 is an Indian actress."));
					assertArrayEquals(
							new Object[] { "President Franklin D. Roosevelt to proclaim 19411207, 'a date which will live in infamy'" },
							runtest("President Franklin D. Roosevelt to proclaim December 7, 1941, 'a date which will live in infamy'"));
					assertArrayEquals(
							new Object[] { "The Academy operated until it was destroyed by Lucius Cornelius Sulla in -00840101" },
							runtest("The Academy operated until it was destroyed by Lucius Cornelius Sulla in 84 BC"));
					assertArrayEquals(
							new Object[] { "For instance, the 19480101 ABL finalist Baltimore Bullets moved to the BAA and won that league's 19480101 title." },
							runtest("For instance, the 1948 ABL finalist Baltimore Bullets moved to the BAA and won that league's 1948 title."));
					assertArrayEquals(
							new Object[] { "It was now about 10:15:00." },
							runtest("It was now about 10:15 am."));
					assertArrayEquals(
							new Object[] { "Godse approached Gandhi on 19480130 during the evening prayer at 17:15:00." },
							runtest("Godse approached Gandhi on January 30, 1948 during the evening prayer at 5:15PM."));
					assertArrayEquals(
							new Object[] { "Pune is known to have existed as a town since 08470101." },
							runtest("Pune is known to have existed as a town since 847 AD."));
					assertArrayEquals(
							new Object[] { "The 20040101 Indian Ocean earthquake was an undersea megathrust earthquake that occurred at 20041226 00:58:53" },
							runtest("The 2004 Indian Ocean earthquake was an undersea megathrust earthquake that occurred at 00:58:53 UTC on Sunday, 26 December 2004"));
					assertArrayEquals(
							new Object[] { "19000411 is the 101st day of the year (102nd in leap years) in the Gregorian calendar." },
							runtest("April 11 is the 101st day of the year (102nd in leap years) in the Gregorian calendar."));
					assertArrayEquals(
							new Object[] { "Apple is one of the world's most valuable publicly traded companies in 20110101Ð20120101." },
							runtest("Apple is one of the world's most valuable publicly traded companies in 2011Ð12."));
				} else {
					assertArrayEquals(
							new Object[] { "Vidya", "Balan", "born",
									"19780101", "is", "an", "Indian",
									"actress." },
							runtest("Vidya", "Balan", "born", "1", "January",
									"1978", "is", "an", "Indian", "actress."));
					assertArrayEquals(
							new Object[] { "President", "Franklin", "D.",
									"Roosevelt", "to", "proclaim", "19411207,",
									"'a", "date", "which", "will", "live",
									"in", "infamy'" },
							runtest("President", "Franklin", "D.", "Roosevelt",
									"to", "proclaim", "December", "7,",
									"1941,", "'a", "date", "which", "will",
									"live", "in", "infamy'"));
					assertArrayEquals(
							new Object[] { "The", "Academy", "operated",
									"until", "it", "was", "destroyed", "by",
									"Lucius", "Cornelius", "Sulla", "in",
									"-00840101" },
							runtest("The", "Academy", "operated", "until",
									"it", "was", "destroyed", "by", "Lucius",
									"Cornelius", "Sulla", "in", "84", "BC"));
					assertArrayEquals(
							new Object[] { "For", "instance,", "the",
									"19480101", "ABL", "finalist", "Baltimore",
									"Bullets", "moved", "to", "the", "BAA",
									"and", "won", "that", "league's",
									"19480101", "title." },
							runtest("For", "instance,", "the", "1948", "ABL",
									"finalist", "Baltimore", "Bullets",
									"moved", "to", "the", "BAA", "and", "won",
									"that", "league's", "1948", "title."));
					assertArrayEquals(
							new Object[] { "It", "was", "now", "about",
									"10:15:00." },
							runtest("It", "was", "now", "about", "10:15", "am."));
					assertArrayEquals(
							new Object[] { "Godse", "approached", "Gandhi",
									"on", "19480130", "during", "the",
									"evening", "prayer", "at", "17:15:00." },
							runtest("Godse", "approached", "Gandhi", "on",
									"January", "30,", "1948", "during", "the",
									"evening", "prayer", "at", "5:15PM."));
					assertArrayEquals(
							new Object[] { "Pune", "is", "known", "to", "have",
									"existed", "as", "a", "town", "since",
									"08470101." },
							runtest("Pune", "is", "known", "to", "have",
									"existed", "as", "a", "town", "since",
									"847", "AD."));
					assertArrayEquals(
							new Object[] { "The", "20040101", "Indian",
									"Ocean", "earthquake", "was", "an",
									"undersea", "megathrust", "earthquake",
									"that", "occurred", "at",
									"20041226 00:58:53" },
							runtest("The", "2004", "Indian", "Ocean",
									"earthquake", "was", "an", "undersea",
									"megathrust", "earthquake", "that",
									"occurred", "at", "00:58:53", "UTC", "on",
									"Sunday,", "26", "December", "2004"));
					assertArrayEquals(
							new Object[] { "19000411", "is", "the", "101st",
									"day", "of", "the", "year", "(102nd", "in",
									"leap", "years)", "in", "the", "Gregorian",
									"calendar." },
							runtest("April", "11", "is", "the", "101st", "day",
									"of", "the", "year", "(102nd", "in",
									"leap", "years)", "in", "the", "Gregorian",
									"calendar."));
					
					assertArrayEquals(new Object[] {"200825"}, runtest("25", "(2008)"));
					assertArrayEquals(new Object[] {"200705"}, runtest("5", "(2007)"));
					assertArrayEquals(new Object[] {"200716"}, runtest("16", "(2007)"));
					assertArrayEquals(new Object[] {"200804"}, runtest("4", "(2008)"));
					assertArrayEquals(new Object[] {"200802"}, runtest("2", "(2008)"));
					assertArrayEquals(new Object[] {"200703"}, runtest("3", "(2007)"));
					assertArrayEquals(new Object[] {"200701"}, runtest("1", "(2007)"));
					assertArrayEquals(new Object[] {"(1)", "David_Lempert"}, runtest("(1)", "David_Lempert"));
					assertArrayEquals(new Object[] {"201002"}, runtest("2", "(2010)"));

					assertArrayEquals(new Object[] {"afghanistan", "(updated", "20070101", "-User", "(UTC)", "American", "Samoa", 
							"Andorra", "Angola", "Anguilla", "Antarctica",
							"Antigua", "Barbuda", "Arctic", "Ocean","Argentina", "Armenia", "Aruba", "Ashmore","Cartier", "Islands", 
							"Atlantic", "Ocean", "Australia", "Austria", "Azerbaijan","Afghanistan", "Akrotiri", "Albania", "Algeria", 
							"American_Samoa", "Andorra", "Angola", "Anguilla", "Antarctica","Antigua_and_Barbuda", "Arctic_Ocean", "Argentina", 
							"Armenia","Aruba", "Ashmore_and_Cartier_Islands","Atlantic_Ocean", "Australia", "Austria", "Azerbaijan"}, runtest("afghanistan", "(updated", "20070101", "-User", "(UTC)", "American", "Samoa", 
							"Andorra", "Angola", "(updated", "2003)","User", "GreenmanGreenman", "31", "Oct", "2003", "Anguilla", "Antarctica", 
							"Antigua", "Barbuda", "Arctic", "Ocean","Argentina", "Armenia", "Aruba", "Ashmore","Cartier", "Islands", 
							"Atlantic", "Ocean", "Australia", "Austria", "Azerbaijan","Afghanistan", "Akrotiri", "Albania", "Algeria", 
							"American_Samoa", "Andorra", "Angola", "Anguilla", "Antarctica","Antigua_and_Barbuda", "Arctic_Ocean", "Argentina", 
							"Armenia","Aruba", "Ashmore_and_Cartier_Islands","Atlantic_Ocean", "Australia", "Austria", "Azerbaijan"));
					
					assertArrayEquals(new Object[]{"BahamasBahamas"," Bahrain"," Baker"," Islandname=UMBeginning"," 20060101"," edition",
							" World"," Factbook"," entry"," territory"," has","been"," merged"," new"," entry"," Bangladesh"," Barbados",
							" Belarus"," Belgium"," Belize"," Benin"," (geography"," done)"," Bermuda"," (updated"," all"," stats"," Economy",
							" Bermuda"," User"," 19000037"," 20070216"," (UTC))"," Bhutan"," Bolivia"," Bosnia"," Herzegovina"," (partly"," 2000",
							" partly"," 2003)"," Botswana"," (updated"," 2003)"," User"," GreenmanGreenman"," 2"," Nov"," 2003"," Bouvet"," Island",
							" Brazil"," British"," Indian"," Ocean"," Territory"," British"," Virgin"," Islands"," (Geography"," British"," Virgin",
							" IslandsGeography"," updated"," 2004)"," Brunei"," Bulgaria"," Burkina"," Faso"," Burma"," Burundi"," Bahamas"," Bahamas",
							" Bahrain"," Baker_Island"," Bangladesh"," Barbados"," Belarus"," Belgium"," Belize"," Benin"," Bermuda"," Economy_of_Bermuda",
							" Bhutan"," Bolivia"," Bosnia_and_Herzegovina"," Botswana"," Bouvet_Island"," Brazil"," British_Indian_Ocean_Territory"," British_Virgin_Islands",
							" Geography"," Geography_of_the_British_Virgin_Islands"," Brunei"," Bulgaria"," Burkina_Faso"," Burma"," Burundi"}, 
							runtest("BahamasBahamas"," Bahrain"," Baker"," Islandname=UMBeginning",
							" 20060101"," edition"," World"," Factbook"," entry"," territory"," has","been"," merged"," new",
							" entry"," Bangladesh"," Barbados"," Belarus"," Belgium"," Belize"," Benin"," (geography"," done)",
							" Bermuda"," (updated"," all"," stats"," Economy"," Bermuda"," User"," 19000037"," 20070216"," (UTC))",
							" Bhutan"," Bolivia"," Bosnia"," Herzegovina"," (partly"," 2000"," partly"," 2003)"," Botswana"," (updated"," 2003)",
							" User"," GreenmanGreenman"," 2"," Nov"," 2003"," Bouvet"," Island"," Brazil"," British"," Indian"," Ocean"," Territory",
							" British"," Virgin"," Islands"," (Geography"," British"," Virgin"," IslandsGeography"," updated"," 2004)"," Brunei"," Bulgaria",
							" Burkina"," Faso"," Burma"," Burundi"," Bahamas"," Bahamas"," Bahrain"," Baker_Island"," Bangladesh"," Barbados"," Belarus"," Belgium",
							" Belize"," Benin"," Bermuda"," Economy_of_Bermuda"," Bhutan"," Bolivia"," Bosnia_and_Herzegovina"," Botswana"," Bouvet_Island"," Brazil",
							" British_Indian_Ocean_Territory"," British_Virgin_Islands"," Geography"," Geography_of_the_British_Virgin_Islands"," Brunei"," Bulgaria",
							" Burkina_Faso"," Burma"," Burundi"));
					
					assertArrayEquals(
							new Object[] { "Apple", "is", "one", "of", "the",
									"world's", "most", "valuable", "publicly",
									"traded", "companies", "in",
									"20110101�20120101." },
							runtest("Apple", "is", "one", "of", "the",
									"world's", "most", "valuable", "publicly",
									"traded", "companies", "in", "2011�12."));
				}

			} catch (TokenizerException e) {

			}
		}
	}
}
