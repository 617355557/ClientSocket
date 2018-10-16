
package client;

public class CommonUtils {
	
	private static int num = 1100000000-1;

	
	public static int getNum() {
	    num=num+1;
	    if(num>1200000000) {
	        num = 1100000000;
	    }
		return num;
	}
}
