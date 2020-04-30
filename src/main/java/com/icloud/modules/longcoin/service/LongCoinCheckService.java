package com.icloud.modules.longcoin.service;

import com.icloud.basecommon.service.redislock.DistributedLock;
import com.icloud.basecommon.service.redislock.DistributedLockUtil;
import com.icloud.common.DateUtil;
import com.icloud.modules.longcoin.dao.LongcoinLocalrecordMapper;
import com.icloud.modules.longcoin.dao.LongcoinScorerecordMapper;
import com.icloud.modules.longcoin.entity.LongcoinLocalrecord;
import com.icloud.modules.longcoin.entity.LongcoinScorerecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 龙币对账
 */
@Service
public class LongCoinCheckService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DownLoadScoreBillService downLoadScoreBillService;
    @Autowired
    private LongcoinLocalrecordMapper longcoinLocalrecordMapper;//本地记录
    @Autowired
    private LongcoinScorerecordMapper longcoinScorerecordMapper;//积分商城记录

    @Autowired
    private DistributedLockUtil distributedLockUtil;

    public void checkLongBill(Date checkDate){
        DistributedLock lock = null;
        try {
            lock = distributedLockUtil.getDistributedLock("toStatisSmokeBean");
            if (lock.acquire()) {
                    logger.info("===============龙币对账，获取锁成功 ===============");
                    if(checkDate==null){
                        checkDate = DateUtil.getDateWithoutTime(DateUtil.getBeforeDays(1));//昨天的数据
                    }
            //        Date yesterday = DateUtils.addDays(new Date(), -1);
            //        String strYesterday = DateUtils.format(yesterday, "yyyy-MM-dd");
                    Date createTimeStart = DateUtil.getDateWithAll(DateUtil.formatDate(checkDate)+" 00:00:00");
                    Date createTimeEnd = DateUtil.getDateWithAll(DateUtil.formatDate(checkDate)+" 23:59:59");
                    logger.info("createTimeStart==="+createTimeStart);
                    logger.info("createTimeEnd==="+createTimeEnd);
                    LongcoinScorerecord longcoinscoreRecordParam = new LongcoinScorerecord();
                    longcoinscoreRecordParam.setBillDate(createTimeStart);
                    //1、查询是否已读取对账记录
                    List<LongcoinScorerecord> list = longcoinScorerecordMapper.findForList(longcoinscoreRecordParam);

                    if(list!=null && list.size()>0){
                        logger.info("龙币商城记录已存在，不需要再次对账");
                        return;
                    }

                    //2、读取龙币ftp服务器数据
                    list = downLoadScoreBillService.getScoreRecordList(checkDate);//通过ftp读取龙币商城记录
                    logger.info("===>>>{}龙币商城对账记录条数:{}", DateUtil.formatDate(checkDate),list!=null?list.size():0);

                    //3、读取本地充值消费记录
                    LongcoinLocalrecord longcoinlocalRecord = new LongcoinLocalrecord();
                    longcoinlocalRecord.setCreateTimeStart(createTimeStart);
                    longcoinlocalRecord.setCreateTimeEnd(createTimeEnd);
                    List<LongcoinLocalrecord> localList = longcoinLocalrecordMapper.findForList(longcoinlocalRecord);
                    logger.info("===>>>{}本地充值消费记录条数:{}", DateUtil.formatDate(checkDate),localList!=null?localList.size():0);

                    //4、对账
                    if(localList!=null && list!=null) {

                        for (LongcoinLocalrecord localrecord : localList) {
                            Iterator<LongcoinScorerecord> it = list.iterator();
                            while (it.hasNext()) {
                                LongcoinScorerecord scoreRecord = it.next();
                                if (isSameOrder(localrecord, scoreRecord)) {
                                    it.remove();
                                    //保存积分商城的龙币记录
                                    saveScoreRecord(scoreRecord);
                                    break;
                                }
                            }
                            if (!"1".equals(localrecord.getChecked()) && createTimeStart.compareTo(DateUtil.getDateWithoutTime(DateUtil.formatTimestamp(localrecord.getCreateTime()))) == 0) {
                                // 对账不成功，没找到账单,前一天的数据可以关闭了
                                if ("0".equals(localrecord.getPayStatus())) {
                                    // 未支付的订单
                                    localrecord.setPayStatus("2");  // 状态0未支付1已支付2关闭
                                    localrecord.setRefundStatus("2");
                                    localrecord.setChecked("1");
                                    localrecord.setCheckedTime(new Date());
                                } else if ("1".equals(localrecord.getPayStatus())) {
                                    localrecord.setChecked("1");
                                    localrecord.setCheckedResult("0");//本地数据多
                                    localrecord.setCheckedTime(new Date());
                                }
                            }
                            longcoinLocalrecordMapper.updateById(localrecord);
                        }
                        // 说明存在已经支付，但平台没订单的支付订单
                        if (!list.isEmpty()) {
                            for (LongcoinScorerecord record : list) {
                                record.setCheckedResult("2");//积分商城多
                                record.setChecked("1");
                                record.setCheckedTime(new Date());
                                saveScoreRecord(record);
                            }
                        }
                    }
            }else {
                //获取锁失败业务代码
                logger.info("===============龙币对账，获取锁失败 ===============");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("龙币对账,异常"+e.getMessage());
        }finally {
            if (lock != null) {
                lock.release();
                logger.info("===============龙币对账，释放锁成功 ===============");
            }
        }
        //5、对账完成，更新对账结果,总的结果汇总，（暂定）

    }


    /**
     * 比较是否相同
     */
    private boolean isSameOrder(LongcoinLocalrecord longcoinlocalRecord, LongcoinScorerecord scoreRecord){
        //比较流水号是否相同
        logger.info("===>>>scoreeq==="+scoreRecord.getSeq()+";localseq==="+longcoinlocalRecord.getSeq()+"  "+(scoreRecord.getSeq().equals(longcoinlocalRecord.getSeq())));
        if(!scoreRecord.getSeq().trim().equals(longcoinlocalRecord.getSeq().trim())){
            return false;
        }
        //比较金额 商城记录金额 - 本地记录金额
        long result = scoreRecord.getAmount().longValue()-longcoinlocalRecord.getAmount().longValue();
        if(result == 0){
            //相同 平帐
            longcoinlocalRecord.setCheckedResult("1");
            scoreRecord.setCheckedResult("1");
        }else if(result < 0){
            //本地多
            longcoinlocalRecord.setCheckedResult("0");
            scoreRecord.setCheckedResult("0");
        }else{
            //商城多
            longcoinlocalRecord.setCheckedResult("2");
            scoreRecord.setCheckedResult("2");
        }
        longcoinlocalRecord.setChecked("1");
        longcoinlocalRecord.setCheckedTime(new Date());

        scoreRecord.setChecked("1");
        scoreRecord.setCheckedTime(new Date());
        return true;
    }


    /**
     * 保存积分平台 龙币记录
     * 先查询是否已经保存过了，保存过的，说明是二次对账
     * @param record
     */
    private void saveScoreRecord(LongcoinScorerecord record){
        List<LongcoinScorerecord> list = longcoinScorerecordMapper.findForList(record);
        if (list==null || list.size() <= 0) {
            longcoinScorerecordMapper.insert(record);
        }
    }

}
