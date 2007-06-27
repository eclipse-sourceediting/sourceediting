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
import java.util.List;
import java.util.Vector;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.IPackageDeclaration;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;
import org.eclipse.wst.jsdt.internal.core.DocumentContextFragmentRoot;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author brad childs
 */
public class JsTranslation implements IJsTranslation {
	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	private ICompilationUnit fCompilationUnit = null;
	private DocumentContextFragmentRoot fDocumentScope;
	
	
	//private String fHtmlMangledPageName = ""; //$NON-NLS-1$
	/** the name of the class (w/out extension) * */
	//private String fHtmlPageName;
	//private String fHtmlText = ""; //$NON-NLS-1$
	private IJavaProject fJavaProject = null;
	//private String fJsText = ""; //$NON-NLS-1$
	/** lock to synchronize access to the compilation unit * */
	private byte[] fLock = null;
	private IProgressMonitor fProgressMonitor = null;
	//private Position[] importRanges;
	//private Position[] locationsInHtml;
	//private IFile targetFile;
	private IStructuredDocument fHtmlDocument;
	
	private static final String SUPER_TYPE_NAME = "Window";
	
	private JsTranslator translator;
	
	private String mangledName;
	
	public JsTranslation(IStructuredDocument htmlDocument, IJavaProject javaProj) {
		fLock = new byte[0];
		fJavaProject = javaProj;
		fHtmlDocument = htmlDocument;
	
		
		translator = new JsTranslator(getModel());
		resetDocScope();
		mangledName = createMangledName();
//		if (xmlModel != null) {
//			
//			IStructuredDocument doc = xmlModel.getStructuredDocument();
//			//translator.translate();
//			fHtmlMangledPageName = createClassname(xmlModel);
//			fJsText = translator.getTranslation().toString();
//			//fHtmlText = translator.getHtmlText();
//			targetFile = getFile(xmlModel);
//			fHtmlPageName = targetFile.getName();
//			
//			fDocumentScope = new DocumentContextFragmentRoot(fJavaProject, targetFile, WebRootFinder.getWebContentFolder(javaProj.getProject()), WebRootFinder.getServerContextRoot(javaProj.getProject()), JsWebNature.VIRTUAL_SCOPE_ENTRY);
//			importRanges = translator.getImportHtmlRanges();
//			cuImports = translator.getRawImports();
//			locationsInHtml = translator.getHtmlLocations();
//			//fHtmlDocument = translator.getStructuredDocument();
//		}
//		
	}	
	
	private void resetDocScope() {
		if(fDocumentScope==null) {
			fDocumentScope = new DocumentContextFragmentRoot(fJavaProject, getFile(), WebRootFinder.getWebContentFolder(fJavaProject.getProject()), WebRootFinder.getServerContextRoot(fJavaProject.getProject()), JsWebNature.VIRTUAL_SCOPE_ENTRY);
		}
		
		fDocumentScope.setIncludedFiles(translator.getRawImports());
	}
	
	private IDOMModel getModel() {
		IDOMModel xmlModel=null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fHtmlDocument);
			if(xmlModel==null) {
				xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(fHtmlDocument);
			}
		}finally {
			if(xmlModel!=null) xmlModel.releaseFromRead();
		}
		return xmlModel;
	}
		
	public IFile getFile() {
			return FileBuffers.getWorkspaceFileAtLocation(new Path(getModel().getBaseLocation()));
	}
		
	
	public IDocument getHtmlDocument() {
		return fHtmlDocument;
	}

	/**
	 * Originally from ReconcileStepForJava. Creates an ICompilationUnit from
	 * the contents of the JSP document.
	 * 
	 * @return an ICompilationUnit from the contents of the JSP document
	 */
	private ICompilationUnit createCompilationUnit() throws JavaModelException {
		System.out.println("------------------------- CREATING CU ----------------------------");
		
		ICompilationUnit cu = fDocumentScope.getDefaultPackageFragment().getCompilationUnit(getMangledName() + JsDataTypes.BASE_FILE_EXTENSION, SUPER_TYPE_NAME).getWorkingCopy(getWorkingCopyOwner(), getProblemRequestor(), getProgressMonitor());
		
		
		IBuffer buffer;
		try {
			buffer = cu.getBuffer();
		} catch (JavaModelException e) {
			e.printStackTrace();
			buffer = null;
		}
		if (buffer != null) {
			translator.setBuffer(buffer);
		}
		cu.makeConsistent(getProgressMonitor());
		// cu.reconcile(ICompilationUnit.NO_AST, true, getWorkingCopyOwner(),
		// getProgressMonitor());
//		if (getHtmlPageName() == null || getMangledName() == null) {
//			String cuName = cu.getPath().lastSegment();
//			if (cuName != null) {
//				//fHtmlMangledPageName = cuName.substring(0, cuName.lastIndexOf('.'));
//				// set name of jsp file
//				String unmangled = JsNameManglerUtil.unmangle(cuName);
//				//fHtmlPageName = unmangled.substring(unmangled.lastIndexOf('/') + 1, unmangled.length());
//			}
//		}
//		if (JsTranslation.DEBUG) {
//			String cuText = cu.toString();
//			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
//			System.out.println("(+) JSPTranslation [" + this + "] finished creating CompilationUnit: " + cu); //$NON-NLS-1$ //$NON-NLS-2$
//			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); //$NON-NLS-1$
//			IPackageDeclaration[] ipd = cu.getPackageDeclarations();
//			for (int i = 0; i < ipd.length; i++) {
//				System.out.println("JSPTranslation.getCU() Package:" + ipd[i].getElementName());
//			}
//		}
		return cu;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#fixupMangledName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#fixupMangledName(java.lang.String)
	 */
	public String fixupMangledName(String displayString) {
		if (displayString == null) {
			return null;
		}
		return displayString.replaceAll(getMangledName() + ".js", getHtmlPageName());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getAllElementsInJsRange(int,
	 *      int)
	 */
	public IJavaElement[] getAllElementsInJsRange(int javaPositionStart, int javaPositionEnd) {
		IJavaElement[] EMTPY_RESULT_SET = new IJavaElement[0];
		IJavaElement[] result = EMTPY_RESULT_SET;
		IJavaElement[] allChildren = null;
		try {
			allChildren = getCompilationUnit().getChildren();
		} catch (JavaModelException e) {
		}
		Vector validChildren = new Vector();
		for (int i = 0; i < allChildren.length; i++) {
			if (allChildren[i] instanceof IJavaElement && allChildren[i].getElementType() != IJavaElement.PACKAGE_DECLARATION) {
				ISourceRange range = getJSSourceRangeOf(allChildren[i]);
				if (javaPositionStart <= range.getOffset() && range.getLength() + range.getOffset() <= (javaPositionEnd)) {
					validChildren.add(allChildren[i]);
				} else if (allChildren[i].getElementType() == IJavaElement.TYPE) {
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
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getCompilationUnit()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getCompilationUnit()
	 */
	public ICompilationUnit getCompilationUnit() {
		synchronized (fLock) {
			try {
				if (fCompilationUnit == null) {
					fCompilationUnit = createCompilationUnit();
					return fCompilationUnit;
				}
				// reconcileCompilationUnit();
				
			} catch (JavaModelException jme) {
				if (JsTranslation.DEBUG) {
					Logger.logException("error creating JSP working copy... ", jme); //$NON-NLS-1$
				}
			}
			
		}
		resetDocScope();
		try {
			fCompilationUnit = fCompilationUnit.getWorkingCopy(getWorkingCopyOwner(), getProblemRequestor(), getProgressMonitor());
			fCompilationUnit.makeConsistent(getProgressMonitor());
		} catch (JavaModelException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return fCompilationUnit;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getElementsFromJspRange(int,
	 *      int)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getElementsFromJsRange(int,
	 *      int)
	 */
	public IJavaElement[] getElementsFromJsRange(int javaPositionStart, int javaPositionEnd) {
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
	
	private String getHtmlPageName() {
		IPath path = new Path(getModel().getBaseLocation());
		return path.lastSegment();
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJspText()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getHtmlText()
	 */
	public String getHtmlText() {
		return fHtmlDocument.get();
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getJavaPath()
	 */
	public String getJavaPath() {
		// create if necessary
		ICompilationUnit cu = getCompilationUnit();
		return (cu != null) ? cu.getPath().toString() : ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getJsElementAtOffset(int)
	 */
	public IJavaElement getJsElementAtOffset(int jsOffset) {
		IJavaElement elements = null;
		// Position[] positions = getJavaRanges(htmlOffset, length);
		//        
		// ICompilationUnit cu = getCompilationUnit();
		// synchronized (cu) {
		try {
			elements = getCompilationUnit().getElementAt(jsOffset);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			if (JsTranslation.DEBUG) {
				Logger.logException("error retrieving java elemtnt from compilation unit... ", e); //$NON-NLS-1$
			}
			// }
		}
		return elements;
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
	
	public String getJsText() {
		// return (fTranslator != null) ?
		// fTranslator.getTranslation().toString(): ""; //$NON-NLS-1$
		return translator.getJsText();
	}
	
	public String getMangledName() {
		return this.mangledName;
	}
	
	private String createMangledName() {
		String classname = ""; //$NON-NLS-1$
		
		String base = getModel().getBaseLocation();
		classname = JsNameManglerUtil.mangle(base);
		
		return classname;
	}
	
	/**
	 * 
	 * @return the problem requestor for the CompilationUnit in this
	 *         JSPTranslation
	 */
	private JsProblemRequestor getProblemRequestor() {
		return CompilationUnitHelper.getInstance().getProblemRequestor();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getProblems()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getProblems()
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getWorkingCopyOwner()
	 */
	public WorkingCopyOwner getWorkingCopyOwner() {
		return CompilationUnitHelper.getInstance().getWorkingCopyOwner();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#ifOffsetInImportNode(int)
	 */
	public boolean ifOffsetInImportNode(int offset) {
		Position[] importRanges = translator.getImportHtmlRanges();
		for (int i = 0; i < importRanges.length; i++) {
			if (importRanges[i].includes(offset)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#isOffsetInScriptNode(int)
	 */
//	public boolean isOffsetInScriptNode(int offset) {
//		/* check import nodes */
//		for (int i = 0; i < locationsInHtml.length; i++) {
//			if (locationsInHtml[i].includes(offset)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#reconcileCompilationUnit()
	 */
	public void reconcileCompilationUnit() {
		// if(true) return;
		ICompilationUnit cu = getCompilationUnit();
		if (fCompilationUnit == null) {
			return;
		}
		if (cu != null) {
			try {
				synchronized (fLock) {
					// if(false)
					//cu.makeConsistent(getProgressMonitor());
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#release()
	 */
	public void release() {
		synchronized (fLock) {
			if (fCompilationUnit != null) {
				try {
					if (JsTranslation.DEBUG) {
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#setProblemCollectingActive(boolean)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#setProblemCollectingActive(boolean)
	 */
	public void setProblemCollectingActive(boolean collect) {
		ICompilationUnit cu = getCompilationUnit();
		if (cu != null) {
			getProblemRequestor().setIsActive(collect);
		}
	}
}