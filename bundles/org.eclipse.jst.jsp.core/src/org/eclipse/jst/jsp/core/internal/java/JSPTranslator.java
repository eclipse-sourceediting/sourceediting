/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import javax.servlet.jsp.tagext.VariableInfo;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache.PropertyGroup;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelper;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelperManager;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibVariable;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.internal.util.ZeroStructuredDocumentRegion;
import org.eclipse.jst.jsp.core.jspel.IJSPELTranslator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.html.core.internal.contentmodel.JSP20Namespace;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.TagMarker;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.parser.ContextRegionContainer;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocumentTracker;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.util.StringTokenizer;

/**
 * Translates a JSP document into a HttpServlet. Keeps two way mapping from
 * Java translation to the original JSP source, which can be obtained through
 * getJava2JspRanges() and getJsp2JavaRanges().
 */
public class JSPTranslator {

	// the name of the element in the extension point
	private static final String EL_TRANSLATOR_EXTENSION_NAME = "elTranslator"; //$NON-NLS-1$

	private static final String ELTRANSLATOR_PROP_NAME = "ELTranslator"; //$NON-NLS-1$

	// Default EL Translator
	private static final String DEFAULT_JSP_EL_TRANSLATOR_ID = "org.eclipse.jst.jsp.defaultJSP20"; //$NON-NLS-1$

	// handy plugin ID constant
	private static final String JSP_CORE_PLUGIN_ID = "org.eclipse.jst.jsp.core"; //$NON-NLS-1$

	// for debugging
	private static final boolean DEBUG = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspjavamapping")); //$NON-NLS-1$  //$NON-NLS-2$
	private static final boolean DEBUG_SAVE_OUTPUT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslationstodisk")); //$NON-NLS-1$  //$NON-NLS-2$

	private IJSPELTranslator fELTranslator = null;

	public static final String ENDL = "\n"; //$NON-NLS-1$

	String fClassHeader = null;
	String fClassname = null;

	String fImplicitImports = null;

	String fServiceHeader = null;

	private String fSessionVariableDeclaration = "javax.servlet.http.HttpSession session = null;" + ENDL; //$NON-NLS-1$
	private String fFooter = "}}"; //$NON-NLS-1$
	private String fException = "Throwable exception = null;"; //$NON-NLS-1$
	public static final String EXPRESSION_PREFIX = "out.print(\"\"+"; //$NON-NLS-1$
	public static final String EXPRESSION_SUFFIX = ");"; //$NON-NLS-1$
	String fSuperclass = null;

	private String fTryCatchStart = ENDL + "try {" + ENDL; //$NON-NLS-1$
	private List fTranslationProblems = new ArrayList();
	private String fTryCatchEnd = " } catch (java.lang.Exception e) {} " + ENDL; //$NON-NLS-1$

	/** fSourcePosition = position in JSP source */
	private int fSourcePosition = -1;
	/** fRelativeOffest = offset in the buffer there the cursor is */
	private int fRelativeOffset = -1;
	/** fCursorPosition = offset in the translated java document */
	private int fCursorPosition = -1;

	/** some page directive attributes */
	private boolean fIsErrorPage, fCursorInExpression = false;
	private boolean fIsInASession = true;

	/** user java code in body of the service method */
	private StringBuffer fUserCode = new StringBuffer();
	/** user EL Expression */
	private StringBuffer fUserELExpressions = new StringBuffer();
	/** user defined vars declared in the beginning of the class */
	private StringBuffer fUserDeclarations = new StringBuffer();

	/** user defined imports */
	private StringBuffer fUserImports = new StringBuffer();

	/**
	 * A map of tag names to tag library variable information; used to store
	 * the ones needed for AT_END variable support.
	 */
	private StackMap fTagToVariableMap = null;

	private StringBuffer fResult; // the final traslated java document
	// string buffer
	private StringBuffer fCursorOwner = null; // the buffer where the cursor
	// is

	private IDOMModel fStructuredModel = null;
	private IStructuredDocument fStructuredDocument = null;
	private IStructuredDocumentRegion fCurrentNode;
	private boolean fInCodeRegion = false; // flag for if cursor is in the
	// current region being translated

	/**
	 * these constants are to keep track of whether the code in question is
	 * embedded (JSP as an attribute or within comment tags) or is just
	 * standard JSP code, or identifies if it's an expression
	 */
	protected final static int STANDARD_JSP = 0;
	protected final static int EMBEDDED_JSP = 1;
	protected final static int DECLARATION = 2;
	protected final static int EXPRESSION = 4;
	protected final static int SCRIPTLET = 8;

	/** used to avoid infinite looping include files */
	private Stack fIncludes = null;
	private Set fIncludedPaths = new HashSet(2);
	private boolean fProcessIncludes = true;
	/** mostly for helper classes, so they parse correctly */
	private ArrayList fBlockMarkers = null;
	/**
	 * for keeping track of offset in user buffers while document is being
	 * built
	 */
	private int fOffsetInUserImports = 0;
	private int fOffsetInUserDeclarations = 0;
	private int fOffsetInUserCode = 0;

	/** correlates ranges (positions) in java to ranges in jsp */
	private HashMap fJava2JspRanges = new HashMap();

	/**
	 * map of ranges in fUserImports (relative to the start of the buffer) to
	 * ranges in source JSP buffer.
	 */
	private HashMap fImportRanges = new HashMap();
	/**
	 * map of ranges in fUserCode (relative to the start of the buffer) to
	 * ranges in source JSP buffer.
	 */
	private HashMap fCodeRanges = new HashMap();
	/**
	 * map of ranges in fUserDeclarations (relative to the start of the
	 * buffer) to ranges in source JSP buffer.
	 */
	private HashMap fDeclarationRanges = new HashMap();

	private HashMap fUseBeanRanges = new HashMap();

	private HashMap fUserELRanges = new HashMap();

	/**
	 * ranges that don't directly map from java code to JSP code (eg.
	 * <%@include file="included.jsp"%>
	 */
	private HashMap fIndirectRanges = new HashMap();

	private IProgressMonitor fProgressMonitor = null;

	/**
	 * save JSP document text for later use may just want to read this from
	 * the file or strucdtured document depending what is available
	 */
	private StringBuffer fJspTextBuffer = new StringBuffer();


	/**
	 * List of EL problems to be translated
	 */
	private ArrayList fELProblems = new ArrayList();

	/**
	 * EL Translator ID
	 */
	private String fELTranslatorID;

	public JSPTranslator() {
		super();
		init();
	}

	/**
	 * configure using an XMLNode
	 * 
	 * @param node
	 * @param monitor
	 */
	private void configure(IDOMNode node, IProgressMonitor monitor) {

		fProgressMonitor = monitor;
		fStructuredModel = node.getModel();
		String baseLocation = fStructuredModel.getBaseLocation();

		fELTranslatorID = getELTranslatorProperty(baseLocation);

		fStructuredDocument = fStructuredModel.getStructuredDocument();

		String className = createClassname(node);
		if (className.length() > 0) {
			setClassname(className);
			fClassHeader = "public class " + className + " extends "; //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * memory saving configure (no StructuredDocument in memory) currently
	 * doesn't handle included files
	 * 
	 * @param jspFile
	 * @param monitor
	 */
	private void configure(IFile jspFile, IProgressMonitor monitor) {
		// when configured on a file
		// fStructuredModel, fPositionNode, fModelQuery, fStructuredDocument
		// are all null
		fProgressMonitor = monitor;

		fELTranslatorID = getELTranslatorProperty(jspFile);

		String className = createClassname(jspFile);
		if (className.length() > 0) {
			setClassname(className);
			fClassHeader = "public class " + className + " extends "; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Set the jsp text from an IFile
	 * 
	 * @param jspFile
	 */
	private void setJspText(IFile jspFile) {
		try {
			BufferedInputStream in = new BufferedInputStream(jspFile.getContents());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fJspTextBuffer.append(line);
				fJspTextBuffer.append(ENDL);
			}
			reader.close();
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
	}

	/**
	 * Get the value of the ELTranslator property from a workspace relative
	 * path string
	 * 
	 * @param baseLocation
	 *            Workspace-relative string path
	 * @return Value of the ELTranslator property associated with the project.
	 */
	private String getELTranslatorProperty(String baseLocation) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		String elTranslatorValue = null;
		IFile file = workspaceRoot.getFile(new Path(baseLocation));
		if (file != null) {
			elTranslatorValue = getELTranslatorProperty(file);
		}
		return elTranslatorValue;
	}

	/**
	 * Get the value of the ELTranslator property from an IFile
	 * 
	 * @param file
	 *            IFile
	 * @return Value of the ELTranslator property associated with the project.
	 */
	private String getELTranslatorProperty(IFile file) {
		String elTranslatorValue = null;
		if (file != null) {
			if (file.exists()) {
				try {
					elTranslatorValue = file.getPersistentProperty(new QualifiedName(JSP_CORE_PLUGIN_ID, ELTRANSLATOR_PROP_NAME));
					if (null == elTranslatorValue) {

						elTranslatorValue = file.getProject().getPersistentProperty(new QualifiedName(JSP_CORE_PLUGIN_ID, ELTRANSLATOR_PROP_NAME));
					}
				}
				catch (CoreException e) {
					// ISSUE: why do we log this here? Instead of allowing to
					// throwup?
					Logger.logException(e);
				}

			}
		}
		return elTranslatorValue;
	}

	/**
	 * @param node
	 * @return
	 */
	private String createClassname(IDOMNode node) {
		String classname = ""; //$NON-NLS-1$
		if (node != null) {
			String base = node.getModel().getBaseLocation();
			classname = JSP2ServletNameUtil.mangle(base);
		}
		return classname;
	}

	/**
	 * @param jspFile
	 * @return
	 */
	private String createClassname(IFile jspFile) {

		String classname = ""; //$NON-NLS-1$
		if (jspFile != null) {
			classname = JSP2ServletNameUtil.mangle(jspFile.getFullPath().toString());
		}
		return classname;
	}

	private IJSPProblem createJSPProblem(final int problemEID, final int problemID, final String message, final int start, final int end) {
		final int line = fStructuredDocument.getLineOfOffset(start);
		final char[] classname = fClassname.toCharArray();

		/*
		 * Note: these problems would result in translation errors on the
		 * server, so the severity is not meant to be controllable
		 */
		return new IJSPProblem() {
			public void setSourceStart(int sourceStart) {
			}

			public void setSourceLineNumber(int lineNumber) {
			}

			public void setSourceEnd(int sourceEnd) {
			}

			public boolean isWarning() {
				return false;
			}

			public boolean isError() {
				return true;
			}

			public int getSourceStart() {
				return start;
			}

			public int getSourceLineNumber() {
				return line;
			}

			public int getSourceEnd() {
				return end;
			}

			public char[] getOriginatingFileName() {
				return classname;
			}

			public String getMessage() {
				return message;
			}

			public int getID() {
				return problemID;
			}

			public String[] getArguments() {
				return new String[0];
			}

			public int getEID() {
				return problemEID;
			}
		};
	}

	public void setClassname(String classname) {
		this.fClassname = classname;
	}

	public String getClassname() {
		return this.fClassname != null ? this.fClassname : "GenericJspServlet"; //$NON-NLS-1$
	}

	/**
	 * So that the JSPTranslator can be reused.
	 */
	public void reset(IDOMNode node, IProgressMonitor progress) {

		// initialize some things on node
		configure(node, progress);
		reset();
		// set the jsp text buffer
		fJspTextBuffer.append(fStructuredDocument.get());
	}

	/**
	 * conservative version (no StructuredDocument/Model)
	 * 
	 * @param jspFile
	 * @param progress
	 */
	public void reset(IFile jspFile, IProgressMonitor progress) {

		// initialize some things on node
		configure(jspFile, progress);
		reset();
		// set the jsp text buffer
		setJspText(jspFile);
	}

	/**
	 * Reinitialize some fields
	 */
	private void reset() {

		// reset progress monitor
		if (fProgressMonitor != null)
			fProgressMonitor.setCanceled(false);

		// reinit fields
		fSourcePosition = -1;
		fRelativeOffset = -1;
		fCursorPosition = -1;

		fIsErrorPage = fCursorInExpression = false;

		fUserCode = new StringBuffer();
		fUserDeclarations = new StringBuffer();
		fUserImports = new StringBuffer();
		fUserELExpressions = new StringBuffer();

		fResult = null;
		fCursorOwner = null; // the buffer where the cursor is

		fCurrentNode = null;
		fInCodeRegion = false; // flag for if cursor is in the current region
		// being translated

		if (fIncludes != null)
			fIncludes.clear();

		fBlockMarkers = null;

		fOffsetInUserImports = 0;
		fOffsetInUserDeclarations = 0;
		fOffsetInUserCode = 0;

		fJava2JspRanges.clear();
		fImportRanges.clear();
		fCodeRanges.clear();
		fUseBeanRanges.clear();
		fDeclarationRanges.clear();
		fUserELRanges.clear();
		fIndirectRanges.clear();
		fIncludedPaths.clear();

		fJspTextBuffer = new StringBuffer();

		fELProblems = new ArrayList();

	}

	/**
	 * @return just the "shell" of a servlet, nothing contributed from the JSP
	 *         doc
	 */
	public final StringBuffer getEmptyTranslation() {
		reset();
		buildResult();
		return getTranslation();
	}

	/**
	 * put the final java document together
	 */
	private final void buildResult() {
		// to build the java document this is the order:
		// 
		// + default imports
		// + user imports
		// + class header
		// [+ error page]
		// + user declarations
		// + service method header
		// + try/catch start
		// + user code
		// + try/catch end
		// + service method footer
		fResult = new StringBuffer(fImplicitImports.length() + fUserImports.length() + fClassHeader.length() + fUserDeclarations.length() + fServiceHeader.length() + fTryCatchStart.length() // try/catch
					// start
					+ fUserCode.length() + fTryCatchEnd.length() // try/catch
					// end
					+ fFooter.length());

		int javaOffset = 0;

		fResult.append(fImplicitImports);
		javaOffset += fImplicitImports.length();

		// updateRanges(fIndirectImports, javaOffset);
		updateRanges(fImportRanges, javaOffset);
		// user imports
		append(fUserImports);
		javaOffset += fUserImports.length();

		// class header
		fResult.append(fClassHeader); //$NON-NLS-1$
		javaOffset += fClassHeader.length();
		fResult.append(fSuperclass + "{" + ENDL); //$NON-NLS-1$
		javaOffset += fSuperclass.length() + 2;

		updateRanges(fDeclarationRanges, javaOffset);
		// user declarations
		append(fUserDeclarations);
		javaOffset += fUserDeclarations.length();

		updateRanges(fUserELRanges, javaOffset);
		append(fUserELExpressions);
		javaOffset += fUserELExpressions.length();

		fResult.append(fServiceHeader);
		javaOffset += fServiceHeader.length();
		// session participant
		if (fIsInASession) {
			fResult.append(fSessionVariableDeclaration);
			javaOffset += fSessionVariableDeclaration.length();
		}
		// error page
		if (fIsErrorPage) {
			fResult.append(fException);
			javaOffset += fException.length();
		}


		fResult.append(fTryCatchStart);
		javaOffset += fTryCatchStart.length();

		updateRanges(fCodeRanges, javaOffset);

		// user code
		append(fUserCode);
		javaOffset += fUserCode.length();


		fResult.append(fTryCatchEnd);
		javaOffset += fTryCatchEnd.length();

		// footer
		fResult.append(fFooter);
		javaOffset += fFooter.length();

		fJava2JspRanges.putAll(fImportRanges);
		fJava2JspRanges.putAll(fDeclarationRanges);
		fJava2JspRanges.putAll(fCodeRanges);
		fJava2JspRanges.putAll(fUserELRanges);

	}

	/**
	 * @param javaRanges
	 * @param offsetInJava
	 */
	private void updateRanges(HashMap rangeMap, int offsetInJava) {
		// just need to update java ranges w/ the offset we now know
		Iterator it = rangeMap.keySet().iterator();
		while (it.hasNext())
			((Position) it.next()).offset += offsetInJava;
	}

	/**
	 * map of ranges (positions) in java document to ranges in jsp document
	 * 
	 * @return a map of java positions to jsp positions.
	 */
	public HashMap getJava2JspRanges() {
		return fJava2JspRanges;
	}

	/**
	 * map of ranges in jsp document to ranges in java document.
	 * 
	 * @return a map of jsp positions to java positions, or null if no
	 *         translation has occured yet (the map hasn't been built).
	 */
	public HashMap getJsp2JavaRanges() {
		if (fJava2JspRanges == null)
			return null;
		HashMap flipFlopped = new HashMap();
		Iterator keys = fJava2JspRanges.keySet().iterator();
		Object range = null;
		while (keys.hasNext()) {
			range = keys.next();
			flipFlopped.put(fJava2JspRanges.get(range), range);
		}
		return flipFlopped;
	}

	public HashMap getJava2JspImportRanges() {
		return fImportRanges;
	}

	public HashMap getJava2JspUseBeanRanges() {
		return fUseBeanRanges;
	}

	public HashMap getJava2JspIndirectRanges() {
		return fIndirectRanges;
	}

	/**
	 * Adds to the jsp<->java map by default
	 * 
	 * @param value
	 *            a comma delimited list of imports
	 */
	protected void addImports(String value) {
		addImports(value, true);
	}

	/**
	 * Pass in a comma delimited list of import values, appends each to the
	 * final result buffer
	 * 
	 * @param value
	 *            a comma delimited list of imports
	 */
	protected void addImports(String value, boolean addToMap) {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=81687
		// added the "addToMap" parameter to exclude imports originating
		// from included JSP files to be added to the jsp<->java mapping
		StringTokenizer st = new StringTokenizer(value, ",", false); //$NON-NLS-1$
		String tok = ""; //$NON-NLS-1$
		// String appendage = ""; //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			tok = st.nextToken();
			appendImportToBuffer(tok, fCurrentNode, addToMap);
		}
	}

	/**
	 * /* keep track of cursor position inside the buffer /* appends buffer to
	 * the final result buffer
	 */
	protected void append(StringBuffer buf) {
		if (getCursorOwner() == buf) {
			fCursorPosition = fResult.length() + getRelativeOffset();
		}
		fResult.append(buf.toString());
	}

	/**
	 * Only valid after a configure(...), translate(...) or
	 * translateFromFile(...) call
	 * 
	 * @return the current result (java translation) buffer
	 */
	public final StringBuffer getTranslation() {

		if (DEBUG) {
			StringBuffer debugString = new StringBuffer();
			try {
				Iterator it = fJava2JspRanges.keySet().iterator();
				while (it.hasNext()) {
					debugString.append("--------------------------------------------------------------\n"); //$NON-NLS-1$
					Position java = (Position) it.next();
					debugString.append("Java range:[" + java.offset + ":" + java.length + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					debugString.append("[" + fResult.toString().substring(java.offset, java.offset + java.length) + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
					debugString.append("--------------------------------------------------------------\n"); //$NON-NLS-1$
					debugString.append("|maps to...|\n"); //$NON-NLS-1$
					debugString.append("==============================================================\n"); //$NON-NLS-1$
					Position jsp = (Position) fJava2JspRanges.get(java);
					debugString.append("JSP range:[" + jsp.offset + ":" + jsp.length + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					debugString.append("[" + fJspTextBuffer.toString().substring(jsp.offset, jsp.offset + jsp.length) + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
					debugString.append("==============================================================\n"); //$NON-NLS-1$
					debugString.append("\n"); //$NON-NLS-1$
					debugString.append("\n"); //$NON-NLS-1$
				}
			}
			catch (Exception e) {
				Logger.logException("JSPTranslation error", e); //$NON-NLS-1$
			}
			Logger.log(Logger.INFO_DEBUG, debugString.toString());
		}

		if (DEBUG_SAVE_OUTPUT) {
			IProject project = getFile().getProject();
			String shortenedClassname = StringUtils.replace(getFile().getName(), ".", "_");
			String filename = shortenedClassname + ".java";
			IPath path = project.getFullPath().append("src/" + filename);
			try {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (!file.exists()) {
					file.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
				}
				ITextFileBufferManager textFileBufferManager = FileBuffers.getTextFileBufferManager();
				textFileBufferManager.connect(path, new NullProgressMonitor());
				ITextFileBuffer javaOutputBuffer = textFileBufferManager.getTextFileBuffer(path);
				javaOutputBuffer.getDocument().set(StringUtils.replace(fResult.toString(), getClassname(), shortenedClassname));
				javaOutputBuffer.commit(new NullProgressMonitor(), true);
				textFileBufferManager.disconnect(path, new NullProgressMonitor());
			}
			catch (Exception e) {
				// this is just for debugging, ignore
			}
			System.out.println("Updated translation: " + path);
		}

		return fResult;
	}

	public List getTranslationProblems() {
		return fTranslationProblems;
	}

	/**
	 * Only valid after a configure(...), translate(...) or
	 * translateFromFile(...) call
	 * 
	 * @return the text in the JSP file
	 */
	public final String getJspText() {
		return fJspTextBuffer.toString();
	}

	/**
	 * @deprecated
	 * @param tagToAdd
	 */
	protected void addTaglibVariables(String tagToAdd) {
		addTaglibVariables(tagToAdd, getCurrentNode());
	}

	protected void addTaglibVariables(String tagToAdd, ITextRegionCollection customTag) {
		IFile f = getFile();

		if (f == null || !f.exists())
			return;

		TaglibHelper helper = TaglibHelperManager.getInstance().getTaglibHelper(f);
		/*
		 * Variables can declare as available when NESTED, AT_BEGIN, or
		 * AT_END. For AT_END variables, store the entire list of variables in
		 * the map field so it can be used on the end tag.
		 */
		String decl = ""; //$NON-NLS-1$
		if (customTag.getFirstRegion().getType().equals(DOMRegionContext.XML_TAG_OPEN)) {
			TaglibVariable[] taglibVars = helper.getTaglibVariables(tagToAdd, getStructuredDocument(), customTag);
			fTranslationProblems.addAll(helper.getProblems(f.getFullPath()));
			/*
			 * These loops are duplicated intentionally to keep the nesting
			 * scoped variables from interfering with the others
			 */
			for (int i = 0; i < taglibVars.length; i++) {
				if (taglibVars[i].getScope() == VariableInfo.AT_BEGIN) {
					decl = taglibVars[i].getDeclarationString();
					appendToBuffer(decl, fUserCode, false, fCurrentNode);
				}
				if (taglibVars[i].getScope() == VariableInfo.AT_END) {
					decl = taglibVars[i].getDeclarationString();
					fTagToVariableMap.push(tagToAdd, taglibVars);
				}
			}
			for (int i = 0; i < taglibVars.length; i++) {
				if (taglibVars[i].getScope() == VariableInfo.NESTED) {
					decl = taglibVars[i].getDeclarationString();
					appendToBuffer("{", fUserCode, false, fCurrentNode);
					appendToBuffer(decl, fUserCode, false, fCurrentNode);
					fTagToVariableMap.push(tagToAdd, taglibVars);
				}
			}
			if (customTag.getLastRegion().getType().equals(DOMRegionContext.XML_EMPTY_TAG_CLOSE)) {
				/*
				 * Process NESTED variables backwards so the scopes "unroll"
				 * correctly.
				 */
				for (int i = taglibVars.length; i > 0; i--) {
					if (taglibVars[i - 1].getScope() == VariableInfo.NESTED) {
						appendToBuffer("}", fUserCode, false, fCurrentNode);
					}
				}
				/* Treat this as the end for empty tags */
				for (int i = 0; i < taglibVars.length; i++) {
					if (taglibVars[i].getScope() == VariableInfo.AT_END) {
						decl = taglibVars[i].getDeclarationString();
						appendToBuffer(decl, fUserCode, false, fCurrentNode);
					}
				}
			}
		}
		/*
		 * Process NESTED variables for an end tag backwards so the scopes
		 * "unroll" correctly.
		 */
		else if (customTag.getFirstRegion().getType().equals(DOMRegionContext.XML_END_TAG_OPEN)) {
			TaglibVariable[] taglibVars = (TaglibVariable[]) fTagToVariableMap.pop(tagToAdd);
			if (taglibVars != null) {
				for (int i = taglibVars.length; i > 0; i--) {
					if (taglibVars[i - 1].getScope() == VariableInfo.NESTED) {
						appendToBuffer("}", fUserCode, false, fCurrentNode);
					}
				}
				for (int i = 0; i < taglibVars.length; i++) {
					if (taglibVars[i].getScope() == VariableInfo.AT_END) {
						decl = taglibVars[i].getDeclarationString();
						appendToBuffer(decl, fUserCode, false, fCurrentNode);
					}
				}
			}
		}
	}

	private IFile getFile() {
		IFile f = null;
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getStructuredDocument());
		try {
			if (sModel != null) {
				Path path = new Path(sModel.getBaseLocation());
				if (path.segmentCount() > 1) {
					f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				}
				if (f != null && f.isAccessible()) {
					return f;
				}
			}
			return null;
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}

	/*
	 * used by inner helper class (XMLJSPRegionHelper, JSPIncludeRegionHelper)
	 */
	public List getBlockMarkers() {
		if (fBlockMarkers == null)
			fBlockMarkers = new ArrayList();
		return fBlockMarkers;
	}

	/**
	 * /* the main control loop for translating the document, driven by the
	 * structuredDocument nodes
	 */
	public void translate() {
		if (fTagToVariableMap == null) {
			fTagToVariableMap = new StackMap();
		}
		fTranslationProblems.clear();

		setCurrentNode(new ZeroStructuredDocumentRegion(fStructuredDocument, 0));
		translatePreludes();

		setCurrentNode(fStructuredDocument.getFirstStructuredDocumentRegion());

		while (getCurrentNode() != null && !isCanceled()) {

			// intercept HTML comment flat node
			// also handles UNDEFINED (which is what CDATA comes in as)
			// basically this part will handle any "embedded" JSP containers
			if (getCurrentNode().getType() == DOMRegionContext.XML_COMMENT_TEXT || getCurrentNode().getType() == DOMRegionContext.XML_CDATA_TEXT || getCurrentNode().getType() == DOMRegionContext.UNDEFINED) {
				translateXMLCommentNode(getCurrentNode());
			}
			else {
				// iterate through each region in the flat node
				translateRegionContainer(getCurrentNode(), STANDARD_JSP);
			}
			if (getCurrentNode() != null)
				advanceNextNode();
		}

		setCurrentNode(new ZeroStructuredDocumentRegion(fStructuredDocument, fStructuredDocument.getLength()));
		translateCodas();

		buildResult();

		fTagToVariableMap.clear();
	}

	protected void setDocumentContent(IDocument document, InputStream contentStream, String charset) {
		Reader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(contentStream, charset), 2048);
			StringBuffer buffer = new StringBuffer(2048);
			char[] readBuffer = new char[2048];
			int n = in.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n = in.read(readBuffer);
			}
			document.set(buffer.toString());
		}
		catch (IOException x) {
			// ignore
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException x) {
					// ignore
				}
			}
		}
	}

	protected void init() {
		fClassHeader = "public class _JSPServlet extends "; //$NON-NLS-1$
		fClassname = "_JSPServlet"; //$NON-NLS-1$

		fImplicitImports = "import javax.servlet.*;" + ENDL + //$NON-NLS-1$
					"import javax.servlet.http.*;" + ENDL + //$NON-NLS-1$
					"import javax.servlet.jsp.*;" + ENDL + ENDL; //$NON-NLS-1$

		fServiceHeader = "public void _jspService(javax.servlet.http.HttpServletRequest request," + //$NON-NLS-1$
					" javax.servlet.http.HttpServletResponse response)" + ENDL + //$NON-NLS-1$
					"\t\tthrows java.io.IOException, javax.servlet.ServletException {" + ENDL + //$NON-NLS-1$
					"javax.servlet.jsp.PageContext pageContext = null;" + ENDL + //$NON-NLS-1$
					"javax.servlet.ServletContext application = null;" + ENDL + //$NON-NLS-1$
					"javax.servlet.ServletConfig config = null;" + ENDL + //$NON-NLS-1$ 
					"javax.servlet.jsp.JspWriter out = null;" + ENDL + //$NON-NLS-1$
					"Object page = null;" + ENDL; //$NON-NLS-1$
		fSuperclass = "javax.servlet.http.HttpServlet"; //$NON-NLS-1$
	}

	/**
	 * 
	 * @return the status of the translator's progrss monitor, false if the
	 *         monitor is null
	 */
	private boolean isCanceled() {
		return (fProgressMonitor == null) ? false : fProgressMonitor.isCanceled();
	}

	private void advanceNextNode() {
		setCurrentNode(getCurrentNode().getNext());
		if (getCurrentNode() != null)
			setSourceReferencePoint();
	}

	private void setSourceReferencePoint() {
		if (isJSP(getCurrentNode().getFirstRegion().getType())) {
			Iterator it = getCurrentNode().getRegions().iterator();
			ITextRegion r = null;
			while (it.hasNext()) {
				r = (ITextRegion) it.next();
				if (r.getType() == DOMJSPRegionContexts.JSP_CONTENT || r.getType() == DOMRegionContext.XML_CONTENT)
					break;
				else if (r.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME)
					break;
				else if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE && getCurrentNode().getFullText(r).trim().equals("import")) //$NON-NLS-1$
					break;
			}
		}
	}

	/**
	 * translates a region container (and XML JSP container, or <% JSP
	 * container)
	 */
	protected void translateRegionContainer(ITextRegionCollection container, int JSPType) {

		ITextRegionCollection containerRegion = container;

		// custom tags need their own scope {}
		handleScopingIfNecessary(containerRegion);

		Iterator regions = containerRegion.getRegions().iterator();
		ITextRegion region = null;
		while (regions.hasNext()) {

			region = (ITextRegion) regions.next();

			String type = region.getType();

			// content assist was not showing up in JSP inside a javascript
			// region
			if (type == DOMRegionContext.BLOCK_TEXT) {
				// check if it's nested jsp in a script tag...
				if (region instanceof ITextRegionContainer) {
					// pass in block text's container & iterator
					Iterator regionIterator = ((ITextRegionCollection) region).getRegions().iterator();
					translateJSPNode(region, regionIterator, type, EMBEDDED_JSP);
				}
				else {
					/**
					 * LIMITATION - Normally the script content within a
					 * script tag is a single document region with a single
					 * BLOCK_TEXT text region within it. Any JSP scripting
					 * will be within its own region container (for the sake
					 * of keeping the scripting open/content/end as a group)
					 * also of BLOCK_TEXT. That ignores custom tags that might
					 * be in there, though, as they require proper scoping and
					 * variable declaration to be performed even though
					 * they're not proper nodes in the DOM. The only way to
					 * really do this is to treat the entire script content as
					 * JSP content on its own, akin to an included segment.
					 * Further complicating this solution is that tagdependent
					 * custom tags have their comment marked as BLOCK_TEXT as
					 * well, so there's no clear way to tell the two cases
					 * apart.
					 */

					// ////////////////////////////////////////////////////////////////////////////////
					// THIS EMBEDDED JSP TEXT WILL COME OUT LATER WHEN
					// PARTITIONING HAS
					// SUPPORT FOR NESTED XML-JSP
					// CMVC 241882
					decodeScriptBlock(containerRegion.getFullText(region), containerRegion.getStartOffset());
					// ////////////////////////////////////////////////////////////////////////////////
				}
			}
			// if (region instanceof ITextRegionCollection &&
			// ((ITextRegionCollection) region).getNumberOfRegions() > 0) {
			// translateRegionContainer((ITextRegionCollection) region,
			// EMBEDDED_JSP);
			// }
			if (type != null && isJSP(type)) // <%, <%=, <%!, <%@
			{
				// translateJSPNode(region, regions, type, JSPType);
				translateJSPNode(containerRegion, regions, type, JSPType);
			}
			else if (type != null && (type == DOMRegionContext.XML_TAG_OPEN || type == DOMRegionContext.XML_END_TAG_OPEN)) {
				translateXMLNode(containerRegion, regions);
			}
		}
		// }
	}

	private void handleScopingIfNecessary(ITextRegionCollection containerRegion) {
		/*
		 * 199047 - Braces missing from translation of custom tags not
		 * defining variables
		 */

		// code within a custom tag gets its own scope
		// so if we encounter a start of a custom tag, we add '{'
		// and for the end of a custom tag we add '}'
		if (containerRegion.getFirstRegion().getType() == DOMRegionContext.XML_TAG_OPEN) {
			// don't add '{' if it's a self closing tag
			if (!isSelfClosingTag(containerRegion)) {
				if (isCustomTag(containerRegion)) {
					startScope();
				}
			}
		}
		else if (containerRegion.getFirstRegion().getType() == DOMRegionContext.XML_END_TAG_OPEN) {
			if (isCustomTag(containerRegion)) {
				endScope();
			}
		}
	}

	private void startScope() {
		// fScopeDepth++;
		StringBuffer text = new StringBuffer();
		// for(int i=0; i<fScopeDepth; i++) text.append(" "); //$NON-NLS-1$
		text.append("{ // <"); //$NON-NLS-1$
		text.append(getRegionName(fCurrentNode));
		text.append(">\n"); //$NON-NLS-1$
		appendToBuffer(text.toString(), fUserCode, false, fCurrentNode); //$NON-NLS-1$
	}

	private void endScope() {
		StringBuffer text = new StringBuffer();
		text.append("} // </"); //$NON-NLS-1$
		text.append(getRegionName(fCurrentNode));
		text.append(">\n"); //$NON-NLS-1$
		appendToBuffer(text.toString(), fUserCode, false, fCurrentNode); //$NON-NLS-1$
	}

	private boolean isSelfClosingTag(ITextRegionCollection containerRegion) {

		if (containerRegion == null)
			return false;

		ITextRegionList regions = containerRegion.getRegions();
		ITextRegion r = regions.get(regions.size() - 1);
		return r.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE;
	}

	private boolean isCustomTag(ITextRegionCollection containerRegion) {
		String tagName = getRegionName(containerRegion);

		if (tagName == null)
			return false;

		JSPSourceParser parser = (JSPSourceParser) fStructuredDocument.getParser();
		int colonIndex = tagName.indexOf(":");
		if (colonIndex > 0) {
			String prefix = tagName.substring(0, colonIndex);
			if (prefix.equals("jsp")) { //$NON-NLS-1$
				return false;
			}
			TagMarker[] prefixes = (TagMarker[]) parser.getNestablePrefixes().toArray(new TagMarker[0]);
			for (int i = 0; i < prefixes.length; i++) {
				if (prefix.equals(prefixes[i].getTagName())) {
					return true;
				}
			}
		}

		return false;
	}

	private String getRegionName(ITextRegionCollection containerRegion) {
		ITextRegionList regions = containerRegion.getRegions();
		ITextRegion nameRegion = null;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion r = regions.get(i);
			if (r.getType() == DOMRegionContext.XML_TAG_NAME) {
				nameRegion = r;
				break;
			}
		}
		return nameRegion != null ? containerRegion.getText(nameRegion).trim() : null;
	}

	/*
	 * ////////////////////////////////////////////////////////////////////////////////// **
	 * TEMP WORKAROUND FOR CMVC 241882 Takes a String and blocks out
	 * jsp:scriptlet, jsp:expression, and jsp:declaration @param blockText
	 * @return
	 */
	private void decodeScriptBlock(String blockText, int startOfBlock) {
		XMLJSPRegionHelper helper = new XMLJSPRegionHelper(this);
		helper.addBlockMarker(new BlockMarker("jsp:scriptlet", null, DOMJSPRegionContexts.JSP_CONTENT, false)); //$NON-NLS-1$
		helper.addBlockMarker(new BlockMarker("jsp:expression", null, DOMJSPRegionContexts.JSP_CONTENT, false)); //$NON-NLS-1$
		helper.addBlockMarker(new BlockMarker("jsp:declaration", null, DOMJSPRegionContexts.JSP_CONTENT, false)); //$NON-NLS-1$
		helper.addBlockMarker(new BlockMarker("jsp:directive.include", null, DOMJSPRegionContexts.JSP_CONTENT, false)); //$NON-NLS-1$
		helper.addBlockMarker(new BlockMarker("jsp:directive.taglib", null, DOMJSPRegionContexts.JSP_CONTENT, false)); //$NON-NLS-1$
		helper.reset(blockText, startOfBlock);
		// force parse
		helper.forceParse();
	}

	/*
	 * returns string minus CDATA open and close text
	 */
	final public String stripCDATA(String text) {
		String resultText = ""; //$NON-NLS-1$
		String CDATA_OPEN = "<![CDATA["; //$NON-NLS-1$
		String CDATA_CLOSE = "]]>"; //$NON-NLS-1$
		int start = 0;
		int end = text.length();
		while (start < text.length()) {
			if (text.indexOf(CDATA_OPEN, start) > -1) {
				end = text.indexOf(CDATA_OPEN, start);
				resultText += text.substring(start, end);
				start = end + CDATA_OPEN.length();
			}
			else if (text.indexOf(CDATA_CLOSE, start) > -1) {
				end = text.indexOf(CDATA_CLOSE, start);
				resultText += text.substring(start, end);
				start = end + CDATA_CLOSE.length();
			}
			else {
				end = text.length();
				resultText += text.substring(start, end);
				break;
			}
		}
		return resultText;
	}

	// END OF WORKAROUND CODE...
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * determines if the type is a pure JSP type (not XML)
	 */
	protected boolean isJSP(String type) {
		return ((type == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN || type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN || type == DOMJSPRegionContexts.JSP_DECLARATION_OPEN || type == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN || type == DOMJSPRegionContexts.JSP_CONTENT || type == DOMJSPRegionContexts.JSP_EL_OPEN) && type != DOMRegionContext.XML_TAG_OPEN);
		// checking XML_TAG_OPEN so <jsp:directive.xxx/> gets treated like
		// other XML jsp tags
	}

	/**
	 * translates the various XMLJSP type nodes
	 * 
	 * @param regions
	 *            the regions of the XMLNode
	 */
	protected void translateXMLNode(ITextRegionCollection container, Iterator regions) {
		// contents must be valid XHTML, translate escaped CDATA into what it
		// really is...
		ITextRegion r = null;
		if (regions.hasNext()) {
			r = (ITextRegion) regions.next();
			// <jsp:directive.xxx > comes in as this
			if (r.getType() == DOMRegionContext.XML_TAG_NAME || r.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME)

			{
				String fullTagName = container.getText(r);
				if (fullTagName.indexOf(':') > -1) {
					addTaglibVariables(fullTagName, getCurrentNode()); // it
					// may
					// be a
					// custom
					// tag
				}
				StringTokenizer st = new StringTokenizer(fullTagName, ":.", false); //$NON-NLS-1$
				if (st.hasMoreTokens() && st.nextToken().equals("jsp")) //$NON-NLS-1$
				{
					if (st.hasMoreTokens()) {
						String jspTagName = st.nextToken();

						if (jspTagName.equals("scriptlet")) //$NON-NLS-1$
						{
							translateXMLJSPContent(SCRIPTLET);
						}
						else if (jspTagName.equals("expression")) //$NON-NLS-1$
						{
							translateXMLJSPContent(EXPRESSION);
						}
						else if (jspTagName.equals("declaration")) //$NON-NLS-1$
						{
							translateXMLJSPContent(DECLARATION);
						}
						else if (jspTagName.equals("directive")) //$NON-NLS-1$
						{
							if (st.hasMoreTokens()) {
								String directiveName = st.nextToken();
								if (directiveName.equals("taglib")) { //$NON-NLS-1$
									while (r != null && regions.hasNext() && !r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
										r = (ITextRegion) regions.next();
										if (container.getText(r).equals(JSP11Namespace.ATTR_NAME_PREFIX)) {
											String prefix = getAttributeValue(r, regions);
											if (prefix != null) {
												handleTaglib(prefix);
											}
										}
									}
									return;
								}
								else if (directiveName.equals("include")) { //$NON-NLS-1$

									String fileLocation = ""; //$NON-NLS-1$

									// skip to required "file" attribute,
									// should be safe because
									// "file" is the only attribute for the
									// include directive
									while (r != null && regions.hasNext() && !r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
										r = (ITextRegion) regions.next();
									}
									fileLocation = getAttributeValue(r, regions);
									if (fileLocation != null)
										handleIncludeFile(fileLocation);
								}
								else if (directiveName.equals("page")) { //$NON-NLS-1$

									// bad if currentNode is referenced after
									// here w/ the current list
									// see:
									// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3035
									// setCurrentNode(getCurrentNode().getNext());
									if (getCurrentNode() != null) {
										// 'regions' contain the attrs
										translatePageDirectiveAttributes(regions, getCurrentNode());
									}
								}
								else if (directiveName.equals("tag")) { //$NON-NLS-1$
									translatePageDirectiveAttributes(regions, getCurrentNode());
								}
								else if (directiveName.equals("variable")) { //$NON-NLS-1$
									translateVariableDirectiveAttributes(regions);
								}
							}
						}
						else if (jspTagName.equals("include")) { //$NON-NLS-1$
							// <jsp:include page="filename") />
							checkAttributeValueContainer(regions, "page"); //$NON-NLS-1$
						}
						else if (jspTagName.equals("forward")) { //$NON-NLS-1$
							checkAttributeValueContainer(regions, "page"); //$NON-NLS-1$
						}
						else if (jspTagName.equals("param")) { //$NON-NLS-1$
							checkAttributeValueContainer(regions, "value"); //$NON-NLS-1$
						}
						else if (jspTagName.equals("setProperty")) { //$NON-NLS-1$
							checkAttributeValueContainer(regions, "value"); //$NON-NLS-1$
						}
						else if (jspTagName.equals("useBean")) //$NON-NLS-1$
						{
							checkAttributeValueContainer(regions, "name"); //$NON-NLS-1$
							// https://bugs.eclipse.org/bugs/show_bug.cgi?id=103004
							// advanceNextNode(); // get the content
							if (getCurrentNode() != null) {
								translateUseBean(container); // 'regions'
							}
						}

					}
				}
				else {
					checkAllAttributeValueContainers(regions);
				}
			}
		}
	}

	/**
	 * translates embedded containers for ALL attribute values
	 * 
	 * @param regions
	 */
	private void checkAllAttributeValueContainers(Iterator regions) {
		// tag name is not jsp
		// handle embedded jsp attributes...
		ITextRegion embedded = null;
		// Iterator attrRegions = null;
		// ITextRegion attrChunk = null;
		while (regions.hasNext()) {
			embedded = (ITextRegion) regions.next();
			if (embedded instanceof ITextRegionContainer) {
				// parse out container

				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=130606
				// fix exponential iteration problem w/ embedded expressions
				translateEmbeddedJSPInAttribute((ITextRegionContainer) embedded);
				// attrRegions = ((ITextRegionContainer)
				// embedded).getRegions().iterator();
				// while (attrRegions.hasNext()) {
				// attrChunk = (ITextRegion) attrRegions.next();
				// String type = attrChunk.getType();
				// // embedded JSP in attribute support only want to
				// // translate one time per
				// // embedded region so we only translate on the JSP open
				// // tags (not content)
				// if (type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN ||
				// type ==
				// DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN || type ==
				// DOMJSPRegionContexts.JSP_DECLARATION_OPEN || type ==
				// DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN || type ==
				// DOMJSPRegionContexts.JSP_EL_OPEN) {
				// // now call jsptranslate
				// translateEmbeddedJSPInAttribute((ITextRegionContainer)
				// embedded);
				// break;
				// }
				// }
			}
		}
	}

	/**
	 * translates embedded container for specified attribute
	 * 
	 * @param regions
	 * @param attrName
	 */
	private void checkAttributeValueContainer(Iterator regions, String attrName) {
		ITextRegion r = null;
		while (regions.hasNext()) {
			r = (ITextRegion) regions.next();
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME && getCurrentNode().getText(r).equals(attrName)) { //$NON-NLS-1$
				// skip to attribute value
				while (regions.hasNext() && (r = (ITextRegion) regions.next()) != null) {
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
						break;
				}
				// forces embedded region to be translated
				if (r instanceof ContextRegionContainer) {
					translateEmbeddedJSPInAttribute((ContextRegionContainer) r);
				}
				break;
			}
		}
	}

	/*
	 * example:
	 * 
	 * <jsp:scriptlet>scriptlet jsp-java content <![CDATA[ more jsp java ]]>
	 * jsp-java content... <![CDATA[ more jsp java ]]> </jsp:scriptlet>
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93366
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=88590 translate
	 * everything inbetween <scriptlet> tags, which may be more than one
	 * region (esp. CDATA)
	 * 
	 */
	private void translateXMLJSPContent(int type) {

		IStructuredDocumentRegion sdr = getCurrentNode().getNext();
		int start = sdr.getStartOffset();
		int end = sdr.getEndOffset();
		String sdrText = ""; //$NON-NLS-1$

		// read structured document regions until
		// </jsp:scriptlet> or EOF
		while (sdr != null && sdr.getType() != DOMRegionContext.XML_TAG_NAME) {

			// setup for next region
			start = sdr.getStartOffset();
			sdrText = sdr.getText();

			if (sdr.getType() == DOMRegionContext.XML_CDATA_TEXT) {

				// just to be safe, make sure CDATA start & end are there
				if (sdrText.startsWith("<![CDATA[") && sdrText.endsWith("]]>")) { //$NON-NLS-1$ //$NON-NLS-2$

					start = sdr.getStartOffset() + 9; // <![CDATA[
					end = sdr.getEndOffset() - 3; // ]]>
					sdrText = sdrText.substring(9, sdrText.length() - 3);
					writeToBuffer(type, sdrText, start, end);
				}
			}
			else {

				// handle entity references
				sdrText = EscapedTextUtil.getUnescapedText(sdrText);
				end = sdr.getEndOffset();
				writeToBuffer(type, sdrText, start, end);
			}
			sdr = sdr.getNext();
		}
		setCurrentNode(sdr);
		setSourceReferencePoint();
	}

	private void writeToBuffer(int type, String content, int jspStart, int jspEnd) {
		switch (type) {
			case SCRIPTLET :
				translateScriptletString(content, getCurrentNode(), jspStart, jspEnd - jspStart);
				break;
			case EXPRESSION :
				translateExpressionString(content, getCurrentNode(), jspStart, jspEnd - jspStart);
				break;
			case DECLARATION :
				translateDeclarationString(content, getCurrentNode(), jspStart, jspEnd - jspStart);
				break;
		}
	}

	/**
	 * goes through comment regions, checks if any are an embedded JSP
	 * container if it finds one, it's sends the container into the
	 * translation routine
	 */
	protected void translateXMLCommentNode(IStructuredDocumentRegion node) {
		Iterator it = node.getRegions().iterator();
		ITextRegion commentRegion = null;
		while (it != null && it.hasNext()) {
			commentRegion = (ITextRegion) it.next();
			if (commentRegion instanceof ITextRegionContainer) {
				translateRegionContainer((ITextRegionContainer) commentRegion, EMBEDDED_JSP); // it's
				// embedded
				// jsp...iterate
				// regions...
			}
		}
	}

	/**
	 * determines which type of JSP node to translate
	 */
	protected void translateJSPNode(ITextRegion region, Iterator regions, String type, int JSPType) {
		if (type == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN && regions != null) {
			translateDirective(regions);
		}
		else {
			ITextRegionCollection contentRegion = null;
			if (JSPType == STANDARD_JSP && (setCurrentNode(getCurrentNode().getNext())) != null) {
				contentRegion = getCurrentNode();
			}
			else if (JSPType == EMBEDDED_JSP && region instanceof ITextRegionCollection) {
				translateEmbeddedJSPInBlock((ITextRegionCollection) region, regions);
				// ensure the rest of this method won't be called
			}
			if (contentRegion != null) {
				if (type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) {
					translateExpression(contentRegion);
				}
				else if (type == DOMJSPRegionContexts.JSP_DECLARATION_OPEN) {
					translateDeclaration(contentRegion);
				}
				else if (type == DOMJSPRegionContexts.JSP_CONTENT || type == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) {
					translateScriptlet(contentRegion);
				}
			}
			else {
				// this is the case of an attribute w/ no region <p
				// align="<%%>">
				setCursorOwner(getJSPTypeForRegion(region));
			}
		}
	}


	private void translateEL(String elText, String delim, IStructuredDocumentRegion currentNode, int contentStart, int contentLength) {
		IJSPELTranslator translator = getELTranslator();
		if (null != translator) {
			translator.translateEL(elText, delim, currentNode, contentStart, contentLength, fUserELExpressions, fUserELRanges, fStructuredDocument);
		}
	}

	/**
	 * Discover and instantiate an EL translator.
	 */
	public IJSPELTranslator getELTranslator() {
		if (fELTranslator == null) {

			/*
			 * name of plugin that exposes this extension point
			 */
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(JSP_CORE_PLUGIN_ID, EL_TRANSLATOR_EXTENSION_NAME); // -
			// extension
			// id

			// Iterate over all declared extensions of this extension point.
			// A single plugin may extend the extension point more than once,
			// although it's not recommended.
			IConfigurationElement bestTranslator = null;
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int curExtension = 0; curExtension < extensions.length; curExtension++) {
				IExtension extension = extensions[curExtension];

				IConfigurationElement[] translators = extension.getConfigurationElements();
				for (int curTranslator = 0; curTranslator < translators.length; curTranslator++) {

					IConfigurationElement elTranslator = translators[curTranslator];

					if (!EL_TRANSLATOR_EXTENSION_NAME.equals(elTranslator.getName())) { // -
						// name
						// of
						// configElement
						continue;
					}

					String idString = elTranslator.getAttribute("id"); //$NON-NLS-1$
					if (null != idString && idString.equals(fELTranslatorID) || (null == bestTranslator && DEFAULT_JSP_EL_TRANSLATOR_ID.equals(idString))) {
						bestTranslator = elTranslator;
					}
				}
			}

			if (null != bestTranslator) {
				try {
					Object execExt = bestTranslator.createExecutableExtension("class"); //$NON-NLS-1$
					if (execExt instanceof IJSPELTranslator) {
						return fELTranslator = (IJSPELTranslator) execExt;
					}
				}
				catch (CoreException e) {
					Logger.logException(e);
				}
			}
		}
		return fELTranslator;
	}

	/**
	 * Pass the ITextRegionCollection which is the embedded region
	 * 
	 * @param regions
	 *            iterator for collection
	 */
	private void translateEmbeddedJSPInBlock(ITextRegionCollection collection, Iterator regions) {
		ITextRegion region = null;
		while (regions.hasNext()) {
			region = (ITextRegion) regions.next();
			if (isJSP(region.getType()))
				break;
			region = null;
		}
		if (region != null) {
			translateEmbeddedJSPInAttribute(collection);
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=126377
			// all of collection was translated so just finish off iterator
			while (regions.hasNext())
				regions.next();
		}
	}

	/*
	 * Translates all embedded jsp regions in embeddedContainer for example:
	 * <a href="index.jsp?p=<%=abc%>b=<%=xyz%>">abc</a>
	 */
	private void translateEmbeddedJSPInAttribute(ITextRegionCollection embeddedContainer) {
		// THIS METHOD IS A FIX FOR
		// jsp embedded in attribute regions
		// loop all regions
		ITextRegionList embeddedRegions = embeddedContainer.getRegions();
		ITextRegion delim = null;
		ITextRegion content = null;
		String type = null;
		for (int i = 0; i < embeddedRegions.size(); i++) {

			// possible delimiter, check later
			delim = embeddedRegions.get(i);
			type = delim.getType();

			// check next region to see if it's content
			if (i + 1 < embeddedRegions.size()) {
				String regionType = embeddedRegions.get(i + 1).getType();
				if (regionType == DOMJSPRegionContexts.JSP_CONTENT || regionType == DOMJSPRegionContexts.JSP_EL_CONTENT)
					content = embeddedRegions.get(i + 1);

			}

			if (content != null) {
				int contentStart = embeddedContainer.getStartOffset(content);
				int rStart = fCurrentNode.getStartOffset() + contentStart;
				int rEnd = fCurrentNode.getStartOffset() + embeddedContainer.getEndOffset(content);

				boolean inThisRegion = rStart <= fSourcePosition && rEnd >= fSourcePosition;
				// int jspPositionStart = fCurrentNode.getStartOffset() +
				// contentStart;

				if (type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) {
					fLastJSPType = EXPRESSION;
					// translateExpressionString(embeddedContainer.getText(content),
					// fCurrentNode, contentStart, content.getLength());
					translateExpressionString(embeddedContainer.getText(content), embeddedContainer, contentStart, content.getLength());
				}
				else if (type == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) {
					fLastJSPType = SCRIPTLET;
					// translateScriptletString(embeddedContainer.getText(content),
					// fCurrentNode, contentStart, content.getLength());
					translateScriptletString(embeddedContainer.getText(content), embeddedContainer, contentStart, content.getLength());
				}
				else if (type == DOMJSPRegionContexts.JSP_DECLARATION_OPEN) {
					fLastJSPType = DECLARATION;
					// translateDeclarationString(embeddedContainer.getText(content),
					// fCurrentNode, contentStart, content.getLength());
					translateDeclarationString(embeddedContainer.getText(content), embeddedContainer, contentStart, content.getLength());
				}
				else if (type == DOMJSPRegionContexts.JSP_EL_OPEN) {
					fLastJSPType = EXPRESSION;
					translateEL(embeddedContainer.getText(content), embeddedContainer.getText(delim), fCurrentNode, contentStart, content.getLength());
				}

				// calculate relative offset in buffer
				if (inThisRegion) {
					setCursorOwner(fLastJSPType);
					int currentBufferLength = getCursorOwner().length();
					setRelativeOffset((fSourcePosition - contentStart) + currentBufferLength);
					if (fLastJSPType == EXPRESSION) {
						// if an expression, add then length of the enclosing
						// paren..
						setCursorInExpression(true);
						setRelativeOffset(getRelativeOffset() + EXPRESSION_PREFIX.length());
					}
				}
			}
			else {
				type = null;
			}
		}
	}

	private int fLastJSPType = SCRIPTLET;

	/**
	 * JSPType is only used internally in this class to describe tye type of
	 * region to be translated
	 * 
	 * @param region
	 * @return int
	 */
	private int getJSPTypeForRegion(ITextRegion region) {
		String regionType = region.getType();
		int type = SCRIPTLET;
		if (regionType == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN)
			type = SCRIPTLET;
		else if (regionType == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN)
			type = EXPRESSION;
		else if (regionType == DOMJSPRegionContexts.JSP_DECLARATION_OPEN)
			type = DECLARATION;
		else if (regionType == DOMJSPRegionContexts.JSP_CONTENT)
			type = fLastJSPType;
		// remember the last type, in case the next type that comes in is
		// JSP_CONTENT
		fLastJSPType = type;
		return type;
	}

	/**
	 * /* <%@ %> /* need to pass in the directive tag region
	 */
	protected void translateDirective(Iterator regions) {
		ITextRegion r = null;
		String regionText, attrValue = ""; //$NON-NLS-1$
		while (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) { // could
			// be
			// XML_CONTENT
			// =
			// "",
			// skips
			// attrs?
			regionText = getCurrentNode().getText(r);
			if (regionText.equals("taglib")) { //$NON-NLS-1$
				// add custom tag block markers here
				handleTaglib();
				return;
			}
			else if (regionText.equals("include")) { //$NON-NLS-1$
				String fileLocation = ""; //$NON-NLS-1$
				// CMVC 258311
				// PMR 18368, B663
				// skip to required "file" attribute, should be safe because
				// "file" is the only attribute for the include directive
				while (r != null && regions.hasNext() && !r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
					r = (ITextRegion) regions.next();
				}
				fileLocation = getAttributeValue(r, regions);
				if (attrValue != null)
					handleIncludeFile(fileLocation);
			}
			else if (regionText.equals("page")) { //$NON-NLS-1$
				translatePageDirectiveAttributes(regions, getCurrentNode());
			}
			else if (regionText.equals("tag")) { //$NON-NLS-1$
				// some attributes overlap, so both are handled in this method
				translatePageDirectiveAttributes(regions, getCurrentNode());
			}
			else if (regionText.equals("variable")) { //$NON-NLS-1$
				translateVariableDirectiveAttributes(regions);
			}
			else if (regionText.equals("attribute")) { //$NON-NLS-1$
				translateAttributeDirectiveAttributes(regions);
			}
		}
	}

	private void translateAttributeDirectiveAttributes(Iterator regions) {
		ITextRegion r = null;
		String attrName, attrValue;

		String varType = "java.lang.String"; //$NON-NLS-1$ // the default class...
		String varName = null;
		String description = "";//$NON-NLS-1$ 
		boolean isFragment = false;

		// iterate all attributes
		while (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() != DOMJSPRegionContexts.JSP_CLOSE) {
			attrName = attrValue = null;
			if (r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
				attrName = getCurrentNode().getText(r).trim();
				if (attrName.length() > 0) {
					if (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
						if (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
							attrValue = StringUtils.strip(getCurrentNode().getText(r));
						}
						// has equals, but no value?
					}
					if (attrName.equals(JSP11Namespace.ATTR_NAME_TYPE)) {
						varType = attrValue;
					}
					else if (attrName.equals(JSP20Namespace.ATTR_NAME_FRAGMENT)) {
						isFragment = Boolean.valueOf(attrValue).booleanValue();
					}
					else if (attrName.equals(JSP11Namespace.ATTR_NAME_NAME)) {
						varName = attrValue;
					}
					else if (attrName.equals(JSP20Namespace.ATTR_NAME_DESCRIPTION)) {
						description = attrValue;
					}
				}
			}
		}
		if (varName != null) {
			if (isFragment) {
				// 2.0:JSP.8.5.2
				varType = "javax.servlet.jsp.tagext.JspFragment";
			}
			String declaration = new TaglibVariable(varType, varName, "", description).getDeclarationString(true, TaglibVariable.M_PRIVATE);
			appendToBuffer(declaration, fUserDeclarations, false, fCurrentNode);
		}
	}

	private void translateVariableDirectiveAttributes(Iterator regions) {
		/*
		 * Shouldn't create a scripting variable in *this* tag file's
		 * translation, only in JSP files that use it -
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=188780
		 */
	}

	/*
	 * This method should ideally only be called once per run through
	 * JSPTranslator This is intended for use by inner helper classes that
	 * need to add block markers to their own parsers. This method only adds
	 * markers that came from <@taglib> directives, (not <@include>), since
	 * include file taglibs are handled on the fly when they are encountered. *
	 * @param regions
	 * 
	 * @deprecated - does not properly handle prefixes
	 */
	protected void handleTaglib() {
		// get/create TLDCMDocument
		TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(fStructuredDocument);
		if (mgr != null) {
			List trackers = mgr.getCMDocumentTrackers(getCurrentNode().getEnd());
			Iterator it = trackers.iterator();
			CMDocumentTracker tracker = null;
			Iterator taglibRegions = null;
			IStructuredDocumentRegion sdRegion = null;
			ITextRegion r = null;
			while (it.hasNext()) {
				tracker = (CMDocumentTracker) it.next();
				sdRegion = tracker.getStructuredDocumentRegion();
				// since may be call from another thread (like a background
				// job)
				// this check is to be safer
				if (sdRegion != null && !sdRegion.isDeleted()) {
					taglibRegions = sdRegion.getRegions().iterator();
					while (!sdRegion.isDeleted() && taglibRegions.hasNext()) {
						r = (ITextRegion) taglibRegions.next();
						if (r.getType().equals(DOMJSPRegionContexts.JSP_DIRECTIVE_NAME)) {
							String text = sdRegion.getText(r);
							if (JSP12TLDNames.TAGLIB.equals(text) || JSP12Namespace.ElementName.DIRECTIVE_TAGLIB.equals(text)) {
								addBlockMarkers(tracker.getDocument());
							}
						}
					}
				}
			}
		}
	}

	/*
	 * This method should ideally only be called once per run through
	 * JSPTranslator This is intended for use by inner helper classes that
	 * need to add block markers to their own parsers. This method only adds
	 * markers that came from <@taglib> directives, (not <@include>), since
	 * include file taglibs are handled on the fly when they are encountered. *
	 * @param regions
	 */
	private void handleTaglib(String prefix) {
		// get/create TLDCMDocument
		TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(fStructuredDocument);
		if (mgr != null) {
			// get trackers for the CMDocuments enabled at this offset
			List trackers = mgr.getCMDocumentTrackers(getCurrentNode().getEnd());
			Iterator it = trackers.iterator();
			CMDocumentTracker tracker = null;
			while (it.hasNext()) {
				tracker = (CMDocumentTracker) it.next();
				addBlockMarkers(prefix + ":", tracker.getDocument());
			}
		}
	}

	/*
	 * adds block markers to JSPTranslator's block marker list for all
	 * elements in doc @param doc
	 */
	protected void addBlockMarkers(CMDocument doc) {
		if (doc.getElements().getLength() > 0) {
			Iterator elements = doc.getElements().iterator();
			CMNode node = null;
			while (elements.hasNext()) {
				node = (CMNode) elements.next();
				getBlockMarkers().add(new BlockMarker(node.getNodeName(), null, DOMJSPRegionContexts.JSP_CONTENT, true));
			}
		}
	}

	/*
	 * adds block markers to JSPTranslator's block marker list for all
	 * elements in doc @param doc
	 */
	protected void addBlockMarkers(String prefix, CMDocument doc) {
		if (doc.getElements().getLength() > 0) {
			Iterator elements = doc.getElements().iterator();
			CMNode node = null;
			while (elements.hasNext()) {
				node = (CMNode) elements.next();
				if (node instanceof TLDElementDeclaration && ((TLDElementDeclaration) node).getBodycontent().equals(JSP12TLDNames.CONTENT_TAGDEPENDENT))
					getBlockMarkers().add(new BlockMarker(prefix + node.getNodeName(), null, DOMRegionContext.BLOCK_TEXT, true));
				else
					getBlockMarkers().add(new BlockMarker(prefix + node.getNodeName(), null, DOMJSPRegionContexts.JSP_CONTENT, true));
			}
		}
	}

	/**
	 * If r is an attribute name region, this method will safely return the
	 * value for that attribute.
	 * 
	 * @param r
	 * @param remainingRegions
	 * @return the value for the attribute name (r), or null if isn't one
	 */
	protected String getAttributeValue(ITextRegion r, Iterator remainingRegions) {
		if (r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
			if (remainingRegions.hasNext() && (r = (ITextRegion) remainingRegions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				if (remainingRegions.hasNext() && (r = (ITextRegion) remainingRegions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
					return StringUtils.stripQuotes(getCurrentNode().getText(r));
				}
			}
		}
		return null;
	}

	/**
	 * takes an iterator of the attributes of a page directive and the
	 * directive itself. The iterator is used in case it can be optimized, but
	 * the documentRegion is still required to ensure that the values are
	 * extracted from the correct text.
	 */
	protected void translatePageDirectiveAttributes(Iterator regions, IStructuredDocumentRegion documentRegion) {
		ITextRegion r = null;
		String attrName, attrValue;
		// iterate all attributes
		while (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() != DOMJSPRegionContexts.JSP_CLOSE) {
			attrName = attrValue = null;
			if (r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {

				attrName = documentRegion.getText(r).trim();
				if (attrName.length() > 0) {
					if (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
						if (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {

							attrValue = StringUtils.strip(documentRegion.getText(r));
						}
						// has equals, but no value?
					}
					setDirectiveAttribute(attrName, attrValue);
				}
			}
		}
	}

	/**
	 * sets the appropriate page/tag directive attribute
	 */
	protected void setDirectiveAttribute(String attrName, String attrValue) {
		if (attrValue == null)
			return; // uses default (if there was one)
		if (attrName.equals("extends")) //$NON-NLS-1$
		{
			fSuperclass = attrValue;
		}
		else if (attrName.equals("import")) //$NON-NLS-1$
		{
			addImports(attrValue);
		}
		else if (attrName.equals("session")) //$NON-NLS-1$
		{
			fIsInASession = "true".equalsIgnoreCase(attrValue);
		}
		else if (attrName.equals("buffer")) //$NON-NLS-1$
		{
			// ignore for now
		}
		else if (attrName.equals("autoFlush")) //$NON-NLS-1$
		{
			// ignore for now
		}
		else if (attrName.equals("isThreadSafe")) //$NON-NLS-1$
		{
			// fThreadSafe = "true".equalsIgnoreCase(attrValue); //$NON-NLS-1$
		}
		else if (attrName.equals("isErrorPage")) //$NON-NLS-1$
		{
			fIsErrorPage = "true".equalsIgnoreCase(attrValue);
		}
	}

	protected void handleIncludeFile(String filename) {
		if (filename != null && fProcessIncludes) {
			IPath basePath = getModelPath();
			if (basePath != null) {
				String filePath = FacetModuleCoreSupport.resolve(basePath, filename).toString();
				fIncludedPaths.add(filePath);

				if (!getIncludes().contains(filePath) && !filePath.equals(basePath.toString())) {
					getIncludes().push(filePath);
					JSPIncludeRegionHelper helper = new JSPIncludeRegionHelper(this);
					// Should we consider preludes on this segment?
					helper.parse(filePath);
					getIncludes().pop();
				}
			}
		}
	}

	private Stack getIncludes() {
		if (fIncludes == null)
			fIncludes = new Stack();
		return fIncludes;
	}

	public Collection getIncludedPaths() {
		return fIncludedPaths;
	}

	protected void translateExpressionString(String newText, ITextRegionCollection embeddedContainer, int jspPositionStart, int jspPositionLength) {
		appendToBuffer(EXPRESSION_PREFIX, fUserCode, false, embeddedContainer);
		appendToBuffer(newText, fUserCode, true, embeddedContainer, jspPositionStart, jspPositionLength);
		appendToBuffer(EXPRESSION_SUFFIX, fUserCode, false, embeddedContainer);
	}

	protected void translateDeclarationString(String newText, ITextRegionCollection embeddedContainer, int jspPositionStart, int jspPositionLength) {
		appendToBuffer(newText, fUserDeclarations, true, embeddedContainer, jspPositionStart, jspPositionLength);
		appendToBuffer(ENDL, fUserDeclarations, false, embeddedContainer);
	}

	/**
	 * used by XMLJSPRegionHelper for included JSP files
	 * 
	 * @param newText
	 * @param embeddedContainer
	 * @param jspPositionStart
	 * @param jspPositionLength
	 */
	protected void translateScriptletString(String newText, ITextRegionCollection embeddedContainer, int jspPositionStart, int jspPositionLength) {
		appendToBuffer(newText, fUserCode, true, embeddedContainer, jspPositionStart, jspPositionLength);
	}

	// the following 3 methods determine the cursor position
	// <%= %>
	protected void translateExpression(ITextRegionCollection region) {
		String newText = getUnescapedRegionText(region, EXPRESSION);
		appendToBuffer(EXPRESSION_PREFIX, fUserCode, false, fCurrentNode);
		appendToBuffer(newText, fUserCode, true, fCurrentNode);
		appendToBuffer(EXPRESSION_SUFFIX, fUserCode, false, fCurrentNode);
	}

	//
	// <%! %>
	protected void translateDeclaration(ITextRegionCollection region) {
		String newText = getUnescapedRegionText(region, DECLARATION);
		appendToBuffer(newText, fUserDeclarations, true, fCurrentNode);
		appendToBuffer(ENDL, fUserDeclarations, false, fCurrentNode);
	}

	//
	// <% %>
	protected void translateScriptlet(ITextRegionCollection region) {
		String newText = getUnescapedRegionText(region, SCRIPTLET);
		appendToBuffer(newText, fUserCode, true, fCurrentNode);
	}

	/**
	 * Append using a region, probably indirect mapping (eg. <%@page
	 * include=""%>)
	 * 
	 * @param newText
	 * @param buffer
	 * @param addToMap
	 * @param jspReferenceRegion
	 */
	private void appendToBuffer(String newText, StringBuffer buffer, boolean addToMap, ITextRegionCollection jspReferenceRegion) {
		int start = 0, length = 0;
		if (jspReferenceRegion != null) {
			start = jspReferenceRegion.getStartOffset();
			length = jspReferenceRegion.getLength();
		}
		appendToBuffer(newText, buffer, addToMap, jspReferenceRegion, start, length, false);
	}

	private void appendToBuffer(String newText, StringBuffer buffer, boolean addToMap, ITextRegionCollection jspReferenceRegion, int jspPositionStart, int jspPositionLength) {
		appendToBuffer(newText, buffer, addToMap, jspReferenceRegion, jspPositionStart, jspPositionLength, true);
	}

	/**
	 * Adds newText to the buffer passed in, and adds to translation mapping
	 * as specified by the addToMap flag. some special cases to consider (that
	 * may be affected by changes to this method): included files scriplets in
	 * an attribute value refactoring
	 * 
	 * @param newText
	 * @param buffer
	 * @param addToMap
	 */
	private void appendToBuffer(String newText, StringBuffer buffer, boolean addToMap, ITextRegionCollection jspReferenceRegion, int jspPositionStart, int jspPositionLength, boolean isIndirect) {

		int origNewTextLength = newText.length();

		// nothing to append
		if (jspReferenceRegion == null)
			return;

		// add a newline so translation looks cleaner
		if (!newText.endsWith(ENDL))
			newText += ENDL;

		if (buffer == fUserCode) {
			buffer.append(newText);
			if (addToMap) {
				if (isUsebeanTag(jspReferenceRegion)) {
					try {
						// requires special mapping
						appendUseBeanToBuffer(newText, jspReferenceRegion, isIndirect);
					}
					catch (Exception e) {
						// still working out kinks
						Logger.logException(e);
					}
				}
				else {
					// all other cases
					Position javaRange = new Position(fOffsetInUserCode, origNewTextLength);
					Position jspRange = new Position(jspPositionStart, jspPositionLength);

					fCodeRanges.put(javaRange, jspRange);
					if (isIndirect)
						fIndirectRanges.put(javaRange, jspRange);
				}
			}
			fOffsetInUserCode += newText.length();
		}
		else if (buffer == fUserDeclarations) {
			buffer.append(newText);
			if (addToMap) {
				Position javaRange = new Position(fOffsetInUserDeclarations, newText.length());
				Position jspRange = new Position(jspPositionStart, jspPositionLength);

				fDeclarationRanges.put(javaRange, jspRange);
				if (isIndirect)
					fIndirectRanges.put(javaRange, jspRange);
			}
			fOffsetInUserDeclarations += newText.length();
		}
	}

	/**
	 * 
	 * @param jspReferenceRegion
	 * @return
	 */
	private boolean isUsebeanTag(ITextRegionCollection jspReferenceRegion) {
		ITextRegionList regions = jspReferenceRegion.getRegions();
		ITextRegion r = null;
		boolean isUseBean = false;
		for (int i = 0; i < regions.size(); i++) {
			r = regions.get(i);
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=128490
			// length of 11 is the length of jsp:useBean
			// and saves the expensive getText.equals call
			if (r.getType() == DOMRegionContext.XML_TAG_NAME) {
				if (r.getTextLength() == 11 && jspReferenceRegion.getText(r).equals("jsp:useBean")) { //$NON-NLS-1$
					isUseBean = true;
				}
				// break no matter what if you hit tagname
				break;
			}
		}
		return isUseBean;
	}

	/**
	 * @param importName
	 *            should be just the package plus the type eg. java.util.List
	 *            or java.util.*
	 * @param jspReferenceRegion
	 *            should be the <%@ page import = "java.util.List"%> region
	 * @param addToMap
	 */
	private void appendImportToBuffer(String importName, ITextRegionCollection jspReferenceRegion, boolean addToMap) {
		String javaImportString = "import " + importName + ";" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$
		fUserImports.append(javaImportString);
		if (addToMap) {
			addImportToMap(importName, jspReferenceRegion);
		}
		fOffsetInUserImports += javaImportString.length();
	}

	/**
	 * new text can be something like: "import java.lang.Object;\n"
	 * 
	 * but the reference region could have been something like: <%@page
	 * import="java.lang.Object, java.io.*, java.util.List"%>
	 * 
	 * so the exact mapping has to be calculated carefully.
	 * 
	 * isIndirect means that the import came from an included file (if true)
	 * 
	 * @param importName
	 * @param jspReferenceRegion
	 */
	private void addImportToMap(String importName, ITextRegionCollection jspReferenceRegion) {

		// massage text
		// String jspText = importName.substring(importName.indexOf("import ")
		// + 7, importName.indexOf(';'));
		// String jspText = importName.trim();

		// these positions will be updated below
		Position javaRange = new Position(fOffsetInUserImports + 7, 1);
		Position jspRange = new Position(jspReferenceRegion.getStart(), jspReferenceRegion.getLength());

		// calculate JSP range by finding "import" attribute
		ITextRegionList regions = jspReferenceRegion.getRegions();
		int size = regions.size();

		int start = -1;
		int length = -1;

		ITextRegion r = null;
		for (int i = 0; i < size; i++) {
			r = regions.get(i);
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
				if (jspReferenceRegion.getText(r).trim().equals("import")) { //$NON-NLS-1$
					// get the attr value region
					if (size > i + 2) {
						r = regions.get(i + 2);
						if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {

							String jspImportText = jspReferenceRegion.getText(r);

							// the position in question (in the JSP) is what
							// is bracketed below
							// includes whitespace
							// <%@page import="java.lang.Object,[ java.io.* ],
							// java.util.List"%>

							// in the java file
							// import [ java.io.* ];

							start = jspImportText.indexOf(importName);
							length = importName.length();

							// safety, don't add to map if bad positioning
							if (start == -1 || length < 1)
								break;

							// update jsp range
							jspRange.setOffset(jspReferenceRegion.getStartOffset(r) + start);
							jspRange.setLength(length);

							// update java range
							javaRange.setLength(length);

							break;
						}
					}
				}
		}

		// safety for bad ranges
		if (start != -1 && length > 1) {
			// put ranges in java -> jsp range map
			fImportRanges.put(javaRange, jspRange);
		}
	}

	/**
	 * temp fix for 282295 until better mapping is in place
	 * 
	 * @param newText
	 * @param jspReferenceRegion
	 */
	private void appendUseBeanToBuffer(String newText, ITextRegionCollection jspReferenceRegion, boolean isIndirect) throws Exception {
		// java string looks like this (tokenized)
		// Type id = new Classname();\n
		// 0 1 2 3 4
		// or
		// Type id = null;\n // if there is no classname
		// 0 1 2 3

		// ----------------------
		// calculate java ranges
		// ----------------------
		StringTokenizer st = new StringTokenizer(newText, " ", false); //$NON-NLS-1$
		int i = 0;
		String[] parsedJava = new String[st.countTokens()];
		while (st.hasMoreTokens())
			parsedJava[i++] = st.nextToken();

		String type = parsedJava[0] != null ? parsedJava[0] : ""; //$NON-NLS-1$
		String id = parsedJava[1] != null ? parsedJava[1] : ""; //$NON-NLS-1$
		String className = parsedJava.length > 4 ? parsedJava[4] : ""; //$NON-NLS-1$

		Position javaTypeRange = new Position(fOffsetInUserCode, type.length());
		Position javaIdRange = new Position(fOffsetInUserCode + type.length() + 1, id.length());
		Position javaClassRange = new Position(fOffsetInUserCode + type.length() + 1 + id.length() + 7, 0);
		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=212242 - Check for
		 * the existence of '(' first.
		 */
		int parenPos = -1;
		if (className.length() >= 4 && (parenPos = className.indexOf('(')) >= 0) {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=86132
			int classNameLength = className.substring(0, parenPos).length();
			javaClassRange = new Position(fOffsetInUserCode + type.length() + 1 + id.length() + 7, classNameLength);
		}

		// ---------------------
		// calculate jsp ranges
		// ---------------------
		ITextRegionList regions = jspReferenceRegion.getRegions();
		ITextRegion r = null;
		String attrName = "", attrValue = ""; //$NON-NLS-1$ //$NON-NLS-2$
		int quoteOffset = 0;
		Position jspTypeRange = null;
		Position jspIdRange = null;
		Position jspClassRange = null;

		for (int j = 0; j < regions.size(); j++) {
			r = regions.get(j);
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				attrName = jspReferenceRegion.getText(r);
				if (regions.size() > j + 2 && regions.get(j + 2).getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
					// get attr value
					r = regions.get(j + 2);
					attrValue = jspReferenceRegion.getText(r);

					// may have quotes
					quoteOffset = (attrValue.startsWith("\"") || attrValue.startsWith("'")) ? 1 : 0; //$NON-NLS-1$ //$NON-NLS-2$

					if (attrName.equals("type")) //$NON-NLS-1$
						jspTypeRange = new Position(jspReferenceRegion.getStartOffset(r) + quoteOffset, StringUtils.stripQuotesLeaveInsideSpace(attrValue).length());
					else if (attrName.equals("id")) //$NON-NLS-1$
						jspIdRange = new Position(jspReferenceRegion.getStartOffset(r) + quoteOffset, StringUtils.stripQuotesLeaveInsideSpace(attrValue).length());
					else if (attrName.equals("class")) //$NON-NLS-1$
						jspClassRange = new Position(jspReferenceRegion.getStartOffset(r) + quoteOffset, StringUtils.stripQuotesLeaveInsideSpace(attrValue).length());
				}
			}
		}

		// put ranges in java -> jsp range map
		if (!type.equals("") && jspTypeRange != null) { //$NON-NLS-1$
			fCodeRanges.put(javaTypeRange, jspTypeRange);
			// note: don't update offsets for this map when result is built
			// they'll be updated when code ranges offsets are updated
			fUseBeanRanges.put(javaTypeRange, jspTypeRange);
			if (isIndirect)
				fIndirectRanges.put(javaTypeRange, jspTypeRange);
		}
		if (!id.equals("") && jspIdRange != null) { //$NON-NLS-1$
			fCodeRanges.put(javaIdRange, jspIdRange);
			// note: don't update offsets for this map when result is built
			// they'll be updated when code ranges offsets are updated
			fUseBeanRanges.put(javaIdRange, jspTypeRange);
			if (isIndirect)
				fIndirectRanges.put(javaIdRange, jspTypeRange);
		}
		if (!className.equals("") && jspClassRange != null) { //$NON-NLS-1$
			fCodeRanges.put(javaClassRange, jspClassRange);
			// note: don't update offsets for this map when result is built
			// they'll be updated when code ranges offsets are updated
			fUseBeanRanges.put(javaClassRange, jspTypeRange);
			if (isIndirect)
				fIndirectRanges.put(javaClassRange, jspTypeRange);
		}
	}

	/**
	 * Set the buffer to the current JSPType: STANDARD_JSP, EMBEDDED_JSP,
	 * DECLARATION, EXPRESSION, SCRIPTLET (for keepting track of cursor
	 * position when the final document is built)
	 * 
	 * @param JSPType
	 *            the JSP type that the cursor is in
	 */
	protected void setCursorOwner(int JSPType) {
		switch (JSPType) {
			case DECLARATION :
				setCursorOwner(fUserDeclarations);
				break;
			case EXPRESSION :
			case SCRIPTLET :
				setCursorOwner(fUserCode);
				break;
			default :
				setCursorOwner(fUserCode);
		}
	}

	/**
	 * this piece of code iterates through fCurrentNodes and clumps them
	 * together in a big text string - unescaping characters if they are not
	 * CDATA - simply appending if they are CDATA it stops iteration when it
	 * hits a node that is an XML_TAG_NAME (which should be the region closing
	 * tag)
	 */
	protected String getUnescapedRegionText(ITextRegionCollection stRegion, int JSPType) {
		StringBuffer buffer = new StringBuffer();
		int start = stRegion.getStartOffset();
		int end = stRegion.getEndOffset();
		// adjustment necessary for embedded region containers
		if (stRegion instanceof ITextRegionContainer && stRegion.getType() == DOMRegionContext.BLOCK_TEXT) {
			if (stRegion.getRegions() != null && stRegion.getRegions().size() > 1) {
				ITextRegion jspContent = stRegion.getRegions().get(1); // should
				// be
				// jspContent
				// region
				start = stRegion.getStartOffset(jspContent);
				end = stRegion.getEndOffset(jspContent);
			}
		}
		int CDATAOffset = 0; // number of characters lost in conversion
		int bufferSize = 0;
		if (stRegion.getType() == DOMJSPRegionContexts.JSP_CONTENT || stRegion.getType() == DOMRegionContext.BLOCK_TEXT // need
					// this
					// for
					// embedded
					// JSP
					// regions
					|| stRegion.getType() == DOMRegionContext.XML_TAG_NAME) // need
		// this
		// in
		// case
		// there's
		// no
		// region...
		{
			fInCodeRegion = (start <= fSourcePosition && fSourcePosition <= end);
			if (fInCodeRegion) {
				setCursorOwner(JSPType);
				setRelativeOffset((fSourcePosition - start) + getCursorOwner().length());
				if (JSPType == EXPRESSION) {
					// if an expression, add then length of the enclosing
					// paren..
					setCursorInExpression(true);
					setRelativeOffset(getRelativeOffset() + EXPRESSION_PREFIX.length());
				}
			}
			ITextRegion jspContent = null;
			if (stRegion.getRegions() != null && stRegion.getRegions().size() > 1)
				jspContent = stRegion.getRegions().get(1);
			return (jspContent != null) ? stRegion.getFullText(jspContent) : stRegion.getFullText(); // don't
			// unescape
			// if
			// it's
			// not
			// an
			// XMLJSP
			// tag
		}
		else if (stRegion.getType() == DOMJSPRegionContexts.JSP_CLOSE) {
			// need to determine cursor owner so that the fCurosorPosition
			// will be
			// correct even if there is no region after the cursor in the JSP
			// file
			setCursorOwner(JSPType);
		}
		// iterate XMLCONTENT and CDATA regions
		// loop fCurrentNode until you hit </jsp:scriptlet> (or other closing
		// tag name)
		while (getCurrentNode() != null && getCurrentNode().getType() != DOMRegionContext.XML_TAG_NAME) // need
		// to
		// stop
		// on
		// the
		// ending
		// tag
		// name...
		{
			start = getCurrentNode().getStartOffset();
			end = getCurrentNode().getEndOffset();
			bufferSize = buffer.length();
			CDATAOffset = unescapeRegion(getCurrentNode(), buffer);
			fInCodeRegion = (start <= fSourcePosition && fSourcePosition <= end);
			if (fInCodeRegion) {
				setCursorOwner(JSPType);
				// this offset is sort of complicated...
				// it's composed of:
				// 1. the length of the start of the current region up till
				// where the cursor is
				// 2. minus the number of characters lost in CDATA translation
				// 3. plus the length of the escaped buffer before the current
				// region, but
				// is still within the jsp tag
				setRelativeOffset((fSourcePosition - getCurrentNode().getStartOffset()) + getCursorOwner().length() - CDATAOffset + bufferSize);
				if (JSPType == EXPRESSION) {
					setCursorInExpression(true);
					// if an expression, add then length of the enclosing
					// paren..
					setRelativeOffset(getRelativeOffset() + EXPRESSION_PREFIX.length());
				}
			}
			if (getCurrentNode() != null)
				advanceNextNode();
		}
		return buffer.toString();
	}

	/**
	 * @param r
	 *            the region to be unescaped (XMLContent, XML ENTITY
	 *            REFERENCE, or CDATA)
	 * @param sb
	 *            the stringbuffer to append the text to
	 * @return the number of characters removed in unescaping this text
	 */
	protected int unescapeRegion(ITextRegion r, StringBuffer sb) {
		String s = ""; //$NON-NLS-1$
		int lengthBefore = 0, lengthAfter = 0, cdata_tags_length = 0;
		if (r != null && (r.getType() == DOMRegionContext.XML_CONTENT || r.getType() == DOMRegionContext.XML_ENTITY_REFERENCE)) {
			lengthBefore = (getCurrentNode() != r) ? getCurrentNode().getFullText(r).length() : getCurrentNode().getFullText().length();
			s = EscapedTextUtil.getUnescapedText(getCurrentNode(), r);
			lengthAfter = s.length();
			sb.append(s);
		}
		else if (r != null && r.getType() == DOMRegionContext.XML_CDATA_TEXT) {
			if (r instanceof ITextRegionContainer) // only interested in
			// contents
			{
				// navigate to next region container (which should be a JSP
				// region)
				Iterator it = ((ITextRegionContainer) r).getRegions().iterator();
				ITextRegion temp = null;
				while (it.hasNext()) {
					temp = (ITextRegion) it.next();
					if (temp instanceof ITextRegionContainer || temp.getType() == DOMRegionContext.XML_CDATA_TEXT) {
						sb.append(getCurrentNode().getFullText(temp));
					}
					else if (temp.getType() == DOMRegionContext.XML_CDATA_OPEN || temp.getType() == DOMRegionContext.XML_CDATA_CLOSE) {
						cdata_tags_length += temp.getLength();
					}
				}
			}
		}
		return (lengthBefore - lengthAfter + cdata_tags_length);
	}

	//
	// <jsp:useBean>
	protected void translateUseBean(ITextRegionCollection container) {
		ITextRegion r = null;
		String attrName = null;
		String attrValue = null;
		String id = null;
		ITextRegion idRegion = null;
		String type = null;
		ITextRegion typeRegion = null;
		String className = null;
		ITextRegion classnameRegion = null;
		String beanName = null;
		ITextRegion beanNameRegion = null;

		Iterator regions = container.getRegions().iterator();
		while (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && (r.getType() != DOMRegionContext.XML_TAG_CLOSE || r.getType() != DOMRegionContext.XML_EMPTY_TAG_CLOSE)) {
			attrName = attrValue = null;
			if (r.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
				attrName = container.getText(r).trim();
				if (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
					if (regions.hasNext() && (r = (ITextRegion) regions.next()) != null && r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						attrValue = StringUtils.stripQuotes(container.getText(r));
					}
					// has equals, but no value?
				}
				// an attribute with no equals?
			}
			// (pa) might need different logic here if we wanna support more
			if (attrName != null && attrValue != null) {
				if (attrName.equals("id")) {//$NON-NLS-1$
					id = attrValue;
					idRegion = r;
				}
				else if (attrName.equals("class")) {//$NON-NLS-1$
					className = attrValue;
					classnameRegion = r;
				}
				else if (attrName.equals("type")) {//$NON-NLS-1$
					type = attrValue;
					typeRegion = r;
				}
				else if (attrName.equals("beanName")) { //$NON-NLS-1$
					beanName = attrValue;
					beanNameRegion = r;
				}
			}
		}

		if (id != null) {
			// The id is not a valid Java identifier
			if (!isValidJavaIdentifier(id) && idRegion != null) {
				Object problem = createJSPProblem(IJSPProblem.UseBeanInvalidID, IProblem.ParsingErrorInvalidToken, MessageFormat.format(JSPCoreMessages.JSPTranslator_0, new String[]{id}), container.getStartOffset(idRegion), container.getTextEndOffset(idRegion) - 1);
				fTranslationProblems.add(problem);
			}
			// No Type information is provided
			if (((type == null && className == null) || (type == null && beanName != null)) && idRegion != null) {
				Object problem = createJSPProblem(IJSPProblem.UseBeanMissingTypeInfo, IProblem.UndefinedType, NLS.bind(JSPCoreMessages.JSPTranslator_3, new String[]{id}), container.getStartOffset(idRegion), container.getTextEndOffset(idRegion) - 1);
				fTranslationProblems.add(problem);
			}
			// Cannot specify both a class and a beanName
			if (className != null && beanName != null && beanNameRegion != null) {
				ITextRegion nameRegion = container.getRegions().get(1);
				Object problem = createJSPProblem(IJSPProblem.UseBeanAmbiguousType, IProblem.AmbiguousType, JSPCoreMessages.JSPTranslator_2, container.getStartOffset(nameRegion), container.getTextEndOffset(nameRegion) - 1);
				fTranslationProblems.add(problem);
			}
			// Only have a class or a beanName at this point, and potentially
			// a type
			// has id w/ type and/or classname/beanName
			// Type id = new Classname/Beanname();
			// or
			// Type id = null; // if there is no classname or beanname
			if ((type != null || className != null)) {
				if (type == null) {
					type = className;
					typeRegion = classnameRegion;
				}

				if (!isTypeFound(type)) {
					Object problem = createJSPProblem(IJSPProblem.F_PROBLEM_ID_LITERAL, IProblem.UndefinedType, MessageFormat.format(JSPCoreMessages.JSPTranslator_1, new String[]{type}), container.getStartOffset(typeRegion), container.getTextEndOffset(typeRegion) - 1);
					fTranslationProblems.add(problem);
				}
				else {
					String prefix = type + " " + id + " = "; //$NON-NLS-1$ //$NON-NLS-2$
					String suffix = "null;" + ENDL; //$NON-NLS-1$
					if (className != null)
						suffix = "new " + className + "();" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$
					else if (beanName != null)
						suffix = "(" + type + ") java.beans.Beans.instantiate(getClass().getClassLoader(), \"" + beanName + "\");" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					appendToBuffer(prefix + suffix, fUserCode, true, fCurrentNode);
				}
			}
		}
	}

	/**
	 * @param type
	 * @return
	 */
	private boolean isTypeFound(String typeName) {
		IType type = null;
		IProject project = getFile().getProject();
		try {
			IJavaProject p = JavaCore.create(project);
			if (p.exists()) {
				if (typeName.indexOf('<') > 0 && typeName.indexOf('>') > 0) {
					StringTokenizer toker = new StringTokenizer(typeName);
					String generic = toker.nextToken("<");
					String element = toker.nextToken(">");
					IType genericType = p.findType(generic);
					IType elementType = p.findType(element);
					return genericType!= null && genericType.exists() && elementType!= null && elementType.exists();
				}
				type = p.findType(typeName);
				return type != null && type.exists();
			}
		}
		catch (JavaModelException e) {
			// Not a Java Project
		}
		catch(NoSuchElementException e) {
			// StringTokenizer failure with unsupported syntax
		}
		return true;
	}

	private boolean isValidJavaIdentifier(String id) {
		char[] idChars = id.toCharArray();
		if (idChars.length < 1)
			return false;
		boolean isValid = Character.isJavaIdentifierStart(idChars[0]);
		for (int i = 1; i < idChars.length; i++) {
			isValid = isValid && Character.isJavaIdentifierPart(idChars[i]);
		}
		return isValid;
	}

	final public int getCursorPosition() {
		return fCursorPosition;
	}

	protected boolean isCursorInExpression() {
		return fCursorInExpression;
	}

	protected void setCursorInExpression(boolean in) {
		fCursorInExpression = in;
	}

	final public void setSourceCursor(int i) {
		fSourcePosition = i;
	}

	final public int getSourcePosition() {
		return fSourcePosition;
	}

	final public TLDCMDocumentManager getTLDCMDocumentManager() {
		return TaglibController.getTLDCMDocumentManager(fStructuredDocument);
	}

	final public void setRelativeOffset(int relativeOffset) {
		this.fRelativeOffset = relativeOffset;
	}

	final public int getRelativeOffset() {
		return fRelativeOffset;
	}

	private void setCursorOwner(StringBuffer cursorOwner) {
		this.fCursorOwner = cursorOwner;
	}

	final public StringBuffer getCursorOwner() {
		return fCursorOwner;
	}

	private IStructuredDocumentRegion setCurrentNode(IStructuredDocumentRegion currentNode) {
		return this.fCurrentNode = currentNode;
	}

	final public IStructuredDocumentRegion getCurrentNode() {
		return fCurrentNode;
	}

	public ArrayList getELProblems() {
		return fELProblems;
	}

	public IStructuredDocument getStructuredDocument() {
		return fStructuredDocument;
	}

	private IPath getModelPath() {
		IPath path = null;
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getStructuredDocument());
		try {
			if (sModel != null)
				path = new Path(sModel.getBaseLocation());
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return path;
	}

	private void translateCodas() {
		fProcessIncludes = false;
		IPath modelpath = getModelPath();
		if (modelpath != null) {
			PropertyGroup[] propertyGroups = DeploymentDescriptorPropertyCache.getInstance().getPropertyGroups(modelpath);
			for (int j = 0; j < propertyGroups.length; j++) {
				IPath[] codas = propertyGroups[j].getIncludeCoda();
				for (int i = 0; i < codas.length; i++) {
					if (!getIncludes().contains(codas[i].toString()) && !codas[i].equals(modelpath)) {
						getIncludes().push(codas[i]);
						JSPIncludeRegionHelper helper = new JSPIncludeRegionHelper(this);
						helper.parse(codas[i].toString());
						getIncludes().pop();
					}
				}
			}
		}
		fProcessIncludes = true;
	}

	private void translatePreludes() {
		fProcessIncludes = false;
		IPath modelpath = getModelPath();
		if (modelpath != null) {
			PropertyGroup[] propertyGroups = DeploymentDescriptorPropertyCache.getInstance().getPropertyGroups(modelpath);
			for (int j = 0; j < propertyGroups.length; j++) {
				IPath[] preludes = propertyGroups[j].getIncludePrelude();
				for (int i = 0; i < preludes.length; i++) {
					if (!getIncludes().contains(preludes[i].toString()) && !preludes[i].equals(modelpath)) {
						getIncludes().push(preludes[i]);
						JSPIncludeRegionHelper helper = new JSPIncludeRegionHelper(this);
						helper.parse(preludes[i].toString());
						getIncludes().pop();
					}
				}
			}
		}
		fProcessIncludes = true;
	}
}
