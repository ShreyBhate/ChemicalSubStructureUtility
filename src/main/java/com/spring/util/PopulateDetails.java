package com.spring.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import chemaxon.calculations.ElementalAnalyser;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;

public class PopulateDetails {

	

public static void main(String[] args) throws Exception{
	
	 ElementalAnalyser elemanal = new ElementalAnalyser();
    Statement stat=null;
    Connection con=null;
    ResultSet rs =null;
    
    Statement stat2=null;
    Connection con2=null;
    ResultSet rs2 =null;
    Map<Integer,String> map= new HashMap<>();
    try{
        Class.forName("com.mysql.jdbc.Driver");
        con=DriverManager.getConnection("jdbc:mysql://178.79.186.74:3306/MEDCHEM","root","2k51125_MCPL");
        con2=DriverManager.getConnection("jdbc:mysql://192.168.10.6:3306/MEDCHEM","root","root123");
        stat2=con2.createStatement();
        stat=con.createStatement();
        //String sql="SELECT compound_id,smile FROM Compound_Master where Structure not like '%$RXN%' ";
        String sql="SELECT pubmed_id, name, structure, smile  FROM MEDCHEM.Compound_Master where pubmed_id in ('27560280','27560281','27560282','27560283','27560285','27565555','27569196','27569197','27573544','27592391','27592392','27592394','27592395','27592396','27597407','27597408','27597409','27597410','27597411','27597412','27597413','27597415','27597416','27597418','27597419','27597727','27598233','27598234','27598236','27598237','27608432','27614190','27614406','27614408','27614409','27620969','27639362','27639363','27639366','27639369','27639370','27643560','27643561','27643640','27643641','27654393','27654394','27654395','27657807','27657808','27657810','27657811','27657812','27662031','27662032','27662034','27668758','27676469','27676471','27688180','27688181','27688183','27688184','27688186','27688187','27688188','27688189','27688190','27688192','27688193','27689724','27689725','27689726','27689727','27689728','27689729','27689730','27689731','27689732','27689733','27710828','27718470','27718472','27718473','27718474','27721147','27721148','27721149','27721150','27721152','27721153','27721154','27721155','27721156','27721157','27721158','27736684','27744185','27744186','27744189')";

        rs=stat.executeQuery(sql);
        while(rs.next()){
        	 
             
             String structure = rs.getString("structure");
             InputStream in = new ByteArrayInputStream(structure.getBytes());
             MolImporter mi = new MolImporter(in);
             Molecule mol = null;
             while ((mol = mi.read()) != null) {

                    // set target molecule
                    elemanal.setMolecule(mol);

                    double exactMass = elemanal.exactMass();
                    double mass = elemanal.mass();
                    System.out.println(exactMass+"**"+mass+"**"+elemanal.formula());
                    // now use the results...
               System.out.println(); 
        	
           String sql2 ="INSERT INTO  MEDCHEM.SDF_DATA (DOI, CHEMICAL_NAME, STRUCTURE, SMILES, MOLECULAR_WEIGHT, MOLECULAR_FORMULA) "
           		+ "values(\""+rs.getString("pubmed_id")+"\", \""+rs.getString("name")+"\", \""+rs.getString("structure")+"\", \""+rs.getString("smile")+"\", \""+exactMass+"\", \""+elemanal.formula()+"\")";
           
           try{
           int i = stat2.executeUpdate(sql2);
           }catch(Exception e ){
        	   e.printStackTrace();
           }
           
        }
             }





    }catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
    }
    
    
    
      /*  // TODO Auto-generated method stub
        ElementalAnalyser elemanal = new ElementalAnalyser();

         Object input;
        // run plugin on target molecules
//"/home/santosh/app/project/MedChem/sdf/250909/AMACCQ/test.txt"

         String str ="\n  Marvin  09260715502D\n" +
                    "\n" +
                    " 15 16  0  0  0  0            999 V2000\n" +
                    "   11.9125   -6.2459    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   12.7375   -6.2459    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   12.9943   -5.4617    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   12.3250   -4.9750    0.0000 N   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   11.6599   -5.4617    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   10.8752   -5.2071    0.0000 O   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   13.7792   -5.2079    0.0000 O   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   12.3237   -4.1500    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   13.0376   -3.7364    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   13.0363   -2.9114    0.0000 C 0 0  0  0  0 0  0  0  0  0  0     0\n" +
                    "   13.7492   -2.5002    0.0000 C   0  0  0  0 0  0  0  0 00  0  0  0\n" +
                    "   13.7483   -1.6760    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   13.0327   -1.2637    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   12.3164   -1.6817    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "   12.3209   -2.5046    0.0000 C   0  0  0  0 0  0  0  0  0  0  0  0\n" +
                    "  2  3  1  0  0  0  0\n" +
                    "  4  8  1  0  0  0  0\n" +
                    "  3  4  1  0  0  0  0\n" +
                    "  8  9  1  0  0  0  0\n" +
                    "  4  5  1  0  0  0  0\n" +
                    "  9 10  1  0  0  0  0\n" +
                    "  5  1  1  0  0  0  0\n" +
                    " 10 11  2  0  0  0  0\n" +
                    "  1  2  2  0  0  0  0\n" +
                    " 11 12  1  0  0  0  0\n" +
                    "  5  6  2  0  0  0  0\n" +
                    " 12 13  2  0  0  0  0\n" +
                    " 13 14  1  0  0  0  0\n" +
                    "  3  7  2  0  0  0  0\n" +
                    " 14 15  2  0  0  0  0\n" +
                    " 15 10  1  0  0  0  0\n" +
                    "M  END\n";
         String[] strArr = {"C1CCCCC1","mol"};
         str =doFunc(strArr);
         InputStream in = new ByteArrayInputStream(str.getBytes());
         MolImporter mi = new MolImporter(in);
         Molecule mol = null;
         while ((mol = mi.read()) != null) {

                // set target molecule
                elemanal.setMolecule(mol);

                double exactMass = elemanal.exactMass();
                double mass = elemanal.mass();
                System.out.println(exactMass+"**"+mass+"**"+elemanal.formula());
                // now use the results...
            }
*/


    }





}
