package com.icloud.config.quartz;

import com.icloud.basecommon.service.redislock.DistributedLock;
import com.icloud.basecommon.service.redislock.DistributedLockUtil;
import com.icloud.modules.longcoin.service.LongCoinCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 龙币对账
 */
@Component
@EnableScheduling
public class LongCoinCheckJob {

    public final static Logger log = LoggerFactory.getLogger(LongCoinCheckJob.class);

    @Autowired
    private LongCoinCheckService longCoinCheckService;
    @Autowired
    private DistributedLockUtil distributedLockUtil;

    //每天凌晨 3 点运行
	@Scheduled(cron = "0 0 3  * * ? ")
    public void toCheckLongCoin() {
        DistributedLock lock = null;
        try {
            longCoinCheckService.checkLongBill(null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时龙币对账,异常"+e.getMessage());
        }
        log.info("===============toCheckLongCoin end ===============");
    }

}