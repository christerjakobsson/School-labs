package TravelInfoGui;

/* HTMLLinkListener
 * 
 * Purpose: Listens to when a link in te editorPane is clicked.
 * 
 * v1.0
 * 
 * Copyright (c). 2014-01-08 Christer Jakobsson.
 */
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Class that are used to listen to HyperLink clicks.
 * 
 * @author Christer
 */
public class LinkListener implements HyperlinkListener {
	private final JEditorPane browserPane;

	/**
	 * Constructor, creates class.
	 * 
	 * @param p
	 */
	LinkListener(JEditorPane p) {
		this.browserPane = p;
	}

	/**
	 * If a link is clicked tries to change the page on the editorPane to
	 * the page.
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		try {
			if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				browserPane.setPage(arg0.getURL());
		} catch (IOException e) {
			browserPane.setText("Error 404");
		}
	}
}