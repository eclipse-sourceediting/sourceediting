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
abstract class AbstractContentSettingsHandler implements IContentSettingsHandler {


	private IContentSettings contentSettings = ContentSettingsCreator.create();
	private IResourceDelta fDelta;

	private void commonWithRespectToKind() {
		getContentSettings().releaseCache();
	}

	protected final IContentSettings getContentSettings() {
		return contentSettings;
	}

	protected final IResourceDelta getDelta() {
		return fDelta;
	}


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
			case IResourceDelta.CHANGED : {
				handleChanged();
			}
				break;
			case IResourceDelta.REMOVED : {
				handleRemoved();
			}
				break;
			case IResourceDelta.ADDED : {
				handleAdded();
			}
				break;


		}



	}

	protected void handleAdded() {
		commonWithRespectToKind();
	}

	protected void handleChanged() {
		commonWithRespectToKind();
	}

	protected void handleRemoved() {
		commonWithRespectToKind();
	}

}
