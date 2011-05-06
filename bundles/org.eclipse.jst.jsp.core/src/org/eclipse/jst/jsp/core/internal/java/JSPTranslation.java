/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.Logger;


/**
 * <p>
 * An implementation of IJSPTranslation.  
 * <br>
 * This object that holds the java translation of 
 * a JSP file as well as a mapping of ranges from the translated Java to the JSP source, 
 * and mapping from JSP source back to the translated Java.
 * </p>
 * 
 * <p>
 * You may also use JSPTranslation to do CompilationUnit-esque things such as:
 * <ul>
 * 	<li>code select (get java elements for jsp selection)</li>
 *  <li>reconcile</li>
 *  <li>get java regions for jsp selection</li>
 *  <li>get a JSP text edit based on a Java text edit</li>
 *  <li>determine if a java offset falls within a jsp:useBean range</li>
 *  <li>determine if a java offset falls within a jsp import statment</li>
 * </ul>
 * </p>
 * 
 * @author pavery
 */
public class JSPTranslation implements IJSPTranslation {
	
	// for debugging
	private static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	/** the name of the class (w/out extension) **/
	private String fClassname = ""; //$NON-NLS-1$
	private IJavaProject fJavaProject = null;
	private HashMap fJava2JspMap = null;
	private HashMap fJsp2JavaMap = null;
	private HashMap fJava2JspImportsMap = null;
	private HashMap fJava2JspUseBeanMap = null;
	private HashMap fJava2JspIndirectMap = null;
	
	// don't want to hold onto model (via translator)
	// all relevant info is extracted in the constructor.
	//private JSPTranslator fTranslator = null;
	private String fJavaText = ""; //$NON-NLS-1$
	private String fJspText = ""; //$NON-NLS-1$
	
	private ICompilationUnit fCompilationUnit = null;
	private IProgressMonitor fProgressMonitor = null;
	/** lock to synchronize access to the compilation unit **/
	private byte[] fLock = null;
	private String fMangledName;
	private String fJspName;
	private List fTranslationProblems;
	private Collection fIncludedPaths;
	
	/** the {@link JSPTranslator} used by this {@link JSPTranslator} */
	private JSPTranslator fTranslator;

	public JSPTranslation(IJavaProject javaProj, JSPTranslator translator) {

		fLock = new byte[0];
		fJavaProject = javaProj;
		fTranslator = translator;
		
		// can be null if it's an empty document (w/ NullJSPTranslation)
		initialize();
	}

	private void initialize() {
		if(fTranslator != null) {
			fJavaText = fTranslator.getTranslation().toString();
			fJspText = fTranslator.getJspText();
			fClassname = fTranslator.getClassname();
			fJava2JspMap = fTranslator.getJava2JspRanges();
			fJsp2JavaMap = fTranslator.getJsp2JavaRanges();
			fJava2JspImportsMap = fTranslator.getJava2JspImportRanges();
			fJava2JspUseBeanMap = fTranslator.getJava2JspUseBeanRanges();
			fJava2JspIndirectMap = fTranslator.getJava2JspIndirectRanges();
			fTranslationProblems = fTranslator.getTranslationProblems();
			fIncludedPaths = fTranslator.getIncludedPaths();
		}
	}

	/**
	 * @return {@link JSPTranslator} used by this {@link JSPTranslation}
	 */
	protected JSPTranslator getTranslator() {
		return this.fTranslator;
	}
	
	public IJavaProject getJavaProject() {
		return fJavaProject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.jsp.core.internal.java.IJSPTranslation#getJavaText()
	 */
	public String getJavaText() {
		//return (fTranslator != null) ? fTranslator.getTranslation().toString() : "";  //$NON-NLS-1$ 
		return fJavaText;
	}
	
	public String getJspText() {
		//return (fTranslator != null) ? fTranslator.getJspText() : "";  //$NON-NLS-1$
		return fJspText;
	}
	
	public String getJavaPath() {
		// create if necessary
		ICompilationUnit cu = getCompilationUnit();
		return (cu != null) ? cu.getPath().toString() : ""; //$NON-NLS-1$
	}
	
	/**
	 * 
	 * @return the corresponding Java offset for a give JSP offset
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
			if (!jspPos.includes(jspOffset) && !(jspPos.offset+jspPos.length == jspOffset))
				continue;

			offsetInRange = jspOffset - jspPos.offset;
			javaPos = (Position) fJsp2JavaMap.get(jspPos);
			if(javaPos != null)
				result = javaPos.offset + offsetInRange;
			else  {

				Logger.log(Logger.ERROR, "JavaPosition was null!" + jspOffset); //$NON-NLS-1$
			}
			break;
		}
		return result;
	}

	/**
	 * 
	 * @return the corresponding JSP offset for a give Java offset
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
			if (!javaPos.includes(javaOffset) && !(javaPos.offset+javaPos.length == javaOffset))
				continue;

			offsetInRange = javaOffset - javaPos.offset;
			jspPos = (Position) fJava2JspMap.get(javaPos);
			
			if(jspPos != null)
				result = jspPos.offset + offsetInRange;
			else  {
				Logger.log(Logger.ERROR, "jspPosition was null!" + javaOffset); //$NON-NLS-1$
			}
			break;
		}

		return result;
	}
		
	public Collection getIncludedPaths() {
		return fIncludedPaths;
	}

	/**
	 * 
	 * @return a map of Positions in the Java document to corresponding Positions in the JSP document
	 */
	public HashMap getJava2JspMap() {
		return fJava2JspMap;
	}

	/**
	 * 
	 * @return a map of Positions in the JSP document to corresponding Positions in the Java document
	 */
	public HashMap getJsp2JavaMap() {
		return fJsp2JavaMap;
	}

	/**
	 * Checks if the specified java range covers more than one partition in the JSP file.
	 * 
	 * <p>
	 * ex.
	 * <code>
	 * <%
	 * 	if(submit)
	 *  {
	 * %>
	 *    <p> print this...</p>
	 * 
	 * <%
	 *  }
	 *  else
	 *  {
	 * %>
	 * 	   <p> print that...</p>
	 * <%
	 *  }
	 * %>
	 * </code>
	 * </p>
	 * 
	 * the if else statement above spans 3 JSP partitions, so it would return true.
	 * @param offset
	 * @param length
	 * @return <code>true</code> if the java code spans multiple JSP partitions, otherwise false.
	 */
	public boolean javaSpansMultipleJspPartitions(int javaOffset, int javaLength) {
		HashMap java2jsp = getJava2JspMap();
		int count = 0;
		Iterator it = java2jsp.keySet().iterator();
		Position javaRange = null;
		while(it.hasNext()) {
			javaRange = (Position)it.next();
			if(javaRange.overlapsWith(javaOffset, javaLength))
				count++;
			if(count > 1)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the Java positions for the given range in the Java document.
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public Position[] getJavaRanges(int offset, int length) {

		List results = new ArrayList();
		Iterator it = getJava2JspMap().keySet().iterator();
		Position p = null;
		while(it.hasNext()) {
			p = (Position)it.next();
			if(p.overlapsWith(offset, length))
				results.add(p);
		}
		return (Position[])results.toArray(new Position[results.size()]);
	}
	
	/**
	 * Indicates if the java Offset falls within the user import ranges
	 * @param javaOffset
	 * @return true if the java Offset falls within the user import ranges, otherwise false
	 */
	public boolean isImport(int javaOffset) {
		return isInRanges(javaOffset, fJava2JspImportsMap);
	}

	/**
	 * Indicates if the java offset falls within the use bean ranges
	 * @param javaOffset
	 * @return true if the java offset falls within the user import ranges, otherwise false
	 */
	public boolean isUseBean(int javaOffset) {
		return isInRanges(javaOffset, fJava2JspUseBeanMap);
	}
	
	/**
	 * @param javaPos
	 * @return
	 */
	public boolean isIndirect(int javaOffset) {
		return isInRanges(javaOffset, fJava2JspIndirectMap, false);
	}
	
	private boolean isInRanges(int javaOffset, HashMap ranges) {
		return isInRanges(javaOffset, ranges, true);
	}
	/**
	 * Tells you if the given offset is included in any of the ranges (Positions) passed in.
	 * includeEndOffset tells whether or not to include the end offset of each range in the test.
	 * 
	 * @param javaOffset
	 * @param ranges
	 * @param includeEndOffset
	 * @return
	 */
	private boolean isInRanges(int javaOffset, HashMap ranges, boolean includeEndOffset) {
		
		Iterator it = ranges.keySet().iterator();
		while(it.hasNext()) {
			Position javaPos = (Position)it.next();
			// also include the start and end offset (only if requested)
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=81687
			if(javaPos.includes(javaOffset) || (includeEndOffset && javaPos.offset+javaPos.length == javaOffset)) 
				return true;
		}
		return false;
	}

	/**
	 * Return the Java CompilationUnit associated with this JSPTranslation (this particular model)
	 * When using methods on the CU, it's reccomended to synchronize on the CU for reliable
	 * results.
	 * 
	 * The method is synchronized to ensure that <code>createComplilationUnit</code> doesn't
	 * get entered 2 or more times simultaneously.  A side effect of that is 2 compilation units
	 * can be created in the JavaModelManager, but we only hold one reference to it in 
	 * fCompilationUnit.  This would lead to a leak since only one instance of the CU is
	 * discarded in the <code>release()</code> method.
	 * 
	 * @return a CompilationUnit representation of this JSPTranslation
	 */
	public ICompilationUnit getCompilationUnit() {
		synchronized(fLock) {
			try {
				if (fCompilationUnit == null) {
					fCompilationUnit = createCompilationUnit();
				}
			}
			catch (JavaModelException jme) {
				if(DEBUG)
					Logger.logException("error creating JSP working copy... ", jme); //$NON-NLS-1$
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

	/**
	 * Replaces mangled (servlet) name with jsp file name.
	 * 
	 * @param displayString
	 * @return
	 */
	public String fixupMangledName(String displayString) {
		
		if(displayString == null)
			return null;
		
		if(getJspName() == null || getMangledName() == null) {
			// names not set yet
			initJspAndServletNames();
		}
		return displayString.replaceAll(getMangledName(), getJspName());
	}

	private void initJspAndServletNames() {
		ICompilationUnit cu = getCompilationUnit();
		if(cu != null) {
			String cuName = null;
			synchronized(cu) {
				// set some names for fixing up mangled name in proposals
				// set mangled (servlet) name
				 cuName = cu.getPath().lastSegment();
			}
			if(cuName != null) {
				setMangledName(cuName.substring(0, cuName.lastIndexOf('.')));
				// set name of jsp file
				String unmangled = JSP2ServletNameUtil.unmangle(cuName);
				setJspName(unmangled.substring(unmangled.lastIndexOf('/') + 1, unmangled.lastIndexOf('.')));
			}
		}
	}

	
	/**
	 * Originally from ReconcileStepForJava.  Creates an ICompilationUnit from the contents of the JSP document.
	 * 
	 * @return an ICompilationUnit from the contents of the JSP document
	 */
	private ICompilationUnit createCompilationUnit() throws JavaModelException {
		
		IJavaProject je = getJavaProject();

		if (je == null || !je.exists())
			return null;
		
		final String name = getClassname() + ".java";
		IPackageFragmentRoot packageFragmentRoot = null;
		IPackageFragmentRoot[] packageFragmentRoots = je.getPackageFragmentRoots();
		for (int i = 0; i < packageFragmentRoots.length; i++) {
			if (!packageFragmentRoots[i].isArchive() && !packageFragmentRoots[i].isExternal()) {
				packageFragmentRoot = packageFragmentRoots[i];
				break;
			}
		}
		if (packageFragmentRoot == null) {
			if(DEBUG) {
				System.out.println("** Abort create working copy: cannot create working copy: JSP is not in a Java project with source package fragment root"); //$NON-NLS-1$
			}
			return null;
		}
		final IPackageFragment fragment = packageFragmentRoot.getPackageFragment(IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH);
		ICompilationUnit cu = fragment.getCompilationUnit(name).getWorkingCopy(getWorkingCopyOwner(), getProgressMonitor());
		setContents(cu);

		if(DEBUG) {
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
			System.out.println("(+) JSPTranslation ["+ this + "] finished creating CompilationUnit: " + cu); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
		}
		
		return cu;
	}

	/**
	 * 
	 * @return the problem requestor for the CompilationUnit in this JSPTranslation
	 */
	private JSPProblemRequestor getProblemRequestor() {
	    return CompilationUnitHelper.getInstance().getProblemRequestor();
	}

	/**
	 * 
	 * @return the IWorkingCopyOwner for this CompilationUnit in this JSPTranslation
	 */
	public WorkingCopyOwner getWorkingCopyOwner() {
	    return CompilationUnitHelper.getInstance().getWorkingCopyOwner();
	}

	/**
	 * 
	 * @return the progress monitor used in long operations (reconcile, creating the CompilationUnit...) in this JSPTranslation
	 */
	private IProgressMonitor getProgressMonitor() {
		if (fProgressMonitor == null)
			fProgressMonitor = new NullProgressMonitor();
		return fProgressMonitor;
	}

	/**
	 * 
	 * @return the List of problems collected during reconcile of the compilation unit
	 */
	public List getProblems() {
		List problems = getProblemRequestor().getCollectedProblems();
		if (problems != null) {
			if (fTranslationProblems.isEmpty()) {
				return problems;
			}
			List combinedProblems = null;
			synchronized (problems) {
				combinedProblems = new ArrayList(problems);
			}
			combinedProblems.addAll(fTranslationProblems);
			return combinedProblems;
		}
		return fTranslationProblems;
	}

	/**
	 * Must be set true in order for problems to be collected during reconcile.
	 * If set false, problems will be ignored during reconcile.
	 * @param collect
	 */
	public void setProblemCollectingActive(boolean collect) {
		ICompilationUnit cu = getCompilationUnit();
		if(cu != null) {	
			getProblemRequestor().setIsActive(collect);
		}
	}

	public void retranslate(JSPTranslator translator) {
		fTranslator = translator;
		initialize();

		synchronized (fLock) {
			setContents(getCompilationUnit());
		}
	}

	/**
	 * Reconciles the compilation unit for this JSPTranslation
	 */
	public void reconcileCompilationUnit() {
		ICompilationUnit cu = getCompilationUnit();
		if (cu != null) {
			try {
				synchronized(cu) {
					cu.makeConsistent(getProgressMonitor());
					cu.reconcile(ICompilationUnit.NO_AST, false, getWorkingCopyOwner(), getProgressMonitor());
				}
			}
			catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
	}

	/**
	 * Set contents of the compilation unit to the translated jsp text.
	 * @param the ICompilationUnit on which to set the buffer contents
	 */
	private void setContents(ICompilationUnit cu) {
		if (cu == null)
			return;
		
		synchronized (cu) {
			IBuffer buffer;
			try {
				
				buffer = cu.getBuffer();
			}
			catch (JavaModelException e) {
				e.printStackTrace();
				buffer = null;
			}
	
			if (buffer != null)
				buffer.setContents(getJavaText());
		}
	}

	/**
	 * Returns the IJavaElements corresponding to the JSP range in the JSP StructuredDocument
	 * 
	 * @param jspStart staring offset in the JSP document
	 * @param jspEnd ending offset in the JSP document
	 * @return IJavaElements corresponding to the JSP selection
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
					if (cuDocLength > 0 && javaPositionStart >= 0 && javaLength >= 0 && javaPositionEnd < cuDocLength) {
						result = cu.codeSelect(javaPositionStart, javaLength);
					}
				}
			}

			if (result == null || result.length == 0)
				return EMTPY_RESULT_SET;
		}
		catch (JavaModelException x) {
			Logger.logException(x);
		}

		return result;
	}

	public String getClassname() {
		return fClassname;
	}

	/**
	 * Must discard compilation unit, or else they can leak in the JavaModelManager
	 */
	public void release() {
		
		synchronized (fLock) {
			if (fCompilationUnit != null) {
				Job discarder = new WorkspaceJob(fClassname) {
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						try {
							if (DEBUG) {
								System.out.println("------------------------------------------------------------------"); //$NON-NLS-1$
								System.out.println("(-) JSPTranslation [" + this + "] discarding CompilationUnit: " + fCompilationUnit); //$NON-NLS-1$ //$NON-NLS-2$
								System.out.println("------------------------------------------------------------------"); //$NON-NLS-1$
							}
							fCompilationUnit.discardWorkingCopy();
						}
						catch (JavaModelException e) {
							// we're done w/ it anyway
						}
						return Status.OK_STATUS;
					}
				};
				discarder.schedule();
			}
		}
	}
}
