package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Investment;
import java.util.List;

public interface InvestmentService {
    Investment create(Investment investment);
    Investment update(Long id, Investment investment);
    Investment getById(Long id);
    PageResult<Investment> listByInvestor(Long investorId, int page, int size);
    PageResult<Investment> listByProject(Long projectId, int page, int size);
    PageResult<Investment> listAll(int page, int size);
}
