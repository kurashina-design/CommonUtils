package jp.kurashina.commons.security;

public class SecurityMapping {

    public SecurityMapping() {
    }

    public SecurityMapping(String key) {
        this.path =  "/" + key + "/**";
        this.read = "PERMISSION_read:" + key;
        this.update = "PERMISSION_update:" + key;
        this.create = "PERMISSION_create:" + key;
        this.delete = "PERMISSION_delete:" + key;
    }

    public String path;
    public String read;
    public String update;
    public String create;
    public String delete;

}
