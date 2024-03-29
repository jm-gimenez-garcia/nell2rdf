/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ste.lod.crew.extract.metadata.models;

import static fr.ste.lod.crew.extract.metadata.util.ConstantList.RULEINFERENCE;
import fr.ste.lod.crew.extract.metadata.util.Utility;

/**
 *
 * @author Maisa
 */
public class RuleInference extends Header {

   private String rules;

    public RuleInference(String str, double Probability) {
        super(str, RULEINFERENCE, Probability);
    }

    @Override
    public void processStringText(String str) {
        this.rules = Utility.getRuleInference(str);
    }

    public String getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return super.toString() + "{" + getRules() + "}]";
    }

    @Override
    public String getStringSource() {
        return "{" + getRules() + "}";
    }
}
