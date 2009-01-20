/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.types;

import org.eclipse.wst.xml.xpath2.processor.*;
/**
 * A representation of the Integer datatype
 */
public class XSInteger extends XSDecimal {

	private int _value;
	/**
	 * Initialises a representation of 0
	 */
	public XSInteger() {
		this(0);
	}
	/**
	 * Initialises a representation of the supplied integer
	 * @param x Integer to be stored
	 */
	public XSInteger(int x) {
		super(x);
		_value = x;
	}
	/**
	 * Retrieves the datatype's full pathname
	 * @return "xs:integer" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:integer";
	}
	/**
	 * Retrieves the datatype's name
	 * @return "integer" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "integer";
	}
	/**
	 * Retrieves a String representation of the integer stored
	 * @return String representation of the integer stored
	 */	@Override
	public String string_value() {
		return ""+_value;
	}
	 /**
	  * Check whether the integer represented is 0
	  * @return True is the integer represented is 0. False otherwise
	  */
	@Override
	public boolean zero() {
		return _value == 0;
	}
		/**
		 * Creates a new ResultSequence consisting of the extractable integer in the 
		 * supplied ResultSequence
		 * @param arg The ResultSequence from which the integer is to be extracted
		 * @return New ResultSequence consisting of the integer supplied
		 * @throws DynamicError
		 */
        @Override
		public ResultSequence constructor(ResultSequence arg) throws DynamicError {
                ResultSequence rs = ResultSequenceFactory.create_new();

                if(arg.empty())
                        return rs;

                AnyAtomicType aat = (AnyAtomicType) arg.first();
                
                try {   
                        Integer i = new Integer(aat.string_value());
                        rs.add(new XSInteger(i.intValue()));
                        return rs;
                } catch(NumberFormatException e) {
                        throw DynamicError.cant_cast(null);
                }
         
        }


    /**
     * Retrieves the actual integer value stored
     * @return The actual integer value stored
     */
	public int int_value() { return _value; }
	/**
	 * Sets the integer stored to that supplied
	 * @param x Integer to be stored
	 */
	public void set_int(int x) {
		_value = x;
		set_double(x);
	}

		/**
		 * Mathematical addition operator between this XSInteger and the supplied ResultSequence. Due to no numeric type promotion or
		 * conversion, the ResultSequence must be of type XSInteger.
		 * @param arg The ResultSequence to perform an addition with
		 * @return A XSInteger consisting of the result of the mathematical addition.
		 */
        @Override
		public ResultSequence plus(ResultSequence arg) throws DynamicError {
                AnyType at = get_single_arg(arg);
                if( !(at instanceof XSInteger) )
                        DynamicError.throw_type_error();
                XSInteger val = (XSInteger) at;

                return ResultSequenceFactory.create_new(new XSInteger(int_value() + val.int_value()));
        }
        /**
		 * Mathematical subtraction operator between this XSInteger and the supplied ResultSequence. Due to no numeric type promotion or
		 * conversion, the ResultSequence must be of type XSInteger.
		 * @param arg The ResultSequence to perform a subtraction with
		 * @return A XSInteger consisting of the result of the mathematical subtraction.
		 */
        @Override
		public ResultSequence minus(ResultSequence arg) throws DynamicError {
                XSInteger val = (XSInteger) get_single_type(arg, XSInteger.class);
                return ResultSequenceFactory.create_new(new XSInteger(int_value() - val.int_value()));
        }
        /**
		 * Mathematical multiplication operator between this XSInteger and the supplied ResultSequence. Due to no numeric type promotion or
		 * conversion, the ResultSequence must be of type XSInteger.
		 * @param arg The ResultSequence to perform a multiplication with
		 * @return A XSInteger consisting of the result of the mathematical multiplication.
		 */
        @Override
		public ResultSequence times(ResultSequence arg) throws DynamicError {
                XSInteger val = (XSInteger) get_single_type(arg, XSInteger.class);
                return ResultSequenceFactory.create_new(new XSInteger(int_value() * val.int_value()));
        }
        /**
		 * Mathematical modulus operator between this XSInteger and the supplied ResultSequence. Due to no numeric type promotion or
		 * conversion, the ResultSequence must be of type XSInteger.
		 * @param arg The ResultSequence to perform a modulus with
		 * @return A XSInteger consisting of the result of the mathematical modulus.
		 */
        @Override
		public ResultSequence mod(ResultSequence arg) throws DynamicError {
                XSInteger val = (XSInteger) get_single_type(arg, XSInteger.class);
                return ResultSequenceFactory.create_new(new XSInteger(int_value() % val.int_value()));
        }
        /**
         * Negates the integer stored
         * @return New XSInteger representing the negation of the integer stored
         */
        @Override
		public ResultSequence unary_minus() {
                return ResultSequenceFactory.create_new(new XSInteger(-1*int_value()));
        }

        /**
         * Absolutes the integer stored
         * @return New XSInteger representing the abolsute of the integer stored
         */
        @Override
		public NumericType abs() {
                return new XSInteger(Math.abs(int_value()));
        }

}
