/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITagDirRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.html.core.internal.contentmodel.JSP20Namespace;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.text.IRegionComparible;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

import com.ibm.icu.text.Collator;

/**
 * Checks for: - duplicate taglib prefix values and reserved taglib prefix
 * values in the same file
 */
public class JSPDirectiveValidator extends JSPValidator {
	/**
	 * 
	 */
	private static final String PREFERENCE_NODE_QUALIFIER = JSPCorePlugin.getDefault().getBundle().getSymbolicName();

	private static Collator collator = Collator.getInstance(Locale.US);

	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$

	private IValidator fMessageOriginator;
	private IEclipsePreferences fPreferences = null;

	private IPreferencesService fPreferencesService = null;
	private HashMap fPrefixValueRegionToDocumentRegionMap = new HashMap();
	private IJavaProject fProject = null;
	private HashMap fReservedPrefixes = new HashMap();
	private IScopeContext[] fScopes = null;
	private int fSeverityIncludeFileMissing = -1;
	private int fSeverityIncludeFileNotSpecified = -1;
	private int fSeveritySuperClassNotFound = -1;
	private int fSeverityTagdirUnresolvableURI = -1;
	private int fSeverityTaglibDuplicatePrefixWithDifferentURIs = -1;

	private int fSeverityTaglibDuplicatePrefixWithSameURIs = -1;
	private int fSeverityTaglibMissingPrefix = -1;

	private int fSeverityTaglibMissingURI = -1;


	private int fSeverityTaglibUnresolvableURI = -1;

	private HashMap fTaglibPrefixesInUse = new HashMap();

	public JSPDirectiveValidator() {
		initReservedPrefixes();
		fMessageOriginator = this;
	}

	public JSPDirectiveValidator(IValidator validator) {
		initReservedPrefixes();
		this.fMessageOriginator = validator;
	}

	/**
	 * Record that the currently validating resource depends on the given
	 * file. Only possible during batch (not source) validation.
	 * 
	 * @param file
	 */
	void addDependsOn(IResource file) {
		if (fMessageOriginator instanceof JSPBatchValidator) {
			((JSPBatchValidator) fMessageOriginator).addDependsOn(file);
		}
	}

	public void cleanup(IReporter reporter) {
		super.cleanup(reporter);
		fTaglibPrefixesInUse.clear();
		fPrefixValueRegionToDocumentRegionMap.clear();
	}

	private void collectTaglibPrefix(IStructuredDocumentRegion documentRegion, ITextRegion valueRegion, String taglibPrefix) {
		fPrefixValueRegionToDocumentRegionMap.put(valueRegion, documentRegion);

		Object o = fTaglibPrefixesInUse.get(taglibPrefix);
		if (o == null) {
			// prefix doesn't exist, remember it
			fTaglibPrefixesInUse.put(taglibPrefix, valueRegion);
		}
		else {
			List regionList = null;
			// already a List
			if (o instanceof List) {
				regionList = (List) o;
			}
			/*
			 * a single value region, create a new List and add previous
			 * valueRegion
			 */
			else {
				regionList = new ArrayList();
				regionList.add(o);
				fTaglibPrefixesInUse.put(taglibPrefix, regionList);
			}
			regionList.add(valueRegion);
		}
	}

	int getMessageSeverity(String key) {
		int sev = fPreferencesService.getInt(PREFERENCE_NODE_QUALIFIER, key, IMessage.NORMAL_SEVERITY, fScopes);
		switch (sev) {
			case ValidationMessage.ERROR :
				return IMessage.HIGH_SEVERITY;
			case ValidationMessage.WARNING :
				return IMessage.NORMAL_SEVERITY;
			case ValidationMessage.INFORMATION :
				return IMessage.LOW_SEVERITY;
			case ValidationMessage.IGNORE :
				return ValidationMessage.IGNORE;
		}
		return IMessage.NORMAL_SEVERITY;
	}
	private void initReservedPrefixes() {
		fReservedPrefixes.put("jsp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("jspx", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("java", ""); //$NON-NLS-1$ //$NON-NLS-2$
		fReservedPrefixes.put("javax", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("servlet", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sun", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
		fReservedPrefixes.put("sunw", ""); //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	private boolean isReservedTaglibPrefix(String name) {
		return fReservedPrefixes.get(name) != null;
	}
	
	private void loadPreferences(IFile file) {
		fScopes = new IScopeContext[]{new InstanceScope(), new DefaultScope()};

		fPreferencesService = Platform.getPreferencesService();
		if (file != null && file.isAccessible()) {
			ProjectScope projectScope = new ProjectScope(file.getProject());
			if (projectScope.getNode(PREFERENCE_NODE_QUALIFIER).getBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, false)) {
				fScopes = new IScopeContext[]{projectScope, new InstanceScope(), new DefaultScope()};
			}
		}

		fSeverityIncludeFileMissing = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_INCLUDE_FILE_NOT_FOUND);
		fSeverityIncludeFileNotSpecified = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_INCLUDE_NO_FILE_SPECIFIED);
		fSeverityTaglibDuplicatePrefixWithDifferentURIs = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_DIFFERENT_URIS);
		fSeverityTaglibDuplicatePrefixWithSameURIs = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_SAME_URIS);
		fSeverityTaglibMissingPrefix = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_MISSING_PREFIX);
		fSeverityTaglibMissingURI = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_MISSING_URI_OR_TAGDIR);
		fSeverityTaglibUnresolvableURI = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR);
		fSeverityTagdirUnresolvableURI = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR);
		fSeveritySuperClassNotFound = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_PAGE_SUPERCLASS_NOT_FOUND);

	}
	
	protected void performValidation(IFile f, IReporter reporter, IStructuredDocument sDoc) {
		loadPreferences(f);
		
		
		setProject(f.getProject());
		/*
		 * when validating an entire file need to clear dupes or else you're
		 * comparing between files
		 */
		fPrefixValueRegionToDocumentRegionMap.clear();
		fTaglibPrefixesInUse.clear();

		IRegionComparible comparer = null;
		if (sDoc instanceof IRegionComparible)
			comparer = (IRegionComparible) sDoc;
		
		// iterate all document regions
		IStructuredDocumentRegion region = sDoc.getFirstStructuredDocumentRegion();
		while (region != null && !reporter.isCancelled()) {
			// only checking directives
			if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				processDirective(reporter, f, sDoc, region);
			}
			// requires tag name, attribute, equals, and value
			else if (comparer != null && region.getNumberOfRegions() > 4) {
				ITextRegion nameRegion = region.getRegions().get(1);
				if (comparer.regionMatches(region.getStartOffset(nameRegion), nameRegion.getTextLength(), "jsp:include")) { //$NON-NLS-1$
					processInclude(reporter, f, sDoc, region, JSP11Namespace.ATTR_NAME_PAGE);
				}
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=295950
				else if (comparer.regionMatches(region.getStartOffset(nameRegion), 14, "jsp:directive.")) { //$NON-NLS-1$
					processDirective(reporter, f, sDoc, region);
				}

			}
			region = region.getNext();
		}

		if (!reporter.isCancelled()) {
			reportTaglibDuplicatePrefixes(f, reporter, sDoc);
		}

		fPrefixValueRegionToDocumentRegionMap.clear();
		fTaglibPrefixesInUse.clear();
		setProject(null);
		unloadPreferences();
	}

	private void processDirective(IReporter reporter, IFile file, IStructuredDocument sDoc, IStructuredDocumentRegion documentRegion) {
		String directiveName = getDirectiveName(documentRegion);

		if (directiveName.endsWith("taglib")) { //$NON-NLS-1$
			processTaglibDirective(reporter, file, sDoc, documentRegion);
		}
		else if (directiveName.equals("jsp:include")) { //$NON-NLS-1$
			processInclude(reporter, file, sDoc, documentRegion, JSP11Namespace.ATTR_NAME_PAGE);
		}
		else if (directiveName.endsWith("include")) { //$NON-NLS-1$
			processInclude(reporter, file, sDoc, documentRegion, JSP11Namespace.ATTR_NAME_FILE);
		}
		else if (directiveName.endsWith("page")) { //$NON-NLS-1$
			processPageDirective(reporter, file, sDoc, documentRegion);
		}
	}

	private void processInclude(IReporter reporter, IFile file, IStructuredDocument sDoc, IStructuredDocumentRegion documentRegion, String attrName) {
		ITextRegion fileValueRegion = getAttributeValueRegion(documentRegion, attrName);
		// There is a file and it isn't a nested region which could contain a JSP expression
		if (fileValueRegion != null && !hasNestedRegion(fileValueRegion)) {
			// file specified
			String fileValue = documentRegion.getText(fileValueRegion);
			fileValue = StringUtils.stripQuotes(fileValue);

			if (fileValue.length() == 0 && fSeverityIncludeFileNotSpecified != ValidationMessage.IGNORE) {
				// file value is specified but empty
				String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_3, attrName);
				LocalizedMessage message = new LocalizedMessage(fSeverityIncludeFileNotSpecified, msgText, file);
				int start = documentRegion.getStartOffset(fileValueRegion);
				int length = fileValueRegion.getTextLength();
				int lineNo = sDoc.getLineOfOffset(start);
				message.setLineNo(lineNo + 1);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(fMessageOriginator, message);
			}
			else if (DeploymentDescriptorPropertyCache.getInstance().getURLMapping(file.getFullPath(), fileValue) == null) {
				IPath testPath = FacetModuleCoreSupport.resolve(file.getFullPath(), fileValue);
				if (testPath.segmentCount() > 1) {
					IFile testFile = file.getWorkspace().getRoot().getFile(testPath);
					addDependsOn(testFile);
					if (!testFile.isAccessible()) {
						if (fSeverityIncludeFileMissing != ValidationMessage.IGNORE) {
							// File not found
							String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_4, new String[]{fileValue, testPath.toString()});
							LocalizedMessage message = new LocalizedMessage(fSeverityIncludeFileMissing, msgText, file);
							int start = documentRegion.getStartOffset(fileValueRegion);
							int length = fileValueRegion.getTextLength();
							int lineNo = sDoc.getLineOfOffset(start);
							message.setLineNo(lineNo + 1);
							message.setOffset(start);
							message.setLength(length);

							reporter.addMessage(fMessageOriginator, message);
						}
					}
				}
			}
		}
		else if (fileValueRegion == null && fSeverityIncludeFileNotSpecified != ValidationMessage.IGNORE) {
			// file is not specified at all
			String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_3, attrName);
			LocalizedMessage message = new LocalizedMessage(fSeverityIncludeFileNotSpecified, msgText, file);
			int start = documentRegion.getStartOffset();
			int length = documentRegion.getTextLength();
			int lineNo = sDoc.getLineOfOffset(start);
			message.setLineNo(lineNo + 1);
			message.setOffset(start);
			message.setLength(length);

			reporter.addMessage(fMessageOriginator, message);
		}
	}

	/**
	 * @param reporter
	 * @param file
	 * @param doc
	 * @param documentRegion
	 */
	private void processPageDirective(IReporter reporter, IFile file, IStructuredDocument doc, IStructuredDocumentRegion documentRegion) {
		ITextRegion superclassValueRegion = getAttributeValueRegion(documentRegion, JSP11Namespace.ATTR_NAME_EXTENDS);
		if (superclassValueRegion != null) {
			// file specified
			String superclassName = documentRegion.getText(superclassValueRegion);
			superclassName = StringUtils.stripQuotes(superclassName);

			IType superClass = null;
			if (superclassName != null && superclassName.length() > 0 && fProject != null && fProject.exists()) {
				try {
					superClass = fProject.findType(superclassName.trim(), new NullProgressMonitor());
				}
				catch (JavaModelException e) {
					Logger.logException(e);
				}
			}

			if (superClass == null && fSeveritySuperClassNotFound != ValidationMessage.IGNORE) {
				// superclass not found
				String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_8, superclassName);
				LocalizedMessage message = new LocalizedMessage(fSeveritySuperClassNotFound, msgText, file);
				int start = documentRegion.getStartOffset(superclassValueRegion);
				int length = superclassValueRegion.getTextLength();
				int lineNo = doc.getLineOfOffset(start);
				message.setLineNo(lineNo + 1);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(fMessageOriginator, message);
			}
		}
	}

	private void processTaglibDirective(IReporter reporter, IFile file, IStructuredDocument sDoc, IStructuredDocumentRegion documentRegion) {
		ITextRegion prefixValueRegion = null;
		ITextRegion uriValueRegion = getAttributeValueRegion(documentRegion, JSP11Namespace.ATTR_NAME_URI);
		ITextRegion tagdirValueRegion = getAttributeValueRegion(documentRegion, JSP20Namespace.ATTR_NAME_TAGDIR);
		if (uriValueRegion != null) {
			// URI is specified
			String uri = documentRegion.getText(uriValueRegion);

			if (file != null) {
				uri = StringUtils.stripQuotes(uri);
				if (uri.length() > 0) {
					ITaglibRecord reference = TaglibIndex.resolve(file.getFullPath().toString(), uri, false);
					if (reference != null) {
						switch (reference.getRecordType()) {
							case (ITaglibRecord.TLD) : {
								ITLDRecord record = (ITLDRecord) reference;
								IResource tldfile = ResourcesPlugin.getWorkspace().getRoot().getFile(record.getPath());
								addDependsOn(tldfile);
							}
								break;
							case (ITaglibRecord.JAR) : {
								IJarRecord record = (IJarRecord) reference;
								IFile[] foundFilesForLocation = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(record.getLocation());
								for (int i = 0; i < foundFilesForLocation.length; i++) {
									addDependsOn(foundFilesForLocation[i]);
								}
							}
								break;
							case (ITaglibRecord.TAGDIR) : {
								ITagDirRecord record = (ITagDirRecord) reference;
								IPath path = record.getPath();
								IResource found = ResourcesPlugin.getWorkspace().getRoot().findMember(path, false);

								try {
									found.accept(new IResourceVisitor() {
										public boolean visit(IResource resource) throws CoreException {
											if (resource.getType() == IResource.FILE) {
												addDependsOn(resource);
											}
											return true;
										}
									});
								}
								catch (CoreException e) {
									Logger.logException(e);
								}
							}
								break;
							case (ITaglibRecord.URL) : {
								IURLRecord record = (IURLRecord) reference;
								String baseLocation = record.getBaseLocation();
								if (baseLocation != null && baseLocation.indexOf("://") < 0) {
									IResource found = ResourcesPlugin.getWorkspace().getRoot().findMember(baseLocation, false);
									if (found != null) {
										try {
											found.accept(new IResourceVisitor() {
												public boolean visit(IResource resource) throws CoreException {
													if (resource.getType() == IResource.FILE) {
														addDependsOn(resource);
													}
													return true;
												}
											});
										}
										catch (CoreException e) {
											Logger.logException(e);
										}
									}
									else {
										IFile externalJar = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(baseLocation));
										if (externalJar != null) {
											addDependsOn(externalJar);
										}
									}
								}
							}
								break;
						}
					}
					if (reference == null && fSeverityTaglibUnresolvableURI != ValidationMessage.IGNORE) {
						// URI specified but does not resolve
						String msgText = null;
						// provide better messages for typical "http:*" URIs
						final float version = DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(file.getFullPath());
						if (uri.startsWith("http:") && version < 1.2) { //$NON-NLS-1$
							if (FacetModuleCoreSupport.isDynamicWebProject(file.getProject())) {
								msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_9, new Object[] { uri, new Float(version)} );
							}
							else {
								msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_10, uri);
							}
						}
						else {
							msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_1, uri);
						}
						LocalizedMessage message = new LocalizedMessage(fSeverityTaglibUnresolvableURI, msgText, file);
						int start = documentRegion.getStartOffset(uriValueRegion);
						int length = uriValueRegion.getTextLength();
						int lineNo = sDoc.getLineOfOffset(start);
						message.setLineNo(lineNo + 1);
						message.setOffset(start);
						message.setLength(length);

						message.setAttribute("PROBLEM_ID", new Integer(611)); //$NON-NLS-1$

						reporter.addMessage(fMessageOriginator, message);
					}
				}
				else if (fSeverityTaglibMissingURI != ValidationMessage.IGNORE) {
					// URI specified but empty string
					String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_3, JSP11Namespace.ATTR_NAME_URI);
					LocalizedMessage message = new LocalizedMessage(fSeverityTaglibMissingURI, msgText, file);
					int start = documentRegion.getStartOffset(uriValueRegion);
					int length = uriValueRegion.getTextLength();
					int lineNo = sDoc.getLineOfOffset(start);
					message.setLineNo(lineNo + 1);
					message.setOffset(start);
					message.setLength(length);

					reporter.addMessage(fMessageOriginator, message);
				}
			}
		}
		else if (tagdirValueRegion != null) {
			// URI is specified
			String tagdir = documentRegion.getText(tagdirValueRegion);

			if (file != null) {
				tagdir = StringUtils.stripQuotes(tagdir);
				if (tagdir.length() <= 0 && fSeverityTaglibMissingURI != ValidationMessage.IGNORE) {
					// tagdir specified but empty string
					String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_3, JSP20Namespace.ATTR_NAME_TAGDIR);
					LocalizedMessage message = new LocalizedMessage(fSeverityTaglibMissingURI, msgText, file);
					int start = documentRegion.getStartOffset(tagdirValueRegion);
					int length = tagdirValueRegion.getTextLength();
					int lineNo = sDoc.getLineOfOffset(start);
					message.setLineNo(lineNo + 1);
					message.setOffset(start);
					message.setLength(length);

					reporter.addMessage(fMessageOriginator, message);
				}
				else if (TaglibIndex.resolve(file.getFullPath().toString(), tagdir, false) == null && fSeverityTagdirUnresolvableURI != ValidationMessage.IGNORE) {
					// URI specified but does not resolve
					String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_11, tagdir);
					LocalizedMessage message = new LocalizedMessage(fSeverityTaglibUnresolvableURI, msgText, file);
					int start = documentRegion.getStartOffset(tagdirValueRegion);
					int length = tagdirValueRegion.getTextLength();
					int lineNo = sDoc.getLineOfOffset(start);
					message.setLineNo(lineNo + 1);
					message.setOffset(start);
					message.setLength(length);

					reporter.addMessage(fMessageOriginator, message);
				}
			}
		}
		else if (fSeverityTaglibMissingURI != ValidationMessage.IGNORE) {
			// URI not specified or empty string
			String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_7, new String[]{JSP20Namespace.ATTR_NAME_TAGDIR, JSP11Namespace.ATTR_NAME_URI});
			LocalizedMessage message = new LocalizedMessage(fSeverityTaglibMissingURI, msgText, file);
			int start = documentRegion.getStartOffset();
			int length = documentRegion.getTextLength();
			int lineNo = sDoc.getLineOfOffset(start);
			message.setLineNo(lineNo + 1);
			message.setOffset(start);
			message.setLength(length);

			reporter.addMessage(fMessageOriginator, message);
		}

		prefixValueRegion = getAttributeValueRegion(documentRegion, JSP11Namespace.ATTR_NAME_PREFIX);
		if (prefixValueRegion != null) {
			// prefix specified
			String taglibPrefix = documentRegion.getText(prefixValueRegion);
			taglibPrefix = StringUtils.stripQuotes(taglibPrefix);

			collectTaglibPrefix(documentRegion, prefixValueRegion, taglibPrefix);

			if (isReservedTaglibPrefix(taglibPrefix)) {
				// prefix is a reserved prefix
				String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_0, taglibPrefix);
				int sev = IMessage.HIGH_SEVERITY;
				LocalizedMessage message = (file == null ? new LocalizedMessage(sev, msgText) : new LocalizedMessage(sev, msgText, file));
				int start = documentRegion.getStartOffset(prefixValueRegion);
				int length = prefixValueRegion.getTextLength();
				int lineNo = sDoc.getLineOfOffset(start);
				message.setLineNo(lineNo + 1);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(fMessageOriginator, message);
			}
			if (taglibPrefix.length() == 0 && fSeverityTaglibMissingPrefix != ValidationMessage.IGNORE) {
				// prefix is specified but empty
				String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_3, JSP11Namespace.ATTR_NAME_PREFIX);
				LocalizedMessage message = new LocalizedMessage(fSeverityTaglibMissingPrefix, msgText, file);
				int start = documentRegion.getStartOffset(prefixValueRegion);
				int length = prefixValueRegion.getTextLength();
				int lineNo = sDoc.getLineOfOffset(start);
				message.setLineNo(lineNo + 1);
				message.setOffset(start);
				message.setLength(length);

				reporter.addMessage(fMessageOriginator, message);
			}
		}
		else if (fSeverityTaglibMissingPrefix != ValidationMessage.IGNORE) {
			// prefix is not specified
			String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_3, JSP11Namespace.ATTR_NAME_PREFIX);
			LocalizedMessage message = new LocalizedMessage(fSeverityTaglibMissingPrefix, msgText, file);
			int start = documentRegion.getStartOffset();
			int length = documentRegion.getTextLength();
			int lineNo = sDoc.getLineOfOffset(start);
			message.setLineNo(lineNo + 1);
			message.setOffset(start);
			message.setLength(length);

			reporter.addMessage(fMessageOriginator, message);
		}
	}

	private void reportTaglibDuplicatePrefixes(IFile file, IReporter reporter, IStructuredDocument document) {
		if (fSeverityTaglibDuplicatePrefixWithDifferentURIs == ValidationMessage.IGNORE && fSeverityTaglibDuplicatePrefixWithSameURIs == ValidationMessage.IGNORE)
			return;

		String[] prefixes = (String[]) fTaglibPrefixesInUse.keySet().toArray(new String[0]);
		for (int prefixNumber = 0; prefixNumber < prefixes.length; prefixNumber++) {
			int severity = fSeverityTaglibDuplicatePrefixWithSameURIs;

			Object o = fTaglibPrefixesInUse.get(prefixes[prefixNumber]);
			/*
			 * Only care if it's a List (because there was more than one
			 * directive with that prefix) and if we're supposed to report
			 * duplicates
			 */
			if (o instanceof List) {
				List valueRegions = (List) o;
				String uri = null;
				for (int regionNumber = 0; regionNumber < valueRegions.size(); regionNumber++) {
					IStructuredDocumentRegion documentRegion = (IStructuredDocumentRegion) fPrefixValueRegionToDocumentRegionMap.get(valueRegions.get(regionNumber));
					ITextRegion uriValueRegion = getAttributeValueRegion(documentRegion, JSP11Namespace.ATTR_NAME_URI);
					if (uriValueRegion == null) {
						uriValueRegion = getAttributeValueRegion(documentRegion, JSP20Namespace.ATTR_NAME_TAGDIR);
					}
					if (uriValueRegion != null) {
						String uri2 = StringUtils.stripQuotes(documentRegion.getText(uriValueRegion));
						if (uri == null) {
							uri = uri2;
						}
						else {
							if (collator.compare(uri, uri2) != 0) {
								severity = fSeverityTaglibDuplicatePrefixWithDifferentURIs;
							}
						}
					}
				}

				if (severity != ValidationMessage.IGNORE) {
					String msgText = NLS.bind(JSPCoreMessages.JSPDirectiveValidator_2, prefixes[prefixNumber]); //$NON-NLS-2$ //$NON-NLS-1$

					// Report an error in all directives using this prefix
					for (int regionNumber = 0; regionNumber < valueRegions.size(); regionNumber++) {

						ITextRegion valueRegion = (ITextRegion) valueRegions.get(regionNumber);
						IStructuredDocumentRegion documentRegion = (IStructuredDocumentRegion) fPrefixValueRegionToDocumentRegionMap.get(valueRegion);
						LocalizedMessage message = (file == null ? new LocalizedMessage(severity, msgText) : new LocalizedMessage(severity, msgText, file));

						// if there's a message, there was an error found
						int start = documentRegion.getStartOffset(valueRegion);
						int length = valueRegion.getTextLength();
						int lineNo = document.getLineOfOffset(start);
						message.setLineNo(lineNo + 1);
						message.setOffset(start);
						message.setLength(length);

						reporter.addMessage(fMessageOriginator, message);
					}
				}
			}
		}
	}

	/**
	 * @param project
	 */
	private void setProject(IProject project) {
		boolean useProject = false;
		if (project != null) {
			fProject = JavaCore.create(project);
			fPreferences = new ProjectScope(fProject.getProject()).getNode(PREFERENCE_NODE_QUALIFIER);
			useProject = fPreferences.getBoolean(HTMLCorePreferenceNames.USE_PROJECT_SETTINGS, false);
		}
		else {
			fProject = null;
		}

		if (!useProject) {
			fPreferences = new InstanceScope().getNode(PREFERENCE_NODE_QUALIFIER);
		}
	}

	private void unloadPreferences() {
		fPreferencesService = null;
		fScopes = null;
	}
	
	/**
	 * True if container has nested regions, meaning container is probably too
	 * complicated (like JSP expressions or EL) to validate with this validator.
	 */
	private boolean hasNestedRegion(ITextRegion container) {
		return (container instanceof ITextRegionContainer && ((ITextRegionContainer) container).getRegions() != null);
	}

	/**
	 * batch validation call
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		if (DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " validating: " + f); //$NON-NLS-1$
		}

		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getModelForRead(f);
			if (sModel != null && !reporter.isCancelled()) {
				performValidation(f, reporter, sModel.getStructuredDocument());
			}
		}
		catch (Exception e) {
			Logger.logException(e);
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}
}
