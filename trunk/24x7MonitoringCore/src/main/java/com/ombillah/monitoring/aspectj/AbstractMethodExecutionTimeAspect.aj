
package com.ombillah.monitoring.aspectj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.CollectedData;

public abstract aspect AbstractMethodExecutionTimeAspect {
	
	@Inject
	private CollectedData collectedData;
	
	public AbstractMethodExecutionTimeAspect() {
		bootstrap();
	}

	private void bootstrap() {
		Injector injector = Bootstrap.init();
		collectedData = injector.getInstance(CollectedData.class);
	}
	
	protected pointcut methodExecTarget();   
    protected pointcut methodExec() : methodExecTarget() && execution(* *(..));
	
	Object around() : methodExec() {

	    Long start = System.currentTimeMillis();
	    Object ret = proceed();
	    Long end = System.currentTimeMillis();
	    
	    if(collectedData == null) {
	    	bootstrap();
	    }
	    if(collectedData == null) {
	    	return ret;
	    }
	    
	    Long executionTime = (end-start);
	    String signature = thisJoinPointStaticPart.getSignature().getName();
	    String className = thisJoinPointStaticPart.getSignature().getDeclaringTypeName();
	    String methodName = className + "." + signature + "()";
	    
	    // ignore spring proxies as the original call will also be intercepted.
	    if(!StringUtils.contains(className, "$Proxy")) {
	    	Map<String, List<Long>> tracers = collectedData.getTracer();
	    	List<Long> execTimes = tracers.get("JAVA||" + methodName);
	    	if(execTimes == null) {
	    		execTimes = Collections.synchronizedList(new ArrayList<Long>());
	    	}
	    	execTimes.add(executionTime);
	    	tracers.put("JAVA||" + methodName, execTimes);
	    	collectedData.setTracer(tracers);
	    }

	    return ret;
	  }
}
