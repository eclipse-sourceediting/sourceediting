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
package org.eclipse.wst.sse.ui.internal.extension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.undo.StructuredTextUndoManager;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor;
import org.eclipse.wst.sse.ui.extensions.ISelfValidateEditAction;


/**
 */
public class DropActionProxy implements InvocationHandler {
	public static Object newInstance(Object obj) {
		Object instance = null;
		try {
			Set set = new HashSet();
			Class clazz = obj.getClass();
			while (clazz != null) {
				Class[] interfaces = clazz.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					set.add(interfaces[i]);
				}
				clazz = clazz.getSuperclass();
			}
			Class[] classes = new Class[set.size()];
			Iterator itr = set.iterator();
			int i = 0;
			while (itr.hasNext()) {
				classes[i] = (Class) itr.next();
				i++;
			}
			instance = Proxy.newProxyInstance(obj.getClass().getClassLoader(), classes, new DropActionProxy(obj));
		}
		catch (Error e) {
			Logger.logException("Exception while proxying a drop action", e); //$NON-NLS-1$
			instance = obj;
		}
		return instance;
	}

	private IExtendedSimpleEditor editor = null;
	private IStructuredModel fRecorder;
	private Object obj;

	private DropActionProxy(Object obj) {
		this.obj = obj;
	}

	private void beginRecording() {
		IDocument document = null;
		if (editor != null) {
			document = editor.getDocument();
			if (document != null)
				fRecorder = getModelManager().getExistingModelForEdit(document);
			// Prepare for Undo
			if (fRecorder != null) {
				StructuredTextUndoManager um = fRecorder.getUndoManager();
				if (um != null) {
					if (this.obj instanceof IAction)
						um.beginRecording(this, ((IAction) this.obj).getText(), ((IAction) this.obj).getDescription());
					else
						um.beginRecording(this);
				}
			}
		}
	}

	private void endRecording() {
		if (fRecorder != null) {
			StructuredTextUndoManager um = fRecorder.getUndoManager();
			if (um != null)
				um.endRecording(this);
			fRecorder.releaseFromEdit();
			fRecorder = null;
		}
	}

	/**
	 *  
	 */
	private IModelManager getModelManager() {
		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(Object, Method,
	 *      Object[])
	 */
	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		Object result;
		String name = m.getName();
		try {
			if (name.equals("equals")) { //$NON-NLS-1$
				// Workaround for JDK's bug 4652876
				// "equals" always returns false even if both InvocationHandler
				// class
				// hold the same objects
				// See
				// http://developer.java.sun.com/developer/bugParade/bugs/4652876.html
				// This problem is in the IBM SDK 1.3.1
				// but I don't see the bug in Sun's JDK 1.4.1 (beta)
				Object arg = args[0];
				return (proxy.getClass() == arg.getClass() && equals(Proxy.getInvocationHandler(arg))) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (name.equals("run")) { //$NON-NLS-1$
				if (args[1] instanceof IExtendedSimpleEditor) {
					editor = (IExtendedSimpleEditor) args[1];
				}
				beginRecording();
				if ((editor != null) && !(obj instanceof ISelfValidateEditAction)) {
					IStatus status = editor.validateEdit(getDisplay().getActiveShell());
					if (!status.isOK()) {
						return null;
					}
				}
			}
			result = m.invoke(obj, args);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		finally {
			if (name.equals("run")) { //$NON-NLS-1$
				endRecording();
			}
		}
		return result;
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}
}
