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

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.scanner.ContentObjectHandler;

/**
 * Scan element that counts the occurrences of content objects of a specified type.
 * 
 * @author Christian Clausner
 *
 */
public class ContentTypeCountScanElement implements ScanElement,
		ContentObjectHandler {

	private int count = 0;
	private ContentType type;
	
	/**
	 * Constructor
	 * @param type Content object type to count.
	 */
	public ContentTypeCountScanElement(ContentType type) {
		this.type = type;
	}
	
	@Override
	public void handleContentObject(ContentObject obj) {
		//Check type
		if (obj.getType().equals(type))
			count++;
	}

	@Override
	public void init(Page page) {
	}

	@Override
	public String getCsvHeader() {
		//Example: 'TextRegion Count'
		return type.getName()+" Count";
	}

	@Override
	public String getCsvValue() {
		return ""+count;
	}

}
