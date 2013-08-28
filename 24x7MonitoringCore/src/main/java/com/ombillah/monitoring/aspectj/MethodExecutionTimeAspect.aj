package com.ombillah.monitoring.aspectj;

public aspect MethodExecutionTimeAspect extends AbstractMethodExecutionTimeAspect {
    
    protected pointcut methodExec() : methodExecTarget() && execution(* *(..));

}
