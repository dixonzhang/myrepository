<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Bootstrap 101 Template</title>

<!-- Bootstrap -->
<link href="<c:url value="/resources/bootstrap-3.2.0-dist/css/bootstrap.min.css"/>" rel="stylesheet">

<script src="<c:url value="/resources/my.js"/>"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="<c:url value="/resources/jquery-1.8.3.min.js"/>"></script>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<script type="text/javascript">
$(function () {
    $(document).ready(function () {
    	activeNav("exception");
    });
});
</script>
</head>
<body>
	<jsp:include page="nav.jsp" />
	
	<div class="container">
		<div class="page-header" style="margin-top: 100px;">
			<h3>异常列表</h3>
		</div>

		<%-- <div class="row">
			<div class="col-sm-20">
				<ul class="nav nav-pills" style="float:right">
					<c:choose>
						<c:when test="${type == 'all'}">
							<li><a id="search" href="#">分类</a></li>
							<li class="active"><a id="newest" href="#">所有</a></li>
						</c:when>
						<c:otherwise>
							<li class="active"><a id="search" href="#">分类</a></li>
							<li><a id="newest" href="#">所有</a></li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		</div> --%>

		<c:choose>
			<c:when test="${empty excepList}">
				<div class="alert alert-warning" role="alert">
					<strong>Warning!</strong> 没可显示的数据.
				</div>
			</c:when>

			<c:otherwise>
				<div class="row">
					<div class="col-sm-12">
						<div class="list-group">
							<c:forEach items="${excepList}" var="excep">
								<a href="#" class="list-group-item">
				              		<h4 class="list-group-item-heading">
				              			<small><fmt:formatDate value="${excep.createdTime}" pattern="yyyy/MM/dd HH:mm:ss"/></small>
				              			${excep.type} 
					              		<strong>【${excep.fullName}】</strong> &nbsp;
					              		
				              		</h4>
				              		<p class="list-group-item-text">${excep.msg}.</p>
				            	</a>
							</c:forEach>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

</body>
</html>