package eu.wdaqua.nell2rdf.utils.string2rdf.components;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import eu.wdaqua.nell2rdf.extract.metadata.models.MBL;
import static eu.wdaqua.nell2rdf.utils.UriNell.*;

public class MBLrdf extends ComponentRDF {

	public MBLrdf(final MBL mbl, Resource belief) {
		super(mbl, belief);
	}

	public void addTriples () {
		super.addTriples();
		addEntity();
		addRelation();
		addValue();
	}
	
	void addEntity() {
		Property predicate = componentExecution.getModel().getProperty(PROPERTY_PROMOTED_ENTITY);
		RDFNode object = componentExecution.getModel().createTypedLiteral(getEntity(),XSDDatatype.XSDstring);
		componentExecution.addProperty(predicate, object);
		
		predicate = componentExecution.getModel().getProperty(PROPERTY_PROMOTED_ENTITY_CATEGORY);
		object = componentExecution.getModel().createTypedLiteral(getEntityCategory(),XSDDatatype.XSDstring);
		componentExecution.addProperty(predicate, object);
	}
	
	void addRelation() {
		Property predicate = componentExecution.getModel().getProperty(PROPERTY_PROMOTED_RELATION);
		RDFNode object = componentExecution.getModel().createTypedLiteral(getRelation(),XSDDatatype.XSDstring);
		componentExecution.addProperty(predicate, object);
	}
	
	void addValue() {
		Property predicate = componentExecution.getModel().getProperty(PROPERTY_PROMOTED_VALUE);
		RDFNode object = componentExecution.getModel().createTypedLiteral(getValue(),XSDDatatype.XSDstring);
		componentExecution.addProperty(predicate, object);
		
		if (getValueCategory() != null) {
			predicate = componentExecution.getModel().getProperty(PROPERTY_PROMOTED_VALUE_CATEGORIE);
			object = componentExecution.getModel().createTypedLiteral(getValueCategory(),XSDDatatype.XSDstring);
			componentExecution.addProperty(predicate, object);
		}
	}
	
	String getComponentName() {
		return RESOURCE_MBL;
	}
	
	String getExecutionType() {
		return CLASS_MBL_EXECUTION;
	}
	
	String getEntity() {
		return ((MBL) componentNell).getMetadata_Entity();
	}
	
	String getEntityCategory() {
		return ((MBL) componentNell).getMetadata_EntityCategory();
	}
	String getRelation() {
		return ((MBL) componentNell).getMetadata_Relation();
	}
	String getValue() {
		return ((MBL) componentNell).getMetadata_Value();
	}
	String getValueCategory() {
		return ((MBL) componentNell).getMetadata_ValueCategory();
	}
	
}
