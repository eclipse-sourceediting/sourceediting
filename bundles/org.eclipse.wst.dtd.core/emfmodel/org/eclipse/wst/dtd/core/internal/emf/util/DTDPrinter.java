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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDSourceOffset;


public class DTDPrinter extends DTDVisitor {
	StringBuffer sb = new StringBuffer();
	boolean updateOffset = true;

	public DTDPrinter(boolean updateOffset) {
		this.updateOffset = updateOffset;
	}

	public StringBuffer getBuffer() {
		return sb;
	}

	public void visitDTDFile(DTDFile file) {
		super.visitDTDFile(file);
	}

	public void visitDTDNotation(DTDNotation notation) {
		generateComment(notation);
		updateStartOffset(notation, sb.length());
		sb.append("<!NOTATION "); //$NON-NLS-1$
		sb.append(notation.getName()).append(" "); //$NON-NLS-1$

		String publicID = notation.getPublicID();
		String systemID = notation.getSystemID();
		if (publicID == null || publicID.equals("")) { //$NON-NLS-1$
			sb.append("SYSTEM "); //$NON-NLS-1$
		}
		else {
			sb.append("PUBLIC \"").append(publicID).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (systemID == null) {
			sb.append("\"\""); //$NON-NLS-1$
		}
		else {
			sb.append("\"").append(systemID).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		endTag();
		updateEndOffset(notation, sb.length() - 1); // -1 for the newline char
		super.visitDTDNotation(notation);
	}

	public void visitDTDEntity(DTDEntity entity) {
		generateComment(entity);
		updateStartOffset(entity, sb.length());
		sb.append("<!ENTITY "); //$NON-NLS-1$

		if (entity.isParameterEntity())
			sb.append("% "); //$NON-NLS-1$

		sb.append(entity.getName()).append(" "); //$NON-NLS-1$
		sb.append(entity.getContent().unparse());

		endTag();
		updateEndOffset(entity, sb.length() - 1); // -1 for the newline char
		super.visitDTDEntity(entity);
	}

	public void visitDTDElement(DTDElement element) {
		generateComment(element);
		updateStartOffset(element, sb.length());
		sb.append("<!ELEMENT " + element.getName()); //$NON-NLS-1$
		DTDElementContent content = element.getContent();
		if (content instanceof DTDPCDataContent || content instanceof DTDElementReferenceContent) {
			sb.append(" ("); //$NON-NLS-1$
			super.visitDTDElement(element);
			sb.append(")"); //$NON-NLS-1$
		} // end of if ()
		else {
			sb.append(" "); //$NON-NLS-1$
			super.visitDTDElement(element);
		} // end of else
		endTag();
		updateEndOffset(element, sb.length() - 1); // -1 for the newline char
		visitAttributes(element);
	}

	public void visitDTDParameterEntityReference(DTDParameterEntityReference parmEntity) {
		generateComment(parmEntity);
		updateStartOffset(parmEntity, sb.length());
		sb.append("%" + parmEntity.getName() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		updateEndOffset(parmEntity, sb.length() - 1); // -1 for the newline
														// char
	}

	public void visitDTDElementContent(DTDElementContent content) {
		updateStartOffset(content, sb.length());
		String trailingChars = ""; //$NON-NLS-1$
		if (content instanceof DTDRepeatableContent) {
			DTDRepeatableContent repeatContent = (DTDRepeatableContent) content;
			DTDOccurrenceType occurrenceType = repeatContent.getOccurrence();
			// Integer occurrence = repeatContent.getOccurrence();
			if (occurrenceType != null) {
				int occurType = occurrenceType.getValue();
				if (occurType != DTDOccurrenceType.ONE) {
					if (repeatContent instanceof DTDEntityReferenceContent) {
						sb.append("("); //$NON-NLS-1$
						trailingChars = ")"; //$NON-NLS-1$
					}
					trailingChars += (char) occurType;
				}
			} // end of if ()
		} // end of if ()

		if (content instanceof DTDGroupContent) {
			super.visitDTDElementContent(content);
		} // end of if ()
		else if (content instanceof DTDElementReferenceContent || content instanceof DTDEntityReferenceContent) {
			sb.append(((DTDRepeatableContent) content).unparseRepeatableContent());
		} // end of if ()
		else {
			// handle DTDPCDataContent, DTDAnyContent and DTDEmptyContent here
			sb.append(content.getContentName());
		} // end of else
		sb.append(trailingChars);
		updateEndOffset(content, sb.length());
	}

	public void visitDTDGroupContent(DTDGroupContent group) {
		sb.append("("); //$NON-NLS-1$
		DTDGroupKind kind = group.getGroupKind();
		// MOF2EMF Port
		// Integer groupKind = group.getGroupKind();
		if (kind == null) {
			group.setGroupKind(DTDGroupKind.get(DTDGroupKind.SEQUENCE));
		} // end of if ()

		String con = group.getGroupKind().getValue() == DTDGroupKind.CHOICE ? " | " : ", "; //$NON-NLS-1$ //$NON-NLS-2$

		// Loop thru the children of the current group
		Collection content = group.getContent();
		if (content != null) {
			boolean firstContent = true;
			for (Iterator i = content.iterator(); i.hasNext();) {
				if (!firstContent) {
					sb.append(con);
				} // end of if ()
				else {
					firstContent = false;
				} // end of else
				visitDTDElementContent((DTDElementContent) i.next());
			}
		}

		sb.append(")"); //$NON-NLS-1$
	}

	private void visitAttributes(DTDElement elem) {
		Collection attrs = elem.getDTDAttribute();
		Iterator i = attrs.iterator();
		if (attrs != null && i.hasNext()) {
			DTDAttribute attrib = (DTDAttribute) i.next();
			String comment = attrib.getComment();
			if (comment != null && comment.length() > 0)
				sb.append("<!--\n ").append(comment).append("\n-->\n"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("<!ATTLIST " + elem.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(" "); //$NON-NLS-1$
			updateStartOffset(attrib, sb.length());
			sb.append(attrib.unparse());
			updateEndOffset(attrib, sb.length());
			sb.append("\n"); //$NON-NLS-1$
			for (; i.hasNext();) {
				attrib = (DTDAttribute) i.next();
				comment = attrib.getComment();
				if (comment != null && comment.length() > 0) {
					sb.append(">\n"); //$NON-NLS-1$
					if (comment != null && comment.length() > 0)
						sb.append("<!--\n ").append(comment).append("\n-->\n"); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append("<!ATTLIST " + elem.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				sb.append(" "); //$NON-NLS-1$
				updateStartOffset(attrib, sb.length());
				sb.append(attrib.unparse());
				updateEndOffset(attrib, sb.length());
				sb.append("\n"); //$NON-NLS-1$
			}
			sb.append(">\n"); //$NON-NLS-1$
		}
	}

	private void endTag() {
		sb.append(">\n"); //$NON-NLS-1$
	}

	private void updateStartOffset(DTDSourceOffset o, int offset) {
		if (updateOffset) {
			o.setStartOffset(offset);
		} // end of if ()
	}

	private void updateEndOffset(DTDSourceOffset o, int offset) {
		if (updateOffset) {
			o.setEndOffset(offset);
		} // end of if ()
	}

	private void generateComment(DTDObject dtdObject) {
		String commentString = null;

		if (dtdObject instanceof DTDElement)
			commentString = ((DTDElement) dtdObject).getComment();
		else if (dtdObject instanceof DTDEntity)
			commentString = ((DTDEntity) dtdObject).getComment();
		else if (dtdObject instanceof DTDNotation)
			commentString = ((DTDNotation) dtdObject).getComment();

		if (commentString != null && commentString.length() > 0) {
			sb.append("<!--").append(commentString).append("-->\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	// /**
	// * @generated
	// */
	// protected StringBuffer getBufferGen() {
	//
	// return sb;
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDFileGen(DTDFile file) {
	//
	// super.visitDTDFile(file);
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDNotationGen(DTDNotation notation) {
	//
	// generateComment(notation);
	// updateStartOffset(notation, sb.length());
	// sb.append("<!NOTATION ");
	// sb.append(notation.getName()).append(" ");
	//
	//
	// String publicID = notation.getPublicID();
	// String systemID = notation.getSystemID();
	// if (publicID == null || publicID.equals(""))
	// {
	// sb.append("SYSTEM ");
	// }
	// else
	// {
	// sb.append("PUBLIC \"").append(publicID).append("\" ");
	// }
	//
	//
	// if (systemID==null)
	// {
	// sb.append("\"\"");
	// }
	// else
	// {
	// sb.append("\"").append(systemID).append("\"");
	// }
	// endTag();
	// updateEndOffset(notation, sb.length() - 1); // -1 for the newline char
	// super.visitDTDNotation(notation);
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDEntityGen(DTDEntity entity) {
	//
	// generateComment(entity);
	// updateStartOffset(entity, sb.length());
	// sb.append("<!ENTITY ");
	//
	//
	// if (entity.isParameterEntity())
	// sb.append("% ");
	//
	//
	// sb.append(entity.getName()).append(" ");
	// sb.append(entity.getContent().unparse());
	//
	//
	// endTag();
	// updateEndOffset(entity, sb.length() - 1); // -1 for the newline char
	// super.visitDTDEntity(entity);
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDElementGen(DTDElement element) {
	//
	// generateComment(element);
	// updateStartOffset(element, sb.length());
	// sb.append("<!ELEMENT " + element.getName());
	// DTDElementContent content = element.getContent();
	// if (content instanceof DTDPCDataContent ||
	// content instanceof DTDElementReferenceContent)
	// {
	// sb.append(" (");
	// super.visitDTDElement(element);
	// sb.append(")");
	// } // end of if ()
	// else
	// {
	// sb.append(" ");
	// super.visitDTDElement(element);
	// } // end of else
	// endTag();
	// updateEndOffset(element, sb.length() - 1); // -1 for the newline char
	// visitAttributes(element);
	// }
	// /**
	// * @generated
	// */
	// protected void
	// visitDTDParameterEntityReferenceGen(DTDParameterEntityReference
	// parmEntity) {
	//
	// generateComment(parmEntity);
	// updateStartOffset(parmEntity, sb.length());
	// sb.append("%" + parmEntity.getName() + ";\n");
	// updateEndOffset(parmEntity, sb.length() - 1); // -1 for the newline
	// char
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDElementContentGen(DTDElementContent content) {
	//
	// updateStartOffset(content, sb.length());
	// String trailingChars = "";
	// if (content instanceof DTDRepeatableContent )
	// {
	// DTDRepeatableContent repeatContent = (DTDRepeatableContent) content;
	// Integer occurrence = repeatContent.getOccurrence();
	// if (occurrence != null)
	// {
	// int occurType = occurrence.intValue();
	// if (occurType != DTDOccurrenceType.ONE)
	// {
	// if (repeatContent instanceof DTDEntityReferenceContent)
	// {
	// sb.append("(");
	// trailingChars = ")";
	// }
	// trailingChars += (char) occurType;
	// }
	// } // end of if ()
	// } // end of if ()
	//    
	// if (content instanceof DTDGroupContent)
	// {
	// super.visitDTDElementContent(content);
	// } // end of if ()
	// else if (content instanceof DTDElementReferenceContent ||
	// content instanceof DTDEntityReferenceContent)
	// {
	// sb.append(((DTDRepeatableContent)content).unparseRepeatableContent());
	// } // end of if ()
	// else
	// {
	// // handle DTDPCDataContent, DTDAnyContent and DTDEmptyContent here
	// sb.append(content.getContentName());
	// } // end of else
	// sb.append(trailingChars);
	// updateEndOffset(content, sb.length());
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDGroupContentGen(DTDGroupContent group) {
	//
	// sb.append("(");
	//    
	// Integer groupKind = group.getGroupKind();
	// if (groupKind == null)
	// {
	// group.setGroupKind(DTDGroupKind.SEQUENCE);
	// } // end of if ()
	//    
	// String con = group.getGroupKind().intValue() == DTDGroupKind.CHOICE ? "
	// | " : ", ";
	//
	//
	// // Loop thru the children of the current group
	// Collection content = group.getContent();
	// if (content != null)
	// {
	// boolean firstContent = true;
	// for (Iterator i = content.iterator(); i.hasNext(); )
	// {
	// if (!firstContent)
	// {
	// sb.append(con);
	// } // end of if ()
	// else
	// {
	// firstContent = false;
	// } // end of else
	// visitDTDElementContent((DTDElementContent) i.next());
	// }
	// }
	//
	//
	// sb.append(")");
	// }
	// /**
	// * @generated
	// */
	// protected void visitAttributesGen(DTDElement elem) {
	//
	// Collection attrs = elem.getDTDAttribute();
	// Iterator i = attrs.iterator();
	// if (attrs != null && i.hasNext())
	// {
	// DTDAttribute attrib = (DTDAttribute) i.next();
	// String comment = attrib.getComment();
	// if (comment!=null && comment.length()>0)
	// sb.append("<!--\n ").append(comment).append("\n-->\n");
	// sb.append("<!ATTLIST " + elem.getName() + "\n");
	// sb.append(" ");
	// updateStartOffset(attrib, sb.length());
	// sb.append(attrib.unparse());
	// updateEndOffset(attrib, sb.length());
	// sb.append("\n");
	// for (; i.hasNext(); )
	// {
	// attrib = (DTDAttribute) i.next();
	// comment = attrib.getComment();
	// if (comment!=null && comment.length()>0)
	// {
	// sb.append(">\n");
	// if (comment!=null && comment.length()>0)
	// sb.append("<!--\n ").append(comment).append("\n-->\n");
	// sb.append("<!ATTLIST " + elem.getName() + "\n");
	// }
	// sb.append(" ");
	// updateStartOffset(attrib, sb.length());
	// sb.append(attrib.unparse());
	// updateEndOffset(attrib, sb.length());
	// sb.append("\n");
	// }
	// sb.append(">\n");
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void endTagGen() {
	//
	// sb.append(">\n");
	// }
	// /**
	// * @generated
	// */
	// protected void updateStartOffsetGen(DTDSourceOffset o, int offset) {
	//
	// if (updateOffset)
	// {
	// o.setStartOffset(offset);
	// } // end of if ()
	// }
	// /**
	// * @generated
	// */
	// protected void updateEndOffsetGen(DTDSourceOffset o, int offset) {
	//
	// if (updateOffset)
	// {
	// o.setEndOffset(offset);
	// } // end of if ()
	// }
	// /**
	// * @generated
	// */
	// protected void generateCommentGen(DTDObject dtdObject) {
	//
	// String commentString = null;
	//
	//
	// if (dtdObject instanceof DTDElement)
	// commentString = ((DTDElement)dtdObject).getComment();
	// else if (dtdObject instanceof DTDEntity)
	// commentString = ((DTDEntity)dtdObject).getComment();
	// else if (dtdObject instanceof DTDNotation)
	// commentString = ((DTDNotation)dtdObject).getComment();
	//
	//
	// if ( commentString!=null && commentString.length()>0)
	// {
	// sb.append("<!--").append(commentString).append("-->\n");
	// }
	// }
}// DTDPrinter
