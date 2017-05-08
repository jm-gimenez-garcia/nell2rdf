/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;


import fr.ste.lod.crew.extract.metadata.util.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Maisa
 */
public class SEAL extends Header {

    private String from;
    private String fromComplete;

    private List<String> listURL;

    public SEAL(String str,double Probability) {
        super(str, "SEAL", Probability);
    }

    public String getFrom() {
        return this.from;
    }

    public String getFromComplete() {
        return fromComplete;
    }
    private void setFrom(String str) {
        if (str.toLowerCase().contains("category")) {
            this.from = "Category";
        } else {
            this.from = "Relation";
        }
    }

    private void setFromComplete(String str) {
        this.fromComplete = Utility.getFromComplete(str);
    }

    public List<String> getListURL() {
        return this.listURL;
    }

    @Override
    public String toString() {
        StringBuffer temp = new StringBuffer();
        temp.append(" FROM: ").append(getFromComplete());
        temp.append(" {");
        this.listURL.forEach((str) -> {
            temp.append('\t').append(str);
        });
        temp.append("}");
        return super.toString() + temp.toString() + "]";
    }

    @Override
    public void processStringText(String str) {
        str = str.trim();
        setFrom(str);
        setFromComplete(str);
        listURL = new ArrayList<>();

        Pattern pattern = Pattern.compile(Utility.REGEX_SEAL_SOURCE);
        Matcher matcher = pattern.matcher(str);
        matcher.find();

        String srtSplit[] = matcher.group().trim().split(" ");
        listURL.addAll(Arrays.asList(srtSplit));
    }
}
