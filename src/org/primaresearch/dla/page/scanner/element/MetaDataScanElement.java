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

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.metadata.MetaData;

/**
 * Scan element for meta data entries.
 * 
 * @author Christian Clausner
 *
 */
public class MetaDataScanElement implements ScanElement {

	Page page = null;
	MetaData metaData = null;
	int type = 0;
	String[] headers = {null,"PcGtsID", "Creator", "Created", "Modified", "Width", "Height"};
	
	public static final int TYPE_PCGTS_ID 	= 1;
	public static final int TYPE_CREATOR 	= 2;
	public static final int TYPE_CREATED 	= 3;
	public static final int TYPE_MODIFIED 	= 4;
	public static final int TYPE_WIDTH 		= 5;
	public static final int TYPE_HEIGHT 	= 6;
	
	/**
	 * Constructor
	 * @param type See TYPE_ class members.
	 */
	public MetaDataScanElement(int type) {
		this.type = type;
	}
	
	@Override
	public void init(Page page) {
		this.page = page;
		this.metaData = page.getMetaData();
	}

	@Override
	public String getCsvHeader() {
		return headers[type];
	}

	@Override
	public String getCsvValue() {
		if (metaData == null)
			return "";
		if (type == TYPE_PCGTS_ID)
			return page.getGtsId() != null ? page.getGtsId().toString() : "";
		else if (type == TYPE_CREATOR)
			return metaData.getCreator();
		else if (type == TYPE_CREATED)
			return MetaData.DATE_FORMAT.format(metaData.getCreationTime());
		else if (type == TYPE_MODIFIED)
			return MetaData.DATE_FORMAT.format(metaData.getLastModificationTime());
		else if (type == TYPE_WIDTH)
			return ""+page.getLayout().getWidth();
		else if (type == TYPE_HEIGHT)
			return ""+page.getLayout().getHeight();
		return "";
	}

}
