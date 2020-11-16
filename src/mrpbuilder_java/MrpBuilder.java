package mrpbuilder_java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xl.util.FileUtils;

public class MrpBuilder {
	
	      byte[] Magic = new byte[4]; // [0:4]     固定标识'MRPG'
	      int FileStart;  // [4:8]     文件头的长度+文件列表的长度-8
	      int MrpTotalLen;  // [8:12]    mrp文件的总长度
	      int MRPHeaderSize;  // [12:16]   文件头的长度，通常是240，如果有额外数据则需要加上额外数据的长度
	      byte[] FileName = new byte[12]; // [16:28]   GB2312编码带'\0'
	      byte[] DisplayName = new byte[24]; // [28:52]   GB2312编码带'\0'
	      byte[] AuthStr = new byte[16]; // [52:68]   编译器的授权字符串的第2、4、8、9、11、12、1、7、6位字符重新组合的一个字符串
	      int  Appid; // [68:72]
	      int  Version; // [72:76]
	      int  Flag; // [76:80]   第0位是显示标志， 1-2位是cpu性能要求，所以cpu取值范围是0-3只对展讯有效， 第3位是否是shell启动的标志，0表示start启动，1表示shell启动
	      int  BuilderVersion; // [80:84]   应该是编译器的版本，从几个mrpbuilder看都是10002
	      int  Crc32; // [84:88]   整个文件计算crc后写回，计算时此字段的值为0
	      byte[] Vendor = new byte[40]; // [88:128]  GB2312编码带'\0' 供应商
	      byte[] Desc = new byte[64]; // [128:192] GB2312编码带'\0'
	      int AppidBE ; // [192:196] 大端appid
	      int VersionBE ; // [196:200] 大端version
	      int Reserve2 ; // [200:204] 保留字段
	      int ScreenWidth ; // [204:206] 在反编译的mrpbuilder中能看到有屏幕信息的字段，但是在斯凯提供的文档中并没有说明
	      int ScreenHeight ; // [206:208]
	      byte Plat  ; // [208:209] mtk/mstar填1，spr填2，其它填0
	      byte[] Reserve3 = new byte[31]; // [209:240]
	      ArrayList<FileItem> list_file;
	      /*
	       * display
	       * path
	       * filename
	       * appid
	       * version
	       * vendor
	       * description
	       */
	      
	    public static void main(String[] args) {
			// 读取数据
	    	String jsonPath = "pack.json";
	    	File file = new File(jsonPath);
	    	String jsonText = "";
	    	try {
				 jsonText = readText(file, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	JSONObject jsonObject = new JSONObject(jsonText);
	    	Config config = new MrpBuilder().new Config();
	    	config.Appid = jsonObject.optInt("appid");
	    	config.DisplayName = jsonObject.optString("display");
	    	config.path = jsonObject.optString("path");
	    	config.FileName = jsonObject.optString("filename");
	    	config.Version = jsonObject.optInt("version");
	    	config.Vendor = jsonObject.optString("vendor");
	    	config.Desc = jsonObject.optString("description");
	    	config.AuthStr = jsonObject.optString("auth");
	    	config.Flag = jsonObject.optInt("flag");
	    	config.ScreenWidth = jsonObject.optInt("screen_width");
	    	config.ScreenHeight = jsonObject.optInt("screen_height");
	    	config.Plat = (byte)jsonObject.optInt("platform");
	    	JSONArray filesarray = jsonObject.getJSONArray("files");
	    	config.list_file = new ArrayList<MrpBuilder.FileItem>();
	    	for(int i=0;i<filesarray.length();i++){
	    		FileItem fileItem = new MrpBuilder().new FileItem();
	    		String temp = filesarray.getString(i);
	    		if(temp.indexOf("=")<0){
	    			fileItem.path = temp;
	    			fileItem.filename = FileUtils.getName(temp);
	    		}
	    		else{
	    			String[] temp_items = temp.split("=");
	    			fileItem.path = temp_items[0];
	    			fileItem.filename = FileUtils.getName(temp_items[1]);
	    			
	    		}
	    		
	    		
	    	}
	    	try {
	    		RandomAccessFile output = new RandomAccessFile(config.path, "rw");
				
				output.write(getGBKBytes(config.Magic,4));
				output.write(new byte[4]);
				output.write(new byte[4]);
				output.write(new byte[4]);
				output.write(getGBKBytes(config.FileName, 12));
				output.write(getGBKBytes(config.DisplayName, 24));
				output.write(getGBKBytes(config.AuthStr, 16));
				output.write(getIntByte(config.Appid));
				output.write(getIntByte(config.Version));
				output.write(getIntByte(config.Flag));
				output.write(getIntByte(config.BuilderVersion));
				output.write(getIntByte(0));
				output.write(getGBKBytes(config.Vendor, 40));
				output.write(getGBKBytes(config.Desc, 64));
				output.write(config.Appid);
				output.write(config.Version);
				output.write(getIntByte(0));
				output.write(getIntByte(config.ScreenWidth));
				output.write(getIntByte(config.ScreenHeight));
				byte[] byte_plat = new byte[1];
				byte_plat[0] = config.Plat;
				output.write(byte_plat);
				output.write(getGBKBytes("", 31));
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	catch (IOException e) {
				// TODO: handle exception
			}
	    	//写入头
	    	
	    	//写入文件列表
	    	
	    	//计算offset
	    	
	    	
		}
	    
	    public static byte[] getIntByte(int number){
	    	byte[] bytes = new byte[4];
	    	bytes[0] = (byte) (number&0xff);
	    	bytes[1] = (byte) ((number>>8)&0xff);
	    	bytes[2] = (byte) ((number>>16)&0xff);
	    	bytes[3] = (byte) ((number>>24)&0xff);
	    	return bytes;
	    }
	    
		public static String readText(File file,String encoding) throws IOException
		{
			String content = "";
			//	File file = new File(path);

			if(file.isFile())
			{
				FileInputStream input= new FileInputStream(file);

				byte [] buf=new byte[input.available()];
				input.read(buf);
				content = new String(buf,encoding);
			}
			return content;
		}
		
		public static byte[] getGBKBytes(String text,int bytelen){
			byte[] rebyte = new byte[bytelen];
			try {
				byte[] temp = text.getBytes("GBK");
				for(int i=0;i<Math.min(temp.length, rebyte.length);i++){
					rebyte[i] = temp[i];
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rebyte;
		}
		
		public static byte[] read(File file) throws IOException
		{
			String content = "";
			//	File file = new File(path);

			if(file.isFile())
			{
				FileInputStream input= new FileInputStream(file);

				byte [] buf=new byte[input.available()];
				input.read(buf);
				return buf;
			}
			return null;
		}
	    
	      
	    class FileItem{
	    	String path;
	    	String filename;
	    	int offset;
	    	int len;
	    }
	    
	    public class Config{
	    	  String path = "";
	    	  String Magic = "MRPG"; // [0:4]     固定标识'MRPG'
		      int FileStart;  // [4:8]     文件头的长度+文件列表的长度-8
		      int MrpTotalLen;  // [8:12]    mrp文件的总长度
		      int MRPHeaderSize;  // [12:16]   文件头的长度，通常是240，如果有额外数据则需要加上额外数据的长度
		      String FileName ; // [16:28]   GB2312编码带'\0'
		      String DisplayName ; // [28:52]   GB2312编码带'\0'
		      String AuthStr ; // [52:68]   编译器的授权字符串的第2、4、8、9、11、12、1、7、6位字符重新组合的一个字符串
		      int  Appid; // [68:72]
		      int  Version; // [72:76]
		      int  Flag = 3; // [76:80]   第0位是显示标志， 1-2位是cpu性能要求，所以cpu取值范围是0-3只对展讯有效， 第3位是否是shell启动的标志，0表示start启动，1表示shell启动
		      int  BuilderVersion = 10002; // [80:84]   应该是编译器的版本，从几个mrpbuilder看都是10002
		      int  Crc32; // [84:88]   整个文件计算crc后写回，计算时此字段的值为0
		      String Vendor ; // [88:128]  GB2312编码带'\0' 供应商
		      String Desc ; // [128:192] GB2312编码带'\0'
		      int AppidBE ; // [192:196] 大端appid
		      int VersionBE ; // [196:200] 大端version
		      int Reserve2 ; // [200:204] 保留字段
		      int ScreenWidth ; // [204:206] 在反编译的mrpbuilder中能看到有屏幕信息的字段，但是在斯凯提供的文档中并没有说明
		      int ScreenHeight ; // [206:208]
		      byte Plat = 1; // [208:209] mtk/mstar填1，spr填2，其它填0
		      byte[] Reserve3 = new byte[31]; // [209:240]
		      ArrayList<FileItem> list_file;
		      public Config() {
			}
	    }
	
}
