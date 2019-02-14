/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 * updated by github.com/kellsaro
 * Feb 15, 2019
 */
 
package io.xbrl.domain.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is a XBRL Instance class
 * @author marcio May05th, 2018
 * 
 * @author kellsaro@gmail.com Feb 15th, 2019
 */
public class Instance {
	
	public final static String DOCUMENT_TYPE = "http://www.xbrl.org/CR/2017-05-02/xbrl-json";
	
	private List<Prefix> prefixList;
	private List<Dts> dtsList;
	private List<Fact> factList;
	private Map<String, Context> contextMap;
	private Map<String, Unit> unitMap;
	private Map<String, Footnote> footnoteMap;
	
	public List<Prefix> getPrefixList() {
		return prefixList = Optional.ofNullable(prefixList).orElse(new ArrayList<>());
	}
	
	public List<Dts> getDtsList() {
		return dtsList = Optional.ofNullable(dtsList).orElse(new ArrayList<>());
	}

	public List<Fact> getFactList() {
		return factList = Optional.ofNullable(factList).orElse(new ArrayList<>());
	}

	public Map<String, Context> getContextMap() {
		return contextMap = Optional.ofNullable(contextMap).orElse(new HashMap<>());
	}

	public Map<String, Unit> getUnitMap() {
		return unitMap = Optional.ofNullable(unitMap).orElse(new HashMap<>());
	}
	
	public Map<String, Footnote> getFootnoteMap() {
		return footnoteMap = Optional.ofNullable(footnoteMap).orElse(new HashMap<>());
	}
}	
