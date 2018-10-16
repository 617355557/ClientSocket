
package client;

public class Int2ByteUtil {
	
	/**
	 * int到byte[]
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray(int i) {
		
		byte[] result = new byte[4];
		// 由低位到高位
		result[3] = (byte) ((i >> 24) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}
	
	/**
	 * byte[]转int
	 * 
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		
		int value = 0;
		// 由低位到高位
		for (int i = 0; i < 4; i++) {
			int shift = i * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}
	
	public static String byte2StringHex(byte[] binaryData) {
	    StringBuilder bstr=new StringBuilder();
        for(int i=0;i<binaryData.length;i++) {
            String hex=Integer.toHexString(binaryData[i] & 0xFF);
            if(hex.length()==1) {
                hex="0"+hex;
            }
            bstr=bstr.append(hex);
        }
        return bstr.toString();
	}
	
	private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
    
    public static byte[] stringhex2Bytes(String c1) {
        int len = (c1.length() / 2);
        byte[] result = new byte[len];
        char[] achar = c1.toCharArray();
        for (int i = 0; i < len; i++) {
         int pos = i * 2;
         result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }
}
