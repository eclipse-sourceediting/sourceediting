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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Platform;


/**
 * AbstractNotifier is similar to (and based on) the EMF NotifierImpl
 * class. This class is simpler (that is, not as many functions).
 */
public abstract class AbstractNotifier implements INodeNotifier {

	private INodeAdapter[] fAdapters;
	private int adapterCount = 0;
	private final static int growthConstant = 3;
	private final static boolean debugAdapterNotificationTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/dom/adapter/notification/time")); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * AbstractNotifier constructor comment.
	 */
	public AbstractNotifier() {
		super();
	}

	/**
	 * addAdapter method comment.
	 */
	public synchronized void addAdapter(INodeAdapter adapter) {

		if (adapter == null)
			return;
		ensureCapacity(adapterCount + 1);
		fAdapters[adapterCount++] = adapter;
	}

	/**
	 * Default behavior for getting an adapter.
	 */
	public INodeAdapter getAdapterFor(Object type) {
		// first, we'll see if we already have one
		INodeAdapter result = getExistingAdapter(type);
		// if we didn't find one in our list already,
		// let's create it
		if (result == null) {
			IFactoryRegistry reg = getFactoryRegistry();
			if (reg != null) {
				AdapterFactory factory = reg.getFactoryFor(type);
				if (factory != null) {
					result = factory.adapt(this);
				}
			}
			// We won't prevent null from being returned, but it would be unusual.
			// It might be because Factory is not working correctly, or 
			// not installed, so we'll allow warning message.
			if ((result == null) && (org.eclipse.wst.sse.core.util.Debug.displayWarnings)) {
				System.out.println("Warning: no adapter was found or created for " + type); //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * Returns a shallow clone of list, since
	 * clients should not manipulate our list directly.
	 * Instead, they should use add/removeAdapter.
	 */
	public Collection getAdapters() {
		if (fAdapters != null) {
			if (adapterCount == 0) {
				fAdapters = null;
				return Collections.EMPTY_LIST;
			}
			else {
				// we need to make a new array, to be sure
				// it doesn't contain nulls at end, which may be 
				// present there for "growth". 
				INodeAdapter[] tempAdapters = new INodeAdapter[adapterCount];
				System.arraycopy(fAdapters, 0, tempAdapters, 0, adapterCount);
				// EMF uses the unmodifiableCollection. Its a bit of a performance
				// drain, but may want to leave in since
				// it would "fail fast" if someone was trying to modify the list.
				return Collections.unmodifiableCollection(Arrays.asList(tempAdapters));
				//return Arrays.asList(newAdapters);
			}
		}
		else
			return Collections.EMPTY_LIST;
	}

	public INodeAdapter getExistingAdapter(Object type) {
		INodeAdapter result = null;
		for (int i = 0; i < adapterCount; i++) {
			INodeAdapter a = fAdapters[i];
			if (a.isAdapterForType(type)) {
				result = a;
				break;
			}
		}
		// if we didn't find one in our list,
		// return the null result
		return result;
	}

	abstract public IFactoryRegistry getFactoryRegistry();

	public void notify(int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		if (fAdapters != null) {
			int localAdapterCount = 0;
			INodeAdapter[] localAdapters = null;

			// lock object while making local assignments
			synchronized (this) {
				localAdapterCount = adapterCount;
				localAdapters = new INodeAdapter[localAdapterCount];
				System.arraycopy(fAdapters, 0, localAdapters, 0, localAdapterCount);
			}

			for (int i = 0; i < localAdapterCount; i++) {
				INodeAdapter a = localAdapters[i];

				if (debugAdapterNotificationTime) {
					long getAdapterTimeCriteria = getAdapterTimeCriteria();
					long startTime = System.currentTimeMillis();
					// ** keep this line identical with non-debug version!!
					a.notifyChanged(this, eventType, changedFeature, oldValue, newValue, pos);
					long notifyDuration = System.currentTimeMillis() - startTime;
					if (getAdapterTimeCriteria >= 0 && notifyDuration > getAdapterTimeCriteria) {
						System.out.println("adapter notifyDuration: " + notifyDuration + "  class: " + a.getClass()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else {
					// ** keep this line identical with debug version!!
					a.notifyChanged(this, eventType, changedFeature, oldValue, newValue, pos);
				}

			}
		}
	}

	private long getAdapterTimeCriteria() {
		// to "re-get" the property each time is a little awkward, but we 
		// do it that way to avoid adding instance variable just for debugging. 
		// This method should only be called if debugAdapterNotifcationTime 
		// is true.
		final String criteriaStr = Platform.getDebugOption("org.eclipse.wst.sse.core/dom/adapter/notification/time/criteria"); //$NON-NLS-1$
		long criteria = -1;
		if (criteriaStr != null) {
			try {
				criteria = Long.parseLong(criteriaStr);
			}
			catch (NumberFormatException e) {
				// catch to be sure we don't burb in notification loop, 
				// but ignore, since just a debug aid
			}
		}
		return criteria;
	}

	public synchronized void removeAdapter(INodeAdapter a) {
		if (fAdapters == null || a == null)
			return;
		int newIndex = 0;
		INodeAdapter[] newAdapters = new INodeAdapter[fAdapters.length];
		int oldAdapterCount = adapterCount;
		boolean found = false;
		for (int oldIndex = 0; oldIndex < oldAdapterCount; oldIndex++) {
			INodeAdapter candidate = fAdapters[oldIndex];
			if (a == candidate) {
				adapterCount--;
				found = true;
			}
			else
				newAdapters[newIndex++] = fAdapters[oldIndex];
		}
		if (found)
			fAdapters = newAdapters;
	}

	private void ensureCapacity(int needed) {
		if (fAdapters == null) {
			// first time
			fAdapters = new INodeAdapter[needed + growthConstant];
			return;
		}
		int oldLength = fAdapters.length;
		if (oldLength < needed) {
			INodeAdapter[] oldAdapters = fAdapters;
			INodeAdapter[] newAdapters = new INodeAdapter[needed + growthConstant];
			System.arraycopy(oldAdapters, 0, newAdapters, 0, adapterCount);
			fAdapters = newAdapters;
		}
	}

	/**
	 * Returns the adapterCount. Equivilent to,  but faster than, getAdapters().size();
	 * @return int
	 */
	public int getAdapterCount() {
		return adapterCount;
	}

}
