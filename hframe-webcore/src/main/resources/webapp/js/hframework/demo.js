require(['layer','static/js/common/utilities/ajax'], function (){

    var layer = require('layer');
    var ajax = require('static/js/common/utilities/ajax');
    var errormsg = require('static/js/uc/common/errormsg');

    $('.bank-list > ul > li > a').on('click', function (){
        var _tpl = $('#riskrating_question').html();
        layer.open({
            area: ['766px', '410px'],
            type: 1,
            title: false,
            closeBtn: 0,
            content: _tpl,
            success: function (l, index) {

                l.find('.btn_close').on('click', function (){
                    layer.close(index);
                });

                l.find('#btn_get_result').on('click', function () {
                    GetRiskateResult(6);
                    var $hideInput = $(this).parents('[name="nice-select"]').find('input[type="hidden"]');
                    $(this).parents('[name="nice-select"]').find('input[type="text"]').val("324");
                });

                l.find('#result').on('click', function (){
                    if($(this).hasClass('next-active')){
                        var $qubox = $('.form-text-label');
                        var _len = $qubox.length;
                        var _val = 0;
                        for(var i = 0; i < _len; i++){
                            var $input = $qubox.eq(i).find('input');
                            var _inputlen = $input.length;
                            var _name = $input.attr('name');
                            for(var j = 0; j < _inputlen; j++){
                                if($input[j].checked){
                                    _val += parseInt($($input[j]).val());
                                }
                            }
                        }

                        GetRiskateResult(_val);
                        //点击确定跳入的链接
                    }
                });

            }
        });
        //$('.pro-risk, #riskrating_question').fadeIn(400);
    });
    $('#txt_bank_card').on('focus', function(){
        $(this).parent().children('.num-lag').show()
    });
    $('#txt_bank_card').on('blur', function () {
        $(this).parent().children('.num-lag').hide()
        CheckBankCard();
    });

    function GetRiskateResult(riskScore){
        var _url = appConfig.ucdomain + '/ajax/asset/calculate';
        var _data = {riskScore: riskScore};

        ajax.UcRequest(_url, _data, function(data){
            if(data.status.indexOf('1000') > -1){
                ShowRiskateResult(data.data.riskType);
            }
        });
    }

    function ShowRiskateResult(type){
        layer.closeAll();
        var _html = $('#tpl_riskrating_result').html();
        layer.open({
            area: ['766px', '410px'],
            type: 1,
            title: false,
            closeBtn: 0,
            content: _html,
            success: function (l, index) {

                l.find('.cve' + type).fadeIn(200);

                l.find('.btn_close').on('click', function(){
                    layer.closeAll();
                    location.reload();
                });
            }
        });
    }

    function ShowErrorInfo(msg, callback){
        var _tpl = $('#tpl_errorMsg').html();
        layer.open({
            area: '400px',
            type: 1,
            title: false,
            closeBtn: 0,
            content: _tpl,
            success: function (l, i){
                $('#error_msg').html(msg);
                $('#btn_ok').on('click', callback);
            }
        });
    }

    function ShowErrorMsg(id, callback, msg){
        var _tpl = '';
        var interText = dot.template($('#' + id).thml());
        if(typeof msg !== 'undefined'){
            var obj = {msg: msg}
            _tpl = interText(obj);
        }else{
            _tpl = interText();
        }
        layer.open({
            area: ['510px', '250px'],
            type: 1,
            title: false,
            closeBtn: 0,
            content: _tpl,
            success: function (layero, i) {
                $('#btn_ok').on('click', callback);

                $('#btn_cancel').on('click', function (){
                    layer.close(i);
                });
            }
        });
    }
    ShowErrorMsg('tpl_confirmNoLogin', function(){
        var _url = escape(location.href);
        location.href = appConfig.ucdomain + '/member/login?backUrl=' + _url;
    });

    $('#btn_save').on('click', function (){
        var _url = appConfig.ucdomain + '/ajax/member/update/info';
        var _data = GetData();
        ajax.UcRequest(_url, _data, SuccessCallBack);
    });

    function SuccessCallBack(data){
        if(data.status.indexOf('1000') > 0){
            layer.msg(data.promptMessage, {time: 1500}, function () {
                location.reload();
            });
        }else{
            layer.msg(data.promptMessage, {time: 1500});
        }
    }


    $('.wjy-list > ul > li').find('.btn').on('click', function (){

    });

    new Page('#page');
    //demo.html
    //<ul id="page" class="pagination" data-pageIndex="${page}" data-pageCount="${pageCount}" data-pageRows="10">
    //    </ul>
});