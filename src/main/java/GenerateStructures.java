import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class GenerateStructures {

	public static void main(String args[]){
		Map<String, String>smileSmart = new HashMap<>();
	
		File fil = new File("/home/jenita.kn/Desktop/smiles.txt");
		FileInputStream inputStream =null;
		try {
			inputStream = new FileInputStream(fil);
		
		 StringBuilder sb = new StringBuilder();
	        BufferedReader br = null;
	      
	            br = new BufferedReader(new InputStreamReader(inputStream));
	            String line = null;

	            line = br.readLine();
	            while ((line = br.readLine()) != null) {
	            	smileSmart.put(line.split("\t")[0].trim(), line.split("\t")[1].trim());
	            }
        
		List<String>smiles = new ArrayList<String>();
		
//		fetchStructures(smiles);
//		System.out.println("started");
		generateImages(smileSmart);
//		System.out.println("completed");
	}catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch(Exception e){
		e.printStackTrace();
	}
	}

	private static void generateImages(Map<String, String> smiles) {
		File dir = new File("/home/jenita.kn/Desktop/sdfFolder");
		String name= ""; String  path ="";
		if(dir.isDirectory()){
			File[] fl = dir.listFiles();
			for(File file: fl){
				name = file.getName().trim();
				path = name.replace(".sdf", "").trim();
				
				for(String smile : smiles.keySet()){
//				System.out.println("--"+smiles.get(smile.trim())+"\n"+path);
				String smart = smiles.get(smile.trim());
//				smart = smart.replace("\\\\", "\\");
					String[] command = {"obabel",  ""+file.getAbsolutePath().trim()+"", "-O",  "/home/jenita.kn/Desktop/imgFolder/"+path+"_"+smile.trim()+".png", "-s", ""+smart.trim()+" blue", "-xw 300", "-xh 300", "-d"};
					ProcessBuilder builder = new ProcessBuilder( command );
					System.out.println(builder.command());
					try {
						builder.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		 
         /*String sdf = "/home/jenita.kn/Desktop/sdfFolder/"+name.trim().replace(" ", "_")+".sdf";
     	
			String[] comm = {"obabel", "-:", "-O",  ""+sdf.trim()+"",  "--gen3D"};
			try{
			ProcessBuilder build = new ProcessBuilder( comm );
			build.start();
			
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("ERRORRRRR");
			}
         */
         

		
	}

	private static void fetchStructures(List<String>smiles) {
		
		Statement stat=null;
        Connection con=null;
        ResultSet rs =null;
        Map<String,String> map= new HashMap<String,String>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://192.168.10.6:3306/MEDCHEM","root","root123");
            stat=con.createStatement();
            
            
//            for(String smile:smiles){
            String sql="SELECT CHEMICAL_NAME, STRUCTURE FROM SDF_DATA ";

            rs=stat.executeQuery(sql);
            while(rs.next()){System.out.println(rs.getString("CHEMICAL_NAME"));
                map.put(rs.getString("CHEMICAL_NAME"), rs.getString("STRUCTURE"));
                String name= rs.getString("CHEMICAL_NAME").trim();
                String sdf = "/home/jenita.kn/Desktop/sdfFolder/"+rs.getString("CHEMICAL_NAME").trim().replace(" ", "_")+".sdf";
            	String targetSmiles = rs.getString("STRUCTURE").trim();
    			String[] comm = {"obabel", "-:"+targetSmiles.trim()+"", "-O",  ""+sdf.trim()+"",  "--gen3D"};
    			try{
    			ProcessBuilder build = new ProcessBuilder( comm );
    			build.start();
    			
    			}catch(Exception e){
    				e.printStackTrace();
    				System.out.println("ERRORRRRR");
    			}
                
                
                
                
            }
//            }
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
				
			try {
				con.close();
				stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
