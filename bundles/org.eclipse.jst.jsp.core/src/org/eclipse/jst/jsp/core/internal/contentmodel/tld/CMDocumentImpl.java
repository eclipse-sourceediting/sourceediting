/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDValidator;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.annotation.AnnotationMap;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation.AnnotationFileInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation.AnnotationFileParser;

public class CMDocumentImpl implements TLDDocument {
	
	/** Contains taginfo and/or any other misc properties*/
	private AnnotationMap fAnnotationMap = null;
	
	private Map fProperties = new HashMap(0);

	/**
	 * Records from where this document was created
	 */
	private String fBaseLocation;
	/** 
	 * since JSP 1.2
	 */
	private String fDescription;
	
	private String fDisplayName;

	/**
	 * NOT public API
	 */
	public CMNamedNodeMapImpl fElements = new CMNamedNodeMapImpl();

	private List fFunctions = new ArrayList(0);
	
	// id of the taglib
	private String fId = null;
	/**
	 * Children of "taglib" within a .tld file each allow
	 * one Text node (#PCDATA) beneath them.  Store the values
	 * here for simplicity.
	 */
	// The JSP specification required for this taglib to function
	private String fJSPVersion = null;
	
	private String fLargeIcon;

	private List fListeners;
	
	private String fParentURI = null;
	// A short name suggested as the default prefix for tags within the lib
	private String fShortName = null;
	
	private String fSmallIcon;
	
	/**
	 * since JSP 2.0
	 * 
	 * The entire element is stored here since its layout is undefined 
	 */
	private List fTaglibExtensions = new ArrayList(0);
	
	// Version information for the taglib itself
	private String fTLibVersion = null;
	
	// A unique public URI describing this taglib.  Recommended to be the URL
	// to the descriptor
	private String fURI = null;
	
	private String fLocationString;

	protected TLDValidator validator;
	
	/**
	 * CMDocumentImpl constructor comment.
	 */
	public CMDocumentImpl() {
		super();
	}

	/**
	 * Get the annotation map associated with this document.  Lazily creates
	 * and loads annotation map.
	 * @return AnnotationMap
	 */
	private AnnotationMap getAnnotationMap() {
		// create a new annotation map and load it up
		if (fAnnotationMap == null) {
			fAnnotationMap = new AnnotationMap();
			
			List annotationFiles = ContentModelManager.getInstance().getAnnotationFilesInfos(getUri());
		    for (Iterator i = annotationFiles.iterator(); i.hasNext();) {
		    	try {
		    		AnnotationFileInfo fileInfo = (AnnotationFileInfo) i.next();
		    		AnnotationFileParser parser = new AnnotationFileParser();
		    		parser.parse(fAnnotationMap, fileInfo);
		        } catch (Exception e) {
		        	Logger.log(Logger.WARNING_DEBUG, "Exception thrown in CMDocumentImpl#getAnnotationMap", e); //$NON-NLS-1$
		        }
		    }
		}
		return fAnnotationMap;
	}
	/**
	 * @return Returns the baseLocation.
	 */
	public String getBaseLocation() {
		return fBaseLocation;
	}

	/**
	 * Gets the description.
	 * @return Returns a String
	 */
	public String getDescription() {
		return fDescription;
	}

	/**
	 * Gets the displayName.
	 * @return Returns a String
	 */
	public String getDisplayName() {
		return fDisplayName;
	}

	/**
	 * getElements method
	 * @return CMNamedNodeMap
	 *
	 * Returns CMNamedNodeMap of ElementDeclaration
	 */
	public CMNamedNodeMap getElements() {
		return fElements;
	}

	/**
	 * getEntities method
	 * @return CMNamedNodeMap
	 *
	 * Returns CMNamedNodeMap of EntityDeclaration
	 */
	public CMNamedNodeMap getEntities() {
		return null;
	}
	
	public List getExtensions() {
		return fTaglibExtensions;
	}
	/**
	 * @return Returns the functions.
	 */
	public List getFunctions() {
		return fFunctions;
	}

	/**
	 * Gets the id.
	 * @return Returns a String
	 */
	public String getId() {
		return fId;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getInfo() {
		return getDescription();
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getJspversion() {
		return fJSPVersion;
	}

	/**
	 * Gets the largeIcon.
	 * @return Returns a String
	 */
	public String getLargeIcon() {
		return fLargeIcon;
	}

	public List getListeners() {
		if (fListeners == null)
			fListeners = new ArrayList();
		return fListeners;
	}

	/**
	 * getNamespace method
	 * @return CMNamespace
	 */
	public CMNamespace getNamespace() {
		return null;
	}

	/**
	 * getNodeName method
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return "#cmdocument"; //$NON-NLS-1$
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 *
	 */
	public int getNodeType() {
		return CMNode.DOCUMENT;
	}
	/**
	 * @return Returns the parentURI.
	 */
	public String getParentURI() {
		return fParentURI;
	}

	/**
	 * getProperty method
	 * @return java.lang.Object
	 *
	 * Returns the object property desciped by the propertyName
	 *
	 */
	public Object getProperty(String propertyName) {
		if (propertyName.equals(TLDDocument.CM_KIND)) {
			return TLDDocument.JSP_TLD;
		}
		else if (propertyName.equals("annotationMap")) { //$NON-NLS-1$
			return getAnnotationMap();
		}
		return fProperties.get(propertyName);
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getShortname() {
		return fShortName;
	}

	/**
	 * Gets the smallIcon.
	 * @return Returns a String
	 */
	public String getSmallIcon() {
		return fSmallIcon;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getTlibversion() {
		return fTLibVersion;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getUri() {
		return fURI;
	}

	/*
	 * @see TLDDocument#getValidator()
	 */
	public TLDValidator getValidator() {
		return validator;
	}
	/**
	 * @param baseLocation The baseLocation to set.
	 */
	public void setBaseLocation(String baseLocation) {
		fBaseLocation = baseLocation;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.fDescription = description;
	}

	/**
	 * Sets the displayName.
	 * @param displayName The displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.fDisplayName = displayName;
	}

	/**
	 * Sets the id.
	 * @param id The id to set
	 */
	public void setId(String id) {
		this.fId = id;
	}

	/**
	 * 
	 * @param newInfo java.lang.String
	 */
	public void setInfo(String newInfo) {
		setDescription(newInfo);
	}

	/**
	 * 
	 * @param newJspversion java.lang.String
	 */
	public void setJspversion(String newJspversion) {
		fJSPVersion = newJspversion;
	}

	/**
	 * Sets the largeIcon.
	 * @param largeIcon The largeIcon to set
	 */
	public void setLargeIcon(String largeIcon) {
		this.fLargeIcon = largeIcon;
	}

	public void setListeners(List listeners) {
		this.fListeners = listeners;
	}
	/**
	 * @param parentURI The parentURI to set.
	 */
	public void setParentURI(String parentURI) {
		fParentURI = parentURI;
	}
	
	public void setProperty(String property, Object value) {
		fProperties.put(property, value);
	}

	/**
	 * 
	 * @param newShortname java.lang.String
	 */
	public void setShortname(String newShortname) {
		fShortName = newShortname;
	}

	/**
	 * Sets the smallIcon.
	 * @param smallIcon The smallIcon to set
	 */
	public void setSmallIcon(String smallIcon) {
		this.fSmallIcon = smallIcon;
	}

	/**
	 * 
	 * @param newTlibversion java.lang.String
	 */
	public void setTlibversion(String newTlibversion) {
		fTLibVersion = newTlibversion;
	}

	/**
	 * 
	 * @param newUri java.lang.String
	 */
	public void setUri(String newUri) {
		fURI = newUri;
	}

	public void setValidator(TLDValidator validator) {
		this.validator = validator;
	}

	/**
	 * supports method
	 * @return boolean
	 *
	 * Returns true if the CMNode supports a specified property
	 *
	 */
	public boolean supports(String propertyName) {
		if (TLDDocument.CM_KIND.equals(propertyName) || "annotationMap".equals(propertyName)) //$NON-NLS-1$
			return true;
		return fProperties.containsKey(propertyName);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("\n\t short name:" + StringUtils.escape(getShortname())); //$NON-NLS-1$
		buffer.append("\n\t display name:" + StringUtils.escape(getDisplayName())); //$NON-NLS-1$
		buffer.append("\n\t description (info):" + StringUtils.escape(getDescription())); //$NON-NLS-1$
		buffer.append("\n\t URI:" + StringUtils.escape(getUri())); //$NON-NLS-1$
		buffer.append("\n\t jsp version:" + StringUtils.escape(getJspversion())); //$NON-NLS-1$
		buffer.append("\n\t taglib version:" + StringUtils.escape(getTlibversion())); //$NON-NLS-1$
		buffer.append("\n\t small icon:" + StringUtils.escape(getSmallIcon())); //$NON-NLS-1$
		buffer.append("\n\t large icon:" + StringUtils.escape(getLargeIcon())); //$NON-NLS-1$
		if (getValidator() != null)
			buffer.append("\n\t validator:" + StringUtils.replace(getValidator().toString(), "\n", "\n\t\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buffer.append("\n\t listeners:"); //$NON-NLS-1$
		for (int i = 0; i < getListeners().size(); i++) {
			buffer.append("\n" + StringUtils.replace(getListeners().get(i).toString(), "\n", "\n\t\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		buffer.append("\n\t elements:"); //$NON-NLS-1$
		CMNamedNodeMap elements = getElements();
		for (int i = 0; i < elements.getLength(); i++) {
			buffer.append(StringUtils.replace(elements.item(i).toString(), "\n", "\n\t\t")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return buffer.toString();
	}

	public String getLocationString() {
		return fLocationString;
	}

	public void setLocationString(String url) {
		fLocationString = url;
	}
}
