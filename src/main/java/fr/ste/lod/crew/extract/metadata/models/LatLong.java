/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;

import fr.ste.lod.crew.extract.metadata.util.Utility;

/**
 *
 * @author Maisa
 */
public class LatLong extends Header {

    public LatLong(String str, String ComponentName, double Probability) {
        super(str, ComponentName, Probability);
    }

    @Override
    public void processStringText(String str) {
        /*  if (this.componentName.equalsIgnoreCase(LATLONG)) {
            temp = Utility.getLatog(str);
        } else if (this.componentName.equalsIgnoreCase(LATLONGTT)) {
            temp = Utility.getLatogTT(str);
        }*/
    }

    @Override
    public void setToken(String str) {
        String temp = Utility.getTokenLatLong(str);
        if (!temp.isEmpty()) {
            String tempSlip[] = temp.split(",");
            mapToken.put("token", new String[]{tempSlip[0], tempSlip[1], tempSlip[2]});
        }
    }

    @Override
    public String toString() {
        StringBuffer temp = new StringBuffer();
        temp.append("[").append("[ComponentName: ").append(this.componentName).append(" {");
        temp.append(mapToken.get("token")[0]).append(",")
                .append(mapToken.get("token")[1]).append(",")
                .append(mapToken.get("token")[2]).append("}");
        return super.toString() + temp.toString();
    }

    @Override
    public String getStringSource() {
        StringBuffer temp = new StringBuffer();
        temp.append("[").append("[ComponentName: ").append(this.componentName).append(" {");
        temp.append(mapToken.get("token")[0]).append(",")
                .append(mapToken.get("token")[1]).append(",")
                .append(mapToken.get("token")[2]).append("}");
        return temp.toString();
    }

    public double[] getTokenElement2LatLong() {
        double[] tempDouble = new double[2];
        String tempString[] = tokenFormatedLL.toString().split(", ");
        tempDouble[0] = Double.valueOf(tempString[1]);
        tempDouble[1] = Double.valueOf(tempString[2]);
        return tempDouble;
    }

}
