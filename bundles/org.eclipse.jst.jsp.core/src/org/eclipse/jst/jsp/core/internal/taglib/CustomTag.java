/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.taglib;

/**
 * Contains information about a custom tag including the tag name (with prefix),
 * the class that defines the functionality of the tag, TEI class, any scripting variables associated with the
 * TEI class, and if the class is an iteration tag
 *
 */
public class CustomTag {

	private final String fTagName;
	private final String fClassName;
	private final String fTeiClassName;
	private final boolean fIsIterationTag;
	private final TaglibVariable[] fVariables;

	public CustomTag(String tagName, String className, String teiClassName, TaglibVariable[] tagVariables, boolean isIterationTag) {
		fTagName = tagName;
		fClassName = className;
		fTeiClassName = teiClassName;
		fVariables = tagVariables;
		fIsIterationTag = isIterationTag;
	}

	/**
	 * Returns the name of the tag with its prefix
	 * @return the tag name including prefix
	 */
	public String getTagName() {
		return fTagName;
	}

	/**
	 * Returns the name of the implementation class for the tag
	 * @return the name of the implementation class for the tag
	 */
	public String getTagClassName() {
		return fClassName;
	}

	/**
	 * Returns the name of the TagExtraInfo class for the tag
	 * @return the name of the TagExtraInfo class for the tag
	 */
	public String getTeiClassName() {
		return fTeiClassName;
	}

	/**
	 * Returns an array of scripting variables associated with the TagExtraInfo class
	 * @return an array of scripting variables associated with the TagExtraInfo class
	 */
	public TaglibVariable[] getTagVariables() {
		return fVariables;
	}

	/**
	 * Identifies if the tag implements the IterationTag interface
	 * @return true if the tag implements the IterationTag interface; false otherwise
	 */
	public boolean isIterationTag() {
		return fIsIterationTag;
	}
}
