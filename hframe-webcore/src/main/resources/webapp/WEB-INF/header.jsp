<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<script type="text/javascript">
    appConfig = {
        wsdomain: '${wsdomain}',
        ucdomain: '${ucdomain}',
        resdomain: '${resdomain}'
    }
</script>
<div class="g-tool">
    <div class="container">
        <div class="pull-left">
            <ul>
                <li>
                    <i class="fa fa-phone"></i>
                    <span>客服热线：</span>
                    <span>400-10-32580</span>
                </li>
                <li>
                    <a href="javascript:;"><i class="fa fa-weixin" id="btn_top_weixin"></i></a>
                </li>
            </ul>
        </div>
        <div class="pull-right t-user">
        </div>
        <div class="pull-right">欢迎来到学费帮！</div>
    </div>
</div>
<div class="header">
    <div class="container">
        <h1 class="logo pull-left">
            <a href="${wsdomain}/index" title="学费帮"><img src="${ucdomain}/static/images/common/logo.png" alt="学费帮"></a>
        </h1>
        <div class="menu pull-right">
            <ul>
                <li><a href="${wsdomain}/index" title="首页">首页</a></li>
                <li><a href="${wsdomain}/budget/onlyoverview" title="算学费">算学费</a></li>
                <%--<li><a href="${wsdomain}/assets/portfolio" title="智能配置">智能配置</a></li>--%>
                <li><a href="${wsdomain}/product/list" title="赚学费">赚学费</a></li>
                <li><a href="${ucdomain}/index" title="我的账户">我的账户</a> </li>
                <li><a href="${wsdomain}/about/beginnerguide" title="新手指南">新手指南</a> </li>
                <%--<li><a href="${wsdomain}/feedback/feedback" title="意见反馈">意见反馈</a></li>--%>
            </ul>
        </div>
    </div>
</div>
<div class="wechat_bg">
    <div class="wechat">
        <a href="javascript:;" title="关闭" class="wechatclose"><i class=" fa fa-times"></i></a>
        <div class="wechatimg text-center">
            <img src="${ucdomain}/static/images/common/weixin.jpg"/>
        </div>
    </div>
</div>
<script type="text/javascript" src="${ucdomain}/static/js/uc/common/header.js"></script>