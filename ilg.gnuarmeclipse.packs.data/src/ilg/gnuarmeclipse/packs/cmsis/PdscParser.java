/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.cmsis;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.data.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PdscParser {

	protected MessageConsoleStream fOut;

	protected IPath fPath;
	protected Document fDocument;

	// private Repos m_repos;

	public PdscParser() {

		fOut = ConsoleStream.getConsoleOut();
	}

	public void setDocument(Document document) {
		fDocument = document;
	}

	public Document getDocument() {
		return fDocument;
	}

	protected String extendDescription(String description, String value) {
		return extendDescription(description, null, value);
	}

	protected String extendDescription(String description, String comment, String value) {

		if (value.length() > 0) {
			if (description.length() > 0)
				description += "\n";
			if (comment != null && comment.length() > 0) {
				description += comment + ": ";
			}
			description += value;
		}
		return description;
	}

	protected String extendName(String name, String value) {

		if (value.length() > 0) {
			if (name.length() > 0)
				name += " / ";
			name += value;
		}
		return name;
	}

	protected String updatePosixSeparators(String spath) {
		return spath.replace('\\', '/');
	}

	public Document parseXml(IPath path) throws ParserConfigurationException, SAXException, IOException {

		File file = path.toFile();
		if (file == null) {
			throw new FileNotFoundException(path.toFile().toString());
		}

		fPath = path;
		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		fDocument = xml.parse(inputSource);

		return fDocument;
	}

	public Document parseXml(File file) throws ParserConfigurationException, SAXException, IOException {

		fPath = new Path(file.getPath());
		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		fDocument = xml.parse(inputSource);

		return fDocument;
	}

	public Document parseXml(URL url) throws IOException, ParserConfigurationException, SAXException {

		long beginTime = System.currentTimeMillis();

		fOut.println("Fetching & parsing \"" + url + " ...");

		// m_url = url;

		InputStream is = Utils.checkForUtf8BOM(url.openStream());

		InputSource inputSource = new InputSource(new InputStreamReader(is));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		fDocument = xml.parse(inputSource);

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		fOut.println("Completed in " + duration + "ms.");

		return fDocument;
	}

	// ------------------------------------------------------------------------
}
