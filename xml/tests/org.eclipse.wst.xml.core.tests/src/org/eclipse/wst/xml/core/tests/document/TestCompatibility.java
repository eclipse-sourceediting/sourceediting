/*******************************************************************************
 * Copyright (c) 2023 Nitin Dahyabhai
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Nitin Dahyabhai - initial implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.events.AboutToBeChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

import junit.framework.TestCase;

public class TestCompatibility extends TestCase {

	public TestCompatibility() {
	}

	public TestCompatibility(String name) {
		super(name);
	}

	private String loadStringFromResource(String name) {
		try (InputStream stream = getClass().getResourceAsStream(name)) {
			InputStreamReader inputStreamReader = new InputStreamReader(stream, "utf8");
			StringBuilder builder = new StringBuilder();
			int charsRead;
			char[] chars = new char[1024];
			while ((charsRead = inputStreamReader.read(chars)) != -1) {
				builder.append(chars, 0, charsRead);
			}
			String s = builder.toString();
			s = s.replaceAll("\r\n", "\n");
			s = s.replaceAll("\r", "\n");
			return s;
		}
		catch (IOException e) {
			return "Unable to read resource " + e;
		}
	}

	private void testImplementation(IStructuredDocument document, String referenceResourceName) throws BadLocationException {
		StringBuilder testBuffer = new StringBuilder();
		document.addDocumentAboutToChangeListener(new IModelAboutToBeChangedListener() {
			@Override
			public void modelAboutToBeChanged(AboutToBeChangedEvent e) {
				testBuffer.append("BEGIN DocumentAboutToChange\n");
				testBuffer.append(e);
				testBuffer.append("\n");
				testBuffer.append("END DocumentAboutToChange\n");
			}
		});
		document.addDocumentChangingListener(new IStructuredDocumentListener() {
			private void append(StructuredDocumentEvent e) {
				testBuffer.append("BEGIN DocumentChanging\n");
				testBuffer.append(e);
				testBuffer.append("\n");
				testBuffer.append("END   DocumentChanging\n");
			}

			@Override
			public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void noChange(NoChangeEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void newModel(NewDocumentEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}
		});
		document.addDocumentChangedListener(new IStructuredDocumentListener() {
			private void append(StructuredDocumentEvent e) {
				testBuffer.append("BEGIN DocumentChanged\n");
				testBuffer.append(e);
				testBuffer.append("\n");
				testBuffer.append("END   DocumentChanged\n");
			}

			@Override
			public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void noChange(NoChangeEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}

			@Override
			public void newModel(NewDocumentEvent structuredDocumentEvent) {
				append(structuredDocumentEvent);
			}
		});
		document.addPrenotifiedDocumentListener(new IDocumentListener() {
			private void append(DocumentEvent e) {
				testBuffer.append("BEGIN PrenotifiedDocumentListener\n");
				testBuffer.append(e);
				testBuffer.append("\n");
				testBuffer.append("END   PrenotifiedDocumentListener\n");
			}

			@Override
			public void documentChanged(DocumentEvent event) {
				append(event);
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				append(event);
			}
		});
		document.addDocumentListener(new IDocumentListener() {
			private void append(DocumentEvent e) {
				testBuffer.append("BEGIN DocumentListener\n");
				testBuffer.append(e);
				testBuffer.append("\n");
				testBuffer.append("END   DocumentListener\n");
			}

			@Override
			public void documentChanged(DocumentEvent event) {
				append(event);
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				append(event);
			}
		});
		testBuffer.append("BEGIN Setting Content\n");
		document.set("<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\" version=\"2.0\">");
		testBuffer.append("END   Setting Content\n");
		int i = document.get().indexOf("<rss");
		testBuffer.append("BEGIN Change Content\n");
		document.replace(i, 0, "\n");
		testBuffer.append("END   Change Content\n");
		i = document.get().indexOf("<rss ");
		testBuffer.append("BEGIN NoChangeContent\n");
		document.replace(i + 4, 1, " ");
		testBuffer.append("END   NoChangeContent\n");
		testBuffer.append("BEGIN Append\n");
		document.replace(document.getLength() - 1, 0, "\n</rss>");
		testBuffer.append("END   Append\n");

		assertEquals(loadStringFromResource(referenceResourceName).trim(), testBuffer.toString().trim());
	}

	/**
	 * Check the ordering of events sent from a BasicStructuredDocument for
	 * XML against a reference
	 * 
	 * @throws BadLocationException
	 */
	public void testBasicEventOrdering() throws BadLocationException {
		IModelHandler modelHandler = ModelHandlerRegistry.getInstance().getHandlerForContentTypeId("org.eclipse.core.runtime.xml");
		IModelLoader modelLoader = modelHandler.getModelLoader();
		IStructuredModel model = modelLoader.createModel();
		IStructuredDocument document = model.getStructuredDocument();
		testImplementation(document, "BasicEventOrdering1.txt");
	}
}
