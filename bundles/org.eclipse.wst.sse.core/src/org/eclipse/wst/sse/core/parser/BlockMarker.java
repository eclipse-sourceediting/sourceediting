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
package org.eclipse.wst.sse.core.parser;



import org.eclipse.wst.sse.core.text.ITextRegion;

public class BlockMarker extends TagMarker {

	// allow for JSP expressions within the block
	protected boolean fAllowJSP = true;

	protected boolean fCaseSensitive = false;

	// the context for the contents of this tag (BLOCK_TEXT, JSP_CONTENT, etc.)
	protected String fContext;

	/**
	 * It's not appropriate to make "empty" BlockMarker, so we'll 
	 * mark as private.
	 */
	private BlockMarker() {
		super();
	}

	public BlockMarker(String tagName, ITextRegion marker, String context) {
		this(tagName, marker, context, true);
	}

	public BlockMarker(String tagName, ITextRegion marker, String context, boolean caseSensitive) {
		this(tagName, marker, context, caseSensitive, true);
	}

	/**
	 * @deprecated - TODO: Nitin, this is used frequently, what's the substitue? 
	 * @param tagName
	 * @param offset
	 * @param context
	 * @param caseSensitive
	 */
	public BlockMarker(String tagName, int offset, String context, boolean caseSensitive) {
		this(tagName, null, context, caseSensitive, true);
	}

	public BlockMarker(String tagName, ITextRegion marker, String context, boolean caseSensitive, boolean allowJSP) {
		super(tagName, marker);
		setContext(context);
		setCaseSensitive(caseSensitive);
		setAllowJSP(allowJSP);
	}

	public BlockMarker(String tagName, String regionContext, boolean caseSensitive) {
		this(tagName, null, regionContext, caseSensitive, false);
	}

	/**
	 * Gets the allowJSP.
	 * @return Returns a boolean
	 */
	public boolean allowsJSP() {
		return fAllowJSP;
	}

	/**
	 * Gets the context.
	 * @return Returns a String
	 */
	public String getContext() {
		return fContext;
	}

	/**
	 * 
	 * @return boolean
	 */
	public final boolean isCaseSensitive() {
		return fCaseSensitive;
	}

	/**
	 * Sets the allowJSP.
	 * @param allowJSP The allowJSP to set
	 */
	public void setAllowJSP(boolean allowJSP) {
		fAllowJSP = allowJSP;
	}

	public final void setCaseSensitive(boolean sensitive) {
		fCaseSensitive = sensitive;
	}

	/**
	 * Sets the context.
	 * @param context The context to set
	 */
	public void setContext(String context) {
		fContext = context;
	}

}
