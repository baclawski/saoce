package smw;

/**
 * Semantic MediaWiki Property.
 * @author Ken Baclawski
 */
public class Property {
    /** The name of the property to be represented. */
    private String name;
    /** Optional Comment suffix. */
    private String commentSuffix;
    /** The type of the property. */
    private String type;
    /** Optional question to use for requesting a value for the property. */
    private String question;
    /** Optional additional fields.*/
    private String options = null;
    /**
     * Construct a Semantic MediaWiki property.
     * @param name The name of the property.
     * @param other Optional other information about the property. The
     * following are recognized:
     * <ol>
     * <li>Comment suffix. If included, then a second property of type Text
     * will be constructed with name obtained by appending a space and the
     * comment suffix.
     * <li>The type of the property. The default is Boolean.
     * <li>A question to use for requesting a value for the property.
     * <li>Optional additional fields.
     * </ol>
     */
    public Property(String name, String... other) {
	this.name = name;
	if (other.length >= 1 && other[0] != null && !other[0].isEmpty()) {
	    this.commentSuffix = other[0];
	} else {
	    this.commentSuffix = null;
	}
	if (other.length >= 2 && other[1] != null && !other[1].isEmpty()) {
	    this.type = other[1];
	} else {
	    this.type = "Boolean";
	}
	if (other.length >= 3 && other[2] != null && !other[2].isEmpty()) {
	    this.question = other[2];
	} else {
	    this.question = null;
	}
	if (other.length >= 4 && other[3] != null && !other[3].isEmpty()) {
	    this.options = other[3];
	} else {
	    this.options = null;
	}
    }

    /**
     * The name of the property.
     * @return The property name.
     */
    public String getName() {
	return name;
    }

    /**
     * The name of the comment property if there is one.
     * @return The comment property name or null if it does not exist.
     */
    public String getCommentName() {
	if (commentSuffix == null) {
	    return null;
	} else {
	    return name + " " + commentSuffix;
	}
    }

    /**
     * The content of the Property page.
     * @return The content for the wiki page for this property.
     */
    public String propertyPageContent() {
	StringBuilder builder = new StringBuilder();
	builder.append("This is a property of type [[Has type::").append(type).append("]].\n");
	if (question != null) {
	    String fixedQuestion = question.replaceAll("[{][{].*[}][}]", "...");
	    builder.append("\n\nThe question to be used when requesting the value of this property is:\n").append(fixedQuestion).append("\n");
	}
	return builder.toString();
    }	

    /**
     * The content of the Comment Property page.
     * @return The content for the wiki page for the comment property.
     */
    public String commentPropertyPageContent() {
	StringBuilder builder = new StringBuilder();
	builder.append("This is a property of type [[Has type::Text]].\n");
	builder.append("It represents explanatory text for another property.\n");
	return builder.toString();
    }	

    /**
     * Append property in template documentation.
     * @param builder The string builder.
     */
    public void templateDoc(StringBuilder builder) {
	builder.append("|").append(name).append("=\n");
	if (commentSuffix != null) {
	    builder.append("|").append(name).append(" ").append(commentSuffix).append("=\n");
	}
    }

    /**
     * Append the property in a template definition.
     * After the last property, the template definition ends with "|}".
     * @param builder The string builder.
     * @param isFirst Whether this is the first property in the form table.
     * @param isOpen Whether a form table is currently open.
     * @param header Optional header information.
     * @return Whether this call opened a table.
     */
    public boolean templateDefinition(StringBuilder builder, boolean isFirst, boolean isOpen, String header) {
	if (isFirst) {
	    builder.append("{| class=\"wikitable\"\n");
	    isOpen = true;
	} else {
	    builder.append("|-\n");
	}
	if (header != null) {
	    builder.append(header);
	}
	if (type.equals("Boolean")) {
	    builder.append("|").append(name).append("\n| [[").append(name).append("::{{{").append(name).append("|No}}}]]\n");
	} else {
	    builder.append("|").append(name).append("\n| [[").append(name).append("::{{{").append(name).append("|}}}]]\n");
	}
	String comment = getCommentName();
	if (comment != null) {
	    builder.append("|-\n|").append(" -- comment").append("\n| [[").append(comment).append("::{{{").append(comment).append("|Optional remarks}}}]]\n");
	}
	return isOpen;
    }

    /**
     * Append the property in a form definition.  If the result of the call
     * leaves an open table and the next property is the first one of its
     * table, then close the open table by appending "|}\n". Also if the last
     * property ends with an open table, then close the table.  Because boolean
     * questions require column headings, one cannot mix booleans with other
     * tabular types (String and URL).
     * @param builder The string builder.
     * @param isFirst Whether this is the first property in the form table.
     * @param isOpen Whether a form table is currently open.
     * @param header Optional header information.
     * @return Whether this call opened a table.
     */
    public boolean formDefinition(StringBuilder builder, boolean isFirst, boolean isOpen, String header) {

	// Determine whether to use a tabular format.

	boolean isBoolean = type.equals("Boolean");
	boolean isTabular = isBoolean;
	if (options != null) {
	    for (String option : options.split("[|]")) {
		if (option.equals("rows=1")) {
		    isTabular = true;
		    break;
		}
	    }
	}
	if (type != null && (type.equals("URL") || type.equals("String"))) {
	    isTabular = true;
	}

	// Open a table if needed.

	if (isTabular && (isFirst || !isOpen)) {
	    if (isBoolean) {
		builder.append("{| class=\"formtable\"\n");
	    } else {
		builder.append("{| class=\"formtable\"\n");
	    }
	} else if (!isFirst && isOpen) {
	    if (isTabular) {
		builder.append("|-\n");
	    } else {
		builder.append("|}\n");
		isOpen = false;
	    }
	}
	if (header != null) {
	    builder.append(header);
	}
	if (isBoolean) {
	    builder.append("| ");
	    if (question != null) {
		builder.append(question);
	    } else {
		builder.append(name);
	    }
	    builder.append("\n");
	    builder.append("| {{{field|").append(name).append("|input type=radiobutton|mandatory|default=No}}}\n");
	    String comment = getCommentName();
	    if (comment != null) {
		builder.append("| {{{field|").append(comment).append("|type=Text|rows=1}}}\n");
	    }
	} else {
	    if (isTabular) {
		builder.append("| ");
	    }
	    if (question != null) {
		builder.append(question);
	    } else {
		builder.append(name);
	    }
	    if (isTabular) {
		builder.append("\n|");
	    }
	    builder.append(" {{{field|").append(name).append("|type=").append(type);
	    if (options != null) {
		builder.append(options);
	    }
	    builder.append("}}}\n");
	}
	return isTabular;
    }
}
