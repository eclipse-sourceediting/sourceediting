/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;



/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.<br>
* 
*/
public interface IJsTranslation {

	/**
	 * @return IJavaScriptProject that this translation belongs to
	 */
	public IJavaScriptProject getJavaProject();

	/**
	 * @return Original HTML document from the translation.
	 */
	public IDocument getHtmlDocument();

	/**
	 * @return integer position of a missing </script> tag (the document isn't well formed and resulted in translation error.).
	 * 
	 */
	public int getMissingTagStart();

	/**
	 * @param javaPositionStart
	 * @param javaPositionEnd
	 * @return all javascript elements within the given range
	 */
	public IJavaScriptElement[] getAllElementsInJsRange(int javaPositionStart, int javaPositionEnd);

	/**
	 * @return the javascript unit from the jsdt.core
	 */
	public IJavaScriptUnit getCompilationUnit();

	/**
	 * @param javaPositionStart
	 * @param javaPositionEnd
	 * @return
	 */
	public IJavaScriptElement[] getElementsFromJsRange(int javaPositionStart, int javaPositionEnd);

	/**
	 * @return string of the document.
	 */
	public String getHtmlText();

	/**
	 * @param jsOffset
	 * @return a single javascript element at the given offset.
	 */
	public IJavaScriptElement getJsElementAtOffset(int jsOffset);

	/**
	 * @return only the translated javascript text
	 */
	public String getJsText();

	/**
	 * @return a list of the script regions within the translation.
	 */
	public Position[] getScriptPositions();

	/**
	 * @param text
	 */
	public void insertInFirstScriptRegion(String text);

	/**
	 * insert javascript at the given offset.  method should ensure the documents well-formedness and proper script region.
	 * 
	 * @param offset
	 * @param text
	 */
	public void insertScript(int offset, String text);

	/**
	 * @return a list of javascript errors
	 */
	public List getProblems();

	/**
	 * @param offset
	 * @return if the offset is within a script import node.
	 */
	public boolean ifOffsetInImportNode(int offset);

	/**
	 * checks the CU for errors/consistancy.
	 */
	public void reconcileCompilationUnit();

	/**
	 * release the translation.  always a good idea to do when you're done.  you may notice document and model locks if not.
	 */
	public void release();
	
	/**
	 * fixes a mangled html--> pure js name.
	 * @param displayString
	 * @return
	 */
	public String fixupMangledName(String displayString); 
	
	/**
	 * start/stop collecting problems within the javascript unit.
	 * @param collect
	 */
	public void setProblemCollectingActive(boolean collect);
	
	/**
	 * @return
	 */
	public String getJavaPath();
	
	/**
	 * 
	 * 
	 * @param htmlDocument
	 * @param javaProj
	 * @param listenForChanges
	 * @return
	 */
	public IJsTranslation getInstance(IStructuredDocument htmlDocument, IJavaScriptProject javaProj, boolean listenForChanges) ;

	/**
	 * notify the translation to update any external dependancies that are created during translation
	 * 
	 */
	public void classpathChange() ;

	/**
	 * @param offset - web page offset
	 * @return the presumed JavaScript offset
	 */
	int getJavaScriptOffset(int indexOf);
	
	/**
	 * @param offset - JavaScript offset
	 * @return the presumed web page offset
	 */
	public int getWebPageOffset(int offset);
}