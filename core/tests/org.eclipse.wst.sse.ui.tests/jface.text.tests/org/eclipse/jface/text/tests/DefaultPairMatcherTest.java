/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Christian Plesner Hansen (plesner@quenta.org) - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

/**
 * Tests for the default pair matcher.
 *
 * @since 3.3
 */
public class DefaultPairMatcherTest extends AbstractPairMatcherTest {

	public static Test suite() {
		return new TestSuite(DefaultPairMatcherTest.class);
	}

	protected ICharacterPairMatcher createMatcher(String chars) {
		return new DefaultCharacterPairMatcher(chars.toCharArray(),
				getDocumentPartitioning());
	}

	/*
	 * @see org.eclipse.jface.text.tests.AbstractPairMatcherTest#getDocumentPartitioning()
	 * @since 3.3
	 */
	protected String getDocumentPartitioning() {
		return IDocumentExtension3.DEFAULT_PARTITIONING;
	}

}
