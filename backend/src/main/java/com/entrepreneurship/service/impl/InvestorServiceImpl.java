package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneurship.entity.InvestorInfo;
import com.entrepreneurship.mapper.InvestorInfoMapper;
import com.entrepreneurship.service.InvestorService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InvestorServiceImpl implements InvestorService {

    private final InvestorInfoMapper investorInfoMapper;

    public InvestorServiceImpl(InvestorInfoMapper investorInfoMapper) {
        this.investorInfoMapper = investorInfoMapper;
    }

    @Override
    public List<InvestorInfo> listAll() {
        return investorInfoMapper.selectList(null);
    }

    @Override
    public InvestorInfo getById(Long id) {
        return investorInfoMapper.selectById(id);
    }

    @Override
    public InvestorInfo getByUserId(Long userId) {
        LambdaQueryWrapper<InvestorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestorInfo::getUserId, userId);
        return investorInfoMapper.selectOne(wrapper);
    }

    @Override
    public void updateInvestorInfo(Long userId, InvestorInfo investorInfo) {
        LambdaQueryWrapper<InvestorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestorInfo::getUserId, userId);
        InvestorInfo existing = investorInfoMapper.selectOne(wrapper);
        if (existing != null) {
            if (investorInfo.getCompany() != null) existing.setCompany(investorInfo.getCompany());
            if (investorInfo.getPosition() != null) existing.setPosition(investorInfo.getPosition());
            if (investorInfo.getInvestmentField() != null) existing.setInvestmentField(investorInfo.getInvestmentField());
            if (investorInfo.getBio() != null) existing.setBio(investorInfo.getBio());
            if (investorInfo.getAvatar() != null) existing.setAvatar(investorInfo.getAvatar());
            investorInfoMapper.updateById(existing);
        }
    }
}
