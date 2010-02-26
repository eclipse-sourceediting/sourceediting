/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * <p>This {@link IResourceChangeListener} is used to keep the {@link JSPTranslator}s for JSP
 * resources persisted to disk.  It can also be used to get persisted translators</p>
 * <p>This class should be registered as an {@link IResourceChangeListener} on the Workspace
 * as well as processing resource change events from a saved state, for example use see below.<p>
 * <p><b>Plugin Activation:</b>
 * <pre>
 * try {
 *   ISavedState savedState = ResourcesPlugin.getWorkspace().addSaveParticipant(
 *     plugin.getBundle().getSymbolicName(), this.fSaveParticipant);
 *   if (savedState != null) {
 *     savedState.processResourceChangeEvents(JSPTranslatorPersistor.getDefault());
 *   }
 * } catch(CoreException e) {}
 * ResourcesPlugin.getWorkspace().addResourceChangeListener(JSPTranslatorPersistor.getDefault());
 * </pre>
 * <b>Plugin Deactivation:</b>
 * <pre>
 * ResourcesPlugin.getWorkspace().removeSaveParticipant(plugin.getBundle().getSymbolicName());
 * ResourcesPlugin.getWorkspace().removeResourceChangeListener(JSPTranslatorPersistor.getDefault());
 * </pre></p>
 * 
 * <p>This class can be deactivated through the <code>persistJSPTranslations</code> system property,
 * a value of <code>true</code> means the persister is activated (which is the default), value of
 * <code>false</code> means the persister is not activated.</p>
 */
public class JSPTranslatorPersister implements IResourceChangeListener {
	/**
	 * <code>true</code> if the persister is activated, <code>false</code>
	 * otherwise.  This is determined by checking the system property
	 * <code>persistJSPTranslations</code>, if no value supplied then
	 * default is <code>true</code>
	 */
	public static final boolean ACTIVATED =
		Boolean.valueOf(System.getProperty("persistJSPTranslations", "true")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
	
	/** the location where {@link JSPTranslator}s are externalized too for persistence purposes */
	private static final IPath PERSIST_LOCATION = JSPCorePlugin.getDefault().getStateLocation().append("translators"); //$NON-NLS-1$
	
	/** used to calculate persisted translator file names */
	private static final CRC32 CHECKSUM_CALC = new CRC32();
	
	/** singleton instance of the {@link JSPTranslatorPersister} */
	private static final JSPTranslatorPersister INSTANCE = new JSPTranslatorPersister();
	
	/**
	 * Used to handle resource change events
	 * @see #resourceChanged(IResourceChangeEvent)
	 */
	private IResourceDeltaVisitor fResourceDeltaVisitor;
	
	/**
	 * <p>Private singleton default constructor</p>
	 */
	private JSPTranslatorPersister() {
		this.fResourceDeltaVisitor = new JSPResourceVisitor();
	}
	
	/**
	 * <p><b>NOTE: </b><i>This can possible return <code>null</code></i></p>
	 * 
	 * @return Singleton instance of the {@link JSPTranslatorPersister} if
	 * {@link #ACTIVATED} is <code>true</code>, <code>null</code> otherwise.
	 */
	public static JSPTranslatorPersister getDefault() {
		return ACTIVATED ? INSTANCE : null;
	}
	
	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		// only analyze the full (starting at root) delta hierarchy
		IResourceDelta delta = event.getDelta();
		if (delta != null && delta.getFullPath().toString().equals("/")) { //$NON-NLS-1$
			try {
				//use visitor to visit all children
				delta.accept(this.fResourceDeltaVisitor, false);
			} catch (CoreException e) {
				Logger.logException("Processing resource change event delta failed, " +
						"persisted JSPTranslators may not have been updated.", e);
			}
		}
	}
	
	/**
	 * <p>Given the {@link IStructuredModel} of a JSP file attempts to retrieve the persisted
	 * {@link JSPTranslator} for that model.</p>
	 * <p><b>NOTE: </b><i>It is possible for there not to be a persisted translator</i></p>
	 * 
	 * @param model {@link IStructuredModel} to get the persisted {@link JSPTranslator} for
	 * @return the persisted {@link JSPTranslator} for the given <code>model</code>, or
	 * <code>null</code> if none could be found or an existing one could not be read
	 */
	public static JSPTranslator getPersistedTranslator(IStructuredModel model) {
		String persistedTranslatorFilePath = getPersistedTranslatorFilePath(model.getBaseLocation());
		File persistedTranslatorFile = new File(persistedTranslatorFilePath);
		
		//attempt to read in the externalized translator
		JSPTranslator translator = null;
		ObjectInputStream in = null;
		try {
			//get the persisted translator file if one exists
			if(persistedTranslatorFile.exists()) {
				long persistedTranslatorFileTimestamp = persistedTranslatorFile.lastModified();
				long jspFileTimestamp = FileBufferModelManager.getInstance().getBuffer(
						model.getStructuredDocument()).getModificationStamp();
				
				/* if the persisted translator timestamp is newer then the jsp file timestamp
				 * then the translation has not become stale, otherwise it has so delete
				 * it and don't use it */
				if(persistedTranslatorFileTimestamp > jspFileTimestamp) {
					FileInputStream fis = new FileInputStream(persistedTranslatorFile);
					in = new ObjectInputStream(fis);
					translator = (JSPTranslator)in.readObject();
					
					//do post read external setup
					if(translator != null) {
						translator.postReadExternalSetup(model);
					}
				} else {
					persistedTranslatorFile.delete();
				}
			}
		} catch(InvalidClassException e) {
			/* this means that the externalized translator is for an older version
			 * of the JSPTranslator, so delete it */
			persistedTranslatorFile.delete();
		}catch (IOException e) {
			Logger.logException("Could not read externalized JSPTranslator at " + persistedTranslatorFilePath, e);
		} catch (ClassNotFoundException e) {
			Logger.logException("Class of a serialized JSPTranslator cannot be found", e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Logger.logException("Could not close externalized JSPTranslator that was just read", e);
				}
			}
		}
		
		return translator;
	}
	
	/**
	 * <p>Given the path to a JSP file determines the path to its persisted {@link JSPTranslator}</p>
	 * 
	 * @param jspFilePath {@link IPath} to JSP file for which the path to its persisted {@link JSPTranslator}
	 * should be determined
	 * 
	 * @return OS file path to the persisted {@link JSPTranslator} associated with the JSP file at
	 * <code>jspFilePath</code>
	 */
	protected static String getPersistedTranslatorFilePath(String jspFilePath) {
		CHECKSUM_CALC.reset();
		CHECKSUM_CALC.update(jspFilePath.getBytes());
		String persistedTranslatorFileName = Long.toString(CHECKSUM_CALC.getValue()) + ".translator"; //$NON-NLS-1$
		IPath location = PERSIST_LOCATION;
		
		// ensure the folder exists on disk
        File folder = new File(location.toOSString());
		if (!folder.isDirectory()) {
			try {
				folder.mkdir();
			}
			catch (SecurityException e) {
			}
		}
		
		location = location.addTrailingSeparator();
		location = location.append(persistedTranslatorFileName);
		return location.toOSString();
	}
	
	/**
	 * @see JSPResourceVisitor#visit(IResourceDelta)
	 */
	private class JSPResourceVisitor implements IResourceDeltaVisitor {
		/**
		 * <p>Default constructor</p>
		 */
		protected JSPResourceVisitor() {
		}
		
		/**
		 * <p>For each {@link IResourceDelta} determine if its a JSP resource and if it is
		 * update its persisted translator accordingly</p>
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			if(isJSPResource(delta.getResource())) {
				switch (delta.getKind()) {
					case IResourceDelta.CHANGED :
					case IResourceDelta.ADDED : {
						/* if a move, then move the persisted translation
						 * else create a new persisted translation, if its a change then
						 *   the old persisted translation will be overwritten */
						if((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
							renamePersistedTranslator(delta.getMovedFromPath(), delta.getFullPath());
						} else {
							IPath jspFilePath = delta.getFullPath();
							JSPTranslator translator = getJSPTranslator(jspFilePath);
							if(translator != null) {
								persistTranslator(translator, jspFilePath);
							}
						}
						
						break;
					}
					case IResourceDelta.REMOVED : {
						/* only remove if its not a move,
						 * if it is a move the added file delta event will move translation */
						if((delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
							deletePersistedTranslator(delta.getFullPath());
						}
						break;
					}
				}
			}
			
			//visit children deltas
			return true;
		}
		
		/**
		 * <p>Determines if an {@link IResource} is a JSP resource</p>
		 * 
		 * @param resource determine if this {@link IResource} is a JSP resource
		 * @return <code>true</code> if <code>resource</code> is a JSP resource,
		 * <code>false</code> otherwise.
		 */
		private boolean isJSPResource(IResource resource) {
			boolean isJSP = false;
			
			//general rule for getting files in the workspace
			if(resource.getFullPath().segmentCount() >= 2) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(resource.getFullPath());
				if(file.getType() == IResource.FILE) {
					//get JSP content type each time because there is a possibility it could change
					IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(
							ContentTypeIdForJSP.ContentTypeID_JSP);
					
					isJSP = contentTypeJSP.isAssociatedWith(file.getName());
				}
			}
			
			return isJSP;
		}
		
		/**
		 * <p>Gets the associated {@link JSPTranslator} for a specific JSP file.</p>
		 * <p><b>NOTE: </b><i>This does not get the persisted translator but rather the
		 * associated translator in memory</i></p>
		 * 
		 * @param jspFilePath {@link IPath} to the JSP file that the associated {@link JSPTranslator}
		 * is needed for
		 * @return {@link JSPTranslator} associated with the given <code>jspFilePath</code>, or
		 * <code>null</code> if none can be found.
		 */
		private JSPTranslator getJSPTranslator(IPath jspFilePath) {
			IFile jspFile = ResourcesPlugin.getWorkspace().getRoot().getFile(jspFilePath);
			IStructuredModel model = null;
			JSPTranslator translator = null;
			try {
				model = StructuredModelManager.getModelManager().getModelForRead(jspFile);
				if(model instanceof IDOMModel) {
					IDOMDocument doc = ((IDOMModel)model).getDocument();
					ModelHandlerForJSP.ensureTranslationAdapterFactory(model);
					JSPTranslationAdapter adapter = (JSPTranslationAdapter)doc.getAdapterFor(IJSPTranslation.class);
					
					//don't want to persist a translator that has not already been requested
					if(adapter != null && adapter.hasTranslation()) {
						translator = adapter.getJSPTranslation().getTranslator();
					}
				}
			} catch (IOException e) {
				Logger.logException("Could not get translator for " + jspFilePath +
						" because could not read model for same.", e);
			} catch (CoreException e) {
				Logger.logException("Could not get translator for " + jspFilePath +
						" because could not read model for same.", e);
			} finally {
				if(model != null) {
					model.releaseFromRead();
				}
			}
			
			return translator;
		}
		
		/**
		 * <p>Persists a {@link JSPTranslator} to disk for a specific JSP file</p>
		 * 
		 * @param translator {@link JSPTranslator} to persist to disk
		 * @param jspFilePath {@link IPath} to the JSP file the given <code>translator</code> is for
		 */
		private void persistTranslator(JSPTranslator translator, IPath jspFilePath) {
			String persistedTranslatorFilePath =
				getPersistedTranslatorFilePath(jspFilePath.toPortableString());
			try {
				FileOutputStream fos = new FileOutputStream(persistedTranslatorFilePath);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(translator);
				out.close();
			} catch (IOException e) {
				Logger.logException("Was unable to externalize JSPTranslator " + translator +
						" to " + persistedTranslatorFilePath, e);
			}
		}
		
		/**
		 * <p>Deletes a persisted translation for a JSP file that has been deleted</p>
		 * 
		 * @param jspFilePath {@link IPath} to the JSP file that has been deleted
		 */
		private void deletePersistedTranslator(IPath jspFilePath) {
			String persistedTranslatorFilePath =
				getPersistedTranslatorFilePath(jspFilePath.toPortableString());
			File persistedTranslatorFile = new File(persistedTranslatorFilePath);
			persistedTranslatorFile.delete();
		}
		
		/**
		 * <p>Renames a persisted translation for a JSP file that has moved</p>
		 * 
		 * @param jspPrevFilePath {@link IPath} to the previous location of JSP file</p>
		 * @param jspNewFilePath {@link IPath} to new location of JSP file</p>
		 */
		private void renamePersistedTranslator(IPath jspPrevFilePath, IPath jspNewFilePath) {
			String prevPersistedTranslatorFilePath =
				getPersistedTranslatorFilePath(jspPrevFilePath.toPortableString());
			String newPersistedTranslatorFilePath =
				getPersistedTranslatorFilePath(jspNewFilePath.toPortableString());
			File oldPersistedTranslatorFile = new File(prevPersistedTranslatorFilePath);
			File newPersistedTranslatorFile = new File(newPersistedTranslatorFilePath);
			
			//do the move
			oldPersistedTranslatorFile.renameTo(newPersistedTranslatorFile);
		}
	}
}