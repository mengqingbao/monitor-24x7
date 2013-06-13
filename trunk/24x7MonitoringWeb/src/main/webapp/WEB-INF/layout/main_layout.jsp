
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib  prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>  
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
<title><tiles:getAsString name="htmlTitle"/></title>

<link rel="stylesheet" href="css/style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/jquery.tidy.table.min.css">
<link rel="stylesheet" type="text/css" href="css/tinyscrollbar.css">

<script src="js/jquery-1.9.1.min.js"></script>
<script src="js/jquery.tidy.table.min.js"></script>
<script src="js/jquery.tinyscrollbar.js"></script>
<script src="js/jquery.jstree.js"></script>
<script type="text/javascript" src="js/_lib/jquery.cookie.js"></script>
<script type="text/javascript" src="js/_lib/jquery.hotkeys.js"></script>
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/flot/excanvas.min.js"></script><![endif]-->   
<script type="text/javascript" src="js/flot/jquery.flot.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.time.js"></script>   
<script type="text/javascript" src="js/flot/jquery.flot.symbol.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.axislabels.js"></script>

</head>

<body>
	<div id="main_container">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="body" />
		<tiles:insertAttribute name="footer" />
	</div>
</body>
</html>
