package main.java.com.evanbelcher.DrillSweet2.data;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * FileFilter that allows directories and json files
 */
public class DS2FileFilter extends FileFilter {

	/**
	 * Accept *.ds2 files and folders only
	 *
	 * @param f the File to evaluate
	 * @return if the file is accepted
	 */
	@Override public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(".ds2");
	}

	/**
	 * The description of the type of file
	 */
	@Override public String getDescription() {
		return "DrillSweet 2 File";
	}
}
