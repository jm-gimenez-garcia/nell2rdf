/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;

/**
 *
 * @author Maisa
 */

import java.util.HashMap;
import java.util.Map;

 class FormatComponent {

    private String componentName;
    protected Map<String, String> tokenFormated = new HashMap<>();
    protected Map<String, Double[]> tokenFormatedLL = new HashMap<>();

    TokenModel tokenmodel;

    public void formattingHeaderToken(Map<String, String[]> in, String componentName) {
        this.componentName = componentName;

        if (this.componentName.equals("LatLong") || (this.componentName.equals("LatLongTT"))) {
            tokenFormatedLL.put(in.get("token")[0],
                    new Double[]{Double.valueOf(in.get("token")[1]), Double.valueOf(in.get("token")[2])});
            tokenmodel = new TokenModel(this.componentName,
                    in.get("token")[0], Double.valueOf(in.get("token")[1]),
                    Double.valueOf(in.get("token")[2]));
        } else {
            if (LineInstanceJOIN.CAT_OR_REL.equals("relation")) {
                tokenFormated.put("FROM", LineInstanceJOIN.CAT_OR_REL.toUpperCase());
                tokenFormated.put("element1", in.get("token")[0]);
                tokenFormated.put("element2", in.get("token")[1]);
            } else if (LineInstanceJOIN.CAT_OR_REL.equals("category")) {
                tokenFormated.put("FROM", LineInstanceJOIN.CAT_OR_REL.toUpperCase());
                tokenFormated.put("element1", in.get("token")[0]);
                tokenFormated.put("element2", in.get("token")[1]);
            }

            tokenmodel = new TokenModel(tokenFormated.get("FROM"), tokenFormated.get("element1"), tokenFormated.get("element2"));

        }
    }

    public String getTokenElement1() {
        if (this.componentName.equals("LatLong") || (this.componentName.equals("LatLongTT"))) {
            return tokenFormatedLL.toString().split(", ")[0];
        } else {
            return tokenFormated.get("element1");
        }
    }

    public String getTokenElement2() {
        return tokenFormated.get("element2");
    }

    public String getTypeKB() {
        return tokenFormated.get("FROM");
    }

    public TokenModel getTokenmodel() {
        return tokenmodel;
    }
}

