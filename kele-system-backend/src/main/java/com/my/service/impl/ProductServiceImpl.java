package com.my.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.dto.CreateProductParam;
import com.my.dto.SkuStockParam;
import com.my.mapper.PmsBrandMapper;
import com.my.mapper.PmsCategoryMapper;
import com.my.mapper.PmsProductMapper;
import com.my.mapper.PmsSkuStockMapper;
import com.my.model.PmsBrand;
import com.my.model.PmsCategory;
import com.my.model.PmsProduct;
import com.my.model.PmsSkuStock;
import com.my.query.ProductListQuery;
import com.my.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 20:32
 * @description
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private PmsProductMapper pmsProductMapper;
    @Autowired
    private PmsSkuStockMapper pmsSkuStockMapper;
    @Autowired
    private PmsBrandMapper pmsBrandMapper;
    @Autowired
    private PmsCategoryMapper pmsCategoryMapper;

    @Override
    public PageInfo<PmsProduct> getProductList(ProductListQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());

        String keyword = query.getKeyword();
        List<PmsProduct> pmsProductList = pmsProductMapper.selectByName(keyword);

        PageInfo<PmsProduct> pmsProductPageInfo = new PageInfo<>(pmsProductList);
        return pmsProductPageInfo;
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        PmsProduct pmsProduct = new PmsProduct();
        Boolean newStatus = false;
        if (status == 1) {
            newStatus = true;
        }
        pmsProduct.setPublishStatus(newStatus);
        pmsProduct.setId(Long.valueOf(id));
        int result = pmsProductMapper.updateByPrimaryKeySelective(pmsProduct);
        return result;
    }

    @Override
    public int deleteProduct(Long id) {
        return pmsProductMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateProduct(Long id, PmsProduct pmsProduct) {
        return pmsProductMapper.updateByPrimaryKeySelective(pmsProduct);
    }

    @Override
    @Transactional
    public int insertProductAndSku(CreateProductParam createProductParam) {
        try{
            //存储产品信息
            PmsProduct pmsProduct = new PmsProduct();
            BeanUtils.copyProperties(createProductParam, pmsProduct);
            pmsProductMapper.insertSelective(pmsProduct);
            //存储产品规格
            if (createProductParam.getSkuStockList() != null && !createProductParam.getSkuStockList().isEmpty()) {
                for (SkuStockParam skuStockParam : createProductParam.getSkuStockList()) {
                    PmsSkuStock pmsSkuStock = new PmsSkuStock();
                    BeanUtils.copyProperties(skuStockParam, pmsSkuStock);
                    pmsSkuStock.setProductId(pmsProduct.getId());
                    pmsSkuStockMapper.insertSelective(pmsSkuStock);
                }
            }
            //增加品牌的产品数量
            Long brandId = createProductParam.getBrandId();
            PmsBrand pmsBrand = pmsBrandMapper.selectByPrimaryKey(brandId);
            int productCount = pmsBrand.getProductCount() + 1;
            pmsBrand.setProductCount(productCount);
            pmsBrandMapper.updateByPrimaryKeySelective(pmsBrand);
            //新增商品类型数量
            //二级类型
            Long categoryId = createProductParam.getCategoryId();
            PmsCategory pmsCategory = pmsCategoryMapper.selectByPrimaryKey(categoryId);
            int productCount1 = pmsCategory.getProductCount() + 1;
            pmsCategory.setProductCount(productCount1);
            pmsCategoryMapper.updateByPrimaryKeySelective(pmsCategory);
            //一级类型
            PmsCategory pmsCategory1 = pmsCategoryMapper.selectByPrimaryKey(pmsCategory.getParentId());
            int productCount2 = pmsCategory1.getProductCount() + 1;
            pmsCategory1.setProductCount(productCount2);
            pmsCategoryMapper.updateByPrimaryKeySelective(pmsCategory1);


            return 1;
        } catch (Exception e){
            throw new RuntimeException("商品创建失败,已回滚所有操作",e);
        }
    }

    @Override
    public List<PmsProduct> searchProductList(String keyword) {
        List<PmsProduct> pmsProductList = pmsProductMapper.selectByName(keyword);
        return pmsProductList;
    }


}
