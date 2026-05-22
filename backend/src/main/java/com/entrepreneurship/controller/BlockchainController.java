package com.entrepreneurship.controller;

import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.BlockchainRecord;
import com.entrepreneurship.service.BlockchainService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @GetMapping("/project/{projectId}")
    public Result<List<BlockchainRecord>> getProjectRecords(@PathVariable Long projectId) {
        SecurityInputUtil.requirePositiveId(projectId, "项目ID");
        List<BlockchainRecord> records = blockchainService.getRecordsByProject(projectId);
        return Result.ok(records);
    }

    @PostMapping("/project/{projectId}/record")
    public Result<BlockchainRecord> addRecord(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.requirePositiveId(projectId, "项目ID");
        String type = SecurityInputUtil.sanitizeBlockchainType(body.get("type"));
        String data = SecurityInputUtil.sanitizeBlockchainData(body.get("data"));
        BlockchainRecord record = blockchainService.addRecord(projectId, userId, type, data);
        return Result.ok(record);
    }

    @GetMapping("/project/{projectId}/trust-score")
    public Result<Map<String, Object>> getTrustScore(@PathVariable Long projectId) {
        SecurityInputUtil.requirePositiveId(projectId, "项目ID");
        Map<String, Object> score = blockchainService.getTrustScore(projectId);
        return Result.ok(score);
    }

    @GetMapping("/verify/{projectId}")
    public Result<Map<String, Object>> verifyChain(@PathVariable Long projectId) {
        SecurityInputUtil.requirePositiveId(projectId, "项目ID");
        boolean valid = blockchainService.verifyChain(projectId);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("projectId", projectId);
        result.put("valid", valid);
        return Result.ok(result);
    }
}
