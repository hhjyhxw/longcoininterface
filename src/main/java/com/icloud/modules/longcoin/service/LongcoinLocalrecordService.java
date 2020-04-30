package com.icloud.modules.longcoin.service;

import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.basecommon.service.MybaseService;
import com.icloud.modules.longcoin.entity.LongcoinLocalrecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.modules.longcoin.dao.LongcoinLocalrecordMapper;
/**
 * 本地龙币消费记录
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-29 09:48:46
 */
@Service
@Transactional
public class LongcoinLocalrecordService extends BaseServiceImpl<LongcoinLocalrecordMapper, LongcoinLocalrecord> {


    @Autowired
    private LongcoinLocalrecordMapper longcoinLocalrecordMapper;
}

