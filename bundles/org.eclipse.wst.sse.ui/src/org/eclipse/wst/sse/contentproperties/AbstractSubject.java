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
package org.eclipse.wst.sse.contentproperties;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSubject implements INotify, ISubject {



	private static Map listenerList = new Hashtable();

	/*
	 * @see IContentSettingsEventSubject#notifyContentSettingsListeners(Object)
	 */
	public synchronized void notifyListeners(org.eclipse.core.resources.IResource changedResource) {

		Set keys = listenerList.keySet();
		Iterator iter = keys.iterator();

		while (iter.hasNext()) {
			IContentSettingsListener csl = (IContentSettingsListener) iter.next();
			csl.contentSettingsChanged(changedResource);
		}
	}

	public synchronized void addListener(IContentSettingsListener listener) {
		listenerList.put(listener, listener);
	}

	public synchronized void removeListener(IContentSettingsListener listener) {
		listenerList.remove(listener);
	}



}
