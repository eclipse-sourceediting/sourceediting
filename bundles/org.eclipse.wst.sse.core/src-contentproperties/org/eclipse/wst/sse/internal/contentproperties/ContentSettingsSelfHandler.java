/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.internal.contentproperties;



import org.eclipse.core.resources.IResourceDelta;

/**
* @deprecated This is package protected so no one cares anyways.
*/
final class ContentSettingsSelfHandler extends AbstractContentSettingsHandler {

	private static INotify notify;// = new ContentSettingsChangedSubject();


	private final void getSubject() {
		notify = ContentSettingsChangeSubject.getNotify();
	}

	/*
	 * @see AbstractContentSettingsHandler#handleAdded()
	 */
	protected void handleAdded() {
		getSubject();
		if (super.getDelta().getFlags() == 0) {
			// pulldown memu->copy->create file without override, new file,
			// property page create .contentsettings
			super.handleAdded();
			notify.notifyListeners(getDelta().getResource());

		} else if ((getDelta().getFlags() & IResourceDelta.MOVED_FROM) != 0) {
			// pulldown menu-> rename without override,pulldown menu->move
			// without override
			super.handleAdded();
			notify.notifyListeners(getDelta().getResource());

		}

	}


	/*
	 * @see AbstractContentSettingsHandler#handleChanged()
	 */
	protected void handleChanged() {
		getSubject();
		if ((getDelta().getFlags() & IResourceDelta.CONTENT) != 0 && (getDelta().getFlags() & IResourceDelta.REPLACED) == 0) {
			// result of edit, property page operate setProperty(apply or ok
			// button) in .contentsettings
			super.handleChanged();

			notify.notifyListeners(getDelta().getResource());

		} else if ((getDelta().getFlags() & IResourceDelta.CONTENT) != 0 && (getDelta().getFlags() & IResourceDelta.REPLACED) != 0) {
			super.handleChanged();
			notify.notifyListeners(getDelta().getResource());

		}


	}

	/*
	 * @see AbstractContentSettingsHandler#handleRemoved()
	 */
	protected void handleRemoved() {
		getSubject();
		if (getDelta().getFlags() == 0) {
			// pulldown menu->delete
			super.handleRemoved();
			notify.notifyListeners(getDelta().getResource());

		} else if ((getDelta().getFlags() & IResourceDelta.MOVED_TO) != 0) {
			// pulldown menu-> rename, pulldown menu->move
			super.handleRemoved();
			notify.notifyListeners(getDelta().getResource());

		}

	}

}
