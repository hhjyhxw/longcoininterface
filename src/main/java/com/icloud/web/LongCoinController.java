package com.icloud.web;

import com.alibaba.fastjson.JSONObject;
import com.icloud.modules.longcoin.entity.LongcoinLocalrecord;
import com.icloud.modules.longcoin.service.LongcoinLocalrecordService;
import com.icloud.score.service.LongbiServiceImpl;
import com.icloud.score.utils.LongCoinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/longcoin")
public class LongCoinController {

    @Autowired
    private LongbiServiceImpl longbiServiceImpl;

    @Autowired
    private LongCoinUtil longCoinUtil;
    @Autowired
    private LongcoinLocalrecordService longcoinLocalrecordService;

    @RequestMapping("/query")
    @ResponseBody
    public JSONObject query(@RequestParam Map<String, String> params) {
        JSONObject result = null;
        String message = "";
        try {
            return longbiServiceImpl.queryLongbi(params);
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        result = new JSONObject();
        result.put("code","4001");
        result.put("message","查询异常："+message);
        return result;
    }

    @RequestMapping("/recharge")
    @ResponseBody
    public JSONObject recharge(@RequestParam Map<String, String> params) {
        JSONObject result = null;
        String message = "";
        try {
            LongcoinLocalrecord record = longCoinUtil.getLocalRecord(params);
            longcoinLocalrecordService.save(record);
            result = longbiServiceImpl.recharge(params);
            if(result==null){
                record.setStatus("2");
                record.setStatusResult("null");
                longcoinLocalrecordService.updateById(record);
                return	null;
            }
            if(result!=null && result.containsKey("returncode") && "000000".equals(result.getString("returncode"))){
                record.setStatus("1");
            }else{
                record.setStatus("2");
                record.setStatusResult(result.getString("returnmsg"));
            }
            longcoinLocalrecordService.updateById(record);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        result = new JSONObject();
        result.put("code","4001");
        result.put("message","充值异常："+message);
        return result;
    }


    @RequestMapping("/consume")
    @ResponseBody
    public JSONObject consume(@RequestParam Map<String, String> params) {
        JSONObject result = null;
        String message = "";
        try {
            LongcoinLocalrecord record = longCoinUtil.getLocalRecord(params);
            longcoinLocalrecordService.save(record);
            result = longbiServiceImpl.consume(params);
            if(result==null){
                record.setStatus("2");
                record.setStatusResult("null");
                longcoinLocalrecordService.updateById(record);
                return	null;
            }
            if(result!=null && result.containsKey("returncode") && "000000".equals(result.getString("returncode"))){
                record.setStatus("1");
            }else{
                record.setStatus("2");
                record.setStatusResult(result.getString("returnmsg"));
            }
            longcoinLocalrecordService.updateById(record);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        result = new JSONObject();
        result.put("code","4001");
        result.put("message","消费异常："+message);
        return result;
    }

}
