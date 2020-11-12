These files are used by all templates.

To access them, each template must get the full path to this folder:

		<property id="commonDir" label="common files"
			description="Will be set to ...templates.core/templates/common/ folder"
			mandatory="true" hidden="true" default="commonDir" persist="false" />

		<process
			type="org.eclipse.embedcdt.templates.core.SetPropertyToPluginResource">
			<simple name="pluginId" value="org.eclipse.embedcdt.templates.core" />
			<simple name="relativePath" value="templates/common" />
			<simple name="propertyName" value="commonDir" />
		</process>

A sample process to copy files is:

	<process type="org.eclipse.embedcdt.templates.core.AddAbsolutePathFiles">
		<simple name="projectName" value="$(projectName)" />
		<complex-array name="files">
			<element>
				<simple name="source" value="$(commonDir)/system/include/cmsis/README.txt" />
				<simple name="target"
					value="$(sysDir)/$(includeDir)/$(cmsisDir)/README.txt" />
				<simple name="replaceable" value="true" />
			</element>
		</complex-array>
	</process>

---

The main-NNN-*.{c|cpp} files are pieces combined by the template engine to 
create the main.{c|cpp} files.

Similarly the led-NN-*.h files are pieces combined by the template engine to 
create the led.h file.
