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
package org.eclipse.wst.sse.core.format;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


public abstract class AbstractStructuredFormatProcessor implements IStructuredFormatProcessor {
	protected IStructuredFormatContraints fFormatContraints = null;
	protected IProgressMonitor fProgressMonitor = null;
	public boolean refreshFormatPreferences = true; // special flag for JUnit tests to skip refresh of format preferences when it's set to false

	private IModelManager getModelManager() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}

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

	public String formatContent(String input) throws IOException, CoreException {
		if (input == null)
			return input;

		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for read. Will return formatted
			// string and NOT save model.
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8")); //$NON-NLS-1$
			String id = inputStream.toString() + "." + getFileExtension(); //$NON-NLS-1$
			structuredModel = getModelManager().getModelForRead(id, inputStream, null);

			// format
			formatModel(structuredModel);

			// return output
			return structuredModel.getStructuredDocument().get();
		}
		finally {
			ensureClosed(null, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public String formatContent(String input, int start, int length) throws IOException, CoreException {
		if (input == null)
			return input;

		if (start >= 0 && length >= 0 && start + length <= input.length()) {
			IStructuredModel structuredModel = null;
			InputStream inputStream = null;
			try {
				// setup structuredModel
				// Note: We are getting model for read. Will return formatted
				// string and NOT save model.
				inputStream = new ByteArrayInputStream(input.getBytes("UTF8")); //$NON-NLS-1$
				String id = inputStream.toString() + "." + getFileExtension(); //$NON-NLS-1$
				structuredModel = getModelManager().getModelForRead(id, inputStream, null);

				// format
				formatModel(structuredModel, start, length);

				// return output
				return structuredModel.getStructuredDocument().get();
			}
			finally {
				ensureClosed(null, inputStream);
				// release from model manager
				if (structuredModel != null)
					structuredModel.releaseFromRead();
			}
		}
		else
			return input;
	}

	public void formatDocument(IDocument document) throws IOException, CoreException {
		if (document == null)
			return;

		IStructuredModel structuredModel = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			structuredModel = getModelManager().getExistingModelForEdit(document);

			// format
			formatModel(structuredModel);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		}
		finally {
			//ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromEdit();
		}
	}

	public void formatDocument(IDocument document, int start, int length) throws IOException, CoreException {
		if (document == null)
			return;

		if (start >= 0 && length >= 0 && start + length <= document.getLength()) {
			IStructuredModel structuredModel = null;
			//OutputStream outputStream = null;
			try {
				// setup structuredModel
				// Note: We are getting model for edit. Will save model if
				// model changed.
				structuredModel = getModelManager().getExistingModelForEdit(document);

				// format
				formatModel(structuredModel, start, length);

				// save model if needed
				if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
					structuredModel.save();
			}
			finally {
				//ensureClosed(outputStream, null);
				// release from model manager
				if (structuredModel != null)
					structuredModel.releaseFromEdit();
			}
		}
	}

	public void formatFile(IFile file) throws IOException, CoreException {
		if (file == null)
			return;

		IStructuredModel structuredModel = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			structuredModel = getModelManager().getModelForEdit(file);

			// format
			formatModel(structuredModel);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		}
		finally {
			//ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromEdit();
		}
	}

	public void formatFile(IFile file, int start, int length) throws IOException, CoreException {
		if (file == null)
			return;

		IStructuredModel structuredModel = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			structuredModel = getModelManager().getModelForEdit(file);

			// format
			formatModel(structuredModel, start, length);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		}
		finally {
			//ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromEdit();
		}
	}

	public void formatFileName(String fileName) throws IOException, CoreException {
		if (fileName == null)
			return;

		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			inputStream = new FileInputStream(fileName);
			structuredModel = getModelManager().getModelForEdit(fileName, inputStream, null);

			// format
			formatModel(structuredModel);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		}
		finally {
			//ensureClosed(outputStream, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromEdit();
		}
	}

	public void formatFileName(String fileName, int start, int length) throws IOException, CoreException {
		if (fileName == null)
			return;

		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		//OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			inputStream = new FileInputStream(fileName);
			structuredModel = getModelManager().getModelForEdit(fileName, inputStream, null);

			// format
			formatModel(structuredModel, start, length);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		}
		finally {
			//ensureClosed(outputStream, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromEdit();
		}
	}

	public void formatModel(IStructuredModel structuredModel) {
		int start = 0;
		int length = structuredModel.getStructuredDocument().getLength();

		formatModel(structuredModel, start, length);
	}

	public void formatModel(IStructuredModel structuredModel, int start, int length) {
		if (structuredModel != null) {
			if (start == 0 && length == structuredModel.getStructuredDocument().getLength())
				setFormatWithSiblingIndent(structuredModel, false);
			else
				setFormatWithSiblingIndent(structuredModel, true);

			if (start >= 0 && length >= 0 && start + length <= structuredModel.getStructuredDocument().getLength()) {
				Vector activeNodes = getActiveNodes(structuredModel, start, length);
				if (activeNodes.size() > 0) {
					Node firstNode = (Node) activeNodes.firstElement();
					Node lastNode = (Node) activeNodes.lastElement();

					boolean done = false;
					Node eachNode = firstNode;
					Node nextNode = null;
					while (!done) {
						// update "done"
						done = (eachNode == lastNode);

						// get next sibling before format because eachNode may
						// be deleted,
						// for example when it's an empty text node
						nextNode = eachNode.getNextSibling();

						// format each node
						formatNode(eachNode);

						// update each node
						if (nextNode != null && nextNode.getParentNode() == null)
							// nextNode is deleted during format
							eachNode = eachNode.getNextSibling();
						else
							eachNode = nextNode;

						// This should not be needed, but just in case
						// something went wrong with with eachNode.
						// We don't want an infinite loop here.
						if (eachNode == null)
							done = true;
					}
				}
			}
		}
	}

	public void formatNode(Node node) {
		if (node != null) {
			Node newNode = node;

			// format the owner node if it's an attribute node
			if (node.getNodeType() == Node.ATTRIBUTE_NODE)
				newNode = ((Attr) node).getOwnerElement();

			// refresh format preferences before getting formatter
			if (refreshFormatPreferences)
				refreshFormatPreferences();

			// get formatter and format contraints
			IStructuredFormatter formatter = getFormatter(newNode);
			IStructuredFormatContraints formatContraints = formatter.getFormatContraints();
			formatContraints.setFormatWithSiblingIndent(true);

			if (formatter != null)
				// format each node
				formatter.format(newNode, formatContraints);
		}
	}

	protected void setFormatWithSiblingIndent(IStructuredModel structuredModel, boolean formatWithSiblingIndent) {
		// 262135 - NPE during format of empty document
		IStructuredFormatContraints formatContraints = getFormatContraints(structuredModel);

		if (formatContraints != null)
			formatContraints.setFormatWithSiblingIndent(formatWithSiblingIndent);
	}

	protected IStructuredFormatContraints getFormatContraints(IStructuredModel structuredModel) {
		// 262135 - NPE during format of empty document
		if (fFormatContraints == null && structuredModel != null) {
			Node node = (Node) structuredModel.getIndexedRegion(0);

			if (node != null) {
				IStructuredFormatter formatter = getFormatter(node);
				if (formatter != null) {
					fFormatContraints = formatter.getFormatContraints();
				}
			}
		}

		return fFormatContraints;
	}

	protected Vector getActiveNodes(IStructuredModel structuredModel, int startNodeOffset, int length) {
		Vector activeNodes = new Vector();

		if (structuredModel != null) {
			Node startNode = (Node) structuredModel.getIndexedRegion(startNodeOffset);
			Node endNode = (Node) structuredModel.getIndexedRegion(startNodeOffset + length);

			// make sure it's an non-empty document
			if (startNode != null) {
				while (isSiblingOf(startNode, endNode) == false) {
					if (endNode != null)
						endNode = endNode.getParentNode();
					if (endNode == null) {
						startNode = startNode.getParentNode();
						endNode = (Node) structuredModel.getIndexedRegion(startNodeOffset + length);
					}
				}

				while (startNode != endNode) {
					activeNodes.addElement(startNode);
					startNode = startNode.getNextSibling();
				}
				if (startNode != null)
					activeNodes.addElement(startNode);
			}
		}

		return activeNodes;
	}

	protected boolean isSiblingOf(Node node, Node endNode) {
		if (endNode == null) {
			return true;
		}
		else {
			Node siblingNode = node;
			while (siblingNode != null) {
				if (siblingNode == endNode)
					return true;
				else
					siblingNode = siblingNode.getNextSibling();
			}
			return false;
		}
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}

	/**
	 * @deprecated renamed to getFileExtension()
	 * TODO will delete in C5
	 */
	abstract protected String getContentType();

	abstract protected String getFileExtension();

	abstract protected IStructuredFormatter getFormatter(Node node);

	abstract protected void refreshFormatPreferences();
}
