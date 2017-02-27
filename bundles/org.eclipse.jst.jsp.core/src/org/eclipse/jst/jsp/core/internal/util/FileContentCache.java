/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.Logger;

public class FileContentCache {
	private static class CacheEntry {
		String contents;
		long modificationStamp = IResource.NULL_STAMP;
		IPath contentPath;

		CacheEntry(IPath path) {
			this.contentPath = path;
			modificationStamp = getModificationStamp(path);
			contents = readContents(path);
		}

		private IFile getFile(IPath path) {
			if (path.segmentCount() > 1) {
				return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
			return null;
		}

		boolean isStale() {
			if (modificationStamp == IResource.NULL_STAMP) {
				return true;
			}
			long newStamp = getModificationStamp(contentPath);
			return newStamp > modificationStamp;
		}


		/**
		 * @param name
		 * @param contents2
		 * @return
		 */
		private String detectCharset(String name, byte[] contents) throws IOException {
			IContentDescription description = Platform.getContentTypeManager().getDescriptionFor(new ByteArrayInputStream(contents), name, new QualifiedName[]{IContentDescription.CHARSET});
			if (description != null) {
				String charset = description.getCharset();
				if (charset == null && description.getContentType() != null)
					charset = description.getContentType().getDefaultCharset();
				if (charset != null)
					return charset;
			}
			return ResourcesPlugin.getEncoding();
		}

		private long getModificationStamp(IPath filePath) {
			IFile f = getFile(filePath);
			if (f != null && f.isAccessible()) {
				return f.getModificationStamp();
			}
			File file = filePath.toFile();
			if (file.exists())
				return file.lastModified();
			return IResource.NULL_STAMP;
		}

		/**
		 * Read once and store the contents to determine the encoding and reuse as input
		 */
		private String readContents(IPath filePath) {
			if (DEBUG)
				System.out.println(getClass().getName() + " readContents: " + filePath); //$NON-NLS-1$
			
			InputStream is = null;
			try {
				IFile f = getFile(filePath);
				if (f != null && f.isAccessible()) {
					is = f.getContents(true);
				}
				else {
					is = new FileInputStream(filePath.toFile());
				}

				ByteArrayOutputStream store = new ByteArrayOutputStream();
				byte[] readBuffer = new byte[8092];
				int n = is.read(readBuffer);
				while (n > 0) {
					store.write(readBuffer, 0, n);
					n = is.read(readBuffer);
				}
				
				byte[] bytes = store.toByteArray();
				String charset = detectCharset(filePath.lastSegment(), bytes);
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				try {
					CharBuffer charBuffer = Charset.forName(charset).decode(buffer);
					return charBuffer.toString();
				}
				catch (IllegalCharsetNameException e) {
					return new String(bytes, charset);
				}
				catch (UnsupportedCharsetException e) {
					return new String(bytes, charset);
				}
			}
			catch (CoreException e) {
				Logger.logException(e);
				// out of sync
			}
			catch (Exception e) {
//				Logger.logException(e);
			}
			finally {
				try {
					if (is != null) {
						is.close();
					}
				}
				catch (Exception e) {
					// nothing to do
				}
			}
			return null;
		}

	}

	static final boolean DEBUG = false;

	static FileContentCache instance = new FileContentCache();

	public static FileContentCache getInstance() {
		return instance;
	}

	static class LimitedHashMap extends LinkedHashMap {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
			return size() > 25; //completely arbitrary number
		}
	}
	private LinkedHashMap fContentMap;

	private FileContentCache() {
		super();
		fContentMap = new LimitedHashMap();
	}

	private void cleanup() {
		synchronized (fContentMap) {
			Iterator iterator = fContentMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				if (entry.getValue() != null && ((Reference) entry.getValue()).get() == null) {
					iterator.remove();
				}
			}
		}
	}

	public String getContents(IPath filePath) {
		if (DEBUG)
			System.out.println(getClass().getName() + "#getContents: " + filePath); //$NON-NLS-1$

		/*
		 * Use an open file buffer if one exists for contents in dirty
		 * editors. LocationKind.IFILE will only apply to workspace paths, but
		 * they're far more likely to be open in an editor and modified than
		 * files outside the workspace (LocationKind.LOCATION). We'll use
		 * LocationKind.NORMALIZE and let the framework sort things out,
		 * unless it proves to be a performance problem.
		 * 
		 * Do not cause a file buffer to be opened by calling connect()
		 */
		ITextFileBuffer existingBuffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(
				filePath, LocationKind.NORMALIZE);
		if (existingBuffer != null) {
			IDocument document = existingBuffer.getDocument();
			if (document != null) {
				return document.get();
			}
		}

		//check the cache
		CacheEntry entry = null;
		Object o = fContentMap.get(filePath);
		if (o instanceof Reference) {
			entry = (CacheEntry) ((Reference) o).get();
		}
		if (entry == null || entry.isStale()) {
			if (DEBUG && entry != null && entry.isStale())
				System.out.println(getClass().getName() + " stale contents: " + filePath); //$NON-NLS-1$
			entry = new CacheEntry(filePath);
			synchronized (fContentMap) {
				fContentMap.put(filePath, new SoftReference(entry));
			}
		}
		cleanup();
		return entry.contents;
	}
}
