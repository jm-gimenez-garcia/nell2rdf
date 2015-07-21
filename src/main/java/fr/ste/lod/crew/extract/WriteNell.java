package fr.ste.lod.crew.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;

public class WriteNell {
	private Model model;
	private boolean n3;
	private boolean turtle;
	private boolean rdfxml;
	private String output;

	public WriteNell(Model model, boolean rdfxml, boolean turtle, boolean n3, String filename) {
		this.model = model;
		this.rdfxml = rdfxml;
		this.turtle = turtle;
		this.n3 = n3;
		this.output = filename;
	}

	public void write() {
		if (this.rdfxml) {
			File fileRDF = new File(output + ".rdf");
			try {
				OutputStream outRDF = new FileOutputStream(fileRDF);
				this.model.write(outRDF, "RDF/XML-ABBREV");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (this.turtle) {
			File fileTurtle = new File(output + ".ttl");
			try {
				OutputStream outTurtle = new FileOutputStream(fileTurtle);
				this.model.write(outTurtle, "Turtle");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (this.n3) {
			File fileN3 = new File(output + ".n3");
			try {
				OutputStream outN3 = new FileOutputStream(fileN3);
				this.model.write(outN3, "N3");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
