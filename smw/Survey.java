package smw;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jrdf.parser.*;
import org.jrdf.graph.*;
import org.jrdf.graph.local.*;

public class Survey {
    public static List<String> list = new ArrayList<String>();
    static {
	list.add("Accept validation test sets or inputs");
	list.add("Apply a style of ontological analysis to design");
	list.add("Assess accuracy. correctness. and completeness of ontology terminological content");
	list.add("Assess and enforce consistency and completeness of inverse relations");
	list.add("Assess and enforce consistency and completeness of range and domain constraints");
	list.add("Assess complexity of concept definitions (necessary and sufficient properties) and use of axioms");
	list.add("Assess correctness or performance of an ontology design");
	list.add("Assess inferencing completeness performance");
	list.add("Assess inferencing time performance");
	list.add("Assess or characterize breadth of domain coverage of an ontology");
	list.add("Assess or characterize depth or detail of domain coverage of an ontology");
	list.add("Assess or characterize use considerations of ontology including licensing. training. cost. updating. software requirements. and security");
	list.add("Assess or track user experience with ontology");
	list.add("Assess query precision and recall performance");
	list.add("Assess query time performance");
	list.add("Assess the inferencing power of an ontology");
	list.add("Assess the reusability of a planned ontology");
	list.add("Assure mathematically complete computability of ontology");
	list.add("Assure semantically adequate computability of ontology");
	list.add("Capture ontology errors during use");
	list.add("Choose RDF support");
	list.add("Choose extant core ontologies for reuse");
	list.add("Choose extant top ontology for reuse");
	list.add("Choose formal logic language");
	list.add("Choose metaphysical methodology");
	list.add("Choose ontology language");
	list.add("Compare and map between ontologies");
	list.add("Compare domain coverage across ontologies");
	list.add("Create mappings from ontologies to and from data and data sources");
	list.add("Detect and guide use of multiple inheritance");
	list.add("Detect violations of domain. referential. or semantic integrity");
	list.add("Distinguish between conceptual and operational ontologies and objectives");
	list.add("Enable adjustable query and inference performance");
	list.add("Enable user to modify or extend ontology to address deficiencies");
	list.add("Enforce proper use and coding of URIs");
	list.add("Enforce proper use of conjunctions versus disjunctions");
	list.add("Ensure kind-of nature and consistency of subclassing");
	list.add("Ensure proper separation and coding of concepts and facts");
	list.add("Ensure proper use and coding of RDF and Web resources");
	list.add("Ensure proper use and coding of data types");
	list.add("Ensure proper use and coding of relations");
	list.add("Ensure proper use and coding of transitive relations");
	list.add("Export ontology in different languages");
	list.add("Facilitate framing a set of competency questions exemplifying ontology objectives");
	list.add("Feedback on consistency of granularity and regularity of domain ontology structure");
	list.add("Find ontologies with specific domain coverage");
	list.add("Generate characteristic queries and tests");
	list.add("Generate ontology application and performance requirements");
	list.add("Generate ontology code from requirements specification");
	list.add("Generate or enforce selected design patterns in ontology code");
	list.add("Generate prototypical instances (individuals) to help verify class intent and class subsumption");
	list.add("Guide and facilitate modularization of ontology");
	list.add("Guide application of open world or closed world semantics");
	list.add("Guide design to optimize normalization. factoring. and simplicity of ontology");
	list.add("Guide formulation of data and information requirements");
	list.add("Guide formulation of domain scope and detail requirements");
	list.add("Guide formulation of goodness of design requirements");
	list.add("Guide formulation of interface requirements");
	list.add("Guide formulation of level of effort requirements");
	list.add("Guide formulation of semantic and reasoning requirements");
	list.add("Guide ontology design for visualization");
	list.add("Guide ontology design to achieve inferencing requirements");
	list.add("Guide ontology design to achieve scalability requirements");
	list.add("Guide ontology development per a quality assurance methodology");
	list.add("Guide or adjust ontology in accord with validation results");
	list.add("Guide subclass versus class individual determinations");
	list.add("Guide use of disjointness axioms");
	list.add("Guide use of existential versus universal quantification in class restrictions");
	list.add("Guide use of necessary and sufficient properties in concept definitions");
	list.add("Identify age and use statistics of an ontology and its versions");
	list.add("Identify frequency of use of ontology language features in an ontology");
	list.add("Identify number and ratio of concepts. relations. and subclassing");
	list.add("Integrate ontology with other information system resources");
	list.add("Integrate ontology with other ontologies");
	list.add("Integrate selected design patterns into an ontology design");
	list.add("Manage lexical naming and annotation of ontology elements");
	list.add("Mix ontology languages");
	list.add("Monitor depth of subclassing consistency across a domain ontology");
	list.add("Offer application patterns based on current ontology or design context");
	list.add("Offer methodology patterns based on current ontology or design context");
	list.add("Offer ontology language patterns based on current ontology or design context");
	list.add("Offer other patterns based on current ontology or design context");
	list.add("Optimize query and inference performance");
	list.add("Organize and maintain a collection of related ontologies and ontology modules");
	list.add("Organize and track the life cycle requirements and progression of an ontology");
	list.add("Overall. detect and correct coding errors or inconsistencies");
	list.add("Produce the current terminological inferences of an ontology");
	list.add("Profile use of ontology elements during use");
	list.add("Promote reuse of ontology");
	list.add("Provide statistics on ontology versioning and use");
	list.add("Rate ontologies on their popularity or review feedback");
	list.add("Track ontology changes and control versions");
	list.add("Validate instance data conforming to an ontology");
	list.add("Validate the intended functionality of software using the ontology");
	list.add("Verify that ontology requirements are met");
	list.add("Verify that two ontologies are interoperable");
    }


    public static String decode(String text) {
	String out = text.replace("-3A", ":");
	out = out.replace("-28", "(");
	out = out.replace("-29", ")");
	out = out.replace("-2C", ".");
	out = out.replace("-23", "#");
	out = out.replace("-26", "&");
	out = out.replace("-2D", "-");
	out = out.replace("_", " ");
	return out;
    }

    public static class Info {
	String tool;
	Map<String, String> questions = new HashMap<String, String>();
	Map<String, String> comments = new HashMap<String, String>();
    }

    /**
     * Export survey information for a tool to RDF and resolve the resources.
     * @param wiki The URL of the wiki.
     * @param name The name of the tool
     * @return The survey information on the specified page.
     */
    public static Info readEntry(String wiki, String name) throws Exception {
	String locString = wiki + "/Special:ExportRDF/" + name;
	String localString = wiki + "/Special:URIResolver/" + name;
	String propertyPrefix = wiki + "/Special:URIResolver/Property:";
	Info info = new Info();

	URL loc = new URL(locString);
	RdfReader reader = new RdfReader();
	Graph graph = reader.parseRdfXml(loc.openStream());
	GraphElementFactory factory = graph.getElementFactory();
	Resource tool = factory.createResource(new URI(localString));

	for (Triple triple : graph.find(tool, AnyPredicateNode.ANY_PREDICATE_NODE, AnyObjectNode.ANY_OBJECT_NODE)) {
	    String predicate = decode(((URIReference)triple.getPredicate()).toString());
	    if (predicate.startsWith(propertyPrefix)) {
		String question = predicate.substring(propertyPrefix.length());

		ObjectNode object = triple.getObject();
		String value;
		if (object instanceof Literal) {
		    Literal literal = (Literal)object;
		    value = literal.getValue().toString();
		} else {
		    value = object.toString();
		}
		if (question.endsWith(" comments")) {
		    if (!value.equals("Optional remarks")) {
			String q = question.substring(0, question.length() - " comments".length());
			info.comments.put(q, value);
		    }
		} else {
		    info.questions.put(question, value);
		}
	    }
	}
	return info;
    }

    /**
     * Colorful yes/no.
     */
    public static String yes = "<noinclude>{| class=\"wikitable\"\n" +
	"|-\n" +
	"|</noinclude>style=\"background: #90ff90; color: black; vertical-align: middle; text-align: {{{align|center}}}; {{{style|}}}\" class=\"table-yes\"|{{{1|Yes}}}<noinclude>\n" +
	"|}\n" +
	"{{documentation}}\n" +
	"</noinclude>";

    public static String no = "<noinclude>{| class=\"wikitable\"\n" +
	"|-\n" +
	"|</noinclude>style=\"background:#ff9090; color:black; vertical-align: middle; text-align: {{{align|center}}}; {{{style|}}}\" class=\"table-no\" | {{{1|No}}}<noinclude>\n" +
	"|}\n" +
	"{{documentation}}\n" +
	"</noinclude>";

    /**
     * Main program
     * @param args The command-line arguments.
     * <ol>
     * <li>The username.
     * <li>The password.
     * <li>The wiki web service 
     * <li>The wiki itself
     * </ol>
     * @throws Exception if an error occurs.
     */
    public static void main(String... args) throws Exception {
	String[] entries = new String[] {
	    "15926Editor", "COLORE", "HyQue", "Macleod", "NCBO_BioPortal", "Ontohub", 
	    "OntologyTest", "OntoQA", "OOPS", "OOR", "OpenLinkVirtuoso", "RepOSE", "SigmaKEE", };
	Map<String, Info> map = new HashMap<String, Info>();
	for (String entry : entries) {
	    map.put(entry, readEntry(args[3], entry));
	}

	StringBuilder builder = new StringBuilder();
	builder.append("This is a summary page generated from the answers to the Ontology Summit 2013 Survey.\n\n");
	builder.append("This page can be difficult to read and navigate in the default appearance (skin).\n");
	builder.append("To change the appearance you should first log in, and then go to the Appearance tab of\n");
	builder.append("[").append(args[3]).append("/Special:Preferences your preferences page],\n");
	builder.append("change the skin to another one such as Vector, and Save your choice.\n\n");

	builder.append("{| style=\"text-align: center\" class=\"wikitable sortable\"").append('\n');
	builder.append("! Question").append('\n');
	for (String entry : entries) {
	    builder.append("! ").append(entry).append('\n');
	}
	builder.append("|-").append('\n');
	for (String question : list) {
	    builder.append("| style=\"text-align:left\" | ").append(question).append("?").append('\n');
	    for (String entry : entries) {
		Info info = map.get(entry);
		if (info != null) {
		    String answer = info.questions.get(question);
		    if (answer.equalsIgnoreCase("true")) {
			builder.append("| {{yes}}").append('\n');
		    } else {
			builder.append("| {{no}}").append('\n');
		    }
		} else {
		    builder.append("| ").append('\n');
		}
	    }
	    builder.append("|-").append('\n');
	}
	builder.append("|}").append('\n');

	// Upload the result.

	//Uploader uploader = new Uploader(args[0], args[1], args[2]);
    //uploader.login();
    //uploader.getEditToken();
	//uploader.edit("Template:Yes", yes);
	//uploader.edit("Template:No", no);
	//uploader.edit("OntologySummit2013_SurveySummary", builder.toString());
    }
}
