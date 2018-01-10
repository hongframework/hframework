var unit = {"MONEY_UNIT": {"" : "元","* 1000":"千","* 10000":"万"},
    "DATE_UNIT":  {"* hour()": "小时","* day()": "天","* week()":"星期","* month()":"个月"},
};

var compare_symbol = {
    "BOOLEAN":{"==":"等于",">":"大于","<":"小于","!=":"不等于",">=":"大于等于","<=":"小于等于"},
    "FUTURE-DATE":  {"> now() +":"在{}之后","> now() -":"在{}之内"},
    "PASS-DATE":  {"< now() -" : "在{}之前","> now() -":"在{}之内"}
}

$("a[edit-json]").each(function(){
    var $this = $(this);
    var editJson = $this.attr("edit-json");
    var editJsonObject = JSON.parse(editJson);
    var varInfo = editJsonObject["var"];
    var valueInfo = editJsonObject["value"];
    var varTitle = varInfo[0];
    var varName = varInfo[1];
    var symbol = editJsonObject["symbol"];
    var valueType = valueInfo[0];
    var valueUnit = valueInfo[1];

    var $parentDiv = $(
        '<div style="position: relative;display: inline">' +
        '<div class="city-picker-dropdown" style="position: absolute;display: none;left: 0;top: 100%;z-index: 9999;background-color: #fff;width: 500px">' +
        '<div class="city-select-wrap">' +
        '<div style="padding: 10px;" class = "rule-edit-area">' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>');
    $this.after($parentDiv);
    $parentDiv.find(".city-picker-dropdown").before($this);
    var $editArea = $parentDiv.find(".rule-edit-area");
    $('<span class="rule-edit-title rule-express-desc-part">' + varTitle + '</span>').appendTo($editArea);
    $('<span class="rule-edit-hidden rule-express-part"  var-name="' + varName+ '">' + varName + '</span>：').appendTo($editArea);

    var compareSymbol = compare_symbol[symbol];

    var $compare = $('<select class=" rule-express-part rule-express-desc-part rule-compare-symbol"></select>');
    var preChars = null;
    for(var key in compareSymbol){
        var title = compareSymbol[key];
        if(title.indexOf("{}") > -1) {
            preChars = title.substring(0, title.indexOf("{}"));
            $('<option value="' + key + '">' + title.substring(title.indexOf("{}") + 2) + '</option>').appendTo($compare);
        }else {
            $('<option value="' + key + '">' + title + '</option>').appendTo($compare);
        }
    }
    if(preChars){
        $('<span class="rule-edit-title rule-express-desc-part">' + preChars + '</span>').appendTo($editArea);
    }else {
        $compare.appendTo($editArea);
    }

    if(valueType == "NUMBER"){
        $('<input type="number" class=" rule-express-part rule-express-desc-part" style="min-width:50px; width:50px;text-align: center" value="1"/>').appendTo($editArea);
    }else{
        $('<input class=" rule-express-part rule-express-desc-part" style="min-width:50px; width:50px;text-align: center"/>').appendTo($editArea);
    }

    if(unit[valueUnit]) {
        var $unit = $('<select class=" rule-express-part rule-express-desc-part"></select>');
        for(var key in unit[valueUnit]){
            $('<option value="' + key + '">' + unit[valueUnit][key] + '</option>').appendTo($unit);
        }
        $unit.appendTo($editArea);
    }else {
        $('<span  class="rule-edit-title rule-express-desc-part">' + valueUnit + '</span>').appendTo($editArea);
        $('<span class="rule-edit-hidden rule-express-part"></span>').appendTo($editArea);
    }

    if(preChars){
        $compare.appendTo($editArea);
    }

    $('<button  class="rule-edit-btn rule-edit-ok">确定</button>').appendTo($editArea);
    $('<button  class="rule-edit-btn  rule-edit-cancel">取消</button>').appendTo($editArea);
//        alert(varTitle + varName + symbol + valueType + valueUnit)
});

$(".city-picker-dropdown .city-select-wrap a").on("click", function(){
    if($(this).next().is("div")){
        if($(this).next().is(":hidden")){
            $(".city-picker-dropdown .city-select-wrap dd .city-picker-dropdown").hide();
        }
        $(this).next().toggle();
    }
});

$(".rule-edit-cancel").on("click", function () {
    $(this).parents(".city-picker-dropdown:first").hide();
});

var expressDescArray = [[]];
var expressArray = [[]];

$(".rule-edit-ok").on("click", function () {
    var $ruleEditDiv = $(this).parents(":first");
    var expressParts = [];
    var expressDescParts = [];
    var lastIsCompareSymbol = false;

    $ruleEditDiv.find(".rule-express-part").each(function(){
        var $this = $(this);
        if($this.is("SELECT")) {
            expressParts.push($this.val());
        }else if($this.is("INPUT")) {
            expressParts.push($this.val());
        }else  if($this.is("span")){
            expressParts.push($this.html());
        }
        lastIsCompareSymbol = $this.is(".rule-compare-symbol");
    });

    if(lastIsCompareSymbol) {
        expressParts = [expressParts[0], expressParts[expressParts.length - 1]].concat(expressParts.slice(1,expressParts.length-1))
    }

    $ruleEditDiv.find(".rule-express-desc-part").each(function(){
        var $this = $(this);
        if($this.is("SELECT")) {
            expressDescParts.push($this.children("option:checked").html())
        }else if($this.is("INPUT")) {
            expressDescParts.push($this.val())
        }else  if($this.is("span")){
            expressDescParts.push($this.html())
        }
    });
//        alert(expressDescParts.join(" ") + "\n" + expressParts.join(" "));
    $(this).parents(".city-picker-dropdown:first").hide();

    var expressDesc = expressDescParts.join(" ").trim();
    var express = expressParts.join(" ").trim();

    var ifThenBoxIndex = $(".chosen-container.chosen-container-multi.cur-chosen-container")
        .prevAll(".chosen-container.chosen-container-multi").length;

    if(expressDescArray.length < ifThenBoxIndex + 1){
        expressDescArray.push([expressDesc]);
        expressArray.push([express]);
    }else {
        expressDescArray[ifThenBoxIndex].push(expressDesc);
        expressArray[ifThenBoxIndex].push(express);
    }

    refreshReview();
    addEditItem(expressDesc);

});


function addEditItem(expressDesc){
    var $search_container = $(".chosen-container.chosen-container-multi.cur-chosen-container .chosen-choices .search-field");
    var $choice = $('<li class="search-choice"><span>' + expressDesc + '</span></li>');
    var $close_link = $('<a />', {
        "class": 'search-choice-close',
        'data-option-array-index': 6
    });
    $close_link.on('click.chosen', (function(evt) {
        evt.preventDefault();
        evt.stopPropagation();
        var ifThenBoxIndex = $(evt.target).parents(".chosen-container.chosen-container-multi:first")
            .prevAll(".chosen-container.chosen-container-multi").length;
        var expressIndex = $(evt.target).parents("li").first().prevAll("li.search-choice").length;
        expressDescArray[ifThenBoxIndex] = expressDescArray[ifThenBoxIndex].slice(0,expressIndex).concat(expressDescArray[ifThenBoxIndex].slice(expressIndex + 1))
        expressArray[ifThenBoxIndex] = expressArray[ifThenBoxIndex].slice(0,expressIndex).concat(expressArray[ifThenBoxIndex].slice(expressIndex + 1))
        refreshReview();
        $(evt.target).parents('li').first().remove();
    }));
    $choice.append($close_link);
    $search_container.before($choice)
}

function refreshReview(){
    var _desc = "";
    var _express = ""
    for(var index in expressDescArray) {
        var tmpExpressDesc = expressDescArray[index].join("<code><span style='color: red'> AND </span></code>");
        var tmpExpress = expressArray[index].join("<code><span style='color: red'> && </span></code>");
        if(index == 0) {
            _desc += ("<span style='color: red'>当 ：</span>" + tmpExpressDesc + "<span style='color: red'> ：</span>");
            _express += ("if(" + tmpExpress + "):");
        }else if(index == expressDescArray.length - 2) {
            _desc +="<span style='color: red'>其他 ：</span>";
            _express += "else:";
        }else if(index % 2 == 0) {
            _desc +=("<span style='color: red'>当 ：</span>"+ tmpExpressDesc + "<span style='color: red'> ：</span>");
            _express += ("elseif(" + tmpExpress + "):");
        }else {
            _desc +="     ["+ tmpExpressDesc + "]";
            _express += "     ["+ tmpExpress + "]";
        }
        _desc +="\n";
        _express += "\n";

    }

    $("#express-readable").html(_desc);
    $("#express").html(_express);
}

$(".chosen-container.chosen-container-multi").live("click", function(){
    if(!$(this).is(".else-container")) {
        $(".chosen-container.chosen-container-multi.cur-chosen-container").removeClass("cur-chosen-container");
        $(this).addClass("cur-chosen-container");
    }
});
$(".input-block-add").live("click", function(){
    var $ifInput = $("#add-if-template").children(":first").clone();
    var $thenInput = $("#add-then-template").children(":first").clone();
    var $elseInput = $("#add-else-template").children(":first").clone();
    if($(".chosen-container.chosen-container-multi:visible").length == 1) {
        $("#first-if-label").css("visibility", "visible");
        $(".chosen-container.chosen-container-multi:visible:last").after($thenInput);
        $(".chosen-container.chosen-container-multi:visible:last").after($elseInput);
        $(".chosen-container.chosen-container-multi:visible:last").after($thenInput.clone());
        expressDescArray.push([],[],[]);
        expressArray.push([],[],[]);
    }else{
        $(".chosen-container.chosen-container-multi.else-container:visible:last").before($ifInput);
        $(".chosen-container.chosen-container-multi.else-container:visible:last").before($thenInput);

        expressDescArray = expressDescArray.slice(0, expressDescArray.length - 2).concat([[],[]]).concat(expressDescArray.slice(expressDescArray.length - 2))
        expressArray = expressArray.slice(0, expressArray.length - 2).concat([[],[]]).concat(expressArray.slice(expressArray.length - 2))
    }
});

$("#submitBtn").on("click", function(){
    var id = $("#objectId").val();
    var value = $("#express").html().replace(/<code><span style="color: red"> &amp;&amp; <\/span><\/code>/g," && ").replace(/&lt;/g,"<").replace(/&gt;/g,">");
    $.post("/ajaxSubmit.json",{id:id, value:value},function(data){
        if(data.resultCode != '0') {
            alert(data.resultMessage);
            return;
        }
        location.reload();
    });
});

var express = $("#express").html().replace(/<code><span style="color: red"> &amp;&amp; <\/span><\/code>/g," && ").replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&amp;/g,"&");
var expressRows = express.split("\n");
for(var i in expressRows){
    var expressRow = expressRows[i].trim();
    if(!expressRow) {
        continue;
    }
    if($(".chosen-container.chosen-container-multi:visible").length < parseInt(i) + 1) {
        $(".input-block-add").click();
    }
    $(".chosen-container.chosen-container-multi:visible").eq(i).click();

    if(expressRow.startsWith("if(")) {
        expressRow = expressRow.substring(3,expressRow.length - 2);
    }else if(expressRow.startsWith("elseif(")){
        expressRow = expressRow.substring(7,expressRow.length - 2);
    }else if(expressRow.startsWith("else:")){
        continue;
    }else{
        expressRow = expressRow.substring(1,expressRow.length - 1);
    }

    var expresses = expressRow.split("&&");
    for(var j in expresses) {
        var aExpress = expresses[j].trim();
        if(!aExpress) {
            continue;
        }
        var expressDesc = getDescription(aExpress);
        console.info(expressDesc);
        expressDescArray[i].push(expressDesc);
        expressArray[i].push(aExpress);
        addEditItem(expressDesc);
    }
}
refreshReview();

function getDescription(aExpress){
    var varName = aExpress.match(/\$[a-zA-Z0-9_]*/g)
    var $varNameSpan = $("div.city-picker-dropdown div.city-select dd div .rule-edit-area span[var-name = '" + varName + "']")
    var editJson = $varNameSpan.parents("div.city-picker-dropdown:first").prev("a[edit-json]").attr("edit-json");
    var editJsonObject = JSON.parse(editJson);
    var varInfo = editJsonObject["var"];
    var valueInfo = editJsonObject["value"];
    var varTitle = varInfo[0];
    var varName = varInfo[1];
    var symbol = editJsonObject["symbol"];
    var valueType = valueInfo[0];
    var valueUnit = valueInfo[1];
    var compareSymbol = compare_symbol[symbol];
    var unitOptions = unit[valueUnit];

    var unKnownExpress =  aExpress.substring(varName.length).trim();
    var compareSymbolKey = "";
    var compareSymbolName = "";
    for(var key in compareSymbol) {
        if(unKnownExpress.startsWith(key) && key.length >= compareSymbolKey.length) {
            compareSymbolKey = key;
            compareSymbolName = compareSymbol[key];
        }
    }

    var unitSymbolKey = "";
    var unitSymbolName = "";
    if(unitOptions){
        for(var key in unitOptions) {
            if(unKnownExpress.endsWith(key) && key.length >= unitSymbolKey.length) {
                unitSymbolKey = key;
                unitSymbolName = unitOptions[key];
            }
        }
    }else{
        unitSymbolName =  valueUnit;
    }


    var value = unKnownExpress.substring(compareSymbolKey.length, unKnownExpress.length - unitSymbolKey.length).trim()

    var expressDesc;

    if(compareSymbolName.indexOf("{}") > -1) {
        expressDesc = varTitle + " " + compareSymbolName.replace("{}", " " + value + " " + unitSymbolName + " ");
    }else {
        expressDesc = varTitle + " " + compareSymbolName + " " + value + " " + unitSymbolName;
    }
    return expressDesc;
}