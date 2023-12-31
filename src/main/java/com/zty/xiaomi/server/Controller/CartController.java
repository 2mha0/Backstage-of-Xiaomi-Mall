package com.zty.xiaomi.server.Controller;

import com.zty.xiaomi.server.Entity.Cart.*;
import com.zty.xiaomi.server.Entity.User;
import com.zty.xiaomi.server.Service.Cart.CartServiceImp;
import com.zty.xiaomi.server.Service.Good.GoodServiceImp;
import com.zty.xiaomi.server.Service.RegLogin.RegLogServiceImp;
import com.zty.xiaomi.server.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("carts")
public class CartController {

    @Autowired
    private RegLogServiceImp regLogServiceImp;
    @Autowired
    private GoodServiceImp goodServiceImp;
    @Autowired
    private CartServiceImp cartServiceImp;

    /**
     * 添加到购物车
     * @param pushcart 将用户id与token，商品id封装成的一个类
     * @return CartResult:购物车结果
     */
    @PostMapping("/push")
    public CartResult putShop(@RequestBody pushCart pushcart, HttpSession session) throws IOException {
        CartResult cartResult = new CartResult();
        // 进行token验证，设置为2h过期
        boolean success = TokenUtil.verify(pushcart.getToken());
        if (success) {
            cartResult.setStatus(0);
            User user = regLogServiceImp.getUserByUserName(pushcart.getUsername().trim());
            String userid = user.getUserid();
            // 查找cart表：通过用户id以及商品id查找货物
            CartGood cartGood = goodServiceImp.getGoodByid(userid, pushcart.getProductId());
            // 购物车中该用户没有加入该商品的记录
            if (cartGood == null) {
                int goodCount = 1;
                CartGoodInsert goodByidinsert = goodServiceImp.getGoodByidinsert(pushcart.getProductId());

                System.out.println(pushcart.getProductId());

                goodServiceImp.insCartGood(userid, pushcart.getProductId(), goodByidinsert.getName(),
                        goodByidinsert.getPrice(), goodCount, goodByidinsert.getSubtitle(), goodByidinsert.getProductStock(),
                        goodByidinsert.getStatus(), true);
            } else {
                // 如果购物车已经有了该商品只需要数量+1
                goodServiceImp.updateCartGood(userid, cartGood.getCount() + 1, pushcart.getProductId(), (cartGood.getCount() + 1) * cartGood.getPrice());
            }

            cartProductVoList cartProductVoList = cartServiceImp.getCartProduct(userid);

            int cartTotalPrice = 0;
            boolean selectedAll = true;
            // 循环获取一共的钱
            int cartTotalQuantity = 0;
            for (cartProduct cartProduct : cartProductVoList.getList()) {
                cartTotalPrice += cartProduct.getProductTotalPrice();
                if (!cartProduct.isProductSelected()) {
                    selectedAll = false;
                }
                cartTotalQuantity += cartProduct.getCount();
            }

            cartResult.setStatus(0);
            cartResult.setCartTotalPrice(cartTotalPrice);
            cartResult.setCartTotalQuantity(cartTotalQuantity);
            cartResult.setData(cartProductVoList);
            cartResult.setImageHost("www.mi.com");
            cartResult.setSelectedAll(selectedAll);

            // 向session中放入商品列表，用户对象，购物车结果(包含数量与总价格)
            session.setAttribute("productList",cartProductVoList);
            session.setAttribute("user",user);
            session.setAttribute("cartResult",cartResult);
        } else {
            cartResult.setStatus(10);
        }
        return cartResult;
    }

    // 最开始需要初始化页面：需要呈现右上角的购物车商品数量
    @GetMapping("/sum")
    public CartResult getCartSum(@RequestParam("username") String username) throws IOException {
        CartResult cartResult = new CartResult();

        User user = regLogServiceImp.getUserByUserName(username);
        String userid = user.getUserid();
        cartProductVoList cartProductVoList = cartServiceImp.getCartProduct(userid);
        if(cartProductVoList==null){
            cartResult.setStatus(0);
            cartResult.setCartTotalQuantity(0);
        }
        else {
            int cartTotalQuantity=0;
            for(cartProduct cartProduct:cartProductVoList.getList()){
                cartTotalQuantity+=cartProduct.getCount();
            }
            cartResult.setStatus(0);
            cartResult.setCartTotalQuantity(cartTotalQuantity);
        }
    return cartResult;
    }
    @RequestMapping("/del")
    public CartResult delCarts(@RequestParam("id") int goodid,
                               @RequestParam("username") String username) throws IOException {
        User user = regLogServiceImp.getUserByUserName(username);
        String userid = user.getUserid();
        goodServiceImp.delGoodByid(userid,goodid);
        CartResult cartResult = getCarts(username);
        return cartResult;
    }

    @RequestMapping("/getcarts")
    public CartResult getCarts (@RequestParam("username") String username) throws IOException {
        CartResult cartResult = new CartResult();
        User user = regLogServiceImp.getUserByUserName(username);
        String userid = user.getUserid();
        cartProductVoList cartProductVoList = cartServiceImp.getCartProduct(userid);
        if(cartProductVoList==null){
            cartResult.setStatus(0);
            cartResult.setCartTotalQuantity(0);
            cartResult.setSelectedAll(false);
            cartResult.setImageHost("www.mi.com");
            cartResult.setCartTotalQuantity(0);
        }
        else {
            int carttotalPrice=0;
            boolean selectedall=true;
            int carttotalQuantity=0;
            for(cartProduct cartProduct:cartProductVoList.getList()){
                if(cartProduct.isProductSelected())
                    carttotalPrice+=cartProduct.getProductTotalPrice();
                if(!cartProduct.isProductSelected()){
                    selectedall=false;
                    }
                    carttotalQuantity+=cartProduct.getCount();
                }

                cartResult.setStatus(0);
                cartResult.setCartTotalPrice(carttotalPrice);
                cartResult.setCartTotalQuantity(carttotalQuantity);
                cartResult.setData(cartProductVoList);
                cartResult.setImageHost("www.mi.com");
                cartResult.setSelectedAll(selectedall);
            }
            return cartResult;
        }

        // 商品数量改变时及时更新cart表
        @GetMapping("/updatecart")
        public CartResult getCarts (
                @RequestParam("goodid") int  goodid,
                @RequestParam("username") String username,
                @RequestParam("quantity") int count,
                @RequestParam("selected") boolean selected
        ) throws IOException {
            User user = regLogServiceImp.getUserByUserName(username);
            String userid = user.getUserid();

            CartGood goodByid = goodServiceImp.getGoodByid(userid, goodid);
            goodServiceImp.updaCartNumSel(userid,goodid,count,selected,goodByid.getPrice()*count);
            CartResult carts = getCarts(username);
            return carts;
        }

        @GetMapping("/selectAll")
        public CartResult SelectAllCart(@RequestParam("username") String username) throws IOException {
            User user = regLogServiceImp.getUserByUserName(username);
            String userid = user.getUserid();
            goodServiceImp.updaCartSelect(userid);
            CartResult cartResult = getCarts(username);
            return cartResult;
        }

        @GetMapping("/unSelectAll")
        public CartResult unSelectAllCart(@RequestParam("username") String username) throws IOException {
            User user = regLogServiceImp.getUserByUserName(username);
            String userid = user.getUserid();
            goodServiceImp.updaCartunSelect(userid);
            CartResult cartResult = getCarts(username);
            cartResult.setCartTotalQuantity(0);
            cartResult.setCartTotalPrice(0);
            return cartResult;
    }
}
