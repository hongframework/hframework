/*
 *auth:zhangqh
 *date:2015-12-17
 */

$(document).ready(function(e) {

    $.fn.onload = function (_func) {
        debugger;
        _func().apply(this);
    }

    $.fn.initCol = function (_data) {
        debugger;
        // 初始化入参信息，没有没传进行默认
        if (_data["tagName"] == null) {
            _data["tagName"] = $(this).attr("name");
        }
        if (_data["tagType"] == null) {
            _data["tagType"] = "select";
        }
        if (_data["dataHide"] == null) {
            _data["dataHide"] = "";
        }
        if (_data["dataCond"] == null) {
            _data["dataCond"] = "";
        }

        if (_data["async"] == null) {
            _data["async"] = "true";
        }

        var source = this;
        var url = "/ajax/getdata/dictionary/getByCode.json?name=" + _data["dataCode"] + "&extColumn=" + _data["dataHide"] + "&condition=" + _data["dataCond"];
        if (_data["dataSrc"] == "table") {
            url += "&isDynamic=1";
        }
        $.ajax({
            async: _data["async"],
            url: url,
            type: 'post',
            dataType: 'json',
            success: function (jsonData) {

                if (typeof jsonData !== 'undefined') {
                    var list = jsonData.dictionaryList;
                    dealData1(source, _data, list);
                }
            },
            error: function () {
            }
        });
    };

    function dealData1(_source, _data, _list) {
        var _html = [];
        _html.push('<select id="' + _data["tagName"] + '" name="' + _data["tagName"] + '" class="' + _data["tagClass"] + '">');
        _html.push('<option value=""> - 请选择 - </option>');

        for (var i = 0; i < _list.length; i++) {
            _html.push('<option value="' + _list[i].value + '" data-hide=' + _list[i].remark + '>' + _list[i].name + '</option>');
        }
        _html.push('</select>');
        var $child = $(_html.join(''));
        $child.change(_data['onchange']);
//			$child.trigger('change');
//			$child.onload(_data['onload']);
//			$child.onload();


        var $parent = $(_source).parent().html('')
        $child.appendTo($parent);

        var _value = $(_source).val();
        //alert(_value);
        $('select[name="' + _data["tagName"] + '"]').val(_value);

        if(_data['onload'] != null) {
            _data['onload']();
        }

        //var $option = $('select[name="' + _data["tagName"] + '"]').children('option');
        //for (var i = 0; i < $option.length; i++) {
        //    var _text = $option.eq(i).val();
        //    if (_value == _text) {
        //        $option[i].selected = true;
        //        break;
        //    }
        //}
    }


    var localData = {};
    function getDicText(dicName, dicValue, isDynamic){
        var has = dicName in localData;
        if(!has) {
            loadData(dicName,isDynamic);
        }
        var tempMap = localData[dicName];
        var text = tempMap[dicValue];
        if(typeof text !== 'undefined' ) {
            return text;
        }else {
            if(dicValue == null || dicValue == "null") {
                dicValue = "";
            }
            return dicValue;
        }
    }

    function loadData(dicName, isDynamic) {
        var url = "${base}/ajax/getdata/dictionary/getByCode.json?name=" + dicName;
        if(isDynamic != null) {
            url += "&isDynamic=1";
        }
        $.ajax({
            async:false,
            url:url,
            type:'post',
            dataType:'json',
            success:function(jsonData){
                if(typeof jsonData !== 'undefined'){
                    var _list = jsonData.dictionaryList;
                    var tempMap = {};
                    for(var i = 0; i < _list.length; i++){
                        tempMap[_list[i].value] = _list[i].name;
                    }
                    localData[dicName] = tempMap;
                }
            },
            error: function (){

            }
        });
    }

});