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
package org.eclipse.jst.jsp.core.internal.taglib;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDAttributeDeclaration;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDVariable;
import org.eclipse.jst.jsp.core.internal.java.IJSPProblem;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

import com.ibm.icu.text.MessageFormat;

/**
 * This class helps find TaglibVariables in a JSP file.
 * 
 * @author pavery
 */
public class TaglibHelper {

	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibvars"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private IProject fProject = null;
	private TaglibClassLoader fLoader = null;

	private Set fProjectEntries = null;
	private Map fTranslationProblems = null;
	private Set fContainerEntries = null;
	private IJavaProject fJavaProject;

	public TaglibHelper(IProject project) {
		setProject(project);
		fProjectEntries = new HashSet();
		fContainerEntries = new HashSet();
		fTranslationProblems = new HashMap();
	}

	/**
	 * @param tagToAdd
	 *            is the name of the tag whose variables we want
	 * @param structuredDoc
	 *            is the IStructuredDocument where the tag is found
	 * @param customTag
	 *            is the IStructuredDocumentRegion opening tag for the custom
	 *            tag
	 */
	public TaglibVariable[] getTaglibVariables(String tagToAdd, IStructuredDocument structuredDoc, ITextRegionCollection customTag) {

		List results = new ArrayList();
		List problems = new ArrayList();
		ModelQuery mq = getModelQuery(structuredDoc);
		if (mq != null) {
			TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(structuredDoc);

			// TaglibSupport support = ((TaglibModelQuery)
			// mq).getTaglibSupport();
			if (mgr == null)
				return new TaglibVariable[0];

			List trackers = mgr.getCMDocumentTrackers(-1);
			Iterator taglibs = trackers.iterator();

			// TaglibSupport support = ((TaglibModelQuery)
			// mq).getTaglibSupport();
			// if (support == null)
			// return new TaglibVariable[0];
			//
			// Iterator taglibs =
			// support.getCMDocuments(customTag.getStartOffset()).iterator();
			CMDocument doc = null;
			CMNamedNodeMap elements = null;
			while (taglibs.hasNext()) {
				doc = (CMDocument) taglibs.next();
				CMNode node = null;
				if ((elements = doc.getElements()) != null && (node = elements.getNamedItem(tagToAdd)) != null && node.getNodeType() == CMNode.ELEMENT_DECLARATION) {

					if (node instanceof CMNodeWrapper) {
						node = ((CMNodeWrapper) node).getOriginNode();
					}
					TLDElementDeclaration tldElementDecl = (TLDElementDeclaration) node;

					/*
					 * Although clearly not the right place to add validation
					 * design-wise, this is the first time we have the
					 * necessary information to validate the tag class.
					 */
					validateTagClass(structuredDoc, customTag, tldElementDecl, problems);

					// 1.2+ taglib style
					addVariables(results, node, customTag);

					// for 1.1 need more info from taglib tracker
					if (doc instanceof TaglibTracker) {
						String uri = ((TaglibTracker) doc).getURI();
						String prefix = ((TaglibTracker) doc).getPrefix();
						// only for 1.1 taglibs
						addTEIVariables(structuredDoc, customTag, results, tldElementDecl, prefix, uri, problems);
					}
				}
			}
		}

		IPath location = TaglibController.getLocation(structuredDoc);
		if (location != null) {
			fTranslationProblems.put(location, problems);
		}

		return (TaglibVariable[]) results.toArray(new TaglibVariable[results.size()]);
	}

	/**
	 * Adds 1.2 style TaglibVariables to the results list.
	 * 
	 * @param results
	 *            list where the <code>TaglibVariable</code> s are added
	 * @param node
	 */
	private void addVariables(List results, CMNode node, ITextRegionCollection customTag) {

		List list = ((TLDElementDeclaration) node).getVariables();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			TLDVariable var = (TLDVariable) it.next();
			if (!var.getDeclare())
				continue;

			String varName = var.getNameGiven();
			if (varName == null) {
				// 2.0
				varName = var.getAlias();
			}
			if (varName == null) {
				String attrName = var.getNameFromAttribute();
				/*
				 * Iterate through the document region to find the
				 * corresponding attribute name, and then use its value
				 */
				ITextRegionList regions = customTag.getRegions();
				boolean attrNameFound = false;
				for (int i = 2; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (DOMRegionContext.XML_TAG_ATTRIBUTE_NAME.equals(region.getType())) {
						attrNameFound = attrName.equals(customTag.getText(region));
					}
					if (attrNameFound && DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE.equals(region.getType())) {
						varName = StringUtils.strip(customTag.getText(region));
					}
				}
			}
			if (varName != null) {
				String varClass = "java.lang.String"; // the default
				// class...//$NON-NLS-1$
				if (var.getVariableClass() != null) {
					varClass = var.getVariableClass();
				}
				results.add(new TaglibVariable(varClass, varName, var.getScope(), var.getDescription()));
			}
		}
	}

	/**
	 * Adds 1.1 style TaglibVariables (defined in a TagExtraInfo class) to the
	 * results list. Also reports problems with the tag and tei classes in
	 * fTranslatorProblems.
	 * 
	 * @param customTag
	 * @param results
	 *            list where the <code>TaglibVariable</code> s are added
	 * @param decl
	 *            TLDElementDeclaration for the custom tag
	 * @param prefix
	 *            custom tag prefix
	 * @param uri
	 *            URI where the tld can be found
	 */
	private void addTEIVariables(IStructuredDocument document, ITextRegionCollection customTag, List results, TLDElementDeclaration decl, String prefix, String uri, List problems) {
		String teiClassname = decl.getTeiclass();
		if (teiClassname == null || teiClassname.length() == 0 || fJavaProject == null)
			return;

		TaglibClassLoader loader = getClassloader();

		Class teiClass = null;
		try {
			/*
			 * JDT could tell us about it, but loading and calling it would
			 * still take time
			 */
			teiClass = Class.forName(teiClassname, true, loader);
			if (teiClass != null) {
				Object teiObject = teiClass.newInstance();
				if (TagExtraInfo.class.isInstance(teiObject)) {
					TagExtraInfo tei = (TagExtraInfo) teiObject;
					Hashtable tagDataTable = extractTagData(customTag);
					TagInfo info = getTagInfo(decl, tei, prefix, uri);
					if (info != null) {
						tei.setTagInfo(info);

						// add to results
						TagData td = new TagData(tagDataTable);

						VariableInfo[] vInfos = tei.getVariableInfo(td);
						if (vInfos != null) {
							for (int i = 0; i < vInfos.length; i++) {
								results.add(new TaglibVariable(vInfos[i].getClassName(), vInfos[i].getVarName(), vInfos[i].getScope(), decl.getDescription()));
							}
						}

						ValidationMessage[] messages = tei.validate(td);
						if (messages != null && messages.length > 0) {
							for (int i = 0; i < messages.length; i++) {
								Object createdProblem = createValidationMessageProblem(document, customTag, messages[i].getMessage());
								if (createdProblem != null) {
									problems.add(createdProblem);
								}
							}
						}
					}
				}
				else {
					Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TEIClassMisc, JSPCoreMessages.TaglibHelper_2, teiClassname, true);
					if (createdProblem != null) {
						problems.add(createdProblem);
					}
					// this is 3rd party code, need to catch all exceptions
					if (DEBUG) {
						Logger.log(Logger.WARNING, teiClassname + " is not a subclass of TaxExtraInfo"); //$NON-NLS-1$ 
					}
				}
			}
		}
		catch (ClassNotFoundException e) {
			Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TEIClassNotFound, JSPCoreMessages.TaglibHelper_0, teiClassname, true);
			if (createdProblem != null) {
				problems.add(createdProblem);
			}
			// TEI class wasn't on build path
			if (DEBUG)
				logException(teiClassname, e);
		}
		catch (InstantiationException e) {
			Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TEIClassNotInstantiated, JSPCoreMessages.TaglibHelper_1, teiClassname, true);
			if (createdProblem != null) {
				problems.add(createdProblem);
			}
			// TEI class couldn't be instantiated
			if (DEBUG)
				logException(teiClassname, e);
		}
		catch (IllegalAccessException e) {
			if (DEBUG)
				logException(teiClassname, e);
		}
		// catch (ClassCastException e) {
		// // TEI class wasn't really a subclass of TagExtraInfo
		// if (DEBUG)
		// logException(teiClassname, e);
		// }
		catch (Exception e) {
			Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TEIClassMisc, JSPCoreMessages.TaglibHelper_2, teiClassname, true);
			if (createdProblem != null) {
				problems.add(createdProblem);
			}
			// this is 3rd party code, need to catch all exceptions
			if (DEBUG)
				logException(teiClassname, e);
		}
		catch (Error e) {
			// this is 3rd party code, need to catch all errors
			Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TEIClassNotInstantiated, JSPCoreMessages.TaglibHelper_1, teiClassname, true);
			if (createdProblem != null) {
				problems.add(createdProblem);
			}
			if (DEBUG)
				logException(teiClassname, e);
		}
		finally {
			// Thread.currentThread().setContextClassLoader(oldLoader);
		}
	}

	/**
	 * @param customTag
	 * @param teiClass
	 * @return
	 */
	private Object createJSPProblem(final IStructuredDocument document, final ITextRegionCollection customTag, final int problemID, final String messageKey, final String argument, boolean preferVars) {
		final String tagname = customTag.getText(customTag.getRegions().get(1));

		final int start;
		if (customTag.getNumberOfRegions() > 1) {
			start = customTag.getStartOffset(customTag.getRegions().get(1));
		}
		else {
			start = customTag.getStartOffset();
		}

		final int end;
		if (customTag.getNumberOfRegions() > 1) {
			end = customTag.getTextEndOffset(customTag.getRegions().get(1)) - 1;
		}
		else {
			end = customTag.getTextEndOffset() - 1;
		}

		final int line = document.getLineOfOffset(start);

		final char[] name;
		IPath location = TaglibController.getLocation(document);
		if (location == null) {
			name = new char[0];
		}
		else {
			name = location.toString().toCharArray();
		}

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
				return name;
			}

			public String getMessage() {
				return MessageFormat.format(messageKey, new String[]{tagname, argument});
			}

			public int getID() {
				return problemID;
			}

			public String[] getArguments() {
				return new String[0];
			}

			public int getEID() {
				return problemID;
			}
		};
	}

	/**
	 * @param customTag
	 * @param validationMessage
	 * @return
	 */
	private Object createValidationMessageProblem(final IStructuredDocument document, final ITextRegionCollection customTag, final String validationMessage) {
		final int start;
		if (customTag.getNumberOfRegions() > 3) {
			start = customTag.getStartOffset(customTag.getRegions().get(2));
		}
		else if (customTag.getNumberOfRegions() > 1) {
			start = customTag.getStartOffset(customTag.getRegions().get(1));
		}
		else {
			start = customTag.getStartOffset();
		}

		final int end;
		if (customTag.getNumberOfRegions() > 3) {
			end = customTag.getTextEndOffset(customTag.getRegions().get(customTag.getNumberOfRegions() - 2));
		}
		else if (customTag.getNumberOfRegions() > 1) {
			end = customTag.getTextEndOffset(customTag.getRegions().get(1)) - 1;
		}
		else {
			end = customTag.getTextEndOffset();
		}

		final int line = document.getLineOfOffset(start);

		final char[] name;
		IPath location = TaglibController.getLocation(document);
		if (location == null) {
			name = new char[0];
		}
		else {
			name = location.toString().toCharArray();
		}

		return new IJSPProblem() {
			public void setSourceStart(int sourceStart) {
			}

			public void setSourceLineNumber(int lineNumber) {
			}

			public void setSourceEnd(int sourceEnd) {
			}

			public boolean isWarning() {
				return true;
			}

			public boolean isError() {
				return false;
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
				return name;
			}

			public String getMessage() {
				return validationMessage;
			}

			public int getID() {
				return getEID();
			}

			public String[] getArguments() {
				return new String[0];
			}

			public int getEID() {
				return IJSPProblem.TEIValidationMessage;
			}
		};
	}

	/**
	 * @param decl
	 * @return the TagInfo for the TLDELementDeclaration if the declaration is
	 *         valid, otherwise null
	 */
	private TagInfo getTagInfo(TLDElementDeclaration decl, TagExtraInfo tei, String prefix, String uri) {

		TagLibraryInfo libInfo = new TagLibraryInfo(prefix, uri) { /*
																	 * dummy
																	 * impl
																	 */
		};

		CMNamedNodeMap attrs = decl.getAttributes();
		TagAttributeInfo[] attrInfos = new TagAttributeInfo[attrs.getLength()];
		TLDAttributeDeclaration attr = null;
		String type = ""; //$NON-NLS-1$ 

		// get tag attribute infos
		for (int i = 0; i < attrs.getLength(); i++) {
			attr = (TLDAttributeDeclaration) attrs.item(i);
			type = attr.getType();
			// default value for type is String
			if (attr.getType() == null || attr.getType().equals("")) //$NON-NLS-1$ 
				type = "java.lang.String"; //$NON-NLS-1$ 
			attrInfos[i] = new TagAttributeInfo(attr.getAttrName(), attr.isRequired(), type, false);
		}

		String tagName = decl.getNodeName();
		String tagClass = decl.getTagclass();
		String bodyContent = decl.getBodycontent();
		if (tagName != null && tagClass != null && bodyContent != null)
			return new TagInfo(tagName, tagClass, bodyContent, decl.getInfo(), libInfo, tei, attrInfos);
		return null;

	}

	/**
	 * @param e
	 */
	private void logException(String teiClassname, Throwable e) {

		String message = "teiClassname: ["; //$NON-NLS-1$ 
		if (teiClassname != null)
			message += teiClassname;
		message += "]"; //$NON-NLS-1$
		Logger.logException(message, e);
	}

	/**
	 * Returns all attribute -> value pairs for the tag in a Hashtable.
	 * 
	 * @param customTag
	 * @return
	 */
	private Hashtable extractTagData(ITextRegionCollection customTag) {
		Hashtable tagDataTable = new Hashtable();
		ITextRegionList regions = customTag.getRegions();
		ITextRegion r = null;
		String attrName = ""; //$NON-NLS-1$
		String attrValue = ""; //$NON-NLS-1$
		for (int i = 2; i < regions.size(); i++) {
			r = regions.get(i);
			// check if attr name
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				attrName = customTag.getText(r);
				// check equals is next region
				if (regions.size() > ++i) {
					r = regions.get(i);
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS && regions.size() > ++i) {
						// get attr value
						r = regions.get(i);
						if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
							// attributes in our document have quotes, so we
							// need to strip them
							attrValue = StringUtils.stripQuotes(customTag.getText(r));
							tagDataTable.put(attrName, attrValue);
						}
					}
				}
			}
		}

		tagDataTable.put("jsp:id", customTag.getText(regions.get(1)) + "_" + customTag.getStartOffset()); //$NON-NLS-1$ 

		return tagDataTable;
	}

	private TaglibClassLoader getClassloader() {
		if (fLoader == null) {
			fLoader = new TaglibClassLoader(this.getClass().getClassLoader());
			fProjectEntries.clear();
			fContainerEntries.clear();
			addClasspathEntriesForProject(getProject(), fLoader);
		}
		return fLoader;
	}

	/**
	 * @param loader
	 */
	private void addClasspathEntriesForProject(IProject p, TaglibClassLoader loader) {

		// avoid infinite recursion and closed project
		if (!p.isAccessible() || fProjectEntries.contains(p.getFullPath().toString()))
			return;
		fProjectEntries.add(p.getFullPath().toString());

		// add things on classpath that we are interested in
		try {
			if (p.hasNature(JavaCore.NATURE_ID)) {

				IJavaProject project = JavaCore.create(p);

				try {
					IClasspathEntry[] entries = project.getRawClasspath();
					addDefaultDirEntry(loader, project);
					addClasspathEntries(loader, project, entries);
				}
				catch (JavaModelException e) {
					Logger.logException(e);
				}
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
	}

	private void addClasspathEntries(TaglibClassLoader loader, IJavaProject project, IClasspathEntry[] entries) throws JavaModelException {
		IClasspathEntry entry;
		for (int i = 0; i < entries.length; i++) {

			entry = entries[i];
			if (DEBUG)
				System.out.println("current entry is: " + entry); //$NON-NLS-1$

			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_SOURCE :
					addSourceEntry(loader, entry);
					break;
				case IClasspathEntry.CPE_LIBRARY :
					addLibraryEntry(loader, entry.getPath());
					break;
				case IClasspathEntry.CPE_PROJECT :
					addProjectEntry(loader, entry);
					break;
				case IClasspathEntry.CPE_VARIABLE :
					addVariableEntry(loader, entry);
					break;
				case IClasspathEntry.CPE_CONTAINER :
					addContainerEntry(loader, project, entry);
					break;
			}
		}
	}

	/**
	 * @param loader
	 * @param entry
	 */
	private void addVariableEntry(TaglibClassLoader loader, IClasspathEntry entry) {
		if (DEBUG)
			System.out.println(" -> adding variable entry: [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		// variable should either be a project or a library entry

		// BUG 169431
		String variableName = entry.getPath().toString();
		IPath variablePath = JavaCore.getResolvedVariablePath(entry.getPath());
		variablePath = JavaCore.getClasspathVariable(variableName);

		// RATLC01076854
		// variable paths may not exist
		// in that case null will be returned
		if (variablePath != null) {
			if (variablePath.segments().length == 1) {
				IProject varProj = ResourcesPlugin.getWorkspace().getRoot().getProject(variablePath.toString());
				if (varProj != null && varProj.exists()) {
					addClasspathEntriesForProject(varProj, loader);
					return;
				}
			}
			addLibraryEntry(loader, variablePath);
		}
	}

	/**
	 * @param loader
	 * @param project
	 * @param entry
	 * @throws JavaModelException
	 */
	private void addContainerEntry(TaglibClassLoader loader, IJavaProject project, IClasspathEntry entry) throws JavaModelException {

		IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
		if (container != null) {
			// avoid infinite recursion
			if (!fContainerEntries.contains(container.getPath().toString())) {
				fContainerEntries.add(container.getPath().toString());

				IClasspathEntry[] cpes = container.getClasspathEntries();
				// recursive call here
				addClasspathEntries(loader, project, cpes);
			}
		}
	}

	/**
	 * @param loader
	 * @param entry
	 */
	private void addProjectEntry(TaglibClassLoader loader, IClasspathEntry entry) {
		if (DEBUG)
			System.out.println(" -> project entry: [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		IPath path = entry.getPath();
		IProject referenceProject = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
		if (referenceProject != null && referenceProject.isAccessible()) {
			addClasspathEntriesForProject(referenceProject, loader);
		}
	}

	/**
	 * @param loader
	 * @param project
	 * @param projectLocation
	 * @throws JavaModelException
	 */
	private void addDefaultDirEntry(TaglibClassLoader loader, IJavaProject project) throws JavaModelException {
		// add default bin directory for the project
		IPath outputPath = project.getOutputLocation();
		loader.addPath(outputPath);
	}

	/**
	 * @param loader
	 * @param entry
	 */
	private void addLibraryEntry(TaglibClassLoader loader, IPath libPath) {
		String jarPathString = libPath.toString();
		File file = new File(libPath.toOSString());

		if (!file.exists()) {
			loader.addPath(libPath);
		}
		else {
			loader.addDirectory(jarPathString);
		}
	}

	/**
	 * @param loader
	 * @param entry
	 */
	private void addSourceEntry(TaglibClassLoader loader, IClasspathEntry entry) {
		// add bin directory for specific entry if it has
		// one
		IPath outputLocation = entry.getOutputLocation();
		if (outputLocation != null) {
			loader.addPath(outputLocation);
		}
	}

	/**
	 * @return Returns the fModelQuery.
	 */
	public ModelQuery getModelQuery(IDocument doc) {
		IStructuredModel model = null;
		ModelQuery mq = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			mq = ModelQueryUtil.getModelQuery(model);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
		return mq;
	}


	/**
	 * @return Returns the fFile.
	 */
	public IProject getProject() {

		return fProject;
	}

	/**
	 * @param file
	 *            The fFile to set.
	 */
	public void setProject(IProject p) {
		fProject = p;
		IJavaProject javaProject = JavaCore.create(p);
		if (javaProject.exists()) {
			fJavaProject = javaProject;
		}
	}

	/**
	 * @param path
	 * @return
	 */
	public Collection getProblems(IPath path) {
		return (Collection) fTranslationProblems.remove(path);
	}

	private void validateTagClass(IStructuredDocument document, ITextRegionCollection customTag, TLDElementDeclaration decl, List problems) {
		// skip if from a tag file
		if (TLDElementDeclaration.SOURCE_TAG_FILE.equals(decl.getProperty(TLDElementDeclaration.TAG_SOURCE))) {
			return;
		}

		String tagClassname = decl.getTagclass();
		Object tagClass = null;
		if (tagClassname != null && tagClassname.length() > 0 && fJavaProject != null) {
			try {
				tagClass = fJavaProject.findType(tagClassname, new NullProgressMonitor());
			}
			catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
		if (tagClass == null) {
			Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TagClassNotFound, JSPCoreMessages.TaglibHelper_3, tagClassname, false);
			if (createdProblem != null) {
				problems.add(createdProblem);
			}
		}
	}
}
