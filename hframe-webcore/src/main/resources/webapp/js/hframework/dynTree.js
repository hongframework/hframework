
var DataSource = function(options) {
    this._data 	= options.data;
    this._delay = options.delay;
}

DataSource.prototype.data = function(options, callback) {
    var self = this;
    var $data = null;

    if(!("name" in options) && !("type" in options)){
        $data = this._data;//the root tree
        callback({ data: $data });
        return;
    }
    else if("type" in options && options.type == "folder") {
        if("additionalParameters" in options && "data" in options)
            $data = options.data;
        else $data = {}//no data
    }

    if($data != null) {//this setTimeout is only for mimicking some random delay
        if(true ||options.delay <= 0) {
            callback({ data: $data });
        }else {
            setTimeout(function(){callback({ data: $data });} , parseInt(Math.random() * 500) + 200);
        }
    }

};

//DataSource.prototype.data = function(options, callback) {
//    var self = this;
//    var $data = null;
//
//    if(!("name" in options)){
//        $data = this._data;//the root tree
//        callback({ data: $data });
//        return;
//    }
//    else if("name" in options) {
//        if("children" in options)
//            $data = options.children;
//        else $data = {}//no data
//    }
//
//    if($data != null)//this setTimeout is only for mimicking some random delay
//        setTimeout(function(){callback({ data: $data });} , parseInt(Math.random() * 500) + 200);
//};

//
//data: [
//    {
//        "id": "6",
//        "icon": " icon-sitemap",
//        "url": "/hframe/hfsec_menu_mgr.html",
//        "name": "菜单管理"
//    },
//    {
//        "id": "15",
//        "children": [
//            {
//                "id": "16",
//                "icon": "icon-legal",
//                "url": "/hframe/hfpm_program_mgr.html",
//                "name": "实体规则配置"
//            },
//            {
//                "id": "17",
//                "icon": "icon-legal",
//                "url": "/hframe/hfpm_program_mgr.html",
//                "name": "数据集助手"
//            }
//        ],
//        "icon": "icon-signal",
//        "url": "/hframe/hfpm_program_mgr.html",
//        "name": "高级配置"
//    }
//]



function initJson(json){
    if("" == json) {
        return "{}";
    }
    return json;
}

var treeDataSource = new DataSource({
    data: transferDataToIceTreeData(JSON.parse(initJson($("#dyn-tree-data").text()))),
    delay: 0
});

function transferDataToIceTreeData(origData){
    if(origData == null) {
        return null;
    }
    var result=[];
    for(var index in origData) {
        var orig = origData[index];
        var iconClass;
        var type = "item";
        var name=orig.name;
        if(orig.children != null && orig.children.length > 0) {
            type = "folder";
            iconClass = "blue";
            //name=orig.name + "<div style='float:right;'><a href='javascript:alert(1)'><i class='icon-file-alt blue'></i></a><div>";
        }else {
            name = '<i class="icon-file-text blue"></i>' + name;// + "<div style='float:right;'><a href='javascript:alert(1)'><i class='icon-file-alt blue'></i></a><div>";
        }

        if(!orig.data) {
            orig.data = {}
        }
        orig.data["id"] = orig.id;

        result.push({name : name,type : type, additionalParameters :orig.data,data: transferDataToIceTreeData(orig.children),'icon-class':iconClass});
    }
    return result;
}
//
//var treeDataSource = new DataSource({
//    data: [
//        { name: 'Test Folder 1', type: 'folder', additionalParameters: { id: 'F1' },
//            data: [
//                { name: 'Test Sub Folder 1', type: 'folder', additionalParameters: { id: 'FF1' } },
//                { name: 'Test Sub Folder 2', type: 'folder', additionalParameters: { id: 'FF2' } },
//                { name: 'Test Item 2 in Folder 1', type: 'item', additionalParameters: { id: 'FI2' } }
//            ]
//        },
//        { name: 'Test Folder 2', type: 'folder', additionalParameters: { id: 'F2' } },
//        { name: 'Test Item 1', type: 'item', additionalParameters: { id: 'I1' } },
//        { name: 'Test Item 2', type: 'item', additionalParameters: { id: 'I2' } }
//    ],
//    delay: 400
//});

//$('#tree1').ace_tree({
//    dataSource: treeDataSource ,
//    multiSelect:true,
//    loadingHTML:'<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
//    'open-icon' : 'icon-minus',
//    'close-icon' : 'icon-plus',
//    'selectable' : true,
//    'selected-icon' : 'icon-ok',
//    'unselected-icon' : 'icon-remove'
//});

$('.tree').ace_tree({
    dataSource: treeDataSource ,
    loadingHTML:'<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
    'open-icon' : 'icon-folder-open',
    'close-icon' : 'icon-folder-close',
    "icon" : 'icon-folder-open',
    'selectable' : true,
    'selected-icon' : null,
    'unselected-icon' : null
});

$('.tree .tree-item').live("mouseover", function(){
    $(this).find(".dyn-tree-oper").show();
});

$('.tree .tree-item').live("mouseout", function(){
    $(this).find(".dyn-tree-oper").hide();
});
//
//$('.tree .tree-item').live("click", function(){
//    $(this).find(".dyn-tree-oper").hide();
//});

$('.tree .tree-folder-header').live("mouseover", function(){
    $(this).find(".dyn-tree-oper").show();
});

$('.tree .tree-folder-header').live("mouseout", function(){
    $(this).find(".dyn-tree-oper").hide();
});


//$('.tree').find(".tree-folder-header").each(function(){
//   $(this).click();
//});