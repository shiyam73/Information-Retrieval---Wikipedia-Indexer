package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class NWayMerge {

	private File[] fileList = null;
	private File[] completedFileList = null;
	private File directory = null;
	private String mergedName = null;
	private TreeMap<Integer, LinkedList<Integer>> sortedMap = null;
	private TreeMap<Integer, BufferedPostingsList> outputMap = null;
	private FileRunner[] fileRunners;
	private FileOutputStream fos = null;
	private OutputStreamWriter osw = null;

	public NWayMerge(File[] fileList, String dir, String mergedName) {
		this.fileList = fileList;
		this.directory = new File(dir);
		this.mergedName = mergedName;
		fileRunners = new FileRunner[fileList.length];
		sortedMap = new TreeMap<Integer, LinkedList<Integer>>();
		outputMap = new TreeMap<Integer, BufferedPostingsList>();
	}

	public void initializeMerge() {

		try {

			boolean createDir = directory.mkdirs();
			System.out.println("Create dir is ::" + createDir);
			int n = 50000 / fileList.length;
			System.out.println("Length ::" + fileList.length);
			for (int i = 0; i < fileList.length; i++) {
				System.out.println("Loading file :: " + fileList[i].getName());
				this.fileRunners[i] = new FileRunner(fileList[i], n);
				fileRunners[i].load();
				Entry<Integer, BufferedPostingsList> e = null;
				if (fileRunners[i].hasNext()) {
					e = fileRunners[i].next();
				} else {
					continue;
				}
				if (sortedMap.containsKey(e.getKey())) {
					LinkedList<Integer> fileId = (LinkedList<Integer>) sortedMap
							.get(e.getKey());
					fileId.add(i);
				} else {
					LinkedList<Integer> fileId = new LinkedList<Integer>();
					fileId.add(i);
					sortedMap.put(e.getKey(), fileId);
				}
				System.out.println(sortedMap.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void merge() {

		int i = 0;

		try {
			if (directory.exists()) {
				File newFile = new File(directory.getAbsolutePath()
						+ File.separator + mergedName);
				fos = new FileOutputStream(newFile);
				osw = new OutputStreamWriter(fos, "UTF-8");

				while (!sortedMap.isEmpty()) {
					Entry<Integer, LinkedList<Integer>> firstEntry = (Entry<Integer, LinkedList<Integer>>) sortedMap
							.pollFirstEntry();
					Integer key = firstEntry.getKey();
					LinkedList<Integer> value = firstEntry.getValue();

					Iterator<Integer> it = value.iterator();

					BufferedPostingsList mergedPostingList = null;

					while (it.hasNext()) {

						i = it.next();
						Entry<Integer, BufferedPostingsList> e = fileRunners[i].sortedMap
								.pollFirstEntry();

						if (mergedPostingList != null) {
							mergedPostingList.append(e.getValue());
						} else {
							mergedPostingList = e.getValue();
						}

						Entry<Integer, BufferedPostingsList> newEntry = null;

						if (fileRunners[i].hasNext()) {
							newEntry = fileRunners[i].next();
						} else {
							System.out.println("File ended");
							continue;
						}

						if (sortedMap.containsKey(newEntry.getKey())) {
							LinkedList<Integer> fileId = (LinkedList<Integer>) sortedMap
									.get(newEntry.getKey());
							fileId.add(i);
						} else {
							LinkedList<Integer> fileId = new LinkedList<Integer>();
							fileId.add(i);
							sortedMap.put(newEntry.getKey(), fileId);
						}
					}
					outputMap.put(key, mergedPostingList);

					i++;

					if (i > 0 && i % 5000 == 0) {
						writeToDisk();
						outputMap.clear();
					}
				}

				writeToDisk();
				outputMap.clear();
				cleanup();
			}
		} catch (Exception e) {
			// System.out.println(outputMap);
			System.out.println(i);
			System.out.println(fileRunners[i].sortedMap);
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				osw.close();
			} catch (Exception e) {

			}
		}
	}

	public void writeToDisk() {
		int i = 0;
		try {
			for (Entry<Integer, BufferedPostingsList> entry : outputMap
					.entrySet()) {
				StringBuilder sb = new StringBuilder(entry.getKey().toString());
				sb.append(" = ");
				sb.append(entry.getValue().toString());
				sb.append("\n");
				osw.write(sb.toString());
				i++;
				if (i % 200 == 0 && i > 1) {
					osw.flush();
				}
			}

			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cleanup(){
		System.out.println("Cleanup called");
		for(int i=0;i<fileList.length;i++){
			System.out.println(fileList[i].getAbsolutePath());
			fileRunners[i].exit();
			if(fileList[i].delete()){
    			System.out.println(fileList[i].getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
		}
	}
	
	
	public static void main(String args[]) {
		System.out.println("Merge files");
		File dir = new File("E:" + File.separator + "Fall 2013"
				+ File.separator + "IR" + File.separator + "temp"
				+ File.separator + "index" + File.separator + "TERM" + "to"
				+ "LINK" + File.separator + "1");
		if (dir.exists() && dir.isDirectory()) {

			File[] fileList = dir.listFiles();
			File mergedDir = new File("E:" + File.separator + "Fall 2013"
					+ File.separator + "IR" + File.separator + "temp"
					+ File.separator + "index" + File.separator + "MergedDir"
					+ File.separator + "1");
			mergedDir.mkdirs();
			if (mergedDir.exists()) {
				NWayMerge nM = new NWayMerge(fileList,
						mergedDir.getAbsolutePath(), "Merged.txt");

				nM.initializeMerge();
				nM.merge();
			}
		}

	}

}
