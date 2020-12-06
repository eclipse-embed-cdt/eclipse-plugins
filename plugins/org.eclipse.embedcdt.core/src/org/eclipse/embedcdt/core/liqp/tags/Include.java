package org.eclipse.embedcdt.core.liqp.tags;

import java.io.File;

import org.eclipse.embedcdt.core.liqp.Template;
import org.eclipse.embedcdt.core.liqp.TemplateContext;
import org.eclipse.embedcdt.core.liqp.nodes.LNode;

public class Include extends Tag {

    public static final String INCLUDES_DIRECTORY_KEY = "org.eclipse.embedcdt.core.liqp@includes_directory";
    public static String DEFAULT_EXTENSION = ".liquid";

    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        try {
            String includeResource = super.asString(nodes[0].render(context));
            String extension = DEFAULT_EXTENSION;
            if(includeResource.indexOf('.') > 0) {
                extension = "";
            }
            File includeResourceFile;            
            File includesDirectory = (File) context.get(INCLUDES_DIRECTORY_KEY);
            if (includesDirectory != null) {
                includeResourceFile = new File(includesDirectory, includeResource + extension);
            } 
            else {
              includeResourceFile = new File(context.flavor.snippetsFolderName, includeResource + extension);
            }            
            Template template = Template.parse(includeResourceFile, context.flavor);
            // check if there's a optional "with expression"
            if(nodes.length > 1) {
                Object value = nodes[1].render(context);
                context.put(includeResource, value);
            }

            return template.render(context.getVariables());

        } catch(Exception e) {
            return "";
        }
    }
}
