/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/*
 * CMBasicNode combines all the different content types into one. The idea
 * behind this is to allow arbitrary name changes to not require a tree
 * update.
 */
public class CMBasicNode extends CMRepeatableNode {

	// static final int EMPTY = 0;
	// static final int ANY = 1;
	// static final int PCDATA = 2;

	public CMBasicNode(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode);
	}

	public String getImagePath() {
		final String name = getName();
		ITextRegion pcdata = getNextRegion(iterator(), DTDRegionTypes.CONTENT_PCDATA);
		if (pcdata != null) {
			return DTDResource.PCDATAICON;
		}

		if (isRootElementContent()) {
			if (name.equals(EMPTY)) {
				return DTDResource.EMPTYICON;
			}
			else if (name.equals(ANY)) {
				return DTDResource.ANYICON;
			}
		}

		// Otherwise this is just an element reference node. Just return
		// what CMRepeatableNode would give us
		return super.getImagePath();
	}

	public ITextRegion getNameRegion() {
		RegionIterator iter = iterator();
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType() == DTDRegionTypes.NAME || region.getType() == DTDRegionTypes.CONTENT_PCDATA) {
				return region;
			}
		}
		return null;
	}

	// returns the occurrencetoken, or the token where the occurrence token
	// should appear after
	public ITextRegion getOccurrenceRegion() {
		RegionIterator iter = iterator();
		skipPastName(iter);
		if (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType() == DTDRegionTypes.OCCUR_TYPE) {
				return region;
			}
		}
		return getNameRegion();
	}

	public String getType() {
		ITextRegion pcdata = getNextRegion(iterator(), DTDRegionTypes.CONTENT_PCDATA);
		if (pcdata != null) {
			return PCDATA;
		}

		if (isRootElementContent()) {
			final String name = getName();
			if (isRootElementContent()) {
				if (name.equals(EMPTY)) {
					return EMPTY;
				}
				else if (name.equals(ANY)) {
					return ANY;
				}
				else {
					// otherwise just return it's name as the type
					return name;
				}

			}
		}
		return ""; //$NON-NLS-1$
	}

	public boolean isEmptyAnyOrPCData() {
		if (isPCData()) {
			return true;
		}


		final String name = getName();
		if (isRootElementContent()) {
			if (name.equals(EMPTY) || name.equals(ANY)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPCData() {
		ITextRegion pcdata = getNextRegion(iterator(), DTDRegionTypes.CONTENT_PCDATA);
		if (pcdata != null) {
			return true;
		}
		return false;
	}

	// public Node insertRegion(Region token)
	// {
	// if (!tokenStream.containsToken(token) && token.getType() ==
	// Token.OCCUR_TYPE)
	// {
	// // then add it so that our range contains it
	// insertIntoTokenStream(token);
	// }
	// return this;
	// }

	public boolean isReference() {
		return !isEmptyAnyOrPCData();
	}

	public void setName(Object requestor, String name) {
		// beginRecording(requestor, "Name Change");

		super.setName(requestor, name);
		if (!isReference()) {
			// if it is no longer a reference node, remove the occurrence
			// token
			setOccurrence(requestor, CMRepeatableNode.ONCE);
		}

		// endRecording(requestor);
	}

	/*
	 * public static String getName(int type) { switch (type) { case EMPTY:
	 * return emptyString; case ANY: return anyString; case PCDATA: return
	 * pcdataString; default: break; } // end of switch () return ""; }
	 */

}// CMBasicNode
