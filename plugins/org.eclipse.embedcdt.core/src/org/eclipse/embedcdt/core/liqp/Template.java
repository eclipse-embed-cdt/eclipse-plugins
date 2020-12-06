package org.eclipse.embedcdt.core.liqp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.eclipse.embedcdt.core.liqp.filters.Filter;
import org.eclipse.embedcdt.core.liqp.nodes.LNode;
import org.eclipse.embedcdt.core.liqp.nodes.LiquidWalker;
import org.eclipse.embedcdt.core.liqp.parser.Flavor;
import org.eclipse.embedcdt.core.liqp.parser.LiquidLexer;
import org.eclipse.embedcdt.core.liqp.parser.LiquidParser;
import org.eclipse.embedcdt.core.liqp.tags.Tag;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The main class of this library. Use one of its static
 * <code>parse(...)</code> to get a hold of a reference.
 * <p/>
 * Also see: https://github.com/Shopify/liquid
 */
public class Template {

    /**
     * The root of the AST denoting the Liquid input source.
     */
    private final CommonTree root;

    /**
     * This instance's tags.
     */
    private final Map<String, Tag> tags;

    /**
     * This instance's filters.
     */
    private final Map<String, Filter> filters;

    private final long templateSize;

    private ProtectionSettings protectionSettings = new ProtectionSettings.Builder().build();

    private RenderSettings renderSettings = new RenderSettings.Builder().build();

    private final ParseSettings parseSettings;

    /**
     * Creates a new Template instance from a given input.
     *  @param input
     *         the file holding the Liquid source.
     * @param tags
     *         the tags this instance will make use of.
     * @param filters
     *         the filters this instance will make use of.
     */
    private Template(String input, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings settings) {

        this.tags = tags;
        this.filters = filters;
        this.parseSettings = settings;

        ANTLRStringStream stream = new ANTLRStringStream(input);
        this.templateSize = stream.size();
        LiquidLexer lexer = new LiquidLexer(parseSettings.stripSpacesAroundTags, stream);
        LiquidParser parser = new LiquidParser(parseSettings.flavor, new CommonTokenStream(lexer));

        try {
            root = parser.parse().getTree();
        }
        catch (RecognitionException e) {
            throw new RuntimeException("could not parse input: " + input, e);
        }
    }

    /**
     * Creates a new Template instance from a given file.
     *
     * @param file
     *         the file holding the Liquid source.
     */
    private Template(File file, Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings) throws IOException {

        this.tags = tags;
        this.filters = filters;
        this.parseSettings = parseSettings;

        try {
            ANTLRFileStream stream = new ANTLRFileStream(file.getAbsolutePath());
            this.templateSize = stream.size();
            LiquidLexer lexer = new LiquidLexer(parseSettings.stripSpacesAroundTags, stream);
            LiquidParser parser = new LiquidParser(parseSettings.flavor, new CommonTokenStream(lexer));
            root = parser.parse().getTree();
        }
        catch (RecognitionException e) {
            throw new RuntimeException("could not parse input from " + file, e);
        }
    }

    /**
     * Returns the root of the AST of the parsed input.
     *
     * @return the root of the AST of the parsed input.
     */
    public CommonTree getAST() {
        return root;
    }

    /**
     * Returns a new Template instance from a given input string.
     *
     * @param input
     *         the input string holding the Liquid source.
     *
     * @return a new Template instance from a given input string.
     */
    public static Template parse(String input) {
        return new Template(input, Tag.getTags(), Filter.getFilters(), new ParseSettings.Builder().build());
    }

    /**
     * Returns a new Template instance from a given input file.
     *
     * @param file
     *         the input file holding the Liquid source.
     *
     * @return a new Template instance from a given input file.
     */
    public static Template parse(File file) throws IOException {
        return new Template(file, Tag.getTags(), Filter.getFilters(), new ParseSettings.Builder().build());
    }

    public static Template parse(File file, ParseSettings settings) throws IOException {
        return new Template(file, Tag.getTags(), Filter.getFilters(), settings);
    }

    public static Template parse(String input, ParseSettings settings) {
        return new Template(input, Tag.getTags(), Filter.getFilters(), settings);
    }

    @Deprecated // Use `parse(file, settings)` instead
    public static Template parse(File file, Flavor flavor) throws IOException {
        ParseSettings settings = new ParseSettings.Builder().withFlavor(flavor).build();
        return parse(file, settings);
    }

    @Deprecated // Use `parse(input, settings)` instead
    public static Template parse(String input, Flavor flavor) throws IOException {
        ParseSettings settings = new ParseSettings.Builder().withFlavor(flavor).build();
        return parse(input, settings);
    }

    public Template with(Tag tag) {
        this.tags.put(tag.name, tag);
        return this;
    }

    public Template with(Filter filter) {
        this.filters.put(filter.name, filter);
        return this;
    }

    public Template withProtectionSettings(ProtectionSettings protectionSettings) {
        this.protectionSettings = protectionSettings;
        return this;
    }

    public Template withRenderSettings(RenderSettings renderSettings) {
        this.renderSettings = renderSettings;
        return this;
    }

    /**
     * Renders the template.
     *
     * @param jsonMap
     *         a JSON-map denoting the (possibly nested)
     *         variables that can be used in this Template.
     *
     * @return a string denoting the rendered template.
     */
    @SuppressWarnings("unchecked")
    public String render(String jsonMap) {

        Map<String, Object> map;

        try {
            map = new ObjectMapper().readValue(jsonMap, HashMap.class);
        }
        catch (Exception e) {
            throw new RuntimeException("invalid json map: '" + jsonMap + "'", e);
        }

        return render(map);
    }

    public String render() {
        return render(new HashMap<String, Object>());
    }

    /**
     * Renders the template.
     *
     * @param key
     *         a key
     * @param value
     *         the value belonging to the key
     * @param keyValues
     *         an array denoting key-value pairs where the
     *         uneven numbers (even indexes) should be Strings.
     *         If the length of this array is uneven, the last
     *         key (without the value) gets `null` attached to
     *         it. Note that a call to this method with a single
     *         String as parameter, will be handled by
     *         `render(String jsonMap)` instead.
     *
     * @return a string denoting the rendered template.
     */
    public String render(Object key, Object value, Object... keyValues) {

        Map<String, Object> map = new HashMap<String, Object>();
        putStringKey(key, value, map);

        for (int i = 0; i < keyValues.length - 1; i += 2) {
            key = String.valueOf(keyValues[i]);
            value = keyValues[i + 1];
            putStringKey(key, value, map);
        }

        return render(map);
    }

    /**
     * Renders the template.
     *
     * @param variables
     *         a Map denoting the (possibly nested)
     *         variables that can be used in this
     *         Template.
     *
     * @return a string denoting the rendered template.
     */
    public String render(final Map<String, Object> variables) {

        if (this.templateSize > this.protectionSettings.maxTemplateSizeBytes) {
            throw new RuntimeException("template exceeds " + this.protectionSettings.maxTemplateSizeBytes + " bytes");
        }

        final LiquidWalker walker = new LiquidWalker(new CommonTreeNodeStream(root), this.tags, this.filters, this.parseSettings.flavor);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> task = new Callable<String>() {
            public String call() throws Exception {
                try {
                    LNode node = walker.walk();
                    Object rendered = node.render(new TemplateContext(protectionSettings, renderSettings, parseSettings.flavor, variables));
                    return rendered == null ? "" : String.valueOf(rendered);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Future<String> future = executorService.submit(task);
        try {
            return future.get(this.protectionSettings.maxRenderTimeMillis, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("exceeded the max amount of time (" +
                    this.protectionSettings.maxRenderTimeMillis + " ms.)");
        }
        catch (Throwable t) {
            throw new RuntimeException("Oops, something unexpected happened: ", t);
        }
        finally {
            executorService.shutdown();
        }
    }

    /**
     * Returns a string representation of the AST of the parsed
     * input source.
     *
     * @return a string representation of the AST of the parsed
     *         input source.
     */
    public String toStringAST() {

        StringBuilder builder = new StringBuilder();

        walk(root, builder);

        return builder.toString();
    }

    /**
     * Walks a (sub) tree of the root of the input source and builds
     * a string representation of the structure of the AST.
     * <p/>
     * Note that line breaks and multiple white space characters are
     * trimmed to a single white space character.
     *
     * @param tree
     *         the (sub) tree.
     * @param builder
     *         the StringBuilder to fill.
     */
    @SuppressWarnings("unchecked")
    private void walk(CommonTree tree, StringBuilder builder) {

        List<CommonTree> firstStack = new ArrayList<CommonTree>();
        firstStack.add(tree);

        List<List<CommonTree>> childListStack = new ArrayList<List<CommonTree>>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<CommonTree> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            }
            else {
                tree = childStack.remove(0);

                String indent = "";

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                }

                String tokenName = LiquidParser.tokenNames[tree.getType()];
                String tokenText = tree.getText().replaceAll("\\s+", " ").trim();

                builder.append(indent)
                        .append(childStack.isEmpty() ? "'- " : "|- ")
                        .append(tokenName)
                        .append(!tokenName.equals(tokenText) ? "='" + tokenText + "'" : "")
                        .append("\n");

                if (tree.getChildCount() > 0) {
                    childListStack.add(new ArrayList<CommonTree>((List<CommonTree>) tree.getChildren()));
                }
            }
        }
    }

    private void putStringKey(Object key, Object value, Map<String, Object> map) {

        if (key == null || key.getClass() != String.class) {
            throw new RuntimeException("invalid key: " + key);
        }

        map.put((String) key, value);
    }
}
