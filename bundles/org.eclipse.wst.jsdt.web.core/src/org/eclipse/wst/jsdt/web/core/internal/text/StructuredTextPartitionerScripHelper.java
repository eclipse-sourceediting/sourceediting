/* Bradley Childs (childsb@us.ibm.com)
 * 
 * This partitioner is an 'intercepter' for a bigger, stronger partitioner. 
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.text;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.jsdt.web.core.internal.java.JsDataTypes;
import org.eclipse.wst.jsdt.web.core.internal.java.NodeHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.rules.IStructuredTypedRegion;
import org.eclipse.wst.sse.core.internal.text.rules.SimpleStructuredTypedRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class StructuredTextPartitionerScripHelper implements
		IStructuredTextPartitioner {

	private IDocument fInternalDocument;

	private IStructuredTextPartitioner fMasterPartitioner;

	private static Hashtable newPartitionType = new Hashtable();
	private static Hashtable contentTypeMap = new Hashtable();

	private String[] subPartitionType;

	public StructuredTextPartitionerScripHelper(
			IStructuredTextPartitioner masterPartitioner,
			String[] subPartitionType, String newPartitionType) {
		setMasterPartitioner(masterPartitioner);
		this.subPartitionType = subPartitionType;
		StructuredTextPartitionerScripHelper.newPartitionType.put(
				subPartitionType, newPartitionType);

	}

	public void setMasterPartitioner(
			IStructuredTextPartitioner embeddedPartitioner) {
		/*
		 * manage connected state of embedded partitioner
		 */
		if (fMasterPartitioner != null && fInternalDocument != null) {
			fMasterPartitioner.disconnect();
			contentTypeMap.remove(fMasterPartitioner);
		}

		this.fMasterPartitioner = embeddedPartitioner;

		if (fMasterPartitioner != null && fInternalDocument != null) {
			fMasterPartitioner.connect(fInternalDocument);

		}
		if (fMasterPartitioner != null) {
			try {
				Class[] parameterTypes = new Class[] {};
				Class c = fMasterPartitioner.getClass();
				Object[] arguments = new Object[] {};
				Method getConfiguredContentTypes = c.getMethod(
						"getConfiguredContentTypes", parameterTypes);
				String[] additionalContentTypes = (String[]) getConfiguredContentTypes
						.invoke(fMasterPartitioner, arguments);
				contentTypeMap.put(fMasterPartitioner, additionalContentTypes);
			} catch (Exception e) {
			}
		}
	}

	public static String[] getConfiguredContentTypes(
			IStructuredTextPartitioner partitioner) {
		return (String[]) contentTypeMap.get(partitioner);

	}

	public static String[] getConfiguredContentTypes() {

		/*
		 * return every content type support (may be MANY if used in multiple
		 * partition types.. This includes every static registerd master
		 * partitioner, and all of the new content types this classes brings
		 * along.
		 */

		Enumeration keys = contentTypeMap.keys();
		Vector contentTypes = new Vector();

		while (keys.hasMoreElements()) {
			String[] addContentTypes = (String[]) contentTypeMap.get(keys
					.nextElement());
			for (int i = 0; i < addContentTypes.length; i++) {
				if (addContentTypes[i] != null
						&& !addContentTypes[i].equals("")) {
					contentTypes.add(addContentTypes[i]);
				}
			}

		}

		keys = newPartitionType.keys();

		while (keys.hasMoreElements()) {
			String addContentTypes = (String) newPartitionType.get(keys
					.nextElement());
			if (addContentTypes != null && !addContentTypes.equals("")) {
				contentTypes.add(addContentTypes);
			}

		}
		for (int i = 0; i < contentTypes.size(); i++) {
			System.out.println(contentTypes.elementAt(i));
		}
		return (String[]) (contentTypes.toArray(new String[0]));
	}

	public StructuredTextPartitionerScripHelper() {
		/*
		 * Should read extension points here and dynamically set the 'master'
		 * partitioner but its hard coded to the JSP partitioner ATM.
		 */

		this(new StructuredTextPartitionerForJSP(),
				JsDataTypes.TAKEOVER_PARTITION_TYPES,
				JsDataTypes.NEW_PARTITION_TYPE);
	}

	public void setEmbeddedPartitioner(
			IStructuredTextPartitioner embeddedPartitioner) {
		/* Required from the JSP partitioner............... SHOULD REMOVE! */

		try {
			Class[] parameterTypes = new Class[] { IStructuredTextPartitioner.class };
			Class c = fMasterPartitioner.getClass();
			Object[] arguments = new Object[] { embeddedPartitioner };
			Method setLanguageMethod = c.getMethod("setEmbeddedPartitioner",
					parameterTypes);
			setLanguageMethod.invoke(fMasterPartitioner, arguments);
		} catch (Exception e) {
			// oops wrong class type
		}
	}

	public void setLanguage(String language) {
		try {
			Class[] parameterTypes = new Class[] { String.class };
			Class c = fMasterPartitioner.getClass();
			Object[] arguments = new Object[] { language };
			Method setLanguageMethod = c.getMethod("setLanguage",
					parameterTypes);
			setLanguageMethod.invoke(fMasterPartitioner, arguments);
		} catch (Exception e) {
			// oops wrong class type
		}
	}

	public void connect(IDocument document) {
		this.fInternalDocument = document;
		if (fMasterPartitioner != null) {
			fMasterPartitioner.connect(document);
		}
	}

	public IStructuredTypedRegion createPartition(int offset, int length,
			String partitionType) {
		// String parType =
		// isSubPartitionType(partitionType)?(String)newPartitionType.get(subPartitionType):partitionType;

		System.out.println("Called create partition on type " + partitionType);

		return fMasterPartitioner
				.createPartition(offset, length, partitionType);
	}

	public String getDefaultPartitionType() {
		String partitionType = fMasterPartitioner.getDefaultPartitionType();
		return isSubPartitionType(partitionType) ? (String) newPartitionType
				.get(subPartitionType) : partitionType;
	}

	public String getPartitionType(ITextRegion region, int offset) {

		String partitionType = fMasterPartitioner.getPartitionType(region,
				offset);
		System.out.println("Region Par Type:" + partitionType
				+ "------------------------------------------");

		System.out.println(fInternalDocument.get().substring(region.getStart(),
				region.getTextEnd()));

		System.out
				.println("/Region:--------------------------------------------");

		return isSubPartitionType(partitionType) ? (String) newPartitionType
				.get(subPartitionType) : partitionType;
	}

	public String getPartitionTypeBetween(
			IStructuredDocumentRegion previousNode,
			IStructuredDocumentRegion nextNode) {
		System.out.println("getPartitoinTypeBetween");
		String partitionType = fMasterPartitioner.getPartitionTypeBetween(
				previousNode, nextNode);
		if (isSubPartitionType(partitionType)) {
			return (String) newPartitionType.get(subPartitionType);
		} else {
			return partitionType;

		}
	}

	public ITypedRegion[] computePartitioning(int offset, int length) {
		//		
		// /* Look for EVENTs and label them SCRIPT partitions */
		//		
		// System.out.println("Compute Partitions--------");
		// System.out.println(fInternalDocument.get().substring(offset,offset+length));
		// Vector typedRegions = new Vector();
		//		
		// if(fInternalDocument instanceof IStructuredDocument){
		// IStructuredDocument structDoc =
		// (IStructuredDocument)(fInternalDocument);
		// IStructuredDocumentRegion[] regions =
		// structDoc.getStructuredDocumentRegions(offset, length);
		//			
		// if(regions==null) return new ITypedRegion[]{};
		// NodeHelper nh;
		//			
		//			
		// for(int i = 0;i<regions.length;i++){
		// if(regions[i].getType()==DOMRegionContext.XML_TAG_NAME && (nh = new
		// NodeHelper(regions[i])).containsAttribute(JsDataTypes.HTMLATREVENTS)){
		// // Found an event tag!
		// ITextRegionList t = regions[i].getRegions();
		// ITextRegion r;
		// Iterator regionIterator = t.iterator();
		// String tagAttrname = new String();
		// while(regionIterator.hasNext() ){
		// r = (ITextRegion)regionIterator.next();
		//						 
		// if(r.getType()== DOMRegionContext.XML_TAG_ATTRIBUTE_NAME){
		// tagAttrname = regions[i].getText().substring(r.getStart() ,
		// r.getTextEnd()).trim();
		//							 
		// }else if(r.getType()==DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE &&
		// nh.isInArray(JsDataTypes.HTMLATREVENTS, tagAttrname)){
		// System.out.println("Found script event type:--------");
		// System.out.println(regions[i].getText().substring(r.getStart() ,
		// r.getTextEnd()));
		// System.out.println("/Found script event type:--------");
		//							 
		// boolean isQuoted =
		// nh.isQuoted(regions[i].getText().substring(r.getStart() ,
		// r.getTextEnd()));
		//							
		// int quoteOffset = isQuoted?1:0;
		//							
		// typedRegions.add(new
		// SimpleStructuredTypedRegion(r.getStart()+quoteOffset,
		// r.getLength() - quoteOffset*2,
		// (String)newPartitionType.get(subPartitionType)));
		// createPartition(r.getStart()+quoteOffset,
		// r.getLength()- quoteOffset*2,
		// (String)newPartitionType.get(subPartitionType));
		//											     
		// createPartition( r.getStart()+quoteOffset,
		// r.getLength()- quoteOffset*2,
		// (String)newPartitionType.get(subPartitionType));
		// tagAttrname = new String();
		//							 
		// /* Add two arbitrary 1 char positions if quoted */
		//							 
		// if(isQuoted){
		//								
		// typedRegions.addAll(Arrays.asList(fMasterPartitioner.computePartitioning(r.getStart(),1)));
		// typedRegions.addAll(Arrays.asList(fMasterPartitioner.computePartitioning(r.getTextEnd(),1)));
		//								 
		//							
		// }
		//								
		//							   	
		// }else{
		//							
		// typedRegions.addAll(Arrays.asList(fMasterPartitioner.computePartitioning(r.getStart(),r.getLength())));
		// }
		//						
		//						
		// }
		//					
		// }else{
		// // not a name tag so compute its partition
		// /*
		// typedRegions.add(new
		// SimpleStructuredTypedRegion(regions[i].getStart(),
		// regions[i].getLength(),
		// (String)newPartitionType.get(subPartitionType)));
		// createPartition(regions[i].getStart(),
		// regions[i].getLength(),
		// (String)newPartitionType.get(subPartitionType));
		//						
		//						
		// */
		//					
		// // Let somebody else compute this partition type
		// typedRegions.addAll(Arrays.asList(fMasterPartitioner.computePartitioning(regions[i].getStart(),regions[i].getLength())));
		// }
		//				
		// } // End for loop itterating over all sub regions in this region.
		//			
		// }
		//		
		// System.out.println("/Compute Partitions--------");
		return fMasterPartitioner.computePartitioning(offset, length);
		// return (ITypedRegion[])typedRegions.toArray(new ITypedRegion[]{});

	}

	public void disconnect() {
		fMasterPartitioner.disconnect();

	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		fMasterPartitioner.documentAboutToBeChanged(event);

	}

	public boolean documentChanged(DocumentEvent event) {
		return fMasterPartitioner.documentChanged(event);
	}

	public String getContentType(int offset) {
		System.out.println("Queried for content type");

		if (!(fInternalDocument instanceof IStructuredDocument)) {
			return fMasterPartitioner.getContentType(offset);
		}

		IStructuredDocument structDoc = (IStructuredDocument) (fInternalDocument);
		IStructuredDocumentRegion structuredDocRegion = structDoc
				.getRegionAtCharacterOffset(offset);

		ITextRegion currentRegion = structuredDocRegion
				.getRegionAtCharacterOffset(offset);

		/* If its not an XML_TAG_ATTRIBUTE inspect no further */
		if (currentRegion.getType() != DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			return fMasterPartitioner.getContentType(offset);
		}

		/* make sure we're not at a quoted character */
		if (structDoc.get().charAt(offset) == '\''
				| structDoc.get().charAt(offset) == '"') {
			return fMasterPartitioner.getContentType(offset);
		}

		/*
		 * If we've fallen through to here, then we're in a
		 * XML_TAG_ATTRIBUTE_VALUE need to check if this event type tag
		 */

		ITextRegionList regionList = structuredDocRegion.getRegions();

		int currentIndex = regionList.indexOf(currentRegion);

		/*
		 * We determinate attrib name 2 previous regions: Node
		 * Type:XML_TAG_ATTRIBUTE_NAME Node Type:XML_TAG_ATTRIBUTE_EQUALS Node
		 * Type:XML_TAG_ATTRIBUTE_VALUE
		 */

		if ((currentIndex - 2) < 0) {
			return fMasterPartitioner.getContentType(offset);
		}

		ITextRegion tagAttrNameRegion = regionList.get(currentIndex - 2);

		String tagAttrName = structuredDocRegion.getText().substring(
				tagAttrNameRegion.getStart(), tagAttrNameRegion.getTextEnd())
				.trim();

		if (NodeHelper.isInArray(JsDataTypes.HTMLATREVENTS, tagAttrName)) {
			System.out.println("returning JS content type");
			return (String) newPartitionType.get(subPartitionType);
		}

		return fMasterPartitioner.getContentType(offset);

	}

	public String[] getLegalContentTypes() {
		return fMasterPartitioner.getLegalContentTypes();
	}

	public ITypedRegion getPartition(int offset) {

		if (getContentType(offset) != (String) newPartitionType
				.get(subPartitionType)) {
			return fMasterPartitioner.getPartition(offset);
		}

		if (!(fInternalDocument instanceof IStructuredDocument)) {
			return fMasterPartitioner.getPartition(offset);
		}

		IStructuredDocument structDoc = (IStructuredDocument) (fInternalDocument);
		IStructuredDocumentRegion structuredDocRegion = structDoc
				.getRegionAtCharacterOffset(offset);

		ITextRegion currentRegion = structuredDocRegion
				.getRegionAtCharacterOffset(offset);
		boolean isAttrQuoted = NodeHelper.isQuoted(structuredDocRegion
				.getText().substring(currentRegion.getStart(),
						currentRegion.getTextEnd()));

		int quoteOffset = isAttrQuoted ? 1 : 0;

		ITypedRegion jsTypedRegion = new SimpleStructuredTypedRegion(
				currentRegion.getStart() + quoteOffset, currentRegion
						.getLength()
						- quoteOffset * 2, (String) newPartitionType
						.get(subPartitionType));

		createPartition(jsTypedRegion.getOffset(), jsTypedRegion.getLength(),
				jsTypedRegion.getType());

		return jsTypedRegion;

	}

	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitionerForJSP();
	}

	private boolean isSubPartitionType(String type) {
		return NodeHelper.isInArray(subPartitionType, type);
	}

	@Override
	public void finalize() {
		if (newPartitionType != null) {
			newPartitionType.remove(subPartitionType);
		}
		if (contentTypeMap != null) {
			contentTypeMap.remove(fMasterPartitioner);
		}
	}

}
