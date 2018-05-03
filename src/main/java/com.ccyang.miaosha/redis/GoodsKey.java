package com.ccyang.miaosha.redis;

/**
 * goods 商品页面缓存 key
 */
public class GoodsKey extends BasePrefix {

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // set expire time = 120s
    public static GoodsKey goodsList = new GoodsKey(120,"gl");
    public static GoodsKey goodsDetail = new GoodsKey(120,"gd");
    public static GoodsKey spikeGoodsStock = new GoodsKey(0,"gs");

}
