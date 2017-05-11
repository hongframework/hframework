<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="../taglib.jsp"%>
<!DOCTYPE html>
<!--[if lt IE 7]>
<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>
<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>
<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js"> <!--<![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../common.jsp"></jsp:include>
<link rel="stylesheet" href="${ucdomain}/static/css/uc/area.css" type="text/css" />
<title><msg:message code="system.name"/></title>
</head>
<body>
<jsp:include page="../header.jsp"></jsp:include>
<input type="hidden" name="token" id="token" value="${token}"/>
<input type="hidden" id="nav_id" value="nav_trade">
<div class="uc">
	<div class="container">
		<div class="uc-left">
			<jsp:include page="../left.jsp"></jsp:include>
		</div>
		<div class="uc-right">
			<jsp:include page="../alert.jsp"></jsp:include>
			<div class="uc-title">
				<h3>资金流水<a href="${base}/trade/index">我的交易</a></h3>

			</div>
			<div class="uc-content">
				<div class="jyls-sort-type" data-qType="${qType}">
					<%--<h4>交易类型</h4>--%>
					<div class="type-box" data-qType="0">
						<c:if test="${qType == 0}">
							<span class="active">资金流水</span>
						</c:if>
						<c:if test="${qType != 0}">
							<span>资金流水</span>
						</c:if>
					</div>
					<div class="type-box bordered" data-qType="1">
						<c:if test="${qType == 1}">
							<span class="active">投资记录</span>
						</c:if>
						<c:if test="${qType != 1}">
							<span>投资记录</span>
						</c:if>
					</div>
					<div class="type-box bordered" data-qType="2">
						<c:if test="${qType == 2}">
							<span class="active">充值记录</span>
						</c:if>
						<c:if test="${qType != 2}">
							<span>充值记录</span>
						</c:if>
					</div>
					<div class="type-box bordered" data-qType="3">
						<c:if test="${qType == 3}">
							<span class="active">提现记录</span>
						</c:if>
						<c:if test="${qType != 3}">
							<span>提现记录</span>
						</c:if>
					</div>
					<div class="type-box bordered" data-qType="4">
						<c:if test="${qType == 4}">
							<span class="active">兑付记录</span>
						</c:if>
						<c:if test="${qType != 4}">
							<span>兑付记录</span>
						</c:if>
					</div>
					<%--<div class="type-box bordered" data-qType="4">--%>
						<%--<c:if test="${qType == 5}">--%>
							<%--<span class="active">兑付记录</span>--%>
						<%--</c:if>--%>
						<%--<c:if test="${qType != 4}">--%>
							<%--<span>兑付记录</span>--%>
						<%--</c:if>--%>
					<%--</div>--%>

				</div>
				<div class="jyls-list">
					<ul>
						<c:if test="${qType==0}">
						<li class="list-title" data-sortname="${sortName}" data-sorttype="${sortType}">
							<div class="t7 can-click" data-sortname="">交易时间</div>
							<div class="t8 can-click" data-sortname="">资金流向</div>
							<div class="t9 can-click" data-sortname="">交易类型</div>
							<div class="t11 can-click" data-sortname="">交易金额（元）</div>
							<div class="t12 can-click" data-sortname="">可用余额（元）</div>
							<div class="t10">备注</div>
						</li>
						</c:if>
						<c:if test="${qType==1}">
							<li class="list-title" data-sortname="${sortName}" data-sorttype="${sortType}">
								<div class="t1 can-click" data-sortname="">认购时间</div>
								<div class="t2 can-click" data-sortname="">投资金额（元）</div>
								<div class="t3 can-click" data-sortname="">认购状态</div>
								<div class="t5">备注</div>
							</li>
						</c:if>
						<!--充值-->
						<c:if test="${qType==2}">
							<li class="list-title" data-sortname="${sortName}" data-sorttype="${sortType}">
								<div class="t1 can-click" data-sortname="">充值时间</div>
								<div class="t2 can-click" data-sortname="">充值金额（元）</div>
								<div class="t3 can-click" data-sortname="">充值状态</div>
								<div class="t5">备注</div>
							</li>
						</c:if>
						<!--提现-->
						<c:if test="${qType==3}">
							<li class="list-title" data-sortname="${sortName}" data-sorttype="${sortType}">
								<div class="t1 can-click" data-sortname="">申请提现时间</div>
								<div class="t2 can-click" data-sortname="">提现金额（元）</div>
								<div class="t3 can-click" data-sortname="">提现状态</div>
								<div class="t5">备注</div>
							</li>
						</c:if>
						<c:if test="${qType==4}">
							<li class="list-title" data-sortname="${sortName}" data-sorttype="${sortType}">
								<div class="t1 can-click" data-sortname="">兑付时间</div>
								<div class="t2 can-click" data-sortname="">兑付金额（元）</div>
								<div class="t5">备注</div>
							</li>
						</c:if>
						<!--全部-->
						<c:if test="${qType==0}">
							<c:forEach var="prop" items="${accountingDetails}">
								<li>
									<div class="t7">
										${prop.tempTime}
									</div>
									<div class="t8">&nbsp;
										<c:if test="${prop.flowTo==1}">
											进账
										</c:if>
										<c:if test="${prop.flowTo==0}">
											出账
										</c:if>
									</div>
									<div class="t8">
										<c:if test="${prop.businessType==1}">
											投资
										</c:if>
										<c:if test="${prop.businessType==2}">
											兑付
										</c:if>
										<c:if test="${prop.businessType==3}">
											活动
										</c:if>
										<c:if test="${prop.businessType==4}">
											充值
										</c:if>
										<c:if test="${prop.businessType==5}">
											提现
										</c:if>
										<c:if test="${prop.businessType==6}">
											返款
										</c:if>
									</div>

									<c:if test="${prop.businessType==1}">
										<div class="t9">-${prop.moneyFormat}</div>
									</c:if>
									<c:if test="${prop.businessType==2}">
										<div class="t9">+${prop.moneyFormat}</div>
									</c:if>
									<c:if test="${prop.businessType==3}">
										<div class="t9">+${prop.moneyFormat}</div>
									</c:if>
									<c:if test="${prop.businessType==4}">
										<div class="t9">+${prop.moneyFormat}</div>
									</c:if>
									<c:if test="${prop.businessType==5}">
										<div class="t9">-${prop.moneyFormat}</div>
									</c:if>
									<c:if test="${prop.businessType==6}">
										<div class="t9">+${prop.moneyFormat}</div>
									</c:if>
									<!-- 剩余金额 -->
									<div class="t9">&nbsp;${prop.accountMoney}</div>
									<div class="t10">
											<c:if test="${prop.businessType==1}">
												<span style="display: inline-block; width: 180px; padding-left: 10px;">投资购买${prop.productName}</span> 交易编号: ${prop.transactionNumber}
											</c:if>
											<c:if test="${prop.businessType==2}">
												<span style="display: inline-block; width: 180px;padding-left: 10px;" >本息兑付</span> 交易编号: <a href="${ucdomain}/trade/retmoneyinfo?transactionNumber=${prop.transactionNumber}" target="_blank">${prop.transactionNumber}</a>
											</c:if>
											<c:if test="${prop.businessType==3}">
												活动
											</c:if>
											<c:if test="${prop.businessType==4}">
												<%--借记卡网银--%>
												<c:if test="${fn:contains(prop.description,'B2CDEBITBANK')}">
													<div class="t5"><span style="display: inline-block; width: 180px;padding-left: 10px;" >网银充值</span></div>
												</c:if>
												<%--"DEBITCARD";//借记卡快捷--%>
												<c:if test="${fn:contains(prop.description,'DEBITCARD')}">
													<div class="t5"><span style="display: inline-block; width: 180px; padding-left: 10px;">快捷充值</span>银行卡后四位: ${cardNumber}</div>
												</c:if>
											</c:if>
											<c:if test="${prop.businessType==5}">
												<%--<div class="t5"><span style="display: inline-block; width: 180px;">--%>
												${prop.description}
												<%--</span></div>--%>
											</c:if>
									</div>
								</li>
							</c:forEach>
						</c:if>
						<!--投资-->
						<c:if test="${qType==1}">
							<c:forEach var="prop" items="${productAmountLocks}">
								<li>
									<div class="t1">${prop.tempTime}</div>
									<div class="t2">${prop.moneyFormat}</div>
									<div class="t3">
										<%--资金转入状态-0：等待转入 1：转入成功 2：转入失败--%>
										<c:if test="${prop.intoStatus==0}">
											认购等待
										</c:if>
										<c:if test="${prop.intoStatus==1}">
											认购成功
										</c:if>
										<c:if test="${prop.intoStatus==2}">
											认购失败
										</c:if>
									</div>
									<div class="t5">
										<span style="display: inline-block; width: 200px;padding-left: 10px;">投资购买${prop.productName}</span> 交易编号: ${prop.transactionNumber}
									</div>
								</li>
							</c:forEach>
						</c:if>
						<!--充值-->
						<c:if test="${qType==2}">
							<c:forEach var="prop" items="${rechargeRecordses}">
								<li>
									<div class="t1">${prop.tempTime}&nbsp;</div>
									<div class="t2">${prop.moneyFormat}&nbsp;</div>
									<div class="t3">
										<c:if test="${prop.status==0}">
											充值失败
										</c:if>
										<c:if test="${prop.status==1}">
											充值成功
										</c:if>
										<c:if test="${prop.status==2}">
											申请充值
										</c:if>
									</div>
									<div class="t5">
											<%--借记卡网银--%>
										<c:if test="${fn:contains(prop.remark,'B2CDEBITBANK')}">
											<div class="t5"><span style="display: inline-block; width: 200px;padding-left: 10px;">网银充值</span></div>
										</c:if>
											<%--"DEBITCARD";//借记卡快捷--%>
										<c:if test="${fn:contains(prop.remark,'DEBITCARD')}">
											<div class="t5"><span style="display: inline-block; width: 200px;padding-left: 10px;">快捷充值</span>银行卡后四位: ${cardNumber}</div>
										</c:if>
									</div>
								</li>
							</c:forEach>
						</c:if>
						<!--提现-->
						<c:if test="${qType==3}">
							<c:forEach var="prop" items="${withdrawCashRecordses}">
								<li>
									<div class="t1">${prop.tempTime}</div>
									<div class="t2">${prop.moneyFormat}</div>
									<div class="t3">
										<c:if test="${prop.status==0}">
											提现失败
										</c:if>
										<c:if test="${prop.status==1}">
											提现成功
										</c:if>
										<c:if test="${prop.status==2}">
											申请提现
										</c:if>
										<c:if test="${prop.status==3}">
											资金冻结
										</c:if>
									</div>
									<div class="t5">
										<%--<c:if test="${prop.status==3}">--%>
											<div class="t5"><span style="display: inline-block; width: 200px;padding-left: 10px;">用户申请提现</span>银行卡后四位: ${cardNumber}</div>
										<%--</c:if>--%>
										<%--<c:if test="${prop.status==1}">--%>
											<%--${prop.remark}--%>
										<%--</c:if>--%>
									</div>
								</li>
							</c:forEach>
						</c:if>
						<!--兑付-->
						<c:if test="${qType==4}">
							<c:forEach var="prop" items="${productReturnMoneys}">
								<li>
									<%--/**返款状态-0：未还款 1：已还款 2：还款成功 3：还款失败 4：拒绝返款**/--%>
									<div class="t1">${prop.tempTime}</div>
									<div class="t2">${prop.moneyFormat}</div>
									<div class="t5"><span style="display: inline-block; width: 200px;">投资返现</span> 交易编号: <a href="${ucdomain}/trade/retmoneyinfo?transactionNumber=${prop.transactionNumber}" target="_blank">${prop.transactionNumber}</a></div>
								</li>
							</c:forEach>
						</c:if>
					</ul>
					<c:if test="${qType==0 && empty accountingDetails}">
						<div class="no-record">
							<i></i>
							<br>
							<span>暂无相关交易流水</span>
						</div>
					</c:if>
					<c:if test="${qType==1 && empty productAmountLocks}">
						<div class="no-record">
							<i></i>
							<br>
							<span>暂无相关交易流水</span>
						</div>
					</c:if>
					<c:if test="${qType==2 && empty rechargeRecordses}">
						<div class="no-record">
							<i></i>
							<br>
							<span>暂无相关交易流水</span>
						</div>
					</c:if>
					<c:if test="${qType==3 && empty withdrawCashRecordses}">
						<div class="no-record">
							<i></i>
							<br>
							<span>暂无相关交易流水</span>
						</div>
					</c:if>
					<c:if test="${qType==4 && empty productReturnMoneys}">
						<div class="no-record">
							<i></i>
							<br>
							<span>暂无相关交易流水</span>
						</div>
					</c:if>
				</div>

				<div class="wjy-page">
					<c:if test="${empty page || page == 0}">
					<ul id="page" class="pagination" data-pageIndex="1" data-pageCount="${pageCount}" data-pageRows="10">

					</ul>
					</c:if>
					<c:if test="${!empty page && page != 0}">
						<ul id="page" class="pagination" data-pageIndex="${page}" data-pageCount="${pageCount}" data-pageRows="10">
						</ul>
					</c:if>
					<div class="page-info">
						<span>第${page}页</span>
						<c:if test="${empty total}">
							<span>共0条记录</span>
						</c:if>
						<c:if test="${!empty total}">
							<span>共${total}条记录</span>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../footer.jsp"></jsp:include>
<script type="text/javascript" src="${ucdomain}/static/js/uc/trade/tradeflow.js"></script>

</body>
</html>
