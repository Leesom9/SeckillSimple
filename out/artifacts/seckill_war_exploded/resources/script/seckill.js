//存放主要交互逻辑的js代码
//javascript 模块化
//seckill.detail.init(param);
var seckill = {
    URL : {

    },
    //验证手机号
    validatePhone : function(phone){
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },
    //详情页秒杀逻辑
    detail : {
        //详情页初始化
        init : function (params) {
            //手机验证和登录，计时交互
            //规划交互流程
            //在cookie里面查找手机号
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定phone
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    //显示弹出层
                    show : true,
                    //没有输入手机号，弹出层是不能隐藏的，进一步处理
                    //禁止位置关闭：点击空白区域关闭弹出层
                    backdrop : 'static',
                    //关闭键盘事件：点击键盘关闭弹出层
                    keyboard : false
                });
                //点击按钮
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if(seckill.validatePhone(inputPhone)){
                        //将电话号码写入cookie
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        //刷新页面
                        window.location.reload();
                    }else{
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">电话号码错误</label>').show(300);
                    }
                });
            }
            //已经登录
        }
    }
}