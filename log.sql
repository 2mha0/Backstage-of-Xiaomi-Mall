drop table logs_user;
drop table logs_cart;
drop table logs;
drop table userinfo;


CREATE TABLE `userinfo` (
                            `userid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
                            `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
                            `email` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
                            `pwd` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
                            `tel` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
                            `status` varchar(5) COLLATE utf8mb4_bin NOT NULL,
                            `date` varchar(20) COLLATE utf8mb4_bin NOT NULL,
                            `privatekey` varchar(500) COLLATE utf8mb4_bin NOT NULL
);


INSERT INTO `userinfo` VALUES ('c8ad1c60-4718-476b-983d-d8eb07c12963','ZTY','zty1119094861@sina.com','VKmfzfw4hbjIrqxeX15yPx_-67yG52EtEGLTHTvKU6rJPqS49aiJXOL71DBfHACFcCYl7lQN8jbo7QssCe9y4Q','18873242003','可用','2021/01/01 19:03:47','MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA4fXACG9h1FM1XZMnThRfD1en5yHQjshwpvB2_6axZwhM0utXG10eHqLalN68tNRZnFs7UdFaSCIkA5GE4Qxl6QIDAQABAkAHxz3lDHCBrpuXrQkdVvYuwQW2nyNexueZ141BH6vbsAzkflKduoYPcscU7SRKuY5sJ5zWzKcMYvEArd0zmUgBAiEA9uQoFOgh0MYyYm0bNAjcxIkhT9FPGrK-ThMqya5XJ7kCIQDqS-bDjlYbMU8MKRmjA1w31M-YKhkwCHR47rbbtoLnsQIgFUIh_WLbfomCTx1L6VgWAko4dxHzuL3JSxm1ZnY_hOkCIQC9861yjFp5D2AD_upfBCFmzm1D85WaILC1PuwXc3_r8QIhAMH77H8eqvRojdyjzOGG0nPjitrFntD68YUfYn4mXrAG');
INSERT INTO `userinfo` VALUES ('2814fb5b-622c-4e43-83f3-8ba51567c36a','zmh','2250370773@qq.com','M0wmCh42cHIs1cGkyYgE8GqSbKMZrFw9QLhbxcx-gvlExoqSnFVpHWry5GxFyLIesPuBPl_QefoLI5uON9M06Q','15023119606','可用','2023/08/08 14:45:09','MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAs0-AnixWrhg2r2Skls3mOLqPcWxZdqaKWJr5pjPQoPb443YSkuFab1DrskXrx4-nwgh4ZgVXYGQZ9DqibXjZoQIDAQABAj9hda_htSEBhPooxuj-Y-talM2sdPwJOmrg65Pn29XQznH2xniB9c4rWgPGeO-dkskcgLul0gyYIHaUKVHAlykCIQDblXaFBd147RsqxzH-f1QSlBC8e0wkrkNCovDIBF_dNQIhANEMN8mUpQZVF02ikmdTMWu65fhFnTKWP4WJqgnAFRQ9AiBFwhHI6LgXNpwu1uFXDHRVApzeg_0ImiPcA4-iu2EJEQIhALJo9wO3NNAetvCvGrvMKJWEwNgOsYaG7FePkesYX-URAiB-DgV0uJgLb7NYJ9N54gir-8hIjDTvM52UhWWRPY8nHg');

# 创建logs表
create table logs
(
    id int primary key auto_increment,
    url varchar(50),
    ip varchar(20),
    timestamp long,
    totalPrice long,
    totalNum long
);
# 创建logs表与user表的关系表
create table logs_user
(
    id int primary key auto_increment,
    log_id int,
    user_id varchar(50) not null
);
# 创建logs与cart表的关系表
create table logs_cart
(
    id int primary key auto_increment,
    log_id int,
    cart_id int
);

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for logs_good
-- ----------------------------
DROP TABLE IF EXISTS `logs_good`;
CREATE TABLE `logs_good`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `log_id` int(11) NULL DEFAULT NULL,
                              `good_id` int(11) NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `logs_good_fk1`(`log_id`) USING BTREE,
                              INDEX `logs_good_fk2`(`good_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for logs_order
-- ----------------------------
DROP TABLE IF EXISTS `logs_order`;
CREATE TABLE `logs_order`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `log_id` int(11) NULL DEFAULT NULL,
                               `order_id` int(11) NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
