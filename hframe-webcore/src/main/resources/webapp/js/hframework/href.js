require(['layer','ajax','js/hframework/errormsg'], function () {
    var layer = require('layer');
    var ajax = require('ajax');
    //var flist = require('js/hframework/list');

    //$('form').submit(function(){
    //    return false;
    //});

    //动态刷新的form注解需要捆绑改submit属性，否则就直接提交走了
    $('form').live("submit", function(){
        if(!$(this).attr("action")) {
            return false;
        }
    });

    $("a").click(function(){
        var href = $(this).attr("href");
        if(href.endsWith(".json") || href.endsWith(".html")) {
            showProcessBar();
            $(this).attr("orig-href", $(this).attr("href"));
            var pageInfo = getPageContextInfo();
            if(pageInfo){
                $(this).attr("href", $(this).attr("orig-href") + "?" +　pageInfo);
            }else{
                $(this).attr("href", $(this).attr("orig-href"));
            }

            //hideProcessBar();
        }

    });

    $(".hfhref").live("click", function(){
        showProcessBar();
        var action =JSON.parse($(this).attr("action"));
        var param  = formatContent($(this).attr("params"), $(this));
        var contextValues = getPageContextInfo();
        if(contextValues) {
            param = contextValues + "&" + param;
        }
        doEvent(action, param, $(this));
        hideProcessBar();
    });

    //select加载时change了一下，如果别的元素连带该select，导致再加载一次，因此需要需要处理hfselect-init
    $(".hfselect").die().live("change", function(){
        var curValue =$(this).val();
        if($(this).hasClass("hfselect-init")) {
            $(this).removeClass("hfselect-init");
            $(this).attr("last-value", curValue);
            return;
        }

        var lastValue = $(this).attr("last-value");
        if(lastValue == curValue) {
            return;
        }
        $(this).attr("last-value", curValue);

        //var initing = $(this).attr("initing");
        //if(initing == "true") {
        //    $(this).removeAttr("initing");
        //    return;
        //}
        var $action =JSON.parse($(this).attr("action"));
        var $param  = formatContent($(this).attr("params"), $(this));
        var $contextValues = getPageContextInfo();
        if($contextValues) {
            $param = $contextValues + "&" + $param;
        }
        doEvent($action, $param, $(this));
    });

    $('.tree').bind('selected',function (event,data){
        treeClick(event,data.info[0],$(this));
    });
    //$('.tree').bind('closed',function (event,data){
    //    treeClick(event,data,$(this));
    //});
    //$('.tree').bind('opened',function (event,data){
    //    treeClick(event,data,$(this));
    //});
    $('.tree').bind('clickBtn',function (event,_$btn,data){
        var id = data.additionalParameters.id;
        var $param = _$btn.attr("params");
        var $action = JSON.parse(_$btn.attr("action"));
        if($param.indexOf("{") > 0 && $param.indexOf("}") > 0) {
            $param = $param.replace($param.substring($param.indexOf("{") , $param.indexOf("}") + 1), id);
        }
        //给被刷新容器直接赋值
        if($("div[path][component]").size() > 0) {
            $("div[path][component]").attr("path",id);
        }

        var $contextValues = getPageContextInfo();
        if($contextValues) {
            if($param && "null" != $param) {
                $param = $contextValues + "&" + $param;
            }else {
                $param = $contextValues;
            }
        }
        doEvent($action, $param, $(this))
    });

    function treeClick(event,selectItemData, $this){
        if(selectItemData.name.indexOf("class='dyn-tree-oper'") >= 0) {
            return ;
        }
        var id = selectItemData.additionalParameters.id;

        var $param = $($(".dyn-tree-ele span")[0]).attr("params");
        var $action = JSON.parse($($(".dyn-tree-ele span")[0]).attr("action"));
        if($param.indexOf("{") > 0 && $param.indexOf("}") > 0) {
            $param = $param.replace($param.substring($param.indexOf("{") , $param.indexOf("}") + 1), id);
        }
        $param = $param + "&_treeItemId=" + id;
        //给被刷新容器直接赋值
        if($("div[path][component]").size() > 0) {
            $("div[path][component]").attr("path",id);
        }

        var $contextValues = getPageContextInfo();
        if($contextValues) {
            $param = $contextValues + "&" + $param;
        }
        //alert($param);
        doEvent($action, $param, $this)

    }


    function doEvent($action, $param,  $this){
        var $type = null;
        for(var type in $action) {
            $type = type;
            break;
        }

        var url = $action[$type].action;
        if($type == "confirm") {
            var content = formatContent($action[$type].content,$this);
            showConfirmDialog(content,function(){
                delete $action[$type];
                doEvent($action,$param,$this);
            });
        }else if($type == "alert") {
            var content = formatContent($action[$type].content,$this);
            showConfirmDialog(content,function(){
                delete $action[$type];
                doEvent($action,$param,$this);
            });
        }else if($type == "pageFwd") {
            var isStack =$action[$type].isStack;
            var $componentParam  = formatContent($($this).attr("params"), $($this));
            if($componentParam != null && $componentParam.endsWith("thisForm")) {
                $thisForm = $this.parents("form")[0];
                if(!$thisForm) {
                    $thisForm = $("body form:last");
                }
                //参数检查
                if(!$.checkSubmit($thisForm)) {
                    //alert("字段不能为空！");
                    return;
                }

                showProcessBar();
                if($param && $param != "thisForm") {
                    location.href = url + "?" + $param;
                }else {
                    location.href = url;
                }


                //由于submit提交，刷新网页时会提示是否重复提交（比如登录后页面），所以暂时没有必要带上thisForm的内容
                //$($thisForm).attr("action", url + "?" + $param);
                //$($thisForm).attr("method", "post");
                //$($thisForm).submit();
            }else {
                if($($this).attr("params") == "checkIds") {
                    var checkIds = new Array();
                    var $thisList = $this.parents(".hflist")[0];
                    var $allChecked = $($thisList).find("tbody input[type=checkbox][name=checkIds]:checked");
                    $allChecked.each(function(){
                        var columnName = $(this).attr("value-key");
                        var columnValue  = formatContent("{" + columnName + "}", $(this));
                        checkIds.push(columnValue);
                    });
                    showProcessBar();
                    location.href = url + "?" + $param + "=" + checkIds.join();
                }else{
                    showProcessBar();
                    if($param) {
                        location.href = url + "?" + $param;
                    }else{
                        location.href = url;
                    }

                }

            }
        }else if($type == "pageFwdWithData") {
            var isStack =$action[$type].isStack;
            var $componentParam  = formatContent($($this).attr("params"), $($this));
            if($componentParam != null && $componentParam.endsWith("thisForm")) {
                $thisForm = $this.parents("form")[0];
                if(!$thisForm) {
                    $thisForm = $("body form:last");
                }
                //参数检查
                if(!$.checkSubmit($thisForm)) {
                    //alert("字段不能为空！");
                    return;
                }

                showProcessBar();
                $($thisForm).attr("action", url + "?" + $param);
                $($thisForm).attr("method", "post");
                $($thisForm).submit();
            }else {
                if($($this).attr("params") == "checkIds") {
                    var checkIds = new Array();
                    var $thisList = $this.parents(".hflist")[0];
                    var $allChecked = $($thisList).find("tbody input[type=checkbox][name=checkIds]:checked");
                    $allChecked.each(function(){
                        var columnName = $(this).attr("value-key");
                        var columnValue  = formatContent("{" + columnName + "}", $(this));
                        checkIds.push(columnValue);
                    });
                    showProcessBar();
                    location.href = url + "?" + $param + "=" + checkIds.join();
                }else{
                    showProcessBar();
                    location.href = url + "?" + $param;
                }

            }
        }else if($type == "openPage") {

            if($($this).attr("params") == "checkIds") {
                var checkIds = new Array();
                var $thisList = $this.parents(".hflist")[0];
                var $allChecked = $($thisList).find("tbody input[type=checkbox][name=checkIds]:checked");
                $allChecked.each(function(){
                    var columnName = $(this).attr("value-key");
                    var columnValue  = formatContent("{" + columnName + "}", $(this));
                    checkIds.push(columnValue);
                });

                window.open( url + "?" + $param + "=" + checkIds.join());
            }else{
                window.open( url + "?" + $param);
            }

        }else if($type == "ajaxSubmitByJson") {
            var _data;
            var targetId =$action[$type].targetId;

            if(targetId != null) {
                var json = {};
                var targetIds = targetId.split(",");
                var checkUnPass = false;
                for(var tarId in targetIds) {
                    $("[component= " + targetIds[tarId] +"]").each(function(i, v){
                        var $component = $(this);
                        //只要visiable才进行保存，比如添加数据源，如果只有mysql数据源，其他的数据源不需要校验与保存
                        if($component.is(":visible") && !checkUnPass){
                            if($component.find("form").length > 0) {
                                var $form = $component.find("form:first")
                                //参数检查
                                if(!$.checkSubmit($form)) {
                                    //alert("字段不能为空！");
                                    checkUnPass = true;
                                    return;
                                }

                                json[targetIds[tarId] + "|" + i] = JSON.parse($form.serializeJson());
                            }else {
                                var hierarchy = $component.orgchart('getHierarchy');
                                json[targetIds[tarId] + "|" + i]  = JSON.stringify(hierarchy, null, 2);
                            }
                        }
                    });
                    if(checkUnPass) {
                        return;
                    }
                }
                //console.log(JSON.stringify(json));
                _data = JSON.stringify(json);
            }else if($param.endsWith("thisForm")) {
                if($this.parents("form").length == 0) {
                    //var $rootNodes = $this.parents("div .hfspan").children(".hfcontainer").children("div").children("div .box");
                    var $rootNodes = $this.parents("div.hfspan").find(".hfcontainer:first > div > div > div.box:first, .hfcontainer:first > div > div.box")
                    var filePath = $this.parents("div .hfspan").find("div[path]").attr("path");
                    $param = $param + "&path=" + filePath;
                    //alert(filePath);
                    var json = getNodesJson($rootNodes);
                    _data = JSON.stringify(json);
                    //console.log(JSON.stringify(json));
                }else {
                    var $thisForm = $this.parents("form")[0];
                    //参数检查
                    if(!$.checkSubmit($($thisForm))) {
                        //alert("字段不能为空！");
                        return;
                    }
                    _data = $($thisForm).serializeJson();
                }

            }else {
                _data = parseUrlParamToObject($param);
            }
            //_data ='[{"hfpmProgramId":"123","hfpmProgramName":"test","hfpmProgramCode":"234","hfpmProgramDesc":"234","opId":"234","createTime":"2015-10-31 00:20:58","modifyOpId":"","modifyTime":"2015-10-31 00:20:58","delFlag":""},{"hfpmProgramId":"151031375370","hfpmProgramName":"框架","hfpmProgramCode":"hframe","hfpmProgramDesc":"框架","opId":"999","createTime":"2015-10-31 00:20:58","modifyOpId":"999","modifyTime":"2015-10-31 00:20:58","delFlag":"0"}]';
            //console.log(_data);
            showProcessBar();
            if(url.substr(0,1) != "/") {
                url = "/" + url;
            }
            $.ajax({
                url: url  + "?" + $param,
                data: _data,
                type: 'post',
                contentType : 'application/json;charset=utf-8',
                dataType: 'json',
                success: function(data){
                    hideProcessBar();
                    if(data.resultCode != '0') {
                        alert(data.resultMessage);
                        return;
                    }

                    delete $action[$type];
                    doEvent($action,$param,$this);
                }
            });

        }else if($type == "ajaxSubmit") {
            var _data = {};
            var $componentParam  = formatContent($($this).attr("params"), $($this));

            if(url.endsWith("deleteByAjax.json") && $componentParam != null && $componentParam.endsWith("=")){
                if($($this.parents("tr")[0]).siblings().size()> 0){
                    $this.parents("tr")[0].remove();
                }
                return;
            }


            if($componentParam != null && $componentParam.endsWith("thisForm")) {
                $thisForm = $this.parents("form")[0];
                //参数检查
                if(!$.checkSubmit($thisForm)) {
                    //alert("字段不能为空！");
                    return;
                }

                $($thisForm).find(".boolCheckBox input[value=1]").each(function(){
                    if($(this).is(':checked')){
                        $.uniform.update($(this).parents(".boolCheckBox:first").prev().find("input[value=0]").removeAttr("checked"));
                    }else {
                        $.uniform.update($(this).parents(".boolCheckBox:first").prev().find("input[value=0]").attr("checked","true"));
                    }
                });

                _data = parseUrlParamToObject(decodeURIComponent($($thisForm).serialize().replace(/\+/g," ")));
            }else {
                if($($this).attr("params") == "checkIds") {
                    var checkIds = new Array();
                    var $thisList = $this.parents(".hflist")[0];
                    var $allChecked = $($thisList).find("tbody input[type=checkbox][name=checkIds]:checked");
                    $allChecked.each(function(){
                        var columnName = $(this).attr("value-key");
                        var columnValue  = formatContent("{" + columnName + "}", $(this));
                        checkIds.push(columnValue);
                    });

                    _data["checkIds"] = checkIds;

                }else {
                    _data = parseUrlParamToObject($param);
                    $param = "";
                    //版本冲突，不知道为什么加这部分内容， 注掉改部分内容
                    //$thisForm = $this.parents("form")[0];
                    //tmpArray = parseUrlParamToObject($($thisForm).serialize());
                    //for(var $index in tmpArray) {
                    //    _data[$index] = decodeURIComponent(tmpArray[$index]);
                    //}
                }



            }
            console.log(_data);
            showProcessBar();
            var curl;
            if(url.indexOf("?") > 0) {
                curl = url  + "&" + $param
            }else {
                curl = url  + "?" + $param
            }
            //_data = {"hfmdEntityAttrId":"","hfmdEntityAttrName":"1231232132","hfmdEntityAttrCode":"","hfmdEntityAttrDesc":"","attrType":"","size":"","ispk":"","nullable":"","isBusiAttr":"","isRedundantAttr":"","relHfmdEntityAttrId":"","hfmdEnumClassId":"","pri":"","hfpmProgramId":"","hfpmModuleId":"","hfmdEntityId":"","opId":"","createTime":"2015-02-13 12:12:12","modifyOpId":"","modifyTime":"2015-02-13 12:12:12","delFlag":""};
            ajax.Post(curl, _data,function(data){
                hideProcessBar();
                if(data.resultCode != '0') {
                    alert(data.resultMessage);
                    return;
                }

                delete $action[$type];
                if(url.endsWith("deleteByAjax.json") && $this.parents("tr").length> 0){
                    if($($this.parents("tr")[0]).siblings().size()> 0){
                        $this.parents("tr")[0].remove();
                    }
                    return;
                }
                doEvent($action,$param,$this);
            });
        }else if($type == "component.reload") {

            var $curComponent = $this.parents("[component]")[0];

            var targetId = $action[$type].targetId;
            $targetComponent = $this.parents("[component]")[0];
            if(targetId) {
                $targetComponent = $("[component=" + targetId + "]");
            }

            if($($curComponent).hasClass("hftree")) {
                $($targetComponent).attr("param",$param);
            }else {
                if($this.parents("form").size() > 0) {
                    $thisForm = $this.parents("form")[0];
                    $($targetComponent).attr("param",$($thisForm).serialize());
                }
            }
            delete $action[$type];

            if($($targetComponent).hasClass("hflist")) {
                refreshList(1,$targetComponent);
            }else {
                refreshComponent($targetComponent);
            }


        }else if($type == "scrollIntoView") {
            if(targetId != null) {
                var json = {};

                var $targetComponent = $this.parents(".hfcontainer:first").find("div[dc='" + targetId + "']:first");
                if($targetComponent == null) {
                    var seq = 0;
                    if (targetId.endsWith("]")) {
                        seq = targetId.substring(targetId.length - 2, targetId.length - 1) - 1;
                        targetId = targetId.substring(0, targetId.length - 3);

                    }
                    $targetComponent = $($("[component= '" + targetId + "']").get(seq));
                }
                $targetComponent[0].scrollIntoView(true);
            }else {
                var id = $param.substring($param.indexOf("&_treeItemId=")+"&_treeItemId=".length);
                $("div[id='" + id + "']")[0].scrollIntoView(true);
            }
            delete $action[$type];


        }else if($type == "componentControl") {
            var targetId =$action[$type].targetId;
            var param = $action[$type].param;
            if(param == null) param = "{}";
            var paramJsonObject = JSON.parse( param);
            if(targetId != null) {
                var json = {};

                var $targetComponent = $this.parents(".hfcontainer:first").find("div[dc='" + targetId + "']:first");
                if(!$targetComponent[0]) {
                    var seq = 0;
                    if(targetId.endsWith("]")){
                        seq = targetId .substring(targetId.length - 2,targetId.length - 1) - 1;
                        targetId = targetId.substring(0,targetId.length - 3);

                    }
                    $targetComponent = $($("[component= '" + targetId + "']").get(seq));
                }
                if(paramJsonObject.event == "toggle") {
                    $targetComponent.toggle(500,function(){
                        if($targetComponent.is(":hidden")) {
                            $this.removeClass("switch-hidden")
                        }else{
                            $this.addClass("switch-hidden")
                        }
                    });
                }else {

                    var $targetHelperComponent = $this.parents(".hfcontainer:first").find("div[dc='" + targetId + "_helpTag']:first");
                    if($targetHelperComponent != null){
                        $this.helperpicker();
                    }else {
                        if($targetComponent.is(":hidden")) {
                            $targetComponent.show();
                        }
                    }

                }
            }

            delete $action[$type];


        }else if($type == "page.reload") {
            showProcessBar();
            var paramObj = {};
            var $curComponent = $this.parents("[component]")[0];
            if(!$($curComponent).hasClass("hftree")) {
                paramObj = parseUrlParamToObject($param, true);
            }

            var url = location.href;
            //alert($param + " | " + url);
            for(var key in paramObj) {
                if(key != "") {
                    url = changeURLParameterValue(url,key, paramObj[key]);
                }
            }

            delete $action[$type];
            location.href = url;
        }else if($type == "page.reload.static") {
            showProcessBar();
            delete $action[$type];
            location.reload();
        }else if($type == "component.row.delete") {
            $this.parents("tr:first").remove();
        }else if($type == "component.row.add") {
            $curRow = $this.parents("tr")[0];
            $newRow = $($curRow).clone();
            $($newRow).find("input").val("");
            $($newRow).find("[readonly=readonly]").removeAttr("readonly");
            $($curRow).after($newRow);
            $($newRow).find(".hfselectx").each(function(i){
                $(this).next().remove();
                $(this).show();
                $(this).chosen();//设置为selectx
            });
            $($curRow).find(".hfselect").each(function(i){
                var $target = $($newRow).find(".hfselect").eq(i);
                $target.removeClass("city-picker-input");
                $target.next().remove();
                $target.next().remove();
                //$target.citypicker.Constructor

                $.selectPanelLoad($target);;
            });

            $($newRow).find(".hfcheckbox input").uniform();
        }else if($type == "component.row.copy") {
            $curRow = $this.parents("tr")[0];
            $newRow = $($curRow).clone();
            $($newRow).find("input[type=hidden]").val("");
            $($curRow).find("select").each(function(i){
                $($newRow).find("select").eq(i).val($(this).val());
            });
            $($curRow).after($newRow);
            $($newRow).find(".hfselectx").each(function(i){
                $(this).next().remove();
                $(this).show();
                $(this).chosen();//设置为selectx
            });

            $($curRow).find(".hfselect").each(function(i){
                var $target = $($newRow).find(".hfselect").eq(i);
                $target.removeClass("city-picker-input");
                $target.next().remove();
                $target.next().remove();
                //$target.citypicker.Constructor

                $.selectPanelLoad($target);;
            });

            $($newRow).find(".hfcheckbox input").uniform();

        }else if($type == "component.row.up") {
            $curRow = $this.parents("tr")[0];
            $targetRow = $($curRow).prev();
            $newRow = $($targetRow).clone();
            $($targetRow).find("select").each(function(i){
                $($newRow).find("select").eq(i).val($(this).val());
            });
            $($curRow).after($newRow);
            $targetRow.remove();

        }else if($type == "component.row.down") {
            $curRow = $this.parents("tr")[0];
            $targetRow = $($curRow).next();
            $newRow = $($targetRow).clone();
            $($targetRow).find("select").each(function(i){
                $($newRow).find("select").eq(i).val($(this).val());
            });
            $($curRow).before($newRow);
            $targetRow.remove();
        }else if($type == "component.row.remove") {
            $curRow = $this.parents("tr")[0];
        }else if($type == "dialog") {
            showDialog(url + "?" + "isPop=true&" +$param,function(){
                delete $action[$type];
                doEvent($action,$param,$this);
            });
        }
        //delete $action[$type];
    }

    function getNodesJson($rootNodes){
        var json = {};
        $($rootNodes).each(function(){
            var componentId = $(this).attr("component");
            var id = $(this).parent("div[dc]").attr("dc");
            if(componentId != null) {
                if(componentId =="mutexContainer") {
                    var data = [];
                    var $subInst = $(this).children(".box-content").children(".tab-content").children(".tab-pane").not(".helper-div").children("div .hfcontainer");
                    $($subInst).each(function(){
                        var $subNodes = $(this).children("div").children("div .box");
                        var subJson = getNodesJson($subNodes);
                        data.push(subJson);
                    });
                    json[id] =data;
                }else if(componentId =="flatContainer") {
                    var data = [];
                    var $subInst = $(this).children(".box-content").children("div").not(".helper-div").children("div .hfcontainer");
                    $($subInst).each(function(){
                        var $subNodes = $(this).children("div").children("div");
                        var subJson = getNodesJson($subNodes);
                        data.push(subJson);
                    });
                    json[id] =data;
                }else {
                    json[id] = $(this).find("form").serializeJson();
                    //console.info($(this).find("form").serializeJson());
                }
            }
        });

        return json;
    }

    function changeURLParameterValue(destiny, par, par_value)
    {
        var pattern = par+'=([^&]*)';
        var replaceText = par+'='+par_value;
        if (destiny.match(pattern))
        {
            var tmp = '/\\'+par+'=[^&]*/';
            tmp = destiny.replace(eval(tmp), replaceText);
            return (tmp);
        }
        else
        {
            if (destiny.match('[\?]'))
            {
                return destiny+'&'+ replaceText;
            }
            else
            {
                return destiny+'?'+replaceText;
            }
        }
        return destiny+'\n'+par+'\n'+par_value;
    }

    var refreshList = function(pageNo, compoContainer){
        var module = $(compoContainer).attr("module");
        var page =$(compoContainer).attr("page");
        var component  =$(compoContainer).attr("component");
        var param  =$(compoContainer).attr("param");
        var _url =  "/" + module + "/" + page + ".html";
        var _data = {"pageNo":pageNo,"component" : component};
        if(param) {
            console.log("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":").replace(new RegExp("&","gm"),",\"").replace(new RegExp(":,","gm"),":null,")  + "}");
            var params =JSON.parse("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":\"").replace(new RegExp("&","gm"),"\",\"").replace(new RegExp(":,","gm"),"\":null,")  + "\"}")
            //var params =JSON.parse("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":").replace(new RegExp("&","gm"),",\"").replace(new RegExp(":,","gm"),":null,")  + "}");
            for (var key in params) {
                _data[key]=decodeURI(params[key]).trim();
            }
        }
        console.log(_data);
        //alert(_data);
        showProcessBar();
        ajax.Post(_url,_data,function(data){
            var $newHfList = $(data);
            if($newHfList.find(".hflist-pager")[0]){
                $(compoContainer).find(".hflist-pager").html($newHfList.find(".hflist-pager").html());
                $(compoContainer).find(".hflist-pager").show();
            }else {
                $(compoContainer).find(".hflist-pager").hide();
            }

            $(compoContainer).find(".hflist-data").html($newHfList.find(".hflist-data").html());
            componentinit();
            $.reloadListDisplay();
            hideProcessBar();
        },'html');
    }

    var refreshComponent = function(compoContainer){
        var module = $(compoContainer).attr("module");
        var page =$(compoContainer).attr("page");
        var component  =$(compoContainer).attr("component");
        var param  =$(compoContainer).attr("param");
        var _url =  "/" + module + "/" + page + ".html";
        var _data = {"component" : component};
        if(param) {
            console.log("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":").replace(new RegExp("&","gm"),",\"").replace(new RegExp(":,","gm"),":null,")  + "}");
            var params =JSON.parse("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":\"").replace(new RegExp("&","gm"),"\",\"").replace(new RegExp(":,","gm"),"\":null,")  + "\"}")
            //var params =JSON.parse("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":").replace(new RegExp("&","gm"),",\"").replace(new RegExp(":,","gm"),":null,")  + "}");
            for (var key in params) {
                _data[key]=decodeURI(params[key]).trim();
            }
        }
        console.log(_data);
        showProcessBar();
        //alert(_data);
        ajax.Post(_url,_data,function(data){
            var $newComponent = $(data);

            var $targetCoponent = $newComponent.find(".hfcontainer[component=container]");
            if($targetCoponent != null && $targetCoponent.size() > 0) {//表明为容器
                $(compoContainer).html($targetCoponent.html());
                componentinit();
                $.reloadDisplay(compoContainer);
            }else {//表明为普通组件
                $(compoContainer).find(".box-content").html($newComponent.find(".box-content").html());
                $.reloadDisplay(compoContainer.find(".box-content"));
                var $groupElement = compoContainer.parents("[group][group !='']:first");
                if($groupElement) {
                    var groupName = $groupElement.attr("group");
                    $("[group][group='" + groupName + "']").each(function(index, element){
                        if($(element) != $groupElement) {
                            $(element).hide();
                        }
                    });
                    $groupElement.show();
                }
            }
            hideProcessBar();
        },'html');
    }



    function parseUrlParamToJson($paramStr){
        return JSON.stringify(parseUrlParamToObject($paramStr));
    }

    function parseUrlParamToObject($paramStr, $containBlank){
        console.info($paramStr);
        var result = {};
        if(!$paramStr) {
            return result;
        }
        var $params = $paramStr.split("&");
        for(var $index in $params) {
            var key = $params[$index].substring(0, $params[$index].indexOf("="));
            var value = $params[$index].substring($params[$index].indexOf("=") + 1);
            if(value != '' || $containBlank) {
                //if(result[key] != null) {
                //    if(result[key] instanceof Array) {
                //        result[key].push(value);
                //    }else {
                //        result[key] = [result[key],value];
                //    }
                //}else {
                if(key == "createTime" || key == "modifyTime" || key == "ctime" || key == "mtime") {
                    continue;
                }
                if(result[key]) {
                    result[key] =result[key] + "," + value;
                }else {
                    result[key] = value;
                }

                //}
            }
        }
        return result;
    }

    function formatContent($param, $this){
        if($param) {
            var result =$param;
            while(result.indexOf("{") > -1 && result.indexOf("}") > -1){
                var value;
                var position = result.substring(result.indexOf("{") + 1, result.indexOf("}"));
                if($this.parents("tr").find("span[code="+ position +"]").size() > 0 ) {
                    value = $this.parents("tr").find("span[code="+ position +"]").text();
                }else if($this.parents("tr").find("[name="+ position +"]").size() > 0 ) {
                    value = $this.parents("tr").find("[name="+ position +"]").val();
                }else if($this.parents("form").size() > 0 )  {
                    value = $this.parents("form").find("[name="+ position +"]").val();
                }else if($("#" + position)){
                    value = $("#" + position).val();
                }
                result = result.replace(new RegExp("{" + position +"}", "gm"),value)
            }
            return result;
        }
        return null;
    }

    function getPageContextInfo(){
        $pageContextValues = $($("#breadcrumb").find("form")[0]).serialize();
        return $pageContextValues;
    }


    function showDialog(url, ok){
        layer.open({
            area: ['766px', '510px'],
            type: 2,
            fix: false, //不固定
            maxmin: true,
            //closeBtn: 1,
            content: url,
            success: function (l, i){
                l.find(".btn").on('click', function(){
                    layer.closeAll();
                    ok();
                    return true;
                });
                l.find('.hfconfirm-btn-cancel').on('click', function(){
                    layer.closeAll();
                    return false;
                    //location.reload();
                });
            }
        });
    }

    var _processBarIndex;
    var _processBarCount = 0;
    function showProcessBar(){
        if(_processBarCount++ == 0) {
            var _tpl = $('#processBar').html();
            _processBarIndex = layer.open({
                offset: '150px',
                area: ['510px', '12px'],
                type: 1,
                scrollbar: false,
                title: false,
                closeBtn: 0,
                content: _tpl
            });
            $(".layui-layer .layui-layer-content").css("height","auto");
            $(".layui-layer").css("background","#5bc0de");
        }

    }

    function hideProcessBar(){
        if(--_processBarCount == 0) {
            layer.close(_processBarIndex);
        }
        if(_processBarCount < 0) {
            _processBarCount = 0;
        }

    }

    function showConfirmDialog(msg, ok){
        var _tpl = $('#myModal').html();
        layer.open({
            area: ['510px', '170px'],
            type: 1,
            title: false,
            closeBtn: 0,
            content: _tpl,
            success: function (l, i){
                $('.hfconfirm-content').html(msg);
                $('.hfconfirm-btn-ok').on('click', function(){
                    layer.closeAll();
                    ok();
                    return true;
                });
                $('.hfconfirm-btn-cancel').on('click', function(){
                    layer.closeAll();
                    return false;
                    //location.reload();
                });
            }
        });
    }

    (function($){

        $.fn.serializeJson = function(){
            //针对于boolCheckBox进行input标签赋值，否则serializeArray将会出现混乱
            $(this).find(".boolCheckBox input[value=1]").each(function(){
                if($(this).is(':checked')){
                    $.uniform.update($(this).parents(".boolCheckBox:first").prev().find("input[value=0]").removeAttr("checked"));
                }else {
                    $.uniform.update($(this).parents(".boolCheckBox:first").prev().find("input[value=0]").attr("checked","true"));
                }
            });

            var blankModifyElementName ={};
            $(this).find("[data-code][data-condition][name]").each(function(){
                blankModifyElementName[($(this).attr("name"))] = null;
            });

            var jsonObject = {};
            var jsonArray = new Array();
            var serializeArray = this.serializeArray();
            // 先转换成{"id": ["12","14"], "name": ["aaa","bbb"], "pwd":["pwd1","pwd2"]}这种形式
            var $lastElement;
            $(serializeArray).each(function () {

                if(this.name == "EOFR_EDIT_FLAG") {
                    for(var name in blankModifyElementName) {
                        if(!(name in jsonObject)) {
                            jsonObject[name] = "";
                        }
                    }
                    jsonArray.push(jsonObject);
                    jsonObject = {};
                    $lastElement = null;
                }else {
                    if (jsonObject[this.name] != null) {

                        if($lastElement && ($lastElement.name == this.name)) {//复选框等含有多组值的元素
                            if ($.isArray(jsonObject[this.name])) {
                                jsonObject[this.name][jsonObject[this.name].length-1] = jsonObject[this.name][jsonObject[this.name].length-1] + "," +  this.value;
                            } else {
                                jsonObject[this.name] = jsonObject[this.name] + "," +  this.value;
                            }
                        }else {
                            if ($.isArray(jsonObject[this.name])) {
                                jsonObject[this.name].push(this.value);
                            } else {
                                jsonObject[this.name] = [jsonObject[this.name], this.value];
                            }
                        }
                    } else {
                        jsonObject[this.name] = this.value;
                    }
                    $lastElement = this;
                }
            });

            if(jsonArray && jsonArray.length > 0) {
                return JSON.stringify(jsonArray);
            }else {
                return "[" + JSON.stringify(jsonObject) + "]";
            }

            //// 再转成[{"id": "12", "name": "aaa", "pwd":"pwd1"},{"id": "14", "name": "bb", "pwd":"pwd2"}]的形式
            //var vCount = 0;
            //// 计算json内部的数组最大长度
            //for(var item in jsonData1){
            //    var tmp = $.isArray(jsonData1[item]) ? jsonData1[item].length : 1;
            //    vCount = (tmp > vCount) ? tmp : vCount;
            //}
            //
            //if(vCount > 1) {
            //    var jsonData2 = new Array();
            //    for(var i = 0; i < vCount; i++){
            //        var jsonObj = {};
            //        for(var item in jsonData1) {
            //            jsonObj[item] = jsonData1[item][i];
            //        }
            //        jsonData2.push(jsonObj);
            //    }
            //    return JSON.stringify(jsonData2);
            //}else{
            //    return "[" + JSON.stringify(jsonData1) + "]";
            //}
        };


        //$.fn.serializeJson = function(){
        //    var jsonData1 = {};
        //    var serializeArray = this.serializeArray();
        //    // 先转换成{"id": ["12","14"], "name": ["aaa","bbb"], "pwd":["pwd1","pwd2"]}这种形式
        //    $(serializeArray).each(function () {
        //        if (jsonData1[this.name] != null) {
        //            if ($.isArray(jsonData1[this.name])) {
        //                jsonData1[this.name].push(this.value);
        //            } else {
        //                jsonData1[this.name] = [jsonData1[this.name], this.value];
        //            }
        //        } else {
        //            jsonData1[this.name] = this.value;
        //        }
        //    });
        //    // 再转成[{"id": "12", "name": "aaa", "pwd":"pwd1"},{"id": "14", "name": "bb", "pwd":"pwd2"}]的形式
        //    var vCount = 0;
        //    // 计算json内部的数组最大长度
        //    for(var item in jsonData1){
        //        var tmp = $.isArray(jsonData1[item]) ? jsonData1[item].length : 1;
        //        vCount = (tmp > vCount) ? tmp : vCount;
        //    }
        //
        //    if(vCount > 1) {
        //        var jsonData2 = new Array();
        //        for(var i = 0; i < vCount; i++){
        //            var jsonObj = {};
        //            for(var item in jsonData1) {
        //                jsonObj[item] = jsonData1[item][i];
        //            }
        //            jsonData2.push(jsonObj);
        //        }
        //        return JSON.stringify(jsonData2);
        //    }else{
        //        return "[" + JSON.stringify(jsonData1) + "]";
        //    }
        //};
    })(jQuery);
});

