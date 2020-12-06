package org.eclipse.embedcdt.core.liqp.tags;

import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.nodes.LNode;

class Raw extends Tag {

    /*
     * temporarily disable tag processing to avoid syntax conflicts.
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        return nodes[0].render(context);
    }
}
