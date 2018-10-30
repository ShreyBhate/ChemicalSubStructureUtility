package com.spring.dao;

import java.io.File;
import java.util.*;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spring.vo.ArticleOccuranceCountVo;
import com.spring.vo.ArticleVo;
import com.spring.vo.StructureVo;



@Repository
@Transactional
public class ManagerDao {
	
	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	
	Logger logger=Logger.getLogger(ManagerDao.class);
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	public List<Map<Integer,String>> searchName(String name, String filterName) {
		
		List<Map<Integer,String>> returnListMaps=new ArrayList<Map<Integer,String>>();
		List<Map<String,Object>> tempListMaps=new ArrayList<Map<String,Object>>();
		logger.info("Dao method=searchName");
		String queryAdd=""; 
		if(!filterName.trim().equals("")){
			queryAdd=" and CHEMICAL_NAME like '%"+filterName+"%'";
		}else queryAdd="";
		
		String sql="SELECT compound_id,CHEMICAL_NAME FROM MEDCHEM.SDF_DATA where CHEMICAL_NAME like '%"+name+"%' "+queryAdd+" limit 100 ";
		try{
			tempListMaps = jdbcTemplate.queryForList(sql);
			for(Map<String,Object> map:tempListMaps){
			
				Map<Integer, String> temp=new HashMap<Integer, String>();
				temp.put((Integer)map.get("compound_id"), (String)map.get("CHEMICAL_NAME"));
				returnListMaps.add(temp);
			}
			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}
		logger.info("returnListMaps = "+returnListMaps);
		return returnListMaps;
	}


	public String  getStructure(String id) {
		String stucture="";
		
		String sql="SELECT STRUCTURE FROM MEDCHEM.SDF_DATA where compound_id = "+id+"  limit 100 ";
		
		try{
			stucture = jdbcTemplate.queryForObject(sql, String.class);			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}		
		logger.info("stucture = "+stucture);		 
		return stucture;	
	}

	
	public String  getStructureFromName(String name) {
		String stucture="";
		
		String sql="SELECT STRUCTURE FROM MEDCHEM.SDF_DATA where CHEMICAL_NAME = \""+name+"\"  ";
		
		try{
			stucture = jdbcTemplate.queryForObject(sql, String.class);			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}		
//		logger.info("stucture = "+stucture);		 
		return stucture;	
	}
	
	public String  getTitleFromDoi(String doi) {
		String stucture="";
		
		String sql="SELECT TITLE FROM MEDCHEM.WILEY_PDF where PDF_NAME = \""+doi+"\"  ";
		
		try{
			stucture = jdbcTemplate.queryForObject(sql, String.class);			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}		
//		logger.info("stucture = "+stucture);		 
		return stucture;	
	}


	public List<Map<Integer,String>> structureSearch(String smiles,String fingerPrint[], String filterName){
		
		logger.info("====Dao molstucture===="+smiles);		 
		List<Map<Integer,String>> returnListMaps=new ArrayList<Map<Integer,String>>();
		List<Map<String,Object>> tempListMaps=new ArrayList<Map<String,Object>>();
		String queryAdd=""; String queryAddFingerprint="";
		if(!filterName.trim().equals("")){
			queryAdd=" and CHEMICAL_NAME like '%"+filterName+"%'";
			queryAddFingerprint=" and mc.CHEMICAL_NAME like '%"+filterName+"%'";
		}else queryAdd="";
		
		String sql="SELECT compound_id, CHEMICAL_NAME FROM MEDCHEM.SDF_DATA where SMILES like '%"+smiles.trim()+"%' "+queryAdd;
         logger.info("SQL : "+sql);
		try{
			tempListMaps = jdbcTemplate.queryForList(sql);
			for(Map<String,Object> map:tempListMaps){
				
				Map<Integer, String> temp=new HashMap<Integer, String>();
				temp.put((Integer)map.get("compound_id"), (String)map.get("CHEMICAL_NAME"));
				returnListMaps.add(temp);
			}		
		
				if(fingerPrint.length==16){
					sql="select mc.compound_id ,mc.CHEMICAL_NAME from MEDCHEM.fingerprints_new mf,MEDCHEM.SDF_DATA mc, MEDCHEM.WILEY_PDF w where fp0&"+fingerPrint[0]+"="+fingerPrint[0]+" and fp1&"+fingerPrint[1]+"="+fingerPrint[1]+" and fp2&"+fingerPrint[2]+"="+fingerPrint[2]+" and"
							+ " fp3&"+fingerPrint[3]+"="+fingerPrint[3]+" and fp4&"+fingerPrint[4]+"="+fingerPrint[4]+" "
							+ " and fp5&"+fingerPrint[5]+"="+fingerPrint[5]+" and fp6&"+fingerPrint[6]+"="+fingerPrint[6]+" and fp7&"+fingerPrint[7]+"="+fingerPrint[7]+" and "
									+ " fp8&"+fingerPrint[8]+"="+fingerPrint[8]+" and fp9&"+fingerPrint[9]+"="+fingerPrint[9]+" and "
							+ " fp10&"+fingerPrint[10]+"="+fingerPrint[10]+" and fp11&"+fingerPrint[11]+"="+fingerPrint[11]+" and fp12&"+fingerPrint[12]+"="+fingerPrint[12]+" and "
									+ " fp13&"+fingerPrint[13]+"="+fingerPrint[13]+" and fp14&"+fingerPrint[14]+"="+fingerPrint[14]+" and fp15&"+fingerPrint[15]+"="+fingerPrint[15]+" and "
											+ " mc.compound_id=mf.compound_id "+queryAddFingerprint +"and mc.DOI = w.PDF_NAME";
//					System.out.println(sql);
					tempListMaps = jdbcTemplate.queryForList(sql);
					for(Map<String,Object> map:tempListMaps){
						
						Map<Integer, String> temp=new HashMap<Integer, String>();
						temp.put((Integer)map.get("compound_id"), (String)map.get("CHEMICAL_NAME"));
						returnListMaps.add(temp);
					}		
					
					
				}
			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}
		logger.info("returnListMaps"+returnListMaps);
		return returnListMaps;		
	}
	
	
	public Integer structureSearchCount(String smiles,String fingerPrint[], String filterName){
		List<String>resSet = new ArrayList<String>();
		logger.info("====Dao molstucture===="+smiles);	int counter=0;	 
		List<Map<Integer,String>> returnListMaps=new ArrayList<Map<Integer,String>>();
		List<Map<String,Object>> tempListMaps=new ArrayList<Map<String,Object>>();
		String queryAdd=""; String queryAddFingerprint="";
		if(!filterName.trim().equals("")){
			queryAdd=" and CHEMICAL_NAME like '%"+filterName+"%'";
			queryAddFingerprint=" and mc.CHEMICAL_NAME like '%"+filterName+"%'";
		}else queryAdd="";
		
		String sql="SELECT compound_id, CHEMICAL_NAME FROM MEDCHEM.SDF_DATA where SMILES like '%"+smiles.trim()+"%' "+queryAdd;
         logger.info("SQL : "+sql);
		try{
			tempListMaps = jdbcTemplate.queryForList(sql);
			System.out.println("tempListMaps:"+tempListMaps);
			for(Map<String,Object> map:tempListMaps){
				System.out.println("coming 1");
				Map<Integer, String> temp=new HashMap<Integer, String>();
				temp.put((Integer)map.get("compound_id"), (String)map.get("CHEMICAL_NAME"));
				returnListMaps.add(temp);
			}		
			System.out.println("returnListMaps:"+returnListMaps +"---fingerprint"+fingerPrint.length);
			
				if(fingerPrint.length==16){
					sql="select mc.compound_id from MEDCHEM.fingerprints_new mf,MEDCHEM.SDF_DATA mc where fp0&"+fingerPrint[0]+"="+fingerPrint[0]+" and fp1&"+fingerPrint[1]+"="+fingerPrint[1]+" and fp2&"+fingerPrint[2]+"="+fingerPrint[2]+" and"
							+ " fp3&"+fingerPrint[3]+"="+fingerPrint[3]+" and fp4&"+fingerPrint[4]+"="+fingerPrint[4]+" "
							+ " and fp5&"+fingerPrint[5]+"="+fingerPrint[5]+" and fp6&"+fingerPrint[6]+"="+fingerPrint[6]+" and fp7&"+fingerPrint[7]+"="+fingerPrint[7]+" and "
									+ " fp8&"+fingerPrint[8]+"="+fingerPrint[8]+" and fp9&"+fingerPrint[9]+"="+fingerPrint[9]+" and "
							+ " fp10&"+fingerPrint[10]+"="+fingerPrint[10]+" and fp11&"+fingerPrint[11]+"="+fingerPrint[11]+" and fp12&"+fingerPrint[12]+"="+fingerPrint[12]+" and "
									+ " fp13&"+fingerPrint[13]+"="+fingerPrint[13]+" and fp14&"+fingerPrint[14]+"="+fingerPrint[14]+" and fp15&"+fingerPrint[15]+"="+fingerPrint[15]+" and "
											+ " mc.compound_id=mf.compound_id ";
					System.out.println("******************"+sql);
					logger.info("d----"+sql);
					resSet = jdbcTemplate.queryForList(sql, String.class);
							
					
					counter = resSet.size();
				}
			
		}catch(Exception e){
			e.printStackTrace();
			logger.info(" Error====== :",e);
		}
		logger.info("counter"+counter);
		return counter;		
	}


	public List<ArticleOccuranceCountVo> getArticleswithStructure(List<Map<Integer,String>>termList) {
		List<Map<String, Object>>resultList = new ArrayList<Map<String, Object>>();
		List<String> articleHeaders = new ArrayList<>();
		List<ArticleOccuranceCountVo> articleVoList = new ArrayList<ArticleOccuranceCountVo>();
		ArticleOccuranceCountVo articleOccuranceCountVo = new ArticleOccuranceCountVo();
		List< Map<String, Object>>mapList = new ArrayList<Map<String, Object>>();
		logger.info("Dao method=getArticleswithStructure");
		int count=0; String doi="";
		articleHeaders.add("TITLE");
		articleHeaders.add("ABSTRACT");
		articleHeaders.add("INTRODUCTION");
		articleHeaders.add("EXPERIMENTAL_SECTION");
		articleHeaders.add("RESULTS_DISCUSSION");
		articleHeaders.add("CONCLUSION");
		articleHeaders.add("REFERENCE");
		articleHeaders.add("NAME");
		articleHeaders.add("COMPID");
		List<ArticleVo>articleVos = new ArrayList<ArticleVo>();
		articleVos = getWileyArticles();
		for(ArticleVo vo: articleVos){
			for(Map<Integer, String>map: termList){
				
			for(Map.Entry<Integer, String>n:map.entrySet()){
				articleOccuranceCountVo = new ArticleOccuranceCountVo();
					
				for(String key: articleHeaders){
				try{
					if(!key.equals("NAME") &&!key.equals("COMPID")){
//					String sql2 ="SELECT ROUND ((LENGTH("+key+")- LENGTH( REPLACE ( "+key+", '"+n.get("CHEMICAL_NAME")+"', '') ) ) / LENGTH('"+n.get("CHEMICAL_NAME")+"')) AS count  FROM MEDCHEM.WILEY_PDF";
					String sql2="SELECT  PDF_NAME,sum(subStringCount("+key+",\""+n.getValue()+"\")) as count FROM  MEDCHEM.WILEY_PDF where PDF_NAME = \""+vo.getDOI()+"\"  ";
//					String sql2="SELECT sum(subStringCount(ABSTRACT,'Physalin H')) as count FROM  MEDCHEM.WILEY_PDF WHERE 1 ";
//					System.out.println(sql2);
				     resultList = jdbcTemplate.queryForList(sql2);
				     if(resultList.size()>0){
				    	 for(Map<String, Object>mapp : resultList){
				    		 count = Integer.parseInt((String)mapp.get("count"));
				    		 doi = (String)mapp.get("PDF_NAME");
				    		 if(key.equals("TITLE")) articleOccuranceCountVo.setTITLE(count);
								else if(key.equals("ABSTRACT")) articleOccuranceCountVo.setABSTRACT(count);
								else if(key.equals("INTRODUCTION")) articleOccuranceCountVo.setINTRODUCTION(count);
								else if(key.equals("EXPERIMENTAL_SECTION")) articleOccuranceCountVo.setEXPERIMENTAL_SECTION(count);
								else if(key.equals("RESULTS_DISCUSSION")) articleOccuranceCountVo.setRESULTS_DISCUSSION(count);
								else if(key.equals("CONCLUSION")) articleOccuranceCountVo.setCONCLUSION(count);
								else if(key.equals("REFERENCE")) articleOccuranceCountVo.setREFERENCE(count);
				    		 	articleOccuranceCountVo.setNAME((String)n.getValue());
								articleOccuranceCountVo.setCOMPID((Integer)n.getKey());
								articleOccuranceCountVo.setDOI(doi);
								articleVoList.add(articleOccuranceCountVo);
				    	 }
					}
					
				}
				}catch(Exception e){
					logger.info(" Error====== :"+e+" --");
				}
				}
			}
				/*articleOccuranceCountVo.setNAME((String)n.getValue());
				articleOccuranceCountVo.setCOMPID((Integer)n.getKey());
				articleOccuranceCountVo.setDOI(doi);
				articleVoList.add(articleOccuranceCountVo);*/
				
			
			}
			}
			
//		logger.info(mapList.entrySet());
		return articleVoList;
	}
	
	
	public List<ArticleVo> getWileyArticles() {
		
		// fetch wiley articles into object
		List<ArticleVo> articleVoList = new ArrayList<ArticleVo>();
		ArticleVo articleVo = new ArticleVo();
		String sql="";
		List<Map<String, Object>>resultList = new ArrayList<Map<String, Object>>();
		try{
			sql="Select * from WILEY_PDF";
			resultList = jdbcTemplate.queryForList(sql);
			if(resultList.size()>0){
				for(Map<String, Object>map:resultList){
					articleVo = new ArticleVo();
					articleVo.setDOI((String)map.get("PDF_NAME"));
					articleVo.setTITLE((String)map.get("TITLE"));
					articleVo.setINTRODUCTION((String)map.get("INTRODUCTION"));
					articleVo.setABSTRACT((String)map.get("ABSTRACT"));
					articleVo.setEXPERIMENTAL_SECTION((String)map.get("EXPERIMENTAL_SECTION"));
					articleVo.setRESULTS_DISCUSSION((String)map.get("RESULTS_DISCUSSION"));
					articleVo.setCONCLUSION((String)map.get("CONCLUSION"));
					articleVo.setREFERENCE((String)map.get("REFERENCE"));
					articleVo.setFULLTEXT((String)map.get("FULL_TEXT"));

					articleVoList.add(articleVo);	
				}
			}
			
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return articleVoList;
		
	}


	public Map<String, String> getStructureForDoiExceptName(String name, String doi) {
		String stucture="";
		List<Map<String, Object>>resultList = new ArrayList<Map<String,Object>>();
		Map<String, String>mapping = new HashMap<String, String>();
		String sql="SELECT CHEMICAL_NAME, STRUCTURE FROM MEDCHEM.SDF_DATA where CHEMICAL_NAME != ?  and DOI=?";
		
		try{
			resultList = jdbcTemplate.queryForList(sql, name, doi);			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}	
		if(resultList.size()>0){
			for(Map<String, Object>map:resultList){
				mapping.put((String)map.get("CHEMICAL_NAME"), (String)map.get("STRUCTURE"));
			}
		}
//		System.out.println(resultList.size()+" ---->"+sql+"-"+name+"-"+doi);
		return mapping;
	}


	public StructureVo fetchStructureDetails(String name) {
		
		StructureVo vo = new StructureVo();
		List<Map<String, Object>>resultList = new ArrayList<Map<String,Object>>();
		Map<String, String>mapping = new HashMap<String, String>();
		String sql="SELECT * FROM MEDCHEM.SDF_DATA where CHEMICAL_NAME =?";
		
		try{
			resultList = jdbcTemplate.queryForList(sql, name);			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}	
		if(resultList.size()>0){
			for(Map<String, Object>map:resultList){
				vo.setChemicalName((String)map.get("CHEMICAL_NAME"));
				vo.setMolecularFormula((String)map.get("MOLECULAR_FORMULA"));
				vo.setMolecularWeight((String)map.get("MOLECULAR_WEIGHT"));
				vo.setComments((String)map.get("COMMENTS"));
			}
		}
		return vo;
		
	}


	public List<Map<Integer,String>> structureSearchListWithOffset(String smiles, String[] fingerPrint, int start, int end) {

		logger.info("====Dao molstucture===="+smiles);	int counter=0;	 
		List<Map<Integer,String>> returnListMaps=new ArrayList<Map<Integer,String>>();
		List<Map<String,Object>> tempListMaps=new ArrayList<Map<String,Object>>();
		
		List<String>idList = new ArrayList<String>();
		List<Map<String, Object>> resultList=new ArrayList<Map<String, Object>>();
		int limit = start+end;
		String sql="SELECT compound_id, CHEMICAL_NAME FROM MEDCHEM.SDF_DATA where SMILES like '%"+smiles.trim()+"%' " ;
         logger.info("SQL : "+sql);
		try{
			tempListMaps = jdbcTemplate.queryForList(sql);
			for(Map<String,Object> map:tempListMaps){
				
				Map<Integer, String> temp=new HashMap<Integer, String>();
				temp.put((Integer)map.get("compound_id"), (String)map.get("CHEMICAL_NAME"));
				returnListMaps.add(temp);
			}		
		
				if(fingerPrint.length==16){
					sql="select  mc.compound_id ,mc.CHEMICAL_NAME from MEDCHEM.fingerprints_new mf,MEDCHEM.SDF_DATA mc, MEDCHEM.WILEY_PDF w where fp0&"+fingerPrint[0]+"="+fingerPrint[0]+" and fp1&"+fingerPrint[1]+"="+fingerPrint[1]+" and fp2&"+fingerPrint[2]+"="+fingerPrint[2]+" and"
							+ " fp3&"+fingerPrint[3]+"="+fingerPrint[3]+" and fp4&"+fingerPrint[4]+"="+fingerPrint[4]+" "
							+ " and fp5&"+fingerPrint[5]+"="+fingerPrint[5]+" and fp6&"+fingerPrint[6]+"="+fingerPrint[6]+" and fp7&"+fingerPrint[7]+"="+fingerPrint[7]+" and "
									+ " fp8&"+fingerPrint[8]+"="+fingerPrint[8]+" and fp9&"+fingerPrint[9]+"="+fingerPrint[9]+" and "
							+ " fp10&"+fingerPrint[10]+"="+fingerPrint[10]+" and fp11&"+fingerPrint[11]+"="+fingerPrint[11]+" and fp12&"+fingerPrint[12]+"="+fingerPrint[12]+" and "
									+ " fp13&"+fingerPrint[13]+"="+fingerPrint[13]+" and fp14&"+fingerPrint[14]+"="+fingerPrint[14]+" and fp15&"+fingerPrint[15]+"="+fingerPrint[15]+" and "
											+ " mc.compound_id=mf.compound_id and mc.DOI = w.PDF_NAME";
//					System.out.println(sql);
					tempListMaps = jdbcTemplate.queryForList(sql);
					for(Map<String,Object> map:tempListMaps){
						
						Map<Integer, String> temp=new HashMap<Integer, String>();
						temp.put((Integer)map.get("compound_id"), (String)map.get("CHEMICAL_NAME"));
						returnListMaps.add(temp);
					}	
	
			}
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}
		
		return returnListMaps;		
	}


	public StructureVo getStructureDetails(String compId) {
		List<Map<String, Object>>resultList = new ArrayList<Map<String,Object>>();
		String sql="SELECT * FROM MEDCHEM.SDF_DATA where compound_id =?";
		StructureVo vo = new StructureVo();
		try{
			resultList = jdbcTemplate.queryForList(sql, compId);			
		}catch(Exception e){
			logger.info(" Error====== :"+e);
		}	
		if(resultList.size()>0){
			for(Map<String, Object>map:resultList){
				vo.setChemicalName((String)map.get("CHEMICAL_NAME"));
				vo.setMolecularFormula((String)map.get("MOLECULAR_FORMULA"));
				vo.setMolecularWeight((String)map.get("MOLECULAR_WEIGHT"));
				vo.setComments((String)map.get("COMMENTS"));
			}
		}
		return vo;
	}
	
public ArticleVo getWileyArticle(String compId) {
		
		// fetch wiley articles into object
	
		ArticleVo articleVo = new ArticleVo();
		String sql="";
		List<Map<String, Object>>resultList = new ArrayList<Map<String, Object>>();
		try{
			sql="SELECT * FROM MEDCHEM.WILEY_PDF pdf, MEDCHEM.SDF_DATA sdf where sdf.compound_id=? and sdf.DOI=pdf.PDF_NAME";
			resultList = jdbcTemplate.queryForList(sql, compId);
			if(resultList.size()>0){
				for(Map<String, Object>map:resultList){
					articleVo = new ArticleVo();
					articleVo.setDOI((String)map.get("PDF_NAME"));
					articleVo.setTITLE((String)map.get("TITLE"));
					articleVo.setINTRODUCTION((String)map.get("INTRODUCTION"));
					articleVo.setABSTRACT((String)map.get("ABSTRACT"));
					articleVo.setEXPERIMENTAL_SECTION((String)map.get("EXPERIMENTAL_SECTION"));
					articleVo.setRESULTS_DISCUSSION((String)map.get("RESULTS_DISCUSSION"));
					articleVo.setCONCLUSION((String)map.get("CONCLUSION"));
					articleVo.setREFERENCE((String)map.get("REFERENCE"));
					articleVo.setFULLTEXT((String)map.get("FULL_TEXT"));
				}
			}
			
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return articleVo;
		
	}

	
	
	
}
