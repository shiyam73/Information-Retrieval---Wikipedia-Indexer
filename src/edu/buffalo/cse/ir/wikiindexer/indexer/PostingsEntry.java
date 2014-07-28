package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.Serializable;

public class PostingsEntry implements Serializable,Comparable<PostingsEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4391541312551612252L;
	private Integer docId;
	private Integer numOccurances;

	public PostingsEntry(Integer docId, Integer numOccurances) {
		this.docId = docId;
		this.numOccurances = numOccurances;
	}
	
	public PostingsEntry(String entry){
		String tuples[] = entry.trim().split("\\|\\|");
		this.docId = Integer.parseInt(tuples[0]);
		this.numOccurances = Integer.parseInt(tuples[1]);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(docId);
		sb.append("||");
		sb.append(numOccurances);
		return sb.toString();
	}

	public Integer getDocId(){
		return docId;
	}
	
	public Integer getNumOccurences(){
		return numOccurances;
	}
	
	@Override
	public int compareTo(PostingsEntry o) {
		if(o.docId > this.docId){
			return -1;
		}else if(o.docId < this.docId){
			return 1;
		}else{
			return -1;
		}
	}
}