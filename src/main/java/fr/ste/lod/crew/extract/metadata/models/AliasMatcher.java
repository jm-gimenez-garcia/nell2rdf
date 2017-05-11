/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;

import fr.ste.lod.crew.extract.metadata.util.Utility;
import static fr.ste.lod.crew.extract.metadata.models.ConstantList.*;
import java.util.Date;


/**
 *
 * @author Maisa
 */
public class AliasMatcher extends Header {

     //[Freebase 7/9/2012]
    private Date FreebaseDate;

    public AliasMatcher(String str, double Probability) {
        super(str, ALIASMATCHER, Probability);
    }

    @Override
    public void processStringText(String str) {
        this.FreebaseDate = Utility.setDateTimeFormatFreebase(Utility.getAliasMatcherFreebase(str));
    }

    public Date getFreebaseDate() {
        return FreebaseDate;
    }

    @Override
    public String toString() {
        return super.toString() + " " + getFreebaseDate() + "]";
    }

    @Override
    public String getStringSource() {
        return this.FreebaseDate.toString();
    }
}
