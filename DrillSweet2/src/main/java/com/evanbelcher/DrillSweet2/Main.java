package com.evanbelcher.DrillSweet2;

import com.evanbelcher.DrillSweet2.data.*;
import com.evanbelcher.DrillSweet2.display.GraphicsRunner;
import com.google.gson.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Driver class
 * Handles keeping everything in memory
 *
 * @author Evan Belcher
 */
@SuppressWarnings("SynchronizeOnNonFinalField") public class Main {

	private static final String stateFileName = "STATE";
	private static PagesConcurrentHashMap pageMap;
	private static State state;
	private static GraphicsRunner graphicsRunner;
	private static final String[] versions = { "1.0.0", "1.1.0", "1.2.0", "1.2.1", "1.3.0", "1.4.0", "1.4.1", "1.5.0" };
	private static Gson gson;

	/**
	 * Start stuff
	 */
	public static void main(String[] args) {
		deleteOldVersions();
		init();
		start();
	}

	/**
	 * Deletes all old DrillSweet 2 exe's
	 */
	private static void deleteOldVersions() {
		File dir = Paths.get(".").toAbsolutePath().normalize().toFile();
		File[] files = dir.listFiles();
		if (files != null)
			for (File f : files)
				for (int i = 0; i < versions.length - 1; i++)
					if (f.getName().equals("DrillSweet.2.v" + versions[i] + ".exe"))
						f.delete();
	}

	/**
	 * Initializes the gson and filePath variables and loads the pageMap and state from the file
	 * system
	 */
	private static void init() {
		gson = new GsonBuilder().enableComplexMapKeySerialization().setLenient().setPrettyPrinting().create();
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
		setUIFont(new FontUIResource("Dialog", Font.BOLD, Main.getState().getSettings().getFontSize()));
		graphicsRunner = new GraphicsRunner();
		graphicsRunner.setWindowTitle("DrillSweet 2 - " + getPagesFileName());
		new Thread(graphicsRunner, "GraphicsThread").start();
		//new Thread(new GraphicsRunner(), "GraphicsThread2").start();
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
	 * Loads the state and pageMap from the file system
	 */
	public static void load(boolean loadState) {
		if (new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/").mkdirs()) {
			state = new State(1, FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/", "show.ds2");
			saveState();
			pageMap = new PagesConcurrentHashMap();
			savePages();
		} else {
			if (loadState) {
				try {
					loadState();
				} catch (FileNotFoundException e) {
					State.print("State file not found: " + FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/" + stateFileName);
					state = new State(1, FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/", "show.ds2");
					saveState();
				}
			}
			try {
				loadPages();
			} catch (FileNotFoundException e) {
				State.print("Pages file not found: " + getFilePath() + getPagesFileName());
				pageMap = new PagesConcurrentHashMap();
				savePages();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (pageMap.getPages().isEmpty())
			pageMap.getPages().put(1, new Page(1));
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
		File f = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/" + stateFileName);
		BufferedReader br = new BufferedReader(new FileReader(f));
		state = gson.fromJson(br, State.class);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the pageMap from the json file
	 *
	 * @throws FileNotFoundException if the json file cannot be found; this is handled by the load()
	 *                               method
	 */
	public static void loadPages() throws IOException {
		File f = new File(getFilePath() + getPagesFileName());
		State.print("LOADING: " + f);
		BufferedReader br = new BufferedReader(new FileReader(f));
		//		Type type = new TypeToken<PagesConcurrentHashMap>() {
		//
		//		}.getType();
		pageMap = gson.fromJson(br, PagesConcurrentHashMap.class);
		State.print(pageMap);
		br.close();
	}

	/**
	 * Saves the pageMap and state to the json files
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
	 * Saves the pageMap in the json file
	 *
	 * @param filename the filename, if not pagesFileName
	 * @return the thread used to save the pageMap
	 */
	public static Thread savePages(String... filename) {
		if (filename.length != 0 && filename.length != 1)
			throw new IllegalArgumentException("You can only pass in one filename, or none at all.");
		Runnable r = () -> {
			String easyFileName = (filename.length == 0 ? getPagesFileName() : filename[0]);
			easyFileName = easyFileName.substring(0, easyFileName.indexOf('.'));

			while (new File(easyFileName + ".lock").exists()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (!new File(easyFileName + ".lock").exists()) {
				File lock = new File(easyFileName + ".lock");
				try {
					lock.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				String str = gson.toJson(pageMap);
				File f = new File(getFilePath() + (filename.length == 0 ? getPagesFileName() : filename[0]));
				State.print("Saving:" + getFilePath() + (filename.length == 0 ? getPagesFileName() : filename[0]));
				BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(f));
					bw.write(str);
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				lock.delete();
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
				String easyFileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/" + (filename.length == 0 ? stateFileName : filename[0]);
				if (easyFileName.indexOf('.') != -1)
					easyFileName = easyFileName.substring(0, easyFileName.indexOf('.'));

				while (new File(easyFileName + ".lock").exists()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (!new File(easyFileName + ".lock").exists()) {
					File lock = new File(easyFileName + ".lock");
					try {
						lock.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String str = gson.toJson(state);
					File f = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DrillSweet2/" + (filename.length == 0 ? stateFileName : filename[0]));
					BufferedWriter bw;
					try {
						bw = new BufferedWriter(new FileWriter(f));
						bw.write(str);
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					lock.delete();
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
		pageMap.getPages().put(getPages().size() + 1, new Page(getPages().size() + 1, page.getSong(), page.getEndingMeasure() + 1, page.getDots()));
		setCurrentPage(pageMap.getPages().size());
	}

	/**
	 * Add the given page at the given index
	 *
	 * @param index the index for the page
	 * @param page  the page to be added
	 */
	@SuppressWarnings("unused") public static void addPage(int index, Page page) {
		pageMap.getPages().put(index, page);
	}

	/**
	 * Returns a copy of pages
	 */
	public static ConcurrentHashMap<Integer, Page> getPages() {
		return new ConcurrentHashMap<>(pageMap.getPages());
	}

	/**
	 * Returns the actual pages object
	 */
	public static ConcurrentHashMap<Integer, Page> getRealPages() {
		return pageMap.getPages();
	}

	/**
	 * Returns the PagesConcurrentHashMap
	 */
	public static PagesConcurrentHashMap getPageMap() {
		return pageMap;
	}

	/**
	 * The current page is determined by getting the page from pageMap corresponding to the
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
	 * Returns the file name for the pageMap json file, should include .ds2
	 */
	public static String getPagesFileName() {
		synchronized (state) {
			return state.getCurrentFileName();
		}
	}

	/**
	 * Sets the file name for the pageMap json file
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
	public static InputStream getFile(String file, Object o) {
		return o.getClass().getResourceAsStream("/" + file);
	}

	/**
	 * Sets the default font for all default components to be the given font
	 *
	 * @param f the new default font
	 */
	public static void setUIFont(FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof FontUIResource)
				UIManager.put(key, f);
		}
	}

}
