package io.relayr.model;

import java.io.Serializable;

public enum IntegrationType implements Serializable{

    WUNDERBAR_1("wunderbar1"), WUNDERBAR_2("wunderbar2"), HOME_CONNECT("homeconnect"), UNKNOWN("");

    private final String mTypeName;

    IntegrationType(String type) {
        this.mTypeName = type;
    }

    public String getName(){
        return mTypeName;
    }

    public static IntegrationType getByName(String typeName){
        return WUNDERBAR_1;
        //TODO NEW_ONBOARDING
//        if (typeName != null) {
//            if (typeName.equals("wunderbar1"))
//                return WUNDERBAR_1;
//            else if (typeName.equals("wunderbar2"))
//                return WUNDERBAR_2;
//            else if (typeName.equals("homeconnect"))
//                return HOME_CONNECT;
//            else
//                return UNKNOWN;
//        }
//        return UNKNOWN;
    }
}
