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

    String FROM = "FROM";
    String ELEMENT1 = "element1";
    String ELEMENT2 = "element2";
    String TOKEN = "token";

    TokenModel tokenmodel;

    public void formattingHeaderToken(Map<String, String[]> in, String componentName) {
        this.componentName = componentName;

        if (this.componentName.equals(ConstantList.LATLONG)) {
            tokenFormatedLL.put(in.get(TOKEN)[0],
                    new Double[]{Double.valueOf(in.get(TOKEN)[1]), Double.valueOf(in.get(TOKEN)[2])});
            tokenmodel = new TokenModel(this.componentName,
                    in.get(TOKEN)[0], Double.valueOf(in.get(TOKEN)[1]),
                    Double.valueOf(in.get(TOKEN)[2]));
        } else {
            if (LineInstanceJOIN.CAT_OR_REL.equals(ConstantList.RELATION)) {
                tokenFormated.put(FROM, LineInstanceJOIN.CAT_OR_REL.toUpperCase());
                tokenFormated.put(ELEMENT1, in.get(TOKEN)[0]);
                tokenFormated.put(ELEMENT2, in.get(TOKEN)[1]);
            } else if (LineInstanceJOIN.CAT_OR_REL.equals(ConstantList.CATEGORY)) {
                tokenFormated.put(FROM, LineInstanceJOIN.CAT_OR_REL.toUpperCase());
                tokenFormated.put(ELEMENT1, in.get(TOKEN)[0]);
                tokenFormated.put(ELEMENT2, in.get(TOKEN)[1]);
            }

            tokenmodel = new TokenModel(tokenFormated.get(FROM),
                    tokenFormated.get(ELEMENT1), tokenFormated.get(ELEMENT2));

        }
    }

    public String getTokenElement1() {
        if (this.componentName.equals(ConstantList.LATLONG)) {
            return tokenFormatedLL.toString().split(", ")[0];
        } else {
            return tokenFormated.get(ELEMENT1);
        }
    }

    public String getTokenElement2() {
        return tokenFormated.get(ELEMENT2);
    }

    public String getTypeKB() {
        return tokenFormated.get(FROM);
    }

    public TokenModel getTokenmodel() {
        return tokenmodel;
    }
}
