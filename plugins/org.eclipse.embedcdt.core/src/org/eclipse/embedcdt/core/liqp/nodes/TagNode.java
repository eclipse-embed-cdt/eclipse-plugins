package org.eclipse.embedcdt.core.liqp.nodes;

import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.parser.Flavor;
import org.eclipse.embedcdt.core.liqp.tags.Tag;

class TagNode implements LNode {

    private Tag tag;
    private LNode[] tokens;
    private Flavor flavor;

    public TagNode(String tagName, Tag tag, LNode... tokens) {
        this(tagName, tag, Flavor.LIQUID, tokens);
    }

    public TagNode(String tagName, Tag tag, Flavor flavor, LNode... tokens) {
        if (tag == null) {
            throw new IllegalArgumentException("no tag available named: " + tagName);
        }
        this.tag = tag;
        this.tokens = tokens;
        this.flavor = flavor;
    }

    @Override
    public Object render(TemplateContext context) {
        return tag.render(context, tokens);
    }
}
