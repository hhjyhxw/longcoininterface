package com.icloud.modules.longcoin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 本地龙币消费记录
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-29 09:48:46
 */
@Data
@TableName("t_longcoin_localrecord")
public class LongcoinLocalrecord implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 每个服务器分配的消费龙币的设备号 */
       @TableField("sid")
       private String sid;
   	   	   /* 流水号 */
       @TableField("seq")
       private String seq;
   	   	   /* 用户账户 */
       @TableField("useraccount")
       private String useraccount;
   	   	   /* 账户类型（1、手机号码 2、微信openId 3、微信unionId） */
       @TableField("accounttype")
       private String accounttype;
   	   	   /* 操作类别 (2、充值、3消费) */
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
   	   	   /* 是否已对账 0否 1已对账 */
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
   	   	   /* 支付状态 0未支付 1已支付 2关闭 */
       @TableField("pay_status")
       private String payStatus;
   	   	   /* 支付状态 0未退款 1已退款 2关闭 */
       @TableField("refund_status")
       private String refundStatus;
   	   	   /* 来源渠道 1、百色现场会 2、 */
       @TableField("from_type")
       private String fromType;

    /* 消费充值状态 0创建、1成功、2失败 */
    @TableField("status")
    private String status;
    /* 消费充值结果描述 */
    @TableField("status_result")
    private String statusResult;
    @TableField(exist = false)
    private Date createTimeStart;//开始查询
    @TableField(exist = false)
    private Date createTimeEnd;//结束查询时间
}
