package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.ui.internal.messages"; //$NON-NLS-1$
	
	public static String ExcludeResultPrefixesContentAssist;
	public static String NoContentAssistance;
	public static String XSLSyntaxColoringPage;
	public static String xsltStyleDocument;
	public static String MainPreferencePage;
	
	static {
	   NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
