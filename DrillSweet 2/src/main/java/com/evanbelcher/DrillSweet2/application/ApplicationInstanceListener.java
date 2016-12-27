package com.evanbelcher.DrillSweet2.application;

/**
 * Listens for when a subsequent application is called.
 * http://www.rbgrn.net/content/43-java-single-application-instance
 *
 * @author Robert Green
 */
public interface ApplicationInstanceListener {

	/**
	 * Called when a subsequent instance of the application is created
	 */
	void newInstanceCreated();
}
