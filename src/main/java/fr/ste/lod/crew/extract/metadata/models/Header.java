/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;


import fr.ste.lod.crew.extract.metadata.util.Utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Maisa
 */
public abstract class Header {

    private final String source;
    protected String componentName;
    private List<Integer> iteration;

    private String dateTime;

    protected Map<String, String[]> mapToken;

    abstract public void processStringText(String str);

    public Header(String str, String ComponentName) {
        this.mapToken = new HashMap<>();
        this.componentName = ComponentName;
        this.source = str;
        processStringText(this.source);
        this.dateTime = Utility.getDateTime(Utility.getComponentsHeader(str));
        this.setIterations(str);

        setToken(str);

    }

    public String getSource() {
        return source;
    }

    public List<Integer> getIteration() {
        return iteration;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setToken(String str) {
        String temp = Utility.getToken(str);
        if (!temp.isEmpty()) {
            String tempSlip[] = temp.split(",");
            mapToken.put("token", new String[]{tempSlip[0], tempSlip[1]});
        } else {
            mapToken.put("token", new String[]{"", ""});
        }
    }

    public void setIterations(String str) {
        String temp = "";
        try {
            temp = str.substring(str.indexOf(":") + 1, str.indexOf("-".concat(this.dateTime)));
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("teste");
        }
        String tempSplit[] = temp.trim().split("-");
        this.iteration = new ArrayList<>();
        for (String iter : tempSplit) {
            try {
                this.iteration.add(Integer.valueOf(iter));
            } catch (NumberFormatException e) {
                System.err.println(str);
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer output = new StringBuffer();
        //Component Name
        output.append("[ComponentName: ").append(this.componentName).append("\t");
        //Iterations
        output.append("Iterations: ");
        int i = 0;
        output.append(this.iteration.get(i));
        for (i = 1; i < this.iteration.size(); i++) {
            output.append("-").append(this.iteration.get(i));
        }
        output.append("\t");
        //Datetime
        output.append("Datetime: ").append(this.dateTime).append("\t");
        //Tokens
        String[] tempKey = mapToken.get("token");
        try {
            output.append("Tokens: <").
                    append(tempKey[0]).append(",").
                    append(tempKey[1]);
        } catch (NullPointerException e) {
            System.out.println("OutO");
        }
        if (this.componentName.contains("Lat")) {
            output.append(",").append(tempKey[2]);
        }
        output.append(">");
        return output.toString();

    }

}
