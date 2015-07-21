package fr.ste.lod.crew.utils.string2rdf;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * 
 * @author Quentin Cruzille
 * 
 * Cree un model Jena et le rempli avec les informations extraites du fichier de Nell.
 *
 */
public class StringTranslate 
{
	/**
	 * Model contenant contenant les triplets de Nell.
	 */
	private Model model;
	
	/**
	 * URI pour notre prefixe.
	 */
	private String base;
	
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
	private String rdfs;
	
	private String xsd;
	
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
	public StringTranslate(String prefix)
	{
		this.model = ModelFactory.createDefaultModel();
		this.base=prefix;
		this.ontologybase = base+"ontology/";
		this.skos="http://www.w3.org/2004/02/skos/core#";
		this.rdfs="http://www.w3.org/2000/01/rdf-schema#";
		this.xsd="http://www.w3.org/2001/XMLSchema#";
		this.model.setNsPrefix("nellkb",base);
		this.model.setNsPrefix("nellonto",ontologybase);
		this.model.setNsPrefix("skos",skos);
		this.model.setNsPrefix("rdfs", rdfs);
		this.model.setNsPrefix("xsd", xsd);
		this.prefLabel=this.model.createProperty(skos+"prefLabel");
		this.fail=new LinkedList<>();
		this.good=new LinkedList<>();
	}
	
	/**
	 * Prend un tableau de chaines de caracteres et les traduits en model Jena.
	 * @param nellData
	 */
	public void stringToRDF(String[] nellData)
	{	
		/* Traitement du sujet. */
		String [] nellDataSplit = nellData[0].split(":",2);
		nellDataSplit[1]=nellDataSplit[1].replaceAll(":", "_");
		Resource subject = this.getOrCreateRessource(nellDataSplit[1]);
		if(!nellData[3].equals(" "))
		{
			this.findLabel(subject, nellData[3]);
		}
		
		if(!nellData[5].equals(" "))
		{
			nellData[5].replaceAll("\"", "");
			subject.addProperty(this.prefLabel, nellData[5]);
		}
		
		if(!nellData[7].equals(""))
		{
			this.findType(nellData[7], subject);
		}
		
		/* Traitement du predicat. */
		Property relation = this.findRelation(nellData[1]);
		
		/* Traitement de l'objet. */
		nellDataSplit = nellData[2].split(":",2);
		if(nellDataSplit[0].equals("concept"))
		{
			/* Cas ou l'objet n'est pas un literal. */
			Resource object;
			nellDataSplit[1]=nellDataSplit[1].replaceAll(":", "_");
			if(nellData[1].equals("generalizations"))
			{
				object=this.getOrCreateRessourceClass(nellDataSplit[1]);
			}
			else
			{
				object=this.getOrCreateRessource(nellDataSplit[1]);
				
				if(!nellData[8].equals(""))
				{
					this.findType(nellData[8], object);
				}
			}
			
			if(!nellData[4].equals(" "))
			{
				this.findLabel(object, nellData[4]);
			}
			
			if(!nellData[6].equals(" "))
			{
				nellData[6].replaceAll("\"", "");
				object.addProperty(this.prefLabel, nellData[6]);
			}
			
			subject.addProperty(relation, object);
		}
		else
		{
			/* Cas ou l'objet est un literal, on verifie le cas specifique ou c'est une URL, 
			 * on utilise xsd:string pour les autres par defaut, a voir comment les differencier par la suite. */
			if(nellDataSplit[0].equals("http"))
			{
				RDFDatatype datatype = XSDDatatype.XSDanyURI;
				Literal object = model.createTypedLiteral(nellData[2], datatype);
				subject.addProperty(relation, object);
			}
			else
			{
				BigInteger trueLiteral = null;
				try
				{
					trueLiteral = new BigInteger(nellData[2]);
					
				}
				catch(NumberFormatException e)
				{
					
				}
				
				if(trueLiteral==null)
				{
					Literal object = model.createTypedLiteral(nellData[2], XSDDatatype.XSDstring);
					subject.addProperty(relation, object);
				}
				else
				{	
					Literal object = model.createTypedLiteral(trueLiteral,XSDDatatype.XSDinteger);	
					subject.addProperty(relation, object);
				}
			}
		}
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
