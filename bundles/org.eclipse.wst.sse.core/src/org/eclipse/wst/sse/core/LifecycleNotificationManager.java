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
package org.eclipse.wst.sse.core;

import org.eclipse.wst.sse.core.util.Utilities;

/**
 * For "internal use" only, by AbstractStructuredModel
 */

class LifecycleNotificationManager {

	private Object[] fListeners;

	LifecycleNotificationManager() {

		super();
	}

	synchronized void addListener(IModelLifecycleListener listener) {

		if (!Utilities.contains(fListeners, listener)) {
			int oldSize = 0;
			if (fListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fListeners.length;
			}
			int newSize = oldSize + 1;
			Object[] newListeners = new Object[newSize];
			if (fListeners != null) {
				System.arraycopy(fListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fListeners = newListeners;
		}
	}

	synchronized void removeListener(IModelLifecycleListener listener) {

		if ((fListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fListeners, listener)) {
				int oldSize = fListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fListeners = newListeners;
			}
		}
	}

	void signalLifecycleEvent(ModelLifecycleEvent event) {

		// we must assign listeners to local variable, since the add and remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (fListeners != null) {
			Object[] holdListeners = fListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				IModelLifecycleListener listener = (IModelLifecycleListener) holdListeners[i];
				// only one type of listner for now ... this could become more complex
				if ((event.getInternalType() & ModelLifecycleEvent.PRE_EVENT) == ModelLifecycleEvent.PRE_EVENT) {

					listener.processPreModelEvent(event);
				}
				if ((event.getInternalType() & ModelLifecycleEvent.POST_EVENT) == ModelLifecycleEvent.POST_EVENT) {
					listener.processPostModelEvent(event);
				}
			}
		}
	}
}
