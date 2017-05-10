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
public class LatLong {

    private final String source;
    //if LatLongTT então -geonames: 
    private LatLong_aux LLong;
    private LatLong_aux LLongTT;

    public LatLong_aux getLLong() {
        return LLong;
    }

    public LatLong_aux getLLongTT() {
        return LLongTT;
    }

    public String getSource() {
        return source;
    }

    public LatLong(String str, double Probability) {
        this.source = str;
        String strSplit[] = str.split("#L");

        switch (strSplit.length) {
            case 1:
                if (strSplit[0].contains("LatLong")) {
                    LLong = new LatLong_aux(strSplit[0].trim(), "LatLong", Probability);
                } else if (strSplit[0].contains("LatLongTT")) {
                    LLongTT = new LatLong_aux(strSplit[1].trim(), "LatLongTT", Probability);
                }
                break;
            case 2:
                LLong = new LatLong_aux(strSplit[0].trim(), "LatLong", Probability);
                LLongTT = new LatLong_aux(strSplit[1].trim(), "LatLongTT", Probability);
                break;
            default:
                System.out.println("Exceção de um trem");
                break;
        }
    }

    public class LatLong_aux extends Header {

        private String string;
        private double X;
        private double Y;

        public LatLong_aux(String str, String ComponentName, double Probability) {
            super(str, ComponentName, Probability);
        }

        public String getString() {
            return string;
        }

        public double getX() {
            return X;
        }

        public double getY() {
            return Y;
        }

        @Override
        public void processStringText(String str) {
            String temp = "";
            String tempSplit[];
            if (this.componentName.equalsIgnoreCase("LatLong")) {
                temp = Utility.getLatog(str);
            } else if (this.componentName.equalsIgnoreCase("LatLongTT")) {
                temp = Utility.getLatogTT(str);
            }

            temp = Utility.getLatogAtribute(temp);
            temp = Utility.removeDuplicatedBlanks(temp.replace("@", ","));
            tempSplit = temp.split(",");
            try {
                this.string = tempSplit[0];
                this.X = Double.valueOf(tempSplit[1]);
                this.Y = Double.valueOf(tempSplit[2]);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                System.out.println("AQIO");
            }

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
            temp.append("[").append("[ComponentName: ").append(getComponentName()).append(" {");
            temp.append(getString()).append("@").append(getX()).append(",").append(getY()).append("}");
            return temp.toString();
        }

        @Override
        public String getStringSource() {
            return toString();
        }

    }

    @Override
    public String toString() {
        StringBuffer temp = new StringBuffer();

        if (getLLong() != null) {
            temp.append(getLLong().toString());
        }
        if (getLLongTT() != null) {
            temp.append(getLLongTT().toString());
        }
        return super.toString() + temp.toString() + "]";
    }

    public String getStringSource() {
        return toString();
    }

}
