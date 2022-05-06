package book.pojo;

import java.util.Map;
import java.util.Set;

public class Cart {
    // 购物车项的集合，key 是 Book 的 id
    private Map<Integer, CartItem> cartItemMap;
    // 购物车总金额
    private Double totalMoney;
    // 购物车中 cartItem 的数量
    private Integer totalCount;
    // 购物车中书本的总数
    private Integer totalBookCount;

    public Cart() {}

    public Map<Integer, CartItem> getCartItemMap() {
        return cartItemMap;
    }

    public void setCartItemMap(Map<Integer, CartItem> cartItemMap) {
        this.cartItemMap = cartItemMap;
    }

    public Double getTotalMoney() {
        totalMoney = 0.0;
        if (cartItemMap != null && cartItemMap.size() > 0) {
            Set<Map.Entry<Integer, CartItem>> entries = cartItemMap.entrySet();
            for (Map.Entry<Integer, CartItem> cartItemEntry : entries) {
                CartItem cartItem = cartItemEntry.getValue();
                totalMoney += cartItem.getBook().getPrice() * cartItem.getBuyCount();
            }
        }
        return totalMoney;
    }

    public Integer getTotalCount() {
        totalCount = 0;
        if (cartItemMap != null && cartItemMap.size() > 0) {
            totalCount = cartItemMap.size();
        }
        return totalCount;
    }


    public Integer getTotalBookCount() {
        totalBookCount = 0;
        if (cartItemMap != null && cartItemMap.size() > 0) {
            for (CartItem cartItem : cartItemMap.values()) {
                totalBookCount += cartItem.getBuyCount();
            }
        }
        return totalBookCount;
    }

}
