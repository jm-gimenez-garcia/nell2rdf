/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;


import fr.ste.lod.crew.extract.metadata.util.Utility;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Maisa
 */
public class OE extends Header {

   private Map<String, String> mapTextURL;

    public OE(String str,double Probability) {
        super(str,"OE", Probability);
    }

    public Map<String, String> getMapTPOccurence() {
        return mapTextURL;
    }

    private void setMapTPOccurence(String text, String url) {
        this.mapTextURL.put(text, url);
    }

    @Override
    public String toString() {
        
        StringBuffer temp = new StringBuffer();
        temp.append(" {");
        this.mapTextURL.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            String value = entry.getValue();
            temp.append(key).append('\t').append(value);
        });
        temp.append("}");
         
   
        return super.toString() + temp.toString() +"]";
    }

    

    @Override
    public void processStringText(String str) {
        this.mapTextURL = new HashMap<>();

        Pattern pattern = Pattern.compile(Utility.REGEX_OE_SOURCE);
        Matcher matcher = pattern.matcher(str);
        matcher.find();

        String temp[] = matcher.group().trim().split("\t");
        int i = 0;
        while (i < temp.length) {
            if ((i + 1) >= temp.length) {
                setMapTPOccurence(temp[i], "");
            } else {
                setMapTPOccurence(temp[i], temp[i + 1]);
            }
            i += 2;
        }
    }
}
