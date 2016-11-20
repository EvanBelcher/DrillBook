package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.event.*;
import javax.swing.JComboBox;
import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.Page;

/**
 * Custom ItemListener for the navigation JComboBox (Page Data).
 *
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class NavigationItemListener implements ItemListener {

	private JComboBox<String> nav;
	private PageDataFrame pdf;
	private Page currentPage;

	/**
	 * Constructs the NavigationItemListener
	 *
	 * @param pdf the PageDataFrame that the combobox is in
	 * @param nav the combobox
	 * @param currentPage the current page
	 * @since 1.0
	 */
	public NavigationItemListener(PageDataFrame pdf, JComboBox<String> nav, Page currentPage) {
		this.pdf = pdf;
		this.nav = nav;
		this.currentPage = currentPage;
	}

	/**
	 * Changes the page or creates a new page
	 *
	 * @since 1.0
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (nav.getSelectedItem().equals("New Page")) {
			Main.addPage();
			currentPage = pdf.getCurrentPage();

			nav.removeItemListener(nav.getItemListeners()[0]);

			nav.insertItemAt(currentPage.toDisplayString(), 0);
			nav.setSelectedIndex(0);

			nav.addItemListener(new NavigationItemListener(pdf, nav, currentPage));
		} else {
			Main.setCurrentPage(nav.getItemCount() - nav.getSelectedIndex() - 1);
		}
		pdf.updateAll();
	}

}
