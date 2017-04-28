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
public class MBL extends Header {

    private String promotionOfConcept;

    public String getPromotionOfConcept() {
        return promotionOfConcept;
    }

    public MBL(String str) {
        super(str, "MBL");
    }

    @Override
    public void processStringText(String str) {
        this.promotionOfConcept = Utility.getMBLCandidateSource(str);
    }

    @Override
    public String toString() {
        return super.toString() + " " + getPromotionOfConcept() + "]";
    }

}
