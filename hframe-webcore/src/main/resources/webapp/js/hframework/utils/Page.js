/**
 * Created by Sean on 2015/12/16.
 */

var Page = function (selector) {
    this.$el = $(selector);
    var _pageIndex = this.$el.attr('data-pageIndex') || '0',
        _pageCount = this.$el.attr('data-pageCount') || '0',
        _pageRows  = this.$el.attr('data-pageRows');

    this.pageIndex = parseInt(_pageIndex);
    this.pageCount = parseInt(_pageCount);
    this.pageRows = parseInt(_pageRows);

    this.Init.apply(this);
}

Page.prototype.Init = function () {
    var _html = this.GetPageHtml();
    this.$el.html(_html);
}

Page.prototype.GetPageHtml = function () {
    var _html = [];
    if(this.pageCount > 7){
        if(this.pageIndex != 1){
            _html.push('<li> <a href="' + this.GetPageUrl(1) + '">首页</a> </li>')
            _html.push('<li> <a href="' + this.GetPageUrl(this.pageIndex - 1) + '">上一页</a> </li>')

        }

        if(this.pageIndex < 5){
            for(var i = 1; i <= 4; i ++){
                if(i == this.pageIndex){
                    _html.push('<li class="active"> <a href="javascript:;">' + i + '</a> </li>')
                }else{
                    _html.push('<li> <a href="' + this.GetPageUrl(i) + '">' + i + '</a> </li>')
                }
            }
            _html.push('<li> <span>…<span> </li>')
        }else if(this.pageCount - this.pageIndex < 4){
            _html.push('<li> <span>…<span> </li>')
            for(var i = this.pageCount - 3; i <= this.pageCount; i ++){
                if(i == this.pageIndex){
                    _html.push('<li class="active"> <a href="javascript:;">' + i + '</a> </li>')
                }else{
                    _html.push('<li> <a href="' + this.GetPageUrl(i) + '">' + i + '</a> </li>')
                }
            }
        }else{
            _html.push('<li> <span>…<span> </li>')
            for(var i = this.pageIndex - 2; i <= this.pageIndex + 2; i ++){
                if(i == this.pageIndex){
                    _html.push('<li class="active"> <a href="javascript:;">' + i + '</a> </li>')
                }else{
                    _html.push('<li> <a href="' + this.GetPageUrl(i) + '">' + i + '</a> </li>')
                }
            }
            _html.push('<li> <span>…<span> </li>')
        }


        if(this.pageIndex != this.pageCount){
            _html.push('<li> <a href="' + this.GetPageUrl(this.pageIndex + 1) + '">下一页</a> </li>')
            _html.push('<li> <a href="' + this.GetPageUrl(this.pageCount) + '">尾页</a> </li>')
        }

    }else if(this.pageCount > 0){
        if(this.pageIndex != 1){
            _html.push('<li> <a href="' + this.GetPageUrl(1) + '">首页</a> </li>')
            _html.push('<li> <a href="' + this.GetPageUrl(this.pageIndex - 1) + '">上一页</a> </li>')

        }
        for(var i = 1; i <= this.pageCount; i ++){
            if(i == this.pageIndex){
                _html.push('<li class="active"> <a href="javascript:;">' + i + '</a> </li>')
            }else{
                _html.push('<li> <a href="' + this.GetPageUrl(i) + '">' + i + '</a> </li>')
            }
        }
        if(this.pageIndex != this.pageCount){
            _html.push('<li> <a href="' + this.GetPageUrl(this.pageIndex + 1) + '">下一页</a> </li>')
            _html.push('<li> <a href="' + this.GetPageUrl(this.pageCount) + '">尾页</a> </li>')
        }
    }

    return _html.join('');
}

/**
 *
 * @param page
 * @constructor
 */
Page.prototype.GetPageUrl = function (page) {
    var _url = this.GetUrl();
    var _params = this.GetParams();
    var _hash = location.hash;
    var flag = false;;
    if(_params && _params.length > 0){
        for(var i = 0; i < _params.length; i++){
            if(_params[i].name != 'page' && _params[i].name != 'rows'){
                if(flag){
                    _url += '&';
                }else{
                    _url += '?';
                }
                flag = true;
                _url += _params[i].name + '=' + _params[i].value;
            }
        }
        if(flag){
            _url += '&page=' + page + '&rows=' + this.pageRows;
        }else{
            _url += '?page=' + page + '&rows=' + this.pageRows;
        }

        _url += _hash;
    }else{
        _url += '?page=' + page + '&rows=' + this.pageRows;
        _url += _hash;
    }
    return _url;
}
Page.prototype.GetUrl = function () {
    var _url = '';
    var _location = location.href;
    if (_location.indexOf('?') > 0) {
        _url = _location.split('?')[0];
    }else{
        if (_location.indexOf('#') > 0) {
            _url = _location.split('#')[0];
        } else {
            _url = _location;
        }
    }
    return _url;
}
Page.prototype.GetParams = function () {
    // 获得请求地址中的参数列表
    var _search = location.search;
    var obj = [];
    var _params = [];
    if (_search != null && _search.length) {
        _search = _search.replace('?', '');
        _params = _search.split('&');
    } else {
        return null;
    }

    if (_params != null && _params.length) {
        var _len = _params.length;

        for (var i = 0; i < _len; i++) {

            var _paramsItem = _params[i].split('=');
            if (_paramsItem != null && _paramsItem.length >= 2) {
                var item = {};
                item.name = _paramsItem[0];
                item.value = _paramsItem[1];
                obj.push(item);
            }
        }

        return obj;
    } else {
        return null;
    }
}
