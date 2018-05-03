package com.ccyang.miaosha.vo;

import com.ccyang.miaosha.domain.Goods;

import java.util.Date;

/**
 * 封装 商品和秒杀商品的信息
 */
public class GoodsVo extends Goods{

    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    private Double spikePrice;

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getSpikePrice() {
        return spikePrice;
    }

    public void setSpikePrice(Double spikePrice) {
        this.spikePrice = spikePrice;
    }

    @Override
    public String toString() {
        return "GoodsVo{" +
                "stockCount=" + stockCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", spikePrice=" + spikePrice +
                '}';
    }
}
