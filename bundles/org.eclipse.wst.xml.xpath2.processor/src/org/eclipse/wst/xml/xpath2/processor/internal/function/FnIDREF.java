/*******************************************************************************
 * Copyright (c) 2009 Standard for Technology in Automotive Retail, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   David Carver (STAR) - bug 281168 - initial API and implementation
 *     David Carver  - bug 281186 - implementation of fn:id and fn:idref
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * 
 */
public class FnIDREF extends Function {
	private static Collection _expected_args = null;
	
	/**
	 * Constructor for FnInsertBefore.
	 */
	public FnIDREF() {
		super(new QName("idref"), 1, 2);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return idref(args, dynamic_context(), dynamic_context());
	}

	/**
	 * Insert-Before operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param dc 
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:insert-before operation.
	 */
	public static ResultSequence idref(Collection args, DynamicContext context, DynamicContext dc) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();
		
		Iterator argIt = cargs.iterator();
		ResultSequence idrefRS = (ResultSequence) argIt.next();
		String[] idst = idrefRS.first().string_value().split(" ");

		ArrayList ids = createIDs(idst);
		ResultSequence nodeArg = null;
		NodeType nodeType = null;
		if (argIt.hasNext()) {
			nodeArg = (ResultSequence) argIt.next();
			nodeType = (NodeType)nodeArg.first();
		} else {
			if (context.context_item() == null) {
				throw DynamicError.contextUndefined();
			}
			if (!(context.context_item() instanceof NodeType)) {
				throw new DynamicError(TypeError.invalid_type(null));
			}
			nodeType = (NodeType) context.context_item();
			if (nodeType.node_value().getOwnerDocument() == null) {
				throw DynamicError.contextUndefined();
			}
		}
		
		Node node = nodeType.node_value();
		if (node.getOwnerDocument() == null) {
			// W3C test suite seems to want XPDY0002 here
			throw DynamicError.contextUndefined();
			//throw DynamicError.noContextDoc();
		}
		
		if (hasID(ids, node)) {
			ElementType element = new ElementType((Element) node, dc.getTypeModel(node));
			rs.add(element);
		}
		
		rs = processAttributes(node, ids, rs, dc);
		rs = processChildNodes(node, ids, rs, dc);

		return rs;
	}
	
	private static ArrayList createIDs(String[] idtokens) {
		ArrayList xsid = new ArrayList();
		for (int i = 0; i < idtokens.length; i++) {
			XSID id = new XSID(idtokens[i]);
			xsid.add(id);
		}
		return xsid;
	}
	
	private static ResultSequence processChildNodes(Node node, List ids, ResultSequence rs, DynamicContext dc) {
		if (!node.hasChildNodes()) {
			return rs;
		}
		
		NodeList nodeList = node.getChildNodes();
		for (int nodecnt = 0; nodecnt < nodeList.getLength(); nodecnt++) {
			Node childNode = nodeList.item(nodecnt);
			if (childNode.getNodeType() == Node.ELEMENT_NODE && !isDuplicate(childNode, rs)) {
				ElementType element = new ElementType((Element)childNode, dc.getTypeModel(node));
				if (element.isIDREF()) {
					if (hasID(ids, childNode)) {
						rs.add(element);
					}
				} 
				rs = processAttributes(childNode, ids, rs, dc);
				rs = processChildNodes(childNode, ids, rs, dc);
			}
		}
		
		return rs;

	}
	
	private static ResultSequence processAttributes(Node node, List idrefs, ResultSequence rs, DynamicContext dc) {
		if (!node.hasAttributes()) {
			return rs;
		}
		
		NamedNodeMap attributeList = node.getAttributes();
		for (int atsub = 0; atsub < attributeList.getLength(); atsub++) {
			Attr atNode = (Attr) attributeList.item(atsub);
			NodeType atType = new AttrType(atNode, dc.getTypeModel(atNode));
			if (atType.isID()) {
				if (hasID(idrefs, atNode)) {
					if (!isDuplicate(node, rs)) {
						ElementType element = new ElementType((Element)node, dc.getTypeModel(node));
						rs.add(element);
					}
				}
			}
		}
		return rs;
	}
	
	private static boolean hasID(List ids, Node node) {
		for (int i = 0; i < ids.size(); i++) {
			XSID idref = (XSID) ids.get(i);
			if (idref.string_value().equals(node.getNodeValue())) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isDuplicate(Node node, ResultSequence rs) {
		Iterator it = rs.iterator();
		while (it.hasNext()) {
			if (it.next().equals(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(new XSString(), SeqType.OCC_STAR);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(SeqType.OCC_NONE));
		}

		return _expected_args;
	}
	
}
