package com.atguigu.gmall.item;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.UUID;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/1 23:28
 */
public class spelTest {

    /**
     * 简单测试
     */
    @Test
    public void test1(){
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        //表达式
        String hello = "hello #{1 + 1}";

        Expression expression = spelExpressionParser.parseExpression(hello,new TemplateParserContext());
        Object value = expression.getValue();
        System.out.println(value);

    }

    /**
     * 测试绑定
     */
    @Test
    public void test2(){
        //准备的数组
        Object[] params1 = {45,50};
        Object[] params2 = {55,60};
        Object[] params3 = {65,70};
        //解析器
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        //字符串
        String key = SysRedisConstant.SKU_INFO_PREFIX + "#{#params[0]}";
        //模板
        Expression expression = spelExpressionParser.parseExpression(key, new TemplateParserContext());

        //计算器
        StandardEvaluationContext context = new StandardEvaluationContext();
        //绑定
         context.setVariable("params", params2);

        String value = expression.getValue(context, String.class);
        System.out.println(value);
    }

    @Test
    public void test3(){
        SpelExpressionParser parser = new SpelExpressionParser();

        String s = "new int[] {1,2,3,4}";

        Expression expression = parser.parseExpression(s);

        int[] value = (int[]) expression.getValue();
        System.out.println(value);
        for (int i : value){
            System.out.println("i = " + i);
        }
    }

    @Test
    public void test4(){
        SpelExpressionParser parser = new SpelExpressionParser();
//        Expression expression = parser.parseExpression("T(java.util.UUID).randomUUID().toString()");
        Expression expression = parser.parseExpression("haha- #{T(java.util.UUID).randomUUID().toString()}",new TemplateParserContext());

        Object value = expression.getValue();
        System.out.println("value = " + value);


    }




}
