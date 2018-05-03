package com.ccyang.miaosha.dao;

import com.ccyang.miaosha.domain.OrderInfo;
import com.ccyang.miaosha.domain.SpikeOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from order_info where user_id = #{userId} and goods_id = #{goodsId}")
    SpikeOrder getSpikeOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date) "
            +"values(#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyProperty ="id",keyColumn="id",resultType=long.class,before=false,statement="select last_insert_id()")
    long insertOrderInfo(OrderInfo orderInfo);

    @Insert("insert into spike_order(user_id,order_id,goods_id) values(#{userId},#{orderId},#{goodsId})")
    int insertSpikeOrder(SpikeOrder spikeOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
