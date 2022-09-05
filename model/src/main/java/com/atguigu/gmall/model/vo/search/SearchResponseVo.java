package com.atguigu.gmall.model.vo.search;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuAttrValue;
import lombok.Data;

import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 21:39
 */
@Data
public class SearchResponseVo {

    private SearchParamVo searchParamVo;

    private String trademarkParam;

    private List<SearchAttr> propsParamList;

    private List<TrademarkVo> trademarkList;

    private List<AttrVo> attrsList;


    private OrderMapVo orderMap;
    private List<Goods> goodsList;
    private Integer pageNo = 1;
    private Integer totalPages;
    private String urlParam;

}
