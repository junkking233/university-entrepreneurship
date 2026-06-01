package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Investment;
import com.entrepreneurship.entity.InvestorInfo;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.mapper.InvestmentMapper;
import com.entrepreneurship.mapper.InvestorInfoMapper;
import com.entrepreneurship.mapper.ProjectMapper;
import com.entrepreneurship.service.InvestmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentMapper investmentMapper;
    private final ProjectMapper projectMapper;
    private final InvestorInfoMapper investorInfoMapper;

    public InvestmentServiceImpl(InvestmentMapper investmentMapper, ProjectMapper projectMapper, InvestorInfoMapper investorInfoMapper) {
        this.investmentMapper = investmentMapper;
        this.projectMapper = projectMapper;
        this.investorInfoMapper = investorInfoMapper;
    }

    @Override
    @Transactional
    public Investment create(Investment investment) {
        if (investment.getStatus() == null || investment.getStatus().isEmpty()) {
            investment.setStatus("pending");
        }
        investment.setCreateTime(LocalDateTime.now());
        investmentMapper.insert(investment);

        Project project = projectMapper.selectById(investment.getProjectId());
        if (project != null) {
            // Note: raisedAmount tracking removed — column not in DB schema
            projectMapper.updateById(project);
        }

        InvestorInfo investor = investorInfoMapper.selectById(investment.getInvestorId());
        if (investor != null) {
            BigDecimal total = investor.getTotalInvestment() != null ? investor.getTotalInvestment() : BigDecimal.ZERO;
            investor.setTotalInvestment(total.add(investment.getAmount()));
            investorInfoMapper.updateById(investor);
        }

        return enrich(investment);
    }

    @Override
    public Investment update(Long id, Investment investment) {
        Investment existing = investmentMapper.selectById(id);
        if (existing != null) {
            if (investment.getAmount() != null) existing.setAmount(investment.getAmount());
            if (investment.getStatus() != null) existing.setStatus(investment.getStatus());
            if (investment.getProjectId() != null) existing.setProjectId(investment.getProjectId());
            investmentMapper.updateById(existing);
        }
        return enrich(existing);
    }

    @Override
    public Investment getById(Long id) {
        return enrich(investmentMapper.selectById(id));
    }

    @Override
    public PageResult<Investment> listByInvestor(Long investorId, int page, int size, String status) {
        LambdaQueryWrapper<Investment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Investment::getInvestorId, investorId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Investment::getStatus, status);
        }
        wrapper.orderByDesc(Investment::getCreateTime);
        Page<Investment> mpPage = new Page<>(page, size);
        Page<Investment> result = investmentMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public PageResult<Investment> listByProject(Long projectId, int page, int size) {
        LambdaQueryWrapper<Investment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Investment::getProjectId, projectId);
        wrapper.orderByDesc(Investment::getCreateTime);
        Page<Investment> mpPage = new Page<>(page, size);
        Page<Investment> result = investmentMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public PageResult<Investment> listAll(int page, int size) {
        LambdaQueryWrapper<Investment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Investment::getCreateTime);
        Page<Investment> mpPage = new Page<>(page, size);
        Page<Investment> result = investmentMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    private Investment enrich(Investment investment) {
        if (investment == null) {
            return null;
        }
        if (investment.getProjectId() != null) {
            Project project = projectMapper.selectById(investment.getProjectId());
            if (project != null) {
                investment.setProjectTitle(project.getTitle());
            }
        }
        investment.setInvestDate(investment.getCreateTime());
        return investment;
    }

    private List<Investment> enrich(List<Investment> investments) {
        investments.forEach(this::enrich);
        return investments;
    }
}
