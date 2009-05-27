/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexDelta;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexListener;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;

/**
 * Provides a direct mapping from IStructuredDocument to supporting
 * TLDCMDocumentManager.
 * 
 * Listens to the creation of JSP type TextFileBuffers and forces a text-less
 * reparse after connecting taglib-supporting listeners. Connecting the
 * listeners before the text is set would be ideal, but there is no way to
 * look up taglib references since the location is not yet knowable. Since
 * taglibs can affect the parsing of the document, a reparse is currently
 * required to react to custom tags with tagdependent content.
 * 
 * TODO: Remove the reparse penalty.
 */
public class TaglibController implements IDocumentSetupParticipant, IDocumentSetupParticipantExtension {

	class DocumentInfo implements ITaglibIndexListener {
		IStructuredDocument document;
		ITextFileBuffer textFileBuffer;
		IPath location;
		LocationKind locationKind;
		TLDCMDocumentManager tldDocumentManager;

		public void indexChanged(ITaglibIndexDelta delta) {
			int type = delta.getKind();
			if (type == ITaglibIndexDelta.CHANGED || type == ITaglibIndexDelta.REMOVED) {
				ITaglibIndexDelta[] deltas = delta.getAffectedChildren();
				boolean affected = false;
				for (int i = 0; i < deltas.length; i++) {
					Object key = TLDCMDocumentManager.getUniqueIdentifier(deltas[i].getTaglibRecord());
					if (tldDocumentManager.getDocuments().containsKey(key)) {
						affected = true;
					}
				}
				if (affected) {
					if (_debugCache) {
						System.out.println("TLDCMDocumentManager cleared its private CMDocument cache"); //$NON-NLS-1$
					}
					tldDocumentManager.getDocuments().clear();
					tldDocumentManager.getSourceParser().resetHandlers();

					if (document instanceof BasicStructuredDocument) {
						((BasicStructuredDocument) document).reparse(this);
					}
				}
			}
			tldDocumentManager.indexChanged(delta);
		}
	}

	static final boolean _debugCache = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/cache")); //$NON-NLS-1$ //$NON-NLS-2$

	class FileBufferListener implements IFileBufferListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferContentAboutToBeReplaced(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferContentAboutToBeReplaced(IFileBuffer buffer) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferContentReplaced(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferContentReplaced(IFileBuffer buffer) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferCreated(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferCreated(IFileBuffer buffer) {
			if (buffer instanceof ITextFileBuffer) {
				IDocument document = ((ITextFileBuffer) buffer).getDocument();
				// ignore non-JSP documents
				synchronized (_instance.fJSPdocuments) {
					if (!_instance.fJSPdocuments.contains(document))
						return;
				}
				Assert.isTrue(document instanceof IStructuredDocument, getClass().getName() + " SetupParticipant was called for non-IStructuredDocument"); //$NON-NLS-1$

				DocumentInfo info = null;
				synchronized (_instance.fDocumentMap) {
					info = (DocumentInfo) _instance.fDocumentMap.get(document);
				}
				if (info != null) {
					// remember the buffer now
					info.textFileBuffer = (ITextFileBuffer) buffer;
				}
				else {
					info = new DocumentInfo();
					info.document = (IStructuredDocument) document;
					info.textFileBuffer = (ITextFileBuffer) buffer;
					info.location = buffer.getLocation();
					info.locationKind = LocationKind.NORMALIZE;
					info.tldDocumentManager = new TLDCMDocumentManager();
					info.tldDocumentManager.setSourceParser((XMLSourceParser) info.document.getParser());
					synchronized (_instance.fDocumentMap) {
						_instance.fDocumentMap.put(document, info);
					}
					TaglibIndex.addTaglibIndexListener(info);
					if (document instanceof BasicStructuredDocument && document.getLength() > 0) {
						((BasicStructuredDocument) document).reparse(this);
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#bufferDisposed(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void bufferDisposed(IFileBuffer buffer) {
			if (buffer instanceof ITextFileBuffer) {
				IDocument document = ((ITextFileBuffer) buffer).getDocument();
				synchronized (_instance.fJSPdocuments) {
					if (!_instance.fJSPdocuments.remove(document))
						return;
				}
			}
			DocumentInfo info = null;
			synchronized (fDocumentMap) {
				Map.Entry[] entries = (Map.Entry[]) fDocumentMap.entrySet().toArray(new Map.Entry[fDocumentMap.size()]);
				for (int i = 0; i < entries.length; i++) {
					info = (DocumentInfo) entries[i].getValue();
					/**
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=222137
					 * 
					 * Might be null if setup() has been called but
					 * bufferCreated() has not, yet.
					 */
					if (info != null && info.textFileBuffer != null && info.textFileBuffer.equals(buffer)) {
						fDocumentMap.remove(entries[i].getKey());
						break;
					}
				}
			}
			if (info != null) {
				info.tldDocumentManager.clearCache();
				TaglibIndex.removeTaglibIndexListener(info);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#dirtyStateChanged(org.eclipse.core.filebuffers.IFileBuffer,
		 *      boolean)
		 */
		public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#stateChangeFailed(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void stateChangeFailed(IFileBuffer buffer) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#stateChanging(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void stateChanging(IFileBuffer buffer) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#stateValidationChanged(org.eclipse.core.filebuffers.IFileBuffer,
		 *      boolean)
		 */
		public void stateValidationChanged(IFileBuffer buffer, boolean isStateValidated) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#underlyingFileDeleted(org.eclipse.core.filebuffers.IFileBuffer)
		 */
		public void underlyingFileDeleted(IFileBuffer buffer) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.filebuffers.IFileBufferListener#underlyingFileMoved(org.eclipse.core.filebuffers.IFileBuffer,
		 *      org.eclipse.core.runtime.IPath)
		 */
		public void underlyingFileMoved(IFileBuffer buffer, IPath path) {
		}


	}

	static TaglibController _instance = null;
	static private boolean fIsShutdown = false;

	public static IPath getLocation(IDocument document) {
		synchronized (_instance.fDocumentMap) {
			DocumentInfo info = (DocumentInfo) _instance.fDocumentMap.get(document);
			if (info != null)
				return info.location;
			return null;
		}
	}

	/**
	 * @param manager
	 * @return
	 */
	public static IPath getLocation(TLDCMDocumentManager manager) {
		// if _instance is null, we are already shutting donw
		if (_instance == null)
			return null;

		IPath location = null;
		synchronized (_instance.fDocumentMap) {
			Iterator docInfos = _instance.fDocumentMap.values().iterator();
			while (docInfos.hasNext() && location == null) {
				DocumentInfo info = (DocumentInfo) docInfos.next();
				if (info.tldDocumentManager.equals(manager))
					location = info.location;
			}
		}
		return location;
	}

	public static TLDCMDocumentManager getTLDCMDocumentManager(IDocument document) {
		// if _instance is null, we are already shutting down
		if (_instance == null)
			return null;
		synchronized (_instance.fDocumentMap) {
			DocumentInfo info = (DocumentInfo) _instance.fDocumentMap.get(document);
			if (info != null)
				return info.tldDocumentManager;
			return null;

		}
	}

	private static synchronized boolean isShutdown() {
		return fIsShutdown;
	}

	private static synchronized void setShutdown(boolean isShutdown) {
		fIsShutdown = isShutdown;
	}

	public synchronized static void shutdown() {
		setShutdown(true);
		FileBuffers.getTextFileBufferManager().removeFileBufferListener(_instance.fBufferListener);
		_instance = null;
	}

	public synchronized static void startup() {
		if (_instance == null) {
			_instance = new TaglibController();
			FileBuffers.getTextFileBufferManager().addFileBufferListener(_instance.fBufferListener);
		}
		setShutdown(false);
	}

	IFileBufferListener fBufferListener;

	Map fDocumentMap;

	List fJSPdocuments;

	/*
	 * This constructor is only to be called as part of the FileBuffer
	 * framework
	 */
	public TaglibController() {
		super();
		fBufferListener = new FileBufferListener();
		fJSPdocuments = new ArrayList(1);
		fDocumentMap = new HashMap(1);
	}


	/*
	 * This method is only to be called as part of the FileBuffer framework
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
	 */
	public void setup(IDocument document) {
		// if we've already shutdown, just ignore
		if (isShutdown())
			return;
		// reference the shared instance's documents directly
		synchronized (_instance.fJSPdocuments) {
			_instance.fJSPdocuments.add(document);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension#setup(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.core.runtime.IPath,
	 *      org.eclipse.core.filebuffers.LocationKind)
	 */
	public void setup(IDocument document, IPath location, LocationKind locationKind) {
		// if we've already shutdown, just ignore
		if (isShutdown())
			return;
		// reference the shared instance's documents directly
		synchronized (_instance.fJSPdocuments) {
			_instance.fJSPdocuments.add(document);
		}

		DocumentInfo info = new DocumentInfo();
		info.document = (IStructuredDocument) document;
		info.textFileBuffer = null; // will be supplied later
		info.location = location;
		info.locationKind = locationKind;
		info.tldDocumentManager = new TLDCMDocumentManager();
		synchronized (_instance.fDocumentMap) {
			_instance.fDocumentMap.put(document, info);
		}
		info.tldDocumentManager.setSourceParser((XMLSourceParser) info.document.getParser());
		if (document instanceof BasicStructuredDocument && document.getLength() > 0) {
			((BasicStructuredDocument) document).reparse(this);
		}
		TaglibIndex.addTaglibIndexListener(info);
	}
}
