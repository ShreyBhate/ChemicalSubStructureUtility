package com.spring.vo;

public class ArticleOccuranceCountVo {

	
	private String ID;	
	private int TITLE=0;
	private int ABSTRACT=0;
	private int INTRODUCTION=0;
	private int EXPERIMENTAL_SECTION=0;
	private int RESULTS_DISCUSSION=0;
	private int CONCLUSION=0;
	private int REFERENCE=0;
	private String NAME;
	private int COMPID;
	private String DOI;
	private String titleData="";
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public int getTITLE() {
		return TITLE;
	}
	public void setTITLE(int tITLE) {
		TITLE = tITLE;
	}
	public int getABSTRACT() {
		return ABSTRACT;
	}
	public void setABSTRACT(int aBSTRACT) {
		ABSTRACT = aBSTRACT;
	}
	public int getINTRODUCTION() {
		return INTRODUCTION;
	}
	public void setINTRODUCTION(int iNTRODUCTION) {
		INTRODUCTION = iNTRODUCTION;
	}
	public int getEXPERIMENTAL_SECTION() {
		return EXPERIMENTAL_SECTION;
	}
	public void setEXPERIMENTAL_SECTION(int eXPERIMENTAL_SECTION) {
		EXPERIMENTAL_SECTION = eXPERIMENTAL_SECTION;
	}
	public int getRESULTS_DISCUSSION() {
		return RESULTS_DISCUSSION;
	}
	public void setRESULTS_DISCUSSION(int rESULTS_DISCUSSION) {
		RESULTS_DISCUSSION = rESULTS_DISCUSSION;
	}
	public int getCONCLUSION() {
		return CONCLUSION;
	}
	public void setCONCLUSION(int cONCLUSION) {
		CONCLUSION = cONCLUSION;
	}
	public int getREFERENCE() {
		return REFERENCE;
	}
	public void setREFERENCE(int rEFERENCE) {
		REFERENCE = rEFERENCE;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public int getCOMPID() {
		return COMPID;
	}
	public void setCOMPID(int cOMPID) {
		COMPID = cOMPID;
	}
	public String getDOI() {
		return DOI;
	}
	public void setDOI(String dOI) {
		DOI = dOI;
	}
	public String getTitleData() {
		return titleData;
	}
	public void setTitleData(String titleData) {
		this.titleData = titleData;
	}
}
