package com.fstar.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.shiro.codec.Base64;

public class ImageUtil {
	static public String IMAGE_PATH = "C://IMAGE//";
	static public String URL_PATH = "IMAGE";
	
	
	public static String writeImage (String base64image, String filePath, String fileName) throws Exception{
		if (base64image == null || base64image.isEmpty() || fileName == null || fileName.isEmpty()){
			return "";
		}
		if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
			base64image = base64image.substring(base64image.indexOf(","));
		}
		byte[] byteImage = Base64.decode(base64image);
		saveFile(byteImage, filePath, fileName);	
		return URL_PATH+File.separator+filePath+File.separator+fileName;
	}
	
	
    /** 
     * 获得指定文件的byte数组 
     * @throws IOException 
     */  
    public static byte[] getBytes(String filePath) throws IOException{  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            throw e;
        } catch (IOException e) {  
            e.printStackTrace();  
            throw e;
        }  
        return buffer;  
    }  
  
    /** 
     * 根据byte数组，生成文件 
     * @throws Exception 
     */  
    public static void saveFile(byte[] bfile, String filePath,String fileName) throws Exception {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath+File.separator+fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw e;
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
}
