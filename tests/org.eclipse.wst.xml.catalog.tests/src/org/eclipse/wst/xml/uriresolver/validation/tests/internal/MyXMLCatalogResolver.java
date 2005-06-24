package org.eclipse.wst.xml.uriresolver.validation.tests.internal;

import java.io.IOException;

import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;

public class MyXMLCatalogResolver extends XMLCatalogResolver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String resolveIdentifier(XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
	    String resolvedId = null;

        // The namespace is useful for resolving namespace aware
        // grammars such as XML schema. Let it take precedence over
        // the external identifier if one exists.
        String namespace = resourceIdentifier.getNamespace();
        if (namespace != null) {
            resolvedId = resolveURI(namespace);
        }
        
        // Resolve against an external identifier if one exists. This
        // is useful for resolving DTD external subsets and other 
        // external entities. For XML schemas if there was no namespace 
        // mapping we might be able to resolve a system identifier 
        // specified as a location hint.
        if (resolvedId == null) {
            String publicId = resourceIdentifier.getPublicId();
            String systemId = getUseLiteralSystemId() 
                ? resourceIdentifier.getLiteralSystemId()
                : resourceIdentifier.getExpandedSystemId();
            if (publicId != null && systemId != null) {
                resolvedId = resolvePublic(publicId, systemId);
            }
            else if (systemId != null) {
                resolvedId = resolveSystem(systemId);
            }
        }
        return resolvedId;
	}

}
