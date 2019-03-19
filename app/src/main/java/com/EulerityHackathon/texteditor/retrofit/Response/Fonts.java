package com.EulerityHackathon.texteditor.retrofit.Response;

public class Fonts {                            //POJO class for mapping json

    public String url;
    public String family;
    public String bold;
    public String italic;

    public Fonts(){

    }
    public String getUrl() {
        return url;
    }

    public String getFamily() {
        return family;
    }

    public String getBold() {
        return bold;
    }

    public String getItalic() {
        return italic;
    }


}
