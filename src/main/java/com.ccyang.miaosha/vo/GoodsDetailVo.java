package com.ccyang.miaosha.vo;

import com.ccyang.miaosha.domain.MiaoshaUser;

public class GoodsDetailVo {

    private MiaoshaUser miaoshaUser;
    private GoodsVo goodsVo;
    private int skipeStatus;    // spike status： no start 、 spiking、 end
    private int remainSeconds;  // remain ...time

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public int getSkipeStatus() {
        return skipeStatus;
    }

    public void setSkipeStatus(int skipeStatus) {
        this.skipeStatus = skipeStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }
}
