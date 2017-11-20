/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat, Inc. - external validator extension
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.Logger;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeConstants;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.validate.extension.IHTMLCustomAttributeValidator;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class HTMLAttributeValidator extends PrimeValidator {

	private static final String JAVASCRIPT_PREFIX = "javascript:"; //$NON-NLS-1$
	public static final int REGION_NAME = 1;
	public static final int REGION_VALUE = 2;
	// <<D210422
	private static final char SINGLE_QUOTE = '\'';
	private static final char DOUBLE_QUOTE = '\"';

	private IPreferencesService fPreferenceService;
	private static Map fIgnorePatterns = new HashMap(); // A storage for ignore patterns (instances of StringMatcher)

	// HTML(5) data attributes
	private static final String ATTR_NAME_DATA = "data-"; //$NON-NLS-1$
	private static final int ATTR_NAME_DATA_LENGTH = ATTR_NAME_DATA.length();
	
	//WHATWG x-vendor-feature attributes
	private static final String ATTR_NAME_USER_AGENT_FEATURE = "x-"; //$NON-NLS-1$
	private static final int ATTR_NAME_USER_AGENT_FEATURE_LENGTH = ATTR_NAME_USER_AGENT_FEATURE.length();
	
	// D210422

	// Accessible Rich Internet Applications (WAI-ARIA)
	private static final String ATTR_NAME_WAI_ARIA = "aria-"; //$NON-NLS-1$
	private static final int ATTR_NAME_WAI_ARIA_LENGTH = ATTR_NAME_WAI_ARIA.length();

	private List<IHTMLCustomAttributeValidator> externalValidators;

	/**
	 * HTMLAttributeValidator constructor comment.
	 */
	public HTMLAttributeValidator() {
		super();
		fPreferenceService = Platform.getPreferencesService();
	}

	/**
	 */
	public static Segment getErrorSegment(IDOMNode errorNode, int regionType) {
		ITextRegion rgn = null;
		switch (regionType) {
			case REGION_NAME :
				rgn = errorNode.getNameRegion();
				break;
			case REGION_VALUE :
				rgn = errorNode.getValueRegion();
				break;
			default :
				// nothing to do.
				break;
		}
		if (rgn != null) {
			if (errorNode instanceof IDOMAttr) {
				IDOMElement ownerElement = (IDOMElement) ((IDOMAttr) errorNode).getOwnerElement();
				if (ownerElement != null) {
					//if editor closed during validation this could be null
					IStructuredDocumentRegion firstRegion = ownerElement.getFirstStructuredDocumentRegion();
					if(firstRegion != null) {
						int regionStartOffset = firstRegion.getStartOffset(rgn);
						int regionLength = rgn.getTextLength();
						return new Segment(regionStartOffset, regionLength);
					}
				}
			}
		}
		return new Segment(errorNode.getStartOffset(), 1);
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLAttributeValidator.class) || super.isAdapterForType(type));
	}

	/**
	 */
	public void validate(IndexedRegion node) {
		Element target = (Element) node;
		if (CMUtil.isForeign(target))
			return;
		CMElementDeclaration edec = CMUtil.getDeclaration(target);
		if (edec == null) {
			NamedNodeMap attrs = target.getAttributes();			
			// unknown tag - go to validators from extension point
			for (int i = 0; i < attrs.getLength(); i++) {
				Attr a = (Attr) attrs.item(i);
				final String attrName = a.getName().toLowerCase(Locale.US);
				// Check for user-defined exclusions
				if (shouldValidateAttributeName(target, attrName)) {
					validateWithExtension(target, a, attrName);
				}
			}
		} else {
			CMNamedNodeMap declarations = edec.getAttributes();
	
			List modelQueryNodes = null;
			NamedNodeMap attrs = target.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				int rgnType = REGION_NAME;
				int state = ErrorState.NONE_ERROR;
				Attr a = (Attr) attrs.item(i);
				// D203637; If the target attr has prefix, the validator should
				// not
				// warn about it. That is, just ignore. It is able to check
				// whether
				// an attr has prefix or not by calling XMLAttr#isGlobalAttr().
				// When a attr has prefix (not global), it returns false.
				boolean isXMLAttr = a instanceof IDOMAttr;
				if (isXMLAttr) {
					IDOMAttr xmlattr = (IDOMAttr) a;
					if (!xmlattr.isGlobalAttr() || xmlattr.getNameRegion() instanceof ITextRegionContainer)
						continue; // skip futher validation and begin next loop.
				}
	
				CMAttributeDeclaration adec = (CMAttributeDeclaration) declarations.getNamedItem(a.getName());
				final String attrName = a.getName().toLowerCase(Locale.US);
				/* Check the modelquery if nothing is declared by the element declaration */
				if (adec == null) {
					if (modelQueryNodes == null)
						modelQueryNodes = ModelQueryUtil.getModelQuery(target.getOwnerDocument()).getAvailableContent((Element) node, edec, ModelQuery.INCLUDE_ATTRIBUTES);
					
					
					for (int k = 0; k < modelQueryNodes.size(); k++) {
						CMNode cmnode = (CMNode) modelQueryNodes.get(k);
						if (cmnode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION && cmnode.getNodeName().toLowerCase(Locale.US).equals(attrName)) {
							adec = (CMAttributeDeclaration) cmnode;
							break;
						}
					}
				}
				
				if (adec == null) {
					if ((attrName.startsWith(ATTR_NAME_DATA) && attrName.length() > ATTR_NAME_DATA_LENGTH) || 
							(attrName.startsWith(ATTR_NAME_USER_AGENT_FEATURE) && attrName.length() > ATTR_NAME_USER_AGENT_FEATURE_LENGTH) ||
							(attrName.startsWith(ATTR_NAME_WAI_ARIA) && attrName.length() > ATTR_NAME_WAI_ARIA_LENGTH)) {
						if (isHTML5(target))
							continue;
					}		
					// Check for user-defined exclusions
					if (!shouldValidateAttributeName(target, attrName)) 
						continue;
	
					// No attr declaration was found. That is, the attr name is
					// undefined.
					// but not regard it as undefined name if it includes nested
					// region
					
					// Then look into extension point for external validator
					validateWithExtension(target, a, attrName);
				} else {
					// The attr declaration was found.
					// At 1st, the name should be checked.
					if (CMUtil.isObsolete(adec)){
						state = ErrorState.OBSOLETE_ATTR_NAME_ERROR;
					}
					if (CMUtil.isHTML(edec) && (!CMUtil.isXHTML(edec))) {
						// If the target element is pure HTML (not XHTML), some
						// attributes
						// might be written in boolean format. It should be check
						// specifically.
						if (CMUtil.isBooleanAttr(adec) && ((IDOMAttr) a).hasNameOnly())
							continue; // OK, keep going. No more check is needed
						// against this attr.
					} else {
						// If the target is other than pure HTML (JSP or XHTML),
						// the name
						// must be checked exactly (ie in case sensitive way).
						String actual = a.getName();
						String desired = adec.getAttrName();
						if (!actual.equals(desired)) { // case mismatch
							rgnType = REGION_NAME;
							state = ErrorState.MISMATCHED_ERROR;
						}
					}
					// Then, the value must be checked.
					if (state == ErrorState.NONE_ERROR) { // Need more check.
						// Now, the value should be checked, if the type is ENUM.
						CMDataType attrType = adec.getAttrType();
						if (a instanceof IDOMAttr) {
							final ITextRegion region = ((IDOMAttr) a).getEqualRegion();
							if (region == null) {
								rgnType = REGION_NAME;
								state = ErrorState.MISSING_ATTR_VALUE_EQUALS_ERROR;
							}
						}
						String actualValue = a.getValue();
						if (attrType.getImpliedValueKind() == CMDataType.IMPLIED_VALUE_FIXED) {
							// Check FIXED value.
							String validValue = attrType.getImpliedValue();
							if (!actualValue.equals(validValue)) {
								rgnType = REGION_VALUE;
								state = ErrorState.UNDEFINED_VALUE_ERROR;
							}
						}
						else if (CMDataType.URI.equals(attrType.getDataTypeName())) {
							if (actualValue.indexOf('#') < 0 && actualValue.indexOf(":/") < 0 && !actualValue.toLowerCase(Locale.ENGLISH).startsWith(JAVASCRIPT_PREFIX) && CMUtil.isHTML(edec)) { //$NON-NLS-1$ //$NON-NLS-2$
								IStructuredDocumentRegion start = ((IDOMNode) node).getStartStructuredDocumentRegion();
								// roundabout start tag check
								if (start != null && start.getFirstRegion().getTextLength() == 1) {
									// only check when we have a way to set dependencies
									Collection dependencies = (Collection) ((IDOMNode) ((IDOMNode) node).getOwnerDocument()).getUserData(HTMLValidationAdapterFactory.DEPENDENCIES);
									if (dependencies != null) {
										IPath basePath = new Path(((IDOMNode) node).getModel().getBaseLocation());
										if (basePath.segmentCount() > 1) {
											IPath path = ModuleCoreSupport.resolve(basePath, actualValue);
											IResource found = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
											if (found != null) {
												dependencies.add(found);
											}
										}
									}
								}
							}
						}
						else if (CMDataType.ENUM.equals(attrType.getDataTypeName())) {
							/*
							 * Check current value is valid among a known list.
							 * There may be enumerated values provided even when
							 * the datatype is not ENUM, but we'll only validate
							 * against that list if the type matches.
							 */
							String[] enumeratedValues = attrType.getEnumeratedValues();
							// several candidates are found.
							boolean found = false;
							for (int j = 0; j < enumeratedValues.length; j++) {
								// At 1st, compare ignoring case.
								if (actualValue.equalsIgnoreCase(enumeratedValues[j])) {
									found = true;
									if (CMUtil.isCaseSensitive(edec) && (!actualValue.equals(enumeratedValues[j]))) {
										rgnType = REGION_VALUE;
										state = ErrorState.MISMATCHED_VALUE_ERROR;
									}
									break; // exit the loop.
								}
							}
							if (!found) {
								// retrieve and check extended values (retrieval can call extensions, which may take longer)
								String[] modelQueryExtensionValues = ModelQueryUtil.getModelQuery(target.getOwnerDocument()).getPossibleDataTypeValues((Element) node, adec);
								// copied loop from above
								for (int j = 0; j < modelQueryExtensionValues.length; j++) {
									// At 1st, compare ignoring case.
									if (actualValue.equalsIgnoreCase(modelQueryExtensionValues[j])) {
										found = true;
										if (CMUtil.isCaseSensitive(edec) && (!actualValue.equals(modelQueryExtensionValues[j]))) {
											rgnType = REGION_VALUE;
											state = ErrorState.MISMATCHED_VALUE_ERROR;
										}
										break; // exit the loop.
									}
								}
								// No candidate was found. That is,
								// actualValue is invalid.
								// but not regard it as undefined value if it
								// includes nested region.
								if (!hasNestedRegion(((IDOMNode) a).getValueRegion())) {
									rgnType = REGION_VALUE;
									state = ErrorState.UNDEFINED_VALUE_ERROR;
								}
							}
						}
					}
					// <<D210422
					if (state == ErrorState.NONE_ERROR) { // Need more check.
						if (isXMLAttr) {
							String source = ((IDOMAttr) a).getValueRegionText();
							if (source != null) {
								char firstChar = source.charAt(0);
								char lastChar = source.charAt(source.length() - 1);
								boolean unclosedAttr = false;
								if (isQuote(firstChar) || isQuote(lastChar)) {
									if (lastChar != firstChar) {
										unclosedAttr = true;
									}
								}
								else{
									if (CMUtil.isXHTML(edec)){
										unclosedAttr = true;
									}
								}
								if (unclosedAttr){
									rgnType = REGION_VALUE;
									state = ErrorState.UNCLOSED_ATTR_VALUE;
								}
							}
						}
					}
					// D210422
				}
				if (state != ErrorState.NONE_ERROR) {
					Segment seg = getErrorSegment((IDOMNode) a, rgnType);
					if (seg != null)
						reporter.report(new ErrorInfoImpl(state, seg, a));
				}
			}
		}
	}

	private void initValidators(IStructuredDocument doc) {
		externalValidators = new ArrayList<IHTMLCustomAttributeValidator>();
		for (IConfigurationElement e : CustomHTMLAttributeValidatorExtensionLoader.getInstance().getValidators()) {
			IHTMLCustomAttributeValidator validator;
			try {
				validator = (IHTMLCustomAttributeValidator) e.createExecutableExtension("class");
				validator.init(doc);
				externalValidators.add(validator);			
			} catch (CoreException e1) {
				Logger.logException(e1);
			}
		}
	}
	
	private void validateWithExtension(Element target, Attr a, String attrName) {
		boolean validated = false;
		if (externalValidators == null) {
			initValidators(((IDOMElement)target).getStructuredDocument());
		}
		
		for (IHTMLCustomAttributeValidator v : externalValidators) {
			try { 
				if (v.canValidate((IDOMElement) target, attrName)) {
					validated = true;
					ValidationMessage result = v.validateAttribute((IDOMElement) target, attrName);
					if (result != null) {
						// report only one validation result or nothing if all reports are null
						reporter.report(result);
						break;
					}
				}
			} catch (Throwable t) {
				Logger.logException(t);
			}
		}
		if (!validated) {
			if (!hasNestedRegion(((IDOMNode) a).getNameRegion())) {
				Segment seg = getErrorSegment((IDOMNode) a, REGION_NAME);
				if (seg != null)
					reporter.report(new ErrorInfoImpl(ErrorState.UNDEFINED_NAME_ERROR, seg, a));
			}
		}
	}
	
	/**
	 * True if container has nested regions, meaning container is probably too
	 * complicated (like JSP regions) to validate with this validator.
	 */
	private boolean hasNestedRegion(ITextRegion container) {
		if (!(container instanceof ITextRegionContainer))
			return false;
		ITextRegionList regions = ((ITextRegionContainer) container).getRegions();
		if (regions == null)
			return false;
		// BUG207194: return true by default as long as container is an
		// ITextRegionContainer with at least 1 region
		return true;
	}

	// <<D214022
	private boolean isQuote(char c) {
		return (c == SINGLE_QUOTE) || (c == DOUBLE_QUOTE);
	}
	// D210422
	
	private boolean isHTML5(Element target) {
		DocumentTypeAdapter documentTypeAdapter = (DocumentTypeAdapter) ((INodeNotifier) target.getOwnerDocument()).getAdapterFor(DocumentTypeAdapter.class);
		return (documentTypeAdapter != null && 
				documentTypeAdapter.hasFeature(HTMLDocumentTypeConstants.HTML5));
	}
	
	private boolean shouldValidateAttributeName(Element target, String attrName) {
		Object adapter = (target instanceof IAdaptable ? ((IAdaptable)target).getAdapter(IResource.class) : null);
		IProject project = (adapter instanceof IResource ? ((IResource)adapter).getProject() : null);
		
		Iterator excludedAttributes = getExcludedAttributeNames(project).iterator();
		while (excludedAttributes.hasNext()) {
			String excluded = (String)excludedAttributes.next();
			StringMatcher strMatcher = (StringMatcher)fIgnorePatterns.get(excluded);
			if (strMatcher == null) {
				strMatcher = new StringMatcher(excluded);
				fIgnorePatterns.put(excluded, strMatcher);
			}
			if (strMatcher.match(attrName.toLowerCase()))
				return false;
		}

		return true;
	}
	
	private Set getExcludedAttributeNames(IProject project) {
		IScopeContext[] fLookupOrder = new IScopeContext[] {new InstanceScope(), new DefaultScope()};
		if (project != null) {
			ProjectScope projectScope = new ProjectScope(project);
			if(projectScope.getNode(HTMLCorePlugin.getDefault().getBundle().getSymbolicName()).getBoolean(HTMLCorePreferenceNames.USE_PROJECT_SETTINGS, false))
				fLookupOrder = new IScopeContext[] {projectScope, new InstanceScope(), new DefaultScope()};
		}
		
		Set result = new HashSet();
		if (fPreferenceService.getBoolean(HTMLCorePlugin.getDefault().getBundle().getSymbolicName(), 
				HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES, HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES_DEFAULT, 
				fLookupOrder)) {
			String ignoreList = fPreferenceService.getString(HTMLCorePlugin.getDefault().getBundle().getSymbolicName(), 
					HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE, HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE_DEFAULT, 
					fLookupOrder);
			
			if (ignoreList.trim().length() == 0)
				return result;
	
			String[] names = ignoreList.split(","); //$NON-NLS-1$
			for (int i = 0; names != null && i < names.length; i++) {
				String name = names[i] == null ? null : names[i].trim();
				if (name != null && name.length() > 0) 
					result.add(name.toLowerCase());
			}
		}
		return result; 
	}
}
