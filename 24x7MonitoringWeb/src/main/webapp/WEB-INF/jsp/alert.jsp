<div class="main_content">
	<script type="text/javascript">
		
		
	    function setDefaultTextEvent() {
	    	$(".defaultText").focus(function(arg) {
   		        if ($(this).val() == $(this)[0].title){
   		            $(this).removeClass("defaultTextActive");
   		            $(this).val("");
   		        }
   		    });
   		    
   		    $(".defaultText").blur(function(){
   		        if ($(this).val() == ""){
   		            $(this).addClass("defaultTextActive");
   		            $(this).val($(this)[0].title);
   		        }
   		    });
   		    
   		    $(".defaultText").blur(); 
	    	
	    }
	    
	
		function createNodeElement(name, anchor, prefix) {
			
			var li = $(document.createElement('li'));
			li.attr('id', name);
			var $img = $(document.createElement('img'));
			$img.attr('src', 'images/folder.png');
			$img.attr('style', 'margin-right: 5px;');
			li.append($img);
			var nodeText = "";
			if(anchor) {
				nodeText += '<a href="#">' + name + '</a>';
			}
			else {
				nodeText = "<span>" + name + "</span>";
			}
			
			li.append(nodeText);
			return li;
		}
		
		function createMethodsTree(ul, array) {
			$.each(array, function(index, item) {
			    var li = $(document.createElement('li'));
				li.attr("id", item.fullName);
				var $img = $(document.createElement('img'));
				var $isClass = true;
				$.each(item.subItems, function(i, value) {
					if(value.subItems.length > 0) {
						$isClass = false;
						return;
					}
				});
				 if(item.subItems.length == 0) {
					$img.attr('src', 'images/method.png');
				}
				else if(item.subItems[0].itemName.indexOf("()") >= 0) {
					$img.attr('src', 'images/class.png');
				}
				
				else {
					$img.attr('src', 'images/folder.png');
				}
				$img.attr('style', 'margin-right: 5px;');
				li.append($img);
				li.append('<a href="#"><span style="display:none">' + item.fullName + '</span>' + item.itemName + '</a>');
				if(item.subItems.length > 0 ) {
				    var ul2 = $('<ul>').appendTo(li);
					createMethodsTree(ul2, item.subItems);
	 			}
				ul.append(li);
			});
			
		}
		
		function createSQLTree(ul, array) {
			
			var $insertLi = createNodeElement("INSERT", true, "SQL");
			var $selectLi = createNodeElement("SELECT", true, "SQL");
			var $updateLi = createNodeElement("UPDATE", true, "SQL");
			var $deleteLi = createNodeElement("DELETE", true, "SQL");
			var $otherLi = createNodeElement("OTHER", true);
			var $insert = $('<ul>').appendTo($insertLi);
			var $select = $('<ul>').appendTo($selectLi);
			var $update = $('<ul>').appendTo($deleteLi);
			var $delete = $('<ul>').appendTo($deleteLi);
			var $other = $('<ul>').appendTo($otherLi);
	
			$.each(array, function(index, item) {
			    var li = $(document.createElement('li'));
				li.attr("id", item);
				var $img = $(document.createElement('img'));
				$img.attr('src', 'images/sql.png');
				$img.attr('style', 'margin-right: 5px;');
				li.append($img);
				li.append('<a href="#" title="' + item + '"><span style="display:none">' + item + '</span>' + item.substring(0, 30) + '...</a>');
				
				if(/^SELECT/i.test(item)) { // if start with SELECT ( i for case insensitive)
					$select.append(li);
				}
				else if(/^UPDATE/i.test(item)) { 
					$update.append(li);
				}
				else if(/^DELETE/i.test(item)) {
					$delete.append(li);
				}
				else if(/^INSERT/i.test(item)) {
					$insert.append(li);
				}
				else {
					$other.append(li);
				}
				
			});
			ul.append($insertLi);
			ul.append($selectLi);
			ul.append($updateLi);
			ul.append($deleteLi);
			ul.append($otherLi);
		}
		
		function createHttpRequestTree(ul, array) {
			$.each(array, function(index, item) {
			    var li = $(document.createElement('li'));
				li.attr("id", item);
				var $img = $(document.createElement('img'));
				$img.attr('src', 'images/request.png');
				$img.attr('style', 'margin-right: 5px;');
				li.append($img);
				li.append('<a href="#" title="' + item + '"><span style="display:none">' + item + '</span>' + item.substring(0, 30) + '...</a>');
				ul.append(li);
				
			});
		}
		
		function retrieveTracedItems() {
			var height = $(".center_content").css("height");
			$("#ajax_box").css("height", height);
			$("#ajax_box").show();
			
			
			$.ajax
			(
				{
				  url:"json/getMonitoredItems", 
				  type: "GET",  
				  data: "",
				  complete: function(jsonResponse) {
						var objectArray = jQuery.parseJSON(jsonResponse.responseText);
						var tracedMethods = objectArray['tracedMethods'];
						var tracedQueries = objectArray['tracedQueries'];
						var tracedRequestUrls = objectArray['HttpRequestUrls'];
						
						var ul = $(document.createElement('ul'));
						
						var javaLi = createNodeElement("java", true);
						var javaUl = $('<ul>').appendTo(javaLi);
						createMethodsTree(javaUl, tracedMethods);
						ul.append(javaLi);
						
						var sqlLi = createNodeElement("SQL", true);
						var sqlUl = $('<ul>').appendTo(sqlLi);
						createSQLTree(sqlUl, tracedQueries);   
						ul.append(sqlLi);
					
						var httpRequestLi = createNodeElement("HTTP Requests", true);
						var requestUl = $('<ul>').appendTo(httpRequestLi);
						createHttpRequestTree(requestUl, tracedRequestUrls); 
						ul.append(httpRequestLi);	
					
						var memoryLi = createNodeElement("Memory", true);
						ul.append(memoryLi);
						
						var dbconnectionLi = createNodeElement("Database Connections", true);
						ul.append(dbconnectionLi);
						
						var dbconnectionLi = createNodeElement("Active Sessions", true);
						ul.append(dbconnectionLi);
						
						var errorLi = createNodeElement("Exception Logging", true);
						ul.append(errorLi);
						
						createJsTree(ul);
						$("#ajax_box").hide();
				   }
				} 
			);
		}
		
		
		function createJsTree(ul) {
			
			$("#classTree").html(ul);
			$("#classTree")
				.jstree({"search" : {"case_insensitive" : true}, "plugins" : ["themes","html_data","ui", "search"] })
				// 1) if using the UI plugin bind to select_node
				.bind("select_node.jstree", function (event, data) { 
					var $tracedItem = data.rslt.obj.attr("id");
					$("#selectedNode").val($tracedItem);
					
					var parents = data.rslt.obj.parents("li");
					var parentNode = "";
					var length = parents.length;
					if(length == 0) {
						parentNode = $tracedItem;
					}
					else {
						parentNode = parents[length - 1].id;
					}
					var metricType = "";
					var metricUnit = "";
					var itemType = "";
					if(parentNode == "java") {
						metricType = "Response Time";
						metricUnit = "ms";
						itemType = "JAVA";
					}
					else if(parentNode == "SQL") {
						metricType = "Response Time";
						metricUnit = "ms";
						itemType = "SQL";
					}
					else if(parentNode == "HTTP Requests") {
						metricType = "Response Time";
						metricUnit = "ms";
						itemType = "HTTP_REQUEST";
					}
					else if(parentNode == "Memory") {
						metricType = "Memory Usage";
						metricUnit = "MB";
						itemType = "MEMORY";
					}
					else if(parentNode == "Database Connections") {
						metricType = "DB Connections Count";
						metricUnit = "connections";
						itemType = "ACTIVE_CONNECTION";
					}
					else if(parentNode == "Active Sessions") {
						metricType = "Sessions Count";
						metricUnit = "sessions";
						itemType = "ACTIVE_SESSION";
					}
					else if(parentNode == "Exception Logging") {
						metricType = "Number of Exceptions";
						metricUnit = "exceptions";
						itemType = "EXCEPTION";
					}
					
					$("#metricType").text(metricType);
					$("#metricUnit").text(metricUnit);
					$("#itemType").val(itemType);
					
					var height = $(".center_content").css("height");
					$("#ajax_box").css("height", height);
					$("#ajax_box").show();
					
					
					$.ajax
					(
						{
						  url:"json/retrievAlertSettings/itemName/" + $tracedItem + "/itemType/" + itemType, 
						  type: "GET",  
						  data: "",
						  complete: function(jsonResponse) {
						  		$("#successMsg").text("");

							  	if(jsonResponse.responseText == "") {
									$("#threshold").val("");
									$("#timeToAlert").val("");
									$("#alertEmail").val("");
									$("#enabled").prop('checked', false);
							  		$("#alertBox").css("display", "block");
									$("#ajax_box").hide();
									return true;
							  	}
								var alert = jQuery.parseJSON(jsonResponse.responseText);
								$("#threshold").val(alert.threshold);
								$("#timeToAlert").val(alert.timeToAlertInMins);
								$("#alertEmail").val(alert.alertEmail);
								$('#enabled').prop('checked', alert.enabled);
								$("#alertBox").css("display", "block");
								$("#ajax_box").hide();
						   }
						} 
					);
					
				})
				// 2) if not using the UI plugin - the Anchor tags work as expected
				//    so if the anchor has a HREF attirbute - the page will be changed
				//    you can actually prevent the default, etc (normal jquery usage)
				.delegate("a", "click", function (event, data) { event.preventDefault(); 
			})
		}
		
		function SaveAlertSettings() {
			
			var height = $(".center_content").css("height");
			$("#ajax_box").css("height", height);
			$("#ajax_box").show();
			
			var $alert = new Object();
			$alert.itemName = $("#selectedNode").val();
			$alert.itemType = $("#itemType").val();
			$alert.threshold = $("#threshold").val();
			$alert.timeToAlertInMins = $("#timeToAlert").val();
			$alert.alertEmail = $("#alertEmail").val();
			$alert.enabled = $("#enabled").is(':checked');
			
			var $jsonString = JSON.stringify( $alert );
		    $.ajax(
	            {
	              url:"json/saveAlert", 
	              type: "POST",  
	              contentType: "application/json; charset=utf-8",
	              data:  $jsonString,
	              complete: callback, 
	            } ); 
			function callback(jsonResponse) {
				$("#ajax_box").hide();
				$("#successMsg").text("Changes saved!");
			}

		}
		
        $(document).ready(function () {
        		setDefaultTextEvent();
				retrieveTracedItems();
		});
		
	</script>
	 <div id="ajax_box" class="ajax_box" style="display:none">
			<div class="ajax_box" style="background: url(images/ajax-loader.gif) no-repeat center center; "></div>
	 </div>
	 
	 <table class="main_table">
	 	<tr>
	 		<td width="25%">
	 			<div style="border: none;" id="classTree">
	               
	            </div>
	 		</td>
	 		<td width="75%">
				<div class="marginL paddingL" style="border: none; display:none" id="alertBox">
				   <div id="successMsg" style="color:red; margin-bottom:10px"></div>
				   <input type="checkbox" id="enabled"/>Enable Alert <br/>  
				   <div id="alertDiv">
					   <input type="hidden" id="selectedNode" />
					   <input type="hidden" id="itemType" />
		               <p>Setting alerts for the selected node and its children</p>
					   	<p>Alert me when the average <span id="metricType"></span> stays above <input id="threshold" size=5 /> <span id="metricUnit"></span> for the last <input id="timeToAlert" size=5 /> minutes</p>
					   	<p>Send alert email to <input id="alertEmail" size=50 />. 
				   	</div>            
	               <button id="save" onclick="SaveAlertSettings()">Save changes</button>
	               
	               
	            </div>
	 		</td>	
	 		
	 	</tr>

	 </table>
	
 </div>