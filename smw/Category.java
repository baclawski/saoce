package smw;

import java.util.List;
import java.util.ArrayList;

/**
 * Specify a category in Semantic MediaWiki.
 * @author Ken Baclawski
 */
@Copyright
public class Category {
    /** The name of the category to be represented. */
    protected String name = "OntologySummit2013_Survey";
    /** The welcome text. */
    protected String welcome = "=Ontology Summit 2013 Survey=\n";
    /** The form introduction and optional instruction. */
    protected String formIntroduction = "";

    /**
     * Construct a category.
     */
    public Category() {}

    /**
     * Get the name of the category.
     * @return The name of the category.
     */
    public String getName() {
	return name;
    }

    /**
     * Construct the category template.
     * @param builder The string builder.
     */
    public void template(StringBuilder builder) {
	builder.append("<noinclude>\n");
	builder.append("This is the \"").append(name).append("\" template.\n");
	builder.append("It should be called with the following format:\n");
	builder.append("<pre>\n");
	// Fill in the properties here
	builder.append("</pre>\n");
	builder.append("Edit the page to see the template text.\n");
	builder.append("</noinclude><includeonly>\n");
	// Fill in the properties here
	builder.append("</includeonly>\n");
    }

    /**
     * Construct the category form.
     * @param builder The string builder.
     */
    public void form(StringBuilder builder) {

	// The noinclude part is shown when the page is opened directly.

	builder.append("<noinclude>").append(welcome);
	builder.append("{{#forminput:").append(name).append("}}\n\n</noinclude>");

	// The includeonly part is shown when the page is opened via #forminput.

	builder.append("<includeonly>").append(formIntroduction);
	builder.append("{{{for template|").append(name).append("}}}\n");

	builder.append("{{{end template}}}\n");
	builder.append("<headertabs/>\n");
	builder.append("</includeonly>\n");
    }

    /**
     * Construct the category wiki page.
     * @param builder The string builder.
     */
    public void category(StringBuilder builder) {
	builder.append(welcome);
	builder.append("{{#forminput:form=").append(name).append("|button text=Continue with survey}}\n");
    }

    /**
     * Construct the welcome page.
     * @param builder The string builder.
     */
    public void welcome(StringBuilder builder) {
	builder.append(welcome);
	builder.append("{{#forminput:form=").append(name).append("|button text=Continue with survey}}\n");
    }
}
