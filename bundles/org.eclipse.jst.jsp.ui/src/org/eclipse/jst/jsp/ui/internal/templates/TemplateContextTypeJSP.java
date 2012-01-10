/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.templates;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibDescriptor;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * Base class for JSP template context types. Templates of this context type
 * apply to any place within JSP content type.
 */
public class TemplateContextTypeJSP extends TemplateContextType {
	public TemplateContextTypeJSP() {
		super();
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new EncodingTemplateVariableResolverJSP());
		addResolver(new URITemplateResolver());
		
	}

	/**
	 * Resolves the ${uri} Template Variable for a taglib directive providing
	 * URIs from the taglib index
	 *
	 */
	class URITemplateResolver extends SimpleTemplateVariableResolver {

		protected URITemplateResolver() {
			super("uri", JSPUIMessages.Template_Taglib_URI); //$NON-NLS-1$
		}
		
		public void resolve(TemplateVariable variable, TemplateContext context) {
			if (context instanceof DocumentTemplateContext) {
				DocumentTemplateContext docContext = (DocumentTemplateContext) context;
				final IPath path = getPath(docContext.getDocument());
				if (path != null) {
					String[] uris = getURIs(TaglibIndex.getAvailableTaglibRecords(path), path);
					if (uris != null && uris.length > 0) {
						variable.setValues(uris);
					}
				}
				
			}
		}

		private String[] getURIs(ITaglibRecord[] records, IPath basePath) {
			if (records != null) {
				Set uris = new HashSet(records.length);
				for (int i = 0; i < records.length; i++) {
					final ITaglibRecord record = records[i];
					final ITaglibDescriptor descriptor = record.getDescriptor();
					String uri = null;
					switch (record.getRecordType()) {
						case ITaglibRecord.URL:
							uris.add(descriptor.getURI());
							break;
						case ITaglibRecord.JAR: {
							IPath location = ((IJarRecord) record).getLocation();
							IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(location);
							IPath localContextRoot = FacetModuleCoreSupport.computeWebContentRootPath(basePath);
							for (int fileNumber = 0; fileNumber < files.length; fileNumber++) {
								if (localContextRoot.isPrefixOf(files[fileNumber].getFullPath())) {
									uri = IPath.SEPARATOR +
											files[fileNumber].getFullPath().removeFirstSegments(localContextRoot.segmentCount()).toString();
								}
								else {
									uri = FacetModuleCoreSupport.getRuntimePath(files[fileNumber].getFullPath()).toString();
								}
								uris.add(uri);
							}
							break;
						}
						case ITaglibRecord.TLD: {
							uri = descriptor.getURI();
							if (uri == null || uri.trim().length() == 0) {
								IPath path = ((ITLDRecord) record).getPath();
								IPath localContextRoot = FacetModuleCoreSupport.computeWebContentRootPath(basePath);
								if (localContextRoot.isPrefixOf(path)) {
									uri = IPath.SEPARATOR + path.removeFirstSegments(localContextRoot.segmentCount()).toString();
								}
								else {
									uri = FacetModuleCoreSupport.getRuntimePath(path).toString();
								}
							}
							uris.add(uri);
							break;
						}
						
					}
				}
				String[] urisArray = (String[]) uris.toArray(new String[uris.size()]);
				Arrays.sort(urisArray);
				return urisArray;
			}
			return null;
		}

		private IPath getPath(IDocument iDoc) {
			IPath path = null;
			if (iDoc instanceof IStructuredDocument) {
				IStructuredDocument document = (IStructuredDocument) iDoc;
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getModelForRead(document);
					if (model != null) {
						String location = model.getBaseLocation();
						if (location != null) {
							path = new Path(location);
						}
					}
				}
				finally {
					if (model != null)
						model.releaseFromRead();
				}
			}
			return path;
		}
	}
}
