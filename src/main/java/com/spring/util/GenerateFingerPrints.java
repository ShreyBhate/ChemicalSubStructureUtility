package com.spring.util;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class GenerateFingerPrints {
    public static void main(String[] args) {


        Statement stat=null;
        Connection con=null;
        ResultSet rs =null;
        Map<Integer,String> map= new HashMap<>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
 con=DriverManager.getConnection("jdbc:mysql://192.168.10.6:3306/MEDCHEM","root","root123");
            stat=con.createStatement();
            //String sql="SELECT compound_id,smile FROM Compound_Master where Structure not like '%$RXN%' ";
            String sql="SELECT compound_id,smiles FROM SDF_DATA ";

            rs=stat.executeQuery(sql);
            while(rs.next()){
                map.put(rs.getInt("compound_id"), rs.getString("smiles"));
            }






        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        ;
        try {
        //map.put(1, "COC1=CC=C(OCC(O)CN2CCC(CC2)OC2=CC=C(C=C2)C(F)(F)F)C=C1 |c:21,23,30,t:2,4,19|");
        //obabel -:\"c1ccccc1\" -ofpt
        for(Map.Entry<Integer, String> ma:map.entrySet()){

        //System.out.println(ma.getValue());
        //String[] command = {"obabel", "-:"+ma.getValue()+"", "-ofpt"};
        String[] command = {"obabel", "-:"+ma.getValue()+"", "-ofpt"};
        ProcessBuilder builder = new ProcessBuilder( command );
            Process process = builder.start();
            String outputString=output(process.getInputStream());
            String hexArray[]=outputString.split("(?<=\\G.{16})");
            String query="insert into fingerprints_new values (0,"+ma.getKey();
            if(hexArray.length<16)
                continue;
            for(String hex:hexArray){

                query=query+","+ hexToDec(hex);
                //stat.executeUpdate(query);
                //insert into fingerprints values (0, 512, 0, 0, 2112, 32768, 0, 0, 0, 0, 134217728, 0, 0, 0, 131072, 0);

            }
            query=query+")";
            stat.executeUpdate(query);
            //System.out.println(query);


        }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally {
            try {
                stat.close();
                con.close();
            }catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }
    public static Number hexToDec(String hex)  {
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
    private static String output(InputStream inputStream) throws IOException {
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
}


	

