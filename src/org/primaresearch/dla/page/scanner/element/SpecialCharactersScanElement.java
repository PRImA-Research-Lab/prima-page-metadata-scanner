/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
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
import org.primaresearch.dla.page.layout.physical.text.TextObject;
import org.primaresearch.dla.page.scanner.ContentObjectHandler;

/**
 * Creates a list of characters that occur in the text content of the document.
 * The characters are output as Unicode number in decimal format.
 *  
 * @author Christian Clausner
 *
 */
public class SpecialCharactersScanElement implements ScanElement, ContentObjectHandler {

	/** Switch to optionally output the character codes in one column */
	boolean oneColumn;
	Set<Integer> specialChars = new HashSet<Integer>();
	//Set<String> normalCharHexCodes = new HashSet<String>();

	/**
	 * Default constructor
	 */
	public SpecialCharactersScanElement() {
		this(false);
	}

	/**
	 * Constructor
	 * @param oneColumn Switch to optionally output the character codes in one column
	 */
	public SpecialCharactersScanElement(boolean oneColumn) {
		this.oneColumn = oneColumn;
	}
	
	/*void initNormalCharacters() {
		String normalChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		char[] chars = normalChars.toCharArray();
		String hexCode;
		for (int i=0; i<chars.length; i++) {
			hexCode = toHexCode(chars[i]);
			normalCharHexCodes.add(hexCode);
		}
	}*/
	
	@Override
	public void init(Page page) {
	}

	@Override
	public String getCsvHeader() {
		if (oneColumn)
			return "Character code";
		return "Character codes";
	}

	@Override
	public String getCsvValue() {
		StringBuilder values = new StringBuilder();
		for (Iterator<Integer> it = specialChars.iterator(); it.hasNext(); ) {
			if (values.length() != 0) {
				if (oneColumn)
					values.append("\n,");
				else
					values.append(',');
			}
			values.append(it.next());
		}
		return values.toString();
	}

	@Override
	public void handleContentObject(ContentObject obj) {
		if (obj == null)
			return;
		//Check if text content object
		if (obj instanceof TextObject) {
			processText(((TextObject)obj).getText());
		}		
	}

	private void processText(String text) {
		if (text == null)
			return;
		char[] chars = text.toCharArray();
		//String hexCode;
		for (int i=0; i<chars.length; i++) {
			//hexCode = toHexCode(chars[i]);
			//if (!normalCharHexCodes.contains(hexCode)) 
				//specialCharHexCodes.add(hexCode);
			specialChars.add((int)chars[i]);
		}
	}
	
	//private String toHexCode(char c) {
	//	
	//}
}
