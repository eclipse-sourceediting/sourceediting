/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.provisional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;



/**
 * AbstractNotifier is similar to (and based on) the EMF NotifierImpl class,
 * but is not related to EMF per se. This class is simpler (that is, not as
 * many functions).
 * 
 * Implementers of this INodeNotifier must subclass this class.
 */
public abstract class AbstractNotifier implements INodeNotifier {
	private final static int growthConstant = 3;
	private int adapterCount = 0;

	private INodeAdapter[] fAdapters;

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

	private synchronized void ensureCapacity(int needed) {
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
	 * NOT API: used only for testing.
	 * 
	 * @return int
	 */
	public int getAdapterCount() {
		return adapterCount;
	}

	/**
	 * Default behavior for getting an adapter.
	 */
	public synchronized INodeAdapter getAdapterFor(Object type) {
		// first, we'll see if we already have one
		INodeAdapter result = getExistingAdapter(type);
		// if we didn't find one in our list already,
		// let's create it
		if (result == null) {
			FactoryRegistry reg = getFactoryRegistry();
			if (reg != null) {
				INodeAdapterFactory factory = reg.getFactoryFor(type);
				if (factory != null) {
					result = factory.adapt(this);
				}
			}
			// We won't prevent null from being returned, but it would be
			// unusual.
			// It might be because Factory is not working correctly, or
			// not installed, so we'll allow warning message.
			if ((result == null) && (org.eclipse.wst.sse.core.internal.util.Debug.displayWarnings)) {
				System.out.println("Warning: no adapter was found or created for " + type); //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * Returns a shallow clone of list, since clients should not manipulate
	 * our list directly. Instead, they should use add/removeAdapter.
	 */
	public synchronized Collection getAdapters() {
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
				// EMF uses the unmodifiableCollection. Its a bit of a
				// performance
				// drain, but may want to leave in since
				// it would "fail fast" if someone was trying to modify the
				// list.
				return Collections.unmodifiableCollection(Arrays.asList(tempAdapters));
				// return Arrays.asList(newAdapters);
			}
		}
		else
			return Collections.EMPTY_LIST;
	}

	private long getAdapterTimeCriteria() {
		// to "re-get" the property each time is a little awkward, but we
		// do it that way to avoid adding instance variable just for
		// debugging.
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

	public synchronized INodeAdapter getExistingAdapter(Object type) {
		INodeAdapter result = null;
		for (int i = 0; i < adapterCount; i++) {
			INodeAdapter a = fAdapters[i];
			if (a != null && a.isAdapterForType(type)) {
				result = a;
				break;
			}
		}
		// if we didn't find one in our list,
		// return the null result
		return result;
	}

	abstract public FactoryRegistry getFactoryRegistry();

	public void notify(int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		int localAdapterCount = 0;
		INodeAdapter[] localAdapters = null;

		// lock object while making local assignments
		synchronized (this) {
			if (fAdapters != null) {
				localAdapterCount = adapterCount;
				localAdapters = new INodeAdapter[localAdapterCount];
				System.arraycopy(fAdapters, 0, localAdapters, 0, localAdapterCount);
			}
		}

		for (int i = 0; i < localAdapterCount; i++) {
			INodeAdapter a = localAdapters[i];

			if (Logger.DEBUG_ADAPTERNOTIFICATIONTIME) {
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
				try {
					// ** keep this line identical with debug version!!
					a.notifyChanged(this, eventType, changedFeature, oldValue, newValue, pos);
				}
				catch (Exception e) {
					// Its important to "keep going", since notifications
					// occur between an
					// aboutToChange event and a changed event -- the
					// changed event typically being require
					// to restore state, etc. So, we just log message, do
					// not re-throw it, but
					// typically the exception does indicate a serious
					// program error.
					Logger.logException("A structured model client, " + a + " threw following exception during adapter notification (" + INodeNotifier.EVENT_TYPE_STRINGS[eventType] + " )", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}

		}
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

}
