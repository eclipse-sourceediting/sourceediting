/*************************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.util.HashSet;
import java.util.Set;

public class UserEntries {
	private Set<UserEntry> entries = new HashSet<UserEntry>();

	public Set<UserEntry> getEntries() {
		return entries;
	}

	public void add(UserEntry entry) {
		entries.add(entry);
	}
}