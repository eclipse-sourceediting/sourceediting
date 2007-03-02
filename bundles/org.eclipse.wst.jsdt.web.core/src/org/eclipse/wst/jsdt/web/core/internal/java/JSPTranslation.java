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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.IPackageDeclaration;
import org.eclipse.wst.jsdt.core.IPackageFragment;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;
import org.eclipse.wst.jsdt.web.core.internal.Logger;

/**
 * <p>
 * An implementation of IJSPTranslation. <br>
 * This object that holds the java translation of a JSP file as well as a
 * mapping of ranges from the translated Java to the JSP source, and mapping
 * from JSP source back to the translated Java.
 * </p>
 * 
 * <p>
 * You may also use JSPTranslation to do CompilationUnit-esque things such as:
 * <ul>
 * <li>code select (get java elements for jsp selection)</li>
 * <li>reconcile</li>
 * <li>get java regions for jsp selection</li>
 * <li>get a JSP text edit based on a Java text edit</li>
 * <li>determine if a java offset falls within a jsp:useBean range</li>
 * <li>determine if a java offset falls within a jsp import statment</li>
 * </ul>
 * </p>
 * 
 * @author pavery
 */
public class JSPTranslation implements IJSPTranslation {

	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform
				.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	/** the name of the class (w/out extension) * */
	private String fClassname = ""; //$NON-NLS-1$
	private IJavaProject fJavaProject = null;
	private HashMap fJava2JspMap = null;
	private HashMap fJsp2JavaMap = null;
	private HashMap fJava2JspImportsMap = null;
	/*
	 * private HashMap fJava2JspUseBeanMap = null; private HashMap
	 * fJava2JspIndirectMap = null;
	 */

	// don't want to hold onto model (via translator)
	// all relevant info is extracted in the constructor.
	private JSPTranslator fTranslator = null;
	private String fJavaText = ""; //$NON-NLS-1$
	private String fJspText = ""; //$NON-NLS-1$

	private ICompilationUnit fCompilationUnit = null;
	private IProgressMonitor fProgressMonitor = null;
	/** lock to synchronize access to the compilation unit * */
	private byte[] fLock = null;
	private String fMangledName;
	private String fJspName;

	public JSPTranslation(IJavaProject javaProj, JSPTranslator translator) {

		fLock = new byte[0];
		fJavaProject = javaProj;
		fTranslator = translator;

		// can be null if it's an empty document (w/ NullJSPTranslation)
		if (translator != null) {
			fJavaText = translator.getTranslation().toString();
			fJspText = translator.getJspText();
			fClassname = translator.getClassname();
			fJava2JspMap = translator.getJava2JspRanges();
			fJsp2JavaMap = translator.getJsp2JavaRanges();
			fJava2JspImportsMap = translator.getJava2JspImportRanges();
			// fJava2JspUseBeanMap = translator.getJava2JspUseBeanRanges();
			// fJava2JspIndirectMap = translator.getJava2JspIndirectRanges();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaProject()
	 */
	public IJavaProject getJavaProject() {
		return fJavaProject;
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
		System.out.println("JSPTranslation.getJavaText():\n" + fJavaText
				+ "-----------");
		return (fTranslator != null) ? fTranslator.getTranslation().toString()
				: ""; //$NON-NLS-1$ 

		// return fJavaText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJspText()
	 */
	public String getJspText() {
		return (fTranslator != null) ? fTranslator.getJspText() : ""; //$NON-NLS-1$
		// System.out.println("JSPTranslation.getJspText():\n" + fJspText +
		// "-----------");
		// return fJspText;
	}

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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaOffset(int)
	 */
	public int getJavaOffset(int jspOffset) {
		int result = -1;
		int offsetInRange = 0;
		Position jspPos, javaPos = null;

		// iterate all mapped jsp ranges
		Iterator it = fJsp2JavaMap.keySet().iterator();
		while (it.hasNext()) {
			jspPos = (Position) it.next();
			// need to count the last position as included
			if (!jspPos.includes(jspOffset)
					&& !(jspPos.offset + jspPos.length == jspOffset)) {
				continue;
			}

			offsetInRange = jspOffset - jspPos.offset;
			javaPos = (Position) fJsp2JavaMap.get(jspPos);
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJspOffset(int)
	 */
	public int getJspOffset(int javaOffset) {
		int result = -1;
		int offsetInRange = 0;
		Position jspPos, javaPos = null;

		// iterate all mapped java ranges
		Iterator it = fJava2JspMap.keySet().iterator();
		while (it.hasNext()) {
			javaPos = (Position) it.next();
			// need to count the last position as included
			if (!javaPos.includes(javaOffset)
					&& !(javaPos.offset + javaPos.length == javaOffset)) {
				continue;
			}

			offsetInRange = javaOffset - javaPos.offset;
			jspPos = (Position) fJava2JspMap.get(javaPos);

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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJava2JspMap()
	 */
	public HashMap getJava2JspMap() {
		return fJava2JspMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJsp2JavaMap()
	 */
	public HashMap getJsp2JavaMap() {
		return fJsp2JavaMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#javaSpansMultipleJspPartitions(int,
	 *      int)
	 */
	public boolean javaSpansMultipleJspPartitions(int javaOffset, int javaLength) {
		HashMap java2jsp = getJava2JspMap();
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaRanges(int,
	 *      int)
	 */
	public Position[] getJavaRanges(int offset, int length) {

		List results = new ArrayList();
		Iterator it = getJava2JspMap().keySet().iterator();
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#isImport(int)
	 */
	public boolean isImport(int javaOffset) {
		return isInRanges(javaOffset, fJava2JspImportsMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#isUseBean(int)
	 */
	public boolean isUseBean(int javaOffset) {
		System.out.println("REMOVE JSPTranslation.isUseBean(int javaOffset)");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#isIndirect(int)
	 */
	public boolean isIndirect(int javaOffset) {
		System.out
				.println("IMPLEMENT JSPTranslation.isIndirect(int javaOffset)");
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
	private boolean isInRanges(int javaOffset, HashMap ranges,
			boolean includeEndOffset) {

		Iterator it = ranges.keySet().iterator();
		while (it.hasNext()) {
			Position javaPos = (Position) it.next();
			// also include the start and end offset (only if requested)
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=81687
			if (javaPos.includes(javaOffset)
					|| (includeEndOffset && javaPos.offset + javaPos.length == javaOffset)) {
				return true;
			}
		}
		return false;
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
			} catch (JavaModelException jme) {
				if (DEBUG) {
					Logger.logException(
							"error creating JSP working copy... ", jme); //$NON-NLS-1$
				}
			}
		}
		return fCompilationUnit;
	}

	private String getMangledName() {
		return fMangledName;
	}

	private void setMangledName(String mangledName) {
		fMangledName = mangledName;
	}

	private String getJspName() {
		return fJspName;
	}

	private void setJspName(String jspName) {
		fJspName = jspName;
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

		if (getJspName() == null || getMangledName() == null) {
			// names not set yet
			initJspAndServletNames();
		}
		return displayString.replaceAll(getMangledName(), getJspName());
	}

	private void initJspAndServletNames() {
		ICompilationUnit cu = getCompilationUnit();
		if (cu != null) {
			String cuName = null;
			synchronized (cu) {
				// set some names for fixing up mangled name in proposals
				// set mangled (servlet) name
				cuName = cu.getPath().lastSegment();
			}
			if (cuName != null) {
				setMangledName(cuName.substring(0, cuName.lastIndexOf('.')));
				// set name of jsp file
				String unmangled = JSP2ServletNameUtil.unmangle(cuName);
				setJspName(unmangled.substring(unmangled.lastIndexOf('/') + 1,
						unmangled.lastIndexOf('.')));
			}
		}
	}

	/**
	 * Originally from ReconcileStepForJava. Creates an ICompilationUnit from
	 * the contents of the JSP document.
	 * 
	 * @return an ICompilationUnit from the contents of the JSP document
	 */
	private ICompilationUnit createCompilationUnit() throws JavaModelException {

		IPackageFragment packageFragment = null;
		IJavaElement je = getJavaProject();

		if (je == null || !je.exists()) {
			return null;
		}

		switch (je.getElementType()) {
		case IJavaElement.PACKAGE_FRAGMENT:
			je = je.getParent();
			// fall through

		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) je;
			packageFragment = packageFragmentRoot
					.getPackageFragment(IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH);
			break;

		case IJavaElement.JAVA_PROJECT:
			IJavaProject jProject = (IJavaProject) je;

			if (!jProject.exists()) {
				if (DEBUG) {
					System.out
							.println("** Abort create working copy: cannot create working copy: JSP is not in a Java project"); //$NON-NLS-1$
				}
				return null;
			}

			// packageFragmentRoot = null;
			// packageFragmentRoot=JavaModelUtil.getPackageFragmentRoot(jProject);
			// IPackageFragmentRoot[] packageFragmentRoots =
			// jProject.getPackageFragmentRoots();
			packageFragmentRoot = jProject.getPackageFragmentRoot(jProject
					.getUnderlyingResource());
			if (packageFragmentRoot instanceof ICompilationUnit) {
				return (ICompilationUnit) packageFragmentRoot;
			}

			/*
			 * int i = 0; while (i < packageFragmentRoots.length) { if
			 * (!packageFragmentRoots[i].isArchive() &&
			 * !packageFragmentRoots[i].isExternal() ) { packageFragmentRoot =
			 * packageFragmentRoots[i]; System.out.println("Accepting package
			 * fragment root:" + packageFragmentRoots[i].getElementName());
			 * System.out.println("this is fragment number" + i + " out of
			 * "+packageFragmentRoots.length ); break; } i++; }
			 */
			if (packageFragmentRoot == null) {
				if (DEBUG) {
					System.out
							.println("** Abort create working copy: cannot create working copy: JSP is not in a Java project with source package fragment root"); //$NON-NLS-1$
				}
				return null;
			}

			/*
			 * for(int j =0; j<packageFragmentRoots.length;j++){
			 * System.out.println(packageFragmentRoots[j].getElementName());; }
			 */

			packageFragment = packageFragmentRoot
					.getPackageFragment(IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH);

			break;

		default:
			return null;
		}

		// JavaModelManager.createCompilationUnitFrom(file, project)

		ICompilationUnit cu = packageFragment.getCompilationUnit(
				getClassname() + JsDataTypes.BASE_FILE_EXTENSION)
				.getWorkingCopy(getWorkingCopyOwner(), getProblemRequestor(),
						getProgressMonitor());

		setContents(cu);
		cu.makeConsistent(getProgressMonitor());
		cu.reconcile(ICompilationUnit.NO_AST, true, getWorkingCopyOwner(),
				getProgressMonitor());

		if (DEBUG) {
			String cuText = cu.toString();
			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
			System.out
					.println("(+) JSPTranslation [" + this + "] finished creating CompilationUnit: " + cu); //$NON-NLS-1$ //$NON-NLS-2$
			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
			IPackageDeclaration[] ipd = cu.getPackageDeclarations();
			for (int i = 0; i < ipd.length; i++) {
				System.out.println("JSPTranslation.getCU() Package:"
						+ ipd[i].getElementName());
			}

		}

		return cu;
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getWorkingCopyOwner()
	 */
	public WorkingCopyOwner getWorkingCopyOwner() {
		return CompilationUnitHelper.getInstance().getWorkingCopyOwner();
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getProblems()
	 */
	public List getProblems() {
		List problems = getProblemRequestor().getCollectedProblems();
		return problems != null ? problems : new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getELProblems()
	 */
	public List getELProblems() {
		System.out.println("Remove JSPTranslation.getELProblems()");
		return null;
		// return fELProblems != null ? fELProblems : new ArrayList();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#reconcileCompilationUnit()
	 */
	public void reconcileCompilationUnit() {
		ICompilationUnit cu = getCompilationUnit();
		if (cu != null) {
			try {
				synchronized (cu) {
					cu.makeConsistent(getProgressMonitor());
					cu.reconcile(ICompilationUnit.NO_AST, true,
							getWorkingCopyOwner(), getProgressMonitor());
				}
			} catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
	}

	/**
	 * Set contents of the compilation unit to the translated jsp text.
	 * 
	 * @param the
	 *            ICompilationUnit on which to set the buffer contents
	 */
	private void setContents(ICompilationUnit cu) {
		if (cu == null) {
			return;
		}

		synchronized (cu) {
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
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getElementsFromJspRange(int,
	 *      int)
	 */
	public IJavaElement[] getElementsFromJspRange(int jspStart, int jspEnd) {

		int javaPositionStart = getJavaOffset(jspStart);
		int javaPositionEnd = getJavaOffset(jspEnd);

		IJavaElement[] EMTPY_RESULT_SET = new IJavaElement[0];
		IJavaElement[] result = EMTPY_RESULT_SET;
		try {
			ICompilationUnit cu = getCompilationUnit();
			if (cu != null) {
				synchronized (cu) {
					int cuDocLength = cu.getBuffer().getLength();
					int javaLength = javaPositionEnd - javaPositionStart;
					if (cuDocLength > 0 && javaPositionStart >= 0
							&& javaLength >= 0 && javaPositionEnd < cuDocLength) {
						result = cu.codeSelect(javaPositionStart, javaLength);
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getClassname()
	 */
	public String getClassname() {
		return fClassname;
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
					if (DEBUG) {
						System.out
								.println("------------------------------------------------------------------"); //$NON-NLS-1$
						System.out
								.println("(-) JSPTranslation [" + this + "] discarding CompilationUnit: " + fCompilationUnit); //$NON-NLS-1$ //$NON-NLS-2$
						System.out
								.println("------------------------------------------------------------------"); //$NON-NLS-1$
					}
					fCompilationUnit.discardWorkingCopy();
				} catch (JavaModelException e) {
					// we're done w/ it anyway
				}
			}
		}
	}
}