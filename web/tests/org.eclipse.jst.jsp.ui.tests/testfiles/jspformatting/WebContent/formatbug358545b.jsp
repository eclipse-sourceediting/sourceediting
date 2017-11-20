<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">

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