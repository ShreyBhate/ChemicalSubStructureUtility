package com.spring.controller;


import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.node.ObjectNode;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.spring.dao.ManagerDao;
import com.spring.form.QueryDetailsBean;
import com.spring.form.searchform;
import com.spring.service.ManagerService;
import com.spring.vo.ArticleOccuranceCountVo;
import com.spring.vo.ArticleVo;

import chemaxon.formats.MolExporter;
import chemaxon.formats.MolImporter;
import chemaxon.jep.function.In;
import chemaxon.struc.Molecule;



@RestController
public class ManagerController {
	
	Logger logger=Logger.getLogger(ManagerDao.class);
	
	@Autowired
	 ServletContext context;
	
	@Autowired
	private ManagerService managerService;	
	
	//192.168.10.7:8080/bioin/searchapi/headers?sheetName=mainSheet
		@GetMapping( value="/headers", produces = { MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<String> getHeaders() {
			
			String headerdetail = "\"int\":\"This is it\"";
			return new ResponseEntity<String>(headerdetail, HttpStatus.OK);
		}
	
		
		@RequestMapping( value="/smiles",  produces = { MediaType.APPLICATION_JSON_VALUE }, method=RequestMethod.POST)
		public ResponseEntity<String> structureSearchSMILES(ModelMap modelMap,HttpServletRequest request, @RequestBody String query){
			String tableData="";
	    	try {
	    	
	    		query = query.split("=")[1];
//	    		System.out.println(filterName+"-"+searchOption+"-"+ name+"-"+smilesName);
	    		 tableData=managerService.structureSearchSmiles(query); 
	    		
	        	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm");

			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return new ResponseEntity<String>(tableData, HttpStatus.OK);
		}
		
		@RequestMapping( value="/name",  produces={MediaType.APPLICATION_JSON_VALUE }, method=RequestMethod.POST)
		public ResponseEntity<String> structureSearchNAME(ModelMap modelMap,HttpServletRequest request, @RequestBody String query){
	    	
			String tableData="";
	    	try {
	        	String name=request.getParameter("name");
	        	System.out.println(name+"-----------------");
	    		System.out.println(query);
	    		
//	    		System.out.println(filterName+"-"+searchOption+"-"+ name+"-"+smilesName);
	    		System.out.println(query);
	    		query = query.split("=")[1];
	    		 tableData=managerService.structureSearchName(query); 
	    		
	        	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm");

			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    	
	    	return new ResponseEntity<String>(tableData, HttpStatus.OK);
		}
		
		
		
		@RequestMapping( value="/substructure",  produces={MediaType.APPLICATION_JSON_VALUE }, method=RequestMethod.POST)
	    public ResponseEntity<String> structureSearchSTRUCTURE(ModelMap modelMap,HttpServletRequest request, @RequestBody String query){   	
	     String tableData="";
	    
	    	try {
	    	
	    		query = query.split("=")[1];
//	    		System.out.println(filterName+"-"+searchOption+"-"+ name+"-"+smilesName);
	    		tableData=managerService.structureSearchStructure(query); 
	    		System.out.println(query);
	    	
	        	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm----->"+query);

			} catch (Exception e) {
				e.printStackTrace();
			}

	    	return new ResponseEntity<String>(tableData, HttpStatus.OK);
	    }	
	  
		@RequestMapping( value="/substructure/search",  produces={MediaType.APPLICATION_JSON_VALUE }, method=RequestMethod.POST)
	    public ResponseEntity<Map<String, Object>> structureSearchSTRUCTURESearch(ModelMap modelMap,HttpServletRequest request, @RequestBody String query){   	
			Map<String, Object> result = new HashMap<>();
	    	try {
	    		System.out.println(query+"))))))))))))))))))))))))))");
	    		query = query.replace("query=", "").trim();
//	    		System.out.println(filterName+"-"+searchOption+"-"+ name+"-"+smilesName);
	    		System.out.println("*******************************"+query);
	    		QueryDetailsBean queryDetailsBean = managerService.structureSearchStructureSearch(query); 

	        	result.put("queryId", queryDetailsBean.getQueryId());
	        	result.put("docCount", queryDetailsBean.getDocCount());
	        	
	        	context.setAttribute(queryDetailsBean.getQueryId(), queryDetailsBean);
	        	
	    		System.out.println(query);
	    	
	        	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm----->"+query);

			} catch (Exception e) {
				e.printStackTrace();
			}

	    	return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	    }	
		
		
		@RequestMapping( value="/substructure/result",  produces={MediaType.APPLICATION_JSON_VALUE }, method=RequestMethod.POST)
	    public ResponseEntity<List<String>> structureSearchSTRUCTUREResult(ModelMap modelMap,HttpServletRequest request, @RequestBody String query){   	
			JSONArray result = new JSONArray();
	    	try {
	    		int start = Integer.parseInt(query.split("&")[1].replace("start=", "").trim());
	    		
	    		
		    	int offset = Integer.parseInt(query.split("&")[2].replace("end=", "").trim());
		    	
	    		query = query.split("&")[0].replace("query=", "").trim();
//	    		System.out.println(filterName+"-"+searchOption+"-"+ name+"-"+smilesName);
	    		System.out.println("*******************************"+query);
	    		result = managerService.getIdsForQueryId(query, start, offset);

	        	System.out.println(query);
	    	
	        	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm----->"+query);

			} catch (Exception e) {
				e.printStackTrace();
			}

	    	return new ResponseEntity<List<String>>(result, HttpStatus.OK);
	    }	
	  
	
		
	
		@RequestMapping( value="/substructure/details",  produces={MediaType.APPLICATION_JSON_VALUE }, method=RequestMethod.POST)
	    public ResponseEntity<String> structureSearchSTRUCTUREDetails(ModelMap modelMap,HttpServletRequest request, @RequestBody String query){   	
	    	
			System.out.println(query);
			String id = query.replace("query=docId", "");
			JSONArray details = managerService.getDocumentById(id);
		
			String jsonObject = details.toString();
			return new ResponseEntity<String>(jsonObject, HttpStatus.OK);
			
	    }
		
	
	@RequestMapping(value="welcome.htm")
	public String welcome(ModelMap modelMap){
		modelMap.addAttribute("message","welcome");
		return "welcome";
	}
	
    @RequestMapping(value="name.htm" , method=RequestMethod.POST)
	public String name(ModelMap modelMap,HttpServletRequest request){		
    	List<Map<Integer,String>> nameList=new ArrayList<Map<Integer,String>>();
    	String name=request.getParameter("nameId");
    	System.out.println("name = "+name);
    	nameList=managerService.searchName(name); 
//    	List< ArticleOccuranceCountVo> mapping = managerService.getNameLocation(name);
    	modelMap.addAttribute("nameList",nameList);
//    	modelMap.addAttribute("pdfDetails",mapping);
    	if(nameList.size()==0){
    		modelMap.addAttribute("message","Name not found");
    	}
    	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> name.htm");
    	return "welcome";    	
    }
    
    @RequestMapping(value="getStructure.htm" , method=RequestMethod.POST)
    @ResponseBody
	public String getStructure(ModelMap modelMap,HttpServletRequest request){		
    	String stucture="";
    	String id=request.getParameter("id");
    	logger.info(" =========id=========== "+id);
    	stucture=managerService.getStructure(id); 
    	logger.info(" stucture action "+stucture);
    	modelMap.addAttribute("stucture",stucture); 
      	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> getStructure.htm");

    	return stucture;   
    }

  @RequestMapping(value="stuctureSearchDisplay.htm",method=RequestMethod.GET)
    public String stuctureSearchDisplay(ModelMap modelMap,HttpServletRequest request){
  	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> stuctureSearchDisplay.htm");

    	return "welcome";    	
    }
	
    @RequestMapping(value="structureSearch.htm",method=RequestMethod.POST)
    public ModelAndView structureSearch(ModelMap modelMap,HttpServletRequest request, @ModelAttribute searchform searchform){
    	
    	String molStructure=searchform.getMolstructure()==null?"":searchform.getMolstructure(); 
    	String filterName = searchform.getFilterName()==null?"":searchform.getFilterName();
    	String searchOption = searchform.getSearchOption()==null?"":searchform.getSearchOption();
    	String name = searchform.getName()==null?"":searchform.getName();
    	String smilesName = searchform.getSmilesName()==null?"":searchform.getSmilesName();
   	    String tableData="";
    	try {
    	
    		
//    		System.out.println(filterName+"-"+searchOption+"-"+ name+"-"+smilesName);
    		tableData=managerService.structureSearch(molStructure, modelMap, filterName, searchOption, name, smilesName); 
    		
        	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm");

		} catch (Exception e) {
			e.printStackTrace();
		}

    		
    
    	return new ModelAndView("welcome", "MODELMAP", modelMap);    	
    }	
  
 /* @RequestMapping (value="structureSearch.htm", method=RequestMethod.POST)
  @ResponseBody String structureSearch(@RequestParam String molStructure, ModelMap modelMap,HttpServletRequest request ){
 
  	List<Map<Integer,String>> nameList=new ArrayList<Map<Integer,String>>();
  	String molStructure=request.getParameter("molstructure"); 
  	List<ArticleVo> articleVoList = managerService.getWileyArticles();
 	    String smiles="";
 	    Map<String, Map<String, Integer>> result= new HashMap<String, Map<String, Integer>>();
 	    byte[] moldata=null;
 	   File destinationFile=null;
  	try {
  		Map<Integer, String>map = new HashMap<Integer, String>();
  		map.put(8, "Physalin H");
  		nameList.add(map);
  		map = new HashMap<>();
  		map.put(9, "5β,6β-Epoxyphysalin B");
  		nameList.add(map);
  		nameList=managerService.stuctureSearch(molStructure); 
  		result =managerService.calculateWeightage(nameList, articleVoList, modelMap); 
  		System.out.println(result.keySet()+"\n"+result.size());
  		modelMap.addAttribute("pdfCount",result);
      	logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$_________________________>>> structureSearch.htm");
      	 
//  	modelMap.addAttribute("nameList",nameList); 
      	if(result.size()==0){
      		modelMap.addAttribute("message","Structure not found");
      	}
      	 InputStream is = new ByteArrayInputStream(molStructure.getBytes());
         MolImporter importer = new MolImporter(is);    
         Molecule mol = importer.read();
         moldata = MolExporter.exportToBinFormat(mol, "png:w300,h300,b32,#00ffff00"); 
       
          destinationFile = new File("/chemaxonProjectImages/myTest.png");
           OutputStream out = new FileOutputStream(destinationFile);
             out.write(moldata);
             out.close();
  	
  	} catch (Exception e) {
		e.printStackTrace();
	}
  	
  	return "penta"+"###/chemaxonProjectImages/myTest.png";
  }	*/


}
