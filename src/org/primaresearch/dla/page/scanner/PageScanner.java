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
package org.primaresearch.dla.page.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.ContentObjectProcessor;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.scanner.element.AttributeValueSetScanElement;
import org.primaresearch.dla.page.scanner.element.BoundsScanElement;
import org.primaresearch.dla.page.scanner.element.ContentTypeCountScanElement;
import org.primaresearch.dla.page.scanner.element.LayersRegionRefCountScanElement;
import org.primaresearch.dla.page.scanner.element.MetaDataScanElement;
import org.primaresearch.dla.page.scanner.element.ReadingOrderRegionRefCountScanElement;
import org.primaresearch.dla.page.scanner.element.RegionCountScanElement;
import org.primaresearch.dla.page.scanner.element.RegionSubTypeCountScanElement;
import org.primaresearch.dla.page.scanner.element.ScanElement;
import org.primaresearch.dla.page.scanner.element.SpecialCharactersScanElement;
import org.primaresearch.dla.page.scanner.element.TextContentScanElement;
import org.primaresearch.io.FormatModel;
import org.primaresearch.io.UnsupportedFormatVersionException;

/**
 * Command line tool that scans a single PAGE XML file and outputs its properties in CSV format.<br>
 * <br>
 * Properties:<br>
 * <ul>
 *  <li>Metadata (ID, creator, creation time, modification time, width, height)</li>
 *  <li>Border and print space (true/false)</li>
 *  <li>Content objects count (per type and sub-type)</li>
 *  <li>Text content statistics (number of characters and white spaces)</li>
 *  <li>Language and script (semicolon separated list)</li>
 *  <li>Reading order and layers (number of region references)</li>
 * </ul>
 * 
 * @author Christian Clausner
 *
 */
public class PageScanner {

	private static final String MODE_DEFAULT = "default"; 
	private static final String MODE_SPECIAL_CHARS = "characters"; 
	private static final String MODE_SPECIAL_CHARS_ONE_COLUMN = "characters-one-column"; 
	
	private String pageFilename = ""; 
	private List<ScanElement> scanElements = new ArrayList<ScanElement>();
	private FormatModel formatModel = null;
	private String mode = "default";
	
	/**
	 * Main function
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			showUsage();
			return;
		}

		PageScanner scanner = new PageScanner();
		
		//Parse arguments
		String filename = null;
		boolean printHeaders = false;
		for (int i=0; i<args.length; i++) {
			if ("-csv-headers".equals(args[i])) {
				printHeaders = true;
			}
			else if ("-scan".equals(args[i])) {
				i++;
				filename = args[i];
			}
			else if ("-mode".equals(args[i])) {
				i++;
				scanner.setMode(args[i]);
			}
			else {
				System.err.println("Unknown argument: "+args[i]);
			}
		}
		if (printHeaders) {
			scanner.printHeaders();
			return;
		}
		if (filename != null) {
			try {
				scanner.scan(filename);
			} catch (UnsupportedFormatVersionException e) {
				e.printStackTrace();
				return;
			}
			scanner.printValues();
			return;
		}
	}
	
	/**
	 * Prints help for usage to std-out
	 */
	private static void showUsage() {
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("");
		System.out.println(" To output the CSV headers:");
		System.out.println("");
		System.out.println("  ... -csv-headers");
		System.out.println("");
		System.out.println(" To output the scanned CSV values for a PAGE XML file:");
		System.out.println("");
		System.out.println("  ... -scan <PAGE XML file>");
		System.out.println("");
		System.out.println(" Scan mode (optional): -mode <m>");
		System.out.println("     Supported modes:");
		System.out.println("         default - Outputs metadata, content object counts, text statistics, ...");
		System.out.println("         characters - Outputs a list of characters occurring in the text content (Unicode)");
		System.out.println("         characters-one-column - Outputs the characters as multiple rows in one column.");
	}

	
	/**
	 * Constructor
	 */
	public PageScanner() {
		this.formatModel = PageXmlInputOutput.getLatestSchemaModel();
		
		//addScanElements();
	}
	
	/**
	 * Sets the scan mode
	 * @param mode 'default', 'characters', or 'characters-one-column'
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	/**
	 * Adds the scan elements according to the set scan mode
	 */
	private void addScanElements() {
		if (MODE_DEFAULT.equals(mode)) {
			//Meta data
			scanElements.add(new MetaDataScanElement(MetaDataScanElement.TYPE_PCGTS_ID));
			scanElements.add(new MetaDataScanElement(MetaDataScanElement.TYPE_CREATOR));
			scanElements.add(new MetaDataScanElement(MetaDataScanElement.TYPE_CREATED));
			scanElements.add(new MetaDataScanElement(MetaDataScanElement.TYPE_MODIFIED));
			scanElements.add(new MetaDataScanElement(MetaDataScanElement.TYPE_WIDTH));
			scanElements.add(new MetaDataScanElement(MetaDataScanElement.TYPE_HEIGHT));
			
			//Border and Print Space
			scanElements.add(new BoundsScanElement(BoundsScanElement.TYPE_BORDER));
			scanElements.add(new BoundsScanElement(BoundsScanElement.TYPE_PRINT_SPACE));
			
			//Content type count
			scanElements.add(new RegionCountScanElement());
			scanElements.add(new ContentTypeCountScanElement(LowLevelTextType.TextLine));
			scanElements.add(new ContentTypeCountScanElement(LowLevelTextType.Word));
			scanElements.add(new ContentTypeCountScanElement(LowLevelTextType.Glyph));
			scanElements.add(new ContentTypeCountScanElement(RegionType.ChartRegion));
			//scanElements.add(new ContentTypeCountScanElement(RegionType.FrameRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.GraphicRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.ImageRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.LineDrawingRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.MathsRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.AdvertRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.ChemRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.MusicRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.NoiseRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.SeparatorRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.TableRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.TextRegion));
			scanElements.add(new ContentTypeCountScanElement(RegionType.UnknownRegion));
			
			//Region sub-type count
			scanElements.add(new RegionSubTypeCountScanElement(RegionType.TextRegion, formatModel));
			scanElements.add(new RegionSubTypeCountScanElement(RegionType.GraphicRegion, formatModel));
			scanElements.add(new RegionSubTypeCountScanElement(RegionType.ChartRegion, formatModel));
			
			//Text statistics
			scanElements.add(new TextContentScanElement(RegionType.TextRegion, TextContentScanElement.TYPE_COUNT_CHARACTERS));
			scanElements.add(new TextContentScanElement(RegionType.TextRegion, TextContentScanElement.TYPE_COUNT_SPACES_AND_TABS));
			scanElements.add(new TextContentScanElement(RegionType.TextRegion, TextContentScanElement.TYPE_COUNT_LINE_BREAKS));
			scanElements.add(new TextContentScanElement(RegionType.TextRegion, TextContentScanElement.TYPE_COUNT_ALL));
	
			scanElements.add(new TextContentScanElement(LowLevelTextType.TextLine, TextContentScanElement.TYPE_COUNT_CHARACTERS));
			scanElements.add(new TextContentScanElement(LowLevelTextType.TextLine, TextContentScanElement.TYPE_COUNT_SPACES_AND_TABS));
			scanElements.add(new TextContentScanElement(LowLevelTextType.TextLine, TextContentScanElement.TYPE_COUNT_ALL));
	
			scanElements.add(new TextContentScanElement(LowLevelTextType.Word, TextContentScanElement.TYPE_COUNT_ALL));
	
			scanElements.add(new TextContentScanElement(LowLevelTextType.Glyph, TextContentScanElement.TYPE_COUNT_ALL));
			
			//Language and script
			scanElements.add(new AttributeValueSetScanElement(RegionType.TextRegion, "primaryLanguage"));
			scanElements.add(new AttributeValueSetScanElement(RegionType.TextRegion, "secondaryLanguage"));
			scanElements.add(new AttributeValueSetScanElement(RegionType.TextRegion, "primaryScript"));
			scanElements.add(new AttributeValueSetScanElement(RegionType.TextRegion, "secondaryScript"));
	
			//Reading order and layers
			scanElements.add(new ReadingOrderRegionRefCountScanElement());
			scanElements.add(new LayersRegionRefCountScanElement());
		}
		else if (MODE_SPECIAL_CHARS.equals(mode)) {
			scanElements.add(new SpecialCharactersScanElement());
		}
		else if (MODE_SPECIAL_CHARS_ONE_COLUMN.equals(mode)) {
			scanElements.add(new SpecialCharactersScanElement(true));
		}
		else {
			throw new IllegalArgumentException("Unknown scan mode: "+mode);
		}
	}
	
	/**
	 * Prints the CSV headers to STDOUT
	 */
	private void printHeaders() {
		if (scanElements.isEmpty())
			addScanElements();
		StringBuilder str = new StringBuilder();
		
		//File name is hard-coded
		str.append("File");
		
		for (int i=0; i<scanElements.size(); i++) {
			str.append(',');
			str.append(scanElements.get(i).getCsvHeader());
		}
		System.out.println(str.toString());
	}

	/**
	 * Scans the specified PAGE XML file.
	 */
	private void scan(String pageFilename) throws UnsupportedFormatVersionException {
		if (scanElements.isEmpty())
			addScanElements();
		this.pageFilename = pageFilename;
		XmlPageReader reader = PageXmlInputOutput.getReader();
		scan(reader.read(new FileInput(new File(pageFilename))));
	}

	/**
	 * Scans the given page object.
	 */
	private void scan(Page page) {
	
		//Init scan elements
		for (int i=0; i<scanElements.size(); i++) {
			scanElements.get(i).init(page);
		}
		
		//Handle content objects
		ContentObjectProcessor processor = new ContentObjectProcessor() {
			@Override
			public void doProcess(ContentObject contentObject) {
				HandleContentObject(contentObject);
			}
		};
		processor.run(page);
		
		/*PageLayout layout = page.getLayout();
		for (int r=0; r<layout.getRegionCount(); r++) {
			Region region = layout.getRegion(r);
			HandleContentObject(region);
			
			if (region instanceof LowLevelTextContainer) {
				ProcessChildren((LowLevelTextContainer)region);
			}
		}*/
	}
	
	/**
	 * Processes the child content objects of the given parent (recursive).
	 */
	/*private void ProcessChildren(LowLevelTextContainer parent) {
		for (int c=0; c<parent.getTextObjectCount(); c++) {
			TextObject obj = parent.getTextObject(c);
			HandleContentObject((ContentObject)obj);
			
			if (obj instanceof LowLevelTextContainer) {
				ProcessChildren((LowLevelTextContainer)obj);
			}
		}
	}*/
	
	/**
	 * Calls all content object handlers for the given object.
	 */
	private void HandleContentObject(ContentObject obj) {
		ScanElement scanElement;
		for (int i=0; i<scanElements.size(); i++) {
			scanElement = scanElements.get(i);
			if (scanElement instanceof ContentObjectHandler) {
				((ContentObjectHandler)scanElement).handleContentObject(obj);
			}
		}
	}
	
	/**
	 * Prints the CSV values to STDOUT
	 */
	private void printValues() {
		StringBuilder str = new StringBuilder();
		
		//File name is hard-coded
		str.append(pageFilename);
		
		for (int i=0; i<scanElements.size(); i++) {
			str.append(',');
			str.append(scanElements.get(i).getCsvValue());
		}
		System.out.println(str.toString());
	}
}
