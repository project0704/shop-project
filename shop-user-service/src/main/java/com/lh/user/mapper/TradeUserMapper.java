package com.lh.user.mapper;

import com.lh.shop.pojo.TradeUser;
import com.lh.shop.pojo.TradeUserExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface TradeUserMapper {
    int countByExample(TradeUserExample example);

    int deleteByExample(TradeUserExample example);

    int deleteByPrimaryKey(Long userId);

    int insert(TradeUser record);

    int insertSelective(TradeUser record);

    List<TradeUser> selectByExample(TradeUserExample example);

    TradeUser selectByPrimaryKey(Long userId);

    int updateByExampleSelective(@Param("record") TradeUser record, @Param("example") TradeUserExample example);

    int updateByExample(@Param("record") TradeUser record, @Param("example") TradeUserExample example);

    int updateByPrimaryKeySelective(TradeUser record);

    int updateByPrimaryKey(TradeUser record);
}