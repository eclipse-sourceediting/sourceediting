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

import java.net.URI;

public class UserEntry {
	private String fileMatch;
	private URI url;

	public String getFileMatch() {
		return fileMatch;
	}

	public void setFileMatch(String fileMatch) {
		this.fileMatch = fileMatch;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}
}