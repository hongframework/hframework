require(['layer','ajax','js/hframework/errormsg','/static/plugins/echarts/echarts.common.min.js'], function () {
    var layer = require('layer');
    var ajax = require('ajax');

    Date.prototype.Format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    $(".hfchart .hfchart-context").each(function(){
        // 基于准备好的dom，初始化echarts实例
        var $this = $(this);
        var hfChart = echarts.init($this[0]);

        var legendData = $this.attr("legend-data").split(",");
        var xAxisData = eval($this.attr("xAxis-data"));
        var interval = $this.attr("interval");
        var cycle = $this.attr("cycle");
        var xAxisType = $this.attr("xAxis-type");
        if(!xAxisType) xAxisType = "time";


        refreshSeries($this, "init" , function(series){
            var toolBoxFeature = getToolBox($this, hfChart);
            // 指定图表的配置项和数据,并生效
            hfChart.setOption({
                //title: {
                //    text: $this.attr("title")
                //},
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {
                    show: true,
                    left :'60%',
                    feature:toolBoxFeature
                },
                dataZoom: [
                    //{
                    //    show: true,
                    //    realtime: true,
                    //    start: 70,
                    //    end: 100
                    //},
                    {
                        type: 'inside',
                        realtime: true,
                        start: 70,
                        end: 100
                    }
                ],
                legend: {
                    left :"10%",
                    data:legendData
                },
                xAxis: {
                    type:xAxisType,
                    axisLabel:{
                        // 使用函数模板，函数参数分别为刻度数值（类目），刻度的索引
                        formatter:function (value, index) {
                            return dateFormat(value,index, cycle);
                        }
                    },
                    data: xAxisData
                },
                yAxis: {},
                series: series
            });

            $this.attr("dataZoomStart", "70");
            $this.attr("dataZoomEnd", "100");
            hfChart.on('datazoom', function (params) {

                var dataZoomStart = $this.attr("dataZoomStart");
                var dataZoomEnd = $this.attr("dataZoomEnd");

                var start = params.start;
                var end = params.end;


                if(!start && params.batch) {
                    start = params.batch[0].start;
                    end = params.batch[0].end;
                }

                if(start == 0 && dataZoomStart != start) {
                    console.log("到头了！");
                }

                if(end == 100&& dataZoomEnd!= end) {
                    console.log("到尾了！");
                }
                $this.attr("dataZoomStart", start);
                $this.attr("dataZoomEnd", end);
            });
            setInterval(function(){
                refreshChart($this,hfChart)
            }, interval * 1000);
        });

    });



    function dateFormat(value, index, type) {
        // 格式化成月/日，只在第一个刻度显示年份
        var date = new Date(value);
        if(type == "second") {
            if (index === 0) {
                return date.Format("MM-dd hh:mm:ss");
            }else {
                return date.Format("hh:mm:ss");
            }
//            return  date.Format("yyyy-MM-dd hh:mm:ss");
        }else if(type == "minute") {
            if (index === 0) {
                return date.Format("MM-dd hh:mm");
            }else {
                return date.Format("hh:mm");
            }
//            return  date.Format("yyyy-MM-dd hh:mm");
        }else if(type == "hour") {
            return  date.Format("MM-dd hh");
        }else if(type == "day") {
            return  date.Format("yyyy-MM-dd");
        }else if(type == "week") {
            return  date.Format("yyyy-MM-dd");
        }else if(type == "month") {
            return  date.Format("yyyy-MM");
        }else if(type == "year") {
            return  date.Format("yyyy");
        }
    }

    function refreshChart(_$this,_hfChart) {

        var refresh = _$this.attr("refresh");
        if(refresh != "true") {
            return;
        }
        refreshSeries(_$this, "refresh", function(series){
            _hfChart.setOption({
                dataZoom: [
                    //{
                    //    show: true,
                    //    realtime: true,
                    //},
                    {
                        type: 'inside',
                        realtime: true,
                    }
                ],
                series: series});
        });

    }

    function getToolBox(_$this, _hfChart){

        var cycle = _$this.attr("cycle");
        var buttons = {};

        var refreshIcon = 'image:///static/plugins/echarts/img/refresh.png';
        if(_$this.attr("refresh") != "true") {
            refreshIcon = 'image:///static/plugins/echarts/img/refresh_off.png';
        }

        buttons["my_refresh"] = {
            show : true,
            title : '自动刷新',
            icon : refreshIcon,
            onclick : function(option1){
                autoRefresh(_$this,_hfChart);
            }
        };

        buttons["my_second"] = {
            show : true,
            title : '秒',
            icon : 'image:///static/plugins/echarts/img/second2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"second");
            }
        };

        buttons["my_minute"] = {
            show : true,
            title : '分',
            icon : 'image:///static/plugins/echarts/img/minute2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"minute");
            }
        };

        buttons["my_hour"] = {
            show : true,
            title : '时',
            icon : 'image:///static/plugins/echarts/img/hour2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"hour");
            }
        };
        buttons["my_day"] = {
            show : true,
            title : '天',
            icon : 'image:///static/plugins/echarts/img/day2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"day");
            }
        };
        buttons["my_week"] = {
            show : true,
            title : '周',
            icon : 'image:///static/plugins/echarts/img/week2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"week");
            }
        };

        buttons["my_month"] = {
            show : true,
            title : '月',
            icon : 'image:///static/plugins/echarts/img/month2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"month");
            }
        };

        buttons["my_year"] = {
            show : true,
            title : '年',
            icon : 'image:///static/plugins/echarts/img/year2_off.jpg',
            onclick :  function(option1){
                cycleChange(_$this,_hfChart,"year");
            }
        };

        buttons["dataZoom"] = {
            yAxisIndex: 'none'
        };

        buttons["magicType"] = {type: ['line', 'bar']};
        buttons["restore"] = {};
        buttons["saveAsImage"] = {};

        var icon = buttons["my_" + cycle].icon;
        buttons["my_" + cycle].icon = icon.substring(0, icon.length-8) + ".jpg";

        return buttons;
    }

    function cycleChange(_$this,_hfChart, _cycle) {
        _$this.attr("cycle", _cycle);
        var toolBoxFeature = getToolBox(_$this, _hfChart);

        refreshSeries(_$this, "switch", function(series){
            _hfChart.setOption({
                xAxis:{
                    axisLabel:{
                        // 使用函数模板，函数参数分别为刻度数值（类目），刻度的索引
                        formatter:function (value, index) {
                            return dateFormat(value,index, _cycle);
                        }
                    }
                },
                dataZoom: [
                    {
                        show: true,
                        realtime: true,
                    },
                    {
                        type: 'inside',
                        realtime: true,
                    }
                ],
                toolbox: {
                    feature:toolBoxFeature
                },
                series: series
            });

            var dataZoomStart = _$this.attr("dataZoomStart");
            var dataZoomEnd = _$this.attr("dataZoomEnd");
            _hfChart.dispatchAction({
                type: 'dataZoom',
                //// 可选，dataZoom 组件的 index，多个 dataZoom 组件时有用，默认为 0
                //dataZoomIndex: number,
                // 开始位置的百分比，0 - 100
                start: 60,
                // 结束位置的百分比，0 - 100
                end: 90,
                //// 开始位置的数值
                //startValue: number,
                //// 结束位置的数值
                //endValue: number
            });
            _hfChart.dispatchAction({
                type: 'dataZoom',
                //// 可选，dataZoom 组件的 index，多个 dataZoom 组件时有用，默认为 0
                //dataZoomIndex: number,
                // 开始位置的百分比，0 - 100
                start: 70,
                // 结束位置的百分比，0 - 100
                end: 100,
                //// 开始位置的数值
                //startValue: number,
                //// 结束位置的数值
                //endValue: number
            });
        });

    }

    function autoRefresh(_$this, _hfChart) {
        var refresh = _$this.attr("refresh");
        if(refresh == "true") {
            _$this.attr("refresh","false");
        }else {
            _$this.attr("refresh","true");
        }
        var toolBoxFeature = getToolBox(_$this, _hfChart);
        _hfChart.setOption({
            dataZoom: [
                {
                    show: true,
                    realtime: true,
                },
                {
                    type: 'inside',
                    realtime: true,
                }
            ],
            toolbox: {
                feature:toolBoxFeature
            },
        });
    }

    function refreshSeries(_$this, _type, _fun){
        var legendCode = _$this.attr("legend-code").split(",");
        var step = _$this.attr("step");
        var xAxisStart = _$this.attr("xAxis-start");
        var xAxisEnd = _$this.attr("xAxis-end");
        var cycle = _$this.attr("cycle");

        var _url =  "/chart/data.json";
        var _data = {"dataCodes":legendCode.join(","), "step" : step, "cycle" : cycle, "xAxisStart" : xAxisStart, "xAxisEnd" : xAxisEnd, "operateType" : _type};
        ajax.Post(_url,_data,function(data){
            if(data.resultCode == 0) {
                _$this.attr("xAxis-start",data.data.xAxisStart);
                _$this.attr("xAxis-end",data.data.xAxisEnd);

                var series = data.data.series;
                for(var index in series) {
                    var dataCode = series[index].name;
                    var legendData = _$this.attr("legend-data").split(",");
                    var legendCode = _$this.attr("legend-code").split(",");
                    var dataCodeIndex = legendCode.indexOf(dataCode);
                    series[index].name = legendData[dataCodeIndex];
                }
                _fun(series);
                //return  series;
            }
        });

        //return null;
    }


});

