/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.dom;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.util.NullInputStream;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Tests performance minimization of adapter notifications for RegionChanged
 * events, which would affect areas like formatting performance
 */
public class RegionChangedAdapterNotificationTests extends TestCase {
	public RegionChangedAdapterNotificationTests() {
		this("RegionChanged Adapter Notification tests");
	}

	/**
	 * Constructor for UpdaterTestRegionChanged.
	 * 
	 * @param name
	 */
	public RegionChangedAdapterNotificationTests(String name) {
		super(name);
	}

	public void testAppendWhitespaceToAttributeName() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 4, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));
			
			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b = c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAppendWhitespaceToEqualSign() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b= c></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 5, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b=  c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testAppendWhitespaceToTagName() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
			((INodeNotifier) document).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 2, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a  b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}
	
	public void testAppendWhitespaceToAttributeValue() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b= c></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 7, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b= c ></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}
	
	public void testChangeAttributeName() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1, -1};
			INodeAdapter adapter = new INodeAdapter() {
				private int eCount = 0;
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					assertTrue("Received more than 2 events.", eCount < 2);
					changed[eCount++] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 4, 0, "d");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			assertEquals("Property Removed Adapter Notification event not sent " + changed[0] + " " + structuredDocument.get(), INodeNotifier.REMOVE, changed[0]);
			assertEquals("Property Added Adapter Notification event not sent " + changed[1] + " " + structuredDocument.get(), INodeNotifier.ADD, changed[1]);

			assertEquals("unexpected document content", "<a bd= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}
	public void testChangeAttributeValue() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 6, 0, "d");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("Property Changed Adapter Notification event not sent " + changed[0] + " " +structuredDocument.get(), INodeNotifier.CHANGE, changed[0]);

			assertEquals("unexpected document content", "<a b= dc></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}
	public void testChangeTagName1() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) document).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 1, 0, "d");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertNotSame("DOM Node not replaced", before, after);
			
			assertEquals("Structure Changed notification not sent to adapter " + changed[0] + " to parent " +structuredDocument.get(), INodeNotifier.STRUCTURE_CHANGED, changed[0]);

			assertEquals("unexpected document content", "<da b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testChangeTagName2() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) document).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 2, 0, "d");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertNotSame("DOM Node not replaced", before, after);
			
			assertEquals("Structure Changed notification not sent to adapter " + changed[0] + " to parent " +structuredDocument.get(), INodeNotifier.STRUCTURE_CHANGED, changed[0]);

			assertEquals("unexpected document content", "<ad b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testChangeQuotedAttributeValue() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b= \"c\"></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 7, 0, "d");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("Property Changed Adapter Notification event not sent " + changed[0] + " " +structuredDocument.get(), INodeNotifier.CHANGE, changed[0]);

			assertEquals("unexpected document content", "<a b= \"dc\"></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testPrependSpaceToAttributeName() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
	
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 3, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a  b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testPrependSpaceToEqualSign() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
	
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 4, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b = c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testPrependSpaceToAttributeValue() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);

		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b= c></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 6, 0, " ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b=  c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testRemoveTrailingSpaceFromEqualSign() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b= c></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 5, 1, "");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b=c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}
	public void testRemoveTrailingSpaceFromAttributeName() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b = c></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 4, 1, "");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testRemoveTrailingSpaceFromAttributeValue() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
		
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b = c ></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 8, 1, "");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b = c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testRemoveTrailingSpaceFromTagName() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
	
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a  b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
			((INodeNotifier) document).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 2, 1, "");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testRemoveTrailingSpaceFromTagName2() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);
	
		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();
	
			structuredDocument.setText(this, "<a  b= c></a>");
	
			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}
	
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);
			((INodeNotifier) document).addAdapter(adapter);
	
			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 3, 1, "");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);
	
			Node after = document.getFirstChild();
	
			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b= c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}

	public void testReplaceTrailingSpaceofEqualSignWithTwoSpaces() throws IOException {
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(getName() + ".xml", new NullInputStream(), null);

		try {
			Document document = model.getDocument();
			IStructuredDocument structuredDocument = model.getStructuredDocument();

			structuredDocument.setText(this, "<a b= c></a>");

			Node before = document.getFirstChild();
			final int[] changed = new int[]{-1};
			INodeAdapter adapter = new INodeAdapter() {
				public boolean isAdapterForType(Object type) {
					return type.equals(RegionChangedAdapterNotificationTests.class);
				}

				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					changed[0] = eventType;
				}
			};
			((INodeNotifier) before).addAdapter(adapter);

			Object[] originalRegions = structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray();
			StructuredDocumentEvent fmEvent = structuredDocument.replaceText(null, 5, 0, "  ");
			assertTrue("Region instances changed", Arrays.equals(originalRegions, structuredDocument.getFirstStructuredDocumentRegion().getRegions().toArray()));

			assertTrue(fmEvent instanceof RegionChangedEvent);

			Node after = document.getFirstChild();

			assertEquals("Node replaced", before, after);
			
			assertEquals("unexpected adapter notification event sent " + structuredDocument.get(), -1, changed[0]);

			assertEquals("unexpected document content", "<a b=   c></a>", structuredDocument.get());
		}
		finally {
			model.releaseFromEdit();
		}
	}
}
