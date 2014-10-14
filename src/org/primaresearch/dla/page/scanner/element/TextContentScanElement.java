/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
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

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.text.TextObject;
import org.primaresearch.dla.page.scanner.ContentObjectHandler;

/**
 * Scan element that calculates text content statistics.
 * 
 * @author Christian Clausner
 *
 */
public class TextContentScanElement implements ScanElement,
		ContentObjectHandler {

	public static final int TYPE_COUNT_CHARACTERS 		= 1;
	public static final int TYPE_COUNT_SPACES_AND_TABS 	= 2;
	public static final int TYPE_COUNT_LINE_BREAKS 		= 3;
	public static final int TYPE_COUNT_ALL 				= 4;
	
	private String[] headers = {null, "Character Count", "Space and Tab Count", "Line Break Count", "Text Size"};
	private ContentType contentType;
	private int statisticsType;
	private int count = 0;
	
	/**
	 * Constructor
	 * @param contentType Content type (e.g. RegionType.TextRegion or LowLevelTextType.Word).
	 * @param statisticsType See TYPE_ class members.
	 */
	public TextContentScanElement(ContentType contentType, int statisticsType) {
		this.contentType = contentType;
		this.statisticsType = statisticsType;
	}
	
	@Override
	public void handleContentObject(ContentObject obj) {
		//Check if text content object
		if (obj instanceof TextObject) {
			//Check content type
			if (contentType.equals(obj.getType())) {
				calcCount(((TextObject)obj).getText());
			}
		}
	}
	
	/**
	 * Counts the occurrences of specific types of characters within the given text.
	 */
	private void calcCount(String text) {
		if (text == null)
			return;
		if (statisticsType == TYPE_COUNT_ALL) {
			count += text.length(); 
		}
		else if (statisticsType == TYPE_COUNT_LINE_BREAKS) {
			for (int i=0; i<text.length(); i++) {
				if (text.charAt(i) == '\n')
					count++;
			}
		}
		else if (statisticsType == TYPE_COUNT_SPACES_AND_TABS) {
			for (int i=0; i<text.length(); i++) {
				if (text.charAt(i) == ' ' || text.charAt(i) == '\t')
					count++;
			}
		}
		else if (statisticsType == TYPE_COUNT_CHARACTERS) {
			for (int i=0; i<text.length(); i++) {
				if (text.charAt(i) != ' ' && text.charAt(i) != '\t' && text.charAt(i) != '\n')
					count++;
			}
		}
	}

	@Override
	public void init(Page page) {
	}

	@Override
	public String getCsvHeader() {
		//Example: 'Line Break Count TextRegion'
		return headers[statisticsType] + " " + contentType.getName();
	}

	@Override
	public String getCsvValue() {
		return ""+count;
	}

}
