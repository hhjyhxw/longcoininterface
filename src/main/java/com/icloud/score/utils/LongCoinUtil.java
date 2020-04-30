package com.icloud.score.utils;


import com.icloud.basecommon.service.redis.RedisService;
import com.icloud.basecommon.service.redislock.DistributedLock;
import com.icloud.basecommon.service.redislock.DistributedLockUtil;
import com.icloud.common.DateUtil;
import com.icloud.config.MyPropertitys;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.longcoin.entity.LongcoinLocalrecord;
import com.icloud.score.entity.LongChargeEntity;
import com.icloud.score.entity.LongConsumeEntity;
import com.icloud.score.entity.LongQueryEntity;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

@Component
public class LongCoinUtil {

    public final static Long inittime = DateUtil.parseTimeString("2000-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
    @Autowired
    private DistributedLockUtil distributedLockUtil;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MyPropertitys myPropertitys;
    /**
     * 获取流水号中的第三部分
     * @return
     */
    public String getBinaryString() {

        DistributedLock lock = distributedLockUtil.getDistributedLock("longcoin_msgSerialNumber");
        try {
            if (lock.acquire()) {
                Object reno =  redisService.get("msgSerialNumber");
                String no = "";
                if (reno==null || "".equals(reno.toString().trim())) {
                    no = "1";
                    redisService.set("msgSerialNumber", no);
                    return toFillingString(no);
                } else {
                    no = reno.toString();
                    int count = Integer.parseInt(no);
                    if (count >= 99999999) {
                        no = "1";
                        redisService.set("msgSerialNumber", no);
                        return toFillingString(no);
                    } else {
                        no = (Integer.parseInt(no) + 1) + "";
                        redisService.set("msgSerialNumber", no);
                        return toFillingString(no);
                    }
                }
            } else { // 获取锁失败
                //获取锁失败业务代码
                throw new BaseException("系统繁忙,请稍后再试");
            }
        } finally {
            if (lock != null) {
                lock.release();
            }
        }
    }

    /**
     * 补齐八位二进制
     *
     * @param str
     * @return
     */
    public static String toIntegerBinaryString(String str) {
        int num = Integer.parseInt(str);
        char[] bits = new char[8];
        for (int i = bits.length - 1; i >= 0; --i) {
            bits[i] = ((num & 1) == 0) ? '0' : '1';
            num >>>= 1;// 下一位
        }
        return String.copyValueOf(bits);
    }

    /**
     * 补齐八位
     *
     * @param str
     * @return
     */
    public static String toFillingString(String str) {

        int length = str.length();
        for(int i=0;i<8-length;i++){
            str = "0"+str;
        }

        return str;
    }

    /**
     * 获取请求参数中的时间戳
     *
     * @return
     */
    public String getTimeStamp() {
        return (System.currentTimeMillis() / 1000
                - DateUtil.parseTimeString("2000-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime() / 1000) + "";
    }

    /**
     * 得到请求中的流水号
     *
     * @return
     */
    public String getSerialNumber() {
        String machineNo = myPropertitys.getLongcoin().getSid();
        String currentTime = DateUtil.getYearMonthDayWithMinus(new Date());
        String runNum = getBinaryString();
        return machineNo + currentTime + runNum;
    }

    /**
     * 得到请求中的流水号
     *
     * @return
     */
    public String getSerialNumber_signup() {
        String machineNo =  myPropertitys.getLongcoin().getSid_signup();
        String currentTime = DateUtil.getYearMonthDayWithMinus(new Date());
        String runNum = getBinaryString();
        return machineNo + currentTime + runNum;
    }


    /**
     * 获取设备号
     *
     * @return
     */
    public String getMachineNo() {
        return myPropertitys.getLongcoin().getSid();

    }

    /**
     * 获取key
     *
     * @return
     */
    public String getKey() {
        return myPropertitys.getLongcoin().getKey();

    }

    /**
     * 获取充值类别
     *
     * @return
     */
    public String getRechargetype() {
        return myPropertitys.getLongcoin().getRechargetype();
    }

    /**
     * 获取查询龙币URL
     *
     * @return
     */
    public String getQueryUrl() {
        return myPropertitys.getLongcoin().getQueryUrl();
    }

    /**
     * 获取充值接口URL
     *
     * @return
     */
    public String getRechargeUrl() {
        return myPropertitys.getLongcoin().getRechargeUrl();
    }

    /**
     * 获取龙币消费URL
     *
     * @return
     */
    public String getConsumeUrl() {
        return myPropertitys.getLongcoin().getConsumeUrl();
    }

    /**
     * 获取消费类别
     *
     * @return
     */
    public String getConsumetype() {
        return myPropertitys.getLongcoin().getConsumetype();
    }

    /**
     * post请求 返回 不带证书
     *
     * @param params
     * @return
     */
    public String sendRequest(Map<String, String> params, String url) {

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(url);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            post.addParameter(entry.getKey(), entry.getValue());
        }

        HttpMethodParams param = post.getParams();
        param.setContentCharset("UTF-8");

        try {
            httpClient.executeMethod(post);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 如果返回200，表明成功
        if (post.getStatusCode() == 200) {
            try {
//                String result = post.getResponseBodyAsString();
//                System.out.println("longbi："+result);
//                return result;
                InputStream inputStream = post.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer .append(str );
                }
                return stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

    }



    /**
     * 获取查询对象参数
     * @param openid
     * @return
     */
    public LongQueryEntity getQueryEntity(String openid){
        LongQueryEntity entity = new LongQueryEntity();
        entity.setSid(getMachineNo());
        entity.setSeq(getSerialNumber());
        entity.setUseraccount(openid);
        entity.setKey(getKey());
        entity.setAccounttype("2");
        entity.setTimestamp(getTimeStamp());
        return entity;
    }

    /**
     * 获取一般充值对象参数
     * @param openid
     * @return
     */
    public LongChargeEntity getChargeEntity(String openid,String chargeAmount){
        LongChargeEntity entity = new LongChargeEntity();
        entity.setSid(getMachineNo());
        entity.setKey(getKey());
        entity.setSeq(getSerialNumber());
        entity.setUseraccount(openid);
        entity.setAccounttype("2");
        entity.setRechargetype(getRechargetype());
        entity.setRechargeamount(chargeAmount);
        entity.setTimestamp(getTimeStamp());
        return entity;
    }

    /**
     * 获取消费对象参数
     * @param openid
     *  @param consumeamount
     * @return
     */
    public LongConsumeEntity getComsueEntity(String openid, String consumeamount){
        LongConsumeEntity entity = new LongConsumeEntity();
        entity.setSid(getMachineNo());
        entity.setKey(getKey());
        entity.setSeq(getSerialNumber());
        entity.setUseraccount(openid);
        entity.setAccounttype("2");
        entity.setConsumetype(getConsumetype());
        entity.setTimestamp(getTimeStamp());
        entity.setConsumeamount(consumeamount);
        return entity;
    }

    /**
     *
     * 获取特殊充值对象参数
     * 会员没注册直接注册成会员
     * @param openid
     * @return
     */
    public LongChargeEntity getSpecifyChargeEntity(String openid, String chargeAmount){
        LongChargeEntity entity = new LongChargeEntity();
        entity.setSid(myPropertitys.getLongcoin().getSid_signup());
        entity.setKey(myPropertitys.getLongcoin().getKey_signup());
        entity.setSeq(getSerialNumber_signup());
        entity.setUseraccount(openid);
        entity.setAccounttype("2");
        entity.setRechargetype(getRechargetype());
        entity.setRechargeamount(chargeAmount);
        entity.setTimestamp(getTimeStamp());
        return entity;
    }

    /**
     * 根据请求参数封装本地消费、充值记录
     * @param paramMap
     * @return
     */
    public LongcoinLocalrecord getLocalRecord(Map<String, String> paramMap){
        LongcoinLocalrecord localrecord = new LongcoinLocalrecord();
        localrecord.setStatus("0");//创建
        localrecord.setChecked("0");//未对账
        localrecord.setFromType(paramMap.get("fromType")!=null?paramMap.get("fromType").toString():null);//请求求道
        localrecord.setSid(paramMap.get("sid").toString());
        localrecord.setSeq(paramMap.get("seq").toString());
        localrecord.setAccounttype(paramMap.get("accounttype").toString());
        localrecord.setUseraccount(paramMap.get("useraccount").toString());
        localrecord.setCreateTime(new Date(1000*(inittime+Long.parseLong(paramMap.get("timestamp").toString()))));

        //充值
        if(paramMap.get("rechargetype")!=null && !"".equals(paramMap.get("rechargetype").trim())){
            localrecord.setOperatortype("2");
            localrecord.setOperatortypeid(paramMap.get("rechargetype").toString());
            localrecord.setAmount(Integer.valueOf(paramMap.get("rechargeamount").toString()));
        }
        //消费
        if(paramMap.get("consumetype")!=null && !"".equals(paramMap.get("consumetype").trim())){
            localrecord.setOperatortype("3");
            localrecord.setOperatortypeid(paramMap.get("consumetype").toString());
            localrecord.setAmount(Integer.valueOf(paramMap.get("consumeamount").toString()));
        }
        return localrecord;
    }
}
