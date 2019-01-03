/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.contentproperties.ui;

/**
 * <p>
 * This interface is not intended to be implemented by clients directly.
 * Instead, please use abstract class(AbstractDeviceProfileEntry) instead.
 * </p>
 * 
 * @deprecated Not needed. See BUG118359
 */
public interface DeviceProfileEntry {
	public String getEntryId();

	public String getEntryName();

	public void release();
}
