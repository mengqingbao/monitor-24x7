<div class="main_content">
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript">
		chart = null;
	
		function createList(ul, array) {
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
				li.append('<a href="#">' + item.itemName + '</a>');
				if(item.subItems.length > 0 ) {
				    var ul2 = $('<ul>').appendTo(li);
					createList(ul2, item.subItems);
	 			}
				ul.append(li);
			});
			
		}

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
	    
	    function findTracer($methodName, $tracers) {
	    	var $return = null;
	    	$.each($tracers, function (index, item) {
		    	if(item.methodName == $methodName) {
		    		$return = item;
		    		return false;
		    	}
			});
			return $return;
	    }
	    
	    function setTimeRangeEventEvent() {
			$('#timeRangeInMins').bind('change', function(ev) {
		
				var height = $(".center_content").css("height");
				$("#ajax_box").css("height", height);
				$("#ajax_box").show();

				var $selectedTimeRange = $(this).val();
				var $selectedResolution = $selectedTimeRange;
			 	$("#resolutionInSecs").val($selectedResolution);
			 	
			 	var searchFilter = new Object();
			    searchFilter.timeRangeInMins = $selectedTimeRange;
			    searchFilter.resolutionInSecs = $selectedResolution;
			    var $jsonString = JSON.stringify( searchFilter );
			    var $selectedTreeNode = $('#classTree').jstree('get_selected').attr('id');
		
			    $.ajax(
			            {
			              url:"json/methodTracingInfo/" + escape($selectedTreeNode), 
			              type: "POST",  
			              contentType: "application/json; charset=utf-8",
			              data:  $jsonString,
			              complete: callback, 
			            } ); 
				function callback(jsonResponse) {
					updateDataSet(jsonResponse);
				}
		
			 });
		}
		
		function updateDataSet(jsonResponse) {
		    $("#ajax_box").hide();
	  		var $jsonArray = jQuery.parseJSON(jsonResponse.responseText);
	  		
	  		if($jsonArray.tracersGrouped.length == 0 ) {
	  			$('#statsGrid').html("");
	  			$(".scrollbar").css("display", "none");
	  			return;
	  		}
	  		var $groupedArray = [];
	  		
			$.each($jsonArray.tracersGrouped, function (index, value) {
				var $itemArray = [];
			    $itemArray.push(value.methodName);
			    $itemArray.push(parseFloat(value.average).toFixed(2));
			    $itemArray.push(value.max);
			    $itemArray.push(value.min);
			    $itemArray.push(value.count);
		    	$groupedArray.push($itemArray);
			});   
			
			var $tracersArray = [];
			var $headersArray = [];
			var $dataArray = [];
			$headersArray.push('Time');
			var $maxSize = 0;
			$.each($jsonArray.tracersByResolution, function (key, value) {
				$.each(value, function (index, item) {
			    	$headersArray.push(item.methodName);
				});
				
			});
			
			$tracersArray.push($headersArray);
			
			$.each($jsonArray.tracersByResolution, function (key, value) {
				var $dataArray = [];
			    $dataArray.push(key);
			    $.each($headersArray, function (index, methodName) {
			    	if(index == 0) {
			    		return true;
			    	}
			    	var $tracer = findTracer(methodName, value);
			    	if($tracer == null) {
			    		$dataArray.push(null);
			    	}
			    	else {
			    		$dataArray.push($tracer.average);
			    	}
				});
				$tracersArray.push($dataArray);
			});
			
			$('#statsGrid')
			.TidyTable({
				enableCheckbox : true,
				enableMenu     : false
			},
			{
				columnTitles : ['Method Name','Avg Response<br/>Time (ms)','Max','Min','Count'],
				columnValues : $groupedArray
		       
			});
			$(".scrollbar").css("display", "block");
			$('#scrollbar1').tinyscrollbar();
			var callback = function() {drawChart($tracersArray)};
			
			google.load("visualization", "1", {packages:["corechart"], "callback" : callback});


		}
        $(document).ready(function () {
        		setTimeRangeEventEvent();
	        	var height = $(".center_content").css("height");
				$("#ajax_box").css("height", height);
				$("#ajax_box").show();
				
				$.ajax
				(
					{
					  url:"json/getTracedMethods", 
					  type: "GET",  
					  data: "",
					  complete: function(jsonResponse) {
					  		$("#ajax_box").hide();
							var objectArray = jQuery.parseJSON(jsonResponse.responseText);
							var ul = $(document.createElement('ul'));
							createList(ul, objectArray);
							$("#classTree").html(ul);
							$("#classTree")
								.jstree({ "plugins" : ["themes","html_data","ui"] })
								// 1) if using the UI plugin bind to select_node
								.bind("select_node.jstree", function (event, data) { 
									// `data.rslt.obj` is the jquery extended node that was clicked
					
									var height = $(".center_content").css("height");
									$("#ajax_box").css("height", height);
									$("#ajax_box").show();
									
									var $selectedTimeRange = $("#timeRangeInMins").val();
									var $selectedResolution = $("#resolutionInSecs").val();

									var searchFilter = new Object();
								    searchFilter.timeRangeInMins = $selectedTimeRange;
								    searchFilter.resolutionInSecs = $selectedResolution;
								    var jsonString = JSON.stringify( searchFilter );
								    
					                $.ajax
									(
										{
										  url:"json/methodTracingInfo/" + escape(data.rslt.obj.attr("id")), 
										  type: "POST",  
										  data: jsonString,
										  contentType: "application/json; charset=utf-8",
										  complete: function(jsonResponse) {
											   updateDataSet(jsonResponse);
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
					} 
				);
	
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
	 			<div class="filter">
				 	time range: 
				 			<select id="timeRangeInMins" name="timeRangeInMins" style="background:#F0F0F0;border:1px solid #e8e8e8">
				 			  <option value="30">Last 30 minutes</option>
							  <option value="120">Last 2 hours</option>
							  <option value="360">Last 6 hours</option>
							  <option value="720">Last 12 hours</option>
							  <option value="1440">Last 24 hours</option>
							  <option value="10080">Last 7 days</option>
						    </select>
					Resolution: 
				 			<select id="resolutionInSecs" name="resolutionInSecs" style="background:#F0F0F0;border:1px solid #e8e8e8" disabled="disabled">
							  <option value="30">30 secs</option>
							  <option value="120">2 minutes</option>
							  <option value="360">6 minutes</option>
							  <option value="720">12 minutes</option>
							  <option value="1440">24 minutes</option>
							  <option value="10080">3 hours</option>
						    </select>
				 </div>
	 			<div id="scrollbar1" >
					<div class="scrollbar" style="display:none"><div class="track"><div class="thumb"><div class="end"></div></div></div></div>
					<div class="viewport">
						 <div class="overview" id="statsGrid"  align="center">
						 	
						 </div>
					</div>
				</div>	
	        	<div id="statsChart" style="width:99%; height:400px">
	        		
	        	</div>
	 		</td>	
	 		
	 	</tr>

	 </table>
	<!--  <div id="splitter">
	        <div>
	            <div style="border: none;" id='jqxTree'>
	               
	            </div>
	        </div>
	        <div id="ContentPanel">
	        	<div id="scrollbar1">
					<div class="scrollbar" style="display:none"><div class="track"><div class="thumb"><div class="end"></div></div></div></div>
					<div class="viewport">
						 <div class="overview" id="statsGrid">
						 	
						 </div>
					</div>
				</div>	
	        	<div id="statsChart" style="width:99%; height:400px">
	        		
	        	</div>
	        </div>
	 </div>
	  -->
 </div>