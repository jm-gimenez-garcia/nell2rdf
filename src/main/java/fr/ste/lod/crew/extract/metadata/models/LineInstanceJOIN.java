/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;
 
import fr.ste.lod.crew.extract.metadata.util.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Maisa
 */
public final class LineInstanceJOIN {

    private final String completeLine;
    static String CAT_OR_REL;

    private final String entity;
    private final String relation;
    private final String value;

    private List<Integer> nrIterations = new ArrayList<>();

    private List<Double> probability;

    //Object Responsable for the Source Column
    private final String source;
    private MBL_OR_ERC MBL_source;

    //Object Responsable for the Source Column
    private List<String> entityLiteralStrings;
    private List<String> valueLiteralStrings;
    private List<String> bestEntityLiteralString;
    private List<String> bestValueLiteralString;
    private List<String> categoriesForEntity;
    private List<String> categoriesForValue;

    private final String candidateSource;
    private Map<String, Object> listComponents;

    public void inicilizeObjets() {
        this.entityLiteralStrings = new ArrayList<>();
        this.valueLiteralStrings = new ArrayList<>();
        this.bestEntityLiteralString = new ArrayList<>();
        this.bestValueLiteralString = new ArrayList<>();
        this.categoriesForEntity = new ArrayList<>();
        this.categoriesForValue = new ArrayList<>();
        this.nrIterations = new ArrayList<>();
        this.probability = new ArrayList<>();

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
            String probabilityPROMOTION, String Source, String EntityLiteralStrings,
            String ValueLiteralStrings, String BestEntityLiteralString, String BestValueLiteralString,
            String CategoriesForEntity, String CategoriesForValue, String CandidatSource, String CompleteLine) {

        this.inicilizeObjets();

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
        this.setProbability(probabilityPROMOTION);
        this.setIterations(Iteration);

        this.entityLiteralStrings.addAll(Arrays.asList(organizeStringsExtraction(EntityLiteralStrings).split(",")));
        this.valueLiteralStrings.addAll(Arrays.asList(organizeStringsExtraction(ValueLiteralStrings).split(",")));
        this.bestEntityLiteralString.addAll(Arrays.asList(organizeStringsExtraction(BestEntityLiteralString).split(",")));
        this.bestValueLiteralString.addAll(Arrays.asList(organizeStringsExtraction(BestValueLiteralString).split(",")));
        this.categoriesForEntity.addAll(Arrays.asList(organizeStringsExtraction(CategoriesForEntity).split(",")));
        this.categoriesForValue.addAll(Arrays.asList(organizeStringsExtraction(CategoriesForValue).split(",")));

        this.completeLine = CompleteLine;

        this.candidateSource = CandidatSource;

        if (relation.equals("candidate:generalizations")) {
            CAT_OR_REL = "category";
        } else {
            CAT_OR_REL = "relation";
        }
        this.setListComponents(Utility.getSTRperComponents(Utility.getCandidateSource(this.candidateSource)), this.probability);

    }

    public Map<String, Object> getListComponents() {
        return listComponents;
    }

    public List<Double> getProbability() {
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

    public void setProbability(String str) {
        String temp = str.replace("]", "").replace("[", "");
        if (temp.contains(",")) {
            String strSplit[];
            strSplit = temp.split(", ");
            for (String srtTemp : strSplit) {
                this.probability.add(Double.valueOf(srtTemp));
            }
        } else {
            this.probability.add(Double.valueOf(temp));
        }
    }

    public void setIterations(String str) {
        String temp = str.replace("]", "").replace("[", "");
        if (temp.contains(",")) {
            String strSplit[];
            strSplit = temp.split(", ");
            for (String srtTemp : strSplit) {
                this.nrIterations.add(Integer.valueOf(srtTemp));
            }
        } else {
            this.nrIterations.add(Integer.valueOf(temp));
        }
    }

    //Macarronada Italiana
    //Here is where the componentes are created; [ aqui
    public void setListComponents(List<String> stringListComponents, List<Double> probList) {

        for (int i = 0; i < stringListComponents.size(); i++) {
            String line = stringListComponents.get(i);

            if (line.startsWith("OntologyModifier-Iter:")) {
                this.listComponents.put("OntologyModifier", new OntologyModifier(line, probList.get(i)));
            } else if (line.startsWith("CPL-Iter:")) {
                this.listComponents.put("CPL", new CPL_CML(line, probList.get(i)));
            } else if (line.startsWith("SEAL-Iter:")) {
                this.listComponents.put("SEAL", new SEAL(line, probList.get(i)));
            } else if (line.startsWith("OE-Iter:")) {
                this.listComponents.put("OE", new OE(line, probList.get(i)));
            } else if (line.startsWith("CMC-Iter:")) {
                this.listComponents.put("CMC", new CMC(line, probList.get(i)));
            } else if (line.startsWith("AliasMatcher-Iter:")) {
                this.listComponents.put("AliasMatcher", new AliasMatcher(line, probList.get(i)));
            } else if (line.startsWith("MBL-Iter:")) {
                this.listComponents.put("MBL", new MBL(line, probList.get(i)));
            } else if (line.startsWith("PRA-Iter")) {
                this.listComponents.put("PRA", new PRA(line, probList.get(i)));
            } else if (line.startsWith("RuleInference-Iter")) {
                this.listComponents.put("RuleInference", new RuleInference(line, probList.get(i)));
            } else if (line.startsWith("KbManipulation-Iter")) {
                this.listComponents.put("KbManipulation", new KbManipulation(line, probList.get(i)));
            } else if (line.startsWith("Semparse-Iter")) {
                this.listComponents.put("Semparse", new Semparse(line, probList.get(i)));
            } else if (line.startsWith("LE-Iter")) {
                this.listComponents.put("LE", new LE(line, probList.get(i)));
            } else if (line.startsWith("SpreadsheetEdits-Iter")) {
                this.listComponents.put("SpreadsheetEdits", new SpreadsheetEdits(line, probList.get(i)));
            } else if ((line.startsWith("LatLong-Iter") || (line.startsWith("LatLongTT-Iter")))) {
                this.listComponents.put("LatLong", new LatLong(line, probList.get(i)));
            }
        }
    }

}
