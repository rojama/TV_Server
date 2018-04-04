

import java.io.File;
import java.io.IOException;

public class BatchTsx {

	public static void main(String[] args) 
	{
		File f=new File("C:\\Server\\live555");
		print(f);
	}

	public static void print(File file){
		if(file!=null){
			if(file.isDirectory()){
				File f[]=file.listFiles();
				if(f!=null){
					for(int i=0;i<f.length;i++){
						print(f[i]);
					}
				}
			}else{
				System.out.println(file);
				if (file.getName().endsWith("ts")
						|| file.getName().endsWith("TS")){
					String absolutePath = file.getAbsolutePath();
					File tsx = new File(absolutePath.substring(0, absolutePath.lastIndexOf("."))+".tsx");
					if (!tsx.exists()){					
						String cmd = "C:\\Server\\live555\\MPEG2TransportStreamIndexer " + absolutePath;
						Runtime rt = Runtime.getRuntime();        
						try {
							System.out.println(cmd);
							Process proc = rt.exec(cmd);
							proc.waitFor();
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}else if (file.getName().endsWith("tsx")){
					String absolutePath = file.getAbsolutePath();
					File ts = new File(absolutePath.substring(0, absolutePath.lastIndexOf("."))+".ts");
					if (!ts.exists()){	
						file.delete();
					}
				}
			}
		}
	}

}
