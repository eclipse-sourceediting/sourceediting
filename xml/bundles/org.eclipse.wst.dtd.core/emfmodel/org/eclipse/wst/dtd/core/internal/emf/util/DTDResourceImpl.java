/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.wst.dtd.core.internal.DTDCoreMessages;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;


public class DTDResourceImpl extends ResourceImpl {

	public DTDResourceImpl() {
		super();
	}

	public DTDResourceImpl(String filename) {
		super(URI.createURI(filename));
		// System.out.println(">>>> DTDResourceImpl ctor 1");
	}

	public DTDResourceImpl(URI uri) {
		super(uri);
		// System.out.println(">>>> DTDResourceImpl ctor 2");
	}

	/**
	 * Returns the resolved object for the given URI
	 * {@link URI#fragment fragment}.
	 * <p>
	 * The fragment encoding will typically be that produced by
	 * {@link #getURIFragment getURIFragment}.
	 * </p>
	 * 
	 * @param uriFragment
	 *            the fragment to resolve.
	 * @return the resolved object for the given fragment.
	 * @see #getURIFragment(EObject)
	 * @see org.eclipse.emf.ecore.resource.ResourceSet#getEObject(URI, boolean)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, org.eclipse.emf.ecore.resource.ResourceSet)
	 * @see org.eclipse.emf.ecore.InternalEObject#eObjectForURIFragmentSegment(String)
	 * @throws org.eclipse.emf.common.util.WrappedException
	 *             if a problem occurs navigating the fragment.
	 */
	public EObject getEObject(java.lang.String uriFragment) {
		EList contents = getContents();
		if (contents != null) {
			Iterator i = contents.iterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (obj instanceof DTDFile) {
					// there should only be one DTDFile in a DTD extent
					EObject result = ((DTDFile) obj).findObject(uriFragment);
					return result;
				}
			}
		}
		System.out.println(">>> DTDKey Error: cannot find object " + uriFragment); //$NON-NLS-1$
		return super.getEObject(uriFragment);
	}

	/**
	 * Returns the URI {@link URI#fragment fragment} that, when passed to
	 * {@link #getEObject getEObject} will return the given object.
	 * <p>
	 * In other words, the following is <code>true</code> for any object
	 * contained by a resource:
	 * 
	 * <pre>
	 * 
	 *    Resource resource = eObject.eResource();
	 *    eObject == resource.getEObject(resource.getURIFragment(eObject))
	 * 
	 * </pre>
	 * 
	 * An implementation may choose to use IDs or to use structured URI
	 * fragments, as supported by
	 * {@link org.eclipse.emf.ecore.InternalEObject#eURIFragmentSegment eURIFragmentSegment}.
	 * </p>
	 * 
	 * @param eObject
	 *            the object to identify.
	 * @return the URI {@link URI#fragment fragment} for the object.
	 * @see #getEObject(String)
	 * @see org.eclipse.emf.ecore.InternalEObject#eURIFragmentSegment(org.eclipse.emf.core.EStructuralFeature,
	 *      EObject)
	 */
	public java.lang.String getURIFragment(EObject eObject) {
		if (eObject instanceof DTDObject) {
			return ((DTDObject) eObject).getPathname();
		}
		return super.getURIFragment(eObject);
	}

	private DTDUtil dtdUtil;

	public DTDUtil getDTDUtil() {
		return dtdUtil;
	}

	public void setDTDUtil(DTDUtil dtdUtil) {
		this.dtdUtil = dtdUtil;
	}

	public void load(Map options) {

		String uriString = getURI().toString();
		try {
			DTDUtil dtdUtil = new DTDUtil();
			dtdUtil.parse(getResourceSet(), uriString);
			dtdUtil.setResource(this);
			setDTDUtil(dtdUtil);
			getContents().add(dtdUtil.getDTDFile());

			// Reset the dirty flag after the resource is loaded
			setModified(false);
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void save(OutputStream os, Object options) throws Exception {
		if (contents != null) {
			Iterator i = contents.iterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (obj instanceof DTDFile) {
					try {
						DTDPrinter printer = new DTDPrinter(true);
						printer.visitDTDFile((DTDFile) obj);

						String s = printer.getBuffer().toString();
						os.write(s.getBytes());
						// Reset the dirty flag after the resource is saved
						setModified(false);
					}
					catch (Exception e) {
					}
					break;
				}
			}
		}
	}

	public void save(ZipOutputStream zos, String entry, String metaModelName) throws Exception {
		throw new Exception(DTDCoreMessages._EXC_OPERATION_NOT_SUPPORTED); //$NON-NLS-1$
	}
}
