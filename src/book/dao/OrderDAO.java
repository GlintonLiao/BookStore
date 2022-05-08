package book.dao;

import book.pojo.OrderBean;
import book.pojo.User;

import java.util.List;

public interface OrderDAO {
    // 添加订单
    void addOrderBean(OrderBean orderBean);
    // 获取指定用户的订单列表
    List<OrderBean> getOrderList(User user);
    // 获取书本数量
    Integer getOrderTotalBookCount(OrderBean orderBean);
}
