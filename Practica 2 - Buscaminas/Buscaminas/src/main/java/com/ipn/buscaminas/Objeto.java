package com.ipn.buscaminas;

import java.io.Serializable;

public class Objeto  implements Serializable {
    private String str;

    public Objeto(String str) {
        this.str = str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

}
