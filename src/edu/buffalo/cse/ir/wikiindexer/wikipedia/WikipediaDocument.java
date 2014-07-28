/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nikhillo This class acts as a container for a single wikipedia page.
 *         It has a structure analogous to how a page looks in a browser The
 *         class has various getters and setters for the different fields You
 *         would call the setters from within your parser code to populate the
 *         fields as you parse them.
 * 
 *         The getters would be used by your indexer(s) as and when you
 *         implement them
 * 
 */
public class WikipediaDocument implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6983166167626238934L;

	/* This is the timestamp */
	private Date publishDate;

	/* Contributor: username or ip */
	private String author;

	/* Page id, not revision nor parent id */
	private long id;

	/* Page title */
	private String title;

	/* Look at the Section class below. Every page is a collection of sections */
	private List<Section> sections;

	/* This is a set of all links referenced by this page */
	private Set<String> links;

	/* This is a list of all categories of the page */
	private List<String> categories;

	/*
	 * A map representation of all language links. The key is the language code,
	 * value is the url
	 */
	private Map<String, String> langLinks;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	private boolean createDir = false;
	private boolean createLinkFile = false;
	private File linkFile = null;
	private boolean createCategoryFile = false;
	private File categoryFile = null;
	private boolean createSectionFile = false;
	private File sectionFile = null;

	private File dir = null;

	/**
	 * Default constructor.
	 * 
	 * @param idFromXml
	 *            : The parsed id from the xml
	 * @param timestampFromXml
	 *            : The parsed timestamp from the xml
	 * @param authorFromXml
	 *            : The parsed author from the xml
	 * @param ttl
	 *            : The title of the page
	 * @throws ParseException
	 *             If the timestamp isn't in the expected format
	 */
	public WikipediaDocument(long id, String timestampFromXml,
			String authorFromXml, String ttl) throws ParseException {
		this.id = id;
		System.out.println("<title>" + ttl + "</title>");
		this.publishDate = (timestampFromXml == null) ? null : sdf
				.parse(timestampFromXml.trim());
		this.author = (authorFromXml == null) ? null : authorFromXml;
		this.title = (ttl == null) ? null : ttl;
		sections = new ArrayList<WikipediaDocument.Section>();
		links = new HashSet<String>();
		categories = new ArrayList<String>();
		langLinks = new HashMap<String, String>();
	}

	/**
	 * Method to add a section to the given document
	 * 
	 * @param title
	 *            : The parsed title of the section
	 * @param text
	 *            : The parsed text of the section
	 */
	protected void addSection(String title, String text) {
		sections.add(new Section(title, text));
	}

	/**
	 * Method to add a link to the set of links referenced by this document
	 * 
	 * @param link
	 *            : The page name for the link
	 */
	protected void addLink(String link) {
		links.add(link);
	}

	/**
	 * Method to bulk add links to the set of links referenced by this document
	 * 
	 * @param links
	 *            : The collection of links to be added, each referenced by the
	 *            page name
	 */
	protected void addLInks(Collection<String> links) {
		this.links.addAll(links);
	}

	/**
	 * Method to add a category to the list of categories that classify this
	 * document
	 * 
	 * @param category
	 *            : The category to be added
	 */
	protected void addCategory(String category) {
		categories.add(category);
	}

	/**
	 * Method to bulk add categories to the list of categories classifying this
	 * document
	 * 
	 * @param categories
	 *            : The collection of categories to be added
	 */
	protected void addCategories(Collection<String> categories) {
		this.categories.addAll(categories);
	}

	/**
	 * Method to add a given language to link mapping to the list of language
	 * mappings for this document
	 * 
	 * @param langCode
	 *            : The language code that references the link
	 * @param langLink
	 *            : The link to be added
	 */
	protected void addLangLink(String langCode, String langLink) {
		langLinks.put(langCode, langLink);
	}

	/**
	 * Method to bulk add language links to the list of mappings for this
	 * document
	 * 
	 * @param links
	 *            : The map containing the mappings to be added
	 */
	protected void addLangLinks(Map<String, String> links) {
		langLinks.putAll(links);
	}

	/**
	 * @return the publishDate
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the sections
	 */
	public List<Section> getSections() {
		if (createSectionFile) {
			loadSections();
		}
		return sections;
	}

	/**
	 * @return the links
	 */
	public Set<String> getLinks() {
		if (createLinkFile) {
			loadLinks();
		}
		return links;
	}

	/**
	 * @return the categories
	 */
	public List<String> getCategories() {
		if (createCategoryFile) {
			loadCategories();
		}
		return categories;
	}

	/**
	 * @return the langLinks
	 */
	public Map<String, String> getLangLinks() {
		return langLinks;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TITLE::" + this.title + "\n");
		sb.append("contributor::" + this.author + "\n");
		sb.append("ID::" + this.id + "\n");
		sb.append("Links::" + this.links + "\n");
		sb.append("Sections::" + this.sections.toString() + "\n");
		sb.append("Categories::" + this.categories + "\n");
		sb.append("PublishDate::" + this.publishDate + "\n");
		sb.append("LangLinks::" + this.langLinks + "\n");

		return sb.toString();
	}

	protected void cleanup() {
		try{
		if (dir != null && dir.exists()) {
			if (sectionFile != null && sectionFile.exists()) {
				sectionFile.delete();
			}
			if (linkFile != null && linkFile.exists()) {
				linkFile.delete();
			}
			if (categoryFile != null && categoryFile.exists()) {
				categoryFile.delete();
			}
			dir.delete();
		}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}

	protected void writeToFile() {
		// Create a folder
		try {
			dir = new File("temp" + File.separator
					+ Long.toString(this.id));
			createDir = dir.mkdirs();
			System.out.println(dir.getAbsolutePath());
			if (createDir || dir.exists()) {
				linkFile = new File(dir.getAbsolutePath() + File.separator
						+ "links.txt");
				if (!(linkFile.exists())) {
					linkFile.createNewFile();
				}
				if (linkFile.exists()) {

					System.out.println("Link File created");
					FileOutputStream linkFout = null;
					ObjectOutputStream oos = null;
					try {
						linkFout = new FileOutputStream(linkFile);
						oos = new ObjectOutputStream(linkFout);
						oos.writeObject(links);
						links = null;
						createLinkFile = true;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							linkFout.close();
							oos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				categoryFile = new File(dir.getAbsolutePath() + File.separator
						+ "categories.txt");
				if (!(categoryFile.exists())) {
					categoryFile.createNewFile();
				}
				if (categoryFile.exists()) {

					System.out.println("Category file created");
					FileOutputStream categoryFout = null;
					ObjectOutputStream oos = null;
					try {
						categoryFout = new FileOutputStream(categoryFile);
						oos = new ObjectOutputStream(categoryFout);
						oos.writeObject(categories);
						categories = null;
						createCategoryFile = true;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							categoryFout.close();
							oos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				sectionFile = new File(dir.getAbsolutePath() + File.separator
						+ "sections.txt");
				if (!(sectionFile.exists())) {
					sectionFile.createNewFile();
				}
				if (sectionFile.exists()) {

					System.out.println("Section file created");
					FileOutputStream sectionOut = null;
					ObjectOutputStream oos = null;
					try {
						sectionOut = new FileOutputStream(sectionFile);
						oos = new ObjectOutputStream(sectionOut);
						oos.writeObject(sections);
						sections = null;
						createSectionFile = true;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							sectionOut.close();
							oos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
			// ((Section) sections).writeToFile(dir.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Create files for links
		// Create file for Categories
		// Create file for each sections
	}

	protected void loadLinks() {
		if (linkFile != null && linkFile.exists()) {
			// Load data to links
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(linkFile);
				ois = new ObjectInputStream(fis);
				links = (HashSet<String>) ois.readObject();
				// Load date to text
				createLinkFile = false;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void loadCategories() {
		if (categoryFile != null && categoryFile.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(categoryFile);
				ois = new ObjectInputStream(fis);
				categories = (ArrayList<String>) ois.readObject();
				// Load date to text
				createCategoryFile = false;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void loadSections() {
		if (sectionFile != null && sectionFile.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(sectionFile);
				ois = new ObjectInputStream(fis);
				sections = (List<Section>) ois.readObject();
				// Load date to text
				createSectionFile = false;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Class to mimic a section of a page
	 */
	public class Section implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String title;
		private String text;
		private File sectionFile;
		private boolean sectionFileFlag = false;

		/**
		 * Default constructor. Please do not change visibility of the method.
		 * 
		 * @param parsedTitle
		 *            : The parsed section title
		 * @param parsedText
		 *            : The parsed section text
		 */
		private Section(String parsedTitle, String parsedText) {
			this.title = parsedTitle;
			this.text = parsedText;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			if (sectionFileFlag) {
				loadFromFile();
			}
			return text;
		}

		public String toString() {
			return ("Section Title::" + this.title + "\n" + "Section Content::"
					+ this.text + "\n");
		}

		public void writeToFile(String path) {

			sectionFile = new File(path + File.separator + this.title);
			if (sectionFile.exists()) {
				FileOutputStream secFout = null;
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(secFout);
					oos.writeObject(text);
					text = null;
					sectionFileFlag = true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						secFout.close();
						oos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void writeObject(ObjectOutputStream outputStream) {
			try {
				outputStream.defaultWriteObject();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void loadFromFile() {
			if (sectionFile != null && sectionFile.exists()) {
				FileInputStream fis = null;
				ObjectInputStream ois = null;
				try {
					fis = new FileInputStream(sectionFile);
					ois = new ObjectInputStream(fis);
					text = (String) ois.readObject();
					// Load date to text
					sectionFileFlag = false;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fis.close();
						ois.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
