package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author 乔豆麻担
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-24 16:34:13
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Resource
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    BaseAttrValueMapper baseAttrValueMapper;


    @Override
    public List<BaseAttrInfo> attrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id) {

        List<BaseAttrInfo> list = baseAttrInfoMapper.attrInfoAndValueByCategoryId(category1Id,category2Id,category3Id);
        return list;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() == null){
            saveAttr(baseAttrInfo);
        }else {
           editAttr(baseAttrInfo);
        }

    }

    private void saveAttr(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insert(baseAttrInfo);
        for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }
    private void editAttr(BaseAttrInfo baseAttrInfo){
        //修改属性名
        baseAttrInfoMapper.updateById(baseAttrInfo);
        //修改属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //1.删除
        //1.1收集
        ArrayList<Long> vids = new ArrayList<>();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            if (baseAttrValue.getId() != null){
                vids.add(baseAttrValue.getId());
            }
        }
        //1.2删除不在上面的list里的id
        if (vids.size() > 0) {
            QueryWrapper<BaseAttrValue> delete = new QueryWrapper<>();
            delete.eq("attr_id",baseAttrInfo.getId());
            delete.notIn("id",vids);
            baseAttrValueMapper.delete(delete);
        }

        for (BaseAttrValue baseAttrValue : attrValueList) {
            //修改
            if (baseAttrValue.getId() != null){
                baseAttrValueMapper.updateById(baseAttrValue);
            }
            //新增
            if (baseAttrValue.getId() == null){
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }
}




