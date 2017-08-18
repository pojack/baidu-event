package cn.edu.swpu.cins.event.analyse.platform.aspect;

import cn.edu.swpu.cins.event.analyse.platform.exception.OperationFailureException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by LLPP on 2017/8/12.
 */
@Aspect
@Component
public class ServiceLoggerAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* cn.edu.swpu.cins.event.analyse.platform.service.impl.*.*(..))")
    public void logAop() {
    }

    @Around("logAop()")
    public Object logAround(ProceedingJoinPoint jp) throws Throwable {
        try {
            String className = jp.getSignature().getDeclaringTypeName();
            String methodName = jp.getSignature().getName();
            //print the information of method be called
            String msg = "\nCalling method: " + className + "." + methodName;
            msg += "\nArguments:";

            //print the arguments;
            for (Object args : jp.getArgs()){
                msg += ("\n" + args.getClass().getName() + "(value:" + args.toString() +")");
            }

            logger.info(msg);
            //Record the cost time
            long startTime = System.currentTimeMillis();
            Object object = jp.proceed(jp.getArgs());
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            logger.info("\nFinished method: " + className + "." + methodName
                    + "\nCostTime: " + costTime + " millisecond");

            return object;
        } catch (Throwable throwable) {
            if (throwable instanceof OperationFailureException) {
                logger.error("Internal exception be threw:\n",throwable);
            }

            throw throwable;
        }
    }
}
