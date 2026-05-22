package com.entrepreneurship.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entrepreneurship.entity.Investment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvestmentMapper extends BaseMapper<Investment> {
}
