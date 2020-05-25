package com.biotag.dogtagreading;

/**
 * Created by Administrator on 2017-10-18.
 */

public class Utils {

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static long byteArrayToLong(byte[] b) {
        String byteString = "";
        for (int i = 0; i < b.length; i++)
        {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            byteString += hex;
        }
        long value = Long.valueOf(byteString, 16);
        return value;
    }


    public static boolean checkArea(String AreaNo, String settingAreaNo) {
        if(AreaNo == null || settingAreaNo == null || AreaNo.equals("")){
            return false;
        }

        String[] AreaNoArrays = AreaNo.split(" ");
        System.out.println("AreaNoArrays.Length =  = " + AreaNoArrays.length);
        for (int i = 0; i < AreaNoArrays.length; i ++) {
            if (settingAreaNo.contains(AreaNoArrays[i])) {
                return true;
            }
        }
        return false;
    }

    public static String convertAreaToDisplay(String CardAreaNo){
        String AreaStr = "";
        String AreaNo = "";
        if(CardAreaNo.contains("F")){
            AreaStr += "内场"+" ";
        }
        if(CardAreaNo.contains("S")){
            AreaStr += "看台区" + " ";
        }
        if (CardAreaNo.contains("B"))
        {
            AreaNo += "B";
            if (!AreaStr.contains("后台区（部分）"))
                AreaStr += "后台区（部分）" + " ";
        }
        if (CardAreaNo.contains("M"))
        {
            AreaNo += "M";
            if (!AreaStr.contains("后台区（部分）"))
                AreaStr += "后台区（部分）" + " ";
        }
        if (CardAreaNo.contains("T"))
        {
            AreaNo += "T";
            if (!AreaStr.contains("后台区（部分）"))
                AreaStr += "后台区（部分）" + " ";
        }
        if (CardAreaNo.contains("C"))
        {
            AreaNo += "C";
            if (!AreaStr.contains("后台区（部分）"))
                AreaStr += "后台区（部分）" + " ";
        }
        if (CardAreaNo.contains("H"))
        {
            AreaNo += "H";
            if (!AreaStr.contains("后台区（部分）"))
                AreaStr += "后台区（部分）" + " ";
        }
        if (CardAreaNo.contains("A"))
        {
            if (CardAreaNo.trim() == "A")
            {
                AreaNo = "A";
                AreaStr += "通道区" + " ";
            }
        }
        if (CardAreaNo.contains("B") && CardAreaNo.contains("M") && CardAreaNo.contains("T") && CardAreaNo.contains("C") && CardAreaNo.contains("H"))
        {
            System.out.println("contains BMTCH");
            AreaStr = AreaStr.replace("后台区（部分）", "后台区");
        }
        AreaStr = AreaStr.trim();

        return AreaStr;
    }

    public static String dealAreaNo(String AreaNo) {
        AreaNo = AreaNo.replaceAll("A *","").trim();
//        AreaNo = AreaNo.replaceAll("[FS] *","").trim();
        return AreaNo;
    }
}
