package com.entrepreneurship.service;

import com.entrepreneurship.entity.InvestorInfo;
import java.util.List;

public interface InvestorService {
    List<InvestorInfo> listAll();
    InvestorInfo getById(Long id);
    InvestorInfo getByUserId(Long userId);
    void updateInvestorInfo(Long userId, InvestorInfo investorInfo);
}
