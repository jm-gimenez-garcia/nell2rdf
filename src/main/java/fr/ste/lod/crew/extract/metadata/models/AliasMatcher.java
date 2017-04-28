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
public class AliasMatcher extends Header {

    //[Freebase 7/9/2012]
    private String FreebaseDate;

    public AliasMatcher(String str) {
        super(str, "AliasMatcher");
    }

    @Override
    public void processStringText(String str) {
        this.FreebaseDate = Utility.getAliasMatcherFreebase(str);
    }

    public String getFreebaseDate() {
        return FreebaseDate;
    }

    @Override
    public String toString() {
        return super.toString() + " "+ getFreebaseDate() + "]";
    }

}
