package com.ccyang.miaosha.redis;

public class SpikeKey extends BasePrefix{


    public SpikeKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static final SpikeKey isGoodsOver = new SpikeKey(0,"igo");
    public static final SpikeKey getSpikePath = new SpikeKey(60,"igo");
    public static final SpikeKey getSpikeVerifyCode = new SpikeKey(300,"svc");
}
