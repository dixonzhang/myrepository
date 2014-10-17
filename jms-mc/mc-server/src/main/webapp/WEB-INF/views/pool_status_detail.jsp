<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Bootstrap 101 Template</title>

<!-- Bootstrap -->
<link href="<c:url value="/resources/bootstrap-3.2.0-dist/css/bootstrap.min.css"/>" rel="stylesheet">
<link href="<c:url value="/resources/datepicher/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
	<script src="<c:url value="/resources/my.js"/>"></script>
   	<script src="<c:url value="/resources/jquery-1.8.3.min.js"/>"></script>
    <script src="<c:url value="/resources/Highcharts-4.0.4/js/highcharts.js"/>"></script>
	<script src="<c:url value="/resources/Highcharts-4.0.4/js/modules/exporting.js"/>"></script>
	<script src="<c:url value="/resources/bootstrap-3.2.0-dist/js/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/resources/datepicher/bootstrap-datetimepicker.min.js"/>"></script>
	<script src="<c:url value="/resources/datepicher/bootstrap-datetimepicker.zh-CN.js"/>"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	
    <script type="text/javascript">
$(function () {
    $(document).ready(function () {
    	
    	activeNav("pool");
    	
       	Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
       	
       	var isNewest = true;
       	
       	var lastTime = '0';
       	var key = $("#key").val();
        

        $('#container_chart').highcharts({
            chart: {
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                //marginRight: 10,
                events: {
                    load: function () {
                        // set up the updating of the chart each second
                       /*  var series = this.series[0];
                        var series2 = this.series[1];
                        var series3 = this.series[2]; */
                        
                       var series = this.series;
                        
                       setInterval(function () {
                           if(isNewest == false)
                        	   return;
                            
                            
                    	   $.ajax({
                        	   type: "GET",
                        	   url: '/monitor/pool/detail/' + key,
                        	   data: 'lastTime=' + lastTime,
                        	   //async: false,
                        	   success: function(msg){
                        	     	//alert( "Data Saved: " + eval(msg)[0].createdTime );
                        	     	if("" == msg)
                        	     		return;
                        	     	
                        	     	var data = eval(msg);
                        	     
        	                     	$.each(data, function(i, item) {
        	                     		lastTime = item.createdTime;//update the lastTime
        	                     		series.forEach(function(serie){
        	                     			serie.addPoint([(new Date(item.createdTime)).getTime(), parseInt(item[serie.name])], true, true);
        	                     		});
        	                     	});
                        	   }
                       		});
                        }, 4000);
                        
                    }
                }
            },
            title: {
                text: (function(){
                	return $("#name").val();; 
                }())
            },
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: 'Value'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.name + '</b><br/>' +
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                        Highcharts.numberFormat(this.y, 2);
                }
            },
            legend: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            series: (function(){
                return getNewest();
            }())
        });
        
        
    	
		//alert($("#newest").val());
		$("#newest").click(function(){
			fireNewest();//重新加载最新数据
			
			
			isNewest = true;
			
			//addClass("selected"); removeClass("selected");
			$("#newest").parent().addClass("active");
			$("#search").parent().removeClass("active");
			
			$("#searchDiv").hide();
		});
		$("#search").click(function(){
			isNewest = false;
			
			//alert($("#search").parent().html());
			
			//addClass("selected"); removeClass("selected");
			$("#search").parent().addClass("active");
			$("#newest").parent().removeClass("active");
			
			$("#searchDiv").show();
		});
		
		
		$("#searchButton").click(function(){
			var startTime = $("#startTime").val();
			startTime = startTime.replace(/\-/g,"/")
			
			
			var count = $("#count").val();
			//alert("time=" + startTime + "  count=" + count);
			
			
			$.ajax({
         	   type: "GET",
         	   url: '/monitor/pool/detail_range/' + key,
         	   data: 'startTime=' + startTime + '&count=' + count,
         	   //async: false,
         	   success: function(msg){
         	     	//alert( "Data Saved: " + eval(msg)[0].createdTime );
         	     	
         	     	//移除原来的series
         	     	var series = Highcharts.charts[0].series;
         	     	for(var i = series.length-1; i >= 0; i--){
         	     		series[i].remove();
         	     	}
         	     	
         	     	if("" == msg)
         	     		return;
         	     	
         	     	var data = eval(msg);
         	     
         	     	var serieArray = wrap(data);
                 	
                 	//添加到chart
                 	serieArray.forEach(function(serie){  
	               		Highcharts.charts[0].addSeries(serie, true);
               		});
         	   }
        	});
		});
		
		
		//重新加载最新的数据
		function fireNewest(){
			lastTime = '0';
			//移除原来的series
 	     	var series = Highcharts.charts[0].series;
 	     	for(var i = series.length-1; i >= 0; i--){
 	     		series[i].remove();
 	     	}
 	     	
 	     	var serieArray = getNewest();
 	     	//添加到chart
         	serieArray.forEach(function(serie){  
           		Highcharts.charts[0].addSeries(serie, true);
       		});
		}
		
		//获取最新数据
		function getNewest(){
			var serieArray=[];
            
            $.ajax({
            	   type: "GET",
            	   url: '/monitor/pool/detail/' + key,
            	   data: 'lastTime=' + lastTime,
            	   async: false,
            	   success: function(msg){
            	     	//alert( "Data Saved: " + eval(msg)[0].createdTime );
            	     	var data = eval(msg);
            	     
            	     	serieArray = wrap(data);
            	   }
           	});
            
            return serieArray;
		}
		
		//组装series数组
		function wrap(data){
			var serieArray=[];
			
			var keys=[];
     		var item0 = data[0];
         	for(var p in item0){
           		if(item0.hasOwnProperty(p)){
           			if(p == "createdTime")
 						continue;
           			
           			var serie = new Object();
           			serie.name = p;
           			serie.data = [];
           			serieArray.push(serie);
          		}
    		}
     	
         	$.each(data, function(i, item) {
         		lastTime = item.createdTime;//update the lastTime
         		serieArray.forEach(function(serie){  
 					serie.data.push([(new Date(item.createdTime)).getTime(), parseInt(item[serie.name])]);
         		});
         	});
         	
         	return serieArray;
		}
		
		//日期插件
		$('.form_datetime').datetimepicker({
	        language:  'zh-CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			forceParse: 0,
	        showMeridian: 1
	    });
    });
});
		</script>
</head>
<body>
	<jsp:include page="nav.jsp" />
	
	<div class="container">
		<div class="page-header" style="margin-top:100px;">
        <h3><c:out value="${name}"/> 池状态</h3>
        <input id="key" value="${key}" type="hidden"/>
        <input id="name" value="${name}" type="hidden"/>
      </div>



		<div class="row">
			<div class="col-sm-20">
				<ul class="nav nav-pills" style="float:right">
					<li><a id="search" href="#">范围</a></li>
					<li class="active"><a id="newest" href="#">最新</a></li>
				</ul>
				
				
				<form  id="searchDiv" style="display:none;margin-right:20px;float:right"  role="form" class="form-inline">
					<div class="form-group">
						<label for="startTime">日期：</label>
						<div class="input-group date form_datetime " data-date=""
							data-date-format="yyyy/mm/dd hh:ii:ss" data-link-field="startTime">
							<input class="form-control" size="18" type="text" value="" readonly>
							<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
						</div>
						<input type="hidden" id="startTime" value="" /><br />
					</div>
					<div class="form-group" >
						<label for="count">&nbsp;数量：</label>
						<select id="count" class="form-control">
							<option>100</option>
							<option>200</option>
							<option>400</option>
							<option>700</option>
							<option>1000</option>
							<option>2000</option>
						</select>
					</div>
				   <button id="searchButton" class="btn btn-default">查询</button>
				</form>
				
			</div>
		</div>
		
		

		<!-- 图表 -->
		<div id="container_chart" style="min-width: 400px; height: 400px; margin: 40px auto 0 auto"></div>
	</div>

</body>
</html>