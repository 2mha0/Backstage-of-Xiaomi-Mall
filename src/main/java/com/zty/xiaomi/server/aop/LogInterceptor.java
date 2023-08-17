package com.zty.xiaomi.server.aop;

import com.zty.xiaomi.server.Entity.Cart.CartResult;
import com.zty.xiaomi.server.Entity.Cart.cartProduct;
import com.zty.xiaomi.server.Entity.Cart.cartProductVoList;
import com.zty.xiaomi.server.Entity.LogInfo;
import com.zty.xiaomi.server.Entity.Product.ProductInfo;
import com.zty.xiaomi.server.Entity.User;
import com.zty.xiaomi.server.Entity.log.*;
import com.zty.xiaomi.server.Service.Cart.CartService;
import com.zty.xiaomi.server.Service.log.LogService;
import com.zty.xiaomi.server.utils.LogWriterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LogInterceptor {
    /**
     * 执行拦截
     */
//    @Around("execution(* com.zty.xiaomi.server.Controller.*.*(..))")
//    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
//        // 计时
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        // 获取请求路径
//        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
//        HttpSession session = httpServletRequest.getSession();
//
//        // 生成请求唯一 id
//        String requestId = UUID.randomUUID().toString();
//        String url = httpServletRequest.getRequestURI();
//        // 获取请求参数
//        Object[] args = point.getArgs();
//        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
//        // 输出请求日志
//        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
//                httpServletRequest.getRemoteHost(), reqParam);
//        // 执行原方法:执行对应的controller方法
//        Object result = point.proceed();
//
//        Object user = session.getAttribute("user");
//        System.out.println("user: " + user);
//
//        // 输出响应日志
//        stopWatch.stop();
//        long totalTimeMillis = stopWatch.getTotalTimeMillis();
//        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
//        return result;
//    }

    @Autowired
    private LogService logService;
    @Autowired
    private CartService cartService;
    private LogInfo logInfo = new LogInfo();
    private LogWriterUtil writer = new LogWriterUtil();
    private static final String FILEPATH = "/Users/zmh/log/xiaomi.log";

    // 需要的信息：请求的唯一id，控制层的url，ip，参数，时间戳，其他对象
    private HttpSession getLog(ProceedingJoinPoint point) throws Throwable {
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpSession session = httpServletRequest.getSession();
        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        // 获取controller的url请求
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]"; // 对参数进行字符串拼接
        // 获取时间戳(毫秒)
        long timestamp = System.currentTimeMillis();

        // 将属性注入实体
        logInfo.setRequestId(requestId);
        logInfo.setUrl(url);
        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        if (ip == null) {
            logInfo.setIp(httpServletRequest.getRemoteHost());
        } else logInfo.setIp(ip);
        logInfo.setReqParam(reqParam);
        logInfo.setTimestamp(timestamp);

        return session;
    }

    /**
     * 对/UserLogin添加拦截(Login方法)
     *
     * @param point
     */
    @Around("execution(* com.zty.xiaomi.server.Controller.LogRegController.Login(..))")
    public Object interceptLogin(ProceedingJoinPoint point) throws Throwable {
        // 执行原方法:执行对应的controller方法
        Object result = point.proceed();
        // 获取相应的log信息
        HttpSession session = getLog(point);

        // 获取用户实体类:该方法需要在point.proceed()被调用后执行，也就是执行完控制层之后获取session域
        User user = (User) session.getAttribute("user");
        logInfo.setOthers(user);
        // 输出请求日志
        LogInterceptor.log.info("请求开始,id: {}, 路径url: {}, ip: {}, 参数: {},时间戳:{},userId:{}", logInfo.getRequestId(), logInfo.getUrl(),
                logInfo.getIp(), logInfo.getReqParam(), logInfo.getTimestamp(), ((User) logInfo.getOthers()).getUserid());
        String log = "id: " + logInfo.getRequestId() + ",路径url: " + logInfo.getUrl() + ",ip: " + logInfo.getIp()
                + ",时间戳: " + logInfo.getTimestamp() + ",userId:" + ((User) logInfo.getOthers()).getUserid() + "\n";
        String url = logInfo.getUrl();
        String ip = logInfo.getIp();
        Long timestamp = logInfo.getTimestamp();
        Logs logs = new Logs();
        logs.setUrl(url);
        logs.setIp(ip);
        logs.setTimestamp(timestamp);
        logs.setTotalPrice(0);
        logs.setTotalNum(0);
        logService.addLog(logs);
        Integer logId = logs.getId();
        LogInterceptor.log.info("logId :" + logId);
        LogsUser logsUser = new LogsUser();
        logsUser.setLogId(logId);
        logsUser.setUserId(user.getUserid());
        logService.addLogUser(logsUser);
        Integer logUserId = logsUser.getId();
        LogInterceptor.log.info("logUserId :" + logUserId);
        writer.writeLog(log, FILEPATH);
        return result;
    }

    /**
     * 对/product/getinfo/{id}添加拦截器(getProductInfo方法)
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.zty.xiaomi.server.Controller.ProductController.getProductInfo(..))")
    public Object interceptGetInfo(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        HttpSession session = getLog(point);
        ProductInfo product = (ProductInfo) session.getAttribute("product");
        logInfo.setOthers(product);
        LogInterceptor.log.info("请求开始,id: {}, 路径url: {}, ip: {}, 参数: {},时间戳:{},productId:{}"
                , logInfo.getRequestId(), logInfo.getUrl(), logInfo.getIp(), logInfo.getReqParam()
                , logInfo.getTimestamp(), product.getProductId());
        String log = "id: " + logInfo.getRequestId() + ",路径url: " + logInfo.getUrl() + ",ip: " + logInfo.getIp()
                + ",时间戳: " + logInfo.getTimestamp() + ",productId:" + product.getProductId() + "\n";
        writer.writeLog(log, FILEPATH);
        String url = logInfo.getUrl();
        String ip = logInfo.getIp();
        Long timestamp = logInfo.getTimestamp();
        Logs logs = new Logs();
        logs.setUrl(url);
        logs.setIp(ip);
        logs.setTimestamp(timestamp);
        logs.setTotalPrice(0);
        logs.setTotalNum(0);
        logService.addLog(logs);
        Integer logId = logs.getId();
        LogsGood logsGood = new LogsGood();
        logsGood.setLogId(logId);
        logsGood.setGoodId(product.getProductId());
        logService.addLogProduct(logsGood);
        Integer logGoodId = logsGood.getGoodId();
        LogInterceptor.log.info("logGoodId :" + logGoodId);
        return result;
    }


    /**
     * 对/push添加拦截器(putShop方法)
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.zty.xiaomi.server.Controller.CartController.putShop(..))")
    public Object interceptPush(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        HttpSession session = getLog(point);
        cartProductVoList productList = (cartProductVoList) session.getAttribute("productList");
        User user = (User) session.getAttribute("user");
        CartResult cartResult = (CartResult) session.getAttribute("cartResult");

        LogInterceptor.log.info("请求开始,id: {}, 路径url: {}, ip: {}, 参数: {},时间戳:{},商品列表:{}" +
                        ",用户id:{},总共价格:{},总共数量:{}", logInfo.getRequestId(), logInfo.getUrl()
                , logInfo.getIp(), logInfo.getReqParam(), logInfo.getTimestamp()
                , productList.printCartProductVoList(), user.getUserid(), cartResult
                        .getCartTotalPrice(), cartResult.getCartTotalQuantity());
        String log = "id: " + logInfo.getRequestId() + ",路径url: " + logInfo.getUrl() + ",ip: " + logInfo.getIp()
                + ",时间戳: " + logInfo.getTimestamp() + ",商品列表: " + productList.printCartProductVoList()
                + ",用户id: " + user.getUserid() + ",总共价格: " + cartResult.getCartTotalPrice() + ",总共数量:" + cartResult.getCartTotalQuantity() + "\n";
        String url = logInfo.getUrl();
        String ip = logInfo.getIp();
        Long timestamp = logInfo.getTimestamp();
        Logs logs = new Logs();
        logs.setUrl(url);
        logs.setIp(ip);
        logs.setTimestamp(timestamp);
        logs.setTotalPrice(0);
        logs.setTotalNum(0);
        logService.addLog(logs);
        Integer logId = logs.getId();
        List<Integer> ids = cartService.getCartIds(user.getUserid());
        if (ids == null) {
            return null;
        }
        for (int cartId : ids) {
            LogsCart logsCart = new LogsCart();
            logsCart.setLogId(logId);
            logsCart.setCartId(cartId);

            logService.addLogCart(logsCart);
            Integer logCartId = logsCart.getCartId();
            LogInterceptor.log.info("logCartId :"+ logCartId);
        }
        writer.writeLog(log, FILEPATH);
        return result;
    }

    @Around("execution(* com.zty.xiaomi.server.Controller.IndexController.getCategory(..))")
    public Object interceptGetCategory(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        getLog(point);
        LogInterceptor.log.info("请求开始,id: {}, 路径url: {}, ip: {}, 参数: {},时间戳:{}", logInfo.getRequestId()
                , logInfo.getUrl(), logInfo.getIp(), logInfo.getReqParam(), logInfo.getTimestamp());
        String log = "id: " + logInfo.getRequestId() + ",路径url: " + logInfo.getUrl() + ",ip: " + logInfo.getIp()
                + ",时间戳: " + logInfo.getTimestamp() + "\n";
        String url = logInfo.getUrl();
        String ip = logInfo.getIp();
        Long timestamp = logInfo.getTimestamp();
        Logs logs = new Logs();
        logs.setUrl(url);
        logs.setIp(ip);
        logs.setTimestamp(timestamp);
        logs.setTotalPrice(0);
        logs.setTotalNum(0);
        logService.addLog(logs);
        Integer logId = logs.getId();
        LogInterceptor.log.info("logId :"+ logId);
        writer.writeLog(log, FILEPATH);
        return result;
    }

    /**
     * 对支付订单进行拦截,拦截的请求是:/orders/pay
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.zty.xiaomi.server.Controller.OrderController.payOrder(..))")
    public Object interceptPayOrder(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        HttpSession session = getLog(point);
        Integer orderNo = (Integer)session.getAttribute("orderNo");
        LogInterceptor.log.info("请求开始,id: {}, 路径url: {}, ip: {}, 参数: {},时间戳:{},orderNo:{}", logInfo.getRequestId()
                , logInfo.getUrl(), logInfo.getIp(), logInfo.getReqParam(), logInfo.getTimestamp(),orderNo);
        String log = "id: " + logInfo.getRequestId() + ",路径url: " + logInfo.getUrl() + ",ip: " + logInfo.getIp()
                + ",时间戳: " + logInfo.getTimestamp() + "orderNo: " + orderNo + "\n";
        String url = logInfo.getUrl();
        String ip = logInfo.getIp();
        Long timestamp = logInfo.getTimestamp();
        Logs logs = new Logs();
        logs.setUrl(url);
        logs.setIp(ip);
        logs.setTimestamp(timestamp);
        logs.setTotalPrice(0);
        logs.setTotalNum(0);
        logService.addLog(logs);
        Integer logId = logs.getId();
        LogsOrder logsOrder = new LogsOrder();
        logsOrder.setLogId(logId);
        logsOrder.setOrderId(orderNo);
        logService.addOrderLog(logsOrder);
        Integer logOrderId = logsOrder.getOrderId();
        LogInterceptor.log.info("logOrderId :"+ logOrderId);
        writer.writeLog(log, FILEPATH);
        return result;
    }
}
