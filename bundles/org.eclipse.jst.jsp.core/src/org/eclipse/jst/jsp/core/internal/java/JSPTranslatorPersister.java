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
import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * <p>This is a static class used to persist JSP translations and retrieve the persisted
 * translations.</p>
 * 
 * <p>It is not actually in charge of finding files to persist, rather it provides API
 * for some other mechanism that tracks JSP files to call into to persist the translations.</p>
 * 
 * <p>This class can be deactivated through the <code>persistJSPTranslations</code> system property,
 * a value of <code>true</code> means the persister is activated (which is the default), value of
 * <code>false</code> means the persister is not activated.</p>
 * 
 * @see JSPIndexManager
 */
public class JSPTranslatorPersister{
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
	
	/** lock to use while using the checksum */
	private static final Object CHECKSUM_CALC_LOCK = new Object();
	
	/**
	 * <p>Private constructor to prevent creating an instance of this class</p>
	 */
	private JSPTranslatorPersister() {
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
			Logger.logException("Could not read externalized JSPTranslator at " + persistedTranslatorFilePath, e); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			Logger.logException("Class of a serialized JSPTranslator cannot be found", e); //$NON-NLS-1$
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Logger.logException("Could not close externalized JSPTranslator that was just read", e); //$NON-NLS-1$
				}
			}
		}
		
		return translator;
	}
	
	/**
	 * @param resource JSP resource who's translation should be persisted
	 */
	public static void persistTranslation(IResource resource) {
		if(ACTIVATED) {
			IPath path = resource.getFullPath();
			String filePath = getPersistedTranslatorFilePath(path.toString());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	
			JSPTranslator translator = getJSPTranslator(file);
			if(translator != null) {
				persistTranslator(translator, filePath);
			}
		}
	}
	
	/**
	 * @param resource JSP resource who's translation should no longer be persisted
	 */
	public static void removePersistedTranslation(IResource resource) {
		if(ACTIVATED) {
			File file = getPersistedFile(resource.getFullPath());
			file.delete();
		}
	}
	
	/**
	 * @param resource JSP resource that has moved and thus its persisted translation should be updated
	 * @param fromPath Path the JSP resource moved from
	 */
	public static void movePersistedTranslation(IResource resource, IPath fromPath) {
		if(ACTIVATED) {
			File from = getPersistedFile(fromPath);
			File to = getPersistedFile(resource.getFullPath());
	
			from.renameTo(to);
		}
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
	private static String getPersistedTranslatorFilePath(String jspFilePath) {
		String persistedTranslatorFileName = "error.translator"; //$NON-NLS-1$
		synchronized(CHECKSUM_CALC_LOCK){
			try {
				CHECKSUM_CALC.reset();
				CHECKSUM_CALC.update(jspFilePath.getBytes("utf16")); //$NON-NLS-1$
				persistedTranslatorFileName = Long.toString(CHECKSUM_CALC.getValue()) + ".translator"; //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				Logger.logException("Could not get utf16 encoded bytes to create checksum to store persisted file.", e); //$NON-NLS-1$
			}
		}
		
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
	 * <p>Gets the associated {@link JSPTranslator} for a specific JSP file.</p>
	 * <p><b>NOTE: </b><i>This does not get the persisted translator but rather the
	 * associated translator in memory</i></p>
	 * 
	 * @param jspFile {@link IFile} to the JSP file that the associated {@link JSPTranslator}
	 * is needed for
	 * @return {@link JSPTranslator} associated with the given <code>jspFilePath</code>, or
	 * <code>null</code> if none can be found.
	 */
	private static JSPTranslator getJSPTranslator(IFile jspFile) {
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
			Logger.logException("Could not get translator for " + jspFile.getName() + //$NON-NLS-1$
					" because could not read model for same.", e); //$NON-NLS-1$
		} catch (CoreException e) {
			Logger.logException("Could not get translator for " + jspFile.getName() + //$NON-NLS-1$
					" because could not read model for same.", e); //$NON-NLS-1$
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
	private static void persistTranslator(JSPTranslator translator, String filePath) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(translator);
			out.close();
		} catch (IOException e) {
			Logger.logException("Was unable to externalize JSPTranslator " + translator + //$NON-NLS-1$
					" to " + filePath, e); //$NON-NLS-1$
		}
	}

	/**
	 * @param path {@link IPath} to the JSP file that the persisted translator is needed for
	 * @return The persisted translator {@link File} for the JSP file at the given path
	 * or <code>null</code> if no persisted translator exists for the JSP file at the given path
	 */
	private static File getPersistedFile(IPath path) {
		return new File(getPersistedTranslatorFilePath(path.toString()));
	}
}