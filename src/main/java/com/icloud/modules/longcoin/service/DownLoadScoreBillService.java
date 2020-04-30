package com.icloud.modules.longcoin.service;

import com.icloud.common.ConfigUtil;
import com.icloud.common.DateUtil;
import com.icloud.common.ftp.SftpFileService;
import com.icloud.config.MyPropertitys;
import com.icloud.modules.longcoin.entity.LongcoinScorerecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 下载龙币对账单
 */
@Service
public class DownLoadScoreBillService {

    private Logger logger = LoggerFactory.getLogger(getClass());
//    @Autowired
//    private FtpFileService ftpFileService;
    @Autowired
    private SftpFileService sftpFileService;

    @Autowired
    private MyPropertitys myPropertitys;

    /**
     * 下载对账单 并生成记录
     * @return
     */
    public List<LongcoinScorerecord> getScoreRecordList(Date date){
        List<LongcoinScorerecord> alllist = new ArrayList<LongcoinScorerecord>();
        List<LongcoinScorerecord> list = new ArrayList<LongcoinScorerecord>();
        String dateStr = DateUtil.getYearMonthDay(date);
        List<String> sds = myPropertitys.getLongcoin().getSids();
        for (int i = 0; i <sds.size() ; i++) {
//            String fileName = "ZL_"+dateStr+"_"+ ConfigUtil.get("longCoin_sid");
            String fileName = "ZL_"+dateStr+"_"+ sds.get(i);
            String sourcePath = myPropertitys.getLongcoin().getData_file_dir()+fileName;
            String localPath = myPropertitys.getLongcoin().getLongcoin_data_bill()+fileName+".txt";
            String bakPath =  myPropertitys.getLongcoin().getData_bak_dir()+fileName;

            try {
                logger.info("sourcePath==="+sourcePath);
                logger.info("localPath==="+localPath);
                logger.info("bakPath==="+bakPath);
                sftpFileService.listDirectFile(myPropertitys.getLongcoin().getData_file_dir());

                File file = new File(myPropertitys.getLongcoin().getLongcoin_data_bill());
                if (!file.exists()) {//判断本地目录是否存在
                    file.mkdirs();
                }
                file = new File(localPath);//判断文件是否存在
                if (!file.exists()) {
                    file.createNewFile();
                }
                //String remoteDirectory, String downloadFile,String saveFile
                boolean result = sftpFileService.download(myPropertitys.getLongcoin().getData_file_dir(),sourcePath,localPath);
                logger.info("ftl下载对账单结果==="+result);
                if(result){
                    list = getList(localPath,date);
                    if(list!=null && list.size()>0){
                        alllist.addAll(list);
                    }
                    //移动原目录
                    // sftpFileService.changeFileDir(ConfigUtil.get("data_file_dir"),fileName,ConfigUtil.get("data_bak_dir"),fileName);
                    //sftpFileService.listDirectFile(ConfigUtil.get("data_bak_dir"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return alllist;
    }

    /**
     * 读取本地文件处理
     * @param filaPath
     * @returnr
     */
    private List<LongcoinScorerecord> getList(String filaPath, Date billDate){
        //总行数
        List<String> list =new ArrayList<String>();
        List<LongcoinScorerecord> scorelist =new ArrayList<LongcoinScorerecord>();
        //读取文件
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filaPath), "UTF-8")); //这里可以控制编码
            String line = null;
            while ((line = br.readLine()) != null) {
                logger.info("line====="+line);
                if (line.trim().length() > 2) {
                    list.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(list.size()>0){
            if(list.get(0).contains("|")){
                logger.info("list.get(0)==="+list.get(0));
                String[]  headerLine = list.get(0).split("\\|");
                logger.info("文件名==="+headerLine[0]+";账单记录的总数==="+headerLine[1]);
            }else {
                logger.info("首行信息=="+list.get(0));
            }
            list.remove(0);
            for(String temp : list){
                String[] arry =temp.split("\\|");
                LongcoinScorerecord record = new LongcoinScorerecord();
                record.setSeq(arry[0]);//序列
                record.setUseraccount(arry[1]);//用户账号
                record.setAccounttype(arry[2]);//record
                record.setOperatortype(arry[3]);//操作类别 2: 充值 3：消耗
                record.setOperatortypeid(arry[4]);// 充值/消耗类别ID
                record.setAmount(Integer.valueOf(arry[5]));//金额
                record.setCreateTime(new Date());//读取文件时间
                record.setBillDate(billDate);//账单日期
                record.setChecked("0");//未对账
                scorelist.add(record);
            }
        }
        return scorelist;
    }


    /**
     * 迁移 对账单到 day_bak
     * @return
     */
    public void vmFileToBak(){

        return;
    }

}
