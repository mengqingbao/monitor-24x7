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
						
						var cpuUsageLi = createNodeElement("CPU Usage", true);
						ul.append(cpuUsageLi);
						
						var dbconnectionLi = createNodeElement("Database Connections", true);
						ul.append(dbconnectionLi);
						
						var activeSessions = createNodeElement("Active Sessions", true);
						ul.append(activeSessions);
						
						var liveThreads = createNodeElement("Live Threads", true);
						ul.append(liveThreads);
						
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
					else if(parentNode == "CPU Usage") {
						metricType = "CPU Usage";
						metricUnit = "%";
						itemType = "CPU";
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
					else if(parentNode == "Live Threads") {
						metricType = "Live Threads";
						metricUnit = "threads";
						itemType = "ACTIVE_THREAD";
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
						  url:"json/retrieveReportSettings/itemName/" + $tracedItem + "/itemType/" + itemType, 
						  type: "GET",  
						  data: "",
						  complete: function(jsonResponse) {
						  		$("#successMsg").text("");

							  	if(jsonResponse.responseText == "") {
							  		$('input[name=reportFrequency]:checked').prop('checked', false);
							  		$('#reportDayOfMonth option:eq(0)').attr('selected','selected');
							  		$('#reportDayOfWeek option:eq(0)').attr('selected','selected');
							  		$('#reportHour option:eq(0)').attr('selected','selected');
							  		$('#reportMinute option:eq(0)').attr('selected','selected');
									$("#reportEmail").val("");
									$("#enabled").prop('checked', false);
							  		$("#reportBox").css("display", "block");
									$("#ajax_box").hide();
									return true;
							  	}
								var report = jQuery.parseJSON(jsonResponse.responseText);
								$("input[type=radio][value=" + report.frequency + "]").click();
								$('#reportDayOfMonth').val(report.dayOfMonth);
								$('#reportDayOfWeek').val(report.dayOfWeek);
								$('#reportHour').val(report.hour);
								$('#reportMinute').val(report.minute);
								$('#reportEmail').val(report.reportEmail);
								$('#enabled').prop('checked', report.enabled);
								$("#reportBox").css("display", "block");
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
		
		function SaveReportSettings() {
			
			var height = $(".center_content").css("height");
			$("#ajax_box").css("height", height);
			$("#ajax_box").show();
			
			var $report = new Object();
			$report.itemName = $("#selectedNode").val();
			$report.itemType = $("#itemType").val();
			$report.frequency = $('input[name=reportFrequency]:checked').val();
			$report.dayOfMonth = $('#reportDayOfMonth').val();
			$report.dayOfWeek = $('#reportDayOfWeek').val();
			$report.minute = $("#reportMinute").val();
			$report.hour = $("#reportHour").val();
			$report.reportEmail = $("#reportEmail").val();
			$report.enabled = $("#enabled").is(':checked');
			
			var $jsonString = JSON.stringify( $report );
		    $.ajax(
	            {
	              url:"json/saveReport", 
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
		
		function setFrequencyClickEvent() {
			$("input[name=reportFrequency]:radio").change(function () {
				var selected = this.value;
				
				if(selected == "hourly") {
					// do nothing. keep div block hidden.
					$("#reportDetails").css("display", "none");
					return true;
				}
				else if(selected == "weekly") {
					$("#dayOfWeek").css("display", "block");
					$("#dayOfMonth").css("display", "none");
				}
				else if(selected == "monthly") {
					$("#dayOfWeek").css("display", "none");
					$("#dayOfMonth").css("display", "block");
				} else {
					$("#dayOfWeek").css("display", "none");
					$("#dayOfMonth").css("display", "none");
				}
				$("#reportDetails").css("display", "block");
			})
		}
		
        $(document).ready(function () {
        		setDefaultTextEvent();
				retrieveTracedItems();
				setFrequencyClickEvent();
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
				<div class="marginL paddingL" style="border: none; display:none" id="reportBox">
				   <div id="successMsg" style="color:red; margin-bottom:10px"></div>
				   <input type="checkbox" id="enabled"/>Enable Scheduled Report <br/>  
				   <div id="alertDiv">
					   <input type="hidden" id="selectedNode" />
					   <input type="hidden" id="itemType" />
		               <p>Scheduling Reports for the selected Item</p>
		               <div class="floatL" style="width:100%">
			               <div style="float:left; width:20%; height:200px; border-right:1px solid; padding-top:10px">
				               	<input type="radio" name="reportFrequency" value="hourly" /> Hourly </br>		               	
				               	<input type="radio" name="reportFrequency" value="daily" /> Daily </br>
				               	<input type="radio" name="reportFrequency" value="weekly" /> Weekly </br>
				               	<input type="radio" name="reportFrequency" value="monthly" /> Monthly </br>
						   </div>
							<div id="reportDetails" style="float:left;width:400px; height:200px; margin-left:25px;padding-top:10px;display:none">
				               	<div class="floatL" style="width:400px">
							 		<div style="width:90%">Run Report at: </div>
							 		
							 		<div id="dayOfWeek" class="marginT" style="width:90%">
							 			Day Of Week:  <select id="reportDayOfWeek" style="background:#F0F0F0;border:1px solid #e8e8e8;margin:5px">
							 			  <option value="2">Monday</option>
							 			  <option value="3">Tuesday</option>
							 			  <option value="4">Wednesday</option>
							 			  <option value="5">Thursday</option>
							 			  <option value="6">Friday</option>
							 			  <option value="7">Saturday</option>
							 			  <option value="1">Sunday</option>
									    </select>
							 		</div>
							 		<div id="dayOfMonth" class="marginT" style="width:90%">
							 			Day Of Month:  <select id="reportDayOfMonth" style="background:#F0F0F0;border:1px solid #e8e8e8;margin:5px">
							 			  <option value="1">1st</option><option value="2">2nd</option>
										  <option value="3">3rd</option><option value="4">4th</option><option value="5">5th</option>
										  <option value="6">6th</option><option value="7">7th</option><option value="8">8th</option>
										  <option value="9">9th</option><option value="10">10th</option><option value="11">11th</option>
										  <option value="12">12th</option><option value="13">13th</option><option value="14">14th</option>
										  <option value="15">15th</option><option value="16">16th</option><option value="17">17th</option>
										  <option value="18">18th</option><option value="19">19th</option><option value="20">20th</option>
										  <option value="21">21th</option><option value="22">22th</option><option value="23">23th</option>
										  <option value="24">24th</option><option value="25">25th</option><option value="26">26th</option>
										  <option value="27">27th</option><option value="28">28th</option><option value="29">29th</option>
										  <option value="30">30th</option>
									    </select>
							 		</div>
							 		<div class="floatL marginT">
							 			Hour: <select id="reportHour" style="background:#F0F0F0;border:1px solid #e8e8e8;margin:5px">
							 			  <option value="0">0</option>
							 			  <option value="1">1</option>
							 			  <option value="2">2</option>
							 			  <option value="3">3</option>
							 			  <option value="4">4</option>
							 			  <option value="5">5</option>
							 			  <option value="6">6</option>
							 			  <option value="7">7</option>
							 			  <option value="8">8</option>
							 			  <option value="9">9</option>
							 			  <option value="10">10</option>
							 			  <option value="11">11</option>
							 			  <option value="12">12</option>
							 			  <option value="13">13</option>
							 			  <option value="14">15</option>
							 			  <option value="16">16</option>
							 			  <option value="17">17</option>
							 			  <option value="18">18</option>
							 			  <option value="19">19</option>
							 			  <option value="20">20</option>
							 			  <option value="21">21</option>
							 			  <option value="22">22</option>
							 			  <option value="23">23</option>
									    </select>
									    
									    Minutes: 
							 			<select id="reportMinute" style="background:#F0F0F0;border:1px solid #e8e8e8" >
										  <option value="0">00</option><option value="1">01</option><option value="2">02</option>
										  <option value="3">03</option><option value="4">04</option><option value="5">05</option>
										  <option value="6">06</option><option value="7">07</option><option value="8">08</option>
										  <option value="9">09</option><option value="10">10</option><option value="11">11</option>
										  <option value="12">12</option><option value="13">13</option><option value="14">14</option>
										  <option value="15">15</option><option value="16">16</option><option value="17">17</option>
										  <option value="18">18</option><option value="19">19</option><option value="20">20</option>
										  <option value="21">21</option><option value="22">22</option><option value="23">23</option>
										  <option value="24">24</option><option value="25">25</option><option value="26">26</option>
										  <option value="27">27</option><option value="28">28</option><option value="29">29</option>
										  <option value="30">30</option>
										  <option value="31">31</option><option value="32">32</option><option value="33">33</option>
										  <option value="34">34</option><option value="35">35</option><option value="36">36</option>
										  <option value="37">37</option><option value="38">38</option><option value="39">39</option>
										  <option value="40">40</option><option value="41">41</option><option value="42">42</option>
										  <option value="43">43</option><option value="44">44</option><option value="45">45</option>
										  <option value="46">46</option><option value="47">47</option><option value="48">48</option>
										  <option value="49">49</option><option value="50">50</option><option value="51">51</option>
										  <option value="52">52</option><option value="53">53</option><option value="54">54</option>
										  <option value="55">55</option><option value="56">56</option><option value="57">57</option>
										  <option value="58">58</option><option value="59">59</option>
									    </select>
									</div>
								</div>
							</div>
					   </div>
					   <div class="floatL">
					   	<p>Send Report to <input id="reportEmail" size=50 />. 
					   	<div style="height:20px">&nbsp</div>
					   	<button id="save" onclick="SaveReportSettings()">Save changes</button>
					   </div>
					   	
				   	</div>            
	               
	               
	            </div>
	 		</td>	
	 		
	 	</tr>

	 </table>
	
 </div>