$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'longcoin/longcoinscorerecord/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '流水号', name: 'seq', index: 'seq', width: 80 }, 			
			{ label: '用户账户', name: 'useraccount', index: 'useraccount', width: 80 }, 			
			{ label: '账户类型（1、手机号码 2、微信openId 3、微信unionId）', name: 'accounttype', index: 'accounttype', width: 80 }, 			
			{ label: '操作类别 (2、消费、3充值)', name: 'operatortype', index: 'operatortype', width: 80 }, 			
			{ label: '充值消耗类别ID ', name: 'operatortypeid', index: 'operatortypeid', width: 80 }, 			
			{ label: '金额', name: 'amount', index: 'amount', width: 80 }, 			
			{ label: '本地创建时间', name: 'createTime', index: 'create_time', width: 80 }, 			
			{ label: '是否已对账', name: 'checked', index: 'checked', width: 80 }, 			
			{ label: '账结果1平0本地多2龙币商城多', name: 'checkedResult', index: 'checked_result', width: 80 }, 			
			{ label: '对账时间', name: 'checkedTime', index: 'checked_time', width: 80 }, 			
			{ label: '异常处理状态1正常，0未处理2已处理', name: 'exceptionFlag', index: 'exception_flag', width: 80 }, 			
			{ label: '处理结果', name: 'result', index: 'result', width: 80 }, 			
			{ label: '账单日期', name: 'billDate', index: 'bill_date', width: 80 }			
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
		longcoinScorerecord: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.longcoinScorerecord = {};
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
                var url = vm.longcoinScorerecord.id == null ? "longcoin/longcoinscorerecord/save" : "longcoin/longcoinscorerecord/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.longcoinScorerecord),
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
                        url: baseURL + "longcoin/longcoinscorerecord/delete",
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
			$.get(baseURL + "longcoin/longcoinscorerecord/info/"+id, function(r){
                vm.longcoinScorerecord = r.longcoinScorerecord;
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