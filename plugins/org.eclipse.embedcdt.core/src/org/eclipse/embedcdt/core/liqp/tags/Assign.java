package org.eclipse.embedcdt.core.liqp.tags;

import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.nodes.FilterNode;
import org.eclipse.embedcdt.core.liqp.nodes.LNode;

class Assign extends Tag {

    /*
     * Assigns some value to a variable
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        String id = String.valueOf(nodes[0].render(context));

        FilterNode filter = null;
        LNode expression;

        if(nodes.length >= 3) {
            filter = (FilterNode)nodes[1];
            expression = nodes[2];
        }
        else {
            expression = nodes[1];
        }

        Object value = expression.render(context);

        if(filter != null) {
            value = filter.apply(value, context);
        }

        // Assign causes variable to be saved "globally"
        context.put(id, value, true);

        return "";
    }
}
