package com.zty.xiaomi.server.Mapper;

import com.zty.xiaomi.server.Entity.LogInfo;
import com.zty.xiaomi.server.Entity.log.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @pakage com.zty.xiaomi.server.Mapper
 * @auther 邮专第一深情
 * @Date 2023/8/11 10:07
 * @Descripetion
 */
@Component
public interface Log {

    @Insert("insert into Logs(url,ip,`timestamp`,totalPrice,totalNum)values(#{url},#{ip},#{timestamp}," +
            "#{totalPrice},#{totalNum})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insert(Logs logs);

    @Insert("insert into logs_user(log_id,user_id)values(#{logId},#{userId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertLogUser(LogsUser logsUser);

    @Insert("insert into logs_good(log_id,good_id)values(#{logId},#{goodId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertLogProduct(LogsGood logsGood);

    @Insert("insert into logs_cart(log_id,cart_id)values(#{logId},#{cartId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertLogCart(LogsCart logsCart);

    @Insert("insert into logs_order(log_id,order_id)values(#{logId},#{orderId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void addOrderLog(LogsOrder logsOrder);

    @Select("select * from Logs")
    List<LogInfo> list();

}
