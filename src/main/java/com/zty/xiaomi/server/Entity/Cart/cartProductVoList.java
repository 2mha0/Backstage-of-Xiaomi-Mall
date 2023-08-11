package com.zty.xiaomi.server.Entity.Cart;

import lombok.Data;

import java.util.List;

@Data
public class cartProductVoList {
    private List<cartProduct> list;

    public String printCartProductVoList() {
        String str = "";
//        for (cartProduct product:list) {
//            str += product.printCartProduct() + ",";
//        }
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i).printCartProduct();
            // 最后一个","不打印
            if (i != list.size()-1){
                str += ",";
            }
        }
        return "cartProductVoList{" +
                "list=" + str +
                '}';
    }
}
