package com.ombillah.monitoring.aspectj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Injector;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.CollectedData;

public aspect HttpRequestHandlerAspect {

	private CollectedData collectedData;

	public HttpRequestHandlerAspect() {
		bootstrap();
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			collectedData = injector.getInstance(CollectedData.class);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	pointcut getRequest(HttpServletRequest request, HttpServletResponse response) 
	  	: execution(protected * javax.servlet.http.HttpServlet.*(HttpServletRequest, HttpServletResponse))  
	  	&& args(request, response);

	Object around(HttpServletRequest request, HttpServletResponse response) throws IOException : getRequest(request, response) {
		
		String contentType = request.getHeader("accept");
		boolean isHtmlPage = StringUtils.contains(contentType, "text/html");
		
		if (!isHtmlPage || collectedData == null) {
			return proceed(request, response);
		}	

		Long start = System.currentTimeMillis();
		Object ret = proceed(request, response);
		Long end = System.currentTimeMillis();
		
		Long executionTime = (end - start);
		
		String responseType = response.getContentType();		
		boolean isHtmlResponse = StringUtils.startsWith(responseType, "text/html");
		
		if(!isHtmlResponse) {
			return ret;
		}
		
		String url = request.getRequestURI();
		String monitoredItemName = url.substring(1); // remove leading backslash
		Map<String, List<Long>> tracers = collectedData.getTracer();
    	List<Long> execTimes = tracers.get("HTTP_REQUEST||" + monitoredItemName);
    	if(execTimes == null) {
    		execTimes = Collections.synchronizedList(new ArrayList<Long>());
    	}
    	execTimes.add(executionTime);
    	tracers.put("HTTP_REQUEST||" + monitoredItemName, execTimes);
    	collectedData.setTracer(tracers);
		
		return ret;
	}

}
