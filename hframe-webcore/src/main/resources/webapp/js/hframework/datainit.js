require(['layer','ajax','js/hframework/errormsg'], function () {

    var layer = require('layer');
    var ajax = require('ajax');
    var errormsg = require('js/hframework/errormsg');
    var loadingDictionaryKeys = {};

    $.textLoad =function($this){

    }

    $.componentLoad = function ($this, _func, _batchLoad, _$container,_showEleFunc) {
        var tagName = $this[0].tagName;
        var  dataCode = $this.attr("data-code");
        var  dataCondition = $this.attr("data-condition");
        //var dataValue = $this.attr("data-value");
        var relatElement = $this.attr("relat-element");

        if(relatElement) {
            while(relatElement.indexOf("{") > 0) {
                var elementName = relatElement.substring(relatElement.indexOf("{") + 1,relatElement.indexOf("}"));

                var $relElement;
                if($this.parents(".hfform").size() > 0) {
                    $relElement =  $($this.parents(".hfform")[0]).find("[name=" + elementName + "]");
                }else if($this.parents("tr").size() > 0) {
                    $relElement = $this.parents("tr").find("[name=" + elementName + "]");
                }else if($this.parents(".breadcrumb").size() > 0) {
                    $relElement = $this.parents(".breadcrumb").find("[name=" + elementName + "]");
                }

                if(!$relElement || $relElement.length == 0) {//取全局的
                    $relElement = $("body [name=" + elementName + "]");
                }

                var relElementValue =$relElement.val();
                if(!relElementValue) {//由于使用依赖的元素也是通过ajax加载，对应的value值还不能正确取出
                    relElementValue = $relElement.attr("data-value");
                }

                relatElement = relatElement.replace("{" + elementName + "}",  relElementValue);
            }


            if(dataCondition) {
                dataCondition = dataCondition + " && " + relatElement;
            }else {
                dataCondition = relatElement + "=" +relatElement;
            }
        }

        if(dataCode.startsWith("JSON:")) {
            var enums  =JSON.parse(dataCode.substr(5).replace(new RegExp(/(')/g),'"'));
            var data = [];
            for(var key in enums) {
                data.push({"value":key,"text":enums[key],"extInfo": null});
            }
            if(_showEleFunc) {
                _showEleFunc({"data": data});
            }
            if(_func) {
                _func();
            }
            return ;
        }

        $this.attr("req_dataCondition", dataCondition);
        if(_batchLoad) {
            if(loadingDictionaryKeys[_$container.attr("id") +"|" + tagName + "|" + dataCode + "|" + dataCondition] == null) {//首次加载
                loadingDictionaryKeys[_$container.attr("id") +"|" + tagName + "|" + dataCode + "|" + dataCondition] = -1;
            }else if(loadingDictionaryKeys[_$container.attr("id") +"|" + tagName + "|" + dataCode + "|" + dataCondition] == -1) {//正在加载过程中，服务端还未返回
                return;
            }else {//已有加载完成
                loadingDictionaryKeys[_$container.attr("id") +"|" + tagName + "|" + dataCode + "|" + dataCondition] = -1;;
            }
        }

        var _url =  "/dictionary.json";
        if(dataCode.startsWith("URL:")) {
            _url = dataCode.substring(4);
        }
        var _data = {"dataCode":dataCode,"dataCondition" : dataCondition};
        ajax.Post(_url,_data,function(data){
            if(data.resultCode == 0) {
                if(_batchLoad) {
                    loadingDictionaryKeys[_$container.attr("id") +"|" + tagName + "|" + dataCode + "|" + dataCondition] = 1;
                }
                if(_showEleFunc) {
                    _showEleFunc(data, dataCondition);
                }
                if(_func) {
                    _func();
                }

            }
        });
    }

    $.checkboxOrRadioLoad = function ($this, _func, _batchLoad, _$container) {
        var  dataCode = $this.attr("data-code");
        var  dataCondition = $this.attr("data-condition");

        $.componentLoad($this, _func, _batchLoad, _$container, function(_data,_req_dataCondition){
            if(!_data.data) return;

            var isBooleanElements = false;
            var booleanData = {};
            if(_data.data.length == 2) {
                var booleanElements = {"0":"否","1":"是"};
                for (var i = 0; i < _data.data.length; i++) {
                    //if(booleanElements[_data.data[i].value] == _data.data[i].text) {

                    if(booleanElements[_data.data[i].value]) {
                        booleanData[_data.data[i].value] = _data.data[i].text;
                        delete booleanElements[_data.data[i].value];
                    }
                }
                isBooleanElements = Object.getOwnPropertyNames(booleanElements).length == 0;
            }


            var inputArray = [];
            for (var i = 0; i < _data.data.length; i++) {
                var $newInput = $this.clone();
                $newInput.find("input").val(_data.data[i].value);
                $newInput.append(_data.data[i].text);
                $newInput.css("display","");
                //$newInput.uniform();
                inputArray.push($newInput);
            }
            if(_batchLoad) {
                _$container.find(".hfcheckbox[data-code='" + dataCode + "'][data-condition='" + dataCondition + "']"+
                    "[req_dataCondition='" + _req_dataCondition + "']").each(function(){
                    initCheckboxOrRadio($(this), inputArray, isBooleanElements, booleanData);
                });
                _$container.find(".hfradio[data-code='" + dataCode + "'][data-condition='" + dataCondition + "']"+
                    "[req_dataCondition='" + _req_dataCondition + "']").each(function(){
                    initCheckboxOrRadio($(this), inputArray, isBooleanElements, booleanData);
                });
            }else {
                initCheckboxOrRadio($this, inputArray, isBooleanElements, booleanData);
            }
        });
    }

    function initCheckboxOrRadio(_$this, _inputArray, _isBooleanElements, _booleanData) {
        var values = _$this.attr("data-value");
        var name = _$this.find("input").attr("name");

        if(_$this.hasClass("hfcheckbox") && _isBooleanElements) {
            _$this.find("input").val(0);
            _$this.addClass("boolCheckBox");

            var $trueCheckBox = _$this.clone();
            $trueCheckBox.find("input").val(1);

            $trueCheckBox.css("display","");
            _$this.after($trueCheckBox);

            if($trueCheckBox.hasClass("hfswitch")){
                $trueCheckBox.find("input").attr("data-off-text", _booleanData["0"]);
                $trueCheckBox.find("input").attr("data-on-text",  _booleanData["1"]);
                if(values == "1") {
                    $trueCheckBox.find("input").attr("checked", "checked");
                }
                $trueCheckBox.find("input").bootstrapSwitch();
            }
        }else {
            var inputArray = [];
            $(_inputArray).each(function(){
                inputArray.push($(this).clone());
            });
            _$this.after(inputArray);
        }
        _$this.parent().find("input[name=" + name + "]").uniform();

        if(values) {
            var valueArray = values.split(",");
            for(var index in valueArray){
                var $input =  _$this.parent().find("input[name=" + name + "][value='" + valueArray[index] + "']");
                if($input) $.uniform.update($input.attr("checked","true"));
            }
        }
        if(!_$this.hasClass("hfcheckbox") || !_isBooleanElements) {
            _$this.remove();
        }

        _$this.change();
    }

    $.resetChosenWidth = function(){
        $(".hfTreeList").each(function(){
            var maxWidth = 0;
            $(this).find(".chosen-container").each(function(){
                var width = $(this).css("width")
                if(width) {
                    if(parseInt(width.substr(0,width.length-2)) > maxWidth) {
                        maxWidth = parseInt(width.substr(0,width.length-2))
                    }
                }
            });
            if(maxWidth > 0) {
                $(this).find(".chosen-container").css("width", maxWidth + "px");
            }
        });
    }

    $.selectLoad = function ($this, _func, _batchLoad, _$container) {
        var $tagName = $this[0].tagName;
        var  dataCode = $this.attr("data-code");
        var  dataCondition = $this.attr("data-condition");

        $.componentLoad($this, _func, _batchLoad, _$container, function(_data,_req_dataCondition){
            if($tagName == 'SELECT') {
                if(!_data.data) return;

                var _html = [];
                for (var i = 0; i < _data.data.length; i++) {
                    _html.push('<option value="' + _data.data[i].value + '" data-hide=' + _data.data[i].extInfo + '>' + _data.data[i].text + '</option>');
                }
                var htmlStr = _html.join('');
                if(_batchLoad) {
                    if(dataCode.startsWith("JSON:")) {
                        _$container.find("select[data-code='" + dataCode + "'][data-condition='" + dataCondition + "']").each(function(){
                            initSelect($(this), htmlStr,dataCode,_data);
                        });
                    }else {
                        _$container.find("select[data-code='" + dataCode + "'][data-condition='" + dataCondition + "']" +
                            "[req_dataCondition='" + _req_dataCondition + "']").each(function(){
                            initSelect($(this), htmlStr,dataCode,_data);
                        });
                    }

                }else {
                    $this.each(function(){
                        initSelect($(this), htmlStr,dataCode,_data);
                    });
                    //initSelect($this, htmlStr,dataCode,_data);
                }
                $.resetChosenWidth();
            }else {
                for (var i = 0; i < _data.data.length; i++) {
                    var $newNode = $($this.prop("outerHTML").replace("#text", _data.data[i].text));
                    var $input = $newNode.find("input");
                    $input.val(_data.data[i].value);
                    $input.attr("id",$input.attr("name") + _data.data[i].value);
                    $this.after($newNode);
                    //$this.after($this.clone());
                }
                $this.prop("outerHTML",$this.prop("outerHTML").replace("#value", "").replace("#text", "请选择"));
            }
        });
    }

    function initSelect(_$this, _htmlStr, _dataCode, _data) {
        if(_$this.attr("multiple")) {
            _$this.html(_htmlStr);
        }else {
            _$this.html('<option value=""> - 请选择 - </option>' + _htmlStr);
            if(_$this.find("option[value='" + _$this.attr("data-value") + "']").size()> 0) {
                _$this.val(_$this.attr("data-value"));
            }else {
                _$this.val("");
            }

        }

        //如果是多选框，或者已经是selectx框
        if(_$this.attr("multiple") || _$this.hasClass("hfselectx") || (_dataCode.startsWith("URL:") || _dataCode.split(".").length > 2) && _data.data.length > 10) { //选择框设置为selectx元素
            _$this.addClass("hfselectx");
            _$this.chosen();//设置为selectx
            _$this.trigger("chosen:updated");
        }
        if(_$this.attr("multiple") && _$this.attr("data-value")) {
            _$this.val(_$this.attr("data-value").split(","));
            _$this.trigger("chosen:updated");
        }
        _$this.change();
    }

    //$.selectLoad = function ($this, _func, batchLoad, _$container) {
    //    var $tagName = $this[0].tagName;
    //    var  dataCode = $this.attr("data-code");
    //    var  dataCondition = $this.attr("data-condition");
    //    var dataValue = $this.attr("data-value");
    //    var relatElement = $this.attr("relat-element");
    //
    //    if(relatElement) {
    //        var elementName = relatElement.substring(relatElement.indexOf("{") + 1,relatElement.indexOf("}"))
    //        var $relElement;
    //        if($this.parents(".hfform").size() > 0) {
    //            $relElement =  $($this.parents(".hfform")[0]).find("[name=" + elementName + "]");
    //        }else if($this.parents("tr").size() > 0) {
    //            $relElement = $this.parents("tr").find("[name=" + elementName + "]");
    //        }else if($this.parents(".breadcrumb").size() > 0) {
    //            $relElement = $this.parents(".breadcrumb").find("[name=" + elementName + "]");
    //        }
    //
    //        var relElementValue =$relElement.val();
    //        if(!relElementValue) {//由于使用依赖的元素也是通过ajax加载，对应的value值还不能正确取出
    //            relElementValue = $relElement.attr("data-value");
    //        }
    //
    //        if(dataCondition) {
    //            dataCondition = dataCondition + " && " + relatElement.replace("{" + elementName + "}",  relElementValue);
    //        }else {
    //            dataCondition = relatElement + "=" +relatElement.replace("{" + elementName + "}",  relElementValue);
    //        }
    //    }
    //
    //    if(dataCode.startsWith("JSON:")) {
    //        var enums  =JSON.parse(dataCode.substr(5).replace(new RegExp(/(')/g),'"'));
    //        var _html = [];
    //        _html.push('<option value=""> - 请选择 - </option>');
    //        for(var key in enums) {
    //            _html.push('<option value="' + key + '">' + enums[key] + '</option>');
    //        }
    //        $this.html(_html.join(''));
    //        $this.val(dataValue);
    //        try{
    //            $this.change();
    //        }catch(e){
    //        }
    //
    //        return ;
    //    }
    //
    //    if(batchLoad) {
    //        if(loadingDictionaryKeys[_$container.attr("id") + dataCode + "|" + dataCondition] == null) {//首次加载
    //            loadingDictionaryKeys[_$container.attr("id") + dataCode + "|" + dataCondition] = -1;
    //        }else if(loadingDictionaryKeys[_$container.attr("id") + dataCode + "|" + dataCondition] == -1) {//正在加载过程中，服务端还未返回
    //            return;
    //        }else {//已有加载完成
    //            loadingDictionaryKeys[_$container.attr("id") + dataCode + "|" + dataCondition] = -1;;
    //        }
    //    }
    //
    //    var _url =  "/dictionary.json";
    //    if(dataCode.startsWith("URL:")) {
    //        _url = dataCode.substring(4);
    //    }
    //    var _data = {"dataCode":dataCode,"dataCondition" : dataCondition};
    //    ajax.Post(_url,_data,function(data){
    //        if(data.resultCode == 0) {
    //            if($tagName == 'SELECT') {
    //                var _html = [];
    //                if(data.data) {
    //                    for (var i = 0; i < data.data.length; i++) {
    //                        _html.push('<option value="' + data.data[i].value + '" data-hide=' + data.data[i].extInfo + '>' + data.data[i].text + '</option>');
    //                    }
    //                }
    //
    //                var htmlStr = _html.join('');
    //                if(batchLoad) {
    //                    loadingDictionaryKeys[_$container.attr("id") + dataCode + "|" + dataCondition] = 1;
    //                }
    //                if(data.data) {
    //                    if(batchLoad) {
    //                        _$container.find("select[data-code='" + dataCode + "'][data-condition='" + dataCondition + "']").each(function(){
    //                            if($(this).attr("multiple")) {
    //                                $(this).html(htmlStr);
    //                            }else {
    //                                $(this).html('<option value=""> - 请选择 - </option>' + htmlStr);
    //                                $(this).val($(this).attr("data-value"));
    //                            }
    //
    //                            if($(this).attr("multiple") || (dataCode.startsWith("URL:") || dataCode.split(".").length > 2) && data.data.length > 10) { //选择框设置为selectx元素
    //                                $(this).addClass("hfselectx");
    //                                $(this).chosen();//设置为selectx
    //                            }
    //                            if($(this).attr("multiple") && $(this).attr("data-value")) {
    //                                $(this).val($(this).attr("data-value").split(","));
    //                                $(this).trigger("chosen:updated");
    //                            }
    //
    //                            $(this).change();
    //                        });
    //                    }else {
    //                        $this.html(_html.join(''));
    //                        $this.val(dataValue);
    //                        if($(this).attr("multiple") || (dataCode.startsWith("URL:") || dataCode.split(".").length > 2) && data.data.length > 10) { //选择框设置为selectx元素
    //                            $this.addClass("hfselectx");
    //                            $this.chosen();//设置为selectx
    //                        }
    //                        $this.change();
    //                    }
    //                }
    //
    //
    //            }else {
    //                for (var i = 0; i < data.data.length; i++) {
    //                    $newNode = $($this.prop("outerHTML").replace("#text", data.data[i].text));
    //                    $input = $newNode.find("input");
    //                    $input.val(data.data[i].value);
    //                    $input.attr("id",$input.attr("name") + data.data[i].value);
    //                    $this.after($newNode);
    //                    //$this.after($this.clone());
    //                }
    //                $this.prop("outerHTML",$this.prop("outerHTML").replace("#value", "").replace("#text", "请选择"));
    //            }
    //            if(_func) {
    //                _func();
    //            }
    //
    //        }
    //    });
    //}

    var treeDataCache = {};
    $.selectPanelLoad = function ($this, $option) {
        var  dataCode = $this.attr("data-code");
        var  dataCondition = $this.attr("data-condition");
        var dataValue = $this.attr("data-value");

        var relatElement = $this.attr("relat-element");

        if(relatElement) {
            while(relatElement.indexOf("{") > 0) {
                var elementName = relatElement.substring(relatElement.indexOf("{") + 1,relatElement.indexOf("}"));

                var $relElement;
                if($this.parents(".hfform").size() > 0) {
                    $relElement =  $($this.parents(".hfform")[0]).find("[name=" + elementName + "]");
                }else if($this.parents("tr").size() > 0) {
                    $relElement = $this.parents("tr").find("[name=" + elementName + "]");
                }else if($this.parents(".breadcrumb").size() > 0) {
                    $relElement = $this.parents(".breadcrumb").find("[name=" + elementName + "]");
                }

                var relElementValue =$relElement.val();
                if(!relElementValue) {//由于使用依赖的元素也是通过ajax加载，对应的value值还不能正确取出
                    relElementValue = $relElement.attr("data-value");
                }

                relatElement = relatElement.replace("{" + elementName + "}",  relElementValue);
            }


            if(dataCondition) {
                dataCondition = dataCondition + " && " + relatElement;
            }else {
                dataCondition = relatElement + "=" +relatElement;
            }
        }

        if(treeDataCache[dataCode] == null) {
            var _url =  "/treeData.json";
            var _data = {"dataCode":dataCode,"dataCondition" : dataCondition, "dataValue" : dataValue};
            ajax.Post(_url,_data,function(data){
                if(data.resultCode == 0) {
                    //console.info(data.data);
                    treeDataCache[dataCode] = data.data.data;
                    $($this).val(data.data.disValue);
                    //$($this).attr("level","city");
                    $($this).citypicker(treeDataCache[dataCode], $option);

                    var event = document.createEvent('HTMLEvents'); //创建事件
                    event.initEvent("input", true, true); //设置事件类型为 input
                    console.info($this);
                    $this[0].dispatchEvent(event); //触发下 该事件（input 事件）
                    //$($this).val(data.data.disValue);
                    //$($this).change();
                    //window.ChineseDistricts = data.data;
                }
            });
        }else {
            $($this).citypicker(treeDataCache[dataCode], $option);
            var event = document.createEvent('HTMLEvents'); //创建事件
            event.initEvent("input", true, true); //设置事件类型为 input
            console.info($this);
            $this[0].dispatchEvent(event); //触发下 该事件（input 事件）
            //$($this).input();
            //$($this).change();
        }
    }

    $.reloadDisplay = function (_$container) {
        var $elements = $(_$container).find("[data-code][data-condition]");
        $elements.each(function(){
            var $this = $(this);
            if($this.is('select')) {
                $.selectLoad($this,null,true,_$container);
            }else if($this.hasClass("hfcheckbox") || $this.hasClass("hfradio") ) {
                $.checkboxOrRadioLoad($this,null,true,_$container);
            }else {
                $.selectPanelLoad($this);
            }
        });
    }
    $.reloadDisplay($("body"));

    $.reloadListDisplay = function () {
        listTextDisplay();
    }

    function listTextDisplay() {
        $("th[code][dataCode][dataCode!='']").each(function(){
            var $this =$(this);
            var code = $(this).attr("code");
            var dataCode = $(this).attr("dataCode");
            var dataValues=[];
            $("span[code='" + code + "']").each(function(){
                if($(this).text()) {
                    dataValues.push($(this).text());
                }
            });
            var _url =  "/getTexts.json";
            var _data = {"dataCode":dataCode, "dataValues" : dataValues};
            ajax.Post(_url,_data,function(data){
                if(data.resultCode == 0) {
                    if($this.html().endsWith("ID")) {
                        $this.html($this.html().substring(0,$this.html().length-2));
                    }
                    $("span[code='" + code + "']").each(function(){
                        if(data.data && data.data[$(this).text()] && data.data[$(this).text()].text) {
                            $(this).attr("value",$(this).text());
                            $(this).text(data.data[$(this).text()].text);
                        }
                    });
                }
            });
        });
    }
    listTextDisplay();


    //
    //$("[data-code][data-condition]").each(function(){
    //    var $this = $(this);
    //    if($this.is('select')) {
    //        $.selectLoad($this,null,true,$("body"));
    //    }else {
    //        $.selectPanelLoad($this);
    //    }
    //});

    function dealData1(_source, _data, _list) {
        var _html = [];
        _html.push('<option value=""> - 请选择 - </option>');

        for (var i = 0; i < _list.length; i++) {
            _html.push('<option value="' + _list[i].value + '" data-hide=' + _list[i].remark + '>' + _list[i].name + '</option>');
        }
        _html.push('</select>');
        var $child = $(_html.join(''));
        $child.change(_data['onchange']);
        var $parent = $(_source).parent().html('')
        $child.appendTo($parent);

        var _value = $(_source).val();
        //alert(_value);
        $('select[name="' + _data["tagName"] + '"]').val(_value);

        if(_data['onload'] != null) {
            _data['onload']();
        }
    }
    //
    //var pageNo = $(this).attr("pageNo");
    //var compoContainer = $(this).parents("[page][module]")[0];
    //var module = $(compoContainer).attr("module");
    //var page =$(compoContainer).attr("page");
    //var component  =$(compoContainer).attr("component");
    //var _url =  "/" + module + "/" + page + ".html";
    //var _data = {"pageNo":pageNo,"component" : component};
    //ajax.Post(_url,_data,function(data){
    //    var $newHfList = $(data);
    //    $(compoContainer).find(".hflist-pager").html($newHfList.find(".hflist-pager").html());
    //    $(compoContainer).find(".hflist-data").html($newHfList.find(".hflist-data").html());
    //},'html');
});

