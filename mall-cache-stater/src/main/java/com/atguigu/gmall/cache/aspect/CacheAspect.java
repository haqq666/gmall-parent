package com.atguigu.gmall.cache.aspect;

import com.atguigu.gmall.cache.constant.SysRedisConstant;
import com.atguigu.gmall.cache.service.CacheOpsService;
import com.atguigu.gmall.cache.annontation.GmallCache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 20:13
 */
@Aspect
@Component
public class CacheAspect {

    @Autowired
    CacheOpsService cacheOpsService;

    SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    TemplateParserContext context = new TemplateParserContext();

    @Around("@annotation(com.atguigu.gmall.cache.annontation.GmallCache))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        //参数列表
//        Object arg = joinPoint.getArgs()[0];
        //缓存key
        //String cacheKey = SysRedisConstant.SKU_INFO_PREFIX + arg;

        //优化根据传的值来获取不同的缓存key
        String cacheKey = determinCacheKey(joinPoint);
        //查缓存
       // result = cacheOpsService.getCacheData(cacheKey, SkuDetailsTo.class);

        //优化返回值类型
        Type returnType = getGenericReturnType(joinPoint);

        result = cacheOpsService.getCacheData(cacheKey, returnType);

        if(result == null){
            //回源
            //先问bloom
           // Boolean contains = cacheOpsService.getBloomContains(arg);



            //优化
            //获取bloom的名字
            String bloomName = determinBloomName(joinPoint);

            if (!StringUtils.isEmpty(bloomName)){

                Object bloomValue = determinBloomNValue(joinPoint);

                Boolean contains = cacheOpsService.getBloomContains(bloomName,bloomValue);
                if (!contains){
                    return null;
                }
            }

            Boolean lock = false;
            String lockName = "";
            try {
                lockName = determinLockName(joinPoint);
                lock = cacheOpsService.tryLock(lockName);
                if (lock) {
                    //回源
                    result = joinPoint.proceed(joinPoint.getArgs());

                    //获取存储时间
                    Long ttl = determinTtl(joinPoint);
                    cacheOpsService.saveCacheData(cacheKey, result,ttl);
                } else {
                    Thread.sleep(1000);
                    //再次查缓存
                    result = cacheOpsService.getCacheData(cacheKey,returnType);
                }
            }finally {
                if (lock){
                    cacheOpsService.unlock(lockName);
                }
            }
        }

        return result;

    }

    private Long determinTtl(ProceedingJoinPoint joinPoint) {
        //获取方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法的对象
        Method method = signature.getMethod();
        //拿到方法上面的注解（名为GmallCache）
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //拿到注解里面的值并返回
        Long ttl = annotation.ttl();

        return ttl;
    }

    private String determinLockName(ProceedingJoinPoint joinPoint) {
        //获取方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法的对象
        Method method = signature.getMethod();
        //拿到方法上面的注解（名为GmallCache）
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //拿到注解里面的值并返回
        String expression = annotation.lockName();
        if (expression.isEmpty()){
            return SysRedisConstant.LOCK_PREFIX + method.getName();
        }

        String lockName = evaluationExpression(expression,joinPoint,String.class);

        return lockName;
    }

    private Object determinBloomNValue(ProceedingJoinPoint joinPoint) {
        //获取方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法的对象
        Method method = signature.getMethod();
        //拿到方法上面的注解（名为GmallCache）
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //拿到注解里面的值并返回
        String expression = annotation.bloomValue();

        Object bloomValue = evaluationExpression(expression,joinPoint,Object.class);

        return bloomValue;
    }

    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        //获取方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法的对象
        Method method = signature.getMethod();
        //拿到方法上面的注解（名为GmallCache）
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //拿到注解里面的值并返回
        String bloomName = annotation.bloomName();

        return bloomName;
    }

    /**
     * 获取返回值类型
     * @param joinPoint
     * @return
     */
    private Type getGenericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Type genericReturnType = method.getGenericReturnType();

        return genericReturnType;
    }

    /**
     * 获取缓存的key
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //获取方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法的对象
        Method method = signature.getMethod();
        //拿到方法上面的注解（名为GmallCache）
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //拿到注解里面的值并返回
        String expression = annotation.cacheKey();
        //计算
        String cacheKey = evaluationExpression(expression,joinPoint,String.class);

        return cacheKey;
    }

    /**
     * 计算缓存key，确定key的实际值
     * @param expression
     * @param joinPoint
     * @param t
     * @param <T>
     * @return
     */
    private<T> T evaluationExpression(String expression, ProceedingJoinPoint joinPoint,
                                        Class<T> t) {

        Expression expression1 = spelExpressionParser.parseExpression(expression, context);

        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();

        Object[] args = joinPoint.getArgs();

        standardEvaluationContext.setVariable("params",args);
        T value = expression1.getValue(standardEvaluationContext, t);
        return value;
    }


}
