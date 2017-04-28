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
public class OntologyModifier extends Header {

    private String from;

    public OntologyModifier(String str) {
        super(str, "OntologyModifier");
    }

    public String getFrom() {
        return this.from;
    }

    private void setFrom(String from) {
        this.from = from;
    }

    @Override
    public void processStringText(String str) {
        setFrom(Utility.getOntologyModifier(str));
    }

    @Override
    public String toString() {
        return super.toString() + " FROM: "+ this.from + ']';
    }
    
    

}
