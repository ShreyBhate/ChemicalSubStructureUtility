package com.spring.service;
import org.apache.commons.codec.binary.Base64;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import static java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spring.dao.ManagerDao;
import com.spring.form.QueryDetailsBean;
import com.spring.vo.ArticleOccuranceCountVo;
import com.spring.vo.ArticleVo;
import com.spring.vo.StructureVo;
import static java.util.stream.Collectors.toMap;
import chemaxon.formats.MolConverter;
import chemaxon.formats.MolExporter;
import chemaxon.formats.MolImporter;
import chemaxon.naming.n2s.lex.data.Str;
import chemaxon.struc.Molecule;



@Service
public class ManagerService {
	
	@Autowired
	 ServletContext context;
	@Autowired
	ManagerDao managerDao;
	
	Logger logger=Logger.getLogger(ManagerService.class);

	public static final  int titleWeightage=2;
	public static final  int abstractWeightage=2;
	public static final  int introWeightage=1;
	public static final  int expWeightage=1;
	public static final  int resultsWeightage=1;
	public static final  int conclusionWeightage=2;
	public static final  int referenceWeightage=1;
	public static final  int fullTextWeightage=1;
	
	
	
	public List<Map<Integer,String>> searchName(String name) {
		logger.info("ChemaxonService searchName");		
		return managerDao.searchName(name, "") ;
	}


	public String getStructure(String id) {
		return managerDao.getStructure(id);
	}

	/*public List<Map<Integer,String>> stuctureSearch(String molStructure) throws Exception{
		String smile=getSmilesFromMol(molStructure);
		
		String fingerPrint[]= getFingerprints(smile);
		
		List<Map<Integer,String>> list = managerDao.stuctureSearch(smile,fingerPrint);
		List<ArticleOccuranceCountVo> articleOccuranceCountVoList = managerDao.getArticleswithStructure(list);
		String tableData = getOrderofArticle(articleOccuranceCountVoList);
		return managerDao.stuctureSearch(smile,fingerPrint);
	}
	*/
	public String structureSearchSmiles(String smiles){
		String fingerPrint[]= null;
		List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
		 fingerPrint = getFingerprints(smiles);
		 list = managerDao.structureSearch(smiles,fingerPrint, "");
//		 List<ArticleOccuranceCountVo> articleOccuranceCountVoList = managerDao.getArticleswithStructure(list);
		 List<ArticleVo>articleVos = managerDao.getWileyArticles();
		String jsonObject = calculateWeightageFromArticlesSMILES(list, articleVos); 
		return jsonObject;
	}
	
	public String structureSearchName(String name){
		String fingerPrint[]= null;
		List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
		list = managerDao.searchName(name, "");
//		 List<ArticleOccuranceCountVo> articleOccuranceCountVoList = managerDao.getArticleswithStructure(list);
		 List<ArticleVo>articleVos = managerDao.getWileyArticles();
		String jsonObject = calculateWeightageFromArticlesSMILES(list, articleVos); 
		return jsonObject;
	}
	
	public String structureSearchStructure(String base64){
		String fingerPrint[]= null;
		List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
		String smile;
		try {
			
//			String base64 = getBase64FromMol(molStructure);
			String mol = getMolFromBase64(base64);
			logger.info("Base64 ---->"+mol);
			smile = getSmilesFromMol(mol);
			fingerPrint = getFingerprints(smile);
			 list = managerDao.structureSearch(smile,fingerPrint, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
//		 List<ArticleOccuranceCountVo> articleOccuranceCountVoList = managerDao.getArticleswithStructure(list);
		 List<ArticleVo>articleVos = managerDao.getWileyArticles();
		String jsonObject = calculateWeightageFromArticlesSMILES(list, articleVos); 
		return jsonObject;
	}
	
	public JSONArray getIdsForQueryId(String queryId, int start, int offset) {
		List<String> phiIdsList = new ArrayList<>();
		QueryDetailsBean queryDetailsBean = (QueryDetailsBean) context.getAttribute(queryId);
		String result = "";
		String searchType = "query";
		
		String query = queryDetailsBean.getQuery();
		String fingerPrint[]= null;
		List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
		String smile;

		try {
			
			byte[] mol = Base64.decodeBase64(query.getBytes());
			logger.info("Base64 ---->"+mol);
			smile = getSmilesFromMol(new String(mol));
			fingerPrint = getFingerprints(smile);
			 list = managerDao.structureSearch(smile,fingerPrint, "");
			 
				
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		 List<ArticleVo>articleVos = managerDao.getWileyArticles();

		 JSONArray finalList  = calculateWeightageFromArticlesStructureOffsets(list, articleVos, start, offset); 
		return finalList;
		
	}
	
	
	public QueryDetailsBean structureSearchStructureSearch(String base64){
		logger.info("structureSearchStructureSearch ----"+base64);
		String fingerPrint[]= null;int counter=0;
		List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
		String smile;
		try {
//			base64="DQogIE1KMTcyNDAwICAgICAgICAgICAgICAgICAgICAgIA0KDQogIDIgIDEgIDAgIDAgIDAgIDAgIDAgIDAgIDAgIDA5OTkgVjIwMDANCiAgIC0wLjQ0NjQgICAgMC4zNzk0ICAgIDAuMDAwMCBDICAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMCAgMA0KICAgLTEuMjcxNCAgICAwLjM3OTQgICAgMC4wMDAwIEggICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwICAwDQogIDEgIDIgIDEgIDAgIDAgIDAgIDANCk0gIEVORA0K";
//			byte[] mol = Base64.getUrlDecoder().decode(new String(base64));
//			String mol = getMolFromBase64(base64.trim());
			byte[] mol =Base64.decodeBase64(base64.getBytes());
//			byte[] mol = Base64.getDecoder().decode(new String(base64));
			logger.info("Base64 ---->"+new String(mol)+"----"+base64);
			smile = getSmilesFromMol(new String(mol));
			fingerPrint = getFingerprints(smile);
//			list = managerDao.structureSearch(smile,fingerPrint, "");
			counter = managerDao.structureSearchCount(smile, fingerPrint, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String uuid = UUID.randomUUID().toString();
    	QueryDetailsBean queryDetailsBean =new QueryDetailsBean(uuid, base64);
    	queryDetailsBean.setDocCount(counter);
		
		return queryDetailsBean;
	}
	


	public String structureSearch(String molStructure, ModelMap modelMap, String filterName, String searchOption, String name, String smilesName)  throws Exception{
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+molStructure.length());
		String smile=getSmilesFromMol(molStructure);
		String fingerPrint[]= null;
		List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
		
		if(searchOption.equals("structure")){
			smile=getSmilesFromMol(molStructure);
			 fingerPrint = getFingerprints(smile);
			 list = managerDao.structureSearch(smile,fingerPrint, filterName);
		}else if(searchOption.equals("name")){
			list = managerDao.searchName(name, filterName);
		}else if(searchOption.equals("smiles")){
			 smile=smilesName;
			 fingerPrint= getFingerprints(smile);
			 list = managerDao.structureSearch(smile,fingerPrint, filterName);
		}
		System.out.println(molStructure.length()+"******************************"+smile);
//		List<Map<Integer,String>> list = managerDao.structureSearch(smile,fingerPrint, filterName);
		List<ArticleOccuranceCountVo> articleOccuranceCountVoList = managerDao.getArticleswithStructure(list);
		List<ArticleVo>articleVos = managerDao.getWileyArticles();
		calculateWeightageFromArticles(list, articleVos, modelMap, molStructure, filterName, searchOption); 
		modelMap.addAttribute("filterName", filterName);
		modelMap.addAttribute("structure", molStructure);
		
		
		return "";
	}
	
	
	//
	private String getOrderofArticle(List<ArticleOccuranceCountVo> articleOccuranceCountVoList, ModelMap modelMap) {
		Map<String, Integer>sortMap= new HashMap<String, Integer>();
		Map<String, Map<String, String>>resultSortMap =new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>>mapping =new HashMap<String, Map<String, String>>();
		Map<String, String>map= new HashMap<String, String>(); int count =0; String text="";
		Map<String,Map<String, String>>otherStructures = new HashMap<String,Map<String, String>>();
		try{
			
			for(ArticleOccuranceCountVo vo :articleOccuranceCountVoList){
				count=0; text="";
				count = (vo.getABSTRACT()*abstractWeightage)+(vo.getTITLE()*titleWeightage)+(vo.getINTRODUCTION()*introWeightage)+(vo.getEXPERIMENTAL_SECTION()*expWeightage)+(vo.getRESULTS_DISCUSSION()*resultsWeightage)+(vo.getCONCLUSION()*conclusionWeightage)+(vo.getREFERENCE()*referenceWeightage);
				if(vo.getTITLE()>0)text+= " Ti("+vo.getTITLE()+")";
				if(vo.getABSTRACT()>0)text+= " Abs("+vo.getABSTRACT()+")";
				if(vo.getINTRODUCTION()>0)text+= " Intro("+vo.getINTRODUCTION()+")";
				if(vo.getEXPERIMENTAL_SECTION()>0)text+= " Exp("+vo.getEXPERIMENTAL_SECTION()+")";
				if(vo.getRESULTS_DISCUSSION()>0)text+= " Res("+vo.getRESULTS_DISCUSSION()+")";
				if(vo.getCONCLUSION()>0)text+= " Con("+vo.getCONCLUSION()+")";
				if(vo.getREFERENCE()>0)text+= " Ref("+vo.getREFERENCE()+")";
				
				
				if(!text.trim().equals("")){
				text = count +" =>{"+text+"}";
				String filePath = fetchStructureImage(vo.getNAME(), vo.getDOI());
				String title = managerDao.getTitleFromDoi(vo.getDOI());
				Map<String, String> structures =fetchStructuresForDoi(vo.getNAME(), vo.getDOI());
				otherStructures.put(vo.getDOI(), structures);
				String key = vo.getDOI()+":::"+title+":::"+filePath;
				if(mapping.containsKey(key)){
					map = mapping .get(key);
					map.put(vo.getNAME(), text);
					mapping.put(key, map);
					
				}else{
					map = new HashMap<String, String>();
					map.put(vo.getNAME(), text);
					mapping.put(key, map);
				}
				}
			}
			
		}catch (Exception e) {
			System.out.println();

		}
		
		
		for(String key: mapping.keySet()){
			map = mapping.get(key);
			Map<String, String> sortedMap = map.entrySet().stream()
					 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	                .collect(toMap(Entry::getKey, Entry::getValue,
	                         (e1,e2) -> e1, LinkedHashMap::new));
			resultSortMap.put(key,sortedMap);
			for(String art: map.keySet()){
				int counter = Integer.parseInt(map.get(art).split(" =>")[0].trim());
				
				sortMap.put(key, counter);
			}
			
		}
		
		Map<String, Integer> sortedKeysMap = sortMap.entrySet().stream()
				 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	           .collect(toMap(Entry::getKey, Entry::getValue,
	                    (e1,e2) -> e1, LinkedHashMap::new));
		
		modelMap.put("tableData", resultSortMap);
		modelMap.put("sortMap", sortMap);
		modelMap.put("otherStructures", otherStructures);
		logger.info(resultSortMap.entrySet());
		logger.info(sortMap.entrySet());
//			String tableData = createTableStructure(mapping);
		return "";
	}


	private String createTableStructure(Map<String, Map<String, String>> mapping) {
	
		StringBuffer sb = new StringBuffer();
		Map<String, String>map = new HashMap<String, String>();
		try{
			sb.append("<table border='1'>");
			for(String doi: mapping.keySet()){
				System.out.println(doi);
				map = mapping.get(doi);
				for(String name:map.keySet()){
					String textCount = map.get(name);
					textCount= textCount.replace("###", "(");
					textCount = textCount+")";
				
							
				String filePath = fetchStructureImage(name, doi.split(":::")[0]);
				sb.append("<tr><td>"+doi.split(":::")[0]+"</td><td>"+doi.split(":::")[1]+"</td><td>"+textCount+"</td></tr>");
				sb.append("<tr><td colspan='3'><div><img id='img_id' src='/chemaxonProjectImages/"+filePath+"' height='64px' width='64px' /></div></td></tr>");
				
			}
			}
			sb.append("</table>");
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public String convertStructuretoImage(String structure, String name){
		 FileOutputStream fos=null;
		byte[] moldata=null; 
		String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/"+name.replace(" ", "_")+".png";
		try{
			
			 InputStream is = new ByteArrayInputStream(structure.getBytes());
	         MolImporter importer = new MolImporter(is);    
	         Molecule mol = importer.read();
	         moldata = MolExporter.exportToBinFormat(mol, "png:w300,h300,b32,#00ffff00"); 
	          fos = new FileOutputStream(path);
	          
	         fos.write(moldata);
	         System.out.println("file written at "+path);
	         fos.close();
	          
	          
		}catch(Exception e ){
			e.printStackTrace();
		}
		
		
		return name.replace(" ", "_")+".png";
	}
	
	public String fetchStructureImage(String name, String doi){
		 FileOutputStream fos=null;
		byte[] moldata=null; 
		String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/"+doi+"_"+name.replace(" ", "_")+".png";
		try{
			String molStructure = managerDao.getStructureFromName(name);
			 InputStream is = new ByteArrayInputStream(molStructure.getBytes());
	         MolImporter importer = new MolImporter(is);    
	         Molecule mol = importer.read();
	         moldata = MolExporter.exportToBinFormat(mol, "png:w300,h300,b32,#00ffff00"); 
	          fos = new FileOutputStream(path);
	          
	         fos.write(moldata);
	         System.out.println("file written at "+path);
	         fos.close();
	          
	          
		}catch(Exception e ){
			e.printStackTrace();
		}
		
		System.out.println(doi+"_"+name.replace(" ", "_")+".png");
		return doi+"_"+name.replace(" ", "_")+".png";
	}
	
	public String fetchStructureImageWithHighLight(String name, String doi, String sourceString){ 
		String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/"+doi+"_"+name.replace(" ", "_")+".png";

		try{
			
			String molStructure = managerDao.getStructureFromName(name);
			   highlightSubStructure(molStructure, sourceString, path, name);
			          
		}catch(Exception e ){
			e.printStackTrace();
		}
		
		System.out.println(doi+"_"+name.replace(" ", "_")+".png");
		return doi+"_"+name.replace(" ", "_")+".png";
	}

	
	private void highlightSubStructure(String target, String sourceString, String path, String name) {
		String smart="";
		try {
			String sdf = path.replace(".png", ".sdf");
			
//			System.out.println(target);
			smart = getSmartsFromMol(sourceString);
			String smilesmart = getSmilesFromMol(sourceString);
//			smart = getSmilesFromMol(sourceString);
			
			logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+smilesmart+"\n"+smart+"\n--------------");
			/*String targetSmiles = getSmilesFromMol(target);
			
				
			String[] comm = {"obabel", "-:"+targetSmiles.trim()+"", "-O",  ""+sdf.trim()+"",  "--gen3D"};
			try{
			ProcessBuilder build = new ProcessBuilder( comm );
			Process process =  build.start();
			 String outputString=output(process.getInputStream());
			 logger.info("**************************1**************************"+build.command());
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("ERRORRRRR");
			}
			//"obabel -:"CN1C=NC2=C1C(=O)N(C(=O)N2C)C Caffeine" -O out.svg";
			
			
			
		
			System.out.println(">>>"+sourceString.length()+"|||=>"+smart.trim() +"----->"+targetSmiles.trim()+"---"+path.trim());
			System.out.println(sdf);		

			try{
			String[] command = {"obabel",  ""+sdf.trim()+"", "-O",  ""+path.trim()+"", "-s", ""+smart.trim()+" blue", "-xw 300", "-xh 300", "-d"};
			ProcessBuilder builder = new ProcessBuilder( command );
			Process process2 = builder.start();
			 String outputString2=output(process2.getInputStream());
			
			 logger.info(outputString2+"**************************2**************************"+builder.command());
			}catch(Exception e){
				e.printStackTrace();
				
			}*/
			
			
			
			
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}


	public Map<String, String> fetchStructuresForDoi(String name, String doi){
		 FileOutputStream fos=null;
		 Map<String, String>structures = new HashMap<String, String>();
		 Map<String, String>structurePath =  new HashMap<String, String>();
		 
		byte[] moldata=null; 
		
		int counter=0;
		try{
			 structures = managerDao.getStructureForDoiExceptName(name, doi);
//			 System.out.println("******************"+structures.size());
			 for(String key: structures.keySet()){
				 String molStructure= structures.get(key);
				 counter++;
				 InputStream is = new ByteArrayInputStream(molStructure.getBytes());
	         	MolImporter importer = new MolImporter(is);    
	         	Molecule mol = importer.read();
	         	moldata = MolExporter.exportToBinFormat(mol, "png:w300,h300,b32,#00ffff00"); 
	         	String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/"+doi.trim()+"_"+counter+".png";
	         	fos = new FileOutputStream(path);
	         
	          structurePath.put(key, doi.trim()+"_"+counter+".png");
	          fos.write(moldata);
//	         	logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+doi.trim()+"_"+counter+".png");
	         	fos.close();
	         	
			 }
	          
		}catch(Exception e ){
			e.printStackTrace();
		}
		
//		System.out.println(doi+"_"+name.replace(" ", "_")+".png");
//		String pathName =  doi+"_"+name.replace(" ", "_")+".png";
		 return structurePath;
	}

	
	public JSONArray  fetchAllStructuresForDoiBase64(String name, String doi){
		 FileOutputStream fos=null;
		 Map<String, String>structures = new HashMap<String, String>();
		 Map<String, String>structurePath =  new HashMap<String, String>();
		 JSONArray array = new JSONArray();
		 JSONObject obj = new JSONObject();
		byte[] moldata=null; 
		
		int counter=1;
		try{
			 structures = managerDao.getStructureForDoiExceptName(name, doi);
//			 System.out.println("******************"+structures.size());
			 for(String key: structures.keySet()){
				 String molStructure= structures.get(key);
				 counter++;
				 InputStream is = new ByteArrayInputStream(molStructure.getBytes());
	         	MolImporter importer = new MolImporter(is);    
	         	Molecule mol = importer.read();
	         	moldata = MolExporter.exportToBinFormat(mol, "png:w300,h300,b32,#00ffff00"); 
	         	String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/"+doi.trim()+"_"+counter+".png";
	         	fos = new FileOutputStream(path);
	         
	          structurePath.put(key, doi.trim()+"_"+counter+".png");
	          fos.write(moldata);
//	         	logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+doi.trim()+"_"+counter+".png");
	         	fos.close();
	         	
	         	
	         	File f =  new File(path);
	            String encodstring = encodeFileToBase64Binary(f);
	            encodstring ="data:image/png;base64,"+encodstring;
	            JSONArray arr = new JSONArray();
	            obj = new JSONObject();
	            structurePath.put(key, encodstring);
	            obj.put("structure", encodstring);
	            obj.put("name", key);
	            obj.put("sid", counter);
	            array.add(obj);
			 }
	         
		}catch(Exception e ){
			e.printStackTrace();
		}
		
//		System.out.println(doi+"_"+name.replace(" ", "_")+".png");
//		String pathName =  doi+"_"+name.replace(" ", "_")+".png";
		 return array;
	}


	public static String doFunc(String[] args) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(args[0].getBytes());
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		MolConverter mc=null;
		try {
			 mc = new MolConverter(bis, bout, args[1], false);
			mc.convert();
		} catch (Exception e) {
			System.out.println(args[0]);

		}
		 mc.close();

		return new String(bout.toByteArray());
	}

	
	
	
	
	public String getSmilesFromMol(String mol) throws Exception {
		String smiles = null;
		
		String[] strArr = { mol, "smiles" };
		return smiles = doFunc(strArr);


	}
	
	
	public String getBase64FromMol(String mol) throws Exception {
		String smiles = null;
		
		String[] strArr = { mol, "base64" };
		smiles = doFunc(strArr);
		System.out.println("Generation--getBase64FromMol------------>>>>"+smiles);
		return smiles;


	}
	
	public String getMolFromBase64(String base64) throws Exception {
		String smiles = null;
		
		String[] strArr = { base64, "mol" };
		smiles = doFunc(strArr);
		System.out.println("Generation-------------->>>>"+smiles);
		return smiles ;


	}
	
	
	public static String getSmartsFromMol(String mol) throws Exception {
		String smiles = null;
		String[] strArr = { mol, "smarts" };
		smiles = doFunc(strArr);
		System.out.println("--||||||||SMARTS||||||||||||-->"+smiles);
		return smiles;


	}
	
	
	
	public static String getStructureFromName(String mol) throws Exception {
		String smiles = null;
		
		String[] strArr = { mol, "smiles" };
		smiles = doFunc(strArr);
		
		System.out.println("---->"+smiles);
		return smiles;


	}
	
	
	
	public String[] getFingerprints(String smile){
		System.out.println(smile);
	String[] command = {"obabel", "-:"+smile.trim()+"", "-ofpt"};
    ProcessBuilder builder = new ProcessBuilder( command );
    String fingerPrint[]= null;
	try{
	
		
		Process process = builder.start();
		String outputString=output(process.getInputStream());
		String hexArray[]=outputString.split("(?<=\\G.{16})");
		fingerPrint =new String[hexArray.length];
		int i=0;
		for(String hex:hexArray){
		
			fingerPrint[i]= hexToDec(hex).toString();
			System.out.println("fingerprint->"+fingerPrint[i]);
			i++;
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return fingerPrint;
	
}
	
public  Number hexToDec(String hex)  {
	   if (hex == null) {
	      throw new NullPointerException("hexToDec: hex String is null.");
	   }

	   // You may want to do something different with the empty string.
	   if (hex.equals("")) { return Byte.valueOf("0"); }

	   // If you want to pad "FFF" to "0FFF" do it here.

	   hex = hex.toUpperCase();

	   // Check if high bit is set.
	   boolean isNegative =
	      hex.startsWith("8") || hex.startsWith("9") ||
	      hex.startsWith("A") || hex.startsWith("B") ||
	      hex.startsWith("C") || hex.startsWith("D") ||
	      hex.startsWith("E") || hex.startsWith("F");

	   BigInteger temp;

	   if (isNegative) {
	      // Negative number
	      temp = new BigInteger(hex, 16);
	      BigInteger subtrahend = BigInteger.ONE.shiftLeft(hex.length() * 4);
	      temp = temp.subtract(subtrahend);
	   } else {
	      // Positive number
	      temp = new BigInteger(hex, 16);
	   }

	   // Cut BigInteger down to size.
	   if (hex.length() <= 2) { return (Byte)temp.byteValue(); }
	   if (hex.length() <= 4) { return (Short)temp.shortValue(); }
	   if (hex.length() <= 8) { return (Integer)temp.intValue(); }
	   if (hex.length() <= 16) { return (Long)temp.longValue(); }
	   return temp;
	}
 public String output(InputStream inputStream) throws IOException {
	StringBuilder sb = new StringBuilder();
	BufferedReader br = null;
	try {
		br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		
		line = br.readLine();
		while ((line = br.readLine()) != null) {
			
			sb.append(line.replace(" ","") );
		}
	} finally {
		br.close();
	}
	return sb.toString();
}


public static void main (String [] args){
	String smile="CCC";
	try {
		String smart = ManagerService.getSmartsFromMol(smile);
		System.out.println(smart);
		String target="C1=CC=C2C(=C1)C=CC=NN2";
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	 /* SmilesParser smilesParser = null;
      String smiles = "c1cc(CC=CC#N)ccn1";
      try {
    	  smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
//    	  smilesParser = new SmilesParser(ChemObjectBuilder.getInstance());
		IAtomContainer molecule = smilesParser.parseSmiles(smiles);
		
		System.out.println(molecule);
	} catch (InvalidSmilesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
     /* molecule = Misc.get2DCoords(molecule);
      Renderer2DPanel rendererPanel = new Renderer2DPanel(molecule, 200, 200);
      rendererPanel.setName("rendererPanel");
      JFrame frame = new JFrame("2D Structure Viewer");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.getContentPane().add(rendererPanel);
      frame.setSize(200, 200);
      frame.setVisible(true);*/
	    
}


/*private static void highlightSubstructure(String smile, String target) {
//	obabel benzodiazepine.sdf.gz -O out.svg --filter "title=3016"
//    -s "c1ccc2c(c1)C(=NCCN2)c3ccccc3 red" -xu -d

	ManagerDao managerDao = new ManagerDao();
	
	
	String[] comm = {"obabel", "-:"+target+"", "-O", "/home/jenita.kn/Desktop/benzene.sdf",  "--gen3D"};
	
	ProcessBuilder build = new ProcessBuilder( comm );
	
	try{
		Process process = build.start();
		process.getOutputStream();
		System.out.println(process);
	}catch(Exception e){
		e.printStackTrace();
	}
	String[] command = {"obabel", "/home/jenita.kn/Desktop/benzene.sdf", "-O","/home/jenita.kn/Desktop/outer.png", "-s", ""+smile+" red", "-xu", "-d"};

    ProcessBuilder builder = new ProcessBuilder( command );
   
	try{
	
		
		Process process = builder.start();
		process.getOutputStream();
		System.out.println(process);
	}catch(Exception e){
		e.printStackTrace();
	}
	

	ManagerDao managerDao = new ManagerDao();
String queryMol = managerDao.getStructureFromName("Physalin H");
	String targetMol = managerDao.getStructureFromName("Physalin H");
	MaxCommonSubstructure mcs = MaxCommonSubstructure.newInstance();
    mcs.setMolecules(queryMol, targetMol);
    McsSearchResult result = mcs.nextResult();
    System.out.println("Atoms in MCS: " + result.getAtomCount());
    System.out.println("Bonds in MCS: " + result.getBondCount());
    System.out.println("MCS molecule: "
            + MolExporter.exportToFormat(result.getAsMolecule(), "smiles"));
    
	
	
	
}*/
public List<ArticleVo> getWileyArticles() {
	return managerDao.getWileyArticles();
}


public Map<String, Map<String, Integer>> calculateWeightage(List<Map<Integer, String>> termList, List<ArticleVo> articleVoList, ModelMap modelMap) {

	/*int titleVal=0;
	int abstractVal=0;
	int introVal=0;
	int expVal=0;
	int resVal=0;
	int conclusionVal=0;
	int refVal=0;*/
	String name=""; int id=0;

	Map<String, Map<String, Integer>> resultMap = new HashMap<String, Map<String,  Integer>>();
	Map<String, Map<String, Integer>> resultSortedMap = new HashMap<String, Map<String,  Integer>>();
	Map<String, Integer>sortMap = new HashMap<String, Integer>();
	Map<String, Integer>articleCountMap = new HashMap<String, Integer>();
	Set<String>  notFoundinArticles= new HashSet<String>();
	System.out.println(articleVoList.size()+"---"+termList.size());
	int counter =0;
	for(Map<Integer, String>map:termList){
		counter = counter+1;
	
		for(Map.Entry<Integer, String> m:map.entrySet()){
			System.out.println(counter+"---"+name);		
		name =m.getValue();
		id= m.getKey();
//		name = name.replaceAll("-",	"\\-");
		
			for(ArticleVo articleVo : articleVoList){
				int titleVal=0;
				int abstractVal=0;
				int introVal=0;
				int expVal=0;
				int resVal=0;
				int conclusionVal=0;
				int refVal=0;
				
				
				titleVal = countOccurances(articleVo.getTITLE(), name);
				abstractVal = countOccurances(articleVo.getABSTRACT(), name);
				introVal= countOccurances(articleVo.getINTRODUCTION(), name);
				expVal = countOccurances(articleVo.getEXPERIMENTAL_SECTION(), name);
				resVal= countOccurances(articleVo.getRESULTS_DISCUSSION(), name);
				conclusionVal= countOccurances(articleVo.getCONCLUSION(), name);
				refVal = countOccurances(articleVo.getREFERENCE(), name);
				
				
				// calculate the score
				int count = titleVal+abstractVal+introVal+expVal+resVal+conclusionVal+refVal;
				
				String key = id+"###"+name;
				if(count>0)
					System.out.println(key+"<---->"+articleVo.getDOI()+"-----"+count+">>>>>>--->"+titleVal+"-"+abstractVal+"-"+introVal+"-"+expVal+"-"+resVal+"-"+conclusionVal+"-"+refVal);
				if(resultMap.containsKey(key) ){
					
					if(count>0){
						articleCountMap = resultMap.get(key);
						articleCountMap.put(articleVo.getDOI(), count);
						resultMap.put(key, articleCountMap);
					}else{
						notFoundinArticles.add(key);
						
					}
					
				}else{
					
					
					if(count>0){
						articleCountMap = new HashMap<String, Integer>();
						articleCountMap.put(articleVo.getDOI(), count);
						resultMap.put(key, articleCountMap);
					}else{
						notFoundinArticles.add(key);
						
					}
					
					

				}
				
//				
				
			}
			
		}
	}
	
	
	int articleCount=0;
	for(String key: resultMap.keySet()){
		articleCountMap = resultMap.get(key);
		Map<String, Integer> sortedMap = articleCountMap.entrySet().stream()
				 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(toMap(Entry::getKey, Entry::getValue,
                         (e1,e2) -> e1, LinkedHashMap::new));
		resultSortedMap.put(key,sortedMap);
		int artCount=0;
		for(String art: articleCountMap.keySet()){
			artCount += articleCountMap.get(art);
			sortMap.put(key, artCount);
		}
		
	}
	
	Map<String, Integer> sortedKeysMap = sortMap.entrySet().stream()
			 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
           .collect(toMap(Entry::getKey, Entry::getValue,
                    (e1,e2) -> e1, LinkedHashMap::new));
	
	
	
	
	System.out.println("withn the method"+sortedKeysMap.keySet());
	modelMap.addAttribute("pdfCount",resultSortedMap);
	modelMap.addAttribute("notFound",notFoundinArticles);
	modelMap.addAttribute("sortMap", sortedKeysMap.keySet());
	return resultSortedMap;
}

private String calculateWeightageFromArticlesSMILES(List<Map<Integer, String>> termList, List<ArticleVo> articleVoList) {
	String name=""; int id=0; JSONArray array = new JSONArray(); int countt=0;
	Map<String,  ArticleVo> articleVoMap = new HashMap<String,  ArticleVo>();
	Map<String,  StructureVo> structureVoMap = new HashMap<String,  StructureVo>(); 
	Map<String, String>finalMapping = new HashMap<String, String>();
	System.out.println(articleVoList.size()+"---"+termList.size());
	int counter =0;int count=0; String text="";
	Map<String, Integer>countMap = new HashMap<String, Integer>();
	Map<String,Map<String, String>>otherServices = new HashMap<String, Map<String, String>>();
	
	for(Map<Integer, String>map:termList){
		counter = counter+1;
		
		for(Map.Entry<Integer, String> m:map.entrySet()){
			System.out.println(counter+"---"+name);		
		name =m.getValue();
		id= m.getKey();
			for(ArticleVo vo : articleVoList){
				
				// article highlight
				articleVoMap.put(vo.getDOI(), vo);
				
				String titleText = vo.getTITLE().replace("\n", "");
				String abstrText = vo.getABSTRACT().replace("\n", "");
				Pattern pat = Pattern.compile(name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-") , Pattern.CASE_INSENSITIVE);
				Matcher mat = pat.matcher(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//				logger.info(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
				if(mat.find()){
//					logger.info(mat.group());					
					String mm = mat.group();
					mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					titleText=titleText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					
					String srt= managerDao.getStructure(String.valueOf(id));
					convertStructuretoImage(srt, name);
//					String mmTag ="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\" data-html=\"true\" data-content=\"<img src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\"  />"+mm+"</a>";
					String mmTag="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\"  data-html=\"true\" data-content=\"<img src='/home/jenita.kn/Desktop/chemaxonProjectFolder/"+name.replace(" ", "_")+".png'\" class=\"media-object\" /> >"+mm+"</a>";
//					String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img'  src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\" />"+mm+"</a>";

					titleText = titleText.replace(mm, "<mark >"+mm+"</mark>");
					vo.setTITLE(titleText);
					articleVoMap.put(vo.getDOI(), vo);
				}
				
				Matcher mat2 = pat.matcher(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//				logger.info(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
				if(mat2.find()){
//					logger.info(mat2.group());					
					String mm = mat2.group();
					mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					abstrText=abstrText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img' alt="+name+" src=\"/home/jenita.kn/Desktop/chemaxonProjectImages/"+name+".png\" />"+mm+"</a>";
					abstrText = abstrText.replace(mm, "<mark >"+mm+"</mark>");
					
					vo.setABSTRACT(abstrText);
					articleVoMap.put(vo.getDOI(), vo);
				}
				
				int titleVal=0;
				int abstractVal=0;
				int introVal=0;
				int expVal=0;
				int resVal=0;
				int conclusionVal=0;
				int refVal=0;
				
				
				titleVal = countOccurances(vo.getTITLE(), name);
				abstractVal = countOccurances(vo.getABSTRACT(), name);
				introVal= countOccurances(vo.getINTRODUCTION(), name);
				expVal = countOccurances(vo.getEXPERIMENTAL_SECTION(), name);
				resVal= countOccurances(vo.getRESULTS_DISCUSSION(), name);
				conclusionVal= countOccurances(vo.getCONCLUSION(), name);
				refVal = countOccurances(vo.getREFERENCE(), name);
				
				
				// calculate the score
				count=0; text="";
/*				count = (vo.getABSTRACT()*abstractWeightage)+(vo.getTITLE()*titleWeightage)+(vo.getINTRODUCTION()*introWeightage)+(vo.getEXPERIMENTAL_SECTION()*expWeightage)+(vo.getRESULTS_DISCUSSION()*resultsWeightage)+(vo.getCONCLUSION()*conclusionWeightage)+(vo.getREFERENCE()*referenceWeightage);
*/				if(titleVal>0){
					text+= " Ti("+titleVal+")";
					count = count+(titleVal*titleWeightage);
				}
					if (abstractVal > 0) {
						text += " Abs(" + abstractVal + ")";
						count = count + (abstractVal * abstractWeightage);
					}
					if (introVal > 0) {
						text += " Int(" + introVal + ")";
						count = count + (introVal * introWeightage);
					}
					if (expVal > 0) {
						text += " Exp(" + expVal + ")";
						count = count + (expVal * expWeightage);
					}
					if (resVal > 0) {
						text += " Res(" + resVal + ")";
						count = count + (resVal * resultsWeightage);
					}
					if (conclusionVal > 0) {
						text += " Con(" + conclusionVal + ")";
						count = count + (conclusionVal * conclusionWeightage);
					}
					if (refVal > 0) {
						text += " Ref(" + refVal + ")";
						count = count + (refVal * referenceWeightage);
					}
				
				
				if(!text.trim().equals("")){
					
//				text = count +" =>{"+text+"}";
//				text = "<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+text+"\" data-placement=\"bottom\">"+count+"</a>";
				
				JSONObject obj = new JSONObject();
				
				String str="";
				str= fetchStructureImage(name, vo.getDOI());
				String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/";
				File f =  new File(path+str);
	             String encodstring = encodeFileToBase64Binary(f);
	             logger.info("base64 --------------->"+encodstring);
	             
				String key = id+"###"+name+"###"+vo.getDOI()+"###"+str;
				if(count>0){
					countt++;
					obj = new JSONObject();
					System.out.println(key+"<---->"+vo.getDOI()+"-----"+count+">>>>>>--->"+titleVal+"-"+abstractVal+"-"+introVal+"-"+expVal+"-"+resVal+"-"+conclusionVal+"-"+refVal);
					finalMapping.put(key, text);
					countMap.put(key, count);
					Map<String, String> structures =fetchStructuresForDoi(name, vo.getDOI());
					otherServices.put(vo.getDOI(), structures);
					StructureVo structureVo =  managerDao.fetchStructureDetails(name);
					structureVoMap.put(name, structureVo);
				
					ArticleVo vvo = articleVoMap.get(vo.getDOI());

				      obj.put("ID", countt);
				      obj.put("DOI", vo.getDOI());
				      obj.put("Title", vvo.getTITLE());
				      obj.put("Abstract", vvo.getABSTRACT());
				      obj.put("Molecular Formula", structureVo.getMolecularFormula());
				      obj.put("Molecular Weight", structureVo.getMolecularWeight());
				      obj.put("Comments", structureVo.getComments());
				      obj.put("Score", count +"("+text+")");
				      obj.put("SubStructure", encodstring);
					 obj.put("Name", name);
				      array.add(obj); 
				}
					
					
			}
				
		}
	}
	
	}	

	
	Map<String, Integer> sortedMap = countMap.entrySet().stream()
			 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
           .collect(toMap(Entry::getKey, Entry::getValue,
                    (e1,e2) -> e1, LinkedHashMap::new));
	logger.info(otherServices.entrySet());

/*	modelMap.addAttribute("sortMap", sortedMap);
	modelMap.addAttribute("dataMap", finalMapping);
	modelMap.addAttribute("otherServices", otherServices);
	modelMap.addAttribute("structureVoMap", structureVoMap);
	modelMap.addAttribute("articles", articleVoMap);
	
	*/
	
	
	
	return array.toJSONString();
	
}
private static String encodeFileToBase64Binary(File file){
	 byte[] encodedfile = null;
    try {
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStreamReader.read(bytes);
        encodedfile =  Base64.encodeBase64(bytes);
        
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    return new String(encodedfile);
}

public String calculateWeightageFromArticles(List<Map<Integer, String>> termList, List<ArticleVo> articleVoList, ModelMap modelMap, String source, String filtername, String searchOption) {

	/*int titleVal=0;
	int abstractVal=0;
	int introVal=0;
	int expVal=0;
	int resVal=0;
	int conclusionVal=0;
	int refVal=0;*/
	String name=""; int id=0;
	Map<String,  ArticleVo> articleVoMap = new HashMap<String,  ArticleVo>();
	Map<String,  StructureVo> structureVoMap = new HashMap<String,  StructureVo>(); 
	Map<String, String>finalMapping = new HashMap<String, String>();
	Map<String, Map<String, Integer>> resultMap = new HashMap<String, Map<String,  Integer>>();
	Map<String, Map<String, Integer>> resultSortedMap = new HashMap<String, Map<String,  Integer>>();
	Map<String, Integer>sortMap = new HashMap<String, Integer>();
	Map<String, Integer>articleCountMap = new HashMap<String, Integer>();
	Set<String>  notFoundinArticles= new HashSet<String>();
	System.out.println(articleVoList.size()+"---"+termList.size());
	int counter =0;int count=0; String text="";
	Map<String, Integer>countMap = new HashMap<String, Integer>();
	Map<String,Map<String, String>>otherServices = new HashMap<String, Map<String, String>>();
	
	for(Map<Integer, String>map:termList){
		counter = counter+1;
		
		for(Map.Entry<Integer, String> m:map.entrySet()){
			System.out.println(counter+"---"+name);		
		name =m.getValue();
		id= m.getKey();
			for(ArticleVo vo : articleVoList){
				
				// article highlight
				articleVoMap.put(vo.getDOI(), vo);
				
				String titleText = vo.getTITLE().replace("\n", "");
				String abstrText = vo.getABSTRACT().replace("\n", "");
				Pattern pat = Pattern.compile(name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-") , Pattern.CASE_INSENSITIVE);
				Matcher mat = pat.matcher(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//				logger.info(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
				if(mat.find()){
//					logger.info(mat.group());					
					String mm = mat.group();
					mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					titleText=titleText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					
					String srt= managerDao.getStructure(String.valueOf(id));
					convertStructuretoImage(srt, name);
//					String mmTag ="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\" data-html=\"true\" data-content=\"<img src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\"  />"+mm+"</a>";
					String mmTag="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\"  data-html=\"true\" data-content=\"<img src='/home/jenita.kn/Desktop/chemaxonProjectFolder/"+name.replace(" ", "_")+".png'\" class=\"media-object\" /> >"+mm+"</a>";
//					String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img'  src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\" />"+mm+"</a>";

					titleText = titleText.replace(mm, "<mark >"+mm+"</mark>");
					vo.setTITLE(titleText);
					articleVoMap.put(vo.getDOI(), vo);
				}
				
				Matcher mat2 = pat.matcher(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//				logger.info(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
				if(mat2.find()){
//					logger.info(mat2.group());					
					String mm = mat2.group();
					mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					abstrText=abstrText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img' alt="+name+" src=\"/home/jenita.kn/Desktop/chemaxonProjectImages/"+name+".png\" />"+mm+"</a>";
					abstrText = abstrText.replace(mm, "<mark >"+mm+"</mark>");
					
					vo.setABSTRACT(abstrText);
					articleVoMap.put(vo.getDOI(), vo);
				}
				
				int titleVal=0;
				int abstractVal=0;
				int introVal=0;
				int expVal=0;
				int resVal=0;
				int conclusionVal=0;
				int refVal=0;
				int fullTextVal=0;
				
				titleVal = countOccurances(vo.getTITLE(), name);
				abstractVal = countOccurances(vo.getABSTRACT(), name);
				introVal= countOccurances(vo.getINTRODUCTION(), name);
				expVal = countOccurances(vo.getEXPERIMENTAL_SECTION(), name);
				resVal= countOccurances(vo.getRESULTS_DISCUSSION(), name);
				conclusionVal= countOccurances(vo.getCONCLUSION(), name);
				refVal = countOccurances(vo.getREFERENCE(), name);
				fullTextVal = countOccurances(vo.getFULLTEXT(), name);
				
				// calculate the score
				count=0; text="";
/*				count = (vo.getABSTRACT()*abstractWeightage)+(vo.getTITLE()*titleWeightage)+(vo.getINTRODUCTION()*introWeightage)+(vo.getEXPERIMENTAL_SECTION()*expWeightage)+(vo.getRESULTS_DISCUSSION()*resultsWeightage)+(vo.getCONCLUSION()*conclusionWeightage)+(vo.getREFERENCE()*referenceWeightage);
*/				if(titleVal>0){
					text+= " Ti("+titleVal+")";
					count = count+(titleVal*titleWeightage);
				}
					if (abstractVal > 0) {
						text += " Abs(" + abstractVal + ")";
						count = count + (abstractVal * abstractWeightage);
					}
					if (introVal > 0) {
						text += " Int(" + introVal + ")";
						count = count + (introVal * introWeightage);
					}
					if (expVal > 0) {
						text += " Exp(" + expVal + ")";
						count = count + (expVal * expWeightage);
					}
					if (resVal > 0) {
						text += " Res(" + resVal + ")";
						count = count + (resVal * resultsWeightage);
					}
					if (conclusionVal > 0) {
						text += " Con(" + conclusionVal + ")";
						count = count + (conclusionVal * conclusionWeightage);
					}
					if (refVal > 0) {
						text += " Ref(" + refVal + ")";
						count = count + (refVal * referenceWeightage);
					}
					if (fullTextVal > 0) {
						text += " Ref(" + fullTextVal + ")";
						count = count + (fullTextVal * referenceWeightage);
					}
				
				if(!text.trim().equals("")){
					
//				text = count +" =>{"+text+"}";
				text = "<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+text+"\" data-placement=\"bottom\">"+count+"</a>";
			//@working here	
				String str="";
				if(searchOption.equals("structure")){
//					str= fetchStructureImageWithHighLight(name, vo.getDOI(), source);
					str=vo.getDOI()+"_"+name.replace(" ", "_")+".png";
//					str= fetchStructureImage(name, vo.getDOI());

				}
				else  str= fetchStructureImage(name, vo.getDOI());
				String key = id+"###"+name+"###"+vo.getDOI()+"###"+str;
				if(count>0)
					System.out.println(key+"<---->"+vo.getDOI()+"-----"+count+">>>>>>--->"+titleVal+"-"+abstractVal+"-"+introVal+"-"+expVal+"-"+resVal+"-"+conclusionVal+"-"+refVal);
					finalMapping.put(key, text);
					countMap.put(key, count);
					Map<String, String> structures =fetchStructuresForDoi(name, vo.getDOI());
					otherServices.put(vo.getDOI(), structures);
					StructureVo structureVo =  managerDao.fetchStructureDetails(name);
					structureVoMap.put(name, structureVo);
			}
				
		}
	}
	
	}	

	
	Map<String, Integer> sortedMap = countMap.entrySet().stream()
			 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
           .collect(toMap(Entry::getKey, Entry::getValue,
                    (e1,e2) -> e1, LinkedHashMap::new));
	logger.info(otherServices.entrySet());

	modelMap.addAttribute("sortMap", sortedMap);
	modelMap.addAttribute("dataMap", finalMapping);
	modelMap.addAttribute("otherServices", otherServices);
	modelMap.addAttribute("structureVoMap", structureVoMap);
	modelMap.addAttribute("articles", articleVoMap);
	return "";
}

public int countOccurances(String string, String subString){
	 string = string.replaceAll("-", "").replaceAll(" ","").replaceAll("\n", "").replaceAll("–", "").trim().toLowerCase();
	 subString = subString.replaceAll("-", "").replaceAll(" ","").replaceAll("\n", "").replaceAll("–", "").trim().toLowerCase();
	 
	 
	Integer substrLen = subString.length();
	Integer count = 0;
	Integer index = string.indexOf(subString);
	while (index >= 0) {
	    count++;
	   
	    string = string.substring(index+substrLen);
	    index = string.indexOf(subString);
	}
	/*if(count>0){
		logger.info(string+"\n--->>>>>>.\n");
		 logger.info(subString);
	}*/
	return count;
}
private JSONArray  calculateWeightageFromArticlesStructureOffsets(List<Map<Integer, String>> termList,List<ArticleVo> articleVoList, int start, int offset) {
	offset=10;
	String name=""; int id=0; JSONArray array = new JSONArray(); int countt=0;
	Map<String,  ArticleVo> articleVoMap = new HashMap<String,  ArticleVo>();
	Map<String,  StructureVo> structureVoMap = new HashMap<String,  StructureVo>(); 
	Map<String, String>finalMapping = new HashMap<String, String>();
	System.out.println(articleVoList.size()+"---"+termList.size());
	int counter =0;int count=0; String text="";
	Map<String, Integer>countMap = new HashMap<String, Integer>();
	Map<String,Map<String, String>>otherServices = new HashMap<String, Map<String, String>>();
	LinkedList<String>idList = new LinkedList<String>();
	for(Map<Integer, String>map:termList){
		counter = counter+1;
		
		for(Map.Entry<Integer, String> m:map.entrySet()){
			System.out.println(counter+"---"+name);		
		name =m.getValue();
		id= m.getKey();
			
			ArticleVo vo = managerDao.getWileyArticle(String.valueOf(id));
			articleVoMap.put(String.valueOf(id), vo);
				String titleText = vo.getTITLE().replace("\n", "");
				String abstrText = vo.getABSTRACT().replace("\n", "");
				Pattern pat = Pattern.compile(name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-") , Pattern.CASE_INSENSITIVE);
				Matcher mat = pat.matcher(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//				logger.info(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
				if(mat.find()){
//					logger.info(mat.group());					
					String mm = mat.group();
					mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					titleText=titleText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					
					String srt= managerDao.getStructure(String.valueOf(id));
					convertStructuretoImage(srt, name);
//					String mmTag ="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\" data-html=\"true\" data-content=\"<img src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\"  />"+mm+"</a>";
					String mmTag="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\"  data-html=\"true\" data-content=\"<img src='/home/jenita.kn/Desktop/chemaxonProjectFolder/"+name.replace(" ", "_")+".png'\" class=\"media-object\" /> >"+mm+"</a>";
//					String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img'  src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\" />"+mm+"</a>";

					titleText = titleText.replace(mm, "<mark >"+mm+"</mark>");
					vo.setTITLE(titleText);
					articleVoMap.put(String.valueOf(id), vo);
				}
				
				Matcher mat2 = pat.matcher(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//				logger.info(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
				if(mat2.find()){
//					logger.info(mat2.group());					
					String mm = mat2.group();
					mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					abstrText=abstrText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
					String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img' alt="+name+" src=\"/home/jenita.kn/Desktop/chemaxonProjectImages/"+name+".png\" />"+mm+"</a>";
					abstrText = abstrText.replace(mm, "<mark >"+mm+"</mark>");
					
					vo.setABSTRACT(abstrText);
					articleVoMap.put(String.valueOf(id), vo);
				}
				
				int titleVal=0;
				int abstractVal=0;
				int introVal=0;
				int expVal=0;
				int resVal=0;
				int conclusionVal=0;
				int refVal=0;
				int fullTextVal=0;
				
				titleVal = countOccurances(vo.getTITLE(), name);
				abstractVal = countOccurances(vo.getABSTRACT(), name);
				/*introVal= countOccurances(vo.getINTRODUCTION(), name);
				expVal = countOccurances(vo.getEXPERIMENTAL_SECTION(), name);
				resVal= countOccurances(vo.getRESULTS_DISCUSSION(), name);
				conclusionVal= countOccurances(vo.getCONCLUSION(), name);
				refVal = countOccurances(vo.getREFERENCE(), name);*/
				fullTextVal = countOccurances(vo.getFULLTEXT(), name);
				
				// calculate the score
				count=0; text="";
/*				count = (vo.getABSTRACT()*abstractWeightage)+(vo.getTITLE()*titleWeightage)+(vo.getINTRODUCTION()*introWeightage)+(vo.getEXPERIMENTAL_SECTION()*expWeightage)+(vo.getRESULTS_DISCUSSION()*resultsWeightage)+(vo.getCONCLUSION()*conclusionWeightage)+(vo.getREFERENCE()*referenceWeightage);
*/				if(titleVal>0){
					text+= " Ti("+titleVal+")";
					count = count+(titleVal*titleWeightage);
				}
					if (abstractVal > 0) {
						text += " Abs(" + abstractVal + ")";
						count = count + (abstractVal * abstractWeightage);
					}
					if (introVal > 0) {
						text += " Int(" + introVal + ")";
						count = count + (introVal * introWeightage);
					}
					if (expVal > 0) {
						text += " Exp(" + expVal + ")";
						count = count + (expVal * expWeightage);
					}
					if (resVal > 0) {
						text += " Res(" + resVal + ")";
						count = count + (resVal * resultsWeightage);
					}
					if (conclusionVal > 0) {
						text += " Con(" + conclusionVal + ")";
						count = count + (conclusionVal * conclusionWeightage);
					}
					if (refVal > 0) {
						text += " Ref(" + refVal + ")";
						count = count + (refVal * referenceWeightage);
					}if (fullTextVal > 0) {
						text += " Full(" + fullTextVal + ")";
						count = count + (fullTextVal * fullTextWeightage);
					}
				
				
				if(!text.trim().equals("")){
					
//				text = count +" =>{"+text+"}";
//				text = "<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+text+"\" data-placement=\"bottom\">"+count+"</a>";
				
				JSONObject obj = new JSONObject();
				
		/*		String str="";
				str= fetchStructureImage(name, vo.getDOI());
				String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/";
				File f =  new File(path+str);
	             String encodstring = encodeFileToBase64Binary(f);
	             logger.info("base64 --------------->"+encodstring);
	             */
				String key = ""+id;
				if(count>0){
					countt++;
					obj = new JSONObject();
					System.out.println(key+"<---->"+vo.getDOI()+"-----"+count+">>>>>>--->"+titleVal+"-"+abstractVal+"-"+introVal+"-"+expVal+"-"+resVal+"-"+conclusionVal+"-"+refVal);
				
					countMap.put(key, count);
					
					
					
					
				}
					
				}		
	
	}
	
	}	
	Map<String, Integer> sortedMap = countMap.entrySet().stream()
			 .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
           .collect(toMap(Entry::getKey, Entry::getValue,
                    (e1,e2) -> e1, LinkedHashMap::new));
	logger.info(otherServices.entrySet());
	for(String key :sortedMap.keySet()){
		if(!idList.contains(key.split("###")[0])){
			idList.add(key.split("###")[0]);
			System.out.println(key);
		}
	}
	System.out.println("=====");
	LinkedList<String>finalList = new LinkedList<String>();
	JSONArray arr = new JSONArray();
	int c=0; int off=0;
	for(String i:idList){
		System.out.println(i);
		c++;
		if(c>=start && c<(start+offset)){
			
			
			finalList.add("CompId"+i);
			ArticleVo v = articleVoMap.get(i);
			StructureVo strVo = managerDao.getStructureDetails(i);
			JSONObject obj  = new JSONObject();
			obj.put("title", v.getTITLE());
			obj.put("abstract", v.getABSTRACT());
			obj.put("name", strVo.getChemicalName());
			obj.put("docId","docId"+i);
			obj.put("score", countMap.get(i));
			arr.add(obj);
		}
	}

	return arr;
}


public JSONArray getDocumentById(String compId) {
	JSONArray array = new JSONArray();
	StructureVo structureVo = new StructureVo();
	ArticleVo vo = new ArticleVo(); String name="";
	int count=0; String text=""; int countt=0;
	try{
		structureVo = managerDao.getStructureDetails(compId);
		vo = managerDao.getWileyArticle(compId);
		name = structureVo.getChemicalName();
		
		// article highlight
		String titleText = vo.getTITLE().replace("\n", "");
		String abstrText = vo.getABSTRACT().replace("\n", "");
		Pattern pat = Pattern.compile(name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-") , Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//		logger.info(titleText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
		if(mat.find()){
//			logger.info(mat.group());					
			String mm = mat.group();
			mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
			titleText=titleText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
			
			String srt= managerDao.getStructure(String.valueOf(compId));
			convertStructuretoImage(srt, name);
//			String mmTag ="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\" data-html=\"true\" data-content=\"<img src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\"  />"+mm+"</a>";
			String mmTag="<a href=\"#\" data-toggle=\"popover\" title=\"Popover Header\"  data-html=\"true\" data-content=\"<img src='/home/jenita.kn/Desktop/chemaxonProjectFolder/"+name.replace(" ", "_")+".png'\" class=\"media-object\" /> >"+mm+"</a>";
//			String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img'  src=\"/home/jenita.kn/Desktop/chemaxonProjectFolder"+name.replace(" ", "_")+".png\" />"+mm+"</a>";

			titleText = titleText.replace(mm, "<mark >"+mm+"</mark>");
			vo.setTITLE(titleText);
			
		}
		
		Matcher mat2 = pat.matcher(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
//		logger.info(abstrText.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-")+"\n"+name.replace("{", "\\{").replace("}", "\\}").replace("-", "\\-"));
		if(mat2.find()){
//			logger.info(mat2.group());					
			String mm = mat2.group();
			mm = mm.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
			abstrText=abstrText.replace("\\{","{").replace("\\}", "}").replace("\\-", "-");
			String mmTag ="<a class=\"anchor\" href=\"#\" data-toggle=\"tooltip\" title=\""+name+"\" data-placement=\"bottom\"><img id='img' alt="+name+" src=\"/home/jenita.kn/Desktop/chemaxonProjectImages/"+name+".png\" />"+mm+"</a>";
			abstrText = abstrText.replace(mm, "<mark >"+mm+"</mark>");
			
			vo.setABSTRACT(abstrText);
			
		}
		
		int titleVal=0;
		int abstractVal=0;
		int introVal=0;
		int expVal=0;
		int resVal=0;
		int conclusionVal=0;
		int refVal=0;
		int fullTextVal=0;
		
		titleVal = countOccurances(vo.getTITLE(), name);
		abstractVal = countOccurances(vo.getABSTRACT(), name);
		/*introVal= countOccurances(vo.getINTRODUCTION(), name);
		expVal = countOccurances(vo.getEXPERIMENTAL_SECTION(), name);
		resVal= countOccurances(vo.getRESULTS_DISCUSSION(), name);
		conclusionVal= countOccurances(vo.getCONCLUSION(), name);*/
	/*	refVal = countOccurances(vo.getREFERENCE(), name);*/
		fullTextVal = countOccurances(vo.getFULLTEXT(), name);
		
		// calculate the score
		count=0; text="";
/*				count = (vo.getABSTRACT()*abstractWeightage)+(vo.getTITLE()*titleWeightage)+(vo.getINTRODUCTION()*introWeightage)+(vo.getEXPERIMENTAL_SECTION()*expWeightage)+(vo.getRESULTS_DISCUSSION()*resultsWeightage)+(vo.getCONCLUSION()*conclusionWeightage)+(vo.getREFERENCE()*referenceWeightage);
*/				if(titleVal>0){
			text+= " Ti("+titleVal+")";
			count = count+(titleVal*titleWeightage);
		}
			if (abstractVal > 0) {
				text += " Abs(" + abstractVal + ")";
				count = count + (abstractVal * abstractWeightage);
			}
			if (introVal > 0) {
				text += " Int(" + introVal + ")";
				count = count + (introVal * introWeightage);
			}
			if (expVal > 0) {
				text += " Exp(" + expVal + ")";
				count = count + (expVal * expWeightage);
			}
			if (resVal > 0) {
				text += " Res(" + resVal + ")";
				count = count + (resVal * resultsWeightage);
			}
			if (conclusionVal > 0) {
				text += " Con(" + conclusionVal + ")";
				count = count + (conclusionVal * conclusionWeightage);
			}
			if (refVal > 0) {
				text += " Ref(" + refVal + ")";
				count = count + (refVal * referenceWeightage);
			}if (fullTextVal > 0) {
				text += " Full(" + fullTextVal + ")";
				count = count + (fullTextVal * fullTextWeightage);
			}

		if(!text.trim().equals("")){

		
		JSONObject obj = new JSONObject();
		
		String str="";
		str= fetchStructureImage(name, vo.getDOI());
		String path="/home/jenita.kn/Desktop/chemaxonProjectFolder/";
		File f =  new File(path+str);
         String encodstring = encodeFileToBase64Binary(f);
         encodstring ="data:image/png;base64,"+encodstring;
         logger.info("base64 --------------->"+encodstring);
         
		String key = compId+"###"+name+"###"+vo.getDOI()+"###"+str;
		if(count>0){
			countt++;
			obj = new JSONObject();
			System.out.println(key+"<---->"+vo.getDOI()+"-----"+count+">>>>>>--->"+titleVal+"-"+abstractVal+"-"+introVal+"-"+expVal+"-"+resVal+"-"+conclusionVal+"-"+refVal+"-"+fullTextVal);
			
//			Map<String, String> structures =fetchStructuresForDoi(name, vo.getDOI());
			JSONArray structures =fetchAllStructuresForDoiBase64(name, vo.getDOI());
			
			// additon of matched structures
			JSONObject strObj = new JSONObject();
			
			strObj.put("structure", encodstring);
			strObj.put("name", name);
			strObj.put("sid", "1");
			structures.add(strObj);

			//details of matched structure
			JSONArray strArr = new JSONArray();
			JSONObject mStrObj = new JSONObject();
			mStrObj.put("molecularFormula", structureVo.getMolecularFormula());
			mStrObj.put("molecularWeight", structureVo.getMolecularWeight());
			mStrObj.put("name", name);
			mStrObj.put("sid", "1");
			strArr.add(mStrObj);
			      
		      obj.put("docId", "docId"+compId);
		      obj.put("doi", vo.getDOI());
		      obj.put("title", vo.getTITLE());
		      obj.put("abstract", vo.getABSTRACT());
		     obj.put("scoreMethod", "("+text.trim()+")");
		      obj.put("score", count);
		      obj.put("name", name);
		      obj.put("matchedStructure", strArr); 
		      obj.put("structures", structures);
		     
		      array.add(obj); 
		}
			
			
	}
		
	}catch( Exception e){
		
	}
	System.out.println(array);
	return array;
}





}
