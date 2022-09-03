package com.atguigu.gmall.search.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/3 23:10
 */
@Data
public class Person {
    @Id
    private Long id;

    @Field(value = "first",type = FieldType.Keyword)
    private String firstName;

    @Field(value = "last",type = FieldType.Keyword)
    private String lastName;

    @Field(value = "age")
    private String age;

    @Field(value = "address")
    private String address;
}
