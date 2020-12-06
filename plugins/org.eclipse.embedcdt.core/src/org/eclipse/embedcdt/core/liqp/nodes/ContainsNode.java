package org.eclipse.embedcdt.core.liqp.nodes;

import java.util.Arrays;

import org.eclipse.embedcdt.core.liqp.LValue;
import org.eclipse.embedcdt.core.liqp.TemplateContext;

class ContainsNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public ContainsNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        Object collection = lhs.render(context);
        Object needle = rhs.render(context);

        if(super.isArray(collection)) {
            Object[] array = super.asArray(collection);
            return Arrays.asList(array).contains(needle);
        }

        if(super.isString(collection)) {
            return super.asString(collection).contains(super.asString(needle));
        }

        return false;
    }
}
