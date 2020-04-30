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
//import com.icloud.modules.longcoin.entity.LongcoinLocalrecord;
//import com.icloud.modules.longcoin.service.LongcoinLocalrecordService;
//import com.icloud.basecommon.model.Query;
//import com.icloud.common.PageUtils;
//import com.icloud.common.R;
//import com.icloud.common.validator.ValidatorUtils;
//
//
///**
// * 本地龙币消费记录
// *
// * @author zdh
// * @email yyyyyy@cm.com
// * @date 2020-04-29 09:48:46
// * 菜单主连接： modules/longcoin/longcoinlocalrecord.html
// */
//@RestController
//@RequestMapping("longcoin/longcoinlocalrecord")
//public class LongcoinLocalrecordController {
//    @Autowired
//    private LongcoinLocalrecordService longcoinLocalrecordService;
//
//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    @RequiresPermissions("longcoin:longcoinlocalrecord:list")
//    public R list(@RequestParam Map<String, Object> params){
//        Query query = new Query(params);
//        PageUtils page = longcoinLocalrecordService.findByPage(query.getPageNum(),query.getPageSize(), query);
//
//        return R.ok().put("page", page);
//    }
//
//
//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{id}")
//    @RequiresPermissions("longcoin:longcoinlocalrecord:info")
//    public R info(@PathVariable("id") Long id){
//        LongcoinLocalrecord longcoinLocalrecord = (LongcoinLocalrecord)longcoinLocalrecordService.getById(id);
//
//        return R.ok().put("longcoinLocalrecord", longcoinLocalrecord);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    @RequiresPermissions("longcoin:longcoinlocalrecord:save")
//    public R save(@RequestBody LongcoinLocalrecord longcoinLocalrecord){
//        longcoinLocalrecordService.save(longcoinLocalrecord);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    @RequiresPermissions("longcoin:longcoinlocalrecord:update")
//    public R update(@RequestBody LongcoinLocalrecord longcoinLocalrecord){
//        ValidatorUtils.validateEntity(longcoinLocalrecord);
//        longcoinLocalrecordService.updateById(longcoinLocalrecord);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    @RequiresPermissions("longcoin:longcoinlocalrecord:delete")
//    public R delete(@RequestBody Long[] ids){
//        longcoinLocalrecordService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }
//
//}
