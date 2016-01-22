package cwp;

import java.io.File;


public class CallCwpTest {

	static {  
        System.loadLibrary("cwp_to_java");  
    }  
      
    public static native String callCwp(String str1, String str2, String str3);
    
    public static String cwp(String craneJsonStr, String hatchJsonStr, String moveJsonStr) {
    	String cranes = craneJsonStr;
        String hatches = hatchJsonStr;
        String moves = moveJsonStr;
    	String str = callCwp(cranes, hatches, moves);  
//        System.out.println(str);
        return str;
    }

}
