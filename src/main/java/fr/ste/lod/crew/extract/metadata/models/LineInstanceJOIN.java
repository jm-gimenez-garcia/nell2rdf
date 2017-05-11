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

        if (relation.equals(ConstantList.LOOK_GENERALIZATIONS)) {
            CAT_OR_REL = ConstantList.CATEGORY;
        } else {
            CAT_OR_REL = ConstantList.RELATION;
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

            //ONTOLOGY MODIFIER
            if (line.startsWith(ConstantList.TEXT_ONTOLOGYMODIFIER)) {
                this.listComponents.put(ConstantList.ONTOLOGYMODIFIER, new OntologyModifier(line, probList.get(i)));
                //ONTOLOGY CPL
            } else if (line.startsWith(ConstantList.TEXT_CPL)) {
                this.listComponents.put(ConstantList.CPL, new CPL(line, probList.get(i)));
                //SEAL
            } else if (line.startsWith(ConstantList.TEXT_SEAL)) {
                this.listComponents.put(ConstantList.SEAL, new SEAL(line, probList.get(i)));
                //OPEN EVAL
            } else if (line.startsWith(ConstantList.TEXT_OE)) {
                this.listComponents.put(ConstantList.OE, new OE(line, probList.get(i)));
                 //CMU
            } else if (line.startsWith(ConstantList.TEXT_CMC)) {
                this.listComponents.put(ConstantList.CMC, new CMC(line, probList.get(i)));
                 //ALIAS MATCHER
            } else if (line.startsWith(ConstantList.TEXT_ALIASMATCHER)) {
                this.listComponents.put(ConstantList.ALIASMATCHER, new AliasMatcher(line, probList.get(i)));
                 //MBL
            } else if (line.startsWith(ConstantList.TEXT_MBL)) {
                this.listComponents.put(ConstantList.MBL, new MBL(line, probList.get(i)));
                 //PRA
            } else if (line.startsWith(ConstantList.TEXT_PRA)) {
                this.listComponents.put(ConstantList.PRA, new PRA(line, probList.get(i)));
                 //RULE INFERENCE
            } else if (line.startsWith(ConstantList.TEXT_RULEINFERENCE)) {
                this.listComponents.put(ConstantList.RULEINFERENCE, new RuleInference(line, probList.get(i)));
                 //KB MANIPULATION
            } else if (line.startsWith(ConstantList.TEXT_KBMANIPULATION)) {
                this.listComponents.put(ConstantList.KBMANIPULATION, new KbManipulation(line, probList.get(i)));
                 //SEMPARSE
            } else if (line.startsWith(ConstantList.TEXT_SEMPARSE)) {
                this.listComponents.put(ConstantList.SEMPARSE, new Semparse(line, probList.get(i)));
                //LE
            } else if (line.startsWith(ConstantList.TEXT_LE)) {
                this.listComponents.put(ConstantList.LE, new LE(line, probList.get(i)));
                 //SPREADSHEET EDITS
            } else if (line.startsWith(ConstantList.TEXT_SPREADSHEETEDITS)) {
                this.listComponents.put(ConstantList.SPREADSHEETEDITS, new SpreadsheetEdits(line, probList.get(i)));
                //LATLONG & LATLONGTT
            } else if ((line.startsWith(ConstantList.TEXT_LATLONG) || (line.startsWith(ConstantList.TEXT_LATLONGTT)))) {
                this.listComponents.put(ConstantList.LATLONG, new LatLong(line, probList.get(i)));
            }
        }
    }

}
