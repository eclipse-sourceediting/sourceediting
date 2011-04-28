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
package org.eclipse.jst.jsp.core.internal.taglib;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDAttributeDeclaration;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDVariable;
import org.eclipse.jst.jsp.core.internal.java.IJSPProblem;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.NotImplementedException;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.AbstractMemoryListener;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.osgi.service.event.Event;

import com.ibm.icu.text.MessageFormat;

/**
 * This class helps find TaglibVariables in a JSP file.
 */
public class TaglibHelper {

	private static final String ITERATION_QUALIFIER = "javax.servlet.jsp.tagext"; //$NON-NLS-1$
	private static final String ITERATION_NAME = "IterationTag"; //$NON-NLS-1$
	
	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibvars"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private IProject fProject = null;
	private ClassLoader fLoader = null;

	private IJavaProject fJavaProject;
	
	/**
	 * A cache of class names that the class loader could not find.
	 * Because the TaglibHelper is destroyed and recreated whenever
	 * the classpath changes this cache will not become stale
	 */
	private Set fNotFoundClasses = null;

	private Map fClassMap = null;

	/**
	 * Used to keep the {@link #fNotFoundClasses} cache clean when memory is low
	 */
	private MemoryListener fMemoryListener;

	public TaglibHelper(IProject project) {
		super();
		setProject(project);
		fMemoryListener = new MemoryListener();
		fMemoryListener.connect();
		fNotFoundClasses = new HashSet();
		fClassMap = Collections.synchronizedMap(new HashMap());
	}

	/**
	 * Checks that <code>type</code> implements an IterationTag
	 * 
	 * @param type
	 * @return true if <code>type</code> implements IterationTag
	 * @throws JavaModelException
	 * @throws ClassNotFoundException thrown when the <code>type</code> is null
	 */
	private boolean isIterationTag(IType type) throws JavaModelException, ClassNotFoundException {
		if (type == null) {
			throw new ClassNotFoundException();
		}
		synchronized (fClassMap) {
			if (fClassMap.containsKey(type.getFullyQualifiedName()))
				return ((Boolean) fClassMap.get(type.getFullyQualifiedName())).booleanValue();
		}

		String signature;
		String qualifier;
		String name;
		String[] interfaces = type.getSuperInterfaceTypeSignatures();
		boolean isIteration = false;
		// Check any super interfaces for the iteration tag
		for (int i = 0; i < interfaces.length; i++) {
			// For source files, the interface may need to be resolved
			String erasureSig = Signature.getTypeErasure(interfaces[i]);
			qualifier = Signature.getSignatureQualifier(erasureSig);
			name = Signature.getSignatureSimpleName(erasureSig);
			// Interface type is unresolved
			if (erasureSig.charAt(0) == Signature.C_UNRESOLVED) {
				String[][] types = type.resolveType(getQualifiedType(qualifier, name));
				// Type was resolved
				if (types != null && types.length > 0) {
					isIteration = handleInterface(type, types[0][0], types[0][1]);
				}
			}
			else {
				isIteration = handleInterface(type, qualifier, name);
			}
			if (isIteration)
				return true;
		}

		signature = type.getSuperclassTypeSignature();
		if (signature != null) {
			String erasureSig = Signature.getTypeErasure(signature);
			qualifier = Signature.getSignatureQualifier(erasureSig);
			name = Signature.getSignatureSimpleName(erasureSig);
			// superclass was unresolved
			if (erasureSig.charAt(0) == Signature.C_UNRESOLVED) {
				String[][] types = type.resolveType(getQualifiedType(qualifier, name));
				// Type was resolved
				if (types != null && types.length > 0) {
					isIteration = isIterationTag(fJavaProject.findType(types[0][0], types[0][1]));
				}
			}
			else {
				isIteration = isIterationTag(fJavaProject.findType(qualifier, name));
			}
		}
		fClassMap.put(type.getFullyQualifiedName(), Boolean.valueOf(isIteration));
		return isIteration;
	}

	private boolean handleInterface(IType type, String qualifier, String name) throws JavaModelException, ClassNotFoundException {
		boolean isIteration = false;
		// Qualified interface is an iteration tag
		if (ITERATION_QUALIFIER.equals(qualifier) && ITERATION_NAME.equals(name))
			isIteration = true;
		// Check ancestors of this interface
		else
			isIteration = isIterationTag(fJavaProject.findType(qualifier, name));

		fClassMap.put(type.getFullyQualifiedName(), Boolean.valueOf(isIteration));
		return isIteration;
	}

	private String getQualifiedType(String qualifier, String name) {
		StringBuffer qual = new StringBuffer(qualifier);
		if (qual.length() > 0)
			qual.append('.');
		qual.append(name);
		return qual.toString();
	}

	private boolean isIterationTag(TLDElementDeclaration elementDecl, IStructuredDocument document, ITextRegionCollection customTag, List problems) {
		String className = elementDecl.getTagclass();
		if (className == null || className.length() == 0 || fProject == null || fNotFoundClasses.contains(className))
			return false;

		try {
			synchronized (fClassMap) {
				if (fClassMap.containsKey(className))
					return ((Boolean) fClassMap.get(className)).booleanValue();
			}
			return isIterationTag(fJavaProject.findType(className));
		} catch (ClassNotFoundException e) {
			//the class could not be found so add it to the cache
			fNotFoundClasses.add(className);

			Object createdProblem = createJSPProblem(document, customTag, IJSPProblem.TagClassNotFound, JSPCoreMessages.TaglibHelper_3, className, true);
			if (createdProblem != null)
				problems.add(createdProblem);
			if (DEBUG)
				Logger.logException(className, e);
		} catch (JavaModelException e) {
			if (DEBUG)
				Logger.logException(className, e);
		} catch (Exception e) {
			// this is 3rd party code, need to catch all errors
			if (DEBUG)
				Logger.logException(className, e);
		} catch (Error e) {
			// this is 3rd party code, need to catch all errors
			if (DEBUG)
				Logger.logException(className, e);
		}

		return false;
	}

	public CustomTag getCustomTag(String tagToAdd, IStructuredDocument structuredDoc, ITextRegionCollection customTag, List problems) {
		List results = new ArrayList();
		boolean isIterationTag = false;
		String tagClass = null;
		String teiClass = null;
		if (problems == null)
			problems = new ArrayList();
		ModelQuery mq = getModelQuery(structuredDoc);
		if (mq != null) {
			TLDCMDocumentManager mgr = TaglibController.getTLDCMDocumentManager(structuredDoc);

			if (mgr != null) {

				List trackers = mgr.getCMDocumentTrackers(-1);
				Iterator taglibs = trackers.iterator();
	
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

						tagClass = tldElementDecl.getTagclass();
						teiClass = tldElementDecl.getTeiclass();
						isIterationTag = isIterationTag(tldElementDecl, structuredDoc, customTag, problems);
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
						break;
					}
				}
			}
		}

		return new CustomTag(tagToAdd, tagClass, teiClass, (TaglibVariable[]) results.toArray(new TaglibVariable[results.size()]), isIterationTag);
	}
	/**
	 * @param tagToAdd
	 *            is the name of the tag whose variables we want
	 * @param structuredDoc
	 *            is the IStructuredDocument where the tag is found
	 * @param customTag
	 *            is the IStructuredDocumentRegion opening tag for the custom
	 *            tag
	 * @param problems problems that are generated while creating variables are added to this collection
	 */
	public TaglibVariable[] getTaglibVariables(String tagToAdd, IStructuredDocument structuredDoc, ITextRegionCollection customTag, List problems) {

		List results = new ArrayList();
		if (problems == null)
			problems = new ArrayList();
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
		if (teiClassname == null || teiClassname.length() == 0 || fJavaProject == null || fNotFoundClasses.contains(teiClassname))
			return;

		ClassLoader loader = getClassloader();

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
			//the class could not be found so add it to the cache
			fNotFoundClasses.add(teiClassname);

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
			end = customTag.getTextEndOffset(customTag.getRegions().get(customTag.getNumberOfRegions() - 2)) - 1;
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
	private TagInfo getTagInfo(final TLDElementDeclaration decl, TagExtraInfo tei, String prefix, String uri) {

		TagLibraryInfo libInfo = new TagLibraryInfoImpl(prefix, uri, decl);

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
		final int size = regions.size();
		for (int i = 2; i < size; i++) {
			r = regions.get(i);
			// check if attr name
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				attrName = customTag.getText(r);
				// check equals is next region
				if (size > ++i) {
					r = regions.get(i);
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS && size > ++i) {
						// get attr value
						r = regions.get(i);
						final String type = r.getType();
						if (type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
							// attributes in our document have quotes, so we
							// need to strip them
							attrValue = StringUtils.stripQuotes(customTag.getText(r));
							tagDataTable.put(attrName, attrValue);
						}
						else if (type == DOMJSPRegionContexts.JSP_TAG_ATTRIBUTE_VALUE_DQUOTE || type == DOMJSPRegionContexts.JSP_TAG_ATTRIBUTE_VALUE_SQUOTE) {
							final StringBuffer buffer = new StringBuffer();
							while (size > ++i && (r = regions.get(i)).getType() != type) {
								buffer.append(customTag.getText(r));
							}
							tagDataTable.put(attrName, buffer.toString());
						}
					}
				}
			}
		}

		tagDataTable.put("jsp:id", customTag.getText(regions.get(1)) + "_" + customTag.getStartOffset()); //$NON-NLS-1$ 

		return tagDataTable;
	}

	private ClassLoader getClassloader() {
		if (fLoader == null) {
			fLoader = new BuildPathClassLoader(this.getClass().getClassLoader(), fJavaProject);
		}
		return fLoader;
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

	private void validateTagClass(IStructuredDocument document, ITextRegionCollection customTag, TLDElementDeclaration decl, List problems) {
		// skip if from a tag file
		if (TLDElementDeclaration.SOURCE_TAG_FILE.equals(decl.getProperty(TLDElementDeclaration.TAG_SOURCE)) || fJavaProject == null) {
			return;
		}

		String tagClassname = decl.getTagclass();
		Object tagClass = null;
		if (tagClassname != null && tagClassname.length() > 0 && fJavaProject.exists()) {
			try {
				tagClass = fJavaProject.findType(tagClassname);
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

	/**
	 * 
	 */
	public void dispose() {
		fLoader = null;
		fJavaProject = null;
		fProject = null;
		fNotFoundClasses = null;
		fClassMap = null;
		fMemoryListener.disconnect();
		fMemoryListener = null;
	}

	public void invalidateClass(String className) {
		fClassMap.remove(className);
	}

	/**
	 * <p>A {@link AbstractMemoryListener} that clears the {@link #fNotFoundClasses} cache
	 * whenever specific memory events are received.</p>
	 * 
	 * <p>Events:
	 * <ul>
	 * <li>{@link AbstractMemoryListener#SEV_NORMAL}</li>
	 * <li>{@link AbstractMemoryListener#SEV_SERIOUS}</li>
	 * <li>{@link AbstractMemoryListener#SEV_CRITICAL}</li>
	 * </ul>
	 * </p>
	 */
	private class MemoryListener extends AbstractMemoryListener {
		/**
		 * <p>Constructor causes this listener to listen for specific memory events.</p>
		 * <p>Events:
		 * <ul>
		 * <li>{@link AbstractMemoryListener#SEV_NORMAL}</li>
		 * <li>{@link AbstractMemoryListener#SEV_SERIOUS}</li>
		 * <li>{@link AbstractMemoryListener#SEV_CRITICAL}</li>
		 * </ul>
		 * </p>
		 */
		MemoryListener() {
			super(new String[] { SEV_NORMAL, SEV_SERIOUS, SEV_CRITICAL });
		}

		/**
		 * On any memory event we handle clear out the project descriptions
		 * 
		 * @see org.eclipse.jst.jsp.core.internal.util.AbstractMemoryListener#handleMemoryEvent(org.osgi.service.event.Event)
		 */
		protected void handleMemoryEvent(Event event) {
			/* if running low on memory then this cache can be cleared
			 * and rebuilt at the expense of processing time
			 */
			fNotFoundClasses.clear();
			fClassMap.clear();
		}

	}

	class TagLibraryInfoImpl extends TagLibraryInfo {
		TLDElementDeclaration decl;

		TagLibraryInfoImpl(String prefix, String uri, TLDElementDeclaration decl){
			super(prefix, uri);
			this.decl = decl;
		}

		public String getURI() {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getURI();
		}

		public String getPrefixString() {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getPrefixString();
		}

		public String getShortName() {
			return ((TLDDocument)decl.getOwnerDocument()).getShortname();
		}

		public String getReliableURN() {
			return ((TLDDocument)decl.getOwnerDocument()).getUri();
		}

		public String getInfoString() {
			return ((TLDDocument)decl.getOwnerDocument()).getInfo();
		}

		public String getRequiredVersion() {
			return ((TLDDocument)decl.getOwnerDocument()).getJspversion();
		}

		public TagInfo[] getTags() {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getTags();
		}

		public TagFileInfo[] getTagFiles() {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getTagFiles();
		}

		public TagInfo getTag(String shortname) {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getTag(shortname);
		}

		public TagFileInfo getTagFile(String shortname) {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getTagFile(shortname);
		}

		public FunctionInfo[] getFunctions() {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return super.getFunctions();
		}

		public FunctionInfo getFunction(String name) {
			new NotImplementedException(name).printStackTrace();
			return super.getFunction(name);
		}

		public TagLibraryInfo[] getTagLibraryInfos()  {
			if (Platform.inDebugMode())
				new NotImplementedException().printStackTrace();
			return new TagLibraryInfo[] { this };
		}
	}
}
