/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.test;


import fr.ste.lod.crew.extract.metadata.models.AliasMatcher;
import fr.ste.lod.crew.extract.metadata.models.CMC;
import fr.ste.lod.crew.extract.metadata.models.CPL;
import fr.ste.lod.crew.extract.metadata.models.KbManipulation;
import fr.ste.lod.crew.extract.metadata.models.LE;
import fr.ste.lod.crew.extract.metadata.models.LatLong;
import fr.ste.lod.crew.extract.metadata.models.LineInstanceJOIN;
import fr.ste.lod.crew.extract.metadata.models.MBL;
import fr.ste.lod.crew.extract.metadata.models.OE;
import fr.ste.lod.crew.extract.metadata.models.OntologyModifier;
import fr.ste.lod.crew.extract.metadata.models.PRA;
import fr.ste.lod.crew.extract.metadata.models.RuleInference;
import fr.ste.lod.crew.extract.metadata.models.SEAL;
import fr.ste.lod.crew.extract.metadata.models.Semparse;
import fr.ste.lod.crew.extract.metadata.models.SpreadsheetEdits;
import fr.ste.lod.crew.extract.metadata.util.Utility;
import fr.ste.lod.crew.extract.metadata.models.ConstantList;
import fr.ste.lod.crew.extract.metadata.models.FormatHeader;
import fr.ste.lod.crew.extract.metadata.models.Header;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maisa
 */
public class ManipulationExecution {

    private LineInstanceJOIN LI;

    public void setFeatures(String line) {

        String[] split = line.split("\t");
        LI = new LineInstanceJOIN(split[0], split[1], split[2], split[3], split[4],
                Utility.DecodeURL(split[5]), split[6], split[7],
                split[8], split[9], split[10],
                split[11], Utility.DecodeURL(split[12]), line, true);
    }

    public void readNELLcsv(String pathIN, String pathOUT) throws FileNotFoundException, IOException {

        BufferedReader reader = new BufferedReader(new FileReader(pathIN));
        String line = reader.readLine();
        System.out.println(line);
        System.out.println("Iniciando processamento");

        StringBuffer temp = new StringBuffer();

        while ((line = reader.readLine()) != null) {
            setFeatures(line);
            Map<String, Header> p = LI.getListComponents();

            temp.append("START: \t");
            p.entrySet().forEach((pair) -> {
                String key = pair.getKey();

                switch (key) {
                    case ConstantList.ONTOLOGYMODIFIER:
                        temp.append(((OntologyModifier) pair.getValue()).toString());
                        //FormatHeader FH = ((OntologyModifier) pair.getValue()).getFormatHeader().
                                
                        break;
                    case ConstantList.CPL:
                        temp.append(((CPL) pair.getValue()).toString());
                        /*JSON_CPL_CML jsonCPL = new JSON_CPL_CML(pair.getValue());
                        jsonCPL.setJsonObject();
                         {
                            try {
                                Utility.writeJsonFile(jsonCPL.getJsonObject(), Main.fileOutToString + "teste", true);
                            } catch (IOException ex) {
                                Logger.getLogger(ManipulationExecution.class.getName()).log(Level.SEVERE, null, ex)
                            }
                        }*/
                        break;
                    case ConstantList.SEAL:
                        temp.append(((SEAL) pair.getValue()).toString());
                        break;
                    case ConstantList.OE:
                        temp.append(((OE) pair.getValue()).toString());
                        break;
                    case ConstantList.CMC:
                        temp.append(((CMC) pair.getValue()).toString());
                        break;
                    case ConstantList.ALIASMATCHER:
                        temp.append(((AliasMatcher) pair.getValue()).toString());
                        /*  {
                            JSON_AliasMatcher jsonAlias = new JSON_AliasMatcher(pair.getValue());
                            try {
                                Utility.writeJsonFile(jsonAlias.getJsonObject(), Main.fileOutToString + "teste", true);
                            } catch (IOException ex) {
                                Logger.getLogger(ManipulationExecution.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }*/
                        break;
                    case ConstantList.MBL:
                        temp.append(((MBL) pair.getValue()).toString());
                        break;
                    case ConstantList.PRA:
                        temp.append(((PRA) pair.getValue()).toString());
                        break;
                    case ConstantList.RULEINFERENCE:
                        temp.append(((RuleInference) pair.getValue()).toString());
                        break;
                    case ConstantList.KBMANIPULATION:
                        temp.append(((KbManipulation) pair.getValue()).toString());
                        break;
                    case ConstantList.SEMPARSE:
                        temp.append(((Semparse) pair.getValue()).toString());
                        break;
                    case ConstantList.LE:
                        temp.append(((LE) pair.getValue()).toString());
                        break;
                    case ConstantList.SPREADSHEETEDITS:
                        temp.append(((SpreadsheetEdits) pair.getValue()).toString());
                        break;
                    case ConstantList.LATLONG:
                    case ConstantList.LATLONGTT:
                        temp.append(((LatLong) pair.getValue()).toString());
                        System.out.println(Arrays.toString(((LatLong) pair.getValue()).getFormatHeader().getTokenElement2LatLong()));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid Component Name: " + key);
                }

                temp.append("\t");

            }
            );
            temp.append("END").append("\n");
            if (temp.length() > 10000000) {
                try {
                    Utility.writeStringBuffer(temp, Main.fileOutToString, true);
                } catch (IOException ex) {
                    Logger.getLogger(LineInstanceJOIN.class.getName()).log(Level.SEVERE, null, ex);
                }
                temp.delete(0, temp.length());
            }
        }

        try {
            Utility.writeStringBuffer(temp, Main.fileOutToString, true);
        } catch (IOException ex) {
            Logger.getLogger(LineInstanceJOIN.class.getName()).log(Level.SEVERE, null, ex);
        }
        temp.delete(0, temp.length());

        System.out.println("Finalizado");

    }
}
