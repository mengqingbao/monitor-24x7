<div class="main_content">
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript">
		chart = null;
		
	 	function drawChart($tracersArray) {

	        var data = google.visualization.arrayToDataTable($tracersArray);
	
	        var options = {
	          title: 'Application Performance'
	        };
			if(chart == null) {
				chart = new google.visualization.LineChart(document.getElementById('statsChart'));
			}
			chart.draw(data, options);
	    }
	    
	    function findTracer($itemName, $tracers) {
	    	var $return = null;
	    	$.each($tracers, function (index, item) {
		    	if(item.itemName == $itemName) {
		    		$return = item;
		    		return false;
		    	}
			});
			return $return;
	    }
	    
	    function setDateTimePickerEvent() {
	    	$('#fromRange, #toRange').datetimepicker({
				controlType: 'select',
				timeFormat: 'hh:mm tt'
			});
	    	
	    	$("#customRangeSubmit").click(function() {
	    		
	    		var $fromRange = $("#fromRange").val();
       		    var $toRange = $("#toRange").val();
       		    
    		 	if(!$fromRange || !$toRange) {
    		 		alert("'From' and 'To' ranges missing");
    		 		return false;
    		 	}
    		 	
    		 	var height = $(".center_content").css("height");
				$("#ajax_box").css("height", height);
				$("#ajax_box").show();

				var $selectedTreeNode = $('#classTree').jstree('get_selected').attr('id');

   			 	var searchFilter = new Object();
			    searchFilter.fromRange = $fromRange;
			    searchFilter.toRange = $toRange;
			    retrievePerformanceNumbers(searchFilter, $selectedTreeNode);
	    		  
	    	});
	    }
		
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
	    
	    function setTimeRangeEventEvent() {
			$('#timeRangeInMins').bind('change', function(ev) {
				var $selectedTimeRange = $(this).val();
				
				if(!$selectedTimeRange) {
					$("#customRangeSelector").css("visibility", "visible");
					return false;
				}
				$("#customRangeSelector").css("visibility", "hidden");
				var height = $(".center_content").css("height");
				$("#ajax_box").css("height", height);
				$("#ajax_box").show();

				
				$("#resolutionInSecs").val($selectedTimeRange);
			    var $selectedTreeNode = $('#classTree').jstree('get_selected').attr('id');

			 	var searchFilter = new Object();
			    searchFilter.timeRangeInMins = $selectedTimeRange;
			    retrievePerformanceNumbers(searchFilter, $selectedTreeNode);
			});
		}
		
	    function setSearchEvent() {
			$("#searchButton").click(function() {
	    		
				var $keyword = $("#searchKeyword").val();
				if(!$keyword) {
					alert("Please search for a keyword");
					return false;
				}
				$("#classTree").jstree("search", $keyword);
				  
	    	});
	    }
	    
	    function handlePerformanceStatsRetrieval($tracedItem, $searchedItems, $updateChartOnly) {
	    	
	    	var height = $(".center_content").css("height");
			$("#ajax_box").css("height", height);
			$("#ajax_box").show();
			
			var $selectedTimeRange = $("#timeRangeInMins").val();
			var $fromRange = $("#fromRange").val();
			var $toRange = $("#toRange").val();
			
			var searchFilter = new Object();
		    searchFilter.timeRangeInMins = $selectedTimeRange;
		    searchFilter.fromRange = $fromRange;
		    searchFilter.toRange = $toRange;
		    searchFilter.searchedItems = $searchedItems;
		    retrievePerformanceNumbers(searchFilter, $tracedItem, $updateChartOnly);
	    	
	    }
	    
		function retrievePerformanceNumbers(searchFilter, tracedItem, $updateChartOnly) {
			$("#chart").attr('src', "about:blank");
			$("#stacktrace").css("display", "none");
			$("#ajax_box").show();

			if(tracedItem == null) {
				$("#ajax_box").hide();
				alert("please select a node from the tree!");
				return false;
			}
			if(!searchFilter.timeRangeInMins 
					&& (!searchFilter.fromRange || !searchFilter.toRange)) {
				$("#ajax_box").hide();
				alert("please select a valid range");
				return false;
			}
			if(tracedItem == "Memory" || tracedItem == "Database Connections") {
				// display chart only.
				$("#scrollbar1").css("display", "none");
				createChartForm(tracedItem, searchFilter);
				$("#ajax_box").hide();
				return false;
			}
			else if(tracedItem == "Exception Logging") {
				$("#scrollbar1").css("display", "block");
				retrieveExceptionLogs(searchFilter);
				$("#ajax_box").hide();
				return false;
			}
			else {
				$("#scrollbar1").css("display", "block");
				retrieveTracingInfoForJavaAndSQL(searchFilter, tracedItem, $updateChartOnly);
			}			
		}
		
		function retrieveExceptionLogs(searchFilter) {
			var $jsonString = JSON.stringify( searchFilter );
		    $('#statsGrid').html("");
		    $.ajax(
	            {
	              url:"json/retrieveExceptionLogs", 
	              type: "POST",  
	              contentType: "application/json; charset=utf-8",
	              data:  $jsonString,
	              complete: callback, 
	            } ); 
			function callback(jsonResponse) {
				displayExceptionLogs(searchFilter, jsonResponse);
				$("#ajax_box").hide();
			}
		}
		
		function displayExceptionLogs(searchFilter, jsonResponse)  {
	  		var $jsonArray = jQuery.parseJSON(jsonResponse.responseText);
	  		var $itemArray = [];
	  		if($jsonArray.length == 0) {
	  			$('#statsGrid2').html("");
	  			$(".scrollbar").css("display", "none");
	  			return false;
	  		}
	  		$.each($jsonArray, function (index, value) {
				var $arr = [];
				$arr.push("<a class='simple_anchor' onclick='displayStacktrace(\"" + escape(value.stacktrace) +  "\")'>" + value.exceptionMessage + "</a>");
				$arr.push(value.count);
			    $itemArray.push($arr);
			});   

	  		$('#statsGrid2')
			.TidyTable({
				enableCheckbox : false,
				enableMenu     : false
			},
			{
				columnTitles : ['Exception Message','Count'],
				columnValues : $itemArray
		       
			});
							
			$(".scrollbar").css("display", "block");
			$('#scrollbar1').tinyscrollbar();
		}
		
		
		function displayStacktrace(stacktrace) {
			var trace = unescape(stacktrace);
			trace = trace.replace(/\n/g, "<br />");
			trace = trace.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
			$("#stacktrace").css("display", "block");
			$("#stacktrace").html(trace);
		}
		
		function retrieveTracingInfoForJavaAndSQL(searchFilter, tracedItem, $updateChartOnly) {
			var $jsonString = JSON.stringify( searchFilter );	
		    $.ajax(
		            {
		              url:"json/retrieveTracingInfo/" + tracedItem, 
		              type: "POST",  
		              contentType: "application/json; charset=utf-8",
		              data:  $jsonString,
		              complete: callback, 
		            } ); 
			function callback(jsonResponse) {
				updateDataSet(searchFilter, tracedItem, jsonResponse, $updateChartOnly);
			}
		}
		
		function updateDataSet(searchFilter, tracedItem, jsonResponse, $updateChartOnly) {
		    $("#ajax_box").hide();
		    $('#statsGrid2').html("");
	  		var $jsonArray = jQuery.parseJSON(jsonResponse.responseText);
	  		
	  		if(($jsonArray.monitoredItemTracersGrouped == null || $jsonArray.monitoredItemTracersGrouped.length == 0)
	  				&& ( $jsonArray.QueryTracersGrouped == null || $jsonArray.QueryTracersGrouped.length == 0 )) {
	  			$('#statsGrid').html("");
	  			$(".scrollbar").css("display", "none");
	  			return;
	  		}
	  		
	  		if(!$updateChartOnly) {
	  			var $groupedArray = [];
		  		var $performanceStats = [];
		  		
		  		if($jsonArray.monitoredItemTracersGrouped != null) {
		  			$performanceStats = $jsonArray.monitoredItemTracersGrouped;
		  		}
		  		else if($jsonArray.queryTracersGrouped != null ) {
		  			$performanceStats = $jsonArray.queryTracersGrouped;		  			
		  		}
		  		
				$.each($performanceStats, function (index, value) {
					var $itemArray = [];
					
				    if(value.itemName) {
				    	$itemArray.push(value.itemName);
				    }
				    if(value.queryText) {
				    	$itemArray.push(value.queryText);
				    }
				    $itemArray.push(parseFloat(value.average).toFixed(2));
				    $itemArray.push(value.max);
				    $itemArray.push(value.min);
				    $itemArray.push(value.count);
			    	$groupedArray.push($itemArray);
				});   
				$('#statsGrid')
				.TidyTable({
					enableCheckbox : true,
					enableMenu     : false
				},
				{
					columnTitles : ['Monitored Item','Response<br/>Time (ms)','Max','Min','Count'],
					columnValues : $groupedArray
			       
				});
				
				updateChartOnCheckBoxChange();
					
				$(".scrollbar").css("display", "block");
				$('#scrollbar1').tinyscrollbar();
	  		}
	  		
	  		createChartForm(tracedItem, searchFilter);
		}
		
		function createChartForm(tracedItem, searchFilter) {
			var $chartForm = $('#chartForm');
	  		var $methodSignature = $("<input>").attr("type", "hidden").attr("name", "monitoredItem").val(tracedItem);
	  		var $timeRangeInMins = $("<input>").attr("type", "hidden").attr("name", "timeRangeInMins").val(searchFilter.timeRangeInMins);
	  		var $fromRange = $("<input>").attr("type", "hidden").attr("name", "fromRange").val(searchFilter.fromRange);
	  		var $toRange = $("<input>").attr("type", "hidden").attr("name", "toRange").val(searchFilter.toRange);
	  		
	  		$chartForm.append($methodSignature);
	  		$chartForm.append($timeRangeInMins);
	  		$chartForm.append($fromRange);
	  		$chartForm.append($toRange);
	  		$chartForm.append($toRange);

		   
	    	if(searchFilter.searchedItems != null 
	    			&& searchFilter.searchedItems != undefined
	    			&& searchFilter.searchedItems.length > 0 ) {
	    		
	    		searchFilter.searchedItems.forEach(function (item) { 
	    	  		var $searchedItems = $("<input>").attr("type", "hidden").attr("name", "searchedItems").val(item);
	    	  		$chartForm.append($searchedItems);
			    });
	    	} else {
	    		var $searchedItems = $("<input>").attr("type", "hidden").attr("name", "searchedItems").val("");
    	  		$chartForm.append($searchedItems);
	    	}
	    	
	    	$chartForm.submit();
	    	$chartForm.html('');
		}
		
		function updateChartOnCheckBoxChange() {
			$('.tidy_table :checkbox').click(function() {
				var $checkedItems;
				if(this.value == "all") {
					$checkedItems = $('.tidy_table :checkbox').map(function () {
						  return this.value;
						}).get();
					$checkedItems.splice(0, 1);
				}
				else {
					$checkedItems = $('.tidy_table :checkbox:checked').map(function () {
						 return this.value;
						}).get();
				}
				if($checkedItems.length == 0) {
					$tracedItem = $('#classTree').jstree('get_selected').attr('id');
				}
				else {
					var $tracedItem = $checkedItems[0];
					$checkedItems.splice(0, 1); // remove first element.
				}
				
				handlePerformanceStatsRetrieval($tracedItem, $checkedItems, true);
			});
			
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
						var ul = $(document.createElement('ul'));
						
						var javaLi = createNodeElement("java", false);
						var javaUl = $('<ul>').appendTo(javaLi);
						createMethodsTree(javaUl, tracedMethods);
						ul.append(javaLi);
						
						var sqlLi = createNodeElement("SQL", true);
						var sqlUl = $('<ul>').appendTo(sqlLi);
						createSQLTree(sqlUl, tracedQueries);   
						ul.append(sqlLi);
					
						var memoryLi = createNodeElement("Memory", true);
						ul.append(memoryLi);
						
						var dbconnectionLi = createNodeElement("Database Connections", true);
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
					handlePerformanceStatsRetrieval($tracedItem);
   
				})
				.bind("search.jstree", function (e, data) {
					$('#classTree').jstree('close_all');
					if(data.rslt.nodes.length == 0) {
						alert("No results found! please redefine your search..");
						return false;
					}
					var $searchedItems = [];
					$.each(data.rslt.nodes, function(index, item) {
						var $nodeName = item.parentElement.id;
						var $exists = false;
						$.each($searchedItems, function(i, value) {
							if ($nodeName.indexOf(value) >= 0) {
								$exists = true;
								return true; // exit for loop.
							}
						});
						if(!$exists) {
							$searchedItems.push($nodeName);
						}
					});
					var $tracedItem = $searchedItems[0];
					$searchedItems.splice(0, 1); // remove first element.
					handlePerformanceStatsRetrieval($tracedItem, $searchedItems);
				})
				// 2) if not using the UI plugin - the Anchor tags work as expected
				//    so if the anchor has a HREF attirbute - the page will be changed
				//    you can actually prevent the default, etc (normal jquery usage)
				.delegate("a", "click", function (event, data) { event.preventDefault(); 
			})
		}
		
        $(document).ready(function () {
        		setTimeRangeEventEvent();
        		setDateTimePickerEvent();
        		setDefaultTextEvent();
        		setSearchEvent();
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
	 			<div class="filter" style="height:50px; margin:10px'">
				 	<div class="floatL">
				 		time range: 
				 			<select id="timeRangeInMins" name="timeRangeInMins" style="background:#F0F0F0;border:1px solid #e8e8e8;margin:5px">
				 			  <option value="30">Last 30 minutes</option>
							  <option value="120">Last 2 hours</option>
							  <option value="360">Last 6 hours</option>
							  <option value="720">Last 12 hours</option>
							  <option value="1440">Last 24 hours</option>
							  <option value="10080">Last 7 days</option>
							  <option value="">Custom Range</option>
						    </select>
						    
						    <span id="customRangeSelector" style="visibility:hidden">
						    	<br/>
							    From: <input size=15 id="fromRange" /> 
							    To: <input size="15" id="toRange" />
							    <button id="customRangeSubmit">submit</button>
							</span>
					</div>
					<div class="floatL" style="margin-left:-100px">
					Resolution: 
				 			<select id="resolutionInSecs" name="resolutionInSecs" style="background:#F0F0F0;border:1px solid #e8e8e8" disabled="disabled">
							  <option value="30">30 secs</option>
							  <option value="120">2 minutes</option>
							  <option value="360">6 minutes</option>
							  <option value="720">12 minutes</option>
							  <option value="1440">24 minutes</option>
							  <option value="10080">3 hours</option>
							  <option value="">Custom</option>
						    </select>
					</div>
					<div class="floatL" style="margin-left:20px">
						<input size=50 id="searchKeyword" class="defaultText" title="search for class, method, SQL...."/>
						<button id="searchButton">search</button>
					</div>
				 </div>
	 			<div id="scrollbar1" >
					<div class="scrollbar" style="display:none"><div class="track"><div class="thumb"><div class="end"></div></div></div></div>
					<div class="viewport">
						 <div class="overview" id="statsGrid"  align="center">
						 	
						 </div>
						 <div class="overview" id="statsGrid2"  align="center" style="width:99%">
						 	
						 </div>
					</div>
				</div>	
				<div style="height:20px">&nbsp</div>
				<div id="stacktrace" style="max-width:980px" >
					
	        	</div>
	        	<div id="statsChart" style="width:99%; height:600px" >
					<iframe id="chart" style="border:none;" width="100%" height="550px" marginheight="0" marginwidth="0" frameborder="0">
					  <p>Your browser does not support iframes.</p>
					</iframe>
					<form id="chartForm" target="chart" action="getchart.do" method="post">

					</form>
	        	</div>
	 		</td>	
	 		
	 	</tr>

	 </table>
	
 </div>