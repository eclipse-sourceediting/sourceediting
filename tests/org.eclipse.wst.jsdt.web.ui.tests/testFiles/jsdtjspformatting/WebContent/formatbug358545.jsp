<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" 	import="com.eprice.data.*,
         com.eprice.data.PricingPlanAttribute.AttributeSourceEnum,
         com.eprice.data.Currency,
         com.epriceadmin.form.*,
         java.util.Iterator,      
         com.epriceadmin.service.PriceBookService,
         com.epriceadmin.data.*,
         com.eprice.data.pricebook.*,
         com.eprice.importsexports.data.AttributeValue,
         java.util.*,
	java.text.*"
%>
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="eprice" uri="WEB-INF/tld/eprice.tld"%>
<jsp:useBean id="AdminSession" scope="session" type="com.epriceadmin.data.AdminSession" />
<c:set var="activeForm" value="${AdminSession.activeForm}" scope="session" />
<script type="text/javascript">
	var oldValue = "";

	function copyPrices(prevValue) {
		var pricetxts = document.forms[0].priceField;
		if (isNaN(pricetxts[0].value)) {
			alert('Cannot copy non-numeric prices');
			return;
		}
		for ( var i = 0; i < pricetxts.length; i++) {
			if (!isNaN(pricetxts[0].value)) {
				pricetxts[i].value = pricetxts[0].value
				oldValue = prevValue
			}
		}

	}

	
	function controlForm() {
<%					if( priceBooksEditIndividualForm.getShowAttributes() != null ) {			%>
	document.getElementById("savePrices").className = "buttonDisabled";
		document.getElementById("savePrices").mouseover.className = "buttonDisabled";
<%		}			%>
	}

	function MM_findObj(n, d) { //v4.01
		var p, i, x;
		if (!d)
			d = document;
		if ((p = n.indexOf("?")) > 0 && parent.frames.length) {
			d = parent.frames[n.substring(p + 1)].document;
			n = n.substring(0, p);
		}
		if (!(x = d[n]) && d.all)
			x = d.all[n];
		for (i = 0; !x && i < d.forms.length; i++)
			x = d.forms[i][n];
		for (i = 0; !x && d.layers && i < d.layers.length; i++)
			x = MM_findObj(n, d.layers[i].document);
		if (!x && d.getElementById)
			x = d.getElementById(n);
		return x;
	}

	function MM_showHideLayers() { //v6.0
		var i, p, v, obj, args = MM_showHideLayers.arguments;
		for (i = 0; i < (args.length - 2); i += 3)
			if ((obj = MM_findObj(args[i])) != null) {
				v = args[i + 2];
				if (obj.style) {
					obj = obj.style;
					v = (v == 'show') ? 'visible' : (v == 'hide') ? 'hidden'
							: v;
				}
				obj.visibility = v;
			}
	}
	function verifyLocalAcessPLI() {
<% if( request.getAttribute( "LOCAL_DIVERSE_ACESS_PLI" ) != null ) {%>
	MM_showHideLayers('restrictLocalAccessPLIPopup', '', 'show');
<% }%>
	}
</script>
<title>ePrice Administration - Price Books - Edit Individual Prices</title>
</head>
<body onkeydown="selectSave()" onload="verifyLocalAcessPLI();">
	

	
</body>
</html>