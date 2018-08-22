if($("#pageTemplate") && $("#pageTemplate").val() == "allInOne") {
    $("#content .box .box-header").each(function(i){
        var $head = $(this);
        var $content = $(this).nextAll("div.box-content:first");
        var title = $head.find("h2").html();
        title = title.substr(title.indexOf("</i>") + 4)

        $legend = $("<legend><h2><i class='icon-th-list'></i> " + title + "</h2></legend>");

        if(i != 0) {
            $head.remove();
        }else {
            $head.find("h2").html($head.find("h2").html().substring(0, $head.find("h2").html().indexOf("</i>") + 4) + $("#pageTitle").val());
        }

        $content.children(":first").before($legend)
        $content.parent().css("margin-bottom","0px");
        $content.children("form").css("margin-bottom","0px");
    });
    $(".row-fluid:last button").css("margin-left", "180px")
}


function flatContainerStyleReset(_$flatContainer) {
    _$flatContainer.find(".box").removeClass("box");
    _$flatContainer.find(".box-content").removeClass("box-content");
    _$flatContainer.find(".box-header  .box-icon").html("");
    _$flatContainer.find(".box-header").removeClass("box-header");


    _$flatContainer.find("div:first form fieldset .control-group ").addClass("span1");
    _$flatContainer.find("div:first form fieldset .control-group:first ").removeClass("span1").addClass("span4");

    _$flatContainer.find("div:first .hfform h2").remove();

    _$flatContainer.find("div:first  .form-horizontal div:last").append('<button class="btn btn-danger flat-remove-btn" onclick="javascript:void(0)" title="删除">删除</button>');

    _$flatContainer.find(".pagination").remove();

    _$flatContainer.find("div:first form fieldset").each(function(){
        $(this).find(".control-group").addClass("span2").removeClass("span1");
        $(this).find(".control-group:first ").addClass("span3").removeClass("span4").removeClass("span2");
        $(this).find(".control-group:last ").addClass("span3").removeClass("span1").removeClass("span2");
        $(this).find("input.input-xlarge").addClass("input-medium").removeClass("input-xlarge");
        $(this).find("span.help-inline").remove();
    });

    _$flatContainer.find(".switch-button").each(function(){
        dispalySwitchButton($(this));
    });
}

function dispalySwitchButton(_$switchButton) {
    var $this = _$switchButton;

    var config = JSON.parse($this.attr("action"))["componentControl"];
    var targetId = config["targetId"];
    var $targetComponent = $this.parents(".hfcontainer:first").find("div[dc='" + targetId + "']:first");
    if(!$targetComponent[0]){
        var seq = 0;
        if(targetId.endsWith("]")){
            seq = targetId .substring(targetId.length - 2,targetId.length - 1) - 1;
            targetId = targetId.substring(0,targetId.length - 3);

        }
        $targetComponent = $($("[component= '" + targetId + "']").get(seq));
    }
    var isShow = true;
    if(config["param"]) {
        var visible = JSON.parse(config["param"])["visible"];
        var showCondition = JSON.parse(config["param"])["show_condition"];
        var event = JSON.parse(config["param"])["event"];
        var showWhenIsNotEmpty = showCondition == "IS_NOT_EMPTY";

        if(visible == "auto" && showWhenIsNotEmpty) {
            if($targetComponent.children().is("[component=flatContainer]")){
                isShow = false;
                var input = $targetComponent.find("div[dc='" + targetId + "']:first form").serialize()
                var items = input.split("&")
                for(var i in items){
                    var value = items[i].split("=")[1];
                    if(value){
                        isShow = true;
                    }
                }
            }else if($targetComponent.find(".form-horizontal table:first tbody tr").size() == 1 && $targetComponent.find(".form-horizontal table:first tbody").is("[data-is-empty=true]")){
                isShow = false;
            }else if($targetComponent.find("[data-is-empty=true]").size() == 1){
                isShow = false;
            }
        }
    }
    if(isShow){
        if(event == "toggle"){
            $this.addClass("switch-hidden");
        }

    }else {
        $targetComponent.hide();
    }
}

function componentinit(){
    $("[component=flatContainer]").find(".box").removeClass("box");
    $(".hfcontainer[component='flatContainer']").each(function(){
        flatContainerStyleReset($(this));
    });

    $(".flat-remove-btn").live("click", function(){
        $(this).parents("div.hfcontainer:first").parent().remove();
    });


    $(".hfmutexcont .box-content .box-header .box-icon").html("");
    $(".hfmutexcont .box-content .box-header").removeClass("box-header");

    $(".hftab .box-content .nav-tabs").each(function(){
        $(this).find("li:first").addClass("active");
    });
    $(".hftab .box-content .tab-content").each(function(){
        $(this).find("div:first").addClass("active");
    });

    $(".hftab  .box-content .nav-tabs a").click(function(b) {
        b.preventDefault();
        $(this).tab("show");
    });
    $(".hfcontainer .pagination ").remove();

    $(".hflist  .box-content .hflist-data a[when][when!='{}']").each(function(){
        $(this).hide();
        var conditions = JSON.parse( $(this).attr("when"));
        for(var key in conditions) {
            var $span = $(this).parent("td").parent("tr").find("span[code='"+ key +"']");
            var value = conditions[key];
            if("NotBlank" == value && ($span.attr("value") || $span.text())) {
                $(this).show();
            }else if($span.attr("value") == value || $span.text() == value) {
                $(this).show();
            }

        }
    });

    $(".switch-button").each(function(){
        dispalySwitchButton($(this));
    });

    $("div[group][group!='']").each(function(index,element){
        if(index != 0) {
            $(this).hide();
        }
    });

    $(".hflist  .box-content .hflist-data tr td").each(function(){
        var count = 0;
        var $this = $(this);
        $(this).find("a").each(function(){
            if( $(this).attr("when") &&  $(this).attr("when") !="{}") {
                var conditions = JSON.parse( $(this).attr("when"));
                for(var key in conditions) {
                    var $span = $(this).parent("td").parent("tr").find("span[code='"+ key +"']");
                    var value = conditions[key];
                    if($span.attr("value") == value || $span.text() == value) {
                        count = count + 1;
                    }
                }
            }else {
                count = count + 1;
            }
        });
        if(count > 3) {
            $($this).attr("width",count*50 + "px;");
        }
    });

    initRelatComponent();

    //$( ".datepicker" ).datepicker({
    //    dateFormat: "yy-mm-dd"
    //});

    $( ".datepicker" ).datetimepicker({
        showSecond: true,
        showMillisec: false,
        timeFormat: 'HH:mm:ss',
        dateFormat: "yy-mm-dd"
    });


    $(".hfTreeList").each(function(){
        var $tree = $(this).find(".tree");
        //$(".tree-folder-header").click();
        var $templateTable = $(this).find($("table"));
        var $templateTableRow =  $templateTable.find(".hflist-data tr:first").clone();

        $templateTableRow.children("td:first").find("select,input").each(function(){
            $templateTableRow.children("td:first").append($("<input name='" + $(this).attr("name") + "' id='" + $(this).attr("id") + "' type='hidden' />"))
            $(this).remove();
        });

        var $table = $templateTable.clone();
        $table.children(".hflist-data").empty();
        $tree.prepend($table);

        var $treeItems = $tree.children("div:visible");
        recursionNestTable($tree, $table, $treeItems, $templateTable, $templateTableRow, 0);
        $templateTable.hide();

        $(this).find('input.switch-checkbox[type="checkbox"], input.switch-radio[type="radio"]').not(".switch-checkbox-not-init")
            .bootstrapSwitch();
        ////样式冲突
        //$(this).find(".bootstrap-switch-container .checker").css("width", "0px");
        //$(this).find(".bootstrap-switch-container .checker").css("margin-right", "0px");

    });


    function initRelatComponent() {
        var $firstForm = $(".hfform:first");
        if(!$firstForm || $firstForm.length == 0) return;
        var firstFormId = $firstForm.attr("id");
        var mainEntityCode = firstFormId.substring(0, firstFormId.length - "QueryForm".length);
        var $allLikeRelatField = $("select[data-code*='" + mainEntityCode   + ".']");
        $allLikeRelatField.each(function(){
            var $this = $(this);
            var dateCode = $this.attr("data-code");
            if(dateCode.split(".").length == 3 && $this.parents(".hfform:first") != $firstForm){
                if($this.parent().is("td")){
                    var index = $this.parent().prevAll("td").size();
                    $this.parents("table:first").find("thead tr th").eq(index).hide();
                    $this.parent().hide();
                }else {
                    $this.parents(".control-group:first").hide();
                }
            }
            $this.removeAttr("not-null");
        });
    }


    /**
     * 循环递归 嵌套table
     * @param $table
     * @param $treeItems
     * @param $templateTable
     * @param $templateTableRow
     */
    function recursionNestTable(_$tree, _$table, _$treeItems, _$templateTable, _$templateTableRow, deepIndex){
        _$treeItems.each(function(){
            var $newRow = _$templateTableRow.clone();
            var $contentDiv = $("<div class='tree-folder-content' style='position:relative;margin-left: " + deepIndex * 23 + "px;'></div>");
            var data = $(this).data()["additionalParameters"];
            if($(this).hasClass("tree-folder")) {
                data = $(this).children().data()["additionalParameters"];
            }
            //var data = _$tree.tree('getAdditionalParameters', $(this));
            $contentDiv.append($(this));
            //$newRow.children("td:first").empty();
            $newRow.find("select,input").each(function(){
                if(data[$(this).attr("name")]) {
                    if($(this).is("select")){
                        $(this).attr("data-value", data[$(this).attr("name")]);
                    }else {
                        if($(this).is("[type=checkbox]") || $(this).is("[type=radio]")) {
                            $(this).parents("label.hfcheckbox").attr("data-value", data[$(this).attr("name")]);
                        }else {
                            $(this).val(data[$(this).attr("name")]);
                        }
                    }
                }
            });
            $newRow.children("td:first").append($contentDiv);
            _$table.children(".hflist-data").append($newRow);
            if($(this).hasClass("tree-folder")) {
                var $subTreeItems = $(this).children(".tree-folder-content").children("div:visible");
                recursionNestTable(_$tree, _$table, $subTreeItems, _$templateTable, _$templateTableRow, deepIndex + 1)
                $(this).remove(".tree-folder-content");
            }
        });
    }

    ///**
    // * 循环递归 嵌套table
    // * @param $table
    // * @param $treeItems
    // * @param $templateTable
    // * @param $templateTableRow
    // */
    //function recursionNestTable(_$table, _$treeItems, _$templateTable, _$templateTableRow){
    //    _$treeItems.each(function(){
    //        var $newRow = _$templateTableRow.clone();
    //        $newRow.children(":first").append($(this));
    //        if($(this).hasClass("tree-folder")) {
    //            var $subTable = _$templateTable.clone();
    //            $subTable.children(".hflist-data").empty();
    //            $subTable.children(".hflist-data").append($newRow);
    //            $subTable.children("thead").hide();
    //            var $container = $("<tr><td colspan='100%' style='border: 0px;padding: 0px'></td></tr>");
    //            $container.children(":first").append($subTable)
    //            _$table.children(".hflist-data").append($container);
    //            var $subTreeItems = $(this).children(".tree-folder-content").children("div:visible");
    //            recursionNestTable($subTable, $subTreeItems, _$templateTable, _$templateTableRow)
    //        }else {
    //            _$table.children(".hflist-data").append($newRow);
    //        }
    //    });
    //}

    ///**
    // * 循环递归 嵌套table
    // * @param $table
    // * @param $treeItems
    // * @param $templateTable
    // * @param $templateTableRow
    // */
    //function recursionNestTable(_$table, _$treeItems, _$templateTable, _$templateTableRow){
    //    _$treeItems.each(function(){
    //        var $newRow = _$templateTableRow.clone();
    //        $newRow.children(":first").append($(this));
    //        if($(this).hasClass("tree-folder")) {
    //            var $subTable = _$templateTable.clone();
    //            $subTable.children(".hflist-data").empty();
    //            $subTable.children("thead").hide();
    //            var $container = $("<tr><td colspan='100%' style='border: 0px;padding: 0px'></td></tr>");
    //            $container.children(":first").append($(this));
    //            $(this).children(".tree-folder-content").append($subTable);
    //            //$container.children(":first").append($subTable)
    //            _$table.children(".hflist-data").append($container);
    //            var $subTreeItems = $(this).children(".tree-folder-content").children("div:visible");
    //            recursionNestTable($subTable, $subTreeItems, _$templateTable, _$templateTableRow)
    //        }else {
    //            _$table.children(".hflist-data").append($newRow);
    //        }
    //    });
    //}



}

$.datepicker.regional['ru'] = {
    closeText: '1',
    prevText: 2,
    nextText: 'След&#x3e;',
    currentText: 'Сегодня',
    monthNames: ['一月','二月','三月','四月','五月','六月',
        '七月','八月','九月','十月','十一月','十二月'],
    monthNamesShort: ['Янв','Фев','Мар','Апр','Май','Июн',
        'Июл','Авг','Сен','Окт','Ноя','Дек'],
    dayNames: ['воскресенье','понедельник','вторник','среда','четверг','пятница','суббота'],
    dayNamesShort: ['вск','пнд','втр','срд','чтв','птн','сбт'],
    dayNamesMin: ['日','一','二','三','四','五','六'],
    weekHeader: 'Не',
    dateFormat: 'yy-mm-dd',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: ''
};
$.datepicker.setDefaults($.datepicker.regional['ru']);

$.timepicker.regional['ru'] = {
    timeOnlyTitle: '1',
    timeText: '时间',
    hourText: '时',
    minuteText: '分',
    secondText: '秒',
    millisecText: '毫秒',
    timezoneText: '时区',
    currentText: '当前时间',
    closeText: '确定',
    timeFormat: 'HH:mm',
    amNames: ['AM', 'A'],
    pmNames: ['PM', 'P'],
    isRTL: false
};

$.timepicker.setDefaults($.timepicker.regional['ru']);
componentinit();

