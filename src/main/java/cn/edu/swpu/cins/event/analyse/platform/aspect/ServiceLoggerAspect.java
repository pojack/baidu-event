package cn.edu.swpu.cins.event.analyse.platform.aspect;

import cn.edu.swpu.cins.event.analyse.platform.exception.OperationFailureException;
import org.aspectj.lang.JoinPoint;
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

    @Before("logAop()")
    public void logBefore(JoinPoint joinPoint) {
        String msg = "\njoinpoint: " + joinPoint.toString() +
                "\nsignature name:" + joinPoint.getSignature().getName() +
                "\ndeclaring type name:" + joinPoint.getSignature().getDeclaringTypeName() +
                "\nargument count:" + joinPoint.getArgs().length +
                "\ntype of arguments:";
        for (Object args : joinPoint.getArgs()){
            msg += ("\n" + args.getClass().getName() + "(value:" + args.toString() +")");
        }
        logger.info(msg);
    }

    @AfterReturning("logAop()")
    public void logAfterReturning() {
        System.out.println("返回通知AfterReturning");
    }

    @After("logAop() && args(name)")
    public void logAfter(String name) {
        System.out.println(name + "后置通知After");
    }

    @AfterThrowing("logAop()")
    public void logAfterThrow() {
        System.out.println("异常通知AfterThrowing");
    }

    @Around("logAop()")
    public Object logAround(ProceedingJoinPoint jp) throws Throwable {
        try {
            String className = jp.getSignature().getDeclaringTypeName();
            String methodName = jp.getSignature().getName();
            logger.info("\nCalling method: " + className + "." + methodName);

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
                logger.error(throwable.getMessage());
            }
            throw throwable;
        }
    }
}
