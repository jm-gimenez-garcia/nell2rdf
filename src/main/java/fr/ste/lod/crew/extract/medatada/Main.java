/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.medatada;

import java.io.IOException;

/**
 *
 * @author Maisa
 */
public class Main {

    //public static String fileNELLcsv = "D:\\Google-Drive\\PG\\RDF_NELL\\Teste_NELL.08m.1045.esv.csv";
    public static String fileNELLcsv = "D:\\Google-Drive\\PG\\NELL2RDF\\NELL.08m.1050.esv.csv";
    public static String fileOut = "D:\\Google-Drive\\PG\\NELL2RDF\\";
    public static String fileOutToString = "D:\\fileOutToString";
   

    public static void main(String[] args) throws IOException {

        ManipulationExecution KBManip = new ManipulationExecution();
        KBManip.readNELLcsv(fileNELLcsv, fileOut);
    }
}
