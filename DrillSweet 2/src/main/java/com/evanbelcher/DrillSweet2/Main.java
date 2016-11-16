
package main.java.com.evanbelcher.DrillSweet2;

import java.io.*;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.filechooser.FileSystemView;
import main.java.com.evanbelcher.DrillSweet2.data.*;
import main.java.com.evanbelcher.DrillSweet2.display.GraphicsRunner;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * Driver class
 * Handles keeping everything in memory
 * 
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class Main {
	
	private static ConcurrentHashMap<Integer, Page> pages;
	private static State state;
	private static String filePath;
	private static String pagesFileName = "pages.json";
	private static final String stateFileName = "STATE";
	private static GraphicsRunner graphicsRunner;
	
	private static Gson gson;
	
	/**
	 * Start stuff
	 * 
	 * @since 1.0
	 */
	public static void main(String[] args) {
		init();
		start();
	}
	
	/**
	 * Initializes the gson and filePath variables and loads the pages and state from the file
	 * system
	 * 
	 * @since 1.0
	 */
	private static void init() {
		gson = new GsonBuilder().enableComplexMapKeySerialization()/*.setPrettyPrinting()*/.create();
		filePath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\";
		load();
	}
	
	/**
	 * Initializes and starts the GraphicsRunner (Graphics Thread)
	 * 
	 * @since 1.0
	 */
	private static void start() {
		graphicsRunner = new GraphicsRunner();
		graphicsRunner.setWindowTitle("DrillSweet 2 - " + pagesFileName);
		new Thread(graphicsRunner, "GraphicsThread").start();
	}
	
	/**
	 * Loads the state and pages from the file system
	 * 
	 * @since 1.0
	 */
	public static void load() {
		if (new File(filePath).mkdirs()) {
			state = new State(1);
			saveState();
			pages = new ConcurrentHashMap<>();
			savePages();
		} else {
			try {
				boolean b = state == null;
				loadState();
				State.print(state);
				if (b)
					pagesFileName = state.getCurrentFileName();
			} catch (FileNotFoundException e) {
				System.out.println("State file not found: " + filePath + stateFileName);
				state = new State(1);
				saveState();
			}
			try {
				loadPages();
			} catch (FileNotFoundException e) {
				System.out.println("Pages file not found: " + filePath + pagesFileName);
				pages = new ConcurrentHashMap<>();
				savePages();
			}
		}
		if (pages.isEmpty())
			pages.put(1, new Page(1));
		if (graphicsRunner != null)
			graphicsRunner.setWindowTitle("DrillSweet 2 - " + pagesFileName);
	}
	
	/**
	 * Loads the state from the STATE file
	 * 
	 * @throws FileNotFoundException
	 *             if the STATE file cannot be found; this is handled by the load() method
	 * @since 1.0
	 */
	private static void loadState() throws FileNotFoundException {
		File f = new File(filePath + stateFileName);
		BufferedReader br = new BufferedReader(new FileReader(f));
		state = gson.fromJson(br, new State().getClass());
	}
	
	/**
	 * Loads the pages from the json file
	 * 
	 * @throws FileNotFoundException
	 *             if the json file cannot be found; this is handled by the load() method
	 * @since 1.0
	 */
	private static void loadPages() throws FileNotFoundException {
		File f = new File(filePath + pagesFileName);
		BufferedReader br = new BufferedReader(new FileReader(f));
		Type type = new TypeToken<ConcurrentHashMap<Integer, Page>>() {
		}.getType();
		pages = gson.fromJson(br, type);
		State.print(pages);
	}
	
	/**
	 * Saves the pages and state to the json files
	 * 
	 * @since 1.0
	 */
	public static void save() {
		new File(filePath).mkdirs();
		savePages();
		saveState();
	}
	
	/**
	 * Saves the pages in the json file
	 * 
	 * @return the thread used to save the pages
	 * @since 1.0
	 */
	public static Thread savePages() {
		Runnable r = () -> {
			String str = gson.toJson(pages);
			File f = new File(getFilePath() + getPagesFileName());
			state.setCurrentFileName(getPagesFileName());
			State.print("Saving:" + getFilePath() + getPagesFileName());
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter(f));
				bw.write(str);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		Thread thread = new Thread(r, "SavePagesThread");
		thread.start();
		return thread;
	}
	
	/**
	 * Saves the state in the STATE file in json format
	 * 
	 * @since 1.0
	 */
	private static void saveState() {
		Runnable r = () -> {
			synchronized (state) {
				String str = gson.toJson(state);
				File f = new File(filePath + stateFileName);
				BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(f));
					bw.write(str);
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r, "SaveStateThread").start();
	}
	
	/**
	 * Adds a page with fields based on the current page
	 * 
	 * @since 1.0
	 */
	public static void addPage() {
		Page page = getPages().get(getPages().size());
		pages.put(getPages().size() + 1, new Page(getPages().size() + 1, page.getSong(), page.getEndingMeasure() + 1, page.getDots()));
		setCurrentPage(pages.size());
	}
	
	/**
	 * Add the given page at the given index
	 * 
	 * @param index
	 *            the index for the page
	 * @param page
	 *            the page to be added
	 * @since 1.0
	 */
	public static void addPage(int index, Page page) {
		pages.put(index, page);
	}
	
	/**
	 * @return a copy of pages
	 * @since 1.0
	 */
	public static ConcurrentHashMap<Integer, Page> getPages() {
		return new ConcurrentHashMap<Integer, Page>(pages);
	}
	
	/**
	 * @return the actual pages object
	 * @since 1.0
	 */
	public static ConcurrentHashMap<Integer, Page> getRealPages() {
		return pages;
	}
	
	/**
	 * The current page is determined by getting the page from pages corresponding to the
	 * currentPage stored in the State
	 * 
	 * @return a copy of the current page
	 * @since 1.0
	 */
	public static Page getCurrentPage() {
		int currentPage;
		synchronized (state) {
			currentPage = state.getCurrentPage();
			if (getPages().containsKey(currentPage))
				return getPages().get(currentPage);
			state.setCurrentPage(1);
			return getPages().get(1);
		}
	}
	
	/**
	 * The current page is determined by getting the page from pages corresponding to the
	 * currentPage stored in the State
	 * 
	 * @return the actual current page object
	 * @since 1.0
	 * @since 1.0
	 */
	public static Page getRealCurrentPage() {
		synchronized (state) {
			return pages.get(state.getCurrentPage());
		}
	}
	
	/**
	 * Sets the current page number in the State object
	 * 
	 * @param i
	 *            the current page number
	 * @since 1.0
	 */
	public static void setCurrentPage(int i) {
		synchronized (state) {
			state.setCurrentPage(i);
		}
	}
	
	/**
	 * Returns the State object
	 * 
	 * @return the State
	 * @since 1.0
	 */
	public static State getState() {
		return state;
	}
	
	/**
	 * Returns the file path. This maps to the DrillSweet2 folder in Documents
	 * 
	 * @return filePath
	 * @since 1.0
	 */
	public static String getFilePath() {
		return filePath;
	}
	
	/**
	 * Returns the file name for the pages json file, should include .json
	 * 
	 * @return pagesFileName
	 * @since 1.0
	 */
	public static String getPagesFileName() {
		return pagesFileName;
	}
	
	/**
	 * Sets the file name for the pages json file
	 * 
	 * @param pagesFileName
	 *            the file name, should include .json
	 * @since 1.0
	 */
	public static void setPagesFileName(String pagesFileName) {
		Main.pagesFileName = pagesFileName;
	}
	
	public static File getFile(String file) {
		ClassLoader classLoader = Main.class.getClassLoader();
		return new File(classLoader.getResource(file).getFile().replaceAll("%20", " "));
	}
	
}
