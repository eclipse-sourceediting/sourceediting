/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 245772 - NLS Message refactoring
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.breakpoint;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.sse.ui.internal.StructuredResourceMarkerAnnotationModel;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.model.XSLLineBreakpoint;

/**
 * An SSE <code>IBreakpointProvider</code> for XSL files.
 * 
 * @author Doug Satchwell
 */
public class XSLBreakpointProvider implements IBreakpointProvider,
		IExecutableExtension {
	public IStatus addBreakpoint(IDocument document, IEditorInput input,
			int editorLineNumber, int offset) throws CoreException {
		// check if there is a valid position to set breakpoint
		int pos = getValidPosition(document, editorLineNumber);
		IStatus status = null;
		if (pos >= 0) {
			IResource res = getResourceFromInput(input);
			if (res != null) {
				boolean add = true;
				if (add) {
					IBreakpoint point = new XSLLineBreakpoint(res,
							editorLineNumber, pos, pos);
					if (point == null) {
						status = new Status(IStatus.ERROR,
								XSLDebugUIPlugin.PLUGIN_ID, IStatus.ERROR,
								Messages.XSLBreakpointProvider_0, null);
					} else {
						// DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(point);
					}
				}
			} else if (input instanceof IStorageEditorInput) {
				res = ResourcesPlugin.getWorkspace().getRoot();
				String id = input.getName();
				if (input instanceof IStorageEditorInput
						&& ((IStorageEditorInput) input).getStorage() != null
						&& ((IStorageEditorInput) input).getStorage()
								.getFullPath() != null) {
					id = ((IStorageEditorInput) input).getStorage()
							.getFullPath().toString();
				}
				Map<String, String> attributes = new HashMap<String, String>();
				attributes
						.put(
								StructuredResourceMarkerAnnotationModel.SECONDARY_ID_KEY,
								id);
				IBreakpoint point = new XSLLineBreakpoint(res,
						editorLineNumber, pos, pos);
				if (point == null) {
					status = new Status(IStatus.ERROR,
							XSLDebugUIPlugin.PLUGIN_ID, IStatus.ERROR,
							Messages.XSLBreakpointProvider_0, null);
				}
			}
		}
		if (status == null) {
			status = new Status(IStatus.OK, XSLDebugUIPlugin.PLUGIN_ID,
					IStatus.OK, "JSPUIMessages.OK", null); //$NON-NLS-1$
		}
		return status;
	}

	public IResource getResource(IEditorInput input) {
		return getResourceFromInput(input);
	}

	private IResource getResourceFromInput(IEditorInput input) {
		IResource resource = (IResource) input.getAdapter(IFile.class);
		if (resource == null) {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		return resource;
	}

	private int getValidPosition(IDocument idoc, int editorLineNumber) {
		int result = -1;
		if (idoc != null) {
			int startOffset = 0;
			int endOffset = 0;
			try {
				IRegion line = idoc.getLineInformation(editorLineNumber - 1);
				startOffset = line.getOffset();
				endOffset = Math.max(line.getOffset(), line.getOffset()
						+ line.getLength());

				String lineText = idoc
						.get(startOffset, endOffset - startOffset).trim();

				// blank lines or PI's cannot have breakpoints
				if (lineText.trim().equals("") || lineText.startsWith("<?")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					result = -1;
				} else {
					// get all partitions for current line
					ITypedRegion[] partitions = null;

					partitions = idoc.computePartitioning(startOffset,
							endOffset - startOffset);

					for (int i = 0; i < partitions.length; ++i) {
						// String type = partitions[i].getType();
						result = partitions[i].getOffset();
					}
				}
			} catch (BadLocationException e) {
				result = -1;
			}
		}
		return result;
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		// not used
	}

	public void setSourceEditingTextTools(ISourceEditingTextTools tools) {
		// not used
	}
}
