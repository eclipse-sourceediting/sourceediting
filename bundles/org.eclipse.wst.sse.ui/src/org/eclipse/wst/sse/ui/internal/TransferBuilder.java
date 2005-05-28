/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.wst.sse.ui.internal.extension.DropActionProxy;
import org.eclipse.wst.sse.ui.internal.extension.RegistryReader;
import org.osgi.framework.Bundle;


/**
 * Builds drop target transfers, drag source transfers, and drop actions
 */
public class TransferBuilder extends RegistryReader {

	public static final String ATT_CLASS = "class"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String ATT_METHOD = "method"; //$NON-NLS-1$
	public static final String ATT_PRIORITY = "priority"; //$NON-NLS-1$
	public static final String ATT_SINGLETON = "singleton"; //$NON-NLS-1$
	public static final String ATT_TARGET_ID = "targetID"; //$NON-NLS-1$
	public static final String ATT_TRANSFER_ID = "transferID"; //$NON-NLS-1$

	private final static boolean debugTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/transferbuilder/time")); //$NON-NLS-1$  //$NON-NLS-2$

	public static final String PL_DRAG_SOURCE_TRANSFERS = "dragSourceTransfers"; //$NON-NLS-1$
	public static final String PL_DROP_TARGET_TRANSFERS = "dropTargetTransfers"; //$NON-NLS-1$

	public static final String PLUGIN_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$

	public static final String[] PRIORITIES = {"highest", "high", "mid", "low", "lowest"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	public static final String TAG_DRAG_SOURCE_CONTRIBUTION = "dragSourceContribution"; //$NON-NLS-1$
	public static final String TAG_DROP_ACTION = "dropAction"; //$NON-NLS-1$
	public static final String TAG_DROP_TARGET_CONTRIBUTION = "dropTargetContribution"; //$NON-NLS-1$

	public static final String TAG_TRANSFER = "transfer"; //$NON-NLS-1$

	public static final String TRUE = "true"; //$NON-NLS-1$

	/**
	 * @param element
	 * @param classAttribute
	 * @return Object
	 * @throws CoreException
	 */
	static Object createExecutableExtension(final IConfigurationElement element, final String classAttribute) throws CoreException {

		Object obj = null;

		String singleton = element.getAttribute(ATT_SINGLETON);
		String method = element.getAttribute(ATT_METHOD);
		if (TRUE.equalsIgnoreCase(singleton) && method != null) {
			try {
				String name = element.getAttribute(ATT_CLASS);
				String pluginId = element.getDeclaringExtension().getNamespace();
				Class cls = Platform.getBundle(pluginId).loadClass(name);
				Method mtd = cls.getMethod(method, new Class[]{});

				obj = mtd.invoke(null, null);
			} catch (ClassNotFoundException e) {
				obj = null;
			} catch (NoSuchMethodException e) {
				obj = null;
			} catch (IllegalAccessException e) {
				obj = null;
			} catch (InvocationTargetException e) {
				obj = null;
			}
		} else {
			obj = element.createExecutableExtension(classAttribute);
		}

		return obj;
	}

	/**
	 * Creates an extension. If the extension plugin has not been loaded a
	 * busy cursor will be activated during the duration of the load.
	 * 
	 * @param element
	 * @param classAttribute
	 * @return Object
	 * @throws CoreException
	 */
	public static Object createExtension(final IConfigurationElement element, final String classAttribute) {
		// If plugin has been loaded create extension.
		// Otherwise, show busy cursor then create extension.
		final Object[] result = new Object[1];
		String pluginId = element.getDeclaringExtension().getNamespace();
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle.getState() == Bundle.ACTIVE) {
			try {
				return createExecutableExtension(element, classAttribute);
			} catch (CoreException e) {
				handleCreateExecutableException(result, e);
			}
		} else {
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					try {
						result[0] = createExecutableExtension(element, classAttribute);
					} catch (Exception e) {
						handleCreateExecutableException(result, e);
					}
				}
			});
		}
		return result[0];
	}

	/**
	 * @param result
	 * @param e
	 */
	protected static void handleCreateExecutableException(Object[] result, Throwable e) {
		Logger.logException(e);
		result[0] = null;

	}

	protected List cache;
	protected String targetContributionTag;

	protected List targetIDs;

	/**
	 * @param element
	 * @return IDropAction
	 */
	protected IDropAction createDropAction(IConfigurationElement element) {
		Object obj = null;
		obj = createExtension(element, ATT_CLASS);
		if (obj == null)
			return null;
		return (obj instanceof IDropAction) ? (IDropAction) DropActionProxy.newInstance(obj) : null;
	}

	/**
	 * @param transferId
	 * @return IDropAction[]
	 */
	protected IDropAction[] createDropActions(String transferId) {
		if (cache == null)
			return new IDropAction[0];

		final int num = cache.size();
		if (num == 0)
			return new IDropAction[0];

		IDropAction[] as = new IDropAction[num];
		int j = 0;
		for (int p = 0; p < PRIORITIES.length; p++) {
			for (int i = 0; i < num; i++) {
				Object obj = cache.get(i);
				if (!(obj instanceof IConfigurationElement))
					continue;

				IConfigurationElement element = (IConfigurationElement) obj;
				if (!(TAG_DROP_ACTION.equals(element.getName())) || !(transferId.equals(element.getAttribute(ATT_TRANSFER_ID))))
					continue;

				if (PRIORITIES[p].equals(element.getAttribute(ATT_PRIORITY)) || (p == 2 && element.getAttribute(ATT_PRIORITY) == null)) {
					IDropAction a = createDropAction(element);
					if (a != null) {
						as[j] = a;
						j++;
					}
				}
			}
		}

		if (num == j)
			return as;

		IDropAction[] as2 = new IDropAction[j];
		for (int i = 0; i < j; i++) {
			as2[i] = as[i];
		}

		return as2;
	}

	/**
	 * @param element
	 * @return Transfer
	 */
	protected Transfer createTransfer(IConfigurationElement element) {
		Object obj = null;
		obj = createExtension(element, ATT_CLASS);
		if (obj == null)
			return null;
		return (obj instanceof Transfer) ? (Transfer) obj : null;
	}

	/**
	 * @return Transfer[]
	 */
	protected Transfer[] createTransfers() {
		if (cache == null)
			return new Transfer[0];

		final int num = cache.size();
		if (num == 0)
			return new Transfer[0];

		Transfer[] ts = new Transfer[num];
		int j = 0;
		for (int p = 0; p < PRIORITIES.length; p++) {
			for (int i = 0; i < num; i++) {
				Object obj = cache.get(i);
				if (!(obj instanceof IConfigurationElement))
					continue;

				IConfigurationElement element = (IConfigurationElement) obj;
				if (!TAG_TRANSFER.equals(element.getName()))
					continue;

				if (PRIORITIES[p].equals(element.getAttribute(ATT_PRIORITY)) || (p == 2 && element.getAttribute(ATT_PRIORITY) == null)) {
					Transfer t = createTransfer(element);
					if (t != null) {
						ts[j] = t;
						j++;
					}
				}
			}
		}

		if (num == j)
			return ts;

		Transfer[] ts2 = new Transfer[j];
		for (int i = 0; i < j; i++) {
			ts2[i] = ts[i];
		}

		return ts2;
	}

	/**
	 * @param editorId
	 * @return Transfer[]
	 */
	public Transfer[] getDragSourceTransfers(String editorId) {
		return getDragSourceTransfers(new String[]{editorId});
	}

	/**
	 * @param editorIds
	 * @return Transfer[]
	 */
	public Transfer[] getDragSourceTransfers(String[] editorIds) {
		long time0 = System.currentTimeMillis();
		readContributions(editorIds, TAG_DRAG_SOURCE_CONTRIBUTION, PL_DRAG_SOURCE_TRANSFERS);
		Transfer[] transfers = createTransfers();
		if (debugTime)
			System.out.println(getClass().getName() + "#getDragSourceTransfers(" + editorIds + "): " + transfers.length + " transfers created in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return transfers;
	}

	/**
	 * @param editorId
	 * @param className
	 * @return IDropAction[]
	 */
	public IDropAction[] getDropActions(String editorId, String transferClassName) {
		return getDropActions(new String[]{editorId}, transferClassName);
	}

	/**
	 * @param editorId
	 * @param className
	 * @return IDropAction[]
	 */
	public IDropAction[] getDropActions(String[] editorIds, String transferClassName) {
		long time0 = System.currentTimeMillis();
		readContributions(editorIds, TAG_DROP_TARGET_CONTRIBUTION, PL_DROP_TARGET_TRANSFERS);
		String transferId = getTransferIdOfClassName(transferClassName);
		IDropAction[] actions = createDropActions(transferId);
		if (debugTime)
			System.out.println(getClass().getName() + "#getDropActions(" + editorIds + "): " + actions.length + " drop actions created in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return actions;
	}

	/**
	 * @param editorId
	 * @return Transfer[]
	 */
	public Transfer[] getDropTargetTransfers(String editorId) {
		return getDropTargetTransfers(new String[]{editorId});
	}

	/**
	 * @param editorIds
	 * @return Transfer[]
	 */
	public Transfer[] getDropTargetTransfers(String[] editorIds) {
		long time0 = System.currentTimeMillis();
		readContributions(editorIds, TAG_DROP_TARGET_CONTRIBUTION, PL_DROP_TARGET_TRANSFERS);
		Transfer[] transfers = createTransfers();
		if (debugTime) {
			String idlist = ""; //$NON-NLS-1$
			if (editorIds.length > 0) {
				for (int i = 0; i < editorIds.length; i++) {
					idlist += editorIds[i];
					if (i < editorIds.length - 1)
						idlist += ","; //$NON-NLS-1$
				}
			}
			System.out.println(getClass().getName() + "#getDropTargetTransfers(" + idlist + "): " + transfers.length + " transfers created in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return transfers;
	}

	/**
	 * Returns the name of the part ID attribute that is expected in the
	 * target extension.
	 * 
	 * @param element
	 * @return String
	 */
	protected String getTargetID(IConfigurationElement element) {
		String value = element.getAttribute(ATT_TARGET_ID);
		return value != null ? value : "???"; //$NON-NLS-1$
	}

	/**
	 * @param className
	 * @return String
	 */
	private String getTransferIdOfClassName(String className) {
		String id = ""; //$NON-NLS-1$
		final int num = cache.size();
		if (className == null || cache == null || num == 0)
			return id;

		for (int i = 0; i < num; i++) {
			Object obj = cache.get(i);
			if (obj instanceof IConfigurationElement) {
				IConfigurationElement element = (IConfigurationElement) obj;
				if (className.equals(element.getAttribute(ATT_CLASS))) {
					id = element.getAttribute(ATT_ID);
					break;
				}
			}
		}

		return (id.length() != 0 ? id : className);
	}

	/**
	 * Reads the contributions from the registry for the provided workbench
	 * part and the provided extension point ID.
	 * 
	 * @param id
	 * @param tag
	 * @param extensionPoint
	 */
	protected void readContributions(String[] ids, String tag, String extensionPoint) {
		cache = null;
		targetIDs = Arrays.asList(ids);
		targetContributionTag = tag;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		readRegistry(registry, PLUGIN_ID, extensionPoint);
	}

	protected boolean readElement(IConfigurationElement element) {
		String tag = element.getName();
		if (tag.equals(targetContributionTag)) {
			String id = getTargetID(element);
			if (id == null || !targetIDs.contains(id)) {
				// This is not of interest to us - don't go deeper
				return true;
			}
		} else if (tag.equals(TAG_TRANSFER)) {
			if (cache == null)
				cache = new ArrayList();
			cache.add(element);
			return true; // just cache the element - don't go into it
		} else if (tag.equals(TAG_DROP_ACTION)) {
			if (cache == null)
				cache = new ArrayList();
			//cache.add(createActionDescriptor(element));
			cache.add(element);
			return true; // just cache the action - don't go into
		} else {
			return false;
		}

		readElementChildren(element);
		return true;
	}
}
