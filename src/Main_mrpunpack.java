import java.io.File;
import java.io.FileInputStream;

import mrpbuilder_java.MrpUnpack;

public class Main_mrpunpack {

	public static void main(String[] args) {
		String path = "D:\\Go\\3D贪吃蛇.mrp";
		
		MrpUnpack unpack = new MrpUnpack(new File(path));
		unpack.unpack("D:\\Go\\");
	}
}
