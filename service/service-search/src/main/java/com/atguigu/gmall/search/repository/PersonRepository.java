package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/3 23:16
 */
@Repository
public interface PersonRepository extends CrudRepository<Person,Long> {
}
