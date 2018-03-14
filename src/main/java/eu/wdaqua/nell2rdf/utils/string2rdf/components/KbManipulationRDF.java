package eu.wdaqua.nell2rdf.utils.string2rdf.components;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import eu.wdaqua.nell2rdf.extract.metadata.models.KbManipulation;
import static eu.wdaqua.nell2rdf.utils.UriNell.*;

public class KbManipulationRDF extends ComponentRDF {

	public KbManipulationRDF(final KbManipulation kbManipulation, Resource belief) {
		super(kbManipulation, belief);
	}

	public void addTriples () {
		super.addTriples();
		addOldBug();
	}
	
	void addOldBug() {
		Property predicate = componentExecution.getModel().getProperty(PROPERTY_OLD_BUG);
		RDFNode object = componentExecution.getModel().createTypedLiteral(getOldBug(),XSDDatatype.XSDstring);
		componentExecution.addProperty(predicate, object);
	}
	
	String getComponentName() {
		return RESOURCE_KB_MANIPULATION;
	}
	
	String getExecutionType() {
		return CLASS_KB_MANIPULATION_EXECUTION;
	}
	
	String getOldBug() {
		return ((KbManipulation) componentNell).getMetadata_oldBug();
	}
}
