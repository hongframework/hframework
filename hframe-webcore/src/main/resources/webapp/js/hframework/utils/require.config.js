
//var webHost = appConfig.ucdomain;
//
//
//if(webHost == ''){
//	webHost = '/';
//}


requirejs.config({
  baseUrl: "/",
  paths:{
	  'store':['static/plugins/jquery/store.min'],
	  'echarts': ['static/plugins/ehcarts/echarts-all'],
	  'dot':['static/plugins/dot/doT.min'],
	  'jquery' : ['static/plugins/jquery/jquery'],
	  'jquery-easing': ['static/plugins/jquery/jquery.easing.min'],
	  'jquery-cookie': ['static/plugins/jquery/jquery.cookie'],
	  'jquery-base64': ['static/plugins/jquery/jquery.base64'],
	  'lazyload': ['static/plugins/jquery/jquery.lazyload'],
	  'layer':['static/plugins/layer/layer'],
	  'ajax': ['js/hframework/utils/ajax'],
	  'selectBox': ['static/js/common/unit/selectbox'],
	  'gradeBox': ['static/js/common/unit/gradeBox'],
	  'utility': ['static/js/web/common/utility'],
	  'checkinput': ['static/js/common/utilities/checkinput'],
	  'webuploader': ['static/plugins/webuploader/webuploader.min']
  }
});