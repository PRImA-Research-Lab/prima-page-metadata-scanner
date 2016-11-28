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
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.logical.RegionRef;

/**
 * Scan element that counts the number of referenced regions in the reading order.
 * 
 * @author Christian Clausner
 *
 */
public class ReadingOrderRegionRefCountScanElement implements ScanElement {

	private ReadingOrder order;
	private int count = 0;
	
	@Override
	public void init(Page page) {
		order = page.getLayout().getReadingOrder();
		if (order != null)
			countRegionRefs(order.getRoot());
	}
	
	void countRegionRefs(Group group) {
		for (int i=0; i<group.getSize(); i++) {
			GroupMember member = group.getMember(i);
			if (member instanceof RegionRef)
				count++;
			else if (member instanceof Group)
				countRegionRefs((Group)member);
		}
	}

	@Override
	public String getCsvHeader() {
		return "Reading Order Region Count";
	}

	@Override
	public String getCsvValue() {
		return ""+count;
	}

}
