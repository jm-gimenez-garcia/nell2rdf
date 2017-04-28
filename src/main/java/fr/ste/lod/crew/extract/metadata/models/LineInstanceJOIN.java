/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;
 
import fr.ste.lod.crew.extract.medatada.Main;
import fr.ste.lod.crew.extract.metadata.util.Utility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maisa
 */
public final class LineInstanceJOIN {

    private final String completeLine;

    private final String entity;
    private final String relation;
    private final String value;

    private List<Integer> nrIterations = new ArrayList<>();

    private double probability;

    //Object Responsable for the Source Column
    private String source;
    private MBL_OR_ERC MBL_source;

    //Object Responsable for the Source Column
    private List<String> entityLiteralStrings;
    private List<String> valueLiteralStrings;
    private List<String> bestEntityLiteralString;
    private List<String> bestValueLiteralString;
    private List<String> categoriesForEntity;
    private List<String> categoriesForValue;

    private String candidateSource;
    private Map<String, Object> listComponents;

    public void setProbability(double Probability) {
        this.probability = Probability;
    }

    public void inicilizeObjets() {
        this.entityLiteralStrings = new ArrayList<>();
        this.valueLiteralStrings = new ArrayList<>();
        this.bestEntityLiteralString = new ArrayList<>();
        this.bestValueLiteralString = new ArrayList<>();
        this.categoriesForEntity = new ArrayList<>();
        this.categoriesForValue = new ArrayList<>();

        listComponents = new HashMap<>();
    }

    public String organizeStringsExtraction(String str) {
        str = str.trim();
        if (!str.isEmpty()) {
            try {
                str = str.replace("\" ", "\", ");
                str = str.replace("\"", "").trim();
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("fa");
            }
        }
        return str;
    }

    public LineInstanceJOIN(String Entity, String Relation, String Value, String Iteration,
            double probabilityPROMOTION, String Source, String EntityLiteralStrings,
            String ValueLiteralStrings, String BestEntityLiteralString, String BestValueLiteralString,
            String CategoriesForEntity, String CategoriesForValue, String CandidatSource, String CompleteLine) {

        inicilizeObjets();

        String tempMBLorERC = "";
        this.source = Source;

        if (!"(null)".equals(this.source)) {
            if (Source.contains("EntityResolverCleanup-Iter:")) {
                tempMBLorERC = "EntityResolverCleanup";
            } else if (Source.contains("MBL-Iter:")) {
                tempMBLorERC = "MBL";
            } else {
                System.out.println(Source);
            }
            this.MBL_source = new MBL_OR_ERC(Source, tempMBLorERC);
        }

        this.entity = Entity;
        this.relation = Relation;
        this.value = Value;
        this.probability = probabilityPROMOTION;
        this.setIterations(Iteration);

        this.entityLiteralStrings.addAll(Arrays.asList(organizeStringsExtraction(EntityLiteralStrings).split(",")));
        this.valueLiteralStrings.addAll(Arrays.asList(organizeStringsExtraction(ValueLiteralStrings).split(",")));
        this.bestEntityLiteralString.addAll(Arrays.asList(organizeStringsExtraction(BestEntityLiteralString).split(",")));
        this.bestValueLiteralString.addAll(Arrays.asList(organizeStringsExtraction(BestValueLiteralString).split(",")));
        this.categoriesForEntity.addAll(Arrays.asList(organizeStringsExtraction(CategoriesForEntity).split(",")));
        this.categoriesForValue.addAll(Arrays.asList(organizeStringsExtraction(CategoriesForValue).split(",")));

        this.completeLine = CompleteLine;

        this.candidateSource = CandidatSource;
        setListComponents(Utility.getSTRperComponents(Utility.getCandidateSource(this.candidateSource)));
    }

    public Map<String, Object> getListComponents() {
        return listComponents;
    }

    public void setIterations(String str) {
        String strSplit[] = str.split(" ");
        for (String srtTemp : strSplit) {
            this.nrIterations.add(Integer.valueOf(srtTemp));
        }
    }

    //Macarronada Italiana
    //Here is where the componentes are created; [ aqui
    public void setListComponents(List<String> stringListComponents) {

        StringBuffer noComponent = new StringBuffer();
        for (int i = 0; i < stringListComponents.size(); i++) {
            String line = stringListComponents.get(i);

            if (line.startsWith("OntologyModifier-Iter:")) {
                this.listComponents.put("OntologyModifier", new OntologyModifier(line));
            } else if (line.startsWith("CPL-Iter:")) {
                this.listComponents.put("CPL", new CPL_CML(line));
            } else if (line.startsWith("SEAL-Iter:")) {
                this.listComponents.put("SEAL", new SEAL(line));
            } else if (line.startsWith("OE-Iter:")) {
                this.listComponents.put("OE", new OE(line));
            } else if (line.startsWith("CMC-Iter:")) {
                this.listComponents.put("CMC", new CMC(line));
            } else if (line.startsWith("AliasMatcher-Iter:")) {
                this.listComponents.put("AliasMatcher", new AliasMatcher(line));
            } else if (line.startsWith("MBL-Iter:")) {
                this.listComponents.put("MBL", new MBL(line));
            } else if (line.startsWith("PRA-Iter")) {
                this.listComponents.put("PRA", new PRA(line));
            } else if (line.startsWith("RuleInference-Iter")) {
                this.listComponents.put("RuleInference", new RuleInference(line));
            } else if (line.startsWith("KbManipulation-Iter")) {
                this.listComponents.put("KbManipulation", new KbManipulation(line));
            } else if (line.startsWith("Semparse-Iter")) {
                this.listComponents.put("Semparse", new Semparse(line));
            } else if (line.startsWith("LE-Iter")) {
                this.listComponents.put("LE", new LE(line));
            } else if (line.startsWith("SpreadsheetEdits-Iter")) {
                this.listComponents.put("SpreadsheetEdits", new SpreadsheetEdits(line));
            } else if ((line.startsWith("LatLong-Iter") || (line.startsWith("LatLongTT-Iter")))) {
                this.listComponents.put("LatLong", new LatLong(line));
            } else {
                noComponent.append("{").append(line).append("}").append(this.completeLine).append("\n");
                if (noComponent.length() > 10000000) {
                    try {
                        Utility.writeStringBuffer(noComponent, Main.fileOut + "NO_COMPONENT_FOUND", true);
                    } catch (IOException ex) {
                        Logger.getLogger(LineInstanceJOIN.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    noComponent.delete(0, noComponent.length());
                }
            }
        }

        try {
            Utility.writeStringBuffer(noComponent, Main.fileOut + "NO_COMPONENT_FOUND", true);
        } catch (IOException ex) {
            Logger.getLogger(LineInstanceJOIN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getProbability() {
        return probability;
    }

    public String getEntity() {
        return entity;
    }

    public String getRelation() {
        return relation;
    }

    public String getValue() {
        return value;
    }

    public List<Integer> getNrIterations() {
        return nrIterations;
    }

    public void setNrIterations(int iteration) {
        this.nrIterations.add(iteration);
    }

}
