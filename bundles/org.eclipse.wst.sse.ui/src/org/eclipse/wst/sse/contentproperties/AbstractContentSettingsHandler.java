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

import org.eclipse.core.resources.IResourceDelta;

abstract class AbstractContentSettingsHandler implements IContentSettingsHandler {


	private IContentSettings contentSettings = ContentSettingsCreator.create();
	private IResourceDelta fDelta;


	/*
	 * @see IContentSettingsHandler#handle(IResourceDelta)
	 */
	public void handle(final IResourceDelta delta) {
		this.fDelta = delta;
		// get Resource delta kind
		final int kind = delta.getKind();
		// never used!?
		//IResource resource = delta.getResource();


		switch (kind) {
			case IResourceDelta.CHANGED :
				{
					handleChanged();
				}
				break;
			case IResourceDelta.REMOVED :
				{
					handleRemoved();
				}
				break;
			case IResourceDelta.ADDED :
				{
					handleAdded();
				}
				break;


		}



	}

	private void commonWithRespectToKind() {
		getContentSettings().releaseCache();
	}

	protected void handleChanged() {
		commonWithRespectToKind();
	}

	protected void handleRemoved() {
		commonWithRespectToKind();
	}

	protected void handleAdded() {
		commonWithRespectToKind();
	}

	protected final IResourceDelta getDelta() {
		return fDelta;
	}

	protected final IContentSettings getContentSettings() {
		return contentSettings;
	}

}
