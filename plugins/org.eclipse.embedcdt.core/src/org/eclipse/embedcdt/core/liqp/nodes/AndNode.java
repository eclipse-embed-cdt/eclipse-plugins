package org.eclipse.embedcdt.core.liqp.nodes;

import org.eclipse.embedcdt.core.liqp.LValue;
import org.eclipse.embedcdt.core.liqp.TemplateContext;

class AndNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public AndNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);

        return super.asBoolean(a) && super.asBoolean(b);

    }
}
