package com.happyshop.service.impl;

import com.alipay.api.domain.ReduceToDiscountDstCampPrizeModel;
import com.google.common.collect.Lists;
import com.happyshop.common.Const;
import com.happyshop.common.ServerResponse;
import com.happyshop.dao.*;
import com.happyshop.pojo.*;
import com.happyshop.service.IOrderService;
import com.happyshop.util.BigDecimalUtil;
import com.happyshop.util.DateTimeUtil;
import com.happyshop.util.PropertiesUtil;
import com.happyshop.vo.OrderItemVo;
import com.happyshop.vo.OrderVo;
import com.happyshop.vo.ShippingVo;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/12/28.
 */
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse createOrder(Integer userId, Integer shippingId) {

        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        //计算这个订单的总价
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){//计算失败
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();//肯定可以强转
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);//计算总金额
        //生成订单
        Order order = this.assembleOrder(userId,shippingId,payment);
        if(order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for(OrderItem orderItem : orderItemList){//设置订单号
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItemList);
        //生成成功,我们要减少我们产品的库存
        this.reduceProductStock(orderItemList);
        //清空一下购物车
        this.cleanCart(cartList);

        //返回给前端数据
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse<String> cancel(Integer userId,Long orderNo){
        Order order  = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("该用户此订单不存在");
        }
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已付款,无法取消订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        // TODO: 2017/12/28 是否需要更新库存呢 
        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(row > 0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    /**
     * 判断购物商品是否符合  (库存和销售状态)
     * @param userId
     * @param cartList
     * @return
     */
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车的数据,包括产品的状态和数量
        for(Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态");
            }

            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    /**
     * 计算总金额
     * @param orderItemList
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**
     * 生成订单号策略
     * @return
     */
    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    /**
     * 更新产品库存
     * @param orderItemList
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * 清空购物车已选的
     * @param cartList
     */
    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * 转为order
     * @param userId
     * @param shippingId
     * @param payment
     * @return
     */
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间等等
        //付款时间等等
        int rowCount = orderMapper.insert(order);
        if(rowCount > 0){
            return order;
        }
        return null;
    }

    /**
     * 转化为orderVo
     * @param order
     * @param orderItemList
     * @return
     */
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));


        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));


        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    /**
     * 转换为shippingVo
     * @param shipping
     * @return
     */
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    /**
     * 转换为OrderItemVo
     * @param orderItem
     * @return
     */
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
}
