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
import org.primaresearch.dla.page.layout.shared.GeometricObject;

/**
 * Scan element that calculates the perimeter (polygon length) of border or print space.
 *  
 * @author Christian Clausner
 *
 */
public class BoundsScanElement implements ScanElement {

	public static final int TYPE_BORDER 		= 1;
	public static final int TYPE_PRINT_SPACE 	= 2;
	
	int type;
	int perimeter = 0;
	String[] headers = {null, "Border", "PrintSpace"};
	
	/**
	 * Constructor
	 * @param type TYPE_BORDER or TYPE_PRINT_SPACE
	 */
	public BoundsScanElement(int type) {
		this.type = type;
	}
	
	@Override
	public void init(Page page) {
		GeometricObject geomObj = null;
		if (type == TYPE_BORDER)
			geomObj = page.getLayout().getBorder();
		else if (type == TYPE_PRINT_SPACE)
			geomObj = page.getLayout().getPrintSpace();
		
		if (geomObj != null && geomObj.getCoords() != null)
			perimeter = (int)(geomObj.getCoords().calculateLength()+0.5);
	}

	@Override
	public String getCsvHeader() {
		return headers[type];
	}

	@Override
	public String getCsvValue() {
		//Integer
		return ""+perimeter;
	}

}
