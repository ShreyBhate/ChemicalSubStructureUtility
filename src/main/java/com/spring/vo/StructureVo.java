package com.spring.vo;

public class StructureVo {

	
	private String chemicalName;
	private int compoundId;
	private String molecularFormula;
	private String molecularWeight;
	private String comments;
	private String doi;
	private String smiles;
	private String structure;
	
	
	public String getChemicalName() {
		return chemicalName;
	}
	public int getCompoundId() {
		return compoundId;
	}
	public String getMolecularFormula() {
		return molecularFormula;
	}
	
	public String getComments() {
		return comments;
	}
	public String getDoi() {
		return doi;
	}
	public String getSmiles() {
		return smiles;
	}
	public String getStructure() {
		return structure;
	}
	public void setChemicalName(String chemicalName) {
		this.chemicalName = chemicalName;
	}
	public void setCompoundId(int compoundId) {
		this.compoundId = compoundId;
	}
	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getMolecularWeight() {
		return molecularWeight;
	}
	public void setMolecularWeight(String molecularWeight) {
		this.molecularWeight = molecularWeight;
	}
	

}
