package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.google.common.collect.Lists;
import com.atguigu.gmall.model.vo.search.OrderMapVo;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/5 20:27
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    ElasticsearchRestTemplate restTemplate;

    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        //构建检索条件
        Query query = buildQueryDSL(searchParamVo);
        //查询
        SearchHits<Goods> goods = restTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));
        //转换
        SearchResponseVo searchResponseVo = buildSearchResponseVoResult(goods, searchParamVo);

        return searchResponseVo;
    }

    private SearchResponseVo buildSearchResponseVoResult(SearchHits<Goods> goods, SearchParamVo searchParamVo) {

        SearchResponseVo searchResponseVo = new SearchResponseVo();

        searchResponseVo.setSearchParamVo(searchParamVo);

        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            searchResponseVo.setTrademarkParam("品牌："
                    + searchParamVo.getTrademark().split(":")[1]);
        }

        if (searchParamVo.getProps() != null && searchParamVo.getProps().size() > 0) {
            List<SearchAttr> propsParamsList = new ArrayList<>();
            for (String prop : searchParamVo.getProps()) {
                String[] split = prop.split(":");
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrValue(split[1]);
                searchAttr.setAttrName(split[2]);
                propsParamsList.add(searchAttr);
            }
            searchResponseVo.setPropsParamList(propsParamsList);
        }
        //TODO 聚合分析品牌列表
        searchResponseVo.setTrademarkList(Lists.newArrayList());
        //TODO 聚合分析属性列表
        searchResponseVo.setAttrsList(Lists.newArrayList());

        if (!StringUtils.isEmpty(searchParamVo.getOrder())) {
            String order = searchParamVo.getOrder();
            OrderMapVo mapVo = new OrderMapVo();
            mapVo.setType(order.split(":")[0]);
            mapVo.setSort(order.split(":")[1]);
            searchResponseVo.setOrderMap(mapVo);
        }
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> searchHits = goods.getSearchHits();
        for (SearchHit<Goods> searchHit : searchHits) {
            Goods content = searchHit.getContent();
            if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
                String title = searchHit.getHighlightField("title").get(0);
                content.setTitle(title);
            }
            goodsList.add(content);
        }
        searchResponseVo.setGoodsList(goodsList);


        searchResponseVo.setPageNo(searchResponseVo.getPageNo());

        long totalHits = goods.getTotalHits();
        Long ps = totalHits % SysRedisConstant.PAGESIZE == 0 ?
                totalHits / SysRedisConstant.PAGESIZE :
                (totalHits / SysRedisConstant.PAGESIZE + 1);

        searchResponseVo.setTotalPages(new Integer(ps + ""));

        String url = makeUrlParam(searchParamVo);
        searchResponseVo.setUrlParam(url);


        return searchResponseVo;
    }

    private String makeUrlParam(SearchParamVo searchParamVo) {
        StringBuilder builder = new StringBuilder("list.html?");
        if (searchParamVo.getCategory1Id() != null) {
            builder.append("&category1Id=" + searchParamVo.getCategory1Id());
        }
        if (searchParamVo.getCategory2Id() != null) {
            builder.append("&category2Id=" + searchParamVo.getCategory2Id());
        }
        if (searchParamVo.getCategory3Id() != null) {
            builder.append("&category3Id=" + searchParamVo.getCategory3Id());
        }
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            builder.append("&keyword=" + searchParamVo.getKeyword());
        }
        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            builder.append("&trademark=" + searchParamVo.getTrademark());
        }
        if (searchParamVo.getProps() != null && searchParamVo.getProps().size() > 0) {
            for (String prop : searchParamVo.getProps()) {
                builder.append("&props=" + prop);
            }
        }

        String url = builder.toString();
        return url;
    }

    private Query buildQueryDSL(SearchParamVo searchParamVo) {

        //2.创建queryBuilder;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (searchParamVo.getCategory1Id() != null) {
            queryBuilder
                    .must(QueryBuilders
                            .termQuery("category1Id"
                                    , searchParamVo.getCategory1Id()));
        }

        if (searchParamVo.getCategory2Id() != null) {
            queryBuilder
                    .must(QueryBuilders
                            .termQuery("category2Id"
                                    , searchParamVo.getCategory2Id()));
        }

        if (searchParamVo.getCategory3Id() != null) {
            queryBuilder
                    .must(QueryBuilders
                            .termQuery("category3Id"
                                    , searchParamVo.getCategory3Id()));
        }

        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            queryBuilder
                    .must(QueryBuilders
                            .matchQuery("title", searchParamVo.getKeyword()));
        }

        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            Long tmId = Long.parseLong(searchParamVo.getTrademark().split(":")[0]);
            queryBuilder
                    .must(QueryBuilders
                            .termQuery("tmId", tmId));
        }

        //props=4:128GB:机身存储&props=5:骁龙730:CPU型号
        List<String> props = searchParamVo.getProps();
        if (props != null && props.size() > 0) {
            for (String prop : props) {

                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];

                BoolQueryBuilder nestedQuery = QueryBuilders.boolQuery();
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedQuery.must(QueryBuilders.termQuery("attrs.attrValue", attrValue));

                NestedQueryBuilder nestedQueryBuilder =
                        QueryBuilders.nestedQuery("attrs", nestedQuery, ScoreMode.None);
                queryBuilder.must(nestedQueryBuilder);
            }
        }

        //排序 前端传了排序 order=2:asc
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);
        String order = searchParamVo.getOrder();
        String[] split = order.split(":");
        String sortFile = "hotScore";
        switch (split[0]) {
            case "1":
                sortFile = "hotScore";
                break;
            case "2":
                sortFile = "price";
                break;
            case "3":
                sortFile = "createTime";
                break;
            default:
                sortFile = "hotScore";
        }
        Sort sort = Sort.by(sortFile);
        if (split[1].equals("asc")) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        query.addSort(sort);

        //页码
        PageRequest request =
                PageRequest.of(searchParamVo.getPageNo() - 1, SysRedisConstant.PAGESIZE);
        query.setPageable(request);

        //高亮
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");

            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);

            query.setHighlightQuery(highlightQuery);
        }

        //TODO 平台属性


        return query;
    }
}
