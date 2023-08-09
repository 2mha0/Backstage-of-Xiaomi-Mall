package com.zty.xiaomi.server.Controller;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.zty.xiaomi.server.Entity.ProductFoot.ProductFootInfo;
import com.zty.xiaomi.server.Entity.ProductFoot.ProductFootResult;
import com.zty.xiaomi.server.Entity.ProductHead.ProductHeadInfo;
import com.zty.xiaomi.server.Entity.ProductHead.ProductHeadResult;
import com.zty.xiaomi.server.Entity.Suggest.SuggestFoot;
import com.zty.xiaomi.server.Entity.Suggest.SuggestFootResult;
import com.zty.xiaomi.server.Entity.index.CategoryResult;
import com.zty.xiaomi.server.Service.Category.CategoryIndexImp;
import com.zty.xiaomi.server.Service.ProdFoot.ProductFootImp;
import com.zty.xiaomi.server.Service.ProdHead.ProductHeadImp;
import com.zty.xiaomi.server.Service.Suggest.SugFootServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CategoryIndexImp categoryIndexImp;
    @Autowired
    private ProductHeadImp productHeadImp;
    @Autowired
    private ProductFootImp productFootImp;
    @Autowired
    private SugFootServiceImp sugFootServiceImp;

    /**
     * 布隆过滤器
     * 可以用它来回答给定元素是否存在集合中的问题：
     * 1. 不存在漏报：当它返回false时，我们可以 100% 确定该元素不在集合中
     * 2. 存在误报： 当它返回true时，该元素很有可能在集合中，但我们不能 100% 确定
     * 作用：节省空间且速度快，一些数据库使用此过滤器作为进入磁盘或缓存之前的首次检查。eg.当收到对特定 ID 的请求时，
     * 如果过滤器返回 ID 不存在，则数据库可以停止进一步处理请求并返回给客户端。否则，它会转到磁盘并返回该元素。
     * https://www.baeldung.com/guava-bloom-filter
     */
    private  BloomFilter<Integer> bf = BloomFilter.create(Funnels.integerFunnel(), 4);

    @RequestMapping("/category")
    public CategoryResult getCategory() throws IOException {
        CategoryResult categoryResult = new CategoryResult();
        categoryResult.setStatus(0);
        categoryResult = categoryIndexImp.getCategoryGoods();
        return categoryResult;
    }

    @RequestMapping("/product")
    public ProductHeadResult getProduct(@RequestParam("categoryId") int categoryId) throws IOException {

        bf.put(1);bf.put(2);bf.put(3);bf.put(9);

        ProductHeadResult productHeadResult = new ProductHeadResult();

        if(!bf.mightContain(categoryId)){
            productHeadResult.setStatus(500);
            return productHeadResult;
        }
        productHeadResult.setStatus(0);
        List<ProductHeadInfo> productHeadInfo = productHeadImp.getProductHeadInfo(categoryId);
        productHeadResult.setData(productHeadInfo);
        return productHeadResult;
    }

    @RequestMapping("/productfoot")
    public ProductFootResult getProductFoot(@RequestParam("categoryId") int categoryId) {

        ProductFootResult productFootResult = new ProductFootResult();
        productFootResult.setStatus(0);
        List<ProductFootInfo> productFootInfo = productFootImp.getProductFootInfo(categoryId);
        productFootResult.setData(productFootInfo);
        return productFootResult;
    }

    @RequestMapping("/productfootnormal")
    public ProductFootResult getProductFootNormal(@RequestParam("categoryId") int categoryId) throws IOException {

        ProductFootResult productFootResult = new ProductFootResult();
        productFootResult.setStatus(0);
        List<ProductFootInfo> productFootInfo = productFootImp.getProductFootInfoNormal(categoryId);
        productFootResult.setData(productFootInfo);
        return productFootResult;
    }


    @RequestMapping("/suggest")
    public SuggestFootResult getSugFoot() throws IOException {
        SuggestFootResult suggestFootResult = new SuggestFootResult();
        suggestFootResult.setStatus(200);
        List<SuggestFoot> sugFoot = sugFootServiceImp.getSugFoot();
        suggestFootResult.setSugglist(sugFoot);
        return  suggestFootResult;
    }
}
