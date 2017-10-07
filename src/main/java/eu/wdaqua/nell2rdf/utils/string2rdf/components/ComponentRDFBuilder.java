package eu.wdaqua.nell2rdf.utils.string2rdf.components;

import org.apache.log4j.Logger;

import eu.wdaqua.nell2rdf.extract.metadata.models.AliasMatcher;
import eu.wdaqua.nell2rdf.extract.metadata.models.CMC;
import eu.wdaqua.nell2rdf.extract.metadata.models.CPL;
import eu.wdaqua.nell2rdf.extract.metadata.models.Header;
import eu.wdaqua.nell2rdf.extract.metadata.models.KbManipulation;
import eu.wdaqua.nell2rdf.extract.metadata.models.LE;
import eu.wdaqua.nell2rdf.extract.metadata.models.LatLong;
import eu.wdaqua.nell2rdf.extract.metadata.models.OE;
import eu.wdaqua.nell2rdf.extract.metadata.models.OntologyModifier;
import eu.wdaqua.nell2rdf.extract.metadata.models.PRA;
import eu.wdaqua.nell2rdf.extract.metadata.models.RuleInference;
import eu.wdaqua.nell2rdf.extract.metadata.models.SEAL;
import eu.wdaqua.nell2rdf.extract.metadata.models.Semparse;
import eu.wdaqua.nell2rdf.extract.metadata.models.SpreadsheetEdits;
import eu.wdaqua.nell2rdf.extract.metadata.util.ConstantList;

public class ComponentRDFBuilder {
	
	private static Logger		log						= Logger.getLogger(ComponentRDFBuilder.class);
	
	private ComponentRDFBuilder() {
		// Static class
	}
	
	public static ComponentRDF build(Header componentNell) {
		final ComponentRDF componentRdf;
		switch (componentNell.getComponentName()) {
		case ConstantList.ALIASMATCHER:
			componentRdf = new AliasMatcherRDF((AliasMatcher) componentNell);
			break;
		case ConstantList.CMC:
			componentRdf = new CMCrdf((CMC) componentNell);
			break;
		case ConstantList.CPL:
			componentRdf = new CPLrdf((CPL) componentNell);
			break;
		case ConstantList.KBMANIPULATION:
			componentRdf = new KbManipulationRDF((KbManipulation) componentNell);
			break;
		case ConstantList.LATLONG:
		case ConstantList.LATLONGTT:
			componentRdf = new LatLongRDF((LatLong) componentNell);
			break;
		case ConstantList.LE:
			componentRdf = new LErdf((LE) componentNell);
			break;
		case ConstantList.MBL:
		case ConstantList.TEXT_MBL:
			log.error("MBL component not implemented. Extracting only generic data.");
			componentRdf = new ComponentRDF(componentNell);
			break;
		case ConstantList.OE:
			componentRdf = new OErdf((OE) componentNell);
			break;
		case ConstantList.ONTOLOGYMODIFIER:
			componentRdf = new OntologyModifierRDF((OntologyModifier) componentNell);
			break;
		case ConstantList.PRA:
			componentRdf = new PRArdf((PRA) componentNell);
			break;
		case ConstantList.RULEINFERENCE:
			componentRdf = new RLrdf((RuleInference) componentNell);
			break;
		case ConstantList.SEAL:
			componentRdf = new SEALrdf((SEAL) componentNell);
			break;
		case ConstantList.SEMPARSE:
			componentRdf = new SemparseRDF((Semparse) componentNell);
			break;
		case ConstantList.SPREADSHEETEDITS:
			componentRdf = new SpreadsheetEditsRDF((SpreadsheetEdits) componentNell);
			break;
		default:
			log.warn("It was not possible to identify the type of component. Extracting only generic data.");
			componentRdf = new ComponentRDF(componentNell);
		}
		return componentRdf;
	}

}
