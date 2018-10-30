<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="js/jquery.min.js"></script>
<script src="js/chemexon.js"></script>
<script src="js/marvinjslauncher.js"></script>
<style type="text/css">
#main {
	width: 1000px;
	margin: 0 auto;
}

#sidebar {
	width: 600px;
	height: 400px;
	float: left;
}

#page-wrap {
	width: 400px;	
	height: 400px;
	float: right;
}
</style>

</head>
<body>
	<form  name="searchform" action="" method="post">		
		<table>
			<tr>
				<td>${message}</td>
			</tr>
			<tr></tr>
			<tr align="center">
				<td>Name&nbsp;<input type="text" name="nameId" id="nmid"></input>
				<input	type="button"  value="search" onclick="javascript:searchName()"></input>
				</td>
			</tr>
		</table>
		<br><br>
		
		<div id="main" style="float:left;width:50%">
		<table><tr><td>
			<div  id="sidebar" style="height:600px">
				<iframe class="sketcher-frame" id="sketch" src="editorws.html"
					width="90%" height="500px"></iframe>
				<input type="hidden" id="molstructureId" name="molstructure"  >	
				
			</div>
</td><td>
			<div id="page-wrap">
				<table  border="1">
					<tr>
						<th>Id</th>
						<th>Name</th>
					</tr>
					<c:forEach var="maplist" items="${nameList}">
						<tr>
							<c:forEach var="map" items="${maplist}">
								<td><a href="javascript:getStructure(${map.key})"><c:out
											value="${map.key}" /></a></td>
								<td><c:out value="${map.value}" /></td>
							</c:forEach>
						</tr>
					</c:forEach>
				</table>
	
			</div>
			
		<div style="width: 40%">
<jsp:include page="viewPdf.jsp" />
</div></td></tr></table></div>
	</form>
</body>
<script>
	var s = "\n\n\n"
			+ " 14 15  0  0  0  0  0  0  0  0999 V2000\n"
			+ "    0.5089    7.8316    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    1.2234    6.5941    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    1.2234    7.4191    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   -0.2055    6.5941    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   -0.9200    7.8316    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    0.5089    5.3566    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   -0.2055    7.4191    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    0.5089    6.1816    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "   -0.9200    6.1816    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    0.5089    8.6566    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.4929    7.0066    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.0080    7.6740    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.0080    6.3391    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "    2.2630    8.4586    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
			+ "  1  7  1  0  0  0  0\n" + "  8  2  1  0  0  0  0\n"
			+ "  1  3  1  0  0  0  0\n" + "  2  3  2  0  0  0  0\n"
			+ "  7  4  1  0  0  0  0\n" + "  4  8  1  0  0  0  0\n"
			+ "  4  9  2  0  0  0  0\n" + "  7  5  1  0  0  0  0\n"
			+ "  8  6  1  0  0  0  0\n" + "  1 10  2  0  0  0  0\n"
			+ "  3 12  1  0  0  0  0\n" + "  2 13  1  0  0  0  0\n"
			+ " 13 11  2  0  0  0  0\n" + " 12 11  1  0  0  0  0\n"
			+ " 12 14  1  0  0  0  0\n" + "M  END\n";
</script>
</html>