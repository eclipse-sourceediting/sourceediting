<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		
		<script type="text/javascript">
		<!--
			
			
			/*
			 *
			*/
			function invokeAutoAssign() {
			
				hideAllMenus();
				if(confirm('<scheduler:getString locale="locale1" key="INVOKE_AUTOASSIGN_CONFIRM_MSG" argument="<%= 5 %>" val="6/>j" />')) {
					document.form.<scheduler:getconstant key="ID_PARAM" />.value =9;
					}
			}

			
		//-->
		</script>
	</head>
	<body onload="javascript:initialize();" class="<jsp:getProperty name="dayCellBean" property="bodyClass" />">
		<div></div>
	</body>
</html>