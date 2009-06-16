/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jst.jsp.core.internal.Assert;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.document.PageDirectiveWatcherFactory;
import org.eclipse.jst.jsp.core.internal.parser.JSPReParser;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.document.AbstractDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.CodedReaderCreator;
import org.eclipse.wst.sse.core.internal.encoding.ContentTypeEncodingPreferences;
import org.eclipse.wst.sse.core.internal.encoding.util.BufferedLimitedReader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.TagMarker;
import org.eclipse.wst.sse.core.internal.modelhandler.EmbeddedTypeRegistry;
import org.eclipse.wst.sse.core.internal.modelhandler.EmbeddedTypeRegistryImpl;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Document;

public class JSPDocumentLoader extends AbstractDocumentLoader {
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

	protected void addNestablePrefix(JSPSourceParser parser, String tagName) {
		TagMarker bm = new TagMarker(tagName);
		parser.addNestablePrefix(bm);
	}

	synchronized public IEncodedDocument createNewStructuredDocument(IFile iFile) throws IOException, CoreException {
		IStructuredDocument structuredDocument = null;
		try {
			structuredDocument = createCodedDocument(iFile);

			EmbeddedTypeHandler embeddedType = getEmbeddedType(iFile);
			if (embeddedType != null)
				embeddedType.initializeParser(structuredDocument.getParser());

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
				embeddedType.initializeParser(((IStructuredDocument) structuredDocument).getParser());
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
			Reader fullPreparedReader = getFullPreparedReader();
			BufferedLimitedReader limitedReader = new BufferedLimitedReader(fullPreparedReader, CodedIO.MAX_BUF_SIZE);
			jspProvider.set(limitedReader);
			if (jspProvider instanceof IJSPHeadContentDetector) {
				mimeType = ((IJSPHeadContentDetector) jspProvider).getContentType();
				fullPreparedReader.reset();
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

	public RegionParser getParser() {
		// remember, the Loader
		// will need to finish initialization of parser
		// based on "embedded content"
		JSPSourceParser parser = new JSPSourceParser();
		// add default nestable tag list
		addNestablePrefix(parser, JSP11Namespace.JSP_TAG_PREFIX);
		return parser;
	}

	protected String getPreferredNewLineDelimiter(IFile file) {
		String delimiter = ContentTypeEncodingPreferences.getPreferredNewLineDelimiter(ContentTypeIdForJSP.ContentTypeID_JSP);
		if (delimiter == null)
			delimiter = super.getPreferredNewLineDelimiter(file);
		return delimiter;
	}

	/**
	 * Specification cites ISO-8859-1/Latin-1 as the default charset.
	 */
	protected String getSpecDefaultEncoding() {
		return SPEC_DEFAULT_ENCODING;
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
		// structuredDocument.setDocumentPartitioner(new
		// JSPJavaDocumentPartioner());
		// even though this is an "empty model" ... we want it to have at
		// least
		// the
		// default embeddeded content type handler
		EmbeddedTypeHandler embeddedType = getJSPDefaultEmbeddedType();
		embeddedType.initializeParser(structuredDocument.getParser());
		return structuredDocument;
	}

	public IDocumentLoader newInstance() {
		return new JSPDocumentLoader();
	}

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		IDOMModel domModel = (IDOMModel) structuredModel;
		//
		// document must have already been set for this to
		// work.
		Document document = domModel.getDocument();
		Assert.isNotNull(document, JSPCoreMessages.JSPDocumentLoader_1);
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

	}

}
