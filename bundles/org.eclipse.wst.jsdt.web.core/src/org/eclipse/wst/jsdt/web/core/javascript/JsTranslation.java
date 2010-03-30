/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     bug:244839 - eugene@genuitec.com
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *     
 *******************************************************************************/


package org.eclipse.wst.jsdt.web.core.javascript;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;
import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.internal.core.DocumentContextFragmentRoot;
import org.eclipse.wst.jsdt.internal.core.Member;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsTranslation implements IJsTranslation {

	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jstranslation"); //$NON-NLS-1$
 		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private IJavaScriptUnit fCompilationUnit = null;
	private DocumentContextFragmentRoot fDocumentScope;
	private IJavaScriptProject fJavaProject = null;
	private byte[] fLock = null;
	private IProgressMonitor fProgressMonitor = null;
	protected IStructuredDocument fHtmlDocument;
	protected String fModelBaseLocation;


//	private static final String SUPER_TYPE_NAME = "Window"; //$NON-NLS-1$
//	private static final String SUPER_TYPE_LIBRARY = "org.eclipse.wst.jsdt.launching.baseBrowserLibrary"; //$NON-NLS-1$

	protected IJsTranslator fTranslator;

	private String mangledName;
	protected boolean listenForChanges;

	public JsTranslation() {
		/* do nothing */
	}
	
	public IJsTranslator getTranslator() {
		if(fTranslator!=null) {
			return fTranslator;
		}
		
		fTranslator = new JsTranslator(fHtmlDocument, fModelBaseLocation, listenForChanges);
		return this.fTranslator;
	}
	

	
	protected JsTranslation(IStructuredDocument htmlDocument, IJavaScriptProject javaProj, boolean listenForChanges) {
		fLock = new byte[0];
		fJavaProject = javaProj;
		fHtmlDocument = htmlDocument;
		setBaseLocation();
		mangledName = createMangledName();
		this.listenForChanges=listenForChanges;
	}

	public IJsTranslation getInstance(IStructuredDocument htmlDocument, IJavaScriptProject javaProj, boolean listenForChanges) {
		return new JsTranslation(htmlDocument,javaProj, listenForChanges);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getJavaProject()
	 */
	public IJavaScriptProject getJavaProject() {
		return fJavaProject;
	}

	private IPackageFragmentRoot getDocScope(boolean reset) {
		if (fDocumentScope == null) {
			// IProject project = getJavaProject().getProject();
			// IResource absoluteRoot =
			// ((IContainer)getJavaProject().getResource()).findMember(
			// WebRootFinder.getWebContentFolder(fJavaProject.getProject()));
			fDocumentScope = new DocumentContextFragmentRoot(fJavaProject, getFile(), WebRootFinder.getWebContentFolder(fJavaProject.getProject()), WebRootFinder.getServerContextRoot(fJavaProject.getProject()), JsWebNature.VIRTUAL_SCOPE_ENTRY);
			fDocumentScope.setIncludedFiles(getTranslator().getRawImports());
			return fDocumentScope;
		}

		if (reset)
			fDocumentScope.setIncludedFiles(getTranslator().getRawImports());
		return fDocumentScope;
	}

	private void setBaseLocation() {
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fHtmlDocument);
			if (xmlModel == null) {
				xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(fHtmlDocument);
			}
			fModelBaseLocation = xmlModel.getBaseLocation();
		}
		finally {
			if (xmlModel != null)
				xmlModel.releaseFromRead();
		}
		// return xmlModel;
	}

	public IFile getFile() {
		return FileBuffers.getWorkspaceFileAtLocation(new Path(fModelBaseLocation));
	}


	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getHtmlDocument()
	 */
	public IDocument getHtmlDocument() {
		return fHtmlDocument;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getMissingTagStart()
	 */
	public int getMissingTagStart() {
		return getTranslator().getMissingEndTagRegionStart();
	}

	private String getWebRoot() {
		return WebRootFinder.getWebContentFolder(fJavaProject.getProject()).toString();
	}


	public String getDirectoryUnderRoot() {
		String webRoot = getWebRoot();
		IPath projectWebRootPath = getJavaProject().getPath().append(webRoot);
		IPath filePath = new Path(fModelBaseLocation).removeLastSegments(1);
		return filePath.removeFirstSegments(projectWebRootPath.matchingFirstSegments(filePath)).toString();
	}

	/**
	 * Originally from ReconcileStepForJava. Creates an IJavaScriptUnit from
	 * the contents of the JSP document.
	 * 
	 * @return an IJavaScriptUnit from the contents of the JSP document
	 */
	private IJavaScriptUnit createCompilationUnit() throws JavaScriptModelException {
		IPackageFragmentRoot root = getDocScope(true);
		IJavaScriptUnit cu = root.getPackageFragment("").getJavaScriptUnit(getMangledName() + JsDataTypes.BASE_FILE_EXTENSION).getWorkingCopy(getWorkingCopyOwner(), getProgressMonitor()); //$NON-NLS-1$
		IBuffer buffer;
		try {
			buffer = cu.getBuffer();
		}
		catch (JavaScriptModelException e) {
			e.printStackTrace();
			buffer = null;
		}
		if (buffer != null) {
			getTranslator().setBuffer(buffer);
		}
		return cu;
	}

	public String fixupMangledName(String displayString) {
		if (displayString == null) {
			return null;
		}
		return displayString.replaceAll(getMangledName() + ".js", getHtmlPageName()); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getAllElementsInJsRange(int, int)
	 */
	public IJavaScriptElement[] getAllElementsInJsRange(int javaPositionStart, int javaPositionEnd) {
		IJavaScriptElement[] EMTPY_RESULT_SET = new IJavaScriptElement[0];
		IJavaScriptElement[] result = EMTPY_RESULT_SET;
		IJavaScriptElement[] allChildren = null;
		try {
			allChildren = getCompilationUnit().getChildren();
		}
		catch (JavaScriptModelException e) {
		}
		Vector validChildren = new Vector();
		for (int i = 0; i < allChildren.length; i++) {
			ISourceRange range = getJSSourceRangeOf(allChildren[i]);
			if (javaPositionStart <= range.getOffset() && range.getLength() + range.getOffset() <= (javaPositionEnd)) {
				validChildren.add(allChildren[i]);
			}
			else if (allChildren[i].getElementType() == IJavaScriptElement.TYPE) {
				validChildren.add(allChildren[i]);
			}
		}
		if (validChildren.size() > 0) {
			result = (IJavaScriptElement[]) validChildren.toArray(new IJavaScriptElement[]{});
		}
		if (result == null || result.length == 0) {
			return EMTPY_RESULT_SET;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getCompilationUnit()
	 */
	public IJavaScriptUnit getCompilationUnit() {
        // Genuitec Begin Fix 6149: Exception opening external HTML file
	    if (!getJavaProject().exists()) {
	        return null;
	    }
	    // Genuitec End Fix 6149: Exception opening external HTML file
		synchronized (fLock) {
			try {
				if (fCompilationUnit == null) {
					fCompilationUnit = createCompilationUnit();
					return fCompilationUnit;
				}

			}
			catch (JavaScriptModelException jme) {
				if (JsTranslation.DEBUG) {
					Logger.logException("error creating JSP working copy... ", jme); //$NON-NLS-1$
				}
			}

		}
		getDocScope(true);
		try {
			fCompilationUnit = fCompilationUnit.getWorkingCopy(getWorkingCopyOwner(), getProgressMonitor());
			// fCompilationUnit.makeConsistent(getProgressMonitor());
		}
		catch (JavaScriptModelException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return fCompilationUnit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getElementsFromJsRange(int, int)
	 */
	public IJavaScriptElement[] getElementsFromJsRange(int javaPositionStart, int javaPositionEnd) {
		IJavaScriptElement[] EMTPY_RESULT_SET = new IJavaScriptElement[0];
		IJavaScriptElement[] result = EMTPY_RESULT_SET;
		try {
			IJavaScriptUnit cu = getCompilationUnit();
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
		}
		catch (JavaScriptModelException x) {
			Logger.logException(x);
		}
		return result;
	}

	private String getHtmlPageName() {
		IPath path = new Path(fModelBaseLocation);
		return path.lastSegment();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getHtmlText()
	 */
	public String getHtmlText() {
		return fHtmlDocument.get();
	}

	public String getJavaPath() {
		IPath rootPath = new Path(fModelBaseLocation).removeLastSegments(1);
		String cuPath = rootPath.append("/" + getMangledName() + JsDataTypes.BASE_FILE_EXTENSION).toString(); //$NON-NLS-1$
		return cuPath;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getJsElementAtOffset(int)
	 */
	public IJavaScriptElement getJsElementAtOffset(int jsOffset) {
		IJavaScriptElement elements = null;
		try {
			elements = getCompilationUnit().getElementAt(jsOffset);
		}
		catch (JavaScriptModelException e) {
			// TODO Auto-generated catch block
			if (JsTranslation.DEBUG) {
				Logger.logException("error retrieving java elemtnt from compilation unit... ", e); //$NON-NLS-1$
			}
			// }
		}
		return elements;
	}

	private ISourceRange getJSSourceRangeOf(IJavaScriptElement element) {
		// returns the offset in html of given element
		ISourceRange range = null;
		if (element instanceof Member) {
			try {
				range = ((Member) element).getNameRange();
			} catch (JavaScriptModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (element instanceof SourceRefElement) {
			try {
				range = ((SourceRefElement) element).getSourceRange();
			}
			catch (JavaScriptModelException e) {
				e.printStackTrace();
			}
		}
		return range;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getJsText()
	 */
	public String getJsText() {
		return getTranslator().getJsText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getScriptPositions()
	 */
	public Position[] getScriptPositions() {
		return getTranslator().getHtmlLocations();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#insertInFirstScriptRegion(java.lang.String)
	 */
	public void insertInFirstScriptRegion(String text) {
		Position pos[] = getScriptPositions();
		int scriptStartOffset = 0;
		if(pos!=null && pos.length>0) {
			scriptStartOffset = pos[0].getOffset();
			
		}
		String insertText = (scriptStartOffset==0?"":"\n") + text;
		insertScript(scriptStartOffset,insertText);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#insertScript(int, java.lang.String)
	 */
	public void insertScript(int offset, String text) {

		IDOMModel xmlModel = null;
		Position[] inHtml = getScriptPositions();
		boolean isInsideExistingScriptRegion = false;
		for (int i = 0; i < inHtml.length; i++) {
			if (inHtml[i].overlapsWith(offset, 1)) {
				// * inserting into a script region
				isInsideExistingScriptRegion = true;
			}
		}

		String insertText = null;

		if (isInsideExistingScriptRegion) {
			insertText = text;
		}
		else {
			insertText = offset != 0 ? "\n" : "" + "<script type=\"text/javascript\">\n" + text + "\n</script>\n";
		}
	//	translator.documentAboutToBeChanged(null);

		synchronized (fLock) {
			try {
				xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForEdit(fHtmlDocument);
				if (xmlModel == null) {
					xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(fHtmlDocument);
				}
				if (xmlModel != null) {


					xmlModel.aboutToChangeModel();
					xmlModel.getDocument().getStructuredDocument().replaceText(this, offset, 0, insertText);
					xmlModel.changedModel();
					try {
						xmlModel.save();
					}

					catch (UnsupportedEncodingException e) {}
					catch (IOException e) {}
					catch (CoreException e) {}
				}
			}
			finally {
				if (xmlModel != null)
					xmlModel.releaseFromEdit();
			}
		}

	//	translator.documentChanged(null);

	}

	public String getMangledName() {
		return this.mangledName;
	}

	private String createMangledName() {
		return JsNameManglerUtil.mangle(fModelBaseLocation);
	}

	/**
	 * 
	 * @return the problem requestor for the JavaScriptUnit in this
	 *         JsTranslation
	 */
	private JsProblemRequestor getProblemRequestor() {
		return CompilationUnitHelper.getInstance().getProblemRequestor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#getProblems()
	 */
	public List getProblems() {
		List problemList = getProblemRequestor().getCollectedProblems();
		getProblemRequestor().endReporting();
		IProblem[] problems = null;
		if (problemList == null)
			problems = new IProblem[0];
		else 
			problems = (IProblem[]) problemList.toArray(new IProblem[problemList.size()]);
		
		IJsTranslator translator = getTranslator();
		if (translator instanceof JsTranslator && problems.length > 0) {
			Region[] generatedRanges = ((JsTranslator) translator).getGeneratedRanges();
			for (int i = 0; i < problems.length; i++) {
				for (int j = 0; j < generatedRanges.length; j++) {
					// remove any problems that are fully reported within a region generated by the translator
					if (problems[i].getSourceStart() >= generatedRanges[j].getOffset() && problems[i].getSourceEnd() <= (generatedRanges[j].getOffset() + generatedRanges[j].getLength())) {
						problemList.remove(problems[i]);
					}
				}
			}
		}
		return problemList;
	}

	private IProgressMonitor getProgressMonitor() {
		if (fProgressMonitor == null) {
			fProgressMonitor = new NullProgressMonitor();
		}
		return fProgressMonitor;
	}

	public WorkingCopyOwner getWorkingCopyOwner() {
		return CompilationUnitHelper.getInstance().getWorkingCopyOwner();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#ifOffsetInImportNode(int)
	 */
	public boolean ifOffsetInImportNode(int offset) {
		Position[] importRanges = getTranslator().getImportHtmlRanges();
		for (int i = 0; i < importRanges.length; i++) {
			if (importRanges[i].includes(offset)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#reconcileCompilationUnit()
	 */
	public void reconcileCompilationUnit() {
		// if(true) return;
		IJavaScriptUnit cu = getCompilationUnit();
		if (fCompilationUnit == null) {
			return;
		}
		if (cu != null) {
			try {
				synchronized (fLock) {
					// clear out old validation messages
					WorkingCopyOwner workingCopyOwner = getWorkingCopyOwner();
					JsProblemRequestor problemRequestor = (JsProblemRequestor) workingCopyOwner.getProblemRequestor(cu.getWorkingCopy(getProgressMonitor()));
					if(problemRequestor != null && problemRequestor.getCollectedProblems() != null)
						problemRequestor.getCollectedProblems().clear();
					cu.reconcile(IJavaScriptUnit.NO_AST, true, true, getWorkingCopyOwner(), getProgressMonitor());
				}
			}
			catch (JavaScriptModelException e) {
				Logger.logException(e);
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#release()
	 */
	public void release() {
		if (getTranslator() != null)
			getTranslator().release();
		synchronized (fLock) {
			if (fCompilationUnit != null) {
				try {
					if (JsTranslation.DEBUG) {
						System.out.println("------------------------------------------------------------------"); //$NON-NLS-1$
						System.out.println("(-) JsTranslation [" + this + "] discarding JavaScriptUnit: " + fCompilationUnit); //$NON-NLS-1$ //$NON-NLS-2$
						System.out.println("------------------------------------------------------------------"); //$NON-NLS-1$
					}
					fCompilationUnit.discardWorkingCopy();
				}
				catch (JavaScriptModelException e) {
					// we're done w/ it anyway
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation#setProblemCollectingActive(boolean)
	 */
	public void setProblemCollectingActive(boolean collect) {
		IJavaScriptUnit cu = getCompilationUnit();
		if (cu != null) {
			getProblemRequestor().setIsActive(collect);
		}
	}

	public void classpathChange() {

		if (fDocumentScope != null) {
			fDocumentScope.classpathChange();
		}
	}
}