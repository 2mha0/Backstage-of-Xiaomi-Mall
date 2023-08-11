package com.zty.xiaomi.server.Controller;

import com.zty.xiaomi.server.Entity.Product.ProductInfo;
import com.zty.xiaomi.server.Entity.Product.ProductResult;
import com.zty.xiaomi.server.Service.Product.ProdServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProdServiceImp prodServiceImp;
    @RequestMapping("getinfo")
    public ProductResult getProductInfo(@RequestParam("id") int id, HttpSession session) throws IOException {
        ProductResult productResult = new ProductResult();
        productResult.setStatus(0);
        ProductInfo productInfo = prodServiceImp.getProductInfo(id);
        productResult.setData(productInfo);
        // 将商品信息注入到session中
        productInfo.setProductId(String.valueOf(id));
        session.setAttribute("product",productInfo);
        return productResult;
    }

}
