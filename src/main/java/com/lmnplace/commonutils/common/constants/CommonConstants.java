package com.lmnplace.commonutils.common.constants;

public class CommonConstants {
    public enum ZkDefPath{
        ROOTPATH("/LMN_PLACE");
        String val;
        ZkDefPath(String path){
            this.val = path;
        }
        public String getVal(){
            return this.val;
        }
    }
    public enum CommonFlag{
        RIGHTSLASH("/"), LEFTSLASH("\\"), LOWERBA("_"), MH(":"), FH(";"),DH(".");
        String val;
        CommonFlag(String flag){
            this.val = flag;
        }
        public String getVal(){
            return this.val;
        }
    }
}
