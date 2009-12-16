package org.eclipse.wst.xml.xpath.ui.views;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;

public class DefaultNamespaceContext implements NamespaceContext {

	List<NamespaceInfo> nsinfoList;
	public DefaultNamespaceContext(List<NamespaceInfo> namespaceInfo) {
		nsinfoList = namespaceInfo;
	}
	
	public String getNamespaceURI(String prefix) {
		for (NamespaceInfo ni : nsinfoList) {
			if (prefix.equals(ni.prefix)) {
				return ni.uri;
			}
		}
		return null;
	}

	public String getPrefix(String uri) {
		// Should be same as getPrefixes(uri).get(0)
		for (NamespaceInfo ni : nsinfoList) {
			if (uri.equals(ni.uri)) {
				return ni.prefix;
			}
		}
		return null;
	}

	public Iterator<String> getPrefixes(String uri) {
		List<String> prefixes = new LinkedList<String>();
		for (NamespaceInfo ni : nsinfoList) {
			if (uri.equals(ni.uri)) {
				prefixes.add(ni.prefix);
			}
		}
		return prefixes.iterator();
	}
}