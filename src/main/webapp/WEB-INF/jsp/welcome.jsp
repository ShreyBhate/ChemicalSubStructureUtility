
<%@page import="com.spring.vo.ArticleVo"%>
<%@page import="com.spring.vo.StructureVo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page import="java.util.Map" %>
	<%@ page import="java.util.HashMap" %>
	<%@ page import="java.util.List" %>
	<%@ page import="java.util.ArrayList" %>
	<%@page import="java.util.regex.Pattern" %>
	<%@page import="java.util.regex.Matcher" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

 <meta name="viewport" content="width=device-width, initial-scale=1"> 
 
<title>Chemaxon</title>
<!-- Bootstrap -->
<link href="bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Fontawsome -->
<link href="bower_components/font-awesome/css/font-awesome.css" rel="stylesheet">
<script src="js/jquery.min.js"></script>
<script src="js/jquery-ui.js"></script>
 <script src="js/jquery.maskedinput.min.js"></script>

<script src="bower_components/jquery/dist/jquery.min.js"></script>
<script src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="js/chemexon.js"></script>
<script src="js/marvinjslauncher.js"></script>


<link href="js/jQuery-ui.css" rel="stylesheet">

<style type="text/css">
#main {
	width: 1000px;
	margin: 0 auto;
}

#sidebar {
	width: 500px;
	height: 400px;
	float: left;
}

#page-wrap {
	/*  width: 400px;	 */
	/* height: 400px;  */
	 float: centre ; 
}

tr.border_bottom td {
  border-bottom:2pt solid red;
 	padding-bottom: .5em;
  
}
mark {
    background-color: yellow;
    color: black;
}
anchor{
      background-color: #0000EE ; 
      color: #FFFFFF; 
      border: 1px solid green; 
      padding: 15px;
      font-size: 20px;
  }
</style>

</head>
<%

	String path ="/home/jenita.kn/Desktop/chemaxonProjectFolder";
	Map modelMap=(Map)request.getAttribute("MODELMAP")==null?new HashMap():(Map)request.getAttribute("MODELMAP");
	Map<String, Integer>sortMap = (Map<String, Integer>)modelMap.get("sortMap")==null?new HashMap<String,Integer>():(Map<String,Integer> )modelMap.get("sortMap");
	Map<String,Map<String, String>>otherServices =  (Map<String,Map<String, String>>)modelMap.get("otherServices")==null?new HashMap<String,Map<String, String>>():(Map<String,Map<String, String>>)modelMap.get("otherServices");
	Map<String, String>dataMap = (Map<String, String>)modelMap.get("dataMap")==null?new HashMap<String,String>():(Map<String,String> )modelMap.get("dataMap");
	String filName = (String )modelMap.get("filterName")==null?new String():(String )modelMap.get("filterName");
	String structure = (String )modelMap.get("structure")==null?new String():(String )modelMap.get("structure");
	Map<String, StructureVo>structureVoMap = (Map<String, StructureVo>)modelMap.get("structureVoMap")==null?new HashMap<String,StructureVo>():(Map<String,StructureVo> )modelMap.get("structureVoMap");
	StructureVo vo = new StructureVo();
	Map<String, ArticleVo>articlesVoMap = (Map<String, ArticleVo>)modelMap.get("articles")==null?new HashMap<String,ArticleVo>():(Map<String,ArticleVo> )modelMap.get("articles");
	ArticleVo articleVo = new ArticleVo();
%>
<body>
	<form  name="searchform" action="" method="post">		
<div class="container-fluid">

	<div class="panel panel-primary" align="center"><b><font color="blue">Structure Search</font></b></div>

 <input type="hidden" name="nameId" id="nmid"/>
	<input type="hidden" id="searchOption_id" name="searchOption"/>
 

<div class="row">
    <div class="col-md-6">
    
    <ul class="nav nav-tabs">
  <li class="active"><a data-toggle="tab" href="#structureCollapse">Substructure Search</a></li>
  <li><a data-toggle="tab" href="#nameCollapse">Name Search</a></li>
  <li><a data-toggle="tab" href="#smilesCollapse">SMILES Search</a></li>
    <li><a data-toggle="tab" href="#formulaCollapse">Formula Search</a></li>
</ul> 
	
<input  type="hidden" id="filterName_id" name="filterName" /> 

  <div class="tab-content">
<div id="structureCollapse" class="tab-pane fade in active">
<div  id="sidebar" style="height:300px">
				<iframe class="sketcher-frame" id="sketch" src="editorws.html"
					width="100%" height="250px"></iframe>
				<input type="hidden" id="molstructureId" name="molstructure" >	
				<input type="button" class="btn btn-success" value="Search Structure" onclick="javascript:searchNameStructure('structure');"/><br>
			</div>
			</div>
			
			
	<div id="nameCollapse" class="tab-pane fade">
	<b>Search by Name : </b>
			 		<input  type="text" id="searchName_id" name="name" /> <input type="button" class="btn btn-success" value="Search Name" onclick="javascript:searchName('name');"/>
	<br></div>	
	
	
	<div id="smilesCollapse" class="tab-pane fade">
	<b>Search by SMILES :  </b>
			 		<input  type="text" id="smilesName_id" name="smilesName" /> <input type="button" class="btn btn-success" value="Search SMILES" onclick="javascript:searchName('smiles');"/>
	<br></div>	
			
			
			
			
			<div id="formulaCollapse" class="tab-pane fade">
	<b>Search by Formula :  </b>
			 		<input  type="text" id="formulaName_id" name="formulaName" /> <input type="button" class="btn btn-success" disabled="disabled" value="Search Formula" onclick="javascript:searchName('smiles');"/>
	<br></div>	
			
	

		</div>	</div></div>
			
			
<%-- <a href="javascript:getStructure(${fn:split(mapList,'###')[0]});"><c:out value="${fn:split(mapList,'###')[0]}"/></a>
 --%>

<div class="panel-group">
<%if(sortMap.size()>0){
		int counter=0;%>  
	<%for(String key: sortMap.keySet()){
					counter++;
					String doi = key.split("###")[2];
					%>
				

<div class = "panel panel-default">
 	 <div class="panel-heading">
 	 
 	 <%
 	 	vo = structureVoMap.get(key.split("###")[1]);
 	 	articleVo  = articlesVoMap.get(key.split("###")[2]);
 	 	String name = key.split("###")[1];
 	 	String title = articleVo.getTITLE();
 	 	String abstr = articleVo.getABSTRACT();
		String regex = name.replace("{","\\{").replace("}", "\\}");
 
 	 
 	 
 	 %>
 	 					<font color=#4169E1><b><%=counter %> </b>.
 	 						<b>Doi : </b></font><%=key.split("###")[2] %><br>
							<font color="#4169E1"><b>Title : </b></font><%=title%><br>
							<font color="#4169E1"><b>Abstract : </b></font><%=abstr%><br>
							<font color="#4169E1"><b>Molecular Formula : </b></font><%=vo.getMolecularFormula()%><br>
							<font color="#4169E1"><b>Molecular Weight : </b></font><%=vo.getMolecularWeight()%><br>
							<%if(!vo.getComments().equals("")){ %><font color="#4169E1"><b>Comments : </b></font><%=vo.getComments()%><br><%} %>
							<font color="#4169E1"><b>Score : </b></font><%=dataMap.get(key) %><br>
							
			</div>				
							<div class="panel-body">
							
							<table border="1"><tr><td><img id='img_id' src="/chemaxonProjectImages/<%=key.split("###")[3]%>" height='180px' width='180px' /></td>
							<%  Map<String, String>map = otherServices.get(doi);
								if(map.size()>0){
									for(String chem :map.keySet()){ %>
										<td><img id='img_id' src="/chemaxonProjectImages/<%=map.get(chem) %>" height='180px' width='180px' /></td>
									<%}
								}
							%>
							</tr>
						
							<tr ><td  align="left" ><b><a href="javascript:getStructure(<%= key.split("###")[0]%>);"><%=key.split("###")[1] %></a></b></td>
							<%  Map<String, String>mapp = otherServices.get(doi);
								if(mapp.size()>0){
									for(String chem :mapp.keySet()){%>
										<td><%=chem %></td> 
									<%}
								}
							%></tr></table></div>
							
				</div>
					<%} %>
	 	<%} %></div>
			
</div>
	</form>
	
	<script>

$(document).ready(function(){
	$('[data-toggle="tooltip"]').tooltip();   
	/*  $('[data-toggle="popover"]').popover({
	        placement : 'top',
	        trigger : 'hover',
	       
	        content : '<div class="media"><a href="#" class="pull-left"><img src="image2.png" class="media-object" alt="Sample Image"></a><div class="media-body"></div></div>'
     }); */
});
</script>
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