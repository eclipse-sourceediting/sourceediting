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
package org.eclipse.wst.css.core.internal.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.UnsupportedCharsetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.util.PathHelper;
import org.eclipse.wst.sse.core.internal.util.ProjectResolver;
import org.eclipse.wst.sse.core.internal.util.URIResolver;

/**
 */
public class URLModelProviderCSS {

	private static final int GET_MODEL_FOR_READ = 1;
	// private static final int GET_NEW_MODEL_FOR_READ = 2;
	private static final int GET_MODEL_FOR_EDIT = 3;
	// private static final int GET_NEW_MODEL_FOR_EDIT = 4;
	// private static final int READ_BUFFER_SIZE = 4096;
	// IModelManager
	private IModelManager modelManager = null;

	/**
	 */
	public URLModelProviderCSS() {
		super();

		// obtain model manager
		modelManager = StructuredModelManager.getModelManager();
	}

	/**
	 * Calculate ID from a filename. This must be same as
	 * FileModelProvider.calculateId(IFile)
	 */
	private static String calculateId(IPath fullIPath) {
		return fullIPath.toString();
	}

	/**
	 * <code>baseModel</code>: the model containing the link
	 * <code>ref</code>: the link URL string
	 */
	private IStructuredModel getCommonModelFor(final IStructuredModel baseModel, final String ref, final int which) throws IOException {
		// first, create absolute url
		String absURL = resolveURI(baseModel, ref, true);
		if ((absURL == null) || (absURL.length() == 0)) {
			return null;
		}

		// need to remove file:// scheme if necessary
		try {
			final URL aURL = new URL(absURL);
			// An actual URL was given, only file:/// is supported
			// resolve it by finding the file it points to
			if (!aURL.getProtocol().equals("platform")) { //$NON-NLS-1$
				if (aURL.getProtocol().equals("file") && (aURL.getHost().equals("localhost") || aURL.getHost().length() == 0)) { //$NON-NLS-2$//$NON-NLS-1$
					absURL = aURL.getFile();
					final IPath ipath = new Path(absURL);
					// if path has a device, and if it begins with
					// IPath.SEPARATOR, remove it
					final String device = ipath.getDevice();
					if ((device != null) && (device.length() > 0)) {
						if (device.charAt(0) == IPath.SEPARATOR) {
							final String newDevice = device.substring(1);
							absURL = ipath.setDevice(newDevice).toString();
						}
					}

				}
			}
		}
		catch (MalformedURLException mfuExc) {
		}

		// next, decide project
		IProject project = null;
		final IPath fullIPath = new Path(absURL);
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IContainer container = workspace.getContainerForLocation(fullIPath);
		if (container != null) {
			// fullIPath doesn't exist in workspace
			project = container.getProject();
		}

		// If HTML document has a link to an extern CSS which is not in
		// IProject
		// workspace.getContainerForLoation() may return null. We need to take
		// care
		// of this case

		// now, get absURL's IFile
		if ((project != null) && (project.getLocation().isPrefixOf(fullIPath) == false)) {
			// it's at outside of Project
			return null;
		}

		IStructuredModel model = null;
		if (project != null) {
			IPath filePath = fullIPath.removeFirstSegments(project.getLocation().segmentCount());
			IFile file = (filePath != null && !filePath.isEmpty()) ? project.getFile(filePath) : null;
			if (file == null) {
				return null;
			}

			// obtain model
			if (which == GET_MODEL_FOR_EDIT) {
				model = getModelForEdit(file);
			}
			else if (which == GET_MODEL_FOR_READ) {
				model = getModelForRead(file);
			}

			// setting synchronization stamp is IModelManager's client's
			// responsibility
			if (model != null && model.getSynchronizationStamp() == IResource.NULL_STAMP)
				model.resetSynchronizationStamp(file);
		}
		else {
			String id = null;
			InputStream inStream = null;
			// obtain resolver
			URIResolver resolver = (project != null) ? (URIResolver) project.getAdapter(URIResolver.class) : null;
			if (resolver == null) {
				// ProjectResolver can take care of the case if project is
				// null.
				resolver = new ProjectResolver(project);
			}
			if (resolver == null) {
				return null;
			}

			// there is no project. we can't expect IProject help to create
			// id/inputStream
			File file = fullIPath.toFile();

			// obatin id
			id = calculateId(fullIPath);

			// obtain InputStream
			try {
				inStream = new FileInputStream(file);
			}
			catch (FileNotFoundException fnfe) {
				// the file does not exist, or we don't have read permission
				return null;
			}

			// obtain model
			try {
				if (which == GET_MODEL_FOR_EDIT) {
					model = getModelManager().getModelForEdit(id, inStream, resolver);
				}
				else if (which == GET_MODEL_FOR_READ) {
					model = getModelManager().getModelForRead(id, inStream, resolver);
				}
			}
			catch (UnsupportedEncodingException ue) {
			}
			catch (IOException ioe) {
			}
			finally {
				// close now !
				if (inStream != null) {
					inStream.close();
				}
			}
		}

		// set locationid
		if (model != null && model.getBaseLocation() == null) {
			model.setBaseLocation(fullIPath.toString());
		}

		return model;
	}

	/**
	 * <code>baseModel</code>: the model containing the link
	 * <code>ref</code>: the link URL string
	 */
	public IStructuredModel getModelForEdit(IStructuredModel baseModel, String ref) throws IOException {
		return getCommonModelFor(baseModel, ref, GET_MODEL_FOR_EDIT);
	}

	/**
	 */
	private IStructuredModel getModelForEdit(IFile file) throws IOException {
		if (file == null)
			return null;
		IModelManager manager = getModelManager();

		// create a fake InputStream
		IStructuredModel model = null;
		try {
			model = manager.getModelForEdit(file);
		}
		catch (UnsupportedCharsetException ex) {
			try {
				model = manager.getModelForEdit(file, EncodingRule.FORCE_DEFAULT);
			}
			catch (IOException ioe) {
			}
			catch (CoreException ce) {
			}
		}
		catch (CoreException ce) {
		}
		return model;
	}

	/**
	 * <code>baseModel</code>: the model containing the link
	 * <code>ref</code>: the link URL string
	 */
	public IStructuredModel getModelForRead(IStructuredModel baseModel, String ref) throws UnsupportedEncodingException, IOException {
		return getCommonModelFor(baseModel, ref, GET_MODEL_FOR_READ);
	}

	/**
	 */
	private IStructuredModel getModelForRead(IFile file) throws IOException {
		if (file == null)
			return null;
		IModelManager manager = getModelManager();

		// create a fake InputStream
		IStructuredModel model = null;
		try {
			model = manager.getModelForRead(file);
		}
		catch (UnsupportedCharsetException ex) {
			try {
				model = manager.getModelForRead(file, EncodingRule.FORCE_DEFAULT);
			}
			catch (IOException ioe) {
			}
			catch (CoreException ce) {
			}
		}
		catch (CoreException ce) {
		}
		return model;
	}

	/**
	 */
	private IModelManager getModelManager() {
		return modelManager;
	}

	public IStructuredModel getNewModelForEdit(IFile iFile) {
		if (iFile == null)
			return null;
		IModelManager manager = getModelManager();
		if (manager == null)
			return null;

		IStructuredModel model = null;
		try {
			model = manager.getNewModelForEdit(iFile, false);
		}
		catch (IOException ex) {
		}
		catch (ResourceInUse riu) {
		}
		catch (ResourceAlreadyExists rae) {
		}
		catch (CoreException ce) {
		}
		return model;
	}

	public IStructuredModel getNewModelForRead(IFile iFile) {
		if (iFile == null)
			return null;
		IModelManager manager = getModelManager();
		if (manager == null)
			return null;

		IStructuredModel model = null;
		try {
			model = manager.getNewModelForEdit(iFile, false);
		}
		catch (IOException ex) {
		}
		catch (ResourceInUse riu) {
		}
		catch (ResourceAlreadyExists rae) {
		}
		catch (CoreException ce) {
		}
		return model;
	}

	/**
	 * <code>baseModel</code>: the model containing the link
	 * <code>ref</code>: the link URL string
	 * <code>resolveCrossProjectLinks</code>: If resolveCrossProjectLinks
	 * is set to true, then this method will properly resolve the URI if it is
	 * a valid URI pointing to another (appropriate) project.
	 */
	public static String resolveURI(IStructuredModel baseModel, String ref, boolean resolveCrossProjectLinks) {
		if (baseModel == null)
			return null;

		// get resolver in Model
		final URIResolver resolver = baseModel.getResolver();

		// resolve to absolute url
		final String absurl = (resolver != null) ? resolver.getLocationByURI(ref, resolveCrossProjectLinks) : null;
		if ((resolver != null) && (absurl == null) && (ref != null) && (ref.trim().length() > 0) && (ref.trim().charAt(0) == '/')) {
			// to reach here means :
			// ref is a Docroot relative
			// resolver can't resolve ref
			// so that href is a broken and should not create model
			return null;
		}
		if ((absurl != null) && (absurl.length() > 0)) {
			return absurl;
		}

		// maybe ref is at outside of the Project
		// obtain docroot;
		final IContainer container = (resolver != null) ? resolver.getRootLocation() : null;
		String docroot = null;
		if (container != null) {
			docroot = container.getLocation().toString();
		}
		if (docroot == null) {
			docroot = baseModel.getBaseLocation();
		}
		if (docroot == null) {
			// should not be
			return null;
		}

		// obtain document url
		String modelBaseLocation = baseModel.getBaseLocation();
		if ((modelBaseLocation == null) || (modelBaseLocation.length() == 0)) {
			// fallback...
			modelBaseLocation = baseModel.getId();
		}
		if ((modelBaseLocation == null) || (modelBaseLocation.length() == 0)) {
			// i can't resolve uri !
			return null;
		}

		// resolve url
		URLHelper helper = new URLHelper(PathHelper.getContainingFolderPath(modelBaseLocation), PathHelper.getContainingFolderPath(PathHelper.appendTrailingURLSlash(docroot)));
		return helper.toAbsolute(ref);
	}

}
