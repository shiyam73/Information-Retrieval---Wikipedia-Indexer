/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexWriter;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexerException;
import edu.buffalo.cse.ir.wikiindexer.indexer.NWayMerge;
import edu.buffalo.cse.ir.wikiindexer.indexer.Partitioner;

/**
 * @author nikhillo
 * 
 */
public class ThreadedIndexerRunner {
	private Properties props;
	private int docCount = 0;
	private RunnerThread[] rthreads;

	protected ThreadedIndexerRunner(Properties idxProps) {
		this.props = idxProps;
		int numParts = Partitioner.getNumPartitions();

		if (numParts > 0) {
			rthreads = new RunnerThread[numParts];
			for (int i = 0; i < numParts; i++) {
				rthreads[i] = new RunnerThread(i);
			}
		}
	}

	protected void addToIndex(Map<String, Integer> tokenmap, int docid) {
		String term;
		int numOccur, numPart;
		TermIndexEntry tidx;
		RunnerThread currThread;
		try {
			//System.out.println("Add to index called::" + docid + "tokenMap::" + tokenmap);
			
			
			for (Entry<String, Integer> etr : tokenmap.entrySet()) {
				term = etr.getKey();
				numOccur = etr.getValue();

				if (term != null && numOccur > 0) {
					numPart = Partitioner.getPartitionNumber(term);

					if (numPart >= 0 && numPart < rthreads.length) {
						tidx = new TermIndexEntry(term, docid, numOccur);
						currThread = rthreads[numPart];
						currThread.pvtQueue.add(tidx);
						if (!currThread.isRunning) {
							currThread.isRunning = true;
							new Thread(currThread).start();
						}
					}
				}
			}
			++docCount;
			if (docCount % 2000 == 0 && docCount > 1) {

				for (RunnerThread thr : rthreads) {
					while (!thr.isQueueEmpty()) {
						Thread.sleep(50);
					}
					thr.writer.writeToDisk();
					System.out.println("Writing to disk::" + docCount);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("Error Threaded index runner docId::" + docid);
		}
	}

	protected void cleanup() {
	//	this.mergePartionedIndexes();

		for (RunnerThread thr : rthreads) {
			thr.setComplete();
		}
	}

	protected void mergePartionedIndexes() {
		
	/*	System.out.println("Merging into one file");
		File dir = new File(props.getProperty("tmp.dir") + File.separator + "index"
				+ File.separator + "merged" + INDEXFIELD.TERM + "to"
				+ INDEXFIELD.LINK);
		
		File finalDir = new File(props.getProperty("tmp.dir") + File.separator
					+ "index" + File.separator + INDEXFIELD.TERM + "to"
					+ INDEXFIELD.LINK);
		finalDir.mkdirs();
		if (dir.exists() && dir.isDirectory()) {
			System.out.println("Merging into one file " + dir.listFiles().toString());
			NWayMerge merger = new NWayMerge(dir.listFiles(),
					props.getProperty("tmp.dir") + File.separator
					+ "index" + File.separator + INDEXFIELD.TERM + "to"
					+ INDEXFIELD.LINK, INDEXFIELD.TERM + "to"
							+ INDEXFIELD.LINK);
			merger.initializeMerge();
			merger.merge(); 
		} */
	}

	protected boolean isFinished() {
		boolean flag = true;

		for (RunnerThread thr : rthreads) {
			flag &= (thr.isComplete && thr.isQueueEmpty() && thr.writer.isComplete);
		}

		return flag;
	}

	private class TermIndexEntry {
		private String term;
		private int docId;
		private int numOccurances;

		private TermIndexEntry(String ipTerm, int ipDocId, int ipNumOccur) {
			term = ipTerm;
			docId = ipDocId;
			numOccurances = ipNumOccur;
		}
	}

	private class RunnerThread implements Runnable {
		private ConcurrentLinkedQueue<TermIndexEntry> pvtQueue;
		private IndexWriter writer;
		private boolean isComplete;
		private boolean isRunning;

		private RunnerThread(int pnum) {
			pvtQueue = new ConcurrentLinkedQueue<ThreadedIndexerRunner.TermIndexEntry>();
			writer = new IndexWriter(props, INDEXFIELD.TERM, INDEXFIELD.LINK);
			writer.setPartitionNumber(pnum);
		}

		private void setComplete() {
			System.out.println("Threaded Indexed complete!!!");
			isComplete = true;
		}

		private boolean isQueueEmpty() {
			synchronized (pvtQueue) {
				return pvtQueue.isEmpty();
			}
		}

		public void run() {
			TermIndexEntry etr;

			while (true) {
				etr = pvtQueue.poll();
				// System.out.println("Entry::"+etr.term+"Count::"+etr.numOccurances);

				if (etr == null) {
					if (isComplete) {
						try {
							System.out
									.println("Writing to disk for one last time!!!!!");
							writer.writeDict();
							writer.writeToDisk();
							System.out
									.println("Write to disk completed for TIR!!");
						} catch (IndexerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						writer.cleanUp();
						break; // everything is done
					} else {
						try {
							Thread.sleep(2000); // 2 seconds -- config maybe?
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					// we have an entry
					try {
						writer.addToIndex(etr.term, etr.docId,
								etr.numOccurances);
					} catch (IndexerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
