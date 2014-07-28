package edu.buffalo.cse.ir.wikiindexer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * 
 */

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants.RequiredConstant;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexReader;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexerException;
import edu.buffalo.cse.ir.wikiindexer.indexer.SharedDictionary;
import edu.buffalo.cse.ir.wikiindexer.parsers.Parser;
import edu.buffalo.cse.ir.wikiindexer.test.AllTests;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerFactory;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.DocumentTransformer;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.IndexableDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;

/**
 * @author nikhillo
 * 
 */
public class SingleRunner {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		} else {
			if (args[0] != null && args[0].length() > 0) {
				String filename = args[0];
				Properties properties = loadProperties(filename);
				if (properties == null) {
					System.err
							.println("Error while loading the Properties file. Please check the messages above and try again");
					System.exit(2);
				} else {
					if (args[1] != null && args[1].length() == 2) {
						String mode = args[1].substring(1).toLowerCase();

						if ("t".equals(mode)) {
							runTests(filename);
						} else if ("i".equals(mode)) {
							runIndexer(properties);
						} else if ("b".equals(mode)) {
							runTests(filename);
							runIndexer(properties);
						} else {
							System.err.println("Invalid mode specified!");
							printUsage();
							System.exit(4);
						}
					} else {
						System.err.println("Invalid or no mode specified!");
						printUsage();
						System.exit(5);
					}
				}
			} else {
				System.err
						.println("The provided properties filename is empty or could not be read");
				printUsage();
				System.exit(3);
			}
		}

	}

	private static void runIndexer(Properties properties)
			throws InterruptedException {
		long start, start1;
		System.out.println("Starting .......");
		ArrayList<WikipediaDocument> list = new ArrayList<WikipediaDocument>();
		Parser parser = new Parser(properties);
		start = start1 = System.currentTimeMillis();
		parser.parse(FileUtil.getDumpFileName(properties), list);

		System.out.println("Finished parsing: "
				+ (System.currentTimeMillis() - start));
		Map<INDEXFIELD, Tokenizer> tknizerMap;
		ExecutorService svc = Executors.newSingleThreadExecutor();
		CompletionService<IndexableDocument> pool = new ExecutorCompletionService<IndexableDocument>(
				svc);
		int numdocs = list.size();

		start = System.currentTimeMillis();
		System.out.println("Starting tokenization");
		for (WikipediaDocument doc : list) {
			
			tknizerMap = initMap(properties);
			pool.submit(new DocumentTransformer(tknizerMap, doc));
		}

		System.out.println("Submitted tokenization: "
				+ (System.currentTimeMillis() - start));

		IndexableDocument idoc;
		SharedDictionary docDict = new SharedDictionary(properties,
				INDEXFIELD.LINK);
		int currDocId;
		ThreadedIndexerRunner termRunner = new ThreadedIndexerRunner(properties);
		SingleIndexerRunner authIdxer = new SingleIndexerRunner(properties,
				INDEXFIELD.AUTHOR, INDEXFIELD.LINK, docDict, false);
		SingleIndexerRunner catIdxer = new SingleIndexerRunner(properties,
				INDEXFIELD.CATEGORY, INDEXFIELD.LINK, docDict, false);
		SingleIndexerRunner linkIdxer = new SingleIndexerRunner(properties,
				INDEXFIELD.LINK, INDEXFIELD.LINK, docDict, true);
		Map<String, Integer> tokenmap;

		System.out.println("Starting indexing.....");
		start = System.currentTimeMillis();
		double pctComplete = 0;
		for (int i = 0; i < numdocs; i++) {
			try {
				idoc = pool.take().get();
				if (idoc != null) {
					currDocId = docDict.lookup(idoc.getDocumentIdentifier());
					TokenStream stream;
					try {
						for (INDEXFIELD fld : INDEXFIELD.values()) {
							stream = idoc.getStream(fld);

							if (stream != null) {
								tokenmap = stream.getTokenMap();

								if (tokenmap != null) {
									switch (fld) {
									case TERM:
										termRunner.addToIndex(tokenmap,
												currDocId);
										break;
									case AUTHOR:
										authIdxer.processTokenMap(currDocId,
												tokenmap);
										break;
									case CATEGORY:
										catIdxer.processTokenMap(currDocId,
												tokenmap);
										break;
									case LINK:
										 linkIdxer.processTokenMap(
										 currDocId, tokenmap);
										break;
									}
								}
							}

						}
					} catch (IndexerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			pctComplete = (i * 100.0d) / numdocs;

			if (pctComplete % 10 == 0) {
				System.out.println(pctComplete + "% submission complete");
			}
		}

		System.out.println("Submitted all tasks in: "
				+ (System.currentTimeMillis() - start));

		try {
			termRunner.cleanup();
			authIdxer.cleanup();
			catIdxer.cleanup();
			linkIdxer.cleanup();
			docDict.writeToDisk();
			docDict.cleanUp();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Waiting for all tasks to complete");
	/*	while (termRunner.isFinished() && authIdxer.isFinished()
				&& catIdxer.isFinished() && linkIdxer.isFinished()) {
			// do nothing
			Thread.sleep(3000);
		}
*/
		while (!termRunner.isFinished() || !authIdxer.isFinished()
				|| !catIdxer.isFinished() || !linkIdxer.isFinished()) {
			System.out.println("TermRunner::"+termRunner.isFinished());
			System.out.println("authIdxer::"+termRunner.isFinished());
			System.out.println("catIdxer::"+termRunner.isFinished());
			System.out.println("linkIdxer::"+termRunner.isFinished());
			// do nothing
			Thread.sleep(3000);
		}

		
		System.out.println("Process complete: "
				+ (System.currentTimeMillis() - start));
		System.out.println("Process complete: "
				+ (System.currentTimeMillis() - start1));
		svc.shutdown();

		System.out.println("Shutdown completed");
		Thread.sleep(3000);
		IndexReader ir = new IndexReader(properties, INDEXFIELD.TERM);

		System.out.println("Get Top K::"+ir.getTopK(10));
		System.out.println("Total keyterms::TERM" + ir.getTotalKeyTerms());
		System.out.println("Total valueterms::TERM" + ir.getTotalValueTerms());

	/*	Map<String,Integer> postings = ir.getPostings("who");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("radio");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("from");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("Show");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("Cotton");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("Billy");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("Band");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("year");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("televis");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("after");
		System.out.println("Postings::"+postings+"::size::"+postings.size());

		System.out.println(ir.query("who", "radio", "from", "Show", "Cotton", "Billy", "Band", "year", "televis", "after"));
		*/
		Map<String,Integer> postings = ir.getPostings("from");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("ha");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("which");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("have");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("also");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("ar");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("on");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("been");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("us");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir.getPostings("after");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
			
		System.out.println(ir.query("from", "ha", "which", "have", "also", "ar", "on", "been", "us", "after"));
		/*System.out.println(ir.getPostings("cancer"));
		System.out.println(ir.getPostings("diagnos"));
		System.out.println(ir.getPostings("instrumental"));
		System.out.println(ir.getPostings("london"));
		System.out.println(ir.getPostings("merchant"));
		System.out.println(ir.getPostings("number"));
		System.out.println(ir.getPostings("occasion"));
		System.out.println(ir.getPostings("organizationchar"));
		System.out.println(ir.getPostings("Somebody"));
		System.out.println(ir.getPostings("series"));
		System.out.println(ir.getPostings("singer"));
		System.out.println(ir.getPostings("transfer"));
		System.out.println(ir.getPostings("without"));
		System.out.println(ir.getPostings("fighterbristol"));
		System.out.println(ir.getPostings("vaudeville"));
		System.out.println(ir.getPostings("vocalist"));*/
	/*	System.out.println(ir.getPostings("show"));
		System.out.println(ir.getPostings("radio"));
		System.out.println(ir.getPostings("from"));
		System.out.println(ir.getPostings("cotton"));
		System.out.println(ir.getPostings("billi"));
		System.out.println(ir.getPostings("band"));
		System.out.println(ir.getPostings("televis")); */

		//System.out.println(ir.query("show","radio","from","billi","band","televis"));
		
		IndexReader ir1 = new IndexReader(properties, INDEXFIELD.LINK);

		System.out.println("Get Link Posting");
		
		System.out.println("Top 10::"+ir1.getTopK(10));
		postings = ir1.getPostings("List of Internet phenomena");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Gangnam Style");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Philosophy");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Gangnam Style in popular culture");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Universe");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Index of philosophy of science articles");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		
		postings = ir1.getPostings("Timeline of scientific thought");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Relationship between religion and science");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("Philosophy of science");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir1.getPostings("2012 phenomenon");
		System.out.println("Postings::"+postings+"::size::"+postings.size()); 

//		System.out.println(ir1.query("List of Internet phenomena", "Gangnam Style", "Philosophy", "Gangnam Style in popular culture", "Universe", "Index of philosophy of science articles", "Timeline of scientific thought","Relationship between religion and science", "Philosophy of science", "2012 phenomenon"));
				
		/*System.out.println(ir1.getPostings("Russ Conway"));
		System.out.println(ir1.getPostings("Kathie Kay"));
		System.out.println(ir1.getPostings("Alan Breeze"));
		System.out.println(ir1.getPostings("Billy Cotton Band Show"));
		System.out.println(ir1.getPostings("Billy Cotton")); 
		
		System.out.println(ir1.query("Russ Conway", "Kathie Kay", "Alan Breeze", "Billy Cotton Band Show", "Billy Cotton")); */
		
		System.out.println("Total keyterms::LINK" + ir1.getTotalKeyTerms());
		System.out.println("Total valueterms::LINK" + ir1.getTotalValueTerms());

		IndexReader ir2 = new IndexReader(properties, INDEXFIELD.AUTHOR);

		System.out.println("Top 10::"+ir2.getTopK(10));
		postings = ir2.getPostings("Addbot");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("Bibcode Bot");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("Praemonitus");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("Deflective");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("Yobot");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("EmausBot");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		
		postings = ir2.getPostings("ClueBot NG");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("OKBot");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("ChrisGualtieri");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir2.getPostings("Trivialist");
		System.out.println("Postings::"+postings+"::size::"+postings.size()); 

		System.out.println(ir2.query("Addbot", "Bibcode Bot", "Praemonitus", "Deflective", "Yobot", "EmausBot", "ClueBot NG", "OKBot", "ChrisGualtieri", "Trivialist"));
	/*	System.out.println(ir2.getPostings("1exec1"));
		System.out.println(ir2.getPostings("2.96.87.226"));
		System.out.println(ir2.getPostings("Addbot"));
		System.out.println(ir2.getPostings("Johnpacklambert"));
		System.out.println(ir2.getPostings("Narrow Feint")); */ 

		System.out.println(ir2.query("Addbot", "1exec1", "Johnpacklambert", "Narrow Feint", "2.96.87.226"));
		
		System.out.println("Total keyterms::AUTHOR" + ir2.getTotalKeyTerms());
		System.out.println("Total valueterms::AUTHOR"
				+ ir2.getTotalValueTerms());

		IndexReader ir3 = new IndexReader(properties, INDEXFIELD.CATEGORY);

		System.out.println("Top 10 Categories::" + ir3.getTopK(20));
		
		postings = ir3.getPostings("Internet memes");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Asteroids named from Greek mythology");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Urban legends");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Philosophy of science");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Main Belt asteroids");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Jupiter Trojans (Greek camp)");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		
		postings = ir3.getPostings("Drinking culture");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Living people");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Jupiter Trojans (Trojan camp)");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("S-type asteroids");
		System.out.println("Postings::"+postings+"::size::"+postings.size());

		postings = ir3.getPostings("C-type asteroids");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Discoveries by Carolyn S. Shoemaker");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Astronomical objects discovered in 1973");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Viral videos");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Philosophy");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		
		postings = ir3.getPostings("Discoveries by Ingrid van Houten-Groeneveld");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Discoveries by Cornelis Johannes van Houten");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Discoveries by Tom Gehrels");
		System.out.println("Postings::"+postings+"::size::"+postings.size());
		postings = ir3.getPostings("Apollo asteroids");
		System.out.println("Postings::"+postings+"::size::"+postings.size());		
		
		
		System.out.println("Merged::"+ir3.query("Asteroids named from Greek mythology","Main Belt asteroids"));
		
	/*	System.out.println(ir3.getPostings("Billy Cotton Band Show"));
		System.out.println(ir3.getPostings("Wimbledon F.C. players"));
		System.out.println(ir3.getPostings("Scottish female singers"));
		System.out.println(ir3.getPostings("Royal Fusiliers soldiers"));
		System.out.println(ir3.getPostings("Royal Flying Corps officers"));
		System.out.println(ir3.getPostings("People from Lambeth"));
		System.out.println(ir3.getPostings("People from Glasgow")); 

		System.out.println(ir.query("Billy Cotton Band Show", "Wimbledon F.C. players", "Scottish female singers", "Royal Fusiliers soldiers", "Royal Flying Corps officers", "People from Lambeth", "People from Glasgow"));
		//[Billy Cotton Band Show, Wimbledon F.C. players, Scottish female singers, Royal Fusiliers soldiers, Royal Flying Corps officers, People from Lambeth, People from Glasgow, People from Bristol, Musicians from London, Music in Bristol, Grand Prix drivers, English songwriters, English racing drivers, English pianists, English male singers, English bandleaders, Deaths from Alzheimer's disease, Cub Records artists, British Army personnel of World War I, Brentford F.C. players]
		
		System.out.println("Total keyterms::CAT" + ir3.getTotalKeyTerms());
		System.out.println("Total valueterms::CAT" + ir3.getTotalValueTerms());
//*/
	}

	private static Map<INDEXFIELD, Tokenizer> initMap(Properties props) {
		HashMap<INDEXFIELD, Tokenizer> map = new HashMap<INDEXFIELD, Tokenizer>(
				INDEXFIELD.values().length);
		TokenizerFactory fact = TokenizerFactory.getInstance(props);
		for (INDEXFIELD fld : INDEXFIELD.values()) {
			map.put(fld, fact.getTokenizer(fld));
		}

		return map;
	}

	/**
	 * Method to print the correct usage to run this class.
	 */
	private static void printUsage() {
		System.err.println("The usage is: ");
		System.err
				.println("java edu.buffalo.cse.ir.wikiindexer.Runner <filename> <flag>");
		System.err.println("where - ");
		System.err
				.println("filename: Fully qualified file name from which to load the properties");
		System.err.println("flag: one amongst the following -- ");
		System.err.println("-t: Only execute tests");
		System.err.println("-i: Only run the indexer");
		System.err.println("-b: Run both, tests first then indexer");

	}

	/**
	 * Method to execute all tests
	 * 
	 * @param filename
	 *            : Filename for the properties file
	 */
	private static void runTests(String filename) {
		System.setProperty("PROPSFILENAME", filename);
		JUnitCore core = new JUnitCore();
		core.run(new Computer(), AllTests.class);

	}

	/**
	 * Method to load the Properties object from the given file name
	 * 
	 * @param filename
	 *            : The filename from which to load Properties
	 * @return The loaded object
	 */
	private static Properties loadProperties(String filename) {

		try {
			Properties props = FileUtil.loadProperties(filename);

			if (validateProps(props)) {
				return props;
			} else {
				System.err
						.println("Some properties were either not loaded or recognized. Please refer to the manual for more details");
				return null;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: "
					+ filename);
		} catch (IOException e) {
			System.err
					.println("Error while reading properties from the specified file: "
							+ filename);
		}

		return null;
	}

	/**
	 * Method to validate that the properties object has been correctly loaded
	 * 
	 * @param props
	 *            : The Properties object to validate
	 * @return true if valid, false otherwise
	 */
	private static boolean validateProps(Properties props) {
		/* Validate size */
		if (props != null
				&& props.entrySet().size() == IndexerConstants.NUM_PROPERTIES) {
			/* Get all required properties and ensure they have been set */
			Field[] flds = IndexerConstants.class.getDeclaredFields();
			boolean valid = true;
			Object key;

			for (Field f : flds) {
				if (f.isAnnotationPresent(RequiredConstant.class)) {
					try {
						key = f.get(null);
						if (!props.containsKey(key) || props.get(key) == null) {
							System.err.println("The required property "
									+ f.getName() + " is not set");
							valid = false;
						}
					} catch (IllegalArgumentException e) {

					} catch (IllegalAccessException e) {

					}
				}
			}

			return valid;
		}

		return false;
	}

}
