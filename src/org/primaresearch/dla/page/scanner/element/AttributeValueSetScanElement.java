/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.dla.page.scanner.element;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.scanner.ContentObjectHandler;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Scan element that builds a set of values for specified content type and attribute.<br>
 * <br>
 * Example:<br>
 * Content type: TextRegion<br>
 * Attribute: Language<br>
 * Result value: German;English;French
 * 
 * @author Christian Clausner
 *
 */
public class AttributeValueSetScanElement implements ScanElement,
		ContentObjectHandler {

	ContentType contentType;
	String attributeName;
	Set<String> values = new HashSet<String>();
	
	public AttributeValueSetScanElement(ContentType contentType, String attributeName) {
		this.contentType = contentType;
		this.attributeName = attributeName;
	}
	
	@Override
	public void handleContentObject(ContentObject obj) {
		//Check type
		if (contentType.equals(obj.getType())) {
			VariableMap atts = obj.getAttributes();
			if (atts != null) {
				//Get attribute
				Variable att = atts.get(attributeName);
				if (att != null && att.getValue() != null) {
					//Add value to set
					String val = att.getValue().toString();
					values.add(val);
				}
			}
		}
	}

	@Override
	public void init(Page page) {
	}

	@Override
	public String getCsvHeader() {
		//Example: 'TextRegion (primaryLanguage)'
		return contentType.getName() + " ("+attributeName+")";
	}

	@Override
	public String getCsvValue() {
		//Build semicolon separated list of values
		StringBuilder str = new StringBuilder();
		for (Iterator<String> it = values.iterator(); it.hasNext(); ) {
			if (str.length() > 0)
				str.append(';');
			str.append(it.next());
		}
		return str.toString();
	}

}
