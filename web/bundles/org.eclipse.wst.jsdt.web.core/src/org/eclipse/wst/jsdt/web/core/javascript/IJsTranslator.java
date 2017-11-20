/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *     
 *******************************************************************************/


package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;



/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public interface IJsTranslator extends IDocumentListener{

	/**
	 * @return string of javascript from the document
	 */
	public String getJsText();

	/**
	 * sets the javascript unit buffer
	 * @param buffer
	 */
	public void setBuffer(IBuffer buffer);

	/**
	 * 
	 * @return a list of html locations within the docuemnt.
	 */
	public Position[] getHtmlLocations();

	/**
	 * @return the region of a missing </script> tag
	 */
	public int getMissingEndTagRegionStart();

	/**
	 * @return position array of <script src=".."> within the doc.
	 */
	public Position[] getImportHtmlRanges();

	/**
	 * @return raw/unresolved <script imports>
	 */
	public String[] getRawImports();

	/**
	 *  begin translating the document.
	 */
	public void translate();

	/**
	 * translates an inline (event="..") js container region and adds it to the document text.  must be called in order
	 * @param container
	 */
	public void translateInlineJSNode(IStructuredDocumentRegion container);

	/**
	 * translates a script block.  must be called in the order it appears within the document.
	 * @param container
	 */
	public void translateJSNode(IStructuredDocumentRegion container);

	/**
	 * translates a <script src=".."> element, parsing out an import.
	 * @param region
	 */
	public void translateScriptImportNode(IStructuredDocumentRegion region);

	/**
	 * release any resources the translation is holding onto.
	 * 
	 */
	public void release();

}