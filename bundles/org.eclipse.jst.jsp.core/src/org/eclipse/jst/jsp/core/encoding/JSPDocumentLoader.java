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
package org.eclipse.jst.jsp.core.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.document.PageDirectiveWatcherFactory;
import org.eclipse.jst.jsp.core.internal.parser.JSPReParser;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.internal.text.rules.StructuredTextPartitionerForJSP;
import org.eclipse.wst.common.encoding.CodedReaderCreator;
import org.eclipse.wst.common.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.PropagatingAdapter;
import org.eclipse.wst.sse.core.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.modelhandler.EmbeddedTypeRegistry;
import org.eclipse.wst.sse.core.internal.modelhandler.EmbeddedTypeRegistryImpl;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.sse.core.parser.JSPCapableParser;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.parser.TagMarker;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.Document;

public class JSPDocumentLoader extends AbstractDocumentLoader {
	private final static String DEFAULT_LANGUAGE = "java"; //$NON-NLS-1$
	private final static String DEFAULT_MIME_TYPE = "text/html"; //$NON-NLS-1$
	private final static String SPEC_DEFAULT_ENCODING = "ISO-8859-1"; //$NON-NLS-1$

	protected static IFile getFileFor(IStructuredModel model) {
		if (model == null)
			return null;
		String path = model.getBaseLocation();
		if (path == null || path.length() == 0) {
			Object id = model.getId();
			if (id == null)
				return null;
			path = id.toString();
		}
		// TODO needs rework for linked resources
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFileForLocation(new Path(path));
		return file;
	}

	private EmbeddedTypeRegistry fEmbeddedContentTypeRegistry;

	public JSPDocumentLoader() {
		super();
	}

	private void addNestablePrefix(JSPSourceParser parser, String tagName) {
		TagMarker bm = new TagMarker(tagName);
		parser.addNestablePrefix(bm);
	}

	synchronized public IEncodedDocument createNewStructuredDocument(IFile iFile) throws IOException, CoreException {
		IStructuredDocument structuredDocument = null;
		try {
			structuredDocument = createCodedDocument(iFile);

			EmbeddedTypeHandler embeddedType = getEmbeddedType(iFile);
			if (embeddedType != null)
				embeddedType.initializeParser((JSPCapableParser) structuredDocument.getParser());

			fFullPreparedReader.reset();
			setDocumentContentsFromReader(structuredDocument, fFullPreparedReader);

		}
		finally {
			if (fFullPreparedReader != null) {
				fFullPreparedReader.close();
			}
		}
		return structuredDocument;
	}

	private IStructuredDocument createCodedDocument(IFile iFile) throws CoreException, UnsupportedEncodingException, IOException {
		IStructuredDocument structuredDocument = (IStructuredDocument) createNewStructuredDocument();

		getCodedReaderCreator().set(iFile);

		fFullPreparedReader = getCodedReaderCreator().getCodedReader();
		fEncodingMemento = getCodedReaderCreator().getEncodingMemento();

		structuredDocument.setEncodingMemento(getCodedReaderCreator().getEncodingMemento());

		return structuredDocument;
	}

	public IEncodedDocument createNewStructuredDocument(String filename, InputStream inputStream) throws UnsupportedEncodingException, IOException {
		if (filename == null && inputStream == null) {
			throw new IllegalArgumentException("can not have both null filename and inputstream"); //$NON-NLS-1$
		}
		IEncodedDocument structuredDocument = createNewStructuredDocument();
		CodedReaderCreator codedReaderCreator = new CodedReaderCreator();
		try {
			codedReaderCreator.set(filename, inputStream);
			fFullPreparedReader = codedReaderCreator.getCodedReader();
			fEncodingMemento = codedReaderCreator.getEncodingMemento();
			structuredDocument.setEncodingMemento(fEncodingMemento);
			// the fact that file is null means this method/code path is no
			// good for JSP fragments
			EmbeddedTypeHandler embeddedType = getEmbeddedType((IFile) null);
			fFullPreparedReader.reset();
			if (embeddedType != null)
				embeddedType.initializeParser((JSPCapableParser) ((IStructuredDocument) structuredDocument).getParser());
			setDocumentContentsFromReader(structuredDocument, fFullPreparedReader);
		}
		catch (CoreException e) {
			// impossible in this context
			throw new Error(e);
		}
		finally {
			if (fFullPreparedReader != null) {
				fFullPreparedReader.close();
			}
		}
		return structuredDocument;

	}

	/**
	 * Method getDefaultDocumentPartitioner.
	 * 
	 * @return IDocumentPartitioner
	 */
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new StructuredTextPartitionerForJSP();
	}

	/**
	 * Method getDefaultMimeType.
	 * 
	 * @return String
	 */
	private String getDefaultMimeType() {
		return DEFAULT_MIME_TYPE;
	}

	/**
	 * @see com.ibm.sed.model.AbstractDumper#getDocumentEncodingDetector()
	 */
	public IDocumentCharsetDetector getDocumentEncodingDetector() {
		if (fDocumentEncodingDetector == null) {
			fDocumentEncodingDetector = new JSPDocumentHeadContentDetector();
		}
		return fDocumentEncodingDetector;
	}

	/**
	 * Gets the embeddedContentTypeRegistry.
	 * 
	 * @return Returns a EmbeddedContentTypeRegistry
	 */
	private EmbeddedTypeRegistry getEmbeddedContentTypeRegistry() {
		if (fEmbeddedContentTypeRegistry == null) {
			fEmbeddedContentTypeRegistry = EmbeddedTypeRegistryImpl.getInstance();
		}
		return fEmbeddedContentTypeRegistry;
	}

	private EmbeddedTypeHandler getEmbeddedType(IStructuredModel model) {
		Document doc = ((XMLModel) model).getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) doc).getAdapterFor(PageDirectiveAdapter.class);
		EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
		return embeddedHandler;
	}

	/**
	 * Determine the MIME content type specified in a page directive. This
	 * should appear "as early as possible in the JSP page" according to the
	 * JSP v1.2 specification.
	 */
	private EmbeddedTypeHandler getEmbeddedType(IFile file) throws UnsupportedEncodingException, CoreException, IOException {
		EmbeddedTypeHandler handler = null;
		if (fFullPreparedReader == null) {
			handler = getJSPDefaultEmbeddedType();
		}
		else {
			String mimeType = null;

			IDocumentCharsetDetector jspProvider = getDocumentEncodingDetector();
			jspProvider.set(getFullPreparedReader());
			if (jspProvider instanceof IJSPHeadContentDetector) {
				mimeType = ((IJSPHeadContentDetector) jspProvider).getContentType();
			}

			EmbeddedTypeRegistry reg = getEmbeddedContentTypeRegistry();
			if (mimeType == null || mimeType.length() == 0) {
				handler = getJSPDefaultEmbeddedType();
			}
			else {
				handler = reg.getTypeFor(mimeType);
			}
		}
		return handler;
	}

	/**
	 * For JSP files, text/html is the default content type. This may want
	 * this different for types like jsv (jsp for voice xml) For now, hard
	 * code to new instance. In future, should get instance from registry.
	 * Specification cites HTML as the default contentType.
	 */
	private EmbeddedTypeHandler getJSPDefaultEmbeddedType() {
		EmbeddedTypeRegistry reg = getEmbeddedContentTypeRegistry();
		return reg.getTypeFor(getDefaultMimeType());
	}

	/**
	 * Method getLanguage.
	 * 
	 * @param model
	 * @return String
	 */
	private String getLanguage(IStructuredModel model) throws IOException {
		String result = null;
		// first check the model (document itself) to see if contains
		result = getLanguageFromStructuredDocument(model.getStructuredDocument());
		// Note: if model contains an unsupported
		// language, we'll even return it,
		// since who knows what future holds.

		// always return something
		if (result == null) {
			result = DEFAULT_LANGUAGE;
		}
		return result;
	}

	/**
	 * Method getLanguageFromStructuredDocument.
	 * 
	 * @param structuredDocument
	 * @return String
	 */
	private String getLanguageFromStructuredDocument(IDocument document) throws IOException {
		if (document == null)
			return null;
		String result = null;
		// bascially same algorithm as get encoding or
		// get content type from structuredDocument.
		IJSPHeadContentDetector localHeadParser = (IJSPHeadContentDetector) getDocumentEncodingDetector();
		// we can be assured that its already been
		// parsed. If not call parseHeaderForPageDirective()
		// before calling getLanguage;
		localHeadParser.set(document);
		result = localHeadParser.getLanguage();
		return result;
	}

	public RegionParser getParser() {
		// remember, the Loader
		// will need to finish initialization of parser
		// based on "embedded content"
		JSPSourceParser parser = new JSPSourceParser();
		// add default nestable tag list
		addNestablePrefix(parser, JSP11Namespace.JSP_TAG_PREFIX);
		return parser;
	}

	protected String getPreferredNewLineDelimiter() {
		return ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForJSP.ContentTypeID_JSP);
	}

	/**
	 * Specification cites ISO-8859-1/Latin-1 as the default charset.
	 */
	protected String getSpecDefaultEncoding() {
		return SPEC_DEFAULT_ENCODING;
	}

	/**
	 * This is "reinitialize" since there should always be at least the
	 * default one assigned, before we start checking the stream
	 */
	private void initCloneOfEmbeddedType(IStructuredModel model, EmbeddedTypeHandler oldEmbeddedContentType, EmbeddedTypeHandler newEmbeddedContentType) throws IOException {
		// check program logic
		Assert.isNotNull(oldEmbeddedContentType, "Program error: invalid call during model initialization"); //$NON-NLS-1$
		// once we know the embedded content type, we need to set it in the
		// PageDirectiveAdapter ... the order of initialization is
		// critical here, the doc must have been created, but its contents not
		// set
		// yet,
		// and all factories must have been set up also.
		XMLModel domModel = (XMLModel) model;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		XMLDocument document = domModel.getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) document.getAdapterFor(PageDirectiveAdapter.class);
		// ==> // PropagatingAdapter propagatingAdapter = (PropagatingAdapter)
		// ((INodeNotifier) document).getAdapterFor(PropagatingAdapter.class);
		// ==> // ModelQueryAdapter modelQueryAdapter = (ModelQueryAdapter)
		// ((INodeNotifier) document).getAdapterFor(ModelQueryAdapter.class);
		// because, even in the clone case, the model has been paritally
		// intialized
		// with
		// the old embedded type (during createModel), we need to unitialize
		// parts of it, based on the old (or default) ones
		oldEmbeddedContentType.uninitializeFactoryRegistry(model.getFactoryRegistry());
		oldEmbeddedContentType.uninitializeParser((JSPCapableParser) structuredDocument.getParser());
		// remember, embedded type factories are automatically cleared when
		// embededType changed
		pageDirectiveAdapter.setEmbeddedType(newEmbeddedContentType);
		if (newEmbeddedContentType != null) {
			newEmbeddedContentType.initializeFactoryRegistry(model.getFactoryRegistry());
			newEmbeddedContentType.initializeParser((JSPCapableParser) structuredDocument.getParser());
		}
		// adding language here, in this convienent central
		// location, but some obvious renaming or refactoring
		// wouldn't hurt, in future.
		// I needed to add this language setting for JSP Fragment support
		// Note: this is the one that counts, since at this point,
		// the model has an ID, so we can look up IFile, etc.
		String language = getLanguage(model);
		if (language != null && language.length() > 0) {
			pageDirectiveAdapter.setLanguage(language);
		}
	}

	/**
	 * Method initEmbeddedType.
	 */
	private void initEmbeddedType(IStructuredModel model) {
		initializeEmbeddedTypeFromDefault(model);
	}

	/**
	 * Method initEmbeddedType.
	 */
	private void initEmbeddedType(IStructuredModel oldModel, IStructuredModel newModel) throws IOException {
		EmbeddedTypeHandler existingEmbeddedType = getEmbeddedType(oldModel);
		EmbeddedTypeHandler newEmbeddedContentType = existingEmbeddedType.newInstance();
		if (existingEmbeddedType == null) {
			initEmbeddedType(newModel);
		}
		else {
			//initEmbeddedType(newModel);
			initCloneOfEmbeddedType(newModel, existingEmbeddedType, newEmbeddedContentType);
		}
		setLanguageInPageDirective(newModel);
	}

	/**
	 * This is "initialize" since is always assumes it hasn't been initalized
	 * yet.
	 */
	private void initializeEmbeddedType(IStructuredModel model, EmbeddedTypeHandler embeddedContentType) {
		// check program logic
		Assert.isNotNull(embeddedContentType, "Program error: invalid call during model initialization"); //$NON-NLS-1$
		// once we know the embedded content type, we need to set it in the
		// PageDirectiveAdapter ... the order of initialization is
		// critical here, the doc must have been created, but its contents not
		// set
		// yet,
		// and all factories must have been set up also.
		XMLModel domModel = (XMLModel) model;
		XMLDocument document = domModel.getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) document.getAdapterFor(PageDirectiveAdapter.class);
		pageDirectiveAdapter.setEmbeddedType(embeddedContentType);
		embeddedContentType.initializeFactoryRegistry(model.getFactoryRegistry());
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		embeddedContentType.initializeParser((JSPCapableParser) structuredDocument.getParser());
		// adding language here, in this convienent central
		// location, but some obvious renaming or refactoring
		// wouldn't hurt, in future.
		// I needed to add this language setting for JSP Fragment support
		// Note: I don't think this attempted init counts for much.
		// I think its always executed when model is very first
		// being initialized, and doesn't even have content
		// or an ID yet. I thought I'd leave, since it wouldn't
		// hurt, in case its called in other circumstances.
		//		String language = getLanguage(model);
		//		pageDirectiveAdapter.setLanguage(language);
	}

	/**
	 * This init method is for the case where we are creating an empty model,
	 * which we always do.
	 */
	private void initializeEmbeddedTypeFromDefault(IStructuredModel model) {
		EmbeddedTypeHandler embeddedContentType = getJSPDefaultEmbeddedType();
		initializeEmbeddedType(model, embeddedContentType);
	}

	/**
	 * This method must return a new instance of IStructuredDocument, that has
	 * been initialized with appropriate parser. For many loaders, the
	 * (default) parser used is known for any input. For others, the correct
	 * parser (and its initialization) is normall dependent on the content of
	 * the file. This no-argument method should assume "empty input" and would
	 * therefore return the default parser for the default contentType. If the
	 * parser is to handle tag libraries, it must have a TaglibSupport object
	 * with a valid URIResolver and this IStructuredDocument attached to it
	 * before the contents are set on the IStructuredDocument.
	 */
	protected IEncodedDocument newEncodedDocument() {
		IStructuredDocument structuredDocument = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser());
		((BasicStructuredDocument) structuredDocument).setReParser(new JSPReParser());
		//		structuredDocument.setDocumentPartitioner(new
		// JSPJavaDocumentPartioner());
		// even though this is an "empty model" ... we want it to have at
		// least
		// the
		// default embeddeded content type handler
		EmbeddedTypeHandler embeddedType = getJSPDefaultEmbeddedType();
		embeddedType.initializeParser((JSPCapableParser) structuredDocument.getParser());
		return structuredDocument;
	}

	public IDocumentLoader newInstance() {
		return new JSPDocumentLoader();
	}

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		XMLModel domModel = (XMLModel) structuredModel;
		//
		// document must have already been set for this to
		// work.
		Document document = domModel.getDocument();
		Assert.isNotNull(document);
		// if there is a model in the adapter, this will adapt it to
		// first node. After that the PropagatingAdater spreads over the
		// children being
		// created. Each time that happends, a side effect is to
		// also "spread" sprecific registered adapters,
		// they two can propigate is needed.
		// This 'get' causes first to be be attached.
		PropagatingAdapter propagatingAdapter = (PropagatingAdapter) ((INodeNotifier) document).getAdapterFor(PropagatingAdapter.class);
		// may make this easier to use in futue
		propagatingAdapter.addAdaptOnCreateFactory(new PageDirectiveWatcherFactory());
		// For JSPs, the ModelQueryAdapter must be "attached" to the document
		// before content is set in the model, so taglib initization can
		// take place.
		((INodeNotifier) document).getAdapterFor(ModelQueryAdapter.class);
		//
		// 
	}

	/**
	 * This is "reinitialize" since there should always be at least the
	 * default one assigned, before we start checking the stream
	 */
	private void reInitializeEmbeddedType(IStructuredModel model, EmbeddedTypeHandler oldEmbeddedContentType, EmbeddedTypeHandler newEmbeddedContentType) throws IOException {
		// check program logic
		Assert.isNotNull(oldEmbeddedContentType, "Program error: invalid call during model initialization"); //$NON-NLS-1$
		// once we know the embedded content type, we need to set it in the
		// PageDirectiveAdapter ... the order of initialization is
		// critical here, the doc must have been created, but its contents not
		// set
		// yet,
		// and all factories must have been set up also.
		XMLModel domModel = (XMLModel) model;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		XMLDocument document = domModel.getDocument();
		PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) document.getExistingAdapter(PageDirectiveAdapter.class);
		// ==> // PropagatingAdapter propagatingAdapter = (PropagatingAdapter)
		// ((INodeNotifier)
		// document).getExistingAdapter(PropagatingAdapter.class);
		// ==> // ModelQueryAdapter modelQueryAdapter = (ModelQueryAdapter)
		// ((INodeNotifier)
		// document).getExistingAdapter(ModelQueryAdapter.class);
		oldEmbeddedContentType.uninitializeFactoryRegistry(model.getFactoryRegistry());
		oldEmbeddedContentType.uninitializeParser((JSPCapableParser) structuredDocument.getParser());
		// since 'document' is not recreated in this
		// reinit path, we need to remove all adapters,
		// except for the propagated adapters (including page
		// directive adapter, and model query adapter).
		// to accomplish this, we'll just remove all, then
		// add back with a call to pre-load adapt.
		// let clients decide to unload adapters from document
		//		Collection oldAdapters = document.getAdapters();
		//		Iterator oldAdaptersIterator = oldAdapters.iterator();
		//		while (oldAdaptersIterator.hasNext()) {
		//			INodeAdapter oldAdapter = (INodeAdapter)
		// oldAdaptersIterator.next();
		//			if (oldAdapter != pageDirectiveAdapter && oldAdapter !=
		// propagatingAdapter && oldAdapter != modelQueryAdapter) {
		//				// DO NOT remove directly!
		//				// can change contents while in notifity loop!
		//				//oldAdaptersIterator.remove();
		//				document.removeAdapter(oldAdapter);
		//			}
		//		}
		// DMW: I believe something like the following is needed,
		// since releases cached adapters
		//				if (document instanceof DocumentImpl) {
		//					((DocumentImpl) document).releaseDocumentType();
		//					((DocumentImpl) document).releaseStyleSheets();
		//				}
		// remember, embedded type factories are automatically cleared when
		// embededType changed
		pageDirectiveAdapter.setEmbeddedType(newEmbeddedContentType);
		//		// but still need to clear the page directive watchers, and let
		// them
		// be
		// rediscovered (with new, accurate node as target)
		//		pageDirectiveAdapter.clearPageWatchers();
		if (newEmbeddedContentType != null) {
			newEmbeddedContentType.initializeFactoryRegistry(model.getFactoryRegistry());
			newEmbeddedContentType.initializeParser((JSPCapableParser) structuredDocument.getParser());
		}
		// adding language here, in this convienent central
		// location, but some obvious renaming or refactoring
		// wouldn't hurt, in future.
		// I needed to add this language setting for JSP Fragment support
		// Note: this is the one that counts, since at this point,
		// the model has an ID, so we can look up IFile, etc.
		String language = getLanguage(model);
		if (language != null && language.length() > 0) {
			pageDirectiveAdapter.setLanguage(language);
		}
	}

	private void setLanguageInPageDirective(IStructuredModel newModel) throws IOException {
		if (newModel instanceof XMLModel) {
			XMLDocument document = ((XMLModel) newModel).getDocument();
			PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) document.getAdapterFor(PageDirectiveAdapter.class);
			String language = getLanguage(newModel);
			pageDirectiveAdapter.setLanguage(language);
		}
	}

}