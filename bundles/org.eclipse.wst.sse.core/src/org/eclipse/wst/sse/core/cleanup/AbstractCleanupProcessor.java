/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.cleanup;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.Logger;


/**
 * @deprecated renamed to AbstractStructuredCleanupProcessor
 *
 * TODO will delete in C5
 */
abstract public class AbstractCleanupProcessor implements CleanupProcessor {
	public String cleanupContent(String input) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		try {
			// setup structuredModel
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8")); //$NON-NLS-1$
			String id = inputStream.toString() + getContentType();
			structuredModel = getModelManager().getModelForRead(id, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, 0, structuredModel.getStructuredDocument().getLength());

			// return output
			return structuredModel.getStructuredDocument().get();
		}
		catch (UnsupportedEncodingException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (IOException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		finally {
			ensureClosed(null, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public String cleanupContent(String input, int start, int length) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		try {
			// setup structuredModel
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8")); //$NON-NLS-1$
			String id = inputStream.toString() + getContentType();
			structuredModel = getModelManager().getModelForRead(id, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, start, length);

			// return output
			return structuredModel.getStructuredDocument().get();
		}
		catch (UnsupportedEncodingException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (IOException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		finally {
			ensureClosed(null, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFile(IFile file) {
		IStructuredModel structuredModel = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			structuredModel = getModelManager().getModelForRead(file);

			// cleanup
			cleanupModel(structuredModel, 0, structuredModel.getStructuredDocument().getLength());

			// save output to file
			//outputStream = new
			// FileOutputStream(file.getLocation().toString());
			structuredModel.save(file);
		}
		catch (CoreException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (IOException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		finally {
			//ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFile(IFile file, int start, int length) {
		IStructuredModel structuredModel = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			structuredModel = getModelManager().getModelForRead(file);

			// cleanup
			cleanupModel(structuredModel, start, length);

			// save output to file
			//outputStream = new
			// FileOutputStream(file.getLocation().toString());
			structuredModel.save(file);
		}
		catch (CoreException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (IOException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		finally {
			//ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFileName(String fileName) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			inputStream = new FileInputStream(fileName);
			structuredModel = getModelManager().getModelForRead(fileName, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, 0, structuredModel.getStructuredDocument().getLength());

			// save output to file
			//outputStream = new FileOutputStream(fileName);
			structuredModel.save();
		}
		//TODO I don't think we should be turning any of these into runtime
		// exceptions
		catch (CoreException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (FileNotFoundException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (UnsupportedEncodingException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (IOException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		finally {
			//ensureClosed(outputStream, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFileName(String fileName, int start, int length) {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			inputStream = new FileInputStream(fileName);
			structuredModel = getModelManager().getModelForRead(fileName, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, start, length);

			// save output to file
			//outputStream = new FileOutputStream(fileName);
			structuredModel.save();
		}
		//TODO I don't think we should be turning any of these into runtime
		// exceptions

		catch (CoreException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (FileNotFoundException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (UnsupportedEncodingException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		catch (IOException exception) {
			throw new SourceEditingRuntimeException(exception);
		}
		finally {
			//ensureClosed(outputStream, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	abstract public void cleanupModel(IStructuredModel structuredModel);

	abstract public void cleanupModel(IStructuredModel structuredModel, int start, int length);

	protected void ensureClosed(OutputStream outputStream, InputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		catch (IOException e) {
			Logger.logException(e); // hopeless
		}
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		catch (IOException e) {
			Logger.logException(e); // hopeless
		}
	}

	private IModelManager getModelManager() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}

	/**
	 * The content type string is case insensitive. It may be "XML" for XML,
	 * "CSS" for CSS, and "HTML", "JSP", "JHTML", "SHTML", "SHTM", "HTML-SS",
	 * "XHTML", "HTM" for HTML.
	 * <p>
	 * This is needed for creating a temporary model when the
	 * cleanupContent(String input) form of CleanupProcessor is called.
	 */
	protected abstract String getContentType();

}
