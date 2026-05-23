package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.Investment;
import com.entrepreneurship.entity.InvestorInfo;
import com.entrepreneurship.service.InvestmentService;
import com.entrepreneurship.service.InvestorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investment")
public class InvestmentController {

    private final InvestmentService investmentService;
    private final InvestorService investorService;

    public InvestmentController(InvestmentService investmentService, InvestorService investorService) {
        this.investmentService = investmentService;
        this.investorService = investorService;
    }

    @PostMapping
    public Result<Investment> create(@RequestBody Investment investment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.sanitize(investment);
        InvestorInfo investor = investorService.getByUserId(userId);
        if (investor == null) {
            return Result.error("只有投资人可以进行投资");
        }
        investment.setInvestorId(investor.getId());
        return Result.ok(investmentService.create(investment));
    }

    @GetMapping("/{id}")
    public Result<Investment> getById(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "投资ID");
        return Result.ok(investmentService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Investment> update(@PathVariable Long id, @RequestBody Investment investment) {
        SecurityInputUtil.requirePositiveId(id, "投资ID");
        SecurityInputUtil.sanitize(investment);
        return Result.ok(investmentService.update(id, investment));
    }

    @GetMapping("/my")
    public Result<PageResult<Investment>> listMy(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        InvestorInfo investor = investorService.getByUserId(userId);
        if (investor == null) {
            return Result.error("不是投资人");
        }
        return Result.ok(investmentService.listByInvestor(investor.getId(), SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }

    @GetMapping("/project/{projectId}")
    public Result<PageResult<Investment>> listByProject(@PathVariable Long projectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SecurityInputUtil.requirePositiveId(projectId, "项目ID");
        return Result.ok(investmentService.listByProject(projectId, SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }

    @GetMapping("/list")
    public Result<PageResult<Investment>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(investmentService.listAll(SecurityInputUtil.page(page), SecurityInputUtil.size(size)));
    }

    @GetMapping("/investor/profile")
    public Result<InvestorInfo> getInvestorProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(investorService.getByUserId(userId));
    }

    @PutMapping("/investor/profile")
    public Result<?> updateInvestorProfile(@RequestBody InvestorInfo investorInfo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.sanitize(investorInfo);
        investorService.updateInvestorInfo(userId, investorInfo);
        return Result.ok("更新成功");
    }
}
