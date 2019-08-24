//存放主要交互逻辑的js代码
//javascript 模块化
//seckill.detail.init(param);
var seckill = {
    URL : {
        now : function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        execution : function (seckillId, md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    //处理秒杀逻辑
    handleSeckillKill : function(seckillId,node){
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //在回调函数中，执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log('killUrl = '+ killUrl);
                    //如果使用$('#killBtn').click，事件将一直绑定。
                    //使用one，点击事件只绑定一次
                    //防止用户连续点击，防止服务器端接收频繁的请求
                    $('#killBtn').one('click', function () {
                        //绑定执行秒杀请求
                        //此处等于$('#killBtn'),但是多运行一次jquary选择器
                        //1：先禁用按钮
                        $(this).addClass('disabled');
                        //2：发送秒杀请求，执行秒杀
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                    node.show();
                }else{
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新进入计时逻辑
                    seckill.countdown(seckillId,now,start,end);
                }
            }else{
                console.log('result = ' + result);
            }
        });
    },
    //验证手机号
    validatePhone : function(phone){
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },
    //判断时间
    countdown : function(seckillId,nowTime,startTime,endTime){
        var seckillBox = $('#seckill-box')
        //对时间的判断
        if(nowTime > endTime){
            seckillBox.html('秒杀结束！');
        }else if(nowTime < startTime){
            //秒杀未开始,计时事件绑定
            //+1000防止用户端时间偏移
            var killTime = new Date(startTime + 1000);
            //每次时间变化都会调用这个函数，回调函数：在时间变化的时候帮我们做相应的日期输出
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调事件
            }).on('finish.countdown',function () {
                //获取秒杀地址，控制实现逻辑，执行秒杀
                seckill.handleSeckillKill(seckillId,seckillBox);
            });
        }else{//秒杀开始
            seckill.handleSeckillKill(seckillId,seckillBox);

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
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //$.get('seckill/time/now',{},function(result){
            $.get(seckill.URL.now(), {}, function(result){
                //result必须存在，并且result的success必须是true
                if(result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                }else{
                    console.log('result ='+result);
                }
            });
        }
    }
}