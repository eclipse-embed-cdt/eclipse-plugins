package org.eclipse.embedcdt.core.liqp.nodes;

import org.eclipse.embedcdt.core.liqp.LValue;
import org.eclipse.embedcdt.core.liqp.TemplateContext;

class LtEqNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public LtEqNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);

        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() <= super.asNumber(b).doubleValue();
    }
}
