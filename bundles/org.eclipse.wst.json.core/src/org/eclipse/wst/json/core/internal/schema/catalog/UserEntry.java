/*************************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name="entry")
public class UserEntry {

	@XmlAttribute
	private String fileMatch;

	@XmlAttribute
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