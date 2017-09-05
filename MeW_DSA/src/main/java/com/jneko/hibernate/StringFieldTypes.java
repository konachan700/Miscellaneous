package com.jneko.hibernate;

public enum StringFieldTypes {
    SIMPLE_STRING, IP_ADDRESS, URL_HTTP, URL_FTP, URL_SSH, PHONE;
    
    public String getFullName() {
        switch(this) {
            case SIMPLE_STRING:
                return "Simple text";
            case IP_ADDRESS:
                return "IP-address";
            case URL_HTTP:
                return "Web address";
            case URL_FTP:
                return "FTP address";
            case URL_SSH:
                return "SSH address";
            case PHONE:
                return "Phone";
            default:
                return this.name();
        }
    }
}
