package org.eclipse.wst.xml.resolver.tools.tests.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.apps.XParseError;
import org.apache.xml.resolver.tools.ResolvingXMLReader;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ResolvingXMLParser {
	
	public boolean validationError = false;

	protected static int maxErrs = 10;

	protected static boolean nsAware = true;

	protected static boolean validating = true;

	protected static boolean showErrors = true;

	protected static boolean showWarnings = true;

	protected static Vector catalogFiles = new Vector();

	protected  void parseAndValidate(String xmlFile, String[] catalogs)
			throws MalformedURLException, FileNotFoundException, IOException {

			CatalogManager myManager = new CatalogManager();
			StringBuffer catalogFile = new StringBuffer();
			for(int i=0; i<catalogs.length; i++){
				catalogFile = catalogFile.append(catalogs[i] + ";");
			}
			myManager.setCatalogFiles(catalogFile.toString());
		    myManager.setIgnoreMissingProperties(true);
		    myManager.setVerbosity(2);
		    ResolvingXMLReader reader = new ResolvingXMLReader(myManager);
			try {
				reader.setFeature("http://xml.org/sax/features/namespaces",
								nsAware);
				reader.setFeature("http://xml.org/sax/features/validation",
						validating);
				reader.setFeature(
						"http://apache.org/xml/features/validation/schema", true);
			} catch (SAXNotRecognizedException e1) {
				e1.printStackTrace();
			} catch (SAXNotSupportedException e1) {
				e1.printStackTrace();
			}

		XParseError xpe = new XParseError(showErrors, showWarnings);
		xpe.setMaxMessages(maxErrs);
		reader.setErrorHandler(xpe);

		String parseType = validating ? "validating" : "well-formed";
		String nsType = nsAware ? "namespace-aware" : "namespace-ignorant";
		if (maxErrs > 0) {
			System.out.println("Attempting " + parseType + ", " + nsType
					+ " parse");
		}

		try {
			reader.parse(xmlFile);
		} catch (Exception e) {
			validationError = true;
			e.printStackTrace();
		}

		if (maxErrs > 0) {
			System.out.print("Parse ");
			if (xpe.getFatalCount() > 0) {
				validationError = true;
				System.out.print("failed ");
			} else {
				System.out.print("succeeded ");
				System.out.print("(");
			}
			System.out.print("with ");

			int errCount = xpe.getErrorCount();
			int warnCount = xpe.getWarningCount();

			if (errCount > 0) {
				validationError = true;
				System.out.print(errCount + " error");
				System.out.print(errCount > 1 ? "s" : "");
				System.out.print(" and ");
			} else {
				System.out.print("no errors and ");
			}

			if (warnCount > 0) {
				validationError = true;
				System.out.print(warnCount + " warning");
				System.out.print(warnCount > 1 ? "s" : "");
				System.out.print(".");
			} else {
				System.out.print("no warnings.");
			}

			System.out.println("");
		}
	}
	
	
}
