package com.ombillah.monitoring.aspectj;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
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
			collectedData = injector.getInstance(Key.get(CollectedData.class, Names.named("MethodAndHttpRequestCollector")));
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
		
		final CopyPrintWriter writer = new CopyPrintWriter(response.getWriter());

		Long start = System.currentTimeMillis();
		
		Object ret = proceed(request, new HttpServletResponseWrapper(response) {
	        @Override public PrintWriter getWriter() {
	            return writer;
	        }
	    });
		
		Long end = System.currentTimeMillis();
		Long executionTime = (end - start);

		
		String title = "";
		
		try {
			String html = writer.getCopy();
			StringReader reader = new StringReader(html);
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = new HTMLDocument();
			kit.read(reader, doc, 0);
			title = (String) doc.getProperty(Document.TitleProperty);
		} catch (Exception e) {
			return ret;
		}
		
		
		String url = request.getServletPath();
		String monitoredItemName = url + "|" + title;
		Map<String, List<Long>> tracers = collectedData.getTracer();
    	List<Long> execTimes = tracers.get(monitoredItemName);
    	if(execTimes == null) {
    		execTimes = Collections.synchronizedList(new ArrayList<Long>());
    	}
    	execTimes.add(executionTime);
    	tracers.put(monitoredItemName, execTimes);
    	collectedData.setTracer(tracers);
		
		return ret;

	}
	
	class CopyPrintWriter extends PrintWriter {

	    private StringBuilder copy = new StringBuilder();

	    public CopyPrintWriter(Writer writer) {
	        super(writer);
	    }

	    @Override
	    public void write(int c) {
	        copy.append((char) c); // It is actually a char, not an int.
	        super.write(c);
	    }

	    @Override
	    public void write(char[] chars, int offset, int length) {
	        copy.append(chars, offset, length);
	        super.write(chars, offset, length);
	    }

	    @Override
	    public void write(String string, int offset, int length) {
	        copy.append(string, offset, length);
	        super.write(string, offset, length);
	    }

	    public String getCopy() {
	        return copy.toString();
	    }

	}
}
