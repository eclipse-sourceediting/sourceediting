/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import org.eclipse.jst.jsp.core.contentmodel.tld.URIResolverProvider;
import org.eclipse.wst.sse.core.util.URIResolver;

public class DefaultTaglibSupport extends AbstractTaglibSupport {

	public DefaultTaglibSupport() {
		super();
	}

	public URIResolver getResolver() {
		if (getTaglibManager().getResolverProvider() == null)
			return null;
		return getTaglibManager().getResolverProvider().getResolver();
	}

	public void setResolver(URIResolver resolver) {
		final URIResolver tldResolver = resolver;
		getTaglibManager().setResolverProvider(new URIResolverProvider() {
			public URIResolver getResolver() {
				return tldResolver;
			}
		});
	}
}