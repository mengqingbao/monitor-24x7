
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib  prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>  
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
<title><tiles:getAsString name="htmlTitle"/></title>

<link rel="stylesheet" href="css/style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/jquery.tidy.table.min.css">
<link rel="stylesheet" type="text/css" href="css/tinyscrollbar.css">
<link rel="stylesheet" media="all" type="text/css" href="css/jquery-ui.css" />
<link rel="stylesheet" media="all" type="text/css" href="css/jquery-ui-timepicker-addon.css" />
		

<script src="js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="js/jquery-ui-sliderAccess.js"></script>
<script src="js/jquery.tidy.table.js"></script>
<script src="js/jquery.tinyscrollbar.js"></script>
<script src="js/jquery.jstree.js"></script>

</head>

<body>
	<div id="main_container">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="body" />
		<tiles:insertAttribute name="footer" />
	</div>
</body>
</html>
