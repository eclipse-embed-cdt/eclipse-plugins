package org.eclipse.embedcdt.core.liqp.nodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.filters.Filter;

public class FilterNode implements LNode {

    private Filter filter;
    private List<LNode> params;

    public FilterNode(String filterName, Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("no filter available  named: " + filterName);
        }
        this.filter = filter;
        this.params = new ArrayList<LNode>();
    }

    public void add(LNode param) {
        params.add(param);
    }

    public Object apply(Object value, TemplateContext context) {

        List<Object> paramValues = new ArrayList<Object>();

        for (LNode node : params) {
            paramValues.add(node.render(context));
        }

        return filter.apply(value, context, paramValues.toArray(new Object[paramValues.size()]));
    }

    @Override
    public Object render(TemplateContext context) {
        throw new RuntimeException("cannot render a filter");
    }
}
