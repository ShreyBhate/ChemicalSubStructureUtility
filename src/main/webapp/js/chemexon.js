/**
 * 
 */

function getStructure(id) { 
    $.ajax({
        url : "getStructure.htm?id=" + encodeURIComponent(id),             
        type : "POST",
        dataType : "text",
        success : function(response) {              
        	loadMolfile(response) ;   
        },
        Error : function(e) {
            alert("e:" + e)
        }
    });
}

var marvinSketcherInstance;
function loadMolfile (molfile) {
	MarvinJSUtil.getEditor("#sketch").then(function (sketcherInstance) {
		marvinSketcherInstance = sketcherInstance;
		marvinSketcherInstance.importStructure("mol", molfile)	
		},function (error) {
		alert("Cannot retrieve sketcher instance from iframe:"+error);
	});
}


function exportMol() {
	return  MarvinJSUtil.getEditor("#sketch").then(function (sketcherInstance){
		marvinSketcherInstance = sketcherInstance;		
		console.log(marvinSketcherInstance.exportAsMol())
		 molStructure=marvinSketcherInstance.exportAsMol();
		},function (error) {
		alert("Cannot retrieve sketcher instance from iframe:"+error);
	});
	  ;
}

function searchName(option) {

	
	var name = document.getElementById("searchName_id").value;
	var smiles = document.getElementById("smilesName_id").value;
	var emptyStructure ="  MJ172400                      \n\n" +
		"  0  0  0  0  0  0  0  0  0  0999 V2000\n" +
		"M  END";
//	alert(name)
	
	
	if(option=="name"){
		
		if(name==''){
			alert("Enter a name to search");
			return false;
		}
		
		document.searchform.name.value=name;
		
	}else if(option=="smiles"){
		
		if(smiles==''){
			alert("Enter smiles to search");
			return false;
		}

		document.searchform.smilesName.value=smiles;
		
	}
	
	
		document.searchform.searchOption.value=option;
		document.searchform.action = "structureSearch.htm";
		document.searchform.submit();
}


function searchNameStructure(structure){
	var option=structure;
	var emptyStructure ="  MJ172400                      \n\n" +
		"  0  0  0  0  0  0  0  0  0  0999 V2000\n" +
		"M  END";
	var molStructure;
			MarvinJSUtil.getEditor("#sketch").then(function (sketcherInstance){
			marvinSketcherInstance = sketcherInstance;		

			molStructure = marvinSketcherInstance.exportAsMol();
//			 alert(molStructure)
			 if(molStructure==emptyStructure){
				 alert("Empty Structure. Draw a structure and search");
				 return false;
			 }
				
			}, function(error) {
				alert("Cannot retrieve sketcher instance from iframe:" + error);
			});
			
		   document.searchform.molstructure.value=molStructure;
		   document.searchform.searchOption.value=option;
		   document.searchform.action = "structureSearch.htm";
		   document.searchform.submit();
}

function filterNameSearch(){
	
	var option = document.getElementById("searchOption_id").value;

	 var emptyStructure ="  MJ172400                      \n\n" +
	 		"  0  0  0  0  0  0  0  0  0  0999 V2000\n" +
	 		"M  END";
	var name = document.getElementById('filterName_id').value;
	if(name==''){
		alert("Enter name to filter by");
		return false
	}
	
	
	var name = document.getElementById("searchName_id").value;
	var smiles = document.getElementById("smilesName_id").value;
	
//	alert(name)
	if(option=="name"){
		
		if(name==''){
			alert("Enter a name to search");
			return false;
		}
		
		document.searchform.name.value=name;
		
	}else if(option=="smiles"){
		
		if(smiles==''){
			alert("Enter smiles to search");
			return false;
		}

		document.searchform.smilesName.value=smiles;
		
	}else{
		 var emptyStructure ="  MJ172400                      \n\n" +
	 		"  0  0  0  0  0  0  0  0  0  0999 V2000\n" +
	 		"M  END";
		 
		 
	   MarvinJSUtil.getEditor("#sketch").then(function (sketcherInstance){
		marvinSketcherInstance = sketcherInstance;		

		 molStructure=marvinSketcherInstance.exportAsMol();
		 alert(molStructure)
		 if(molStructure==emptyStructure){
			 alert("Empty Structure. Draw a structure and search");
			 return false;
		 }
			document.searchform.molstructure.value=molStructure;
		}, function(error) {
			alert("Cannot retrieve sketcher instance from iframe:" + error);
		});
	} 
	
	
		document.searchform.searchOption.value=option;
		document.searchform.action = "structureSearch.htm";
		document.searchform.submit();
}