/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.contentassist;

import java.util.HashMap;

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.util.Assert;

/**
 * Implementation of IContextInformation.
 * Adds knowledge about the information display string such as required 
 * attributes for this context.
 * 
 * @author pavery
 */
public class AttributeContextInformation implements IContextInformation {
	/** The name of the context */
	private String fContextDisplayString;
	/** The information to be displayed */
	private String fInformationDisplayString;
	/** The image to be displayed */
	private Image fImage;
	private HashMap fAttr2RangeMap;
	private int fPosition;

	/**
	 * Creates a new context information without an image.
	 *
	 * @param contextDisplayString the string to be used when presenting the context
	 * @param informationDisplayString the string to be displayed when presenting the context information
	 */
	public AttributeContextInformation(String contextDisplayString, String informationDisplayString, HashMap attr2RangeMap) {
		this(null, contextDisplayString, informationDisplayString, attr2RangeMap);
	}

	/**
	 * Creates a new context information with an image.
	 *
	 * @param image the image to display when presenting the context information
	 * @param contextDisplayString the string to be used when presenting the context
	 * @param informationDisplayString the string to be displayed when presenting the context information,
	 *		may not be <code>null</code>
	 */
	public AttributeContextInformation(Image image, String contextDisplayString, String informationDisplayString, HashMap attr2RangeMap) {
		Assert.isNotNull(informationDisplayString);

		fImage = image;
		fContextDisplayString = contextDisplayString;
		fInformationDisplayString = informationDisplayString;
		fAttr2RangeMap = attr2RangeMap;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getContextDisplayString()
	 */
	public String getContextDisplayString() {
		if (fContextDisplayString != null)
			return fContextDisplayString;
		return fInformationDisplayString;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getImage()
	 */
	public Image getImage() {
		return fImage;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContextInformation#getInformationDisplayString()
	 */
	public String getInformationDisplayString() {
		return fInformationDisplayString;
	}

	/**
	 * Maps (String -> Position).  The attribute name to the Text position.
	 * @return
	 */
	public HashMap getAttr2RangeMap() {
		return fAttr2RangeMap;
	}

	/**
	 * @see IContextInformationExtension#getContextInformationPosition()
	 */
	public int getContextInformationPosition() {
		return fPosition;
	}

	public void setContextInformationPosition(int position) {
		fPosition = position;
	}
}
