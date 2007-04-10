/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.provisional.events;



/**
 * Clients can implement this interface, and register with the
 * structuredDocument using addAboutToBeChangedListner to be notified that the
 * structuredDocument is about to be changed, but hasn't been changed at the
 * time this event is fired.
 */
public interface IModelAboutToBeChangedListener {

	void modelAboutToBeChanged(AboutToBeChangedEvent structuredDocumentEvent);
}
