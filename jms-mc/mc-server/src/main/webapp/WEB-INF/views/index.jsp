<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>监控中心-连接状态</title>

<!-- Bootstrap -->
<link href="<c:url value="/resources/bootstrap-3.2.0-dist/css/bootstrap.min.css"/>" rel="stylesheet">

<script src="<c:url value="/resources/my.js"/>"></script>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="<c:url value="/resources/jquery-1.8.3.min.js"/>"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="<c:url value="/resources/bootstrap-3.2.0-dist/js/bootstrap.min.js"/>"></script>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

<script type="text/javascript">
$(function () {
	
    $(document).ready(function () {
    	activeNav("index");
    	
    	
    	$("#loading").fadeIn("slow");
    	
    	$.ajax({
     	   type: "GET",
     	   url: '/monitor/conn/count',
     	   //async: false,
     	   success: function(msg){
     		    var countBean = eval(msg)[0];
     		    
     		    //console.log("data ", countBean.key + ' -- ' + countBean.count);
     		    
     		    //没有显示的数据
     		   	if(countBean.count == 0){
    	     		$("#warning").fadeIn("slow");
    	     		return;
    	     	}
     		    
     		    //获取数据数量
     		    var amount = 0;
     		    
     		    //不停地获取数据，直到获取完毕
     		  	interval = setInterval(function () {
     		  		$.ajax({
                 	   type: "GET",
                 	   url: '/monitor/conn/load/' + countBean.key,
                 	   //async: false,
                 	   success: function(msg){
                 	     	if("" == msg)
                 	     		return;
                 	     	
                 	     	
                 	     	var data = eval(msg);
                 	     	
                 	     	//数量增加
                 	     	amount += data.length;
                 	     
                 	     	var ul = $(".list-group");
                 	     	$.each(data, function(i, item) {
                 	     		var li = '<li class="list-group-item"><h5><span class="label ';
                 	     		if(item.connected){
                 	     			li += 'label-success">&nbsp;ON&nbsp;';
                 	     		}
                 	     		else{
                 	     			li += 'label-danger">OFF';
                 	     		}
                 	     		
                 	     		li += "</span>&nbsp;" + item.appName + '<strong> (' + item.ip + ':' + item.port + ')</strong></h5></li>';
                 	     		
            					ul.append(li);     	     		
                          	});
                 	     	
                 	     	//停止Interval
                 	     	if(amount == countBean.count){
                 	     		clearInterval(interval);
                 	     		$("#loading").hide();
                 	     	}
                 	   }
               		});
     		  		//-- end of second ajax
     		  		
     		  	}, 500);
     		    
     		    //-- end of first ajax
     	   }
   		});
    	
    	
    });
    
});
</script>


</head>
<body>
	<jsp:include page="nav.jsp" />

	<div class="container">
		<div class="page-header" style="margin-top: 100px;">
			<h3>系统间的接口连接状态</h3>
		</div>
		
		
		<div class="row">
			<div class="col-sm-12">
				<ul class="list-group">
				</ul>
			</div>
		</div>
		
		
		
		<div id="warning" style="display: none;" class="alert alert-warning" role="alert">
			<strong>Warning!</strong> 没可显示的数据.
		</div>
		<div id="loading" style="display: none;" class="alert alert-warning" role="alert">
			<strong>loading...</strong>
		</div>
	</div>

</body>
</html>