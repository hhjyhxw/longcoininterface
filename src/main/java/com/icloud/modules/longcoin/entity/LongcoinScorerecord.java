package com.icloud.modules.longcoin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商城龙币记录
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-29 09:48:46
 */
@Data
@TableName("t_longcoin_scorerecord")
public class LongcoinScorerecord implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 流水号 */
       @TableField("seq")
       private String seq;
   	   	   /* 用户账户 */
       @TableField("useraccount")
       private String useraccount;
   	   	   /* 账户类型（1、手机号码 2、微信openId 3、微信unionId） */
       @TableField("accounttype")
       private String accounttype;
   	   	   /* 操作类别 (2、消费、3充值) */
       @TableField("operatortype")
       private String operatortype;
   	   	   /* 充值消耗类别ID  */
       @TableField("operatortypeid")
       private String operatortypeid;
   	   	   /* 金额 */
       @TableField("amount")
       private Integer amount;
   	   	   /* 本地创建时间 */
       @TableField("create_time")
       private Date createTime;
   	   	   /* 是否已对账 */
       @TableField("checked")
       private String checked;
   	   	   /* 账结果1平0本地多2龙币商城多 */
       @TableField("checked_result")
       private String checkedResult;
   	   	   /* 对账时间 */
       @TableField("checked_time")
       private Date checkedTime;
   	   	   /* 异常处理状态1正常，0未处理2已处理 */
       @TableField("exception_flag")
       private String exceptionFlag;
   	   	   /* 处理结果 */
       @TableField("result")
       private String result;
   	   	   /* 账单日期 */
       @TableField("bill_date")
       private Date billDate;

    @TableField(exist = false)
    private Date createTimeStart;//开始查询
    @TableField(exist = false)
    private Date createTimeEnd;//结束查询时间
   	
}
