/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.IPackageDeclaration;
import org.eclipse.wst.jsdt.core.IPackageFragment;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;
import org.eclipse.wst.jsdt.internal.core.DocumentContextFragment;
import org.eclipse.wst.jsdt.internal.core.DocumentContextFragmentRoot;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;

/**
 * @author brad childs
 */
public class JSPTranslation implements IJSPTranslation {
	// for debugging
	private static final boolean DEBUG;
	
	
	
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	/** the name of the class (w/out extension) * */
	private String fClassname = ""; //$NON-NLS-1$
	private ICompilationUnit fCompilationUnit = null;
	private List fGeneratedFunctionNames = null;
	private HashMap fJsToHtmlMap = null;
	private IJavaProject fJavaProject = null;
	private String fJsText = ""; //$NON-NLS-1$
	private HashMap fHtmlToJsMap = null;
	private String fJsName;
	private String fHtmlText = ""; //$NON-NLS-1$
	/** lock to synchronize access to the compilation unit * */
	private byte[] fLock = null;
	private String fMangledName;
	private IProgressMonitor fProgressMonitor = null;
	private DocumentContextFragmentRoot fDocumentScope;
	private String[] cuImports;
	private ArrayList importRanges;
	private IFile targetFile;
	
	public JSPTranslation(IJavaProject javaProj, JSPTranslator translator) {
		fLock = new byte[0];
		fJavaProject = javaProj;
		if (translator != null) {
			fJsText = translator.getTranslation().toString();
			fHtmlText = translator.getHtmlText();
			fClassname = translator.getClassname();
			fJsToHtmlMap = translator.getJs2HtmlRanges();
			fHtmlToJsMap = translator.getHtml2JsRanges();
			fJsName = translator.getFile().getName();
			targetFile = translator.getFile();
			fDocumentScope = new DocumentContextFragmentRoot(fJavaProject, 
															 targetFile, 
															 WebRootFinder.getWebContentFolder(javaProj.getProject()), 
															 WebRootFinder.getServerContextRoot(javaProj.getProject()),
															 JsWebNature.VIRTUAL_SCOPE_ENTRY);
			fGeneratedFunctionNames = translator.getExcludedElements();
			importRanges = translator.getImportHtmlRanges();
			ArrayList rawImports = translator.getRawImports();
			if (rawImports != null) {
				cuImports = (String[]) rawImports.toArray(new String[rawImports.size()]);
			} else {
				cuImports = new String[0];
			}
		}
	}
	
	public boolean ifOffsetInImportNode(int offset) {
		/* check import nodes */
		for (int i = 0; i < importRanges.size(); i++) {
			Position p = (Position) importRanges.get(i);
			if (p.includes(offset)) return true;
		}
		return false;
	}
	
	public boolean isOffsetInScriptNode(int offset) {
		return isInRanges(offset, fHtmlToJsMap);
	}
	
	/**
	 * Originally from ReconcileStepForJava. Creates an ICompilationUnit from
	 * the contents of the JSP document.
	 * 
	 * @return an ICompilationUnit from the contents of the JSP document
	 */
	private ICompilationUnit createCompilationUnit() throws JavaModelException {
		fDocumentScope.setIncludedFiles(cuImports);
		ICompilationUnit cu = fDocumentScope.getDefaultPackageFragment().getCompilationUnit(getClassname() + JsDataTypes.BASE_FILE_EXTENSION).getWorkingCopy(getWorkingCopyOwner(), getProblemRequestor(), getProgressMonitor());
		
		IBuffer buffer;
		try {
			buffer = cu.getBuffer();
		} catch (JavaModelException e) {
			e.printStackTrace();
			buffer = null;
		}
		if (buffer != null) {
			buffer.setContents(getJavaText());
		}
		
		cu.makeConsistent(getProgressMonitor());
		// cu.reconcile(ICompilationUnit.NO_AST, true, getWorkingCopyOwner(),
		// getProgressMonitor());
		
		if (getJspName() == null || getMangledName() == null) {
			String cuName = cu.getPath().lastSegment();
			if (cuName != null) {
				setMangledName(cuName.substring(0, cuName.lastIndexOf('.')));
				// set name of jsp file
				String unmangled = JSP2ServletNameUtil.unmangle(cuName);
				setHtmlFileName(unmangled.substring(unmangled.lastIndexOf('/') + 1, unmangled.length()));
			}
		}
		
		if (JSPTranslation.DEBUG) {
			String cuText = cu.toString();
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
			System.out.println("(+) JSPTranslation [" + this + "] finished creating CompilationUnit: " + cu); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
			IPackageDeclaration[] ipd = cu.getPackageDeclarations();
			for (int i = 0; i < ipd.length; i++) {
				System.out.println("JSPTranslation.getCU() Package:" + ipd[i].getElementName());
			}
		}

		return cu;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#fixupMangledName(java.lang.String)
	 */
	public String fixupMangledName(String displayString) {
		if (displayString == null) {
			return null;
		}
		return displayString.replaceAll(getMangledName() + ".js", getJspName());
	}
	
	public IJavaElement[] getAllElementsInJsRange(int jspStart, int jspEnd) {
		int javaPositionStart = getJsOffset(jspStart);
		int javaPositionEnd = getJsOffset(jspEnd);
		IJavaElement[] EMTPY_RESULT_SET = new IJavaElement[0];
		IJavaElement[] result = EMTPY_RESULT_SET;
		
		IJavaElement[] allChildren = null;
		
		
			try {
				allChildren =  getCompilationUnit().getChildren();
			} catch (JavaModelException e) {}
		
		
		Vector validChildren = new Vector();
		for (int i = 0; i < allChildren.length; i++) {
			if (allChildren[i] instanceof IJavaElement && allChildren[i].getElementType() != IJavaElement.PACKAGE_DECLARATION) {
				ISourceRange range = getJSSourceRangeOf(allChildren[i]);
				if (javaPositionStart <= range.getOffset() && range.getLength() + range.getOffset() <= (javaPositionEnd)) {
					validChildren.add(allChildren[i]);
				}
			}
		}
		if (validChildren.size() > 0) {
			result = (IJavaElement[]) validChildren.toArray(new IJavaElement[] {});
		}
		if (result == null || result.length == 0) {
			return EMTPY_RESULT_SET;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getClassname()
	 */
	public String getClassname() {
		return fClassname;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getCompilationUnit()
	 */
	public ICompilationUnit getCompilationUnit() {
		synchronized (fLock) {
			try {
				if (fCompilationUnit == null) {
					fCompilationUnit = createCompilationUnit();
				}
				// reconcileCompilationUnit();
			} catch (JavaModelException jme) {
				if (JSPTranslation.DEBUG) {
					Logger.logException("error creating JSP working copy... ", jme); //$NON-NLS-1$
				}
			}
			return fCompilationUnit;
		}
		
	}
	
	public IJavaElement[] getElementsFromJspRange1(int jspStart, int jspEnd) {
		int javaPositionStart = getJsOffset(jspStart);
		int javaPositionEnd = getJsOffset(jspEnd);
		IJavaElement[] EMTPY_RESULT_SET = new IJavaElement[0];
		IJavaElement[] result = EMTPY_RESULT_SET;
		try {
					int cuDocLength = getCompilationUnit().getBuffer().getLength();
					int javaLength = javaPositionEnd - javaPositionStart;
					if (cuDocLength > 0 && javaPositionStart >= 0 && javaLength >= 0 && javaPositionEnd < cuDocLength) {
					result =  getCompilationUnit().codeSelect(javaPositionStart, javaLength);
			}
			if (result == null || result.length == 0) {
				return EMTPY_RESULT_SET;
			}
		} catch (JavaModelException x) {
			Logger.logException(x);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getElementsFromJspRange(int,
	 *      int)
	 */
	public IJavaElement[] getElementsFromJsRange(int jspStart, int jspEnd) {
		int javaPositionStart = getJsOffset(jspStart);
		int javaPositionEnd = getJsOffset(jspEnd);
		IJavaElement[] EMTPY_RESULT_SET = new IJavaElement[0];
		IJavaElement[] result = EMTPY_RESULT_SET;
		try {
			ICompilationUnit cu = getCompilationUnit();
			// cu.makeConsistent(getProgressMonitor());
			// cu.reconcile(ICompilationUnit.NO_AST, true,
			// getWorkingCopyOwner(), getProgressMonitor());
			if (cu != null) {
				synchronized (fLock) {
					int cuDocLength = cu.getBuffer().getLength();
					int javaLength = javaPositionEnd - javaPositionStart;
					if (cuDocLength > 0 && javaPositionStart >= 0 && javaLength >= 0 && javaPositionEnd <= cuDocLength) {
						result = cu.codeSelect(javaPositionStart, javaLength, getWorkingCopyOwner());
					}
				}
			}
			if (result == null || result.length == 0) {
				return EMTPY_RESULT_SET;
			}
		} catch (JavaModelException x) {
			Logger.logException(x);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJava2JspMap()
	 */
	public HashMap getJs2HtmlMap() {
		return fJsToHtmlMap;
	}
	
	public IJavaElement getJsElementAtOffset(int htmlstart) {
		IJavaElement elements = null;
		int jsOffset = getJsOffset(htmlstart);
		// Position[] positions = getJavaRanges(htmlOffset, length);
		//        
		//ICompilationUnit cu = getCompilationUnit();
		//synchronized (cu) {
			try {
				elements = getCompilationUnit().getElementAt(jsOffset);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				if (JSPTranslation.DEBUG) {
					Logger.logException("error retrieving java elemtnt from compilation unit... ", e); //$NON-NLS-1$
				}
			//}
		}
		return elements;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaOffset(int)
	 */
	public int getJsOffset(int jspOffset) {
		int result = -1;
		int offsetInRange = 0;
		Position jspPos, javaPos = null;
		// iterate all mapped jsp ranges
		Iterator it = fHtmlToJsMap.keySet().iterator();
		while (it.hasNext()) {
			jspPos = (Position) it.next();
			// need to count the last position as included
			if (!jspPos.includes(jspOffset) && !(jspPos.offset + jspPos.length == jspOffset)) {
				continue;
			}
			offsetInRange = jspOffset - jspPos.offset;
			javaPos = (Position) fHtmlToJsMap.get(jspPos);
			if (javaPos != null) {
				result = javaPos.offset + offsetInRange;
			} else {
				Logger.log(Logger.ERROR, "JavaPosition was null!" + jspOffset); //$NON-NLS-1$
			}
			break;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#isUseBean(int)
	 */
	// public boolean isUseBean(int javaOffset) {
	// System.out.println("REMOVE JSPTranslation.isUseBean(int javaOffset)");
	// return false;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaPath()
	 */
	public String getJavaPath() {
		// create if necessary
		ICompilationUnit cu = getCompilationUnit();
		return (cu != null) ? cu.getPath().toString() : ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaRanges(int,
	 *      int)
	 */
	public Position[] getJavaRanges(int offset, int length) {
		List results = new ArrayList();
		Iterator it = getJs2HtmlMap().keySet().iterator();
		Position p = null;
		while (it.hasNext()) {
			p = (Position) it.next();
			if (p.overlapsWith(offset, length)) {
				results.add(p);
			}
		}
		return (Position[]) results.toArray(new Position[results.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getJavaText()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaText()
	 */
	public String getJavaText() {
		// return (fTranslator != null) ?
		// fTranslator.getTranslation().toString(): ""; //$NON-NLS-1$
		return fJsText;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJsp2JavaMap()
	 */
	public HashMap getJsp2JavaMap() {
		return fHtmlToJsMap;
	}
	
	private String getJspName() {
		return fJsName;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJspOffset(int)
	 */
	public int getJspOffset(int javaOffset) {
		int result = -1;
		int offsetInRange = 0;
		Position jspPos, javaPos = null;
		// iterate all mapped java ranges
		Iterator it = fJsToHtmlMap.keySet().iterator();
		while (it.hasNext()) {
			javaPos = (Position) it.next();
			// need to count the last position as included
			if (!javaPos.includes(javaOffset) && !(javaPos.offset + javaPos.length == javaOffset)) {
				continue;
			}
			offsetInRange = javaOffset - javaPos.offset;
			jspPos = (Position) fJsToHtmlMap.get(javaPos);
			if (jspPos != null) {
				result = jspPos.offset + offsetInRange;
			} else {
				Logger.log(Logger.ERROR, "jspPosition was null!" + javaOffset); //$NON-NLS-1$
			}
			break;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJspText()
	 */
	public String getJspText() {
		return fHtmlText;
	}
	
	private ISourceRange getJSSourceRangeOf(IJavaElement element) {
		// returns the offset in html of given element
		ISourceRange range = null;
		if (element instanceof SourceRefElement) {
			try {
				range = ((SourceRefElement) element).getSourceRange();
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return range;
	}
	
	private String getMangledName() {
		return fMangledName;
	}
	
	/**
	 * 
	 * @return the problem requestor for the CompilationUnit in this
	 *         JSPTranslation
	 */
	private JSPProblemRequestor getProblemRequestor() {
		return CompilationUnitHelper.getInstance().getProblemRequestor();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getProblems()
	 */
	public List getProblems() {
		List problems = getProblemRequestor().getCollectedProblems();
		return problems != null ? problems : new ArrayList();
	}
	
	/**
	 * 
	 * @return the progress monitor used in long operations (reconcile, creating
	 *         the CompilationUnit...) in this JSPTranslation
	 */
	private IProgressMonitor getProgressMonitor() {
		if (fProgressMonitor == null) {
			fProgressMonitor = new NullProgressMonitor();
		}
		return fProgressMonitor;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getWorkingCopyOwner()
	 */
	public WorkingCopyOwner getWorkingCopyOwner() {
		return CompilationUnitHelper.getInstance().getWorkingCopyOwner();
	}
	
	public boolean isIndirect(int javaOffset) {
		System.out.println("IMPLEMENT JSPTranslation.isIndirect(int javaOffset)");
		return false;
		// return isInRanges(javaOffset, fJava2JspIndirectMap, false);
	}
	
	private boolean isInRanges(int javaOffset, HashMap ranges) {
		return isInRanges(javaOffset, ranges, true);
	}
	
	/**
	 * Tells you if the given offset is included in any of the ranges
	 * (Positions) passed in. includeEndOffset tells whether or not to include
	 * the end offset of each range in the test.
	 * 
	 * @param javaOffset
	 * @param ranges
	 * @param includeEndOffset
	 * @return
	 */
	private boolean isInRanges(int javaOffset, HashMap ranges, boolean includeEndOffset) {
		Iterator it = ranges.keySet().iterator();
		while (it.hasNext()) {
			Position javaPos = (Position) it.next();
			// also include the start and end offset (only if requested)
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=81687
			if (javaPos.includes(javaOffset) || (includeEndOffset && javaPos.offset + javaPos.length == javaOffset)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#javaSpansMultipleJspPartitions(int,
	 *      int)
	 */
	public boolean javaSpansMultipleJspPartitions(int javaOffset, int javaLength) {
		HashMap java2jsp = getJs2HtmlMap();
		int count = 0;
		Iterator it = java2jsp.keySet().iterator();
		Position javaRange = null;
		while (it.hasNext()) {
			javaRange = (Position) it.next();
			if (javaRange.overlapsWith(javaOffset, javaLength)) {
				count++;
			}
			if (count > 1) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#reconcileCompilationUnit()
	 */
	public void reconcileCompilationUnit() {
		//if(true) return;
		ICompilationUnit cu = fCompilationUnit;
		if (fCompilationUnit == null) {
			return;
		}
		if (cu != null) {
			try {
				synchronized (fLock) {
					//if(false) 
						cu.makeConsistent(getProgressMonitor());
					cu.reconcile(ICompilationUnit.NO_AST, true, getWorkingCopyOwner(), getProgressMonitor());
				}
			} catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#release()
	 */
	public void release() {
		synchronized (fLock) {
			if (fCompilationUnit != null) {
				try {
					if (JSPTranslation.DEBUG) {
						System.out.println("------------------------------------------------------------------"); //$NON-NLS-1$
						System.out.println("(-) JSPTranslation [" + this + "] discarding CompilationUnit: " + fCompilationUnit); //$NON-NLS-1$ //$NON-NLS-2$
						System.out.println("------------------------------------------------------------------"); //$NON-NLS-1$
					}
					fCompilationUnit.discardWorkingCopy();
				} catch (JavaModelException e) {
					// we're done w/ it anyway
				}
			}
		}
	}
	
	private void setHtmlFileName(String jspName) {
		fJsName = jspName;
	}
	
	private void setMangledName(String mangledName) {
		fMangledName = mangledName;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#setProblemCollectingActive(boolean)
	 */
	public void setProblemCollectingActive(boolean collect) {
		ICompilationUnit cu = getCompilationUnit();
		if (cu != null) {
			getProblemRequestor().setIsActive(collect);
		}
	}
}