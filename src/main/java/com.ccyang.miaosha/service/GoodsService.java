package com.ccyang.miaosha.service;

import com.ccyang.miaosha.dao.GoodsDao;
import com.ccyang.miaosha.domain.SpikeGoods;
import com.ccyang.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        SpikeGoods g = new SpikeGoods();
        g.setGoodsId(goods.getId());
        int num = goodsDao.reduceStock(g);
        return num > 0;
    }
}
