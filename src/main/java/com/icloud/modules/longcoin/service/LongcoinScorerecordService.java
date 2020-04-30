package com.icloud.modules.longcoin.service;

import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.longcoin.entity.LongcoinScorerecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.modules.longcoin.dao.LongcoinScorerecordMapper;
/**
 * 积分商城龙币记录
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-29 09:48:46
 */
@Service
@Transactional
public class LongcoinScorerecordService extends BaseServiceImpl<LongcoinScorerecordMapper,LongcoinScorerecord> {

    @Autowired
    private LongcoinScorerecordMapper longcoinScorerecordMapper;
}

