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
package org.eclipse.jst.jsp.ui.openon;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.contentmodel.ITaglibRecord;
import org.eclipse.jst.jsp.core.internal.contentmodel.JarRecord;
import org.eclipse.jst.jsp.core.internal.contentmodel.TLDRecord;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibIndex;
import org.eclipse.jst.jsp.core.internal.contentmodel.URLRecord;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.html.ui.openon.DefaultOpenOnHTML;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This action opens classes referenced in JSP directive tags of a JSP page.
 * 
 * @deprecated Use base support for hyperlink navigation
 */
public class JSPDirectiveOpenOnJSP extends DefaultOpenOnHTML {
	JSPJavaOpenOnJSP jspJavaOpenOn;

	static class StorageEditorInput implements IStorageEditorInput {
		IStorage fStorage = null;

		StorageEditorInput(IStorage storage) {
			fStorage = storage;
		}

		public IStorage getStorage() throws CoreException {
			return fStorage;
		}

		public boolean exists() {
			return fStorage != null;
		}

		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		public String getName() {
			return fStorage.getName();
		}

		public IPersistableElement getPersistable() {
			return null;
		}

		public String getToolTipText() {
			return fStorage.getFullPath().toString();
		}

		public Object getAdapter(Class adapter) {
			return null;
		}
	}

	static class ZipStorage implements IStorage {
		File fFile = null;
		String fEntryName = null;
		String fTitle = null;

		ZipStorage(File file, String entryName, String title) {
			fFile = file;
			fEntryName = entryName;
			fTitle = title;
		}

		public InputStream getContents() throws CoreException {
			InputStream stream = null;
			try {
				ZipFile file = new ZipFile(fFile);
				ZipEntry entry = file.getEntry(fEntryName);
				stream = file.getInputStream(entry);
			}
			catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, JSPUIPlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, fTitle, e));
			}
			return stream;
		}

		public IPath getFullPath() {
			return new Path(fFile.getAbsolutePath() + IPath.SEPARATOR + fEntryName);
		}

		public String getName() {
			return fEntryName;
		}

		public boolean isReadOnly() {
			return true;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}
	}

	static class URLStorage implements IStorage {
		URL fURL = null;

		URLStorage(URL url) {
			fURL = url;
		}

		public InputStream getContents() throws CoreException {
			InputStream stream = null;
			try {
				stream = fURL.openStream();
			}
			catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, JSPUIPlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, fURL.toString(), e));
			}
			return stream;
		}

		public IPath getFullPath() {
			return new Path(fURL.toString());
		}

		public String getName() {
			return new Path(fURL.getFile()).lastSegment();
		}

		public boolean isReadOnly() {
			return true;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

	}

	private JSPJavaOpenOnJSP getJSPJavaOpenOn() {
		if (jspJavaOpenOn == null) {
			jspJavaOpenOn = new JSPJavaOpenOnJSP();
			// set the document to current document
			jspJavaOpenOn.setDocument(getDocument());
		}
		return jspJavaOpenOn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.openon.AbstractOpenOn#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument doc) {
		super.setDocument(doc);

		// also set the document for jspJavaOpenOn
		if (jspJavaOpenOn != null) {
			jspJavaOpenOn.setDocument(doc);
		}
	}

	/**
	 * Get JSP translation object
	 * 
	 * @return JSPTranslation if one exists, null otherwise
	 */
	private JSPTranslation getJSPTranslation() {
		// get JSP translation object for this action's editor's document
		XMLModel xmlModel = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
		if (xmlModel != null) {
			XMLDocument xmlDoc = xmlModel.getDocument();
			xmlModel.releaseFromRead();
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			if (adapter != null) {
				return adapter.getJSPTranslation();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.AbstractOpenOn#doOpenOn(org.eclipse.jface.text.IRegion)
	 */
	protected void doOpenOn(IRegion region) {
		boolean opened = false;
		IRegion newRegion = doGetOpenOnRegion(region.getOffset());
		// if there is a corresponding Java source offset, then use
		// JSPJavaOpenOnJSP
		JSPTranslation jspTranslation = getJSPTranslation();
		if ((jspTranslation != null) && (newRegion != null) && (jspTranslation.getJavaOffset(newRegion.getOffset()) > -1)) {
			getJSPJavaOpenOn().doOpenOn(newRegion);
			opened = true;
		}

		// check servlet and taglib mappings
		if (!opened) {
			Node current = getCurrentNode(newRegion.getOffset());
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Attr attr = getLinkableAttr((Element) current);
				if (attr != null) {
					ITaglibRecord reference = TaglibIndex.resolve(getBaseLocation(), attr.getValue(), false);
					if (reference != null) {
						try {
							switch (reference.getRecordType()) {
								case (ITaglibRecord.TLD) : {
									TLDRecord record = (TLDRecord) reference;
									openFileInEditor(record.getLocation().toString());
									opened = true;
								}
									break;
								case (ITaglibRecord.JAR) : {
									JarRecord record = (JarRecord) reference;
									IEditorInput input = new StorageEditorInput(new ZipStorage(record.getLocation().toFile(), "META-INF/taglib.tld", record.getLocation().toString()));
									IEditorDescriptor editor = IDE.getEditorDescriptor(input.getName());
									if (editor != null) {
										IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), input, editor.getId(), true);
										opened = true;
									}
								}
									break;
								case (ITaglibRecord.URL) : {
									URLRecord record = (URLRecord) reference;
									IEditorInput input = new StorageEditorInput(new URLStorage(record.getURL()));
									IEditorDescriptor editor = IDE.getEditorDescriptor(input.getName());
									if (editor != null) {
										IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), input, editor.getId(), true);
										opened = true;
									}
								}
							}
						}
						catch (Exception e) {
							openFileFailed();
						}
					}
				}
			}
		}

		if (!opened)
			super.doOpenOn(newRegion);
	}

	/**
	 * Return an attr of element that is of type URI if one exists. or if
	 * element is jsp:usebean return the type or class attribute. null
	 * otherwise.
	 * 
	 * @param element -
	 *            cannot be null
	 * @return Attr
	 */
	protected Attr getLinkableAttr(Element element) {
		String tagName = element.getTagName();

		// usebean
		if (JSP11Namespace.ElementName.USEBEAN.equalsIgnoreCase(tagName)) {
			// get the list of attributes for this node
			NamedNodeMap attrs = element.getAttributes();
			for (int i = 0; i < attrs.getLength(); ++i) {
				Attr att = (Attr) attrs.item(i);
				String attName = att.getName();
				// look for the type or class attribute
				if ((JSP11Namespace.ATTR_NAME_TYPE.equalsIgnoreCase(attName)) || (JSP11Namespace.ATTR_NAME_CLASS.equalsIgnoreCase(attName))) {
					return att;
				}
			}
		}

		// otherwise, just look for attribute value of type URI
		return super.getLinkableAttr(element);
	}
}