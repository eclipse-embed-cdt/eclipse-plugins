package org.eclipse.embedcdt.core.liqp.tags;

import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.nodes.LNode;

class Comment extends Tag {

    /*
     * Block tag, comments out the text in the block
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return "";
    }
}
