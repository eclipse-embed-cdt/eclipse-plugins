package org.eclipse.embedcdt.core.liqp.filters;

import org.jsoup.Jsoup;

class Strip_HTML extends Filter {

    /*
     * strip_html(input)
     *
     * Remove all HTML tags from the string
     */
    @Override
    public Object apply(Object value, Object... params) {

        String html = super.asString(value);

        return Jsoup.parse(html).text();
    }
}
