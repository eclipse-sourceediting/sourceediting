/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.document;

import java.io.IOException;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;

/**
 * @author nsd
 * 
 */
public class TransitionTests extends UnzippedProjectTester {

	/**
	 * 
	 */
	public TransitionTests() {
		super();
	}


	public void testSoftRevert() throws CoreException, IOException {
		String filePath = "testfiles/xml/EmptyFile.xml"; //$NON-NLS-1$
		IFile file = (IFile) fTestProject.findMember(filePath);
		assertNotNull("Test Case in error. Could not find file " + filePath, file); //$NON-NLS-1$
		IPath locationPath = file.getLocation();
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		bufferManager.connect(locationPath, null);
		ITextFileBuffer buffer = bufferManager.getTextFileBuffer(locationPath);
		IDocument document = buffer.getDocument();
		assertNotNull(document);
		assertTrue("wrong class of document", document instanceof BasicStructuredDocument); //$NON-NLS-1$
		assertTrue("wrong partitioner in document.", ((IDocumentExtension3) document).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING) instanceof StructuredTextPartitionerForXML); //$NON-NLS-1$

		IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) document);
		try {
			try {
				document.replace(0, 0, "__"); //$NON-NLS-1$
				document.replace(2, 0, "<a"); //$NON-NLS-1$
				document.replace(4, 0, ">"); //$NON-NLS-1$
				document.replace(5, 0, "  "); //$NON-NLS-1$
			}
			catch (BadLocationException e) {
				assertNull(e);
			}
			document.set(""); //$NON-NLS-1$
		}
		finally {
			model.releaseFromEdit();

			bufferManager.disconnect(locationPath, null);
		}
	}
}
