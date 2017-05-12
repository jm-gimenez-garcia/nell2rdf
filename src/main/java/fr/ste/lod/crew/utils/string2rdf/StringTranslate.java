package fr.ste.lod.crew.utils.string2rdf;

import fr.ste.lod.crew.NellOntologyConverter;
import fr.ste.lod.crew.extract.metadata.models.ConstantList;
import fr.ste.lod.crew.extract.metadata.models.Header;
import fr.ste.lod.crew.extract.metadata.models.LatLong;
import fr.ste.lod.crew.extract.metadata.util.Component;
import fr.ste.lod.crew.extract.metadata.models.LineInstanceJOIN;
import fr.ste.lod.crew.extract.metadata.util.Utility;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Quentin Cruzille & Jose M. Gimenez-Garcia
 * 
 * Cree un model Jena et le rempli avec les informations extraites du fichier de Nell.
 *
 */
public class StringTranslate {

    public static final String  PREFIX_RESOURCE = "nell";
    public static final String  PREFIX_ONTOLOGY = "nell.onto";
    public static final String  PREFIX_PROVENANCE_RESOURCE = "nell.prov";
    public static final String  PREFIX_PROVENANCE_ONTOLOGY = "nell.prov.onto";

    public static final String  RESOURCE                = "/resource";
    public static final String  ONTOLOGY                = "/ontology";
    public static final String  PROVENANCE_ONTOLOGY     = "/provenance/ontology";
    public static final String  PROVENANCE_RESOURCE     = "/provenance/resource";

	public static final String PROPERTY_ITERATION = "iteration";
    public static final String PROPERTY_ITERATION_OF_PROMOTION = "iterationOfPromotion";
    public static final String PROPERTY_PROBABILITY = "probability";
	public static final String PROPERTY_PROBABILITY_OF_BELIEF = "probabilityOfBelief";
	public static final String PROPERTY_SOURCE = "source";
	public static final String PROPERTY_TOKEN = "hasToken";
	public static final String PROPERTY_RELATION_VALUE = "relationValue";
	public static final String PROPERTY_GENERALIZATION_VALUE = "generalizationValue";
	public static final String PROPERTY_LATITUDE_VALUE = "latitudeValue";
	public static final String PROPERTY_LONGITUDE_VALUE = "longitudeValue";

	public static final String CLASS_BELIEF = "Belief";
	public static final String CLASS_CANDIDATE_BELIEF = "CandidateBelief";
	public static final String CLASS_PROMOTED_BELIEF = "PromotedBelief";
	public static final String CLASS_COMPONENT = "Component";
	public static final String CLASS_COMPONENT_ITERATION = "ComponentIteration";
	public static final String CLASS_TOKEN = "Token";
    public static final String CLASS_TOKEN_RELATION = "RelationToken";
    public static final String CLASS_TOKEN_GENERALIZATION = "GeneralizationToken";
    public static final String CLASS_TOKEN_GEO = "GeoToken";

	public static final String RESOURCE_TOKEN = "token";
    public static final String RESOURCE_TOKEN_RELATION = "relationToken";
    public static final String RESOURCE_TOKEN_GENERALIZATION = "generalizationToken";
    public static final String RESOURCE_TOKEN_GEO = "geoToken";

    public static final String PROPERTY_PROV_WAS_GENERATED_BY = "http://www.w3.org/ns/prov#wasGeneratedBy";
    public static final String PROPERTY_PROV_ENDED_AT_TIME = "http://www.w3.org/ns/prov#endedAtTime";
    public static final String PROPERTY_PROV_WAS_ASSOCIATED_WITH = "http://www.w3.org/ns/prov#wasAssociatedWith";

    public static final String  STATEMENT               = "statement";

    public static Logger		log						= Logger.getLogger(StringTranslate.class);

    private final String		metadata;
    private final boolean       candidates;

    private Map<String,Integer> numberSequences = new HashMap<>();

	/**
	 * Model contenant contenant les triplets de Nell.
	 */
	private Model model;
	
	/**
	 * URI pour notre prefixe.
	 */
	private final String		base;

	/**
	 * base prefix + /ontology for classes and property (T-Box related stuffs)
	 */
	private String resourceBase;
	private String ontologyBase;
	private String provenanceOntologyBase;
	private String provenanceResourceBase;
	
	/**
	 * URI pour le prefixe SKOS.
	 */
	private String skos;
	
	/**
	 * URI pour le prefixe RDFS.
	 */
	private final String		rdfs;

	private final String		xsd;

	// Change to double if more than 2^31-1 (~ 2.14 billion) statements
	private int					statementNumber;

	/**
	 * Propriete prefLabel de SKOS.
	 * (Jena n'implementant pas le vocabulaire SKOS, il est necessaire de la gerer manuellement).
	 */
	private Property prefLabel;
	
	public List<String> fail;
	public List<String> good;
	
	/**
	 * Constructeur, initialise le model et les prefixes.
	 */
	public StringTranslate(final String prefix, final String metadata, String separator, boolean candidates) {
		this.model = ModelFactory.createDefaultModel();
		this.base = prefix;
		this.metadata = metadata;
		this.candidates = candidates;
		this.resourceBase = this.base + RESOURCE + separator;
		this.ontologyBase = this.base + ONTOLOGY + separator;
		this.provenanceOntologyBase = this.base + PROVENANCE_ONTOLOGY + separator;
		this.provenanceResourceBase = this.base + PROVENANCE_RESOURCE + separator;
		this.skos = "http://www.w3.org/2004/02/skos/core#";
		this.rdfs = "http://www.w3.org/2000/01/rdf-schema#";
		this.xsd = "http://www.w3.org/2001/XMLSchema#";
		this.model.setNsPrefix(PREFIX_RESOURCE, this.resourceBase);
		this.model.setNsPrefix(PREFIX_ONTOLOGY, this.ontologyBase);
		this.model.setNsPrefix(PREFIX_PROVENANCE_RESOURCE, this.provenanceResourceBase);
		this.model.setNsPrefix(PREFIX_PROVENANCE_ONTOLOGY, this.provenanceOntologyBase);
		this.model.setNsPrefix("skos", this.skos);
		this.model.setNsPrefix("rdfs", this.rdfs);
		this.model.setNsPrefix("xsd", this.xsd);
		this.prefLabel = this.model.createProperty(this.skos + "prefLabel");
		this.fail = new LinkedList<>();
		this.good = new LinkedList<>();
		this.statementNumber = 0;
		if (metadata != NellOntologyConverter.NONE) {
		    this.model.createProperty(this.provenanceOntologyBase + PROPERTY_ITERATION);
			this.model.createProperty(this.provenanceOntologyBase + PROPERTY_ITERATION_OF_PROMOTION);
			this.model.createProperty(this.provenanceOntologyBase + PROPERTY_PROBABILITY);
			this.model.createProperty(this.provenanceOntologyBase + PROPERTY_PROBABILITY_OF_BELIEF);
			this.model.createProperty(this.provenanceOntologyBase + PROPERTY_SOURCE);

			this.model.createResource(this.provenanceOntologyBase + CLASS_BELIEF);
			this.model.createResource(this.provenanceOntologyBase + CLASS_CANDIDATE_BELIEF);
			this.model.createResource(this.provenanceOntologyBase + CLASS_PROMOTED_BELIEF);
			this.model.createResource(this.provenanceOntologyBase + CLASS_COMPONENT);
			this.model.createResource(this.provenanceOntologyBase + CLASS_COMPONENT_ITERATION);

            this.model.createResource(this.provenanceOntologyBase + Component.ALIAS_MATCHER);
            this.model.createResource(this.provenanceOntologyBase + Component.CMC);
            this.model.createResource(this.provenanceOntologyBase + Component.CPL);
            this.model.createResource(this.provenanceOntologyBase + Component.LE);
            this.model.createResource(this.provenanceOntologyBase + Component.LATLONG);
            this.model.createResource(this.provenanceOntologyBase + Component.MBL);
            this.model.createResource(this.provenanceOntologyBase + Component.OE);
            this.model.createResource(this.provenanceOntologyBase + Component.ONTOLOGY_MODIFIER);
            this.model.createResource(this.provenanceOntologyBase + Component.PRA);
            this.model.createResource(this.provenanceOntologyBase + Component.RULE_INFERENCE);
            this.model.createResource(this.provenanceOntologyBase + Component.SEAL);
            this.model.createResource(this.provenanceOntologyBase + Component.SEMPARSE);
            this.model.createResource(this.provenanceOntologyBase + Component.SPREADSHEET_EDITS);
		}
	}
	
	/**
	 * Prend un tableau de chaines de caracteres et les traduits en model Jena.
	 * @param nellData
	 */

	public void stringToRDF(final String[] nellData) {
		switch (this.metadata) {
			case NellOntologyConverter.NONE:
			    log.debug("Converting string to RDF without metadata");
				stringToRDFWithoutMetadata(nellData);
				break;
			case NellOntologyConverter.REIFICATION:
                log.debug("Converting string to RDF using reification to attach metadata");
				stringToRDFWithReification(nellData);
				break;
			case NellOntologyConverter.N_ARY:
                log.debug("Converting string to RDF using n-ary relations to attach metadata");
				stringToRDFWithNAry(nellData);
				break;
			case NellOntologyConverter.QUADS:
                log.debug("Converting string to RDF using quads to attach metadata");
				stringToRDFWithQuads(nellData);
				break;
			case NellOntologyConverter.SINGLETON_PROPERTY:
                log.debug("Converting string to RDF using singleton property to attach metadata");
				stringToRDFWithSingletoProperty(nellData);
				break;
		}
	}

	private void stringToRDFWithSingletoProperty(final String[] nellData) {
		// TODO Auto-generated method stub

	}

	private void stringToRDFWithQuads(final String[] nellData) {

        // Create normal triple without metadata
        final Statement triple = stringToRDFWithoutMetadata(nellData);

		// Create QUAD
		final Resource tripleId = createSequentialResource("ID");
		final Quad quad = new Quad(tripleId.asNode(), triple.asTriple());
		triple.createReifiedStatement();

        // Attach metadata to triple ID
        attachMetadata(tripleId, nellData);
	}

	private void stringToRDFWithNAry(final String[] nellData) {
		// TODO Auto-generated method stub

	}

	private void stringToRDFWithReification(final String[] nellData) {

		// Create normal triple without metadata
		final Statement triple = stringToRDFWithoutMetadata(nellData);

		// Create reification
        ReifiedStatement statement = triple.createReifiedStatement(createSequentialResourceUri(STATEMENT));

		// Attach metadata to reification statement
		attachMetadata(statement, nellData);
	}

	private void attachMetadata(final Resource resource, final String[] nellData) {
		Property predicate;
		RDFNode object;

		LineInstanceJOIN metadata = new LineInstanceJOIN(nellData[0], nellData[1], nellData[2], nellData[3], nellData[4], Utility.DecodeURL(nellData[5]), nellData[6], nellData[7], nellData[8], nellData[9], nellData[10], nellData[11], Utility.DecodeURL(nellData[12]), String.join("\t", nellData), this.candidates);

		// If it is a promoted belief, add iteration of promotion and probability
        if(!candidates) {
            // Add iteration of promotion
            predicate = this.model.getProperty(this.ontologyBase + PROPERTY_ITERATION_OF_PROMOTION);
            object = this.model.createTypedLiteral(metadata.getNrIterationsInt(),XSDDatatype.XSDinteger);
            resource.addProperty(predicate, object);

            // Add probability
            predicate = this.model.getProperty(this.ontologyBase + PROPERTY_PROBABILITY_OF_BELIEF);
            object = this.model.createTypedLiteral(metadata.getProbabilityDouble());
            resource.addProperty(predicate, object);
        }

        metadata.getListComponents().forEach((String K, Header V) -> {
            Property predicate_λ;
            RDFNode object_λ;

            // Create the Component Iteration
            predicate_λ = this.model.getProperty(PROPERTY_PROV_WAS_GENERATED_BY);
            RDFNode componentIteration = createSequentialResource(K);
            resource.addProperty(predicate_λ,componentIteration);

            // Add data to Component Iteration
            predicate_λ = model.getProperty(PROPERTY_PROV_WAS_ASSOCIATED_WITH);
            object_λ = model.getResource(PREFIX_PROVENANCE_ONTOLOGY + V.getComponentName());
            componentIteration.asResource().addProperty(predicate_λ, object_λ);

            predicate_λ = model.getProperty(PROPERTY_PROV_ENDED_AT_TIME);
            object_λ = model.createTypedLiteral(V.getDateTime(),XSDDatatype.XSDdateTime);
            componentIteration.asResource().addProperty(predicate_λ, object_λ);

            if (candidates) {
                predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY, PROPERTY_ITERATION);
                object_λ = model.createTypedLiteral(V.getIteration(), XSDDatatype.XSDinteger);
                componentIteration.asResource().addProperty(predicate_λ, object_λ);

                predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY, PROPERTY_PROBABILITY);
                object_λ = model.createTypedLiteral(V.getProbability(), XSDDatatype.XSDdecimal);
                componentIteration.asResource().addProperty(predicate_λ,object_λ);
            }

            predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY, PROPERTY_SOURCE);
            object_λ = model.createTypedLiteral(V.getSource(), XSDDatatype.XSDstring);
            componentIteration.asResource().addProperty(predicate_λ, object_λ);

            // Create Token
            RDFNode token;
            if (V instanceof LatLong) {
                token = createSequentialResource(RESOURCE_TOKEN_GEO);
                predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY,PROPERTY_GENERALIZATION_VALUE);
                double[] latlong= V.getFormatHeader().getTokenElement2LatLong();
                object_λ = model.createTypedLiteral(latlong[0], XSDDatatype.XSDdecimal);
                token.asResource().addProperty(predicate_λ, object_λ);
                predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY,PROPERTY_GENERALIZATION_VALUE);
                object_λ = model.createTypedLiteral(((LatLong) V).getFormatHeader().getTokenElement2LatLong()[1], XSDDatatype.XSDdecimal);
                token.asResource().addProperty(predicate_λ, object_λ);
            } else {
                switch (V.getFormatHeader().getTypeKB()) {
                    case ConstantList.RELATION:
                        token = createSequentialResource(RESOURCE_TOKEN_RELATION);
                        predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY,PROPERTY_RELATION_VALUE);
                        object_λ = model.createTypedLiteral(V.getFormatHeader().getTokenElement2(), XSDDatatype.XSDstring);
                        token.asResource().addProperty(predicate_λ, object_λ);
                        break;
                    case ConstantList.CATEGORY:
                        token = createSequentialResource(RESOURCE_TOKEN_GENERALIZATION);
                        predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY,PROPERTY_GENERALIZATION_VALUE);
                        object_λ = model.createTypedLiteral(V.getFormatHeader().getTokenElement2(), XSDDatatype.XSDstring);
                        token.asResource().addProperty(predicate_λ, object_λ);
                        break;
                    default:
                        token = createSequentialResource(RESOURCE_TOKEN);
                        break;
                }
            }
            predicate_λ = model.getProperty(PREFIX_PROVENANCE_ONTOLOGY, PROPERTY_TOKEN);
            componentIteration.asResource().addProperty(predicate_λ, token);
        });
	}

	public Statement stringToRDFWithoutMetadata(final String[] nellData) {

		/* Traitement du sujet. */
		String[] nellDataSplit = nellData[0].split(":", 2);
		nellDataSplit[1] = nellDataSplit[1].replaceAll(":", "_");
		final Resource subject = getOrCreateRessource(nellDataSplit[1]);
		if (!nellData[6].equals(" ")) {
			findLabel(subject, nellData[6]);
		}

		if (!nellData[8].equals(" ")) {
			nellData[8].replaceAll("\"", "");
			subject.addProperty(this.prefLabel, nellData[8]);
		}

		if (!nellData[10].equals("")) {
			findType(nellData[10], subject);
		}
		
		/* Traitement du predicat. */
		Property relation = this.findRelation(nellData[1]);
		
		/* Traitement de l'objet. */
		RDFNode object_node;
		nellDataSplit = nellData[2].split(":", 2);
		if (nellDataSplit[0].equals("concept")) {
			/* Cas ou l'objet n'est pas un literal. */
			Resource object_resource;
			nellDataSplit[1] = nellDataSplit[1].replaceAll(":", "_");
			if (nellData[1].equals("generalizations")) {
				object_resource = getOrCreateRessourceClass(nellDataSplit[1]);
			} else {
				object_resource = getOrCreateRessource(nellDataSplit[1]);

				if (!nellData[11].equals("")) {
					findType(nellData[11], object_resource);
				}
			}

			if (!nellData[7].equals(" ")) {
				findLabel(object_resource, nellData[7]);
			}

			if (!nellData[9].equals(" ")) {
				nellData[9].replaceAll("\"", "");
				object_resource.addProperty(this.prefLabel, nellData[9]);
			}

			object_node = object_resource;
			subject.addProperty(relation, object_resource);
		} else {
			/*
			 * Cas ou l'objet est un literal, on verifie le cas specifique ou c'est une URL, on utilise xsd:string pour les autres par defaut, a voir comment les differencier par la suite.
			 */
			if (nellDataSplit[0].equals("http")) {
				final RDFDatatype datatype = XSDDatatype.XSDanyURI;
				final Literal object_literal = this.model.createTypedLiteral(nellData[2], datatype);
				object_node = object_literal;
				subject.addProperty(relation, object_literal);
			} else {
				BigInteger trueLiteral = null;
				try
				{
					trueLiteral = new BigInteger(nellData[2]);
					
				}
				catch(NumberFormatException e)
				{
					
				}

				if (trueLiteral == null) {
					final Literal object_literal = this.model.createTypedLiteral(nellData[2], XSDDatatype.XSDstring);
					object_node = object_literal;
					subject.addProperty(relation, object_literal);
				} else {
					final Literal object_literal = this.model.createTypedLiteral(trueLiteral, XSDDatatype.XSDinteger);
					object_node = object_literal;
					subject.addProperty(relation, object_literal);
				}
			}
		}

		return model.createStatement(subject, relation, object_node);
		//return ResourceFactory.createStatement(subject, relation, object_node);
	}
	
	/**
	 * Prend une chaine de caractere et verifie s'il existe deje une resource associee e cette chaene, si oui
	 * la renvoie, sinon, la cree.
	 */
	private Resource getOrCreateRessource(String string)
	{
		Resource resource;
		if((resource=model.getResource(this.resourceBase + string))==null)
		{
			resource=model.createResource(this.resourceBase + string);
		}
		return resource;
	}
	
	private Resource getOrCreateRessourceClass(String string)
	{
		Resource resource;
		if((resource=model.getResource(this.ontologyBase +string))==null)
		{
			resource=model.createResource(this.ontologyBase +string);
		}
		return resource;
	}

//	private Property getOrCreateProvenanceProperty(String string) {
//	    Property property;
//	    if ((property = model.getProperty(this.provenanceOntologyBase + string)) == null) {
//	        property = model.createProperty(this.provenanceOntologyBase + string);
//        }
//        return property;
//    }

	private Resource createSequentialResource(final Resource resource_class) {
		final Resource resource = this.model.createResource(createSequentialResourceUri(resource_class.getLocalName()));
		resource.addProperty(RDF.type, resource_class);
		return resource;
	}

	private Resource createSequentialResource(final String resource_name) {
		final Resource resource = this.model.createResource(createSequentialResourceUri(resource_name));
		return resource;
	}

    private String createSequentialResourceUri(final String name) {
//	    return this.resourceBase + name + ++this.statementNumber;
        return this.resourceBase + name + numberSequences.compute(name, (K,V) -> V == null ? 1 : ++V);
    }

	/**
	 * Renvoi la propriete associee e une chaene de caracteres.
	 * @param relation
	 * @return
	 */
	private Property findRelation(String relation)
	{
		switch(relation)
		{
			case "generalizations":
				return (RDF.type);
				
			default:
				String[] s = relation.split(":", 2);
				Property p;
				s[1]=s[1].replaceAll(":", "_");
				if( (p=this.model.getProperty(this.ontologyBase +s[1])) == null )
				{
					p=this.model.createProperty(this.ontologyBase +s[1]);
				}
				return(p);	
		}
	}
	
	/**
	 * Parse la chaene de caracteres label pour trouver les differents label associes e subject.
	 * Associe les label trouves e subject avec RDFS.label et prefLabel avec SKOS.prefLabel.
	 * @param subject
	 * @param label
	 */
	private void findLabel(Resource subject, String label)
	{
		int i;
		String[] labelSplit = label.split("\" ");
		for(i=0;i<labelSplit.length;i++)
		{
			String l=labelSplit[i].replaceAll("\"", "");
			subject.addProperty(RDFS.label, l);
		}	
	}
	
	/**
	 * Parse la chaene de caracteres type pour trouver les classes associees e la resource subject.
	 * Associe les classes trouvees e subject avec RDF.type.
	 * @param type
	 * @param subject
	 */
	private void findType(String type, Resource subject)
	{
		String[] typeSplit = type.split(" ");
		for(int i=0;i<typeSplit.length;i++)
		{
			String[] trueType = typeSplit[i].split(":",2);
			if(trueType[0].equals("concept"))
			{
				trueType[1]=trueType[1].replaceAll(":", "_");
				trueType[1]=trueType[1].replaceAll("\"", "");
				Resource classe=this.getOrCreateRessourceClass(trueType[1]);
				subject.addProperty(RDF.type,classe);
			}
		}
	}

	/**
	 * Renvoie le model.
	 * @return
	 */
	public Model getModel()
	{
		return model;
	}
}
