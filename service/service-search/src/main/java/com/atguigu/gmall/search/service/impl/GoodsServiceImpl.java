package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.vo.search.*;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    @Autowired
    StringRedisTemplate redisTemplate;

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

    @Override
    public void updateHotScore(Long skuId, Long score) {
        Goods goods = goodsRepository.findById(skuId).get();
        goods.setHotScore(score);
        goodsRepository.save(goods);
    }

    private SearchResponseVo buildSearchResponseVoResult(SearchHits<Goods> goods, SearchParamVo searchParamVo) {

        SearchResponseVo searchResponseVo = new SearchResponseVo();

        searchResponseVo.setSearchParamVo(searchParamVo);

        if (!StringUtils.isEmpty(searchParamVo.getTrademark())) {
            searchResponseVo.setTrademarkParam("品牌："
                    + searchParamVo.getTrademark().split(":")[1]);
        }
//peops=1：aaa:bbb&
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
        //聚合分析品牌列表
        List<TrademarkVo> trademarkVos = buildTrademarkList(goods);
        searchResponseVo.setTrademarkList(trademarkVos);

        //TODO 聚合分析属性列表
        List<AttrVo> attrVoList = buildAttrList(goods);
        searchResponseVo.setAttrsList(attrVoList);

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

    private List<AttrVo> buildAttrList(SearchHits<Goods> goods) {
        List<AttrVo> attrVoList = new ArrayList<>();
        ParsedNested attrAgg = goods.getAggregations().get("attrAgg");

        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            AttrVo attrVo = new AttrVo();
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<String> attrValueList = new ArrayList<>();
            for (Terms.Bucket attrValueBucket : attrValueAgg.getBuckets()) {
                String attrValue = attrValueBucket.getKeyAsString();
                attrValueList.add(attrValue);
            }
            attrVo.setAttrValueList(attrValueList);

            attrVoList.add(attrVo);
        }

        return attrVoList;
    }

    private List<TrademarkVo> buildTrademarkList(SearchHits<Goods> goods) {
        List<TrademarkVo> trademarkVoList = new ArrayList<>();

        ParsedLongTerms tmIdAgg = goods.getAggregations().get("tmIdAgg");
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()) {
            TrademarkVo trademarkVo = new TrademarkVo();

            Long tmId = bucket.getKeyAsNumber().longValue();
            trademarkVo.setTmId(tmId);

            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmName(tmName);

            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogoUrl = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmLogoUrl(tmLogoUrl);

            trademarkVoList.add(trademarkVo);
        }
        return trademarkVoList;
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

        //品牌聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg").field("tmId").size(1000);
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders.terms("tmLogoAgg").field("tmLogoUrl").size(1);
        tmIdAgg.subAggregation(tmLogoAgg);
        query.addAggregation(tmIdAgg);

        //属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);


        TermsAggregationBuilder attrNameAgg = AggregationBuilders .terms("attrNameAgg").field("attrs.attrName").size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100);
        attrIdAgg.subAggregation(attrValueAgg);

        attrAgg.subAggregation(attrIdAgg);

        query.addAggregation(attrAgg);

        return query;
    }
}
