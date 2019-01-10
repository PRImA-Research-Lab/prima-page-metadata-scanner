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

/**
 * Interface for PAGE scanner elements. One element can represent a single or multiple CSV entries.
 * 
 * @author Christian Clausner
 *
 */
public interface ScanElement {

	/**
	 * Initialisation (called before any other method).
	 */
	public void init(Page page);

	/**
	 * Returns the CSV header(s) of this scan element. If the element represents multiple CSV entries,
	 * the individual headers must be comma separated.
	 */
	public String getCsvHeader();
	
	/**
	 * Returns the CSV value(s) of this scan element. If the element represents multiple CSV entries,
	 * the individual values must be comma separated.
	 */
	public String getCsvValue();
	
}
