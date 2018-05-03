create table `spike_user` (
  `id` bigint(20) not null comment '用户ID,手机号码',
  `nickname` varchar(255) not null,
  `password` varchar(32) default null comment 'md5(md5(pass + 固定salt) + salt)',
  `salt` varchar(10) default null,
  `head` varchar(128) default null comment '头像,云存储id',
  `register_date` datetime default null comment '注册时间',
  `last_login_date` datetime default null comment '上次登录时间',
  `login_count` int(11) default '0' comment '登录次数',
  primary key (`id`)
)engine=InnoDB DEFAULT charset=utf8;

CREATE TABLE `goods` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `goods_name` VARCHAR(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` VARCHAR(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` VARCHAR(64) DEFAULT NULL COMMENT '商品图片',
  `goods_detail` LONGTEXT COMMENT '商品的详细介绍',
  `goods_price` DECIMAL(10, 2) DEFAULT '0.00' COMMENT '商品单价',
  `goods_stock` INT(11) DEFAULT '0' COMMENT '商品库存, -1表示没有限制',
  PRIMARY KEY (`id`)
)ENGINE = INNODB AUTO_INCREMENT = 3 CHARSET = utf8;


CREATE TABLE spike_goods (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品id',
  `spike_price` DECIMAL(10, 2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock_count` INT(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` DATETIME DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` DATETIME DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
)ENGINE = INNODB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8;


CREATE TABLE `order_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户id',
  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品id',
  `delivery_addr_id` BIGINT(20) DEFAULT NULL COMMENT '收货地址id',
  `goods_name` VARCHAR(16) DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` INT(11) DEFAULT '0' COMMENT '商品数量',
  `goods_price` DECIMAL(10, 2) DEFAULT '0.00' COMMENT '商品单价',
  `order_channel` TINYINT(4) DEFAULT '0' COMMENT '1 pc 2 android 3 ios',
  `status` TINYINT(4) DEFAULT '0' COMMENT '订单状态 0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` DATETIME DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` DATETIME DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
)ENGINE = INNODB AUTO_INCREMENT = 12 DEFAULT CHARSET = utf8;


CREATE TABLE `spike_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户id',
  `order_id` BIGINT(20) DEFAULT NULL COMMENT '订单id',
  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品id',
  PRIMARY KEY (`id`)
)ENGINE = INNODB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8;

INSERT INTO goods VALUES
  (1, 'iphoneX', 'Apple iPhone X(A1865)', '/img/iphonex.png', 'Apple iPhone X', 8765, 100),
  (2, '华为Mate9', '华为 Mate9 4GB+32GB', '/img/mate9.png', '华为Mate9 4GB+32GB', 3212, 10);

INSERT INTO spike_goods VALUES
  (1, 1, 0.01, 4, '2018-04-16 00:00:00', '2018-05-01 00:00:00'),
  (2, 2, 0.01, 9, '2018-04-16 00:00:00', '2018-05-01 00:00:00');

#避免重复秒杀商品
create unique index u_uid_gid on spike_order(`user_id`, `goods_id`);