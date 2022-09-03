package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/3 23:21
 */
@Repository
public interface GoodsRepository extends CrudRepository<Goods,Long> {
}
