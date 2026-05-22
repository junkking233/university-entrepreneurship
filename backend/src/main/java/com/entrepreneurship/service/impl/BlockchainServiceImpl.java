package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneurship.entity.BlockchainRecord;
import com.entrepreneurship.mapper.BlockchainRecordMapper;
import com.entrepreneurship.service.BlockchainService;
import com.entrepreneurship.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    private final BlockchainRecordMapper blockchainRecordMapper;
    private final UserService userService;

    public BlockchainServiceImpl(BlockchainRecordMapper blockchainRecordMapper, UserService userService) {
        this.blockchainRecordMapper = blockchainRecordMapper;
        this.userService = userService;
    }

    @Override
    public BlockchainRecord addRecord(Long projectId, Long userId, String type, String data) {
        BlockchainRecord record = new BlockchainRecord();
        record.setProjectId(projectId);
        record.setUserId(userId);
        record.setType(type);
        record.setData(data);
        record.setTimestamp(System.currentTimeMillis());

        LambdaQueryWrapper<BlockchainRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlockchainRecord::getProjectId, projectId);
        wrapper.orderByDesc(BlockchainRecord::getId);
        wrapper.last("LIMIT 1");
        BlockchainRecord lastRecord = blockchainRecordMapper.selectOne(wrapper);

        String previousHash = lastRecord != null ? lastRecord.getHash() : "0";
        record.setPreviousHash(previousHash);

        String raw = projectId + "|" + userId + "|" + type + "|" + data + "|" + record.getTimestamp() + "|" + previousHash;
        record.setHash(userService.md5(raw));
        record.setCreateTime(LocalDateTime.now());

        blockchainRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<BlockchainRecord> getRecordsByProject(Long projectId) {
        LambdaQueryWrapper<BlockchainRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlockchainRecord::getProjectId, projectId);
        wrapper.orderByAsc(BlockchainRecord::getId);
        return blockchainRecordMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getTrustScore(Long projectId) {
        List<BlockchainRecord> records = getRecordsByProject(projectId);
        int totalRecords = records.size();
        int positiveRecords = 0;

        for (BlockchainRecord r : records) {
            if ("investment".equals(r.getType()) || "approval".equals(r.getType()) || "milestone".equals(r.getType())) {
                positiveRecords++;
            }
        }

        double score = totalRecords > 0 ? (double) positiveRecords / totalRecords * 100 : 0;
        boolean verified = verifyChain(projectId);

        Map<String, Object> result = new HashMap<>();
        result.put("projectId", projectId);
        result.put("totalRecords", totalRecords);
        result.put("trustScore", Math.round(score * 10.0) / 10.0);
        result.put("verified", verified);
        result.put("records", records);
        return result;
    }

    @Override
    public boolean verifyChain(Long projectId) {
        List<BlockchainRecord> records = getRecordsByProject(projectId);
        if (records.isEmpty()) return true;

        for (int i = 0; i < records.size(); i++) {
            BlockchainRecord current = records.get(i);
            String expectedPrevHash = i == 0 ? "0" : records.get(i - 1).getHash();
            if (!expectedPrevHash.equals(current.getPreviousHash())) {
                return false;
            }
            String raw = current.getProjectId() + "|" + current.getUserId() + "|" + current.getType()
                    + "|" + current.getData() + "|" + current.getTimestamp() + "|" + current.getPreviousHash();
            String calculatedHash = userService.md5(raw);
            if (!calculatedHash.equals(current.getHash())) {
                return false;
            }
        }
        return true;
    }
}
