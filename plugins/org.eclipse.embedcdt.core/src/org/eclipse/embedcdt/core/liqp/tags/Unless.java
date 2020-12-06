package org.eclipse.embedcdt.core.liqp.tags;

import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.nodes.LNode;

class Unless extends Tag {

    /*
     * Mirror of if statement
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        for (int i = 0; i < nodes.length - 1; i += 2) {

            Object exprNodeValue = nodes[i].render(context);
            LNode blockNode = nodes[i + 1];

            if (!super.asBoolean(exprNodeValue)) {
                return blockNode.render(context);
            }
        }

        return "";
    }
}
