/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Maisa
 */
public class Utility {

    private static final String REGEX_HYBRID_NEW = "([a-zA-Z-0-9:])*((\\d{4}\\/\\d{2}\\/\\d{2})\\-(\\d{1,2}\\:\\d{2}\\:\\d{2}))";
    private static final String REGEX_DATETIME_NEW = "((\\d{4}\\/\\d{2}\\/\\d{2})( |\\-)(\\d{1,2}\\:\\d{2}\\:\\d{2}))";
    private static final String REGEX_HYBRID_OLD = "([a-zA-Z-0-9:])*(((\\d{4}-\\d{2}-\\d{2})) (\\d{1,2}\\:\\d{2}\\:\\d{2}))";
    private static final String REGEX_DATETIME_OLD = "(((\\d{4}-\\d{2}-\\d{2}))( |\\-)(\\d{1,2}\\:\\d{2}\\:\\d{2}))";
    public static final String REGEX_COMPONENTS_NAME = "([A-Z]+)(\\([a-z_]*),([a-z_]*)\\)";
    //private static final String REGEX_ITERATION = "(Iter:)[0-9]+"; //It's not getting a list, so we are not using this

    private static final String REGEX_TOKEN = "(?<=(<token=))(([a-z_0-9]*)(,){0,1}([a-z_0-9]*))";
    private static final String REGEX_TOKEN_LATLONG = "(?<=(<token=))((([a-z_0-9\\.\\-]*)(,)([a-z_0-9\\.\\-]*)(,)[a-z_0-9\\.\\-]*))";
    private static final String REGEX_LATLONG_VALUES = "(?<=(>-))(.*)";
    private static final String REGEX_LATLONGTT_GEONAMES = "((?<=(>-)|(-geonames:))(.*))";
    private static final String REGEX_LATLONG_ALL_ATRIBUTES = "([A-Za-z ])*(\\@)([0-9-\\.,])*";
    private static final String REGEX_ONTOLOGYMODIFIER = "(?<=((\\d{2}\\:\\d{2}\\:\\d{2}))[-<])";

    //private static final String REGEX_TOKEN = "(?<=(<token=))(([a-z_0-9]+),([a-z_0-9]+))";
    private static final String REGEX_FROM = "(From:)+([^ ])*";
    //get the column SOURCE
    private static final String REGEX_SOURCE = "(MBL-Iter:)(.+?(?=(\\[)))";
    //get only the "column" CANDIDATE SOURCE
    private static final String REGEX_SOURCE_TOYS = "(\\[)(.)*";
    private static final String REGEX_SPREAD_SHEET_USER = "(?<=(>-))(.*)(?=(: ))";
    private static final String REGEX_SPREAD_SHEET_ERV = "(?<=(USER:))(.*)(?=(Action))";
    private static final String REGEX_SPREAD_SHEET_ACTION = "(?<=(Action=\\())([a-zA-Z-0-9\\+\\-])*(?=(\\)))";
    private static final String REGEX_SPREAD_SHEET_FROM = "(?<=(\\(from))(.*)(?=(\\)))";

    private static final String REGEX_RULE_INFERENCE = "(?<=(\\{))(.*)(?=(\\}))";
    private static final String REGEX_PRA = "(?<=(>-))(.)*";
    private static final String REGEX_SEMPARSE = "(?<=(>-))(.)*";
    private static final String REGEX_ALIAS_MATCHER = "(?<=(\\[))(.)*(?=(\\]))";
    private static final String REGEX_ALIAS_MATCHER_FREEBASE = "(?<=(>-))(.)*";

    public static final String REGEX_CML_SOURCE = "(([a-zA-Z\\, ]+( \\_\\t))[0-9]+)";
    public static final String REGEX_CPL_SOURCE = "(arg[12] [a-zA-Z\\, ]+arg[12])\\t([0-9]+)";
    public static final String REGEX_SEAL_SOURCE = "(?<=(-using-KB))([^\\,])*";
    public static final String REGEX_OE_SOURCE = "(?<=(>-))(.)*";
    public static final String REGEX_CMC_SOURCE_FLOAT = "((([A-Z\\_=]+)([A-Za-z\\_])+)(\\t)([0-9.\\-])+)";
    public static final String REGEX_CMC_SOURCE_STRING = "(([A-Z=]+)([a-z])+)";
    public static final String REGEX_MBL = "(?<=(-<token=>-))(.)*";
    private static final String REGEX_HEADER_ALL_COMPONENTS = "(?<=(:))(.)([ 0-9-\\/:])+";

    //static final String REGEX_OE_WEBSITE_SOURCE = "([a-zA-Z]{3,}):\\/\\/([\\w-]+\\.)+[\\w-]+(\\/[\\w- .\\/?%&\+=]*)?";
    private static String extract(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        String temp = "";
        if (matcher.find()) {
            temp = matcher.group();
        }
        return temp.trim();
    }

    public static String getDateTime(String str) {
        Pattern pattern = Pattern.compile(REGEX_DATETIME_NEW);
        Matcher matcher = pattern.matcher(str);
        String temp = "";
        if (matcher.find()) {
            temp = matcher.group();
        } else {
            pattern = Pattern.compile(REGEX_DATETIME_OLD);
            matcher = pattern.matcher(str);
            if (matcher.find()) {
                temp = matcher.group();
            } else {
                System.err.println("AQUI: " + str);
            }

        }

        return temp;
    }

    public static String getComponentsHeader(String str) {
        return extract(str, REGEX_HEADER_ALL_COMPONENTS);
    }

    public static String getLatog(String str) {
        return extract(str, REGEX_LATLONG_VALUES).trim();
    }

    public static String getOntologyModifier(String str) {
        return extract(str, REGEX_ONTOLOGYMODIFIER).trim();
    }

    public static String getLatogAtribute(String str) {
        return extract(str, REGEX_LATLONG_ALL_ATRIBUTES).trim();
    }

    public static String getLatogTT(String str) {
        return extract(str, REGEX_LATLONGTT_GEONAMES).trim();
    }

    public static String getAliasMatcherFreebase(String str) {
        return extract(str, REGEX_ALIAS_MATCHER_FREEBASE);
    }

    public static String getOldBug(String str) {
        return extract(str, REGEX_ALIAS_MATCHER_FREEBASE);
    }

    public static String getPRA(String str) {
        return extract(str, REGEX_PRA);
    }

    public static String getSEMPARSE(String str) {
        return extract(str, REGEX_SEMPARSE);
    }

    public static String getSource(String str) {
        return extract(str, REGEX_SOURCE);
    }

    public static String getRuleInference(String str) {
        return extract(str, REGEX_RULE_INFERENCE);
    }

    public static String getMBLCandidateSource(String str) {
        return extract(str, REGEX_MBL);
    }

    public static String getToken(String str) {
        return extract(str, REGEX_TOKEN);
    }

    public static String getTokenLatLong(String str) {
        return extract(str, REGEX_TOKEN_LATLONG);
    }

    public static String getFromComplete(String str) {
        return extract(str, REGEX_FROM);
    }

    public static String getCandidateSource(String str) {
        return extract(str, REGEX_SOURCE_TOYS);
    }

    public static String getSpreadSheetUserFeedback(String str) {
        return extract(str, REGEX_SPREAD_SHEET_USER);
    }

    public static String getSpreadSheetERV(String str, String user) {
        return extract(str, REGEX_SPREAD_SHEET_ERV.replace("USER", user))
                .replace(",", "").replace("\"", "").trim();
    }

    public static String getSpreadSheetAction(String str) {
        return extract(str, REGEX_SPREAD_SHEET_ACTION);
    }

    public static String getSpreadSheetFrom(String str) {
        return extract(str, REGEX_SPREAD_SHEET_FROM);
    }

    //Tryng to skip the mistakes between the extraction of TP's 
    //with comma and the comma that separate the components
    private static String engineeringSolution(String str) {
        String temp;
        temp = str.replace("[", "");
        temp = temp.replace("]", "");
        temp = temp.replace(", OntologyModifier-Iter:", " #,# OntologyModifier-Iter:");
        temp = temp.replace(", CPL-Iter:", " #,# CPL-Iter:");
        temp = temp.replace(", SEAL-Iter:", " #,# SEAL-Iter:");
        temp = temp.replace(", OE-Iter:", " #,# OE-Iter:");
        temp = temp.replace(", CMC-Iter:", " #,# CMC-Iter:");
        temp = temp.replace(", AliasMatcher-Iter:", " #,# AliasMatcher-Iter:");
        temp = temp.replace(", MBL-Iter:", " #,# MBL-Iter:");
        temp = temp.replace(", PRA-Iter:", " #,# PRA-Iter:");
        temp = temp.replace(", RuleInference-Iter:", " #,# RuleInference-Iter:");
        temp = temp.replace(", KbManipulation-Iter:", " #,# KbManipulation-Iter:");
        temp = temp.replace(", Semparse-Iter:", " #,# Semparse-Iter:");
        temp = temp.replace(", LE-Iter:", " #,# LE-Iter:");
        temp = temp.replace(", SpreadsheetEdits-Iter:", " #,# SpreadsheetEdits-Iter:");
        temp = temp.replace(", LatLong-Iter:", " #,# LatLong-Iter:");
        //Like this because this is the unique componente that has twice of the format + Header
        temp = temp.replace(", LatLongTT-Iter:", "#LLatLongTT-Iter:");

        return temp;
    }

    //Each component is represented by a line
    public static List getSTRperComponents(String str) {
        List<String> listStrings = new ArrayList<>();
        String temp[] = engineeringSolution(str).split(" #,# ");
        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].replace(" #,# ", "");
        }
        listStrings.addAll(Arrays.asList(temp));
        return listStrings;
    }

    public static String DecodeURL(String str) {
        String result = "";
        try {
            result = (java.net.URLDecoder.decode(str, "UTF-8"));
        } catch (IllegalArgumentException | java.lang.StringIndexOutOfBoundsException ex) {
            System.out.println(ex);
            return null;
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    public static String removeDuplicatedBlanks(String str) {
        String patternStr = "\\s+";
        String replaceStr = " ";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        return (matcher.replaceAll(replaceStr)).trim();
    }

    public static void writeStringBuffer(StringBuffer str, String path, boolean next) throws IOException {
        //write contents of StringBuffer to a file
        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(path), next))) {
            //write contents of StringBuffer to a file
            bwr.write(str.toString());
            //flush the stream
            bwr.flush();
            //close the stream
        }
    }

}