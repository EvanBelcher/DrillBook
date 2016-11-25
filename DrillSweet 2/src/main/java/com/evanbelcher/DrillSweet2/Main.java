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
 */
@SuppressWarnings("SynchronizeOnNonFinalField") public class Main {

	private static ConcurrentHashMap<Integer, Page> pages;
	private static State state;
	private static final String stateFileName = "STATE";
	private static GraphicsRunner graphicsRunner;

	private static Gson gson;

	/**
	 * Start stuff
	 */
	public static void main(String[] args) { //look i changed PLEASE WORK
		init();
		start();
	}

	/**
	 * Initializes the gson and filePath variables and loads the pages and state from the file
	 * system
	 */
	private static void init() {
		gson = new GsonBuilder().enableComplexMapKeySerialization()/*.setPrettyPrinting()*/.create();
		try {
			System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		} catch (Exception e) {
			e.printStackTrace();
		}
		load(true);
	}

	/**
	 * Initializes and starts the GraphicsRunner (Graphics Thread)
	 */
	private static void start() {
		graphicsRunner = new GraphicsRunner();
		graphicsRunner.setWindowTitle("DrillSweet 2 - " + getPagesFileName());
		new Thread(graphicsRunner, "GraphicsThread").start();
		new Thread(() -> {
			//noinspection InfiniteLoopStatement
			while (true) {
				try {
					Thread.sleep(120000);
					State.print("printing... " + System.currentTimeMillis());
					save("autosave.show.ds2", "autosave.state");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "AutosaveThread").start();
	}

	/**
	 * Loads the state and pages from the file system
	 */
	public static void load(boolean loadState) {
		if (new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\").mkdirs()) {
			state = new State(1, FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\", "show.ds2");
			saveState();
			pages = new ConcurrentHashMap<>();
			savePages();
		} else {
			if (loadState) {
				try {
					loadState();
				} catch (FileNotFoundException e) {
					State.print("State file not found: " + FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\" + stateFileName);
					state = new State(1, FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\", "show.ds2");
					saveState();
				}
			}
			try {
				loadPages();
			} catch (FileNotFoundException e) {
				State.print("Pages file not found: " + getFilePath() + getPagesFileName());
				pages = new ConcurrentHashMap<>();
				savePages();
			}
		}
		if (pages.isEmpty())
			pages.put(1, new Page(1));
		if (graphicsRunner != null)
			graphicsRunner.setWindowTitle("DrillSweet 2 - " + getPagesFileName());
	}

	/**
	 * Loads the state from the STATE file
	 *
	 * @throws FileNotFoundException if the STATE file cannot be found; this is handled by the
	 *                               load() method
	 */
	private static void loadState() throws FileNotFoundException {
		File f = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\" + stateFileName);
		BufferedReader br = new BufferedReader(new FileReader(f));
		state = gson.fromJson(br, State.class);
	}

	/**
	 * Loads the pages from the json file
	 *
	 * @throws FileNotFoundException if the json file cannot be found; this is handled by the load()
	 *                               method
	 */
	public static void loadPages() throws FileNotFoundException {
		File f = new File(getFilePath() + getPagesFileName());
		State.print("LOADING: " + f);
		BufferedReader br = new BufferedReader(new FileReader(f));
		Type type = new TypeToken<ConcurrentHashMap<Integer, Page>>() {

		}.getType();
		pages = gson.fromJson(br, type);
		State.print(pages);
	}

	/**
	 * Saves the pages and state to the json files
	 */
	public static void save(String... filenames) {
		if (filenames.length != 0 && filenames.length != 2)
			throw new IllegalArgumentException("filenames needs to either be the two filenames or nothing at all.");
		new File(getFilePath()).mkdirs();
		if (filenames.length == 0) {
			savePages();
			saveState();
		} else {
			savePages(filenames[0]);
			saveState(filenames[1]);
		}

	}

	/**
	 * Saves the pages in the json file
	 *
	 * @param filename the filename, if not pagesFileName
	 * @return the thread used to save the pages
	 */
	public static Thread savePages(String... filename) {
		if (filename.length != 0 && filename.length != 1)
			throw new IllegalArgumentException("You can only pass in one filename, or none at all.");
		Runnable r = () -> {
			String str = gson.toJson(pages);
			File f = new File(getFilePath() + (filename.length == 0 ? getPagesFileName() : filename[0]));
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
	 * @param filename the filename, if not stateFileName
	 */
	public static Thread saveState(String... filename) {
		if (filename.length != 0 && filename.length != 1)
			throw new IllegalArgumentException("You can only pass in one filename, or none at all.");
		Runnable r = () -> {
			synchronized (state) {
				String str = gson.toJson(state);
				File f = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DrillSweet2\\" + (filename.length == 0 ? stateFileName : filename[0]));
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
		Thread t = new Thread(r, "SaveStateThread");
		t.start();
		return t;
	}

	/**
	 * Adds a page with fields based on the current page
	 */
	public static void addPage() {
		Page page = getPages().get(getPages().size());
		pages.put(getPages().size() + 1, new Page(getPages().size() + 1, page.getSong(), page.getEndingMeasure() + 1, page.getDots()));
		setCurrentPage(pages.size());
	}

	/**
	 * Add the given page at the given index
	 *
	 * @param index the index for the page
	 * @param page  the page to be added
	 */
	@SuppressWarnings("unused") public static void addPage(int index, Page page) {
		pages.put(index, page);
	}

	/**
	 * Returns a copy of pages
	 */
	public static ConcurrentHashMap<Integer, Page> getPages() {
		return new ConcurrentHashMap<>(pages);
	}

	/**
	 * Returns the actual pages object
	 */
	public static ConcurrentHashMap<Integer, Page> getRealPages() {
		return pages;
	}

	/**
	 * The current page is determined by getting the page from pages corresponding to the
	 * currentPage stored in the State
	 *
	 * @return a copy of the current page
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
	 * Sets the current page number in the State object
	 *
	 * @param i the current page number
	 */
	public static void setCurrentPage(int i) {
		synchronized (state) {
			state.setCurrentPage(i);
		}
	}

	/**
	 * Returns the State object
	 */
	public static State getState() {
		return state;
	}

	/**
	 * Returns the file path. This maps to the DrillSweet2 folder in Documents by default
	 */
	public static String getFilePath() {
		synchronized (state) {
			return state.getFilePath();
		}
	}

	/**
	 * Sets the file path
	 */
	public static void setFilePath(String path) {
		synchronized (state) {
			state.setFilePath(path);
		}
	}

	/**
	 * Returns the file name for the pages json file, should include .ds2
	 */
	public static String getPagesFileName() {
		synchronized (state) {
			return state.getCurrentFileName();
		}
	}

	/**
	 * Sets the file name for the pages json file
	 *
	 * @param newPagesFileName the file name, should include .ds2
	 */
	public static void setPagesFileName(String newPagesFileName) {
		synchronized (state) {
			state.setCurrentFileName(newPagesFileName);
		}
	}

	/**
	 * Finds and returns the resource file of the given name
	 *
	 * @param file the name of the file
	 * @return requested file
	 */
	@SuppressWarnings("ConstantConditions") public static File getFile(String file) {
		ClassLoader classLoader = Main.class.getClassLoader();
		return new File(classLoader.getResource(file).getFile().replaceAll("%20", " "));
	}

}
