/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */

package io.xbrl.domain.service;

import org.w3c.dom.Document;

import lombok.Data;

@Data
public class XbrlFile {

	private final Document document;
	private final String fileName;
	private final long fileSize;
	
	/**
	 * <p>
	 * <b>XbrlFile</b>
	 * </p>
	 * <p>
	 * Creates the xbrl file instance.
	 * </p>
	 * 
	 * @param Document document
	 * @param String fileName
	 * @param long fileSize
	 * 
	 * @throws IllegalArgumentException if isXbrlDoc return false, if fileName is null or empty or blank, or if fileSize is not a positive number
	 */
	public XbrlFile(Document document, String fileName, long fileSize) {
		
		if (!isXbrlDoc(document)) throw new IllegalArgumentException("Must be a valid XBRL document");
		if (fileName == null) throw new IllegalArgumentException("fileName can not be null");
		if (fileSize <= 0L) throw new IllegalArgumentException("fileSize must be greater than 0");
		
		this.document = document;
		this.fileName = fileName;
		this.fileSize = fileSize;
		
	}
	
	/**
	 * <p>
	 * <b>isXbrlDoc</b>
	 * </p>
	 * <p>
	 * Inform if file is a XBRL document.
	 * </p>
	 * 
	 * @return boolean
	 * @param Document
	 */
	public static boolean isXbrlDoc(Document document) {
		
		return document != null && document.getDocumentElement().getNodeName().toLowerCase().contains("xbrl");		
	}
}
