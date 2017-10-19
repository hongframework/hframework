require(['layer','ajax','js/hframework/errormsg'], function () {

    var layer = require('layer');
    var ajax = require('ajax');
    var errormsg = require('js/hframework/errormsg');

    $(".hflist-pager-button").live("click",function(){
        var pageNo = $(this).attr("pageNo");
        var compoContainer = $(this).parents("[page][module]")[0];
        RefreshList(pageNo,compoContainer);
    });


    $(".hflist select").live("change", function(){
        $this = $(this);
        valueChange($this);
    });
    $(".hfform select").live("change", function(){
        $this = $(this);
        valueChange($this);
    });
    $(".breadcrumb select:not(.hfselect)").live("change", function(){
        $this = $(this);
        valueChange($this);
    });


    $('.hfselect[action=""]').live('input', function(){
        $this = $(this);
        valueChange($this);
    });

    $('.hfselect:not([action])').live('input', function(){
        $this = $(this);
        valueChange($this);
    });

    $(".hflist thead th input[type=checkbox]").live('change', function(){
        var $this = $(this);
        if($this.is(":checked")) {
            $this.parents("table:first").find(".hflist-data").find("input[type=checkbox]").not(":checked").click();
        }else {
            $this.parents("table:first").find(".hflist-data").find("input[type=checkbox]:checked").click();
        }
    });


    function valueChange($this){

        var $code = $this.attr("name");
        var curValue =$this.val();

        var lastValue = $this.attr("last-value");
        //if(lastValue == undefined) {
        //    $this.attr("initing","true");
        //}
        if(lastValue == curValue) {
            return;
        }
        $this.attr("last-value", curValue);
        //alert($code + "=" + $value);
        var $allRules;
        if($this.parents(".hfform").size() > 0) {
            $allRules = JSON.parse($this.parents(".hfform:first").find(".ruler").text());
        }else if($this.parents("tr").size() > 0) {
            $allRules = JSON.parse($(".hflist .ruler:first").text());
        }else if($this.parents(".breadcrumb").size() > 0) {
            $allRules = JSON.parse($(".breadcrumb .ruler").text());
        }

        var $curRules = $allRules[$code + "=" + curValue];
        if(!$curRules) {
            $curRules = $allRules[$code];
        }
        doRulerEvent($this, $curRules, curValue);

        $allRules = JSON.parse($("#globalRuler").text());
        if(!$curRules) {
            $curRules = $allRules[$code + "=" + curValue];
            if(!$curRules) {
                $curRules = $allRules[$code];
            }
            doGlobalRulerEvent($this, $curRules, curValue);
        }


        var helperJson = $(" .helper:first").text();
        if(!helperJson) helperJson="{}";
        $allRules = JSON.parse(helperJson);
        $curRules = $allRules[$code];

        var $targetComponent = $("  .helper:first").parents("[component]")[0];
        doHelperEvent($allRules,$this, $curRules, curValue,$targetComponent);
    }

    function doHelperEvent(_allRules, $this, $curRules,$value,$targetComponent) {
        if(!$curRules || !$value) {
            return ;
        }


        var $targetCode = $curRules["targetCode"];
        var $editable = $curRules["editable"];
        RefreshListHelper(_allRules, 1,$targetComponent,$targetCode,$value);
        //for(var $index in $curRules) {
        //    var $rule = $curRules[$index];
        //    var $targetCode = $rule["targetCode"];
        //    var $editable = $rule["editable"];
        //    RefreshListHelper(1,$targetComponent,$targetCode,$value);
        //}

        return;
    }

    function doRulerEvent($this, $curRules,$value) {
        if(!$curRules || !$value) {
            return ;
        }
        if($curRules[0]["ruleType"] == "2") {
            var dataCode = $this.attr("data-code");
            var _url =  "/queryOne.json";
            var _data = {"dataCode":dataCode,"dataValue" : $value};
            ajax.Post(_url,_data,function(data){
                if(data.resultCode == 0) {
                    for(var $index in $curRules) {
                        var $rule = $curRules[$index];
                        var $targetCode = $rule["targetCode"];
                        var $targetValue = $rule["targetValue"];
                        var $editable = $rule["editable"];
                        var $target;
                        if($this.parents(".hfform").size() > 0) {
                            $target =  $($this.parents(".hfform")[0]).find("[name=" + $targetCode + "]");
                        }else if($this.parents(".breadcrumb").size() > 0) {
                            $target = getTargetElement($this.parents(".breadcrumb"), "[name=" + $targetCode + "]");
                        }else {
                            $target = getTargetElement($this.parents("tr"), "[name=" + $targetCode + "]");
                        }


                        $targetValue = $targetValue.substring($targetValue.indexOf("object.") + 7, $targetValue.length-1);
                        //console.info(data.data);
                        if(!$target.val()) {//��ֵ������Ž��и�ֵ
                            $target.val(data.data[$targetValue]);
                        }
                        if($editable == "false") {
                            //$target.attr("disabled",true);
                            $target.attr("readonly","readonly");
                        }
                    }
                }
            });
        }else if($curRules[0]["ruleType"] == "3") {
            for(var $index in $curRules) {
                var $rule = $curRules[$index];
                var $targetCode = $rule["targetCode"];
                var $target;
                if($this.parents(".hfform").size() > 0) {
                    $target =  $($this.parents(".hfform")[0]).find("[name=" + $targetCode + "]");
                }else if($this.parents(".breadcrumb").size() > 0) {
                    $target = getTargetElement($this.parents(".breadcrumb"), "[name=" + $targetCode + "]");
                }else {
                    $target = getTargetElement($this.parents("tr"), "[name=" + $targetCode + "]");
                }
                if($target[0].tagName == 'SELECT') {
                    $.selectLoad($target);
                }else if($($target[0]).hasClass("hfcheckbox") || $this.hasClass("hfradio")) {
                    $.checkboxOrRadioLoad($target);
                }

            }
        }else {
            for(var $index in $curRules) {
                //console.info($curRules[$index]);
                var $rule = $curRules[$index];
                var $targetCode = $rule["targetCode"];
                var $targetValue = $rule["targetValue"];
                var $editable = $rule["editable"];
                var $ruleType = $rule["ruleType"];

                var $target = getTargetElement($this.parents("tr"), "[name=" + $targetCode + "]");
                if($ruleType == "1") {
                    $target.val($targetValue);
                }
                if($editable == "false") {
                    //$target.attr("disabled",true);
                    $target.attr("readonly","readonly");
                }
            }
        }
    }

    function doGlobalRulerEvent($this, $curRules,$value) {
        if(!$curRules || !$value) {
            return ;
        }
        if($curRules[0]["ruleType"] == "3") {
            for(var $index in $curRules) {
                var $rule = $curRules[$index];
                var $targetCode = $rule["targetCode"];
                var $target = getTargetElement($("body"), "[name=" + $targetCode + "]");
                if($target[0].tagName == 'SELECT') {
                    $.selectLoad($target);
                }else if($($target[0]).hasClass("hfcheckbox") || $this.hasClass("hfradio")) {
                    $.checkboxOrRadioLoad($target);
                }
            }
        }
    }
    //$(".hflist select").each(function(){
    //    console.info($(this).html());
    //    $(this).change();
    //});

    function getTargetElement($parent, $selector) {
        return  $parent.find($selector);
        //if($parent.next()) {
        //    $target = $parent.next().find("[name=" + $targetCode + "]");
        //    if($target.size() == 0) {
        //        return getTargetElement($parent.next(), $selector)
        //    }
        //    return $target;
        //}
        //return null;
    }


    $(".hflist-tools-button").die().live("click",function(){
        $(this).toggleClass("badge-info");
        if(!$(this).hasClass("badge-info")) {
            $(this).css("opacity","0.3");
            $(this).find("i").attr("class","icon-minus-sign");

            var compareKey = $(".hflist-fast-data [compare-key]").eq(0).attr("id");
            var compareValue = $(this).attr("tag-id");

            var $curRow;

            $(".hflist-fast-data [id='" + compareKey + "']").each(function(){
                if($(this).val() == compareValue){
                    $curRow =$(this).parents("tr")[0];
                }
            });
            //$curRow =$(".hflist-fast-data [id='" + compareKey + "'][value='" + compareValue + "']").parents("tr")[0];

            var $newRow = $($curRow).clone();

            $($newRow).find("input[type=hidden]").val("");
            $newRow.find("input[value*='{'][value*='}']").each(function(){
                $(this).val(replaceVarChars($(this).val()));
            });
            $($curRow).find("select").each(function(i){
                $($newRow).find("select").eq(i).val($(this).val());
            });
            $(".hflist-data").append($newRow);
            $($newRow).find(".hfselectx").each(function(i){
                $(this).next().remove();
                $(this).show();
                $(this).chosen();//����Ϊselectx
            });

            $($newRow).find(".hfcheckbox input").uniform();

            if($(".hflist-data").children().length > 1) {
                var $firstRow = $(".hflist-data").children(":first");

                var hasInput = false;
                $firstRow.find("input[type!=checkbox][type!=hidden][value !='']").each(function () {
                    if($(this).val() && $(this).val().length > 0) {
                        hasInput  = true;
                    }
                });

                if(!hasInput && $firstRow.find("select option:checked[value!='']").size() == 0) {
                    $firstRow.remove();
                }
            }

            //
            //$($curRow).after($newRow);

        }else {
            $(this).css("opacity","1.0");
            $(this).find("i").attr("class","icon-plus-sign");
        }
    });

    function getVal($this) {
        if($this.val()) {
            return $this.val();
        }

        //select���ʼֵ
        return $this.attr("data-value");
    }

    function getText($this) {
        var $tagName = $this[0].tagName;
        if($tagName == 'SELECT') {
            return $this.find("option:checked").text();
        }
        return null;
    }

    function fastDataTagInit(){
        $(".hflist-fast-data").each(function(i){
            var $helper = $(this).parents(".hflist").find(".helper");
            if($helper) {
                var $allRules = JSON.parse($helper.text());
                for(var key in $allRules) {
                    var compareKey = $allRules[key]["compareKey"];
                    var compareName = $allRules[key]["compareName"];
                    var $this = $(this);
                    $(".hflist-tools").html("");

                    if($this.find("[id="+ compareKey + "]").size() == 0){
                        continue;
                    }
                    var $tagName = $this.find("[id="+ compareKey + "]")[0].tagName;
                    if($tagName == 'SELECT') {
                        $this.find("[id="+ compareKey + "]").each(function(){
                            $.selectLoad($(this),function(){
                                fastDataTagInitExe($this, compareKey, compareName);
                            }, false);
                        });
                    }else {
                        fastDataTagInitExe($this, compareKey, compareName);
                    }
                }
            }
        });

        function fastDataTagInitExe($this, compareKey, compareName){
            $this.find("[id="+ compareKey + "]").each(function(i){
                var tagValue =getVal($(this));
                $(this).attr("compare-key",true);

                var tagName = replaceVarChars(getVal($this.find("[id="+ compareName + "]").eq(i)));
                if(getText($this.find("[id="+ compareName + "]").eq(i))) {
                    tagName = getText($this.find("[id="+ compareName + "]").eq(i));
                }
                var isContain = false;
                $(".hflist-data").find("[id='"+ compareKey + "']").each(function(){
                    if(getVal($(this)) && (getVal($(this)).toUpperCase() == tagValue.toUpperCase() ||
                        getVal($(this)).toUpperCase() == replaceVarChars(tagValue).toUpperCase())) {
                        isContain = true;
                    }
                });
                var _html = [];
                _html.push('<div class="hflist-tools-button badge badge-info " tag-id="' + tagValue + '" style="position: relative;cursor:pointer;margin-left: 3px;"><i class="icon-plus-sign"></i>' +  tagName + '</div>');
                var $child = $(_html.join(''));
                $child.appendTo($(".hflist-tools"));
                $(".hflist-tools").css("display","");

                if(isContain) {
                    $(".hflist-tools-button[tag-id='" + tagValue + "']").toggleClass("badge-info");
                    $(".hflist-tools-button[tag-id='" + tagValue + "']").css("opacity","0.3");
                    $(".hflist-tools-button[tag-id='" + tagValue + "']").find("i").attr("class","icon-minus-sign");
                }
            });
        }
        //$(".hflist-fast-data").each(function(i){
        //    var id = $(this).find("input[id$='Id']").eq(0).attr("id");
        //    var name =$(this).find("input[id$='Name']").eq(0).attr("id");
        //    var code =$(this).find("input[id$='Code']").eq(0).attr("id");
        //    $this = $(this);
        //    $(".hflist-tools").html("");
        //    $this.find("input[id="+ id + "]").each(function(i){
        //        //alert($(this).val() + $this.find("input[id="+ name + "]").eq(i).val());
        //        var tagId = $(this).val();
        //        var tagName = $this.find("input[id="+ name + "]").eq(i).val();
        //        var tagValue = replaceVarChars($this.find("input[id="+ code + "]").eq(i).val());
        //        tagName = replaceVarChars(tagName);
        //        var isContain = false;
        //
        //        $(".hflist-data").find("input[id='"+ code + "']").each(function(){
        //            if($(this).val().toUpperCase() == tagValue.toUpperCase()) {
        //                isContain = true;
        //            }
        //        });
        //        //console.info(tagId,tagName,tagValue, isContain);
        //        var _html = [];
        //        _html.push('<div class="hflist-tools-button badge badge-info " tag-id="' + tagId + '" style="position: relative;cursor:pointer;margin-left: 3px;"><i class="icon-plus-sign"></i>' +  tagName + '</div>');
        //        var $child = $(_html.join(''));
        //        $child.appendTo($(".hflist-tools"));
        //        $(".hflist-tools").css("display","");
        //
        //        if(isContain) {
        //            $(".hflist-tools-button[tag-id='" + tagId + "']").toggleClass("badge-info");
        //            $(".hflist-tools-button[tag-id='" + tagId + "']").css("opacity","0.3");
        //            $(".hflist-tools-button[tag-id='" + tagId + "']").find("i").attr("class","icon-minus-sign");
        //        }
        //    });
        //});
    }
    fastDataTagInit();

    function replaceVarChars(tagValue) {
        if(tagValue && tagValue.indexOf("{") >= 0) {
            var var1 = tagValue.substring(tagValue.indexOf("{") + 1, tagValue.indexOf("}"));
            var varValue = $("#" + var1).val();
            if(varValue != "" && varValue.length > 0) {
                if(varValue.charAt(0) == varValue.charAt(0).toLowerCase()) {
                    tagValue = tagValue.replace("{" + var1 + "}",varValue).toLowerCase();
                }else {
                    tagValue = tagValue.replace("{" + var1 + "}",varValue).toUpperCase();
                }
            }else {
                tagValue = tagValue.replace("{" + var1 + "}",varValue);
            }

        }

        return tagValue;
    }


    var RefreshList = function(pageNo, compoContainer){
        var module = $(compoContainer).attr("module");
        var page =$(compoContainer).attr("page");
        var component  =$(compoContainer).attr("component");
        var param  =$(compoContainer).attr("param");
        var _url =  "/" + module + "/" + page + ".html";
        var _data = {"pageNo":pageNo,"component" : component};
        if(param) {
            var params =JSON.parse("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":").replace(new RegExp("&","gm"),",\"").replace(new RegExp(":,","gm"),":null,")  + "}");
            for (var key in params) {
                _data[key]=params[key];
            }
        }
        console.log(_data);
        //alert(_data);
        ajax.Post(_url,_data,function(data){
            var $newHfList = $(data);
            $(compoContainer).find(".hflist-pager").html($newHfList.find(".hflist-pager").html());
            $(compoContainer).find(".hflist-data").html($newHfList.find(".hflist-data").html());
            componentinit();
            $.reloadListDisplay();
            $.reloadDisplay(compoContainer);
        },'html');
    }

    var RefreshListHelper = function(_allRules, pageNo, compoContainer, $targetCode, $value){
        var module = $(compoContainer).attr("module");
        var page =$(compoContainer).attr("page");
        var component  =$(compoContainer).attr("component");
        var param  =$(compoContainer).attr("param");
        var _url =  "/" + module + "/" + page + ".html";
        var _data = {"pageNo":pageNo,"component" : component};
        if(param) {
            var params =JSON.parse("{\"" + (param + "&1=1").replace(new RegExp("=","gm"),"\":").replace(new RegExp("&","gm"),",\"").replace(new RegExp(":,","gm"),":null,")  + "}");
            for (var key in params) {
                _data[key]=params[key];
            }
        }
        _data[$targetCode]=$value;

        for(var key in _allRules) {
            var json = _allRules[key];
            if($targetCode != json["targetCode"]) {
                _data[json["targetCode"]]=$("#"  + key).val();
            }

        }
        console.log(_data);
        //alert(_data);
        ajax.Post(_url,_data,function(data){
            var $newHfList = $(data);
            //console.log(data);
            $(compoContainer).find(".hflist-fast-data").html($newHfList.find(".hflist-fast-data").html());
            $.reloadDisplay($(compoContainer).find(".hflist-fast-data"));
            fastDataTagInit();
        },'html');
    }

    return {
        RefreshList: RefreshList
    };
});

