/*******************************************************************************
 * Copyright (c) 2002, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

public interface OASISCatalogConstants
{
  String namespaceName = "urn:oasis:names:tc:entity:xmlns:xml:catalog"; //$NON-NLS-1$
  /** Types of the catalog entries */
  /** The CATALOG element name. */
  String TAG_CATALOG = "catalog"; //$NON-NLS-1$
  /** The GROUP catalog entry. */
  String TAG_GROUP = "group"; //$NON-NLS-1$
  /** The PUBLIC catalog entry. */
  String TAG_PUBLIC = "public"; //$NON-NLS-1$
  /** The SYSTEM catalog etnry. */
  String TAG_SYSTEM = "system"; //$NON-NLS-1$
  /** The URI catalog entry. */
  String TAG_URI = "uri"; //$NON-NLS-1$
  /** The REWRITE_SYSTEM catalog entry. */
  String TAG_REWRITE_SYSTEM = "rewriteSystem"; //$NON-NLS-1$
  /** The REWRITE_URI catalog entry. */
  String TAG_REWRITE_URI = "rewriteURI"; //$NON-NLS-1$
  /** The systemSuffix catalog entry. */
  String TAG_SYSTEM_SUFFIX = "systemSuffix"; //$NON-NLS-1$
  /** The uriSuffix catalog entry. */
  String TAG_URI_SUFFIX = "uriSuffix"; //$NON-NLS-1$
  /** The DELEGATE_PUBLIC catalog entry. */
  String TAG_DELEGATE_PUBLIC = "delegatePublic"; //$NON-NLS-1$
  /** The DELEGATE_SYSTEM catalog entry. */
  String TAG_DELEGATE_SYSTEM = "delegateSystem"; //$NON-NLS-1$
  /** The DELEGATE_URI catalog entry. */
  String TAG_DELEGATE_URI = "delegateURI"; //$NON-NLS-1$
  /** The NEXT_CATALOG catalog entry. */
  String TAG_NEXT_CATALOG = "nextCatalog"; //$NON-NLS-1$
  /** Attributes */
  /** Attribute id used in all catalog entries */
  String ATTR_ID = "id"; //$NON-NLS-1$
  /** Attribute id base in all catalog entries */
  String ATTR_BASE = "xml:base"; //$NON-NLS-1$
  /** Attribute id prefer in catalog entries: CATALOG, GROUP */
  String ATTR_PREFERE = "prefer"; //$NON-NLS-1$
  /** Attribute used in catalog entries of type: PUBLIC */
  String ATTR_PUBLIC_ID = "publicId"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: SYSTEM
   */
  String ATTR_SYSTEM_ID = "systemId"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: SYSTEM_SUFFIX
   */
  String ATTR_SYSTEM_ID_SUFFFIX = "systemIdSuffix"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: URI
   */
  String ATTR_NAME = "name"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: PUBLIC, URI
   */
  String ATTR_URI = "uri"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: URI_SUFFIX
   */
  String ATTR_URI_SUFFIX = "uriSuffix"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: REWRITE_SYSTEM, DELEGATE_SYSTEM
   */
  String ATTR_SYSTEM_ID_START_STRING = "systemIdStartString"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: REWRITE_SYSTEM, REWRITE_URI
   */
  String ATTR_REWRITE_PREFIX = "rewritePrefix"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: DELEGATE_PUBLIC
   */
  String ATTR_PUBLIC_ID_START_STRING = "publicIdStartString"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: DELEGATE_PUBLIC,
   * DELEGATE_SYSTEM, DELEGATE_URI, NEXT_CATALOG
   */
  String ATTR_CATALOG = "catalog"; //$NON-NLS-1$
  /**
   * Attribute used in catalog entries of type: REWRITE_URI, DELEGATE_URI
   */
  String ATTR_URI_START_STRING = "uriStartString"; //$NON-NLS-1$
}
