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
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.GroupMember;
import org.primaresearch.dla.page.layout.logical.Layers;
import org.primaresearch.dla.page.layout.logical.RegionRef;

/**
 * Scan element that counts the number of referenced regions in layers.
 * 
 * @author Christian Clausner
 *
 */
public class LayersRegionRefCountScanElement implements ScanElement {

	private Layers layers;
	private int count = 0;
	
	@Override
	public void init(Page page) {
		layers = page.getLayout().getLayers();
		if (layers != null) {
			for (int i=0; i<layers.getSize(); i++)
				countRegionRefs(layers.getLayer(i));
		}
	}
	
	void countRegionRefs(Group group) {
		for (int i=0; i<group.getSize(); i++) {
			GroupMember member = group.getMember(i);
			if (member instanceof RegionRef)
				count++;
		}
	}
	
	@Override
	public String getCsvHeader() {
		return "Layers Region Count";
	}

	@Override
	public String getCsvValue() {
		return ""+count;
	}

}
