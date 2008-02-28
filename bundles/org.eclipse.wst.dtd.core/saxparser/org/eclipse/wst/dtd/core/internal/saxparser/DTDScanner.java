/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;

import java.util.Vector;

/**
 * Scanning / parsing the content string from the Decl statement
 * 
 * @version
 */
public class DTDScanner {
	// element content model strings
	private static final char[] empty_string = {'E', 'M', 'P', 'T', 'Y'};
	private static final char[] any_string = {'A', 'N', 'Y'};
	private static final char[] pcdata_string = {'#', 'P', 'C', 'D', 'A', 'T', 'A'};

	// attribute type and default type strings
	private static final char[] cdata_string = {'C', 'D', 'A', 'T', 'A'};
	private static final char[] id_string = {'I', 'D'};
	private static final char[] ref_string = {'R', 'E', 'F'};
	private static final char[] entit_string = {'E', 'N', 'T', 'I', 'T'};
	private static final char[] ies_string = {'I', 'E', 'S'};
	private static final char[] nmtoken_string = {'N', 'M', 'T', 'O', 'K', 'E', 'N'};
	private static final char[] notation_string = {'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
	private static final char[] required_string = {'#', 'R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D'};
	private static final char[] implied_string = {'#', 'I', 'M', 'P', 'L', 'I', 'E', 'D'};
	private static final char[] fixed_string = {'#', 'F', 'I', 'X', 'E', 'D'};

	private static final char[] system_string = {'S', 'Y', 'S', 'T', 'E', 'M'};
	private static final char[] public_string = {'P', 'U', 'B', 'L', 'I', 'C'};
	private static final char[] ndata_string = {'N', 'D', 'A', 'T', 'A'};

	private int[] fOpStack = null;
	private CMGroupNode[] fGrpStack = null;

	private EntityPool entityPool;

	/* Attribute Def scanner state */
	static final int Att_Scanner_State_Name = 1, Att_Scanner_State_Type = 2, Att_Scanner_State_DefaultType = 3, Att_Scanner_State_DefaultValue = 4;

	private StringParser contentString;
	private String cData;
	private int prevNodeOffset = 0;
	private int nodeOffset = 1;
	private String ownerDTD;

	private String errorString;

	public DTDScanner(String ownerDTD, String cm) {
		// System.out.println("Cm: " + cm);
		contentString = new StringParser(cm);
		cData = cm;
		this.ownerDTD = ownerDTD;
	}

	//
	// [46] contentspec ::= 'EMPTY' | 'ANY' | Mixed | children
	//
	// Specific to VisualDTD : also allows %PEReference;
	//
	public CMNode scanContentModel() {
		CMNode cmNode = null;
		contentString.skipPastSpaces();
		if (contentString.skippedString(empty_string)) {
			cmNode = new CMBasicNode("EMPTY", CMNodeType.EMPTY); //$NON-NLS-1$
		}
		else if (contentString.skippedString(any_string)) {
			cmNode = new CMBasicNode("ANY", CMNodeType.ANY); //$NON-NLS-1$
		}
		else if (contentString.lookingAtChar('%', true)) {
			cmNode = new CMReferenceNode(contentString.getData(), CMNodeType.ENTITY_REFERENCE);
		}
		else if (!contentString.lookingAtChar('(', true)) {
			// This means that the contentmodel string is bad...
			// System.out.println("!!! exception - no (");
			return cmNode;
		}
		else {
			// System.out.println("Remaining String = " +
			// contentString.getRemainingString());

			contentString.skipPastSpaces();
			boolean skippedPCDATA = contentString.skippedString(pcdata_string);
			if (skippedPCDATA) {
				cmNode = scanMixed();
			}
			else {
				cmNode = scanChildren();
			}

		}
		return cmNode;
	}

	//
	// [51] Mixed ::= '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*' | '(' S?
	// '#PCDATA' S? ')'
	//
	// Called after scanning past '(' S? '#PCDATA'
	//
	private CMNode scanMixed() {
		// System.out.println("ScanMixed...");
		CMGroupNode cmNode = new CMGroupNode();
		cmNode.setGroupKind(CMNodeType.GROUP_CHOICE);
		cmNode.addChild(new CMBasicNode("#PCDATA", CMNodeType.PCDATA)); //$NON-NLS-1$

		// int prevNodeIndex = -1;
		boolean starRequired = false;


		while (true) {
			if (contentString.lookingAtSpace(true)) {
				contentString.skipPastSpaces();
			}
			prevNodeOffset = contentString.getCurrentOffset();

			if (!contentString.lookingAtChar('|', true)) {
				if (!contentString.lookingAtChar(')', true)) {
					break;
				}
				if (contentString.lookingAtChar('*', true)) {
					cmNode.setOccurrence(CMNodeType.ZERO_OR_MORE);
				}
				else if (starRequired) {
					// System.out.println(" * is required ... ");
				}
				break;
			}

			if (contentString.lookingAtSpace(true)) {
				contentString.skipPastSpaces();
			}

			nodeOffset = contentString.getCurrentOffset();
			if (nodeOffset != -1) {
				// skip pass "|"
				prevNodeOffset = nodeOffset;
			}

			starRequired = true;
			contentString.skipPastNameAndPEReference(')');
			nodeOffset = contentString.getCurrentOffset();

			if (nodeOffset == -1)
				break;

			// add leave node
			int len = nodeOffset - prevNodeOffset;
			String refName = contentString.getString(prevNodeOffset, len);
			prevNodeOffset = nodeOffset;
			if (refName.startsWith("%")) //$NON-NLS-1$
				cmNode.addChild(new CMReferenceNode(refName, CMNodeType.ENTITY_REFERENCE));
			else
				cmNode.addChild(new CMReferenceNode(refName, CMNodeType.ELEMENT_REFERENCE));

		} // end while


		if (cmNode.getChildren().size() == 1) {
			// simplify the contentModel if the CMGroupNode only has one child
			// node.
			return (CMBasicNode) cmNode.getChildren().elementAt(0);
		}

		return cmNode;
	}

	//
	// [47] children ::= (choice | seq) ('?' | '*' | '+')?
	// [49] choice ::= '(' S? cp ( S? '|' S? cp )* S? ')'
	// [50] seq ::= '(' S? cp ( S? ',' S? cp )* S? ')'
	// [48] cp ::= (Name | choice | seq) ('?' | '*' | '+')?
	//
	// Called after scanning past '('

	private CMNode scanChildren() {

		// System.out.println("scanChildren...");

		if (contentString.lookingAtSpace(true)) {
			contentString.skipPastSpaces();
		}

		prevNodeOffset = contentString.getCurrentOffset();
		nodeOffset = contentString.getCurrentOffset();
		int depth = 1;
		initializeContentModelStack(depth);
		int len;

		String nodeName;
		CMGroupNode cmNode = new CMGroupNode();
		fGrpStack[depth] = cmNode;

		while (true) {

			// System.out.println(" Begin outter while loop ..." );
			if (contentString.lookingAtChar('(', true)) {
				if (contentString.lookingAtSpace(true)) {
					// skip past any white spaces after the '('
					contentString.skipPastSpaces();
				}

				depth++;
				initializeContentModelStack(depth);
				fGrpStack[depth] = new CMGroupNode();
				fGrpStack[depth - 1].addChild(fGrpStack[depth]);
				continue;
			}

			prevNodeOffset = contentString.getCurrentOffset();
			contentString.skipPastNameAndPEReference(')');
			nodeOffset = contentString.getCurrentOffset();

			/*
			 * System.out.println(" prevNodeOFfset " + prevNodeOffset);
			 * System.out.println(" currentNodeOFfset " + nodeOffset);
			 */

			len = nodeOffset - prevNodeOffset;

			if (nodeOffset == -1 || len < 1) {
				break;
			}

			nodeName = contentString.getString(prevNodeOffset, len);

			CMRepeatableNode rn;
			if (nodeName.startsWith("%")) { //$NON-NLS-1$
				rn = new CMReferenceNode(nodeName, CMNodeType.ENTITY_REFERENCE);
			}
			else {
				rn = new CMReferenceNode(nodeName, CMNodeType.ELEMENT_REFERENCE);
			}

			fGrpStack[depth].addChild(rn);

			prevNodeOffset = nodeOffset;

			if (contentString.lookingAtChar('?', true)) {
				rn.setOccurrence(CMNodeType.OPTIONAL);
			}
			else if (contentString.lookingAtChar('*', true)) {
				rn.setOccurrence(CMNodeType.ZERO_OR_MORE);
			}
			else if (contentString.lookingAtChar('+', true)) {
				rn.setOccurrence(CMNodeType.ONE_OR_MORE);
			}

			if (contentString.lookingAtSpace(true)) {
				contentString.skipPastSpaces();
			}

			prevNodeOffset = contentString.getCurrentOffset();

			while (true) {
				// System.out.println(" Begin inner while loop ... depth " +
				// depth );
				if (fOpStack[depth] != CMNodeType.GROUP_SEQUENCE && contentString.lookingAtChar('|', true)) {
					fOpStack[depth] = CMNodeType.GROUP_CHOICE;
					fGrpStack[depth].setGroupKind(CMNodeType.GROUP_CHOICE);
					if (contentString.lookingAtSpace(true))
						contentString.skipPastSpaces();
					break;
				}
				else if (fOpStack[depth] != CMNodeType.GROUP_CHOICE && contentString.lookingAtChar(',', true)) {
					fOpStack[depth] = CMNodeType.GROUP_SEQUENCE;
					fGrpStack[depth].setGroupKind(CMNodeType.GROUP_SEQUENCE);
					if (contentString.lookingAtSpace(true))
						contentString.skipPastSpaces();
					break;
				}
				else {
					if (contentString.lookingAtSpace(true)) {
						contentString.skipPastSpaces();
					}
					if (!contentString.lookingAtChar(')', true)) {
						// System.out.println(" error ) not found");
					}
					// end of the curent group node
					if (contentString.lookingAtChar('?', true)) {
						fGrpStack[depth].setOccurrence(CMNodeType.OPTIONAL);
					}
					else if (contentString.lookingAtChar('*', true)) {
						fGrpStack[depth].setOccurrence(CMNodeType.ZERO_OR_MORE);
					}
					else if (contentString.lookingAtChar('+', true)) {
						fGrpStack[depth].setOccurrence(CMNodeType.ONE_OR_MORE);
					}
					depth--;
					if (depth == 0) {
						break;
					}
					if (contentString.lookingAtSpace(true))
						contentString.skipPastSpaces();

					// System.out.println(" End end inner while loop ... depth
					// " + depth );

				}
			} // inner while

			if (depth == 0) {
				break;
			}
		} // outer while

		if (cmNode.getChildren().size() == 1) {
			// simplify the contentModel if the CMGroupNode only has one child
			// node and that node is an element ref.
			CMRepeatableNode rn = (CMRepeatableNode) cmNode.getChildren().elementAt(0);
			if (rn instanceof CMReferenceNode) {
				CMReferenceNode ref = (CMReferenceNode) rn;
				if (ref.getType() == CMNodeType.ELEMENT_REFERENCE) {
					if (ref.getOccurrence() == CMNodeType.ONE) {
						ref.setOccurrence(cmNode.getOccurrence());
						return ref;
					}
					else if (cmNode.getOccurrence() == CMNodeType.ONE) {
						return ref;
					}
				} // end of if ()
			}
		}

		return cmNode;
	}

	//
	// [52] AttlistDecl ::= '<!ATTLIST' S Name AttDef* S? '>'
	// [53] AttDef ::= S Name S AttType S DefaultDecl
	// [60] DefaultDecl ::= '#REQUIRED' | '#IMPLIED' | (('#FIXED' S)?
	// AttValue)
	//

	public Vector scanAttlistDecl(EntityPool entityPool) {
		Vector attList = new Vector();
		this.entityPool = entityPool;

		int scannerState = Att_Scanner_State_Name;
		
		AttNode attNode;

		while (true) {
			attNode = new AttNode();

			// System.out.println(" scanner state: " + scannerState);

			// scanning att name
			if (scannerState == Att_Scanner_State_Name) {
				// System.out.println("scan att Name...");
				scannerState = checkForAttributeWithPEReference(attNode, scannerState);
				if (scannerState == -1) {
					return attList;
				}
			}

			// scanning att type
			if (scannerState == Att_Scanner_State_Type) {
				// System.out.println("scan att type...");

				if (contentString.skippedString(cdata_string)) {
					attNode.type = new String(cdata_string);
					scannerState = Att_Scanner_State_DefaultType;
				}
				else if (contentString.skippedString(id_string)) {
					if (!contentString.skippedString(ref_string)) {
						attNode.type = new String(id_string);
					}
					else if (!contentString.lookingAtChar('S', true)) {
						attNode.type = "IDREF"; //$NON-NLS-1$
					}
					else {
						attNode.type = "IDREFS"; //$NON-NLS-1$
					}
					scannerState = Att_Scanner_State_DefaultType;
				}
				else if (contentString.skippedString(entit_string)) {
					if (contentString.lookingAtChar('Y', true)) {
						attNode.type = "ENTITY"; //$NON-NLS-1$
					}
					else if (contentString.skippedString(ies_string)) {
						attNode.type = "ENTITIES"; //$NON-NLS-1$
					}
					scannerState = Att_Scanner_State_DefaultType;
				}
				else if (contentString.skippedString(nmtoken_string)) {
					if (contentString.lookingAtChar('S', true)) {
						attNode.type = "NMTOKENS"; //$NON-NLS-1$
					}
					else {
						attNode.type = "NMTOKEN"; //$NON-NLS-1$
					}
					scannerState = Att_Scanner_State_DefaultType;
				}
				else if (contentString.skippedString(notation_string)) {
					if (contentString.lookingAtSpace(true)) {
						contentString.skipPastSpaces();
					}
					if (!contentString.lookingAtChar('(', true)) {
						System.out.println(" missing ( in notation "); //$NON-NLS-1$
					}
					attNode.type = "NOTATION"; //$NON-NLS-1$
					attNode.enumList = scanEnumeration(contentString, true);
					scannerState = Att_Scanner_State_DefaultType;
				}
				else if (contentString.lookingAtChar('(', true)) {
					attNode.type = "ENUMERATION"; //$NON-NLS-1$
					attNode.enumList = scanEnumeration(contentString, false);
					scannerState = Att_Scanner_State_DefaultType;
				}
				else {
					scannerState = checkForAttributeWithPEReference(attNode, scannerState);
					if (scannerState == Att_Scanner_State_Type) {
						setErrorString("Failed to find type for attribute '" + attNode.name + "'.  Please refer to the original DTD file."); //$NON-NLS-1$ //$NON-NLS-2$
						// we failed to find a type for this attribute
						return attList;
					}
				}
			}

			if (scannerState == Att_Scanner_State_DefaultType) {
				contentString.skipPastSpaces();
				// System.out.println("scan default type...");
				if (contentString.skippedString(required_string)) {
					attNode.defaultType = new String(required_string);
				}
				else if (contentString.skippedString(implied_string)) {
					attNode.defaultType = new String(implied_string);
				}
				else {
					if (contentString.skippedString(fixed_string)) {
						contentString.skipPastSpaces();
						attNode.defaultType = new String(fixed_string);
					}
					else
						// "default"
						attNode.defaultType = "NOFIXED"; //$NON-NLS-1$

					if (contentString.lookingAtSpace(true))
						contentString.skipPastSpaces();
					attNode.defaultValue = scanDefaultAttValue(contentString);
				}
				scannerState = Att_Scanner_State_Name; // next
			}


			attList.addElement(attNode);
			if (contentString.lookingAtSpace(true))
				contentString.skipPastSpaces();
			nodeOffset = contentString.getCurrentOffset();

			if (nodeOffset >= cData.length())
				return attList;

			prevNodeOffset = contentString.getCurrentOffset();

		}// end while loop
	}

	//
	// content model stack
	//
	private void initializeContentModelStack(int depth) {
		if (fOpStack == null) {
			fOpStack = new int[8];
			fGrpStack = new CMGroupNode[8];
		}
		else if (depth == fOpStack.length) {
			int[] newStack = new int[depth * 2];
			System.arraycopy(fOpStack, 0, newStack, 0, depth);
			fOpStack = newStack;

			CMGroupNode[] newGrpStack = new CMGroupNode[depth * 2];
			System.arraycopy(fGrpStack, 0, newGrpStack, 0, depth);
			fGrpStack = newGrpStack;
		}
		fOpStack[depth] = -1;
		fGrpStack[depth] = null;
	}

	//
	private int checkForAttributeWithPEReference(AttNode attNode, int scannerState) {
		int state = -1;
		int len;
		String rawText;
		EntityDecl pEntity;

		// System.out.println(" checkforATTPERReference- start scannerState: "
		// + scannerState);
		if (scannerState == Att_Scanner_State_Name) {
			contentString.skipPastNameAndPEReference(' ');
			nodeOffset = contentString.getCurrentOffset();
			len = nodeOffset - prevNodeOffset;

			rawText = contentString.getString(prevNodeOffset, len);
			attNode.name = rawText;

			// System.out.println("State_name : " + rawText);

			if (rawText.startsWith("%") && rawText.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
				String pe = rawText.substring(1, rawText.length() - 1);

				pEntity = entityPool.referPara(pe);
				if (pEntity != null) {
					// System.out.println(" name :" + rawText +" expandTo:" +
					// pEntity.expandedValue);
					state = whatIsTheNextAttributeScanningState(pEntity.expandedValue, scannerState);
					// System.out.println("checkForAttrwithPER - nextstate: "
					// + state);
				}
				else
					state = Att_Scanner_State_Type;
			}
			else
				state = Att_Scanner_State_Type;

		}
		else if (scannerState == Att_Scanner_State_Type) {
			if (contentString.lookingAtChar('%', true)) {
				rawText = getPEName();
				attNode.type = "%" + rawText; //$NON-NLS-1$
				String peName = rawText.substring(0, rawText.length() - 1);
				// System.out.println("State_type : pe- " + peName);
				pEntity = entityPool.referPara(peName);
				if (pEntity != null) {
					state = whatIsTheNextAttributeScanningState(pEntity.expandedValue, scannerState);
				}
				else
					state = Att_Scanner_State_DefaultType;
			}
			else
				state = Att_Scanner_State_Type;
		}
		else if (scannerState == Att_Scanner_State_DefaultType) {
			if (contentString.lookingAtChar('%', true)) {
				rawText = getPEName();
				attNode.defaultType = rawText;
				// System.out.println("State_defaultType : " + rawText);
				pEntity = entityPool.referPara(rawText.substring(1, rawText.length() - 1));
				if (pEntity != null) {
					state = whatIsTheNextAttributeScanningState(pEntity.expandedValue, scannerState);
				}
				else
					state = Att_Scanner_State_DefaultValue;
			}
			else
				state = Att_Scanner_State_DefaultType;
		}
		else if (scannerState == Att_Scanner_State_DefaultValue) {
			if (contentString.lookingAtChar('%', true)) {
				rawText = getPEName();
				attNode.defaultValue = rawText;
				// System.out.println("State_defaultValue : " + rawText);

				pEntity = entityPool.referPara(rawText.substring(1, rawText.length() - 1));
				if (pEntity != null) {
					state = whatIsTheNextAttributeScanningState(pEntity.expandedValue, scannerState);
				}
				else
					state = Att_Scanner_State_DefaultValue;
			}
			else
				state = Att_Scanner_State_DefaultValue;
		}

		if (contentString.lookingAtSpace(true))
			contentString.skipPastSpaces();
		prevNodeOffset = contentString.getCurrentOffset();
		return state;
	}

	private String getPEName() {
		prevNodeOffset = contentString.getCurrentOffset();
		contentString.skipToChar(';', true);
		nodeOffset = contentString.getCurrentOffset();
		int len = nodeOffset - prevNodeOffset;
		return contentString.getString(prevNodeOffset, len);
	}

	private int whatIsTheNextAttributeScanningState(String attrContentString, int currentState) {
		StringParser sp = new StringParser(attrContentString.trim());


		int nextState = currentState;
		int nOffset = 0;

		/*
		 * System.out.println("WhatistheNext AttContentStringt : " +
		 * attrContentString); System.out.println("WhatistheNext
		 * AttContentStringL : " + attrContentString.length());
		 * System.out.println("WhatistheNext currentstate : " + currentState);
		 */

		while (true) {
			// System.out.println("WhatistheNext inside whil nextstate : " +
			// nextState);
			if (nextState == Att_Scanner_State_Name) { // the current
														// scanning state is
														// Attr Name, is the
														// next part Attr Type
														// ?
				if (isAttrName(sp)) {
					nextState = Att_Scanner_State_Type;
				}
			}
			else if (nextState == Att_Scanner_State_Type) { // the current
															// scanning state
															// is Attr Type,
															// is the next
															// part Default
															// Attr Type ?

				if (isAttrType(sp)) {
					nextState = Att_Scanner_State_DefaultType;
				}
				else
					System.out.println("WhatistheNext Attr Part - is not an Attr Type"); //$NON-NLS-1$

			}

			if (nextState == Att_Scanner_State_DefaultType) {

				int dType = isAttrDefaultType(sp);
				// System.out.println("WhatistheNext inside dType : " +
				// dType);
				if (dType == 1) // #REQUIRED or #IMPLIED
				{
					nextState = Att_Scanner_State_Name;
				}
				else {
					if (dType == 2) // #FIXED
					{
						// need to look at this again
						nextState = Att_Scanner_State_DefaultType;
					}

					if (scanDefaultAttValue(sp) != null) {
						nextState = Att_Scanner_State_Name;

					}
				}
			}

			sp.skipPastSpaces();

			if (nOffset == sp.getCurrentOffset())
				break;

			nOffset = sp.getCurrentOffset();

			if (nOffset >= attrContentString.length() || nOffset == -1)
				break;

		} // end while

		return nextState;
	}


	private boolean isAttrName(StringParser sp) {
		// System.out.println("isAttrName - sp:" + sp.fData);
		// System.out.println("isAttrName - currentOffset:" +
		// sp.getCurrentOffset());
		boolean isAttrName = false;
		int prev = sp.getCurrentOffset();
		sp.skipPastName(' ');
		if ((sp.getCurrentOffset() - prev) > 0)
			isAttrName = true;
		return isAttrName;

	}

	private boolean isAttrType(StringParser sp) {
		// System.out.println("isAttrType - sp:" + sp.fData);
		// System.out.println("isAttrType - currentOffset:" +
		// sp.getCurrentOffset());
		boolean isAttrType = false;
		if (sp.skippedString(cdata_string)) {
			isAttrType = true;
		}
		else if (sp.skippedString(id_string)) {
			if (!sp.skippedString(ref_string)) {
				isAttrType = true; // ID
			}
			else if (!sp.lookingAtChar('S', true)) {
				isAttrType = true; // IDREFS
			}
			else {
				isAttrType = true; // IDREF
			}
		}
		else if (sp.skippedString(entit_string)) {
			if (sp.lookingAtChar('Y', true)) {
				isAttrType = true; // ENTITY
			}
			else if (sp.skippedString(ies_string)) {
				isAttrType = true; // ENTITITES
			}
		}
		else if (sp.skippedString(nmtoken_string)) {
			if (sp.lookingAtChar('S', true)) {
				isAttrType = true; // NMTOKENS
			}
			else {
				isAttrType = true; // NMTOKEN
			}
		}
		else if (sp.skippedString(notation_string)) {
			if (!sp.lookingAtChar('(', true)) {
				// System.out.println(" missing ( in notation ");
			}
			Vector enumList = scanEnumeration(sp, true);
			if (enumList == null || enumList.size() == 0)
				isAttrType = false;
			else
				isAttrType = true;
		}
		else if (sp.lookingAtChar('(', true)) {
			// "ENUMERATION";
			Vector enumList = scanEnumeration(sp, false);
			if (enumList == null || enumList.size() == 0)
				isAttrType = false;
			else
				isAttrType = true;
		}

		return isAttrType;

	}

	/*
	 * return 0 - not default type 1 - #REQUIRED or #IMPLIED 2 - #FIXED
	 */

	private int isAttrDefaultType(StringParser sp) {
		// System.out.println("isAttrDefaultType - sp:" + sp.fData);
		int result = 0;
		if (sp.skippedString(required_string)) {
			result = 1;
		}
		else if (sp.skippedString(implied_string)) {
			result = 1;
		}
		else if (sp.skippedString(fixed_string)) {
			result = 2;
		}
		return result;
	}

	//
	// [58] NotationType ::= 'NOTATION' S '(' S? Name (S? '|' S? Name)* S? ')'
	// [59] Enumeration ::= '(' S? Nmtoken (S? '|' S? Nmtoken)* S? ')'
	//
	// Called after scanning '('
	//
	private Vector scanEnumeration(StringParser sp, boolean isNotationType) {
		// System.out.println(" scanEnum ...");
		Vector enumList = null;
		int len;

		if (sp.lookingAtSpace(true))
			sp.skipPastSpaces();

		int prevNodeOffset = sp.getCurrentOffset();
		int nodeOffset;

		String nodeName;

		while (true) {

			if (isNotationType)
				sp.skipPastNameAndPEReference(')');
			else
				sp.skipPastNmtokenAndPEReference(')');

			nodeOffset = sp.getCurrentOffset();

			if (nodeOffset == -1) {
				return enumList;
			}

			len = nodeOffset - prevNodeOffset;
			nodeName = sp.getString(prevNodeOffset, len);

			if (enumList == null)
				enumList = new Vector();

			enumList.addElement(nodeName);

			if (sp.lookingAtSpace(true))
				sp.skipPastSpaces();

			if (!sp.lookingAtChar('|', true)) {
				if (!sp.lookingAtChar(')', true)) {
					System.out.println("scanning enum values - error missing ')'"); //$NON-NLS-1$
					break;
				}
				break;
			}

			if (sp.lookingAtSpace(true))
				sp.skipPastSpaces();

			prevNodeOffset = sp.getCurrentOffset();
		}
		return enumList;
	}

	//
	// [10] AttValue ::= '"' ([^<&"] | Reference)* '"'
	// | "'" ([^<&'] | Reference)* "'"
	//
	/**
	 * Scan the default value in an attribute declaration
	 * 
	 * @param elementType
	 *            handle to the element that owns the attribute
	 * @param attrName
	 *            handle in the string pool for the attribute name
	 * @return handle in the string pool for the default attribute value
	 * @exception java.lang.Exception
	 */
	public String scanDefaultAttValue(StringParser sp) {
		String value = null;
		// System.out.println("scan default ATT Value... sp:" + sp.fData);

		sp.skipPastSpaces();

		boolean single;
		if (!(single = sp.lookingAtChar('\'', true)) && !sp.lookingAtChar('\"', true)) {
			return value;
		}

		char qchar = single ? '\'' : '\"';
		int sOffset = sp.getCurrentOffset();
		//BUG 203494 - Skip to the last occurrence of the qchar that way
		// all characters between are obtained
		sp.skipToLastOfChar(qchar, true);
		int len = sp.getCurrentOffset() - sOffset - 1;
		if (len == 0)
			value = ""; //$NON-NLS-1$
		else
			value = sp.getString(sOffset, len);

		return value;
	}

	//
	// [70] EntityDecl ::= GEDecl | PEDecl
	// [71] GEDecl ::= '<!ENTITY' S Name S EntityDef S? '>'
	// [72] PEDecl ::= '<!ENTITY' S '%' S Name S PEDef S? '>'
	// [73] EntityDef ::= EntityValue | (ExternalID NDataDecl?)
	// [74] PEDef ::= EntityValue | ExternalID
	// [75] ExternalID ::= 'SYSTEM' S SystemLiteral
	// | 'PUBLIC' S PubidLiteral S SystemLiteral
	// [76] NDataDecl ::= S 'NDATA' S Name
	// [9] EntityValue ::= '"' ([^%&"] | PEReference | Reference)* '"'
	// | "'" ([^%&'] | PEReference | Reference)* "'"
	//
	// Called after scanning 'ENTITY'
	//
	public EntityDecl scanEntityDecl() {
		prevNodeOffset = 1;
		nodeOffset = 1;
		int len;
		boolean isPEDecl = false;
		EntityDecl entityDecl = null;

		String name = null;
		String ndata = null;

		if (contentString.lookingAtSpace(true)) {
			contentString.skipPastSpaces();
			if (!contentString.lookingAtChar('%', true)) {
				isPEDecl = false; // <!ENTITY x "x">
			}
			else if (contentString.lookingAtSpace(true)) {
				isPEDecl = true; // <!ENTITY % x "x">
			}
			prevNodeOffset = contentString.getCurrentOffset();
			contentString.skipPastName(' ');
			nodeOffset = contentString.getCurrentOffset();
			len = nodeOffset - prevNodeOffset;
			if (len > 0) {
				name = contentString.getString(prevNodeOffset, len);
				prevNodeOffset = nodeOffset;
			}
		}

		if (name == null)
			return null;

		contentString.skipPastSpaces();
		prevNodeOffset = contentString.getCurrentOffset();

		boolean single;
		if ((single = contentString.lookingAtChar('\'', true)) || contentString.lookingAtChar('\"', true)) {
			String value = scanEntityValue(single);
			entityDecl = new EntityDecl(name, ownerDTD, value, isPEDecl);
		}
		else {
			// external entity
			ExternalID xID = scanExternalID();
			if (xID != null) {
				if (!isPEDecl) // general entity
				{
					boolean unparsed = false;
					contentString.skipPastSpaces();
					unparsed = contentString.skippedString(ndata_string);
					if (unparsed) {
						contentString.skipPastSpaces();
						prevNodeOffset = contentString.getCurrentOffset();
						contentString.skipPastName(' ');
						nodeOffset = contentString.getCurrentOffset();
						len = nodeOffset - prevNodeOffset;
						ndata = contentString.getString(prevNodeOffset, len);
					}
				}
				entityDecl = new EntityDecl(name, ownerDTD, xID, isPEDecl, ndata);
			}
		}
		return entityDecl;
	}


	//
	// [82] NotationDecl ::= '<!NOTATION' S Name S (ExternalID | PublicID) S?
	// '>'
	// [75] ExternalID ::= 'SYSTEM' S SystemLiteral
	// | 'PUBLIC' S PubidLiteral S SystemLiteral
	// [83] PublicID ::= 'PUBLIC' S PubidLiteral
	//
	// Called after scanning 'NOTATION'
	//
	public NotationDecl scanNotationDecl() {
		prevNodeOffset = 1;
		nodeOffset = 1;
		int len;
		NotationDecl notationDecl = null;

		String name = null;

		if (contentString.lookingAtSpace(true)) {
			contentString.skipPastSpaces();
			prevNodeOffset = contentString.getCurrentOffset();
			contentString.skipPastName(' ');
			nodeOffset = contentString.getCurrentOffset();
			len = nodeOffset - prevNodeOffset;
			if (len > 0) {
				name = contentString.getString(prevNodeOffset, len);
				prevNodeOffset = nodeOffset;
			}
		}

		if (name == null)
			return null;

		contentString.skipPastSpaces();
		prevNodeOffset = contentString.getCurrentOffset();

		notationDecl = new NotationDecl(name, ownerDTD);

		ExternalID xID = scanExternalID();
		if (xID != null) {
			if (xID.getSystemLiteral() != null) {
				notationDecl.setSystemId(xID.getSystemLiteral());
			}
			if (xID.getPubIdLiteral() != null) {
				notationDecl.setPublicId(xID.getPubIdLiteral());
			}
		}

		return notationDecl;
	}


	private String scanEntityValue(boolean single) {
		char qchar = single ? '\'' : '\"';
		prevNodeOffset = contentString.getCurrentOffset();
		contentString.skipToChar(qchar, false);
		int len = contentString.getCurrentOffset() - prevNodeOffset;
		String value = contentString.getString(prevNodeOffset, len);
		return value;
	}

	//
	// [75] ExternalID ::= 'SYSTEM' S SystemLiteral
	// | 'PUBLIC' S PubidLiteral S SystemLiteral
	private ExternalID scanExternalID() {
		ExternalID xID = null;
		char qchar = '\"';
		if (contentString.skippedString(system_string)) {
			if (!contentString.lookingAtSpace(true)) {
				return null;
			}
			contentString.skipPastSpaces();

			if (contentString.lookingAtChar('\'', true))
				qchar = '\'';
			else if (contentString.lookingAtChar('\"', true))
				qchar = '\"';

			prevNodeOffset = contentString.getCurrentOffset();
			contentString.skipToChar(qchar, true);
			int len = contentString.getCurrentOffset() - prevNodeOffset - 1;
			String systemId = contentString.getString(prevNodeOffset, len);
			// System.out.println("systemid..." + systemId);
			xID = new ExternalID(systemId);
		}
		else if (contentString.skippedString(public_string)) {
			if (!contentString.lookingAtSpace(true)) {
				return null;
			}

			contentString.skipPastSpaces();
			if (contentString.lookingAtChar('\'', true))
				qchar = '\'';
			else if (contentString.lookingAtChar('\"', true))
				qchar = '\"';

			// publicLiteral
			prevNodeOffset = contentString.getCurrentOffset();
			contentString.skipToChar(qchar, true);
			int len = contentString.getCurrentOffset() - prevNodeOffset - 1;
			String publicId = contentString.getString(prevNodeOffset, len);

			// systemLiteral
			contentString.skipPastSpaces();
			if (contentString.lookingAtChar('\'', true))
				qchar = '\'';
			else if (contentString.lookingAtChar('\"', true))
				qchar = '\"';
			prevNodeOffset = contentString.getCurrentOffset();
			contentString.skipToChar(qchar, true);
			len = contentString.getCurrentOffset() - prevNodeOffset - 1;

			if (len > 0) {
				String systemId = contentString.getString(prevNodeOffset, len);
				xID = new ExternalID(publicId, systemId);
			}
			else {
				xID = new ExternalID(publicId, null);
			}
		}
		return xID;
	}

	//
	// [83] PublicID ::= 'PUBLIC' S PubidLiteral
	//

	// dmw 11/15 private method never read locally
	// TODO: change to private if used, otherwise should be removed
	String scanPublicID() {
		String pID = null;
		if (contentString.skippedString(public_string)) {
			if (!contentString.lookingAtSpace(true)) {
				return pID;
			}
			contentString.skipPastSpaces();
			prevNodeOffset = contentString.getCurrentOffset();

			contentString.skipToChar(' ', true);
			int len = contentString.getCurrentOffset() - prevNodeOffset;
			pID = contentString.getString(prevNodeOffset, len);
		}

		return pID;
	}

	//
	// Main
	//

	/** Main program entry point. */
	public static void main(String argv[]) {

		// is there anything to do?
		if (argv.length == 0) {
			System.exit(1);
		}

		// DTDScanner sc = new DTDScanner(argv[0]);
		DTDScanner sc = new DTDScanner("hello.dtd", " gif SYSTEM \"GIF File\" "); //$NON-NLS-1$ //$NON-NLS-2$

		NotationDecl n = sc.scanNotationDecl();
		System.out.println("Noation Name: " + n.getNodeName()); //$NON-NLS-1$
		System.out.println("SystemId: " + n.getSystemId()); //$NON-NLS-1$


		// Attributes
		// Vector lists = sc.scanAttlistDecl();
		// sc.printAttList(lists);

		// System.out.println("CM: " + sc.scanContentModel());

	} // main(String[])

	/**
	 * Gets the errorString
	 * 
	 * @return Returns a String
	 */
	public String getErrorString() {
		return errorString;
	}

	/**
	 * Sets the errorString
	 * 
	 * @param errorString
	 *            The errorString to set
	 */
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}

}
