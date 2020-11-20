import java.io.FileNotFoundException;
import java.io.IOException;

import mrpbuilder_java.*;

public class Main_mrpbuilder {
	
	public static void main(String[] args) {
		MrpBuilder builder = new MrpBuilder();
		System.out.println(""+args.length);
		try {
			builder.main(args);
			System.out.println("打包成功");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
