<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="refresh" content="2">
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
    	activeNav("pool");
    });
});
</script>
</head>
<body>
	<jsp:include page="nav.jsp" />
	
	<div class="container">
		<div class="page-header" style="margin-top: 100px;">
			<h3>应用池状态</h3>
		</div>

		<c:choose>
	      	<c:when test="${empty psList}">
	      		<div class="alert alert-warning" role="alert">
	        		<strong>Warning!</strong> 没可显示的数据.
	      		</div>
	      	</c:when>
	      	
	      	<c:otherwise>
	      		<div class="row">
		        	<div class="col-md-6">
		        		<table class="table table-bordered table-striped">
				           <!--  <thead>
				              <tr>
				                <th>#</th>
				                <th>First Name</th>
				                <th>Last Name</th>
				                <th>Username</th>
				              </tr>
				            </thead> -->
				            <tbody>
				            	<c:forEach items="${psList}" var="entry">
									<tr>
										<td rowspan="2">
											<!-- <button type="button" class="btn btn-lg btn-link"> -->
											<a href="<c:url value="/monitor/pool/chart/${entry.key}"/>"><c:out value="${entry.fullName}" /></a>
											<!-- </button> -->
										</td>
										
				            			<c:forEach items="${entry.statusMap}" var="entry2" varStatus="s">
											<td><c:out value="${entry2.key}" /></td>
										</c:forEach>
									</tr>
									<tr>
				            			<c:forEach items="${entry.statusMap}" var="entry2" varStatus="s">
											<td><c:out value="${entry2.value}" /></td>
										</c:forEach>
									</tr>
				            	</c:forEach>
				            </tbody>
				          </table>
		        	</div>
		        </div>
	      	</c:otherwise>
      </c:choose>
	</div>      

</body>
</html>