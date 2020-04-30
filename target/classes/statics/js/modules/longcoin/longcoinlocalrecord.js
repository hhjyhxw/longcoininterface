$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'longcoin/longcoinlocalrecord/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '每个服务器分配的消费龙币的设备号', name: 'sid', index: 'sid', width: 80 }, 			
			{ label: '流水号', name: 'seq', index: 'seq', width: 80 }, 			
			{ label: '用户账户', name: 'useraccount', index: 'useraccount', width: 80 }, 			
			{ label: '账户类型（1、手机号码 2、微信openId 3、微信unionId）', name: 'accounttype', index: 'accounttype', width: 80 }, 			
			{ label: '操作类别 (2、充值、3消费)', name: 'operatortype', index: 'operatortype', width: 80 }, 			
			{ label: '充值消耗类别ID ', name: 'operatortypeid', index: 'operatortypeid', width: 80 }, 			
			{ label: '金额', name: 'amount', index: 'amount', width: 80 }, 			
			{ label: '本地创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '是否已对账 0否 1已对账', name: 'checked', index: 'checked', width: 80 }, 			
			{ label: '账结果1平0本地多2龙币商城多', name: 'checkedResult', index: 'checked_result', width: 80 }, 			
			{ label: '对账时间', name: 'checkedTime', index: 'checked_time', width: 80 }, 			
			{ label: '异常处理状态1正常，0未处理2已处理', name: 'exceptionFlag', index: 'exception_flag', width: 80 }, 			
			{ label: '处理结果', name: 'result', index: 'result', width: 80 }, 			
			{ label: '支付状态 0未支付 1已支付 2关闭', name: 'payStatus', index: 'pay_status', width: 80 }, 			
			{ label: '支付状态 0未退款 1已退款 2关闭', name: 'refundStatus', index: 'refund_status', width: 80 }, 			
			{ label: '来源渠道 1、百色现场会 2、', name: 'fromType', index: 'from_type', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#icloudapp',
	data:{
		showList: true,
		title: null,
		longcoinLocalrecord: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.longcoinLocalrecord = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.longcoinLocalrecord.id == null ? "longcoin/longcoinlocalrecord/save" : "longcoin/longcoinlocalrecord/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.longcoinLocalrecord),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                             vm.reload();
                             $('#btnSaveOrUpdate').button('reset');
                             $('#btnSaveOrUpdate').dequeue();
                        }else{
                            layer.alert(r.msg);
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "longcoin/longcoinlocalrecord/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            }else{
                                layer.alert(r.msg);
                            }
                        }
				    });
			    }
             }, function(){
             });
		},
		getInfo: function(id){
			$.get(baseURL + "longcoin/longcoinlocalrecord/info/"+id, function(r){
                vm.longcoinLocalrecord = r.longcoinLocalrecord;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});