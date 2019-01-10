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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.scanner.ContentObjectHandler;
import org.primaresearch.io.FormatModel;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.constraints.ValidStringValues;
import org.primaresearch.shared.variable.constraints.VariableConstraint;

/**
 * Scan element that counts the occurrences of regions per sub-type.<br>
 * This element represents multiple CSV entries.
 * 
 * @author Christian Clausner
 *
 */
public class RegionSubTypeCountScanElement implements ScanElement,	ContentObjectHandler {

	private RegionType regionType;
	private ValidStringValues validValues = null;
	private Map<String,Integer> counts = new HashMap<String,Integer>();
	
	/**
	 * Constructor
	 * @param regionType The type of region to handle.
	 * @param formatModel Model with attribute templates.
	 */
	public RegionSubTypeCountScanElement(RegionType regionType, FormatModel formatModel) {
		this.regionType = regionType;
		
		//Extract the sub-types from the format model
		VariableMap attributeTemplates = formatModel.getTypeAttributeTemplates().get(getSchemaTypeName(regionType));
		if (attributeTemplates != null) {
			//GEt the sub-type attribute
			Variable var = attributeTemplates.get("type");
			if (var != null) {
				//Get the variable constraint (contains the list of valid sub-types)
				VariableConstraint constraint = var.getConstraint();
				if (constraint != null && constraint instanceof ValidStringValues) {
					validValues = (ValidStringValues)constraint;
				}
			}
		}
	}
	
	@Override
	public void handleContentObject(ContentObject obj) {
		if (validValues == null)
			return;
		
		//Check region type
		if (regionType.equals(obj.getType())) {
			VariableMap atts = obj.getAttributes(); 
			if (atts != null) {
				//Get type attribute
				Variable var = atts.get("type");
				if (var != null && var.getValue() != null) {
					String subtype = var.getValue().toString();
					//Increase count for the type
					Integer count = counts.get(subtype);
					if (count == null)
						count = 0;
					count++;
					counts.put(subtype, (Integer)count);
				}
			}
		}
	}

	@Override
	public void init(Page page) {
	}

	@Override
	public String getCsvHeader() {
		if (validValues == null)
			return "";
		//Comma separated headers for all sub-types
		//Example: 'TextRegion (heading) Count,TextRegion (paragraph) Count,...'
		StringBuilder str = new StringBuilder();
		for (Iterator<String> it = validValues.getValidValues().iterator(); it.hasNext(); ) {
			String subtype = it.next();
			str.append(regionType.getName());
			str.append(" (");
			str.append(subtype);
			str.append(") Count");
			if (it.hasNext())
				str.append(',');
		}
		return str.toString();
	}

	@Override
	public String getCsvValue() {
		if (validValues == null)
			return "";
		//Comma separated counts per sub-type
		StringBuilder str = new StringBuilder();
		for (Iterator<String> it = validValues.getValidValues().iterator(); it.hasNext(); ) {
			String subtype = it.next();
			Integer count = counts.get(subtype);
			if (count == null)
				count = 0;
			str.append(count.toString());
			if (it.hasNext())
				str.append(',');
		}
		return str.toString();
	}
	
	//Copied from DefaultAttributeFactory
	private String getSchemaTypeName(ContentType type) {
		if (type == RegionType.ChartRegion)
			return "ChartRegionType";
		//else if (type == RegionType.FrameRegion)
		//	return "FrameRegionType";
		else if (type == RegionType.GraphicRegion)
			return "GraphicRegionType";
		else if (type == RegionType.ImageRegion)
			return "ImageRegionType";
		else if (type == RegionType.LineDrawingRegion)
			return "LineDrawingRegionType";
		else if (type == RegionType.MathsRegion)
			return "MathsRegionType";
		else if (type == RegionType.AdvertRegion)
			return "AdvertRegionType";
		else if (type == RegionType.ChemRegion)
			return "ChemRegionType";
		else if (type == RegionType.MusicRegion)
			return "MusicRegionType";
		else if (type == RegionType.NoiseRegion)
			return "NoiseRegionType";
		else if (type == RegionType.SeparatorRegion)
			return "SeparatorRegionType";
		else if (type == RegionType.TableRegion)
			return "TableeRegionType";
		else if (type == RegionType.TextRegion)
			return "TextRegionType";
		else if (type == RegionType.UnknownRegion)
			return "UnknownRegionType";
		else if (type == LowLevelTextType.TextLine)
			return "TextLineType";
		else if (type == LowLevelTextType.Word)
			return "WordType";
		else if (type == LowLevelTextType.Glyph)
			return "GlyphType";
		return null;
	}

}
