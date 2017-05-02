package fr.ste.lod.crew.utils.string2rdf;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.ste.lod.crew.extract.metadata.models.LineInstanceJOIN;
import fr.ste.lod.crew.extract.metadata.util.Utility;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;

import fr.ste.lod.crew.NellOntologyConverter;

/**
 * 
 * @author Quentin Cruzille & Jose M. Gimenez-Garcia
 * 
 * Cree un model Jena et le rempli avec les informations extraites du fichier de Nell.
 *
 */
public class StringTranslate {

	public static final String	ITERATION_OF_PROMOTION	= "iteration_of_promotion";
	public static final String	PROBABILITY				= "probability";
	public static final String	SOURCE					= "source";
	public static final String	CANDIDATE_SOURCE		= "candidate_source";
    public static final String  STATEMENT               = "statement";
    public static final String  VALUE                   = "value";

    public static Logger		log						= Logger.getLogger(StringTranslate.class);

	/**
	 * Model contenant contenant les triplets de Nell.
	 */
	private Model model;
	
	/**
	 * URI pour notre prefixe.
	 */
	private final String		base;

	private final String		metadata;

	/**
	 * base prefix + /ontology for classes and property (T-Box related stuffs)
	 */
	private String ontologybase;
	
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
	public StringTranslate(final String prefix, final String metadata) {
		this.model = ModelFactory.createDefaultModel();
		this.base = prefix;
		this.metadata = metadata;
		this.ontologybase = this.base + "ontology/";
		this.skos = "http://www.w3.org/2004/02/skos/core#";
		this.rdfs = "http://www.w3.org/2000/01/rdf-schema#";
		this.xsd = "http://www.w3.org/2001/XMLSchema#";
		this.model.setNsPrefix("nellkb", this.base);
		this.model.setNsPrefix("nellonto", this.ontologybase);
		this.model.setNsPrefix("skos", this.skos);
		this.model.setNsPrefix("rdfs", this.rdfs);
		this.model.setNsPrefix("xsd", this.xsd);
		this.prefLabel = this.model.createProperty(this.skos + "prefLabel");
		this.fail = new LinkedList<>();
		this.good = new LinkedList<>();
		this.statementNumber = 0;
		if (metadata != NellOntologyConverter.NONE) {
			this.model.createProperty(this.ontologybase + ITERATION_OF_PROMOTION);
			this.model.createProperty(this.ontologybase + PROBABILITY);
			this.model.createProperty(this.ontologybase + SOURCE);
			this.model.createProperty(this.ontologybase + CANDIDATE_SOURCE);
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
        ReifiedStatement statement = triple.createReifiedStatement(createSequentialUri(STATEMENT));

		// Attach metadata to reification statement
		attachMetadata(statement, nellData);
	}

	private void attachMetadata(final Resource resource, final String[] nellData) {
		Property predicate;
		RDFNode object;

		LineInstanceJOIN metadata = new LineInstanceJOIN(nellData[0], nellData[1], nellData[2], nellData[3], Double.valueOf(nellData[4]), Utility.DecodeURL(nellData[5]), nellData[6], nellData[7], nellData[8], nellData[9], nellData[10], nellData[11], Utility.DecodeURL(nellData[12]), String.join("\t", nellData));

		// Add iteration of promotion
        predicate = this.model.getProperty(this.ontologybase + ITERATION_OF_PROMOTION);
        object = this.model.createTypedLiteral(metadata.getNrIterations());
		resource.addProperty(predicate, object);

		// Add probability
        predicate = this.model.getProperty(this.ontologybase + PROBABILITY);
        object = this.model.createTypedLiteral(metadata.getProbability());
		resource.addProperty(predicate, object);

        metadata.getListComponents().forEach((K, V) -> {
            Property predicate_λ = this.model.getProperty(this.ontologybase + SOURCE);
            RDFNode source = createSequentialResource(K);
            resource.addProperty(predicate_λ,source);

            predicate_λ = model.getProperty(this.ontologybase + VALUE);
            RDFNode token = this.model.createTypedLiteral(V.toString());
            source.asResource().addProperty(predicate_λ, token);
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
	 * @param s
	 * @return
	 */
	private Resource getOrCreateRessource(String string)
	{
		Resource resource;
		if((resource=model.getResource(this.base+string))==null)
		{
			resource=model.createResource(this.base+string);
		}
		return resource;
	}
	
	private Resource getOrCreateRessourceClass(String string)
	{
		Resource resource;
		if((resource=model.getResource(this.ontologybase+string))==null)
		{
			resource=model.createResource(this.ontologybase+string);
		}
		return resource;
	}

	private Resource createSequentialResource(final Resource resource_class) {
		final Resource resource = this.model.createResource(createSequentialUri(resource_class.getLocalName()));
		resource.addProperty(RDF.type, resource_class);
		return resource;
	}

	private Resource createSequentialResource(final String resource_name) {
		final Resource resource = this.model.createResource(createSequentialUri(resource_name));
		return resource;
	}

    private String createSequentialUri(final String name) {
	    return this.base + name + ++this.statementNumber;
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
				if( (p=this.model.getProperty(this.ontologybase+s[1])) == null )
				{
					p=this.model.createProperty(this.ontologybase+s[1]);
				}
				return(p);	
		}
	}
	
	/**
	 * Parse la chaene de caracteres label pour trouver les differents label associes e subject.
	 * Associe les label trouves e subject avec RDFS.label et prefLabel avec SKOS.prefLabel.
	 * @param subject
	 * @param label
	 * @param prefLabel
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
