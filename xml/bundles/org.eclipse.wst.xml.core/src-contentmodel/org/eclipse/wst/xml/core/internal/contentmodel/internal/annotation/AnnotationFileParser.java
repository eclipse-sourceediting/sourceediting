/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.annotation.Annotation;
import org.eclipse.wst.xml.core.internal.contentmodel.annotation.AnnotationMap;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 */
public class AnnotationFileParser {
	public static final String TAG_ID_ANNOTATIONS = "abstractGrammarAnnotations"; //$NON-NLS-1$
	public static final String TAG_ID_ANNOTATION = "annotation"; //$NON-NLS-1$
	public static final String TAG_ID_PROPERTY = "property"; //$NON-NLS-1$

	/**
	 * This method is called to parse an annotation file and store the
	 * contents into an annotationMap
	 */
	private void parse(AnnotationMap annotationMap, InputStream input, AnnotationFileInfo fileInfo) throws Exception {
		// move to Xerces-2.... add 'contextClassLoader' stuff
		ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(new InputSource(input), new AnnotationMapContentHandler(annotationMap, fileInfo));
		}
		finally {
			Thread.currentThread().setContextClassLoader(prevClassLoader);
		}
	}

	/**
	 * This method is called to parse an annotation file and store the
	 * contents into an annotationMap
	 */
	public void parse(AnnotationMap map, AnnotationFileInfo fileInfo) throws Exception {
		InputStream inputStream = null;
		try {
			URL url = Platform.find(Platform.getBundle(fileInfo.getBundleId()), Path.fromOSString(fileInfo.getAnnotationFileLocation()));
			if (url != null) {
				inputStream = url.openStream();
				parse(map, inputStream, fileInfo);
			}
		}
		catch (Exception e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
			throw (e);
		}
		finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			}
			catch (IOException e) {
			}
		}
	}

	protected class AnnotationMapContentHandler extends DefaultHandler {
		private AnnotationMap annotationMap;
		private Annotation currentAnnotation;
		private String currentPropertyName;
		private StringBuffer propertyValueBuffer;
		private ResourceBundle resourceBundle;
		private AnnotationFileInfo fFileInfo;

		public AnnotationMapContentHandler(AnnotationMap annotationMap, AnnotationFileInfo fileInfo) {
			this.annotationMap = annotationMap;
			this.fFileInfo = fileInfo;
		}

		private URL generatePropertiesFileURL(AnnotationFileInfo fileInfo, String propertiesLocation) {
			URL propertiesURL = null;

			// prepend $nl$ variable to location
			IPath annotationPath = Path.fromOSString("$nl$/" + fileInfo.getAnnotationFileLocation()); //$NON-NLS-1$
			// remove the annotation.xml file
			IPath annotationFolder = annotationPath.removeLastSegments(1);
			// append location of propertiles file
			IPath propertiesFile = annotationFolder.append(propertiesLocation);
			// append .properties extension if needed
			if (propertiesFile.getFileExtension() == null)
				propertiesFile = propertiesFile.addFileExtension("properties"); //$NON-NLS-1$
			// create a URL out of the properties file location
			propertiesURL = Platform.find(Platform.getBundle(fileInfo.getBundleId()), propertiesFile);
			return propertiesURL;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			propertyValueBuffer = new StringBuffer();
			if (localName.equals(TAG_ID_ANNOTATIONS)) {
				int attributesLength = attributes.getLength();
				for (int i = 0; i < attributesLength; i++) {
					String attributeName = attributes.getLocalName(i);
					String attributeValue = attributes.getValue(i);
					if (attributeName.equals("propertiesLocation")) //$NON-NLS-1$
					{
						URL bundleURL = generatePropertiesFileURL(fFileInfo, attributeValue);
						if (bundleURL != null) {
							InputStream bundleStream = null;
							try {
								bundleStream = bundleURL.openStream();
								resourceBundle = new PropertyResourceBundle(bundleStream);
							}
							catch (IOException e) {
								Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
							}
							finally {
								try {
									if (bundleStream != null)
										bundleStream.close();
								}
								catch (IOException x) {
									Logger.log(Logger.WARNING_DEBUG, x.getMessage(), x);
								}
							}
						}
					}
					else if (attributeName.equals("caseSensitive")) //$NON-NLS-1$
					{
						if (attributeValue.trim().equals("false")) //$NON-NLS-1$
						{
							annotationMap.setCaseSensitive(false);
						}
					}
				}
			}
			else if (localName.equals(TAG_ID_ANNOTATION)) {
				currentAnnotation = null;
				String specValue = attributes.getValue("spec"); //$NON-NLS-1$
				if (specValue != null) {
					currentAnnotation = new Annotation();
					currentAnnotation.setSpec(specValue);
				}
				annotationMap.addAnnotation(currentAnnotation);
			}
			else if (localName.equals(TAG_ID_PROPERTY)) {
				if (currentAnnotation != null) {
					currentPropertyName = attributes.getValue("name"); //$NON-NLS-1$
				}
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (currentPropertyName != null && currentAnnotation != null) {
				String propertyValue = propertyValueBuffer.toString();
				if (propertyValue != null) {
					if (propertyValue.startsWith("%") && resourceBundle != null) //$NON-NLS-1$
					{
						try {
							propertyValue = resourceBundle.getString(propertyValue.substring(1));
						}
						catch (Exception e) {
							// ignore any exception that occurs while trying
							// to fetch a resource
						}
					}
					currentAnnotation.setProperty(currentPropertyName, propertyValue);
				}
			}

			if (localName.equals(TAG_ID_ANNOTATION)) {
				currentAnnotation = null;
			}
			else if (localName.equals(TAG_ID_PROPERTY)) {
				currentPropertyName = null;
			}
		}

		public void characters(char[] ch, int start, int length) {
			if (currentPropertyName != null && currentAnnotation != null) {
				propertyValueBuffer.append(ch, start, length);
			}
		}
	}
}
