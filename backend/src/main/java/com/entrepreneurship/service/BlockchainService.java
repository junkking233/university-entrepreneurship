package com.entrepreneurship.service;

import com.entrepreneurship.entity.BlockchainRecord;
import java.util.List;
import java.util.Map;

public interface BlockchainService {
    BlockchainRecord addRecord(Long projectId, Long userId, String type, String data);
    List<BlockchainRecord> getRecordsByProject(Long projectId);
    Map<String, Object> getTrustScore(Long projectId);
    boolean verifyChain(Long projectId);
}
