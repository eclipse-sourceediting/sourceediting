/*******************************************************************************
 * Copyright (c) 2010 Mukul Gandhi, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mukul Gandhi - initial API and implementation
 *     Mukul Gandhi - bug 318313 - improvements to computation of typed values of nodes,
 *                                 when validated by XML Schema primitive types
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A factory class implementation, to construct PsychoPath schema type representation
 * corresponding to XML Schema types.
 */
public class SchemaTypeValueFactory {

	public static AnyType newSchemaTypeValue(String typeName, String strValue) {
		
		if (XSConstants.ANY_URI.equals(typeName)) {
			return new XSAnyURI(strValue);
		}
		
		if (XSConstants.BOOLEAN.equals(typeName)) {
			return new XSBoolean(Boolean.valueOf(strValue).
					                 booleanValue());
		}
		
		if (XSConstants.DATE.equals(typeName)) {       
			return XSDate.parse_date(strValue);
		}
		
		if (XSConstants.DATE_TIME.equals(typeName)) {
			return XSDateTime.parseDateTime(strValue);
		}
		
		// decimal and it's subtypes		
		if (XSConstants.DECIMAL.equals(typeName)) {      
			return new XSDecimal(new BigDecimal(strValue));
		}
		
		if (XSConstants.INTEGER.equals(typeName)) {      
			return new XSInteger(new BigInteger(strValue));
		}
		
		if (XSConstants.LONG.equals(typeName)) {     
			return new XSLong(new BigInteger(strValue));
		}
		
		if (XSConstants.INT.equals(typeName)) {      
			return new XSInt(new BigInteger(strValue));
		}
		
		if (XSConstants.SHORT.equals(typeName)) {      
			return new XSShort(new BigInteger(strValue));
		}
		
		if (XSConstants.BYTE.equals(typeName)) {      
			return new XSByte(new BigInteger(strValue));
		}
		
		if (XSConstants.NON_NEGATIVE_INTEGER.equals(typeName)) {      
			return new XSNonNegativeInteger(new BigInteger(strValue));
		}
		
		if (XSConstants.POSITIVE_INTEGER.equals(typeName)) {      
			return new XSPositiveInteger(new BigInteger(strValue));
		}
		
		if (XSConstants.UNSIGNED_LONG.equals(typeName)) {      
			return new XSUnsignedLong(new BigInteger(strValue));
		}
		
		if (XSConstants.UNSIGNED_INT.equals(typeName)) {      
			return new XSUnsignedInt(new BigInteger(strValue));
		}
		
		if (XSConstants.UNSIGNED_SHORT.equals(typeName)) {      
			return new XSUnsignedShort(new BigInteger(strValue));
		}
		
		if (XSConstants.UNSIGNED_BYTE.equals(typeName)) {      
			return new XSUnsignedByte(new BigInteger(strValue));
		}
		
		if (XSConstants.NON_POSITIVE_INTEGER.equals(typeName)) {      
			return new XSNonPositiveInteger(new BigInteger(strValue));
		}
		
		if (XSConstants.NEGATIVE_INTEGER.equals(typeName)) {      
			return new XSNegativeInteger(new BigInteger(strValue));
		}
		// end of, decimal types
		
		if (XSConstants.DOUBLE.equals(typeName)) {       
			return new XSDouble(Double.parseDouble(strValue));
		}
		
		// duration and it's subtypes
		if (XSConstants.DURATION.equals(typeName)) {       
			return XSDuration.parseDTDuration(strValue);
		}
		
		if (XSConstants.DAY_TIME_DURATION.equals(typeName)) {       
			return XSDayTimeDuration.parseDTDuration(strValue);
		}
		
		if (XSConstants.YEAR_MONTH_DURATION.equals(typeName)) {       
			return XSYearMonthDuration.parseYMDuration(strValue);
		}
		// end of, duration types
		
		if (XSConstants.FLOAT.equals(typeName)) {        
			return new XSFloat(Float.parseFloat(strValue));
		}
		
		if (XSConstants.G_DAY.equals(typeName)) {        
			return XSGDay.parse_gDay(strValue);
		}
		
		if (XSConstants.G_MONTH.equals(typeName)) {        
			return XSGMonth.parse_gMonth(strValue);
		}
		
		if (XSConstants.G_MONTH_DAY.equals(typeName)) {        
			return XSGMonthDay.parse_gMonthDay(strValue);
		}
		
		if (XSConstants.G_YEAR.equals(typeName)) {        
			return XSGYear.parse_gYear(strValue);
		}
		
		if (XSConstants.G_YEAR_MONTH.equals(typeName)) {        
			return XSGYearMonth.parse_gYearMonth(strValue);
		}
		
		if (XSConstants.NOTATION.equals(typeName)) {
			return new XSString(strValue);
		}
		
		if (XSConstants.Q_NAME.equals(typeName)) {
			return QName.parse_QName(strValue);
		}
		
		if (XSConstants.STRING.equals(typeName)) {
			return new XSString(strValue);   
		}                        
		
		if (XSConstants.TIME.equals(typeName)) {
			return XSTime.parse_time(strValue);
		}  
		
	    // create a XSString value, as fallback option 
		return new XSString(strValue);
		
	}
}