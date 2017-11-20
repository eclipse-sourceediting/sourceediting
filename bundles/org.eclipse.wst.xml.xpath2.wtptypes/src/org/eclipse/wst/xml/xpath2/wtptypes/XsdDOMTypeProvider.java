package org.eclipse.wst.xml.xpath2.wtptypes;

import java.util.Collections;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl.XSDAttributeUseAdapter;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl.XSDSchemaAdapter;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XsdDOMTypeProvider {
	public TypeModel getTypeModel(final Document doc) {
		if (doc instanceof IDOMDocument) {
			return new XsdTypeModel((IDOMDocument)doc);
		}
		return null;
	}

	public interface WrappedCMQuery<R> {
		R query();
	}

	static public <R> R wrapCMQuery(CMDocumentManager documentManager, WrappedCMQuery<R> query) {
		boolean wasAsync = documentManager.getPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD);
		boolean wasAutoLoad = documentManager.getPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD);
		try {
			documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, false);
			documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, true);
			
			return query.query();
		} finally {
			documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, wasAsync);
			documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, wasAutoLoad);
		}
	}

	public static class XsdTypeModel implements TypeModel {
		private final ModelQueryAdapter modelAdapter;
		private final IDOMDocument doc;
	
		public XsdTypeModel(IDOMDocument doc) {
			this.doc = doc;
			modelAdapter = (ModelQueryAdapter) doc.getAdapterFor(ModelQueryAdapter.class);
		}
	
		private XSDTypeDefinition lookupXSDType(final String namespace, final String localName) {
			return wrapCMQuery(modelAdapter.getModelQuery().getCMDocumentManager(), new WrappedCMQuery<XSDTypeDefinition>() {
				public XSDTypeDefinition query() {
					
					CMDocument cmDoc = modelAdapter.getModelQuery().getCorrespondingCMDocument(doc.getDocumentElement());
					
					if (cmDoc instanceof XSDSchemaAdapter) {
						XSDSchema schema = (XSDSchema) ((XSDSchemaAdapter)cmDoc).getKey();
						XSDTypeDefinition typeDefinition = schema.resolveTypeDefinition(namespace, localName);
						if (typeDefinition.getBaseType() == null) return null; // crude check for on-the-fly created unresolved types 
						return typeDefinition;
					}
					return null;
				}
			});
		}
	
		public TypeDefinition lookupType(String namespace, String name) {
			XSDTypeDefinition xsdDef = lookupXSDType(namespace, name);
			return xsdDef != null ? new XsdTypeDefinition(xsdDef) : null;
		}
	
		public TypeDefinition lookupElementDeclaration(final String namespace,
				final String elementName) {
	
			return wrapCMQuery(modelAdapter.getModelQuery().getCMDocumentManager(), new WrappedCMQuery<XsdTypeDefinition>() {
				public XsdTypeDefinition query() {
					CMDocument cmDoc = modelAdapter.getModelQuery().getCorrespondingCMDocument(doc.getDocumentElement());
					if (cmDoc instanceof XSDSchemaAdapter) {
						XSDSchema schema = (XSDSchema) ((XSDSchemaAdapter)cmDoc).getKey();
						
						XSDElementDeclaration declaration = schema.resolveElementDeclaration(namespace, elementName);
						if (declaration != null) return new XsdTypeDefinition(declaration.getTypeDefinition());
					}
					return null;
				}
			});
		}
	
		public TypeDefinition lookupAttributeDeclaration(final String namespace,
				final String attributeName) {
			
			return wrapCMQuery(modelAdapter.getModelQuery().getCMDocumentManager(), new WrappedCMQuery<XsdTypeDefinition>() {
				public XsdTypeDefinition query() {
					CMDocument cmDoc = modelAdapter.getModelQuery().getCorrespondingCMDocument(doc.getDocumentElement());
					
					if (cmDoc instanceof XSDSchemaAdapter) {
						XSDSchema schema = (XSDSchema) ((XSDSchemaAdapter)cmDoc).getKey();
						
						XSDAttributeDeclaration declaration = schema.resolveAttributeDeclaration(namespace, attributeName);
						if (declaration != null) return new XsdTypeDefinition(declaration.getTypeDefinition());
					}
					return null;
				}
			});
		}
	
		public TypeDefinition getType(final Node node) {
			return wrapCMQuery(modelAdapter.getModelQuery().getCMDocumentManager(), new WrappedCMQuery<XsdTypeDefinition>() {
				public XsdTypeDefinition query() {
					if (node instanceof Attr) {
						CMAttributeDeclaration declaration = modelAdapter.getModelQuery().getCMAttributeDeclaration((Attr)node);
						if (declaration == null) {
							CMNode nodeDecl = modelAdapter.getModelQuery().getOrigin(node);
							if (nodeDecl instanceof CMAttributeDeclaration) declaration =  (CMAttributeDeclaration) nodeDecl;
						}							
						if (declaration instanceof XSDAttributeUseAdapter) {
							XSDAttributeUse au = (XSDAttributeUse)((XSDAttributeUseAdapter)declaration).getKey();
							return new XsdTypeDefinition(au.getAttributeDeclaration().getTypeDefinition());
						}
					} else if (node instanceof Element) {						
						CMElementDeclaration declaration = modelAdapter.getModelQuery().getCMElementDeclaration((Element)node);
						if (declaration == null) {
							CMNode nodeDecl = modelAdapter.getModelQuery().getOrigin(node);
							if (nodeDecl instanceof CMElementDeclaration) declaration = (CMElementDeclaration) nodeDecl;
						}
						if (declaration instanceof XSDElementDeclarationAdapter) {
							XSDElementDeclaration decl = ((XSDElementDeclaration)((XSDElementDeclarationAdapter)declaration).getKey()).getResolvedElementDeclaration();
							return new XsdTypeDefinition(decl.getTypeDefinition());
						}
					}
					return null;
				}
			});
		}
	
		public class XsdTypeDefinition implements TypeDefinition {
		
			private final XSDTypeDefinition typeDefinition;
		
			public XsdTypeDefinition(XSDTypeDefinition typeDefinition) {
				this.typeDefinition = typeDefinition;
			}
		
			public String getNamespace() {
				return typeDefinition.getTargetNamespace();
			}
		
			public String getName() {
				return getName();
			}
		
			public boolean isComplexType() {
				return typeDefinition instanceof XSDComplexTypeDefinition;
			}
		
			public TypeDefinition getBaseType() {
				XSDTypeDefinition base = null;
				
				if (typeDefinition instanceof XSDSimpleTypeDefinition) {
					base = ((XSDSimpleTypeDefinition)typeDefinition).getBaseTypeDefinition();
				} else if (typeDefinition instanceof XSDComplexTypeDefinition) {
					base = ((XSDComplexTypeDefinition)typeDefinition).getBaseTypeDefinition();
				}
				
				return (base != null) ? new XsdTypeDefinition(base) : null;
			}
		
			public TypeDefinition getSimpleType() {
				return null;
			}
					
			public boolean derivedFromType(TypeDefinition ancestorType,
					short derivationMethod) {
				if (ancestorType == null) throw new NullPointerException("ancestorType must be non-null"); //!NON-NLS-1
				
				XSDTypeDefinition xsdAncestor = mapXsdType(ancestorType);
				if (xsdAncestor == null) return false;
	
				return isDerivedFrom(xsdAncestor, this.typeDefinition, derivationMethod);
			}
	
			private XSDTypeDefinition mapXsdType(TypeDefinition ancestorType) {
				if (ancestorType instanceof XsdTypeDefinition) {
					return ((XsdTypeDefinition)ancestorType).typeDefinition;
				} else {
	 				// We should be able to handle the situation where we are passed a type from a different (kind of) type provider
					// We'll try to map this type to the name type in our system, and if that doesn't work, we can assume that there is no relationship...
					return lookupXSDType(ancestorType.getNamespace(), ancestorType.getName());
				}
			}
		
			public boolean derivedFrom(String namespace, String name,
					short derivationMethod) {
				XSDTypeDefinition ancestorType = lookupXSDType(namespace, name);
				if (ancestorType == null) return false;
				
				return isDerivedFrom(ancestorType, this.typeDefinition, derivationMethod);
			}

			public List<Short> getSimpleTypes(Attr attr) {
				return Collections.singletonList((short)0);
			}

			public List<Short> getSimpleTypes(Element attr) {
				return Collections.singletonList((short)0);
			}

			public Class getNativeType() {
				return null;
			}
		
		}
	}

	static boolean isDerivedFrom(XSDTypeDefinition base, XSDTypeDefinition derived, short derivationMethod) {
		if (base == derived) return true;
		
		// TODO: Check flags
		return false;
	}

	public TypeModel getTypeModel() {
		return null;
	}
}