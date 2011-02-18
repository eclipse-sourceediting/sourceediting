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
package org.eclipse.jst.jsp.core.internal.document;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.modelhandler.EmbeddedTypeStateData;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.IContentDescriptionForJSP;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeFamilyForHTML;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.EmbeddedTypeRegistry;
import org.eclipse.wst.sse.core.internal.modelhandler.EmbeddedTypeRegistryImpl;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import com.ibm.icu.util.StringTokenizer;

/**
 * This class has the responsibility to provide an embedded factory registry
 * for JSP Aware INodeAdapter Factories to use.
 * 
 * Typically, the embedded type is to be considered a feature of the document,
 * so JSP Aware AdpaterFactories should call
 * getAdapter(PageDirectiveAdapter.class) directoy on the document (or owning
 * document) node.
 */
public class PageDirectiveAdapterImpl implements PageDirectiveAdapter {

	protected static final String STR_CHARSET = "charset"; //$NON-NLS-1$
	private final static Object adapterType = PageDirectiveAdapter.class;
	private IStructuredModel model;
	protected final String[] JAVASCRIPT_LANGUAGE_KEYS = new String[]{"javascript", "javascript1.0", "javascript1.1_3", "javascript1.2", "javascript1.3", "javascript1.4", "javascript1.5", "javascript1.6", "jscript", "sashscript"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
	protected final String[] JAVA_LANGUAGE_KEYS = new String[]{"java"}; //$NON-NLS-1$

	/**
	 * Constructor for PageDirectiveAdapterImpl.
	 */
	public PageDirectiveAdapterImpl(INodeNotifier target) {
		super();
		notifierAtCreation = target;
		// we need to remember our instance of model,
		// in case we need to "signal" a re-init needed.
		if (target instanceof IDOMNode) {
			IDOMNode node = (IDOMNode) target;
			model = node.getModel();
		}

	}

	/**
	 * parses the full contentType value into its two parts the contentType,
	 * and the charset, if present. Note: this method is a lightly modified
	 * version of a method in AbstractHeadParser. There, we're mostly
	 * interested in the charset part of contentTypeValue. Here, we're mostly
	 * interested in the mimeType part.
	 */
	private String getMimeTypeFromContentTypeValue(String contentTypeValue) {
		if (contentTypeValue == null)
			return null;
		String cleanContentTypeValue = StringUtils.stripNonLetterDigits(contentTypeValue);
		StringTokenizer tokenizer = new StringTokenizer(cleanContentTypeValue, ";= \t\n\r\f"); //$NON-NLS-1$
		int tLen = tokenizer.countTokens();
		// if contains encoding should have three tokens, the mimetype, the
		// word 'charset', and the encoding value
		String[] tokens = new String[tLen];
		int j = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[j] = tokenizer.nextToken();
			j++;
		}
		// 
		// Following is the common form for target expression
		// <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		// But apparrently is also valid without the content type there,
		// just the charset, as follows:
		// <META http-equiv="Content-Type" content="charset=UTF-8">
		// So we'll loop through tokens and key off of 'charset'

		int charsetPos = -1;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equalsIgnoreCase(STR_CHARSET)) {
				charsetPos = i;
				break;
			}
		}
		// String charset = null;
		String contentType = null;
		if (charsetPos > -1) {
			// case where charset was present
			// int charsetValuePos = charsetPos + 1;
			// if (charsetValuePos < tokens.length) {
			// charset = tokens[charsetValuePos];
			// }
			int contentTypeValuePos = charsetPos - 1;
			if (contentTypeValuePos > -1) {
				contentType = tokens[contentTypeValuePos];
			}
		}
		else {
			// charset was not present, so if there's
			// a value, we assume its the contentType value
			if (tokens.length > 0) {
				contentType = tokens[0];
			}
		}
		return contentType;
	}

	private EmbeddedTypeHandler embeddedTypeHandler;
	private List embeddedFactoryRegistry = new ArrayList();
	private String cachedLanguage;
	private String cachedContentType;
	private INodeNotifier notifierAtCreation;
	private String elIgnored = null;

	private int firstLanguagePosition = -1;
	private int firstContentTypePosition = -1;

	/*
	 * @see INodeAdapter#isAdapterForType(Object)
	 */
	public boolean isAdapterForType(Object type) {
		return (type == adapterType);
	}

	/*
	 * @see INodeAdapter#notifyChanged(INodeNotifier, int, Object, Object,
	 *      Object, int)
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	public void setEmbeddedType(EmbeddedTypeHandler handler) {
		// if really the same handler, no need for further processing
		if (embeddedTypeHandler == handler) {
			return;
		}
		// then one exists, and the new one is truely different, so we need to
		// release and remove current factories
		if (embeddedTypeHandler != null) {
			Iterator list = embeddedFactoryRegistry.iterator();
			while (list.hasNext()) {
				INodeAdapterFactory factory = (INodeAdapterFactory) list.next();
				factory.release();
			}

			embeddedFactoryRegistry.clear();
		}

		embeddedTypeHandler = handler;
		// when the handler is set, "transfer" its factories to our own list.
		// note: our own list may also be added to else where, such as on
		// "editor side".
		if (embeddedTypeHandler != null) {
			Iterator iterator = embeddedTypeHandler.getAdapterFactories().iterator();
			while (iterator.hasNext()) {
				INodeAdapterFactory factory = (INodeAdapterFactory) iterator.next();
				embeddedFactoryRegistry.add(factory);
			}
		}
	}

	/**
	 * @see PageDirectiveAdapter#adapt(INodeNotifier, Object)
	 */
	public INodeAdapter adapt(INodeNotifier notifier, Object type) {
		INodeAdapter result = null;
		// if embeddedContentType hasn't been set,
		// then we can not adapt it.
		if (embeddedTypeHandler != null) {
			if (embeddedFactoryRegistry != null) {
				Iterator iterator = embeddedFactoryRegistry.iterator();
				INodeAdapterFactory factory = null;
				while (iterator.hasNext()) {
					factory = (INodeAdapterFactory) iterator.next();
					if (factory.isFactoryForType(type)) {
						result = factory.adapt(notifier);
						break;
					}
				}
			}
		}
		return result;

	}

	/**
	 * @see PageDirectiveAdapter#getEmbeddedType()
	 */
	public EmbeddedTypeHandler getEmbeddedType() {
		if (embeddedTypeHandler == null) {
			embeddedTypeHandler = getDefaultEmbeddedType();
		}
		return embeddedTypeHandler;
	}

	public void addEmbeddedFactory(INodeAdapterFactory factory) {
		// should we check if already exists in list?
		embeddedFactoryRegistry.add(factory);
	}

	// /**
	// * Used by PageDirectiveWatchers to signal that some important attribute
	// has changed, and
	// * any cached values should be re-calcuated
	// */
	// void changed() {
	// // we won't actually check if change is needed, if the model state is
	// already changing.
	// if (!model.isReinitializationNeeded()) {
	// // go through our list of page watcher adapters, and updates the
	// attributes
	// // we're interested in, if and only if they are the earliest occurance
	// in the resource
	// String potentialContentType = null;
	// String potentialLanguage = null;
	// int contentTypePosition = -1;
	// int languagePosition = -1;
	// Iterator iterator = pageDirectiveWatchers.iterator();
	// while (iterator.hasNext()) {
	// PageDirectiveWatcher pdWatcher = (PageDirectiveWatcher)
	// iterator.next();
	// String contentType = pdWatcher.getContentType();
	// String language = pdWatcher.getLanguage();
	// int offset = pdWatcher.getOffset();
	// if (potentialContentType == null || (hasValue(contentType) && (offset <
	// contentTypePosition))) {
	// potentialContentType = contentType;
	// contentTypePosition = offset;
	// }
	// }
	// // now we have the best candiates for cached values, let's see if
	// they've really changed from
	// // what we had. If so, note we go through the setters so side effects
	// can take place there.
	// potentialContentType =
	// getMimeTypeFromContentTypeValue(potentialContentType);
	// if (potentialContentType == null || potentialContentType.length() == 0)
	// {
	// //potentialContentType = getDefaultContentType();
	// } else {
	// setCachedContentType(potentialContentType);
	// }
	//
	// if (potentialLanguage != null && hasValue(potentialLanguage)) {
	// setCachedLanguage(potentialLanguage);
	// }
	// }
	// }
	void changedContentType(int elementOffset, String newValue) {
		// only need to process if this new value is
		// earlier in the file than our current value
		if (firstContentTypePosition == -1 || elementOffset <= firstContentTypePosition) {
			// dw_TODO: update embedded partitioner in JSP document
			// partitioner
			// nsd_TODO: update embedded partitioner in JSP document
			// partitioner

			// no need to change current value, if we're told some
			// earlier value is null or blank (sounds like an error, anyway)
			if (hasValue(newValue)) {
				firstContentTypePosition = elementOffset;
				String potentialContentType = getMimeTypeFromContentTypeValue(newValue);
				// only do the set processing if different
				// from what it already is
				// if (!potentialContentType.equalsIgnoreCase(cachedLanguage))
				// {
				setCachedContentType(potentialContentType);
				// }
			}
		}
	}

	/**
	 * Used by PageDirectiveWatchers to signal that some important attribute
	 * has changed, and any cached values should be re-calcuated
	 */
	void changedLanguage(int elementOffset, String newValue) {
		// only need to process if this new value is
		// earlier in the file than our current value
		// has to be less than or equal to, in case our previous earliest one,
		// is itself changing!
		if (firstLanguagePosition == -1 || elementOffset <= firstLanguagePosition) {

			// no need to change current value, if we're told some
			// earlier value is null or blank (sounds like an error, anyway)
			if (hasValue(newValue)) {
				firstLanguagePosition = elementOffset;
				// only do the set processing if different
				// from what it already is
				if (!newValue.equalsIgnoreCase(cachedLanguage)) {
					setCachedLanguage(newValue);
				}
			}

			// dw_TODO: set language in document partitioner
			// nsd_TODO: set language in document partitioner
		}
	}

	/**
	 * Used by PageDirectiveWatchers to signal that some important attribute
	 * has changed, and any cached values should be re-calcuated
	 */
	void changedPageEncoding(int elementOffset, String newValue) {

		// we don't currently track active value, since
		// just need during read and write (where its
		// calculated. We will need in future, to
		// acurately clone a model and to display
		// "current encoding" to user in status bar.
	}

	/**
	 * Method hasValue.
	 * 
	 * @param contentType
	 * @return boolean
	 */
	private boolean hasValue(String value) {
		if (value != null && value.length() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Returns the cachedContentType.
	 * 
	 * @return String
	 */
	public String getContentType() {
		if (cachedContentType == null) {
			cachedContentType = getDefaultContentType();
		}
		return cachedContentType;
	}

	/**
	 * Method getDefaultContentType.
	 * 
	 * @return String
	 */
	private String getDefaultContentType() {
		String type = null;
		IFile file = getFile(model);
		if (file != null) {
			type = JSPFContentProperties.getProperty(JSPFContentProperties.JSPCONTENTTYPE, file, true);
		}
		// BUG136468
		if (type == null)
			type = "text/html"; //$NON-NLS-1$
		return type;
	}

	/**
	 * Returns the cachedLanguage.
	 * 
	 * @return String
	 */
	public String getLanguage() {
		if (cachedLanguage == null)
			cachedLanguage = getDefaultLanguage();
		return cachedLanguage;
	}

	/**
	 * Method getDefaultLanguage.
	 * 
	 * @return String
	 */
	private String getDefaultLanguage() {
		String language = null;
		IFile file = getFile(model);
		if (file != null) {
			language = JSPFContentProperties.getProperty(JSPFContentProperties.JSPLANGUAGE, file, true);
		}
		// BUG136468
		if (language == null)
			language = "java"; //$NON-NLS-1$
		return language;
	}

	/**
	 * Sets the cachedContentType.
	 * 
	 * @param cachedContentType
	 *            The cachedContentType to set
	 */
	public void setCachedContentType(String newContentType) {
		/*
		 * if the passed in value is the same as existing, there's nothing to
		 * do. if its different, then we need to change the contentHandler as
		 * well and, more to the point, signal a re-initializtation is needed.
		 * 
		 * Note: if the value we're getting set to does not have a handler in
		 * the registry, we'll actually not set it to null or anything, we'll
		 * just continue on with the one we have. This is pretty important to
		 * avoid re-initializing on every key stroke if someone is typing in a
		 * new content type, but haven't yet finished the whole "word".
		 * However, if an contentType is not recognized, the registry returns
		 * the one for XML.
		 */
		
		/* set the actual value first, the rest is "side effect" */
		this.cachedContentType = newContentType;

		/* see if we need to update embedded handler */

		/*
		 * If the document is a type of XHTML, we do not use the page
		 * directive's contentType to determine the embedded type ... its
		 * XHTML! ... and, eventually, the DOCTYPE adapter should determine
		 * if/when it needs to change.
		 */

		/* just safety check, can be removed later, early in release cycle */
		if (model == null) {
			// throw IllegalStateException("model should never be null in
			// PageDirective Adapter");
			Logger.log(Logger.ERROR, "model should never be null in PageDirective Adapter");
			return;
		}

		EmbeddedTypeHandler potentialNewandler = null;
		IContentDescription contentDescription = getContentDescription(model.getStructuredDocument());
		Object prop = contentDescription.getProperty(IContentDescriptionForJSP.CONTENT_FAMILY_ATTRIBUTE);
		if (prop != null) {
			if (ContentTypeFamilyForHTML.HTML_FAMILY.equals(prop)) {
				potentialNewandler = EmbeddedTypeRegistryImpl.getInstance().getTypeFor("text/html");
			}
		}

		if (potentialNewandler == null) {
			/*
			 * getHandler should always return something (never null), based
			 * on the rules in the factory.
			 */
			potentialNewandler = getHandlerFor(this.cachedContentType);
		}
		/*
		 * we do this check for re-init here, instead of in setEmbeddedType,
		 * since setEmbeddedType is called during the normal initializtion
		 * process, when re-init is not needed (since there is no content)
		 */
		if (embeddedTypeHandler == null) {
			setEmbeddedType(potentialNewandler);
		}
		else if (potentialNewandler != null && embeddedTypeHandler != potentialNewandler) {
			/*
			 * changing this embedded handler here may be in the middle of a
			 * notify loop. That's why we set that "it's needed". Then the
			 * model decides when its "safe" to actually do the re-init.
			 * 
			 * be sure to hold oldHandler in temp var or else setEmbeddedType
			 * will "reset" it before modelReinitNeeded(oldHandler, handler)
			 * is called
			 * 
			 */
			EmbeddedTypeHandler oldHandler = embeddedTypeHandler;
			setEmbeddedType(potentialNewandler);
			modelReinitNeeded(oldHandler, potentialNewandler);
		}

	}

	/**
	 * This method is used to re-init based on embeddedTypeHandler changing.
	 * It is given priority over the language change, since there its more
	 * important to have old and new handlers's in the stateData field.
	 */
	private void modelReinitNeeded(EmbeddedTypeHandler oldHandler, EmbeddedTypeHandler newHandler) {
		if (model.isReinitializationNeeded()) {
			System.out.println("already being initialized"); //$NON-NLS-1$
		}

		try {
			model.aboutToChangeModel();
			model.setReinitializeStateData(new EmbeddedTypeStateData(oldHandler, newHandler));
			model.setReinitializeNeeded(true);
		}
		finally {
			model.changedModel();
		}
	}

	/**
	 * Method modelReinitNeeded.
	 */
	private void modelReinitNeeded(String oldlanguage, String newLanguage) {
		// bit of a short cut for now .... we dont' need language at the
		// moment,
		// but should set the state data
		if (model.isReinitializationNeeded()) {
			if (Debug.displayWarnings) {
				System.out.println("already being initialized"); //$NON-NLS-1$
			}
		}
		else {
			try {
				// if already being re-initialized, we don't want to
				// reset the data in the stateData field.
				model.aboutToChangeModel();
				model.setReinitializeStateData(newLanguage);
				model.setReinitializeNeeded(true);
			}
			finally {
				model.changedModel();
			}
		}
	}

	public void setCachedLanguage(String newLanguage) {
		if (cachedLanguage != null && languageStateChanged(cachedLanguage, newLanguage)) {
			/*
			 * a complete re-init overkill in current system, since really
			 * just need for the line style providers, BUT, a change in
			 * language could effect other things, and we don't expect to
			 * happen often so a little overkill isn't too bad. The deep
			 * problem is that there is no way to get at the "edit side"
			 * adpapters specifically here in model class. we have to do the
			 * model changed sequence to get the screen to update. do not
			 * signal again, if signaled once (the reinit state data will be
			 * wrong. (this needs to be improved in future)
			 */
			if (!model.isReinitializationNeeded()) {
				modelReinitNeeded(cachedLanguage, newLanguage);
			}
		}
		if (languageKnown(newLanguage))
			setLanguage(newLanguage);
	}

	/**
	 * This is public access method, used especially from loader, for JSP
	 * Fragment support.
	 */
	public void setLanguage(String newLanguage) {
		this.cachedLanguage = newLanguage;
		IDocumentPartitioner partitioner = ((IDocumentExtension3) model.getStructuredDocument()).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
		if (partitioner instanceof StructuredTextPartitionerForJSP) {
			((StructuredTextPartitionerForJSP) partitioner).setLanguage(newLanguage);
		}
	}

	/**
	 * Method languageStateChange.
	 * 
	 * @param cachedLanguage
	 * @param newLanguage
	 * @return boolean
	 */
	private boolean languageStateChanged(String cachedLanguage, String newLanguage) {
		boolean result = false; // languages are equal, then no change in
		// state
		if (!cachedLanguage.equalsIgnoreCase(newLanguage)) {
			result = languageKnown(newLanguage);
		}
		return result;
	}

	/**
	 * Method languageKnown.
	 * 
	 * @param cachedLanguage
	 * @return boolean
	 */
	private boolean languageKnown(String language) {
		return (StringUtils.contains(JAVA_LANGUAGE_KEYS, language, false) || StringUtils.contains(JAVASCRIPT_LANGUAGE_KEYS, language, false));
	}

	private IFile getFile(IStructuredModel model) {
		String location = model.getBaseLocation();
		if (location != null) {
			IPath path = new Path(location);
			if (path.segmentCount() > 1) {
				return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		}
		return null;
	}

	private EmbeddedTypeHandler getHandlerFor(String contentType) {
		EmbeddedTypeRegistry reg = getEmbeddedContentTypeRegistry();
		EmbeddedTypeHandler handler = null;
		if (reg != null)
			handler = reg.getTypeFor(contentType);
		return handler;
	}

	/**
	 * Gets the embeddedContentTypeRegistry.
	 * 
	 * @return Returns a EmbeddedContentTypeRegistry
	 */
	private EmbeddedTypeRegistry getEmbeddedContentTypeRegistry() {
		return EmbeddedTypeRegistryImpl.getInstance();
	}

	/**
	 * For JSP files, text/html is the default content type. This may want
	 * this different for types like jsv (jsp for voice xml) For now, hard
	 * code to new instance. In future, should get instance from registry.
	 * 
	 * Specification cites HTML as the default contentType.
	 */
	protected EmbeddedTypeHandler getDefaultEmbeddedType() {
		return getHandlerFor(getDefaultContentType());
	}

	public INodeNotifier getTarget() {
		return notifierAtCreation;
	}

	public void release() {
		if (embeddedTypeHandler != null) {
			if (embeddedFactoryRegistry != null) {
				Iterator iterator = embeddedFactoryRegistry.iterator();
				INodeAdapterFactory factory = null;
				while (iterator.hasNext()) {
					factory = (INodeAdapterFactory) iterator.next();
					factory.release();
				}
			}
			// pa_TODO: possibly need to release here...
			// or "uninitializeFactoryRegistry"
			// initializeFactoryRegistry was called from JSPModelLoader
			embeddedTypeHandler = null;
		}
	}

	private IContentDescription getContentDescription(IDocument doc) {
		if (doc == null)
			return null;
		DocumentReader in = new DocumentReader(doc);
		return getContentDescription(in);
	}

	/**
	 * Returns content description for an input stream Assumes it's JSP
	 * content. Closes the input stream when finished.
	 * 
	 * @param in
	 * @return the IContentDescription for in, or null if in is null
	 */
	private IContentDescription getContentDescription(Reader in) {

		if (in == null)
			return null;

		IContentDescription desc = null;
		try {

			IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
			desc = contentTypeJSP.getDescriptionFor(in, IContentDescription.ALL);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		finally {
			try {
				in.close();
			}
			catch (IOException e) {
				Logger.logException(e);
			}
		}
		return desc;
	}

	public String getElIgnored() {
		return elIgnored;
	}

	public void setElIgnored(String ignored) {
		elIgnored = ignored;
	}
}