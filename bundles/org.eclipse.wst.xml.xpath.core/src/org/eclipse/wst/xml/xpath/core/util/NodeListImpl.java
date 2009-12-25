package org.eclipse.wst.xml.xpath.core.util;

import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @since 1.1
 */
public class NodeListImpl implements NodeList {

	ResultSequence rs;
	
	public NodeListImpl(ResultSequence result) {
		rs = result;
	}
	
	public int getLength() {
		return rs.size();
	}

	public Node item(int arg0) {
		AnyType type = rs.get(arg0);
		if (type instanceof NodeType) {
			NodeType nodeType = (NodeType) type;
			return nodeType.node_value();
		}
		return null;
	}
}
