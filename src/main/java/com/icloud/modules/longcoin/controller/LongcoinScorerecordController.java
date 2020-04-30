//package com.icloud.modules.longcoin.controller;
//
//import java.util.Arrays;
//import java.util.Map;
//import com.icloud.basecommon.model.Query;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import com.icloud.modules.longcoin.entity.LongcoinScorerecord;
//import com.icloud.modules.longcoin.service.LongcoinScorerecordService;
//import com.icloud.basecommon.model.Query;
//import com.icloud.common.PageUtils;
//import com.icloud.common.R;
//import com.icloud.common.validator.ValidatorUtils;
//
//
///**
// * 积分商城龙币记录
// *
// * @author zdh
// * @email yyyyyy@cm.com
// * @date 2020-04-29 09:48:46
// * 菜单主连接： modules/longcoin/longcoinscorerecord.html
// */
//@RestController
//@RequestMapping("longcoin/longcoinscorerecord")
//public class LongcoinScorerecordController {
//    @Autowired
//    private LongcoinScorerecordService longcoinScorerecordService;
//
//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    @RequiresPermissions("longcoin:longcoinscorerecord:list")
//    public R list(@RequestParam Map<String, Object> params){
//        Query query = new Query(params);
//        PageUtils page = longcoinScorerecordService.findByPage(query.getPageNum(),query.getPageSize(), query);
//
//        return R.ok().put("page", page);
//    }
//
//
//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{id}")
//    @RequiresPermissions("longcoin:longcoinscorerecord:info")
//    public R info(@PathVariable("id") Long id){
//        LongcoinScorerecord longcoinScorerecord = (LongcoinScorerecord)longcoinScorerecordService.getById(id);
//
//        return R.ok().put("longcoinScorerecord", longcoinScorerecord);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    @RequiresPermissions("longcoin:longcoinscorerecord:save")
//    public R save(@RequestBody LongcoinScorerecord longcoinScorerecord){
//        longcoinScorerecordService.save(longcoinScorerecord);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    @RequiresPermissions("longcoin:longcoinscorerecord:update")
//    public R update(@RequestBody LongcoinScorerecord longcoinScorerecord){
//        ValidatorUtils.validateEntity(longcoinScorerecord);
//        longcoinScorerecordService.updateById(longcoinScorerecord);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    @RequiresPermissions("longcoin:longcoinscorerecord:delete")
//    public R delete(@RequestBody Long[] ids){
//        longcoinScorerecordService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }
//
//}
