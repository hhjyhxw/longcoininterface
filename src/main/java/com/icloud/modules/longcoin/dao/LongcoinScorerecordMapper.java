package com.icloud.modules.longcoin.dao;

import com.icloud.modules.longcoin.entity.LongcoinLocalrecord;
import com.icloud.modules.longcoin.entity.LongcoinScorerecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 积分商城龙币记录
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-29 09:48:46
 */
public interface LongcoinScorerecordMapper extends BaseMapper<LongcoinScorerecord> {

	List<LongcoinScorerecord> queryMixList(Map<String, Object> map);

    List<LongcoinScorerecord> findForList(LongcoinScorerecord t);

}
