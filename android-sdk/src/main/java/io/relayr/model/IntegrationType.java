package io.relayr.model;

import java.io.Serializable;

public enum IntegrationType implements Serializable{

    WUNDERBAR_1("Wunderbar1"), WUNDERBAR_2("Wunderbar2"), HOME_CONNECT("HomeConnect"), UNKNOWN("");

    private final String mTypeName;

    IntegrationType(String type) {
        this.mTypeName = type;
    }

    public String getName(){
        return mTypeName;
    }

    public static IntegrationType getByName(String typeName){
        if (typeName != null) {
            if (typeName.equals("Wunderbar1"))
                return WUNDERBAR_1;
            else if (typeName.equals("Wunderbar2"))
                return WUNDERBAR_2;
            else if (typeName.equals("HomeConnect"))
                return HOME_CONNECT;
            else
                return UNKNOWN;
        }
        return UNKNOWN;
    }
}
