package edu.buffalo.cse.ir.wikiindexer.indexer;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.Map.Entry;

public class FileRunner {

	private BufferedReader br = null;
	private InputStreamReader isr = null;
	private FileInputStream fis = null;
	private File f = null;
	private int n =0;
	public TreeMap<Integer,BufferedPostingsList> sortedMap = null;
	
	public FileRunner(File f,int n){
		this.f =f;
		try{
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis,"UTF-8");
			br = new BufferedReader(isr);
			this.n = n;
			}catch(Exception e){
				System.out.println("File not found::"+f.getAbsolutePath());
			}
			sortedMap = new TreeMap<Integer, BufferedPostingsList>();
	}
	
	public FileRunner(String path,int n){
		this(new File(path),n);
	}
	
	public boolean hasNext(){
		if(sortedMap != null && !(sortedMap.isEmpty())){
			return true;
		}else{
			System.out.println("Loading next set for file::"+f.getAbsolutePath());
			load();
			if(sortedMap != null && !(sortedMap.isEmpty())){
				return true;
			}
			return false;
		}
	}
	
	public Entry<Integer, BufferedPostingsList>  next(){	
			return (Entry<Integer, BufferedPostingsList>)sortedMap.firstEntry();
	}
	
	public void load(){
		
		String line;
		int count = 0;
		try{
		while((count < n) && ((line = br.readLine()) != null )){
			// Line is of format key = [ dicId||occ , docId||occ ];
			String tokens[] = line.split("=");
			
			String key = tokens[0];
			String value = tokens[1].trim().substring(1, tokens[1].length() -2);
			
			sortedMap.put(Integer.parseInt(key.trim()),new BufferedPostingsList(value));
			count ++;
		}
		}catch(Exception e){
		
		}	
	}
	
	public void exit(){
		try{
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			isr.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
