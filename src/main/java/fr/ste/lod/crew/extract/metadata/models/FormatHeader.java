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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class FormatHeader {

    private String componentName;
    protected Map<String, String> tokenFormated = new HashMap<>();
    protected Map<String, Double[]> tokenFormatedLL = new HashMap<>();

    private final String FROM = "FROM";
    private final String ELEMENT1 = "element1";
    private final String ELEMENT2 = "element2";
    private final String TOKEN = "token";

    TokenModel tokenmodel;

    private int tempIteration;
    private double tempProbability;
    private Date tempDateTime;

    public int getTempIteration() {
        return tempIteration;
    }

    public double getTempProbability() {
        return tempProbability;
    }

    public Date getTempDateTime() {
        return tempDateTime;
    }

    public void setTempDateTime(Date tempDateTime) {
        this.tempDateTime = tempDateTime;
    }

    public void setTempIteration(int tempIteration) {
        this.tempIteration = tempIteration;
    }

    public void setTempProbability(double tempProbability) {
        this.tempProbability = tempProbability;
    }

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

    public double[] getTokenElement2LatLong() {
        double[] tempDouble = new double[2];
        String tempString[] = tokenFormatedLL.toString().split(", ");
        tempDouble[0] = Double.valueOf(tempString[1]);
        tempDouble[1] = Double.valueOf(tempString[2]);
        return tempDouble;
    }

}
