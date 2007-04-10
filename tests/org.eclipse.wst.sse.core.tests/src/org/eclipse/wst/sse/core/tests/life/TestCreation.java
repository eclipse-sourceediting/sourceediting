/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.life;

import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

import junit.framework.TestCase;




public class TestCreation extends TestCase {
	private boolean DEBUG = false;

	public void testCreation() {
		IStructuredModel structuredModel = new EmptyModelForTests();
		ModelLifecycleEvent event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_RELEASED);
		if (DEBUG) {
			System.out.println(event);
		}
		event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_DIRTY_STATE);
		if (DEBUG) {
			System.out.println(event);
		}
		event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED);
		if (DEBUG) {
			System.out.println(event);
		}
		/*        event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_REINITIALIZED);
		 if (DEBUG) {
		 System.out.println(event);
		 }
		 event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.ADAPTERS_NOTIFIED);
		 if (DEBUG) {
		 System.out.println(event);
		 }
		 event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_RELOADED);
		 if (DEBUG) {
		 System.out.println(event);
		 }
		 event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_RESOURCE_DELETED);
		 if (DEBUG) {
		 System.out.println(event);
		 }
		 event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_RESOURCE_MOVED);
		 if (DEBUG) {
		 System.out.println(event);
		 }
		 */
		event = new ModelLifecycleEvent(structuredModel, ModelLifecycleEvent.MODEL_SAVED);
		if (DEBUG) {
			System.out.println(event);
		}


		// if gets to here without exception, assume ok.
		assertTrue(true);


	}
}
