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
public class KbManipulation extends Header {

    private String oldBug;

    public KbManipulation(String str, double Probability) {
        super(str, "KbManipulation", Probability);
    }

    public String getOldBug() {
        return oldBug;
    }

    @Override
    public void processStringText(String str) {
        oldBug = Utility.getOldBug(str);
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.oldBug + "]";
    }

    @Override
    public String getStringSource() {
        return this.oldBug;
    }
}
