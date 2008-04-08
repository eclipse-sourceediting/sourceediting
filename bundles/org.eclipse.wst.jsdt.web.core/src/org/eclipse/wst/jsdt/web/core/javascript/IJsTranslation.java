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

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;



/**
 * @author childsb
 *
 */
public interface IJsTranslation {

	public IJavaProject getJavaProject();

	public IDocument getHtmlDocument();

	public int getMissingTagStart();

	public IJavaElement[] getAllElementsInJsRange(int javaPositionStart, int javaPositionEnd);

	public ICompilationUnit getCompilationUnit();

	public IJavaElement[] getElementsFromJsRange(int javaPositionStart, int javaPositionEnd);

	public String getHtmlText();

	public IJavaElement getJsElementAtOffset(int jsOffset);

	public String getJsText();

	public Position[] getScriptPositions();

	public void insertInFirstScriptRegion(String text);

	public void insertScript(int offset, String text);

	public List getProblems();

	public boolean ifOffsetInImportNode(int offset);

	public void reconcileCompilationUnit();

	public void release();
	
	public String fixupMangledName(String displayString); 
	
	public void setProblemCollectingActive(boolean collect);
	
	public String getJavaPath();
	
	public IJsTranslation getInstance(IStructuredDocument htmlDocument, IJavaProject javaProj, boolean listenForChanges) ;

	public void classpathChange() ;
}