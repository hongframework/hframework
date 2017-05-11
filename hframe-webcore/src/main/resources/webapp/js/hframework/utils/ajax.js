define(['layer'],function (){
    // 这个主要是用对后台进行ajax操作的函数的封装，后期可能会经常遇到
    var layer = require('layer');

    var AjaxRequest = function (url ,headers, data, success, async, beforsend, complete){
        // 异步操作的接口
        if(typeof async === 'undefined' || async){
            async = true;
        }else{
            async = false;
        }
        $.ajax({
            url: url,
            headers:headers,
            data: data,
            type: 'post',
            dataType: 'json',
            async: async,
            beforeSend: function (){
                if(beforsend && typeof beforsend === 'function'){
                    beforsend();
                }
            },
            success: function(data, status){

                if(status == 'success'){
                    if(data.status.indexOf('9999') > -1){
                        layer.msg('请求服务器错误！');
                    } else if(data.status.indexOf('9998') > -1){
                        layer.msg('页面已经过期！');
                    }else {
                        success(data);
                    }
                }else{
                    layer.msg('请求服务器错误！');
                }
            },
            error: function (){
                layer.msg('操作失败！', {time: 1000});
            },
            complete: function (){
                if(complete && typeof complete === 'function'){
                    complete();
                }
            }
        });
    };

    var AjaxPost = function (url,data,callback, dataType){
        if(dataType == null) {
            dataType = 'json';
        }
        $.post(url, data, callback, dataType);
    }
    // 异步方法封装
    var AjaxGet = function (url,data,callback){
        $.get(url, data, callback);
    }
    return {
        Request: AjaxRequest,
        Post: AjaxPost,
        Get: AjaxGet
    };
});