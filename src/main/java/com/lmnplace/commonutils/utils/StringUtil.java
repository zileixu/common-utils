package com.lmnplace.commonutils.utils;

import com.lmnplace.commonutils.common.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {

    /**
     * 除首字母外将字符串中大写字符转化为点加小写，例如：abcD 转化为 abc.d
     * @param name
     * @return
     */
    public static String convertName(String name){
        StringBuffer outName = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            int c = (int)name.charAt(i);
            if(65<=c&&c<91){
                if(i!=0){
                    outName.append(CommonConstants.CommonFlag.DH.getVal());
                }
                outName.append((char)(c+32));
            }else{
                outName.append(name.charAt(i));
            }
        }
        return outName.toString();
    }

    /**
     * 用于校验sql  注入
     * @param str
     * @return
     */
    public static boolean sqlInj(String str) {
        String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,";
        String inj_stra[] = split(inj_str, "|");
        for (int i = 0; i < inj_stra.length; i++) {
            if (str.indexOf(inj_stra[i]) >= 0) {
                return true;
            }
        }
        return false;

    }


    /**
     * 下划线转驼峰
     *
     * @param fieldName
     * @return
     */
    public static String underlineToHump(String fieldName) {
        StringBuilder result = new StringBuilder();
        String[] para = fieldName.split("_");
        for (String s : para) {
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static int num(int length, int size) {
        return length % size == 0 ? length / size : length / size + 1;
    }


    public static boolean checkFileSuffix(String fileName, String... suffix) {
        if (suffix.length == 0) {
            return false;
        }
        boolean flag = false;
        for (int i = 0; i < suffix.length; i++) {
            flag = fileName.toLowerCase().endsWith(suffix[i]);
            if (flag) {
                return flag;
            }
        }
        return flag;
    }

    public static String removeSuffix(String fileName, String suffix) {
        int n;
        if ((n = fileName.indexOf(suffix)) != -1) {
            return fileName.substring(0, n);
        }
        return fileName;
    }

    public static String fillHead(String src, String prefix, int length) {
        int len = src.length();
        if (len < length) {
            while (len < length) {
                StringBuilder sb = new StringBuilder();
                sb.append(prefix).append(src);//左补prefix
                src = sb.toString();
                len = src.length();
            }
        }
        return src;
    }

    /**
     * 提供车牌 进行模糊搜索，将其变更为 %a%b% 的格式
     * @param carPlateNumber
     * @return
     */
    public static String replaceCarPlateNumber(String carPlateNumber) {
        if (StringUtils.isEmpty(carPlateNumber)) {
            return "";
        }
        char[] chars = carPlateNumber.toCharArray();
        StringBuilder buf = new StringBuilder();
        buf.append("%");
        for (int i = 0; i < chars.length; ++i) {
            buf.append(chars[i]);
            buf.append("%");
        }
        return buf.toString();
    }

    public static void assertNotBlank(String val){
        if(StringUtil.isBlank(val)){
            throw new IllegalArgumentException("argument cannot be blank for val");
        }
    }
}
