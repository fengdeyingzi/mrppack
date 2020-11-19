import java.io.FileNotFoundException;

import mrpbuilder_java.*;

public class Main_mrpbuilder {
	
	public static void main(String[] args) {
		MrpBuilder builder = new MrpBuilder();
		try {
			builder.main(new String[]{"pack.json"});
			System.out.println("打包成功");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
