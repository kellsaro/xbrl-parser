/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */
 
package io.xbrl.domain.service;

import java.util.Collections;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Document;

import io.xbrl.domain.instance.Context;
import io.xbrl.domain.instance.Dts;
import io.xbrl.domain.instance.Fact;
import io.xbrl.domain.instance.Footnote;
import io.xbrl.domain.instance.Instance;
import io.xbrl.domain.instance.PeriodForever;
import io.xbrl.domain.instance.PeriodInstant;
import io.xbrl.domain.instance.PeriodStartEnd;
import io.xbrl.domain.instance.Prefix;
import io.xbrl.domain.instance.Unit;
import lombok.Getter;

@Getter
public class XbrlParser {
	
	 private XbrlFile xbrlFile;
	
	 public XbrlParser(Document doc, String fileName, long fileSize) throws IllegalArgumentException {
		 
		 setXbrlFile(doc, fileName, fileSize);
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
	public static boolean isXbrlDoc(Document file) {
		
		return file != null && file.getDocumentElement().getNodeName().toLowerCase().contains("xbrl");		
	}
		
	/**
	 * <p>
	 * <b>setFile</b>
	 * </p>
	 * <p>
	 * Sets the XBRL document.
	 * </p>
	 * 
	 * @param Document
	 * @param String fileName
	 * @param long fileSize
	 * 
	 * @throws IllegalArgumentException if isXbrlDoc return false, if fileName is null or empty or blank, or if fileSize is not a positive number
	 */
	public void setXbrlFile(Document doc, String fileName, long fileSize) throws IllegalArgumentException {
		
		xbrlFile = new XbrlFile(doc, fileName, fileSize);
	}
	
	/**
	 * <p>
	 * <b>getXbrlFileAsDocument</b>
	 * </p>
	 * <p>
	 * Gets the XBRL file as Optional<org.w3c.Document> object.
	 * </p>
	 * 
	 * @return Optional with the org.w3c.Document if containsXbrlDoc() is true, or
	 * an Optional with null value if containsXbrlDoc() is false.
	 */
	public Document getXbrlFileAsDocument() {
		
		return xbrlFile.getDocument();
	}
	
	/**
	 * Preload part of processing, just for giving to user some information about loading
	 * 
	 * @param instance
	 * @return
	 */
	public static String getPreload(Instance instance) {
		
		// report
		StringBuilder json = new StringBuilder("{\n"); // root
		json.append("  \"report\" : {\n"); // start of report
		
		if (instance != null) {
			
			try {
				
				json.append(String.format("    \"documentType\":\"%s\", \n", Instance.DOCUMENT_TYPE));
				
				printPrefixes(json, instance);
				printDtses(json, instance);
				printPreloadFacts(json, instance);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		json.append("  } \n"); // end of report
		json.append("} \n"); // root

		if (isValidJSON(json.toString())) return json.toString();
		
		return null;
	}
	
	/**
	 * print in result string all prefixes from XBRL-XML document
	 * 
	 * @param json
	 * @param instance
	 */
	private static void printPrefixes(StringBuilder json, Instance instance) {
		
		if (instance.getPrefixList() != null) {
			
			Optional<Prefix> optXbrliPrefix = instance.getPrefixList().stream()
					.filter(p -> p.getName().equals("xbrli"))
					.findFirst();
			
			if (!optXbrliPrefix.isPresent())
				instance.getPrefixList().add(new Prefix("xbrli", "http://www.xbrl.org/2003/instance"));
			
			Optional<Prefix> optXbrlPrefix = instance.getPrefixList().stream()
					.filter(p -> p.getName().equals("xbrl"))
					.findFirst();
			
			if (optXbrlPrefix.isPresent()) 
				instance.getPrefixList().remove(optXbrlPrefix.get());
			
			instance.getPrefixList().add(new Prefix("xbrl","http://www.xbrl.org/CR/2017-05-02/oim"));
			
			json.append("  \"prefix\" : { \n");
			
			for (Prefix prefix: instance.getPrefixList()) 
				json.append(String.format("    \"%s\":\"%s\", \n", prefix.getName(), prefix.getValue()));
			
			json.deleteCharAt(json.toString().trim().length()-1)
			    .append("  }, \n");
		}
	}
	
	/**
	 * print in result string all dts from XBRL-XML document
	 * 
	 * @param json
	 * @param instance
	 */
	private static void printDtses(StringBuilder json, Instance instance) {
		
		if (instance.getDtsList() != null) {
			
			json.append("  \"dts\" : { \n");
			
			for (Dts dts: instance.getDtsList())
				json.append(String.format("    \"%s\":\"%s\", \n", dts.getName(), dts.getHref()));
			
			json.deleteCharAt(json.toString().trim().length()-1); //delete last "," of object
			json.append("  }, \n");
		}
	}
	
	/**
	 * Preload part of processing, just for giving to user some information about loading
	 * in this case, just need to know how many facts are in report
	 * 
	 * @param json
	 * @param instance
	 */
	private static void printPreloadFacts(StringBuilder json, Instance instance) {
		
		json.append("  \"fact\": [\n");
		json.append("      { \"msg\" : \"wait a moment, still loading facts...\" }");
		json.append("  ]\n");
	}
		
	
	
	/**
	 * print all facts from XBRL instance file
	 * 
	 * @param json
	 * @param instance
	 */
	private static void printFacts(StringBuilder json, Instance instance){
		
		if (instance.getFactList() != null) {
			
			json.append("	\"fact\": [\n");

			Queue<Fact> qfact = new ConcurrentLinkedQueue<>(Collections.unmodifiableList(instance.getFactList()));
			
			String comma = "";
			
			while (qfact.peek() != null) {
				
				Fact fact = qfact.poll();
				
				json.append(comma);
				comma = ",";
				printFact(json, fact, instance);
					
			}

			json.append("]\n");
		}
	}	
	
	/**
	 * get all prefixes in json format
	 * 
	 * @param instance
	 * @return
	 */
	public String getJustPrefixes(Instance instance) {
		
		StringBuilder json = new StringBuilder("{\n");
		
		if (instance.getPrefixList() != null) {

			
			Optional<Prefix> optXbrliPrefix = instance.getPrefixList().stream()
					.filter(p -> p.getName().equals("xbrli"))
					.findFirst();
			
			if (!optXbrliPrefix.isPresent()) 
				instance.getPrefixList().add(new Prefix("xbrli", "http://www.xbrl.org/2003/instance"));
			
			Optional<Prefix> optXbrlPrefix = instance.getPrefixList().stream()
					.filter(p -> p.getName().equals("xbrl"))
					.findFirst();
			
			if (optXbrlPrefix.isPresent()) 
				instance.getPrefixList().remove(optXbrlPrefix.get());
			
			instance.getPrefixList().add(new Prefix("xbrl", "http://www.xbrl.org/CR/2017-05-02/oim"));
			
			String comma = "";
			
			for (Prefix prefix: instance.getPrefixList()) {
				
				json.append(comma);
				comma = ",\n";
				json.append(String.format("      \"%s\":\"%s\"", prefix.getName(), prefix.getValue()));
			}
		}
		
		json.append("}\n");
		
		return isValidJSON(json.toString()) ? json.toString() : null;
	}
	
	/**
	 * get the dts data
	 * 
	 * @param instance
	 * @return
	 */
	public String getJustDts(Instance instance) {
		
		StringBuilder json = new StringBuilder("{\n\n");
		
		if (instance != null && instance.getDtsList() != null) {
			
			
			String comma = "";
			
			for (Dts dts: instance.getDtsList()) {
				
				json.append(comma);
				comma = ",\n		";
				json.append("\""+dts.getName()+"\":\""+dts.getHref()+"\"");
			}
		}
		
		if (json != null && json.length() != 0) {
			
			json.append("\n    }\n");
			
			if (isValidJSON(json.toString())) return json.toString();
		}
		
		return null;
	}
	
	/**
	 * get the facts data
	 * 
	 * @param instance
	 * @return
	 */
	public String getJustFacts(Instance instance) {
		StringBuilder json = new StringBuilder(" [\n")  ;
		if (instance.getFactList() != null) {
			
			Queue<Fact> qfact = new ConcurrentLinkedQueue<>(
					Collections.unmodifiableList(instance.getFactList())
					);
			String comma = "";
			while (qfact.peek() != null) {
				Fact fact = qfact.poll();
				json.append(comma);
				comma = ",\n";
				printFact(json, fact, instance);
			}			
		}
		
		if (json != null && json.length() != 0) {
			
			json.append("\n  ]\n");
			
			if (isValidJSON(json.toString())) {
				System.out.println("facts valid json");
				return json.toString();
			}
		}

		return null;
	}	
	
	/**
	 * "write" in a string a fact in json format
	 * 
	 * @param json
	 * @param fact
	 * @param instance
	 * @return
	 */
	private static StringBuilder printFact(StringBuilder json, Fact fact, Instance instance) {
		
		json.append("    { \n"); //open fact
		
		if (fact.getId() != null && !fact.getId().isEmpty()) json.append("      \"id\":\""+fact.getId()+"\", \n");
		
		if (fact.getValue() != null && !fact.getValue().isEmpty()) json.append("      \"value\":\""+fact.getValue()+"\", \n");
		
		json.append("      \"aspect\": { \n");
		json.append("        \"xbrl:concept\":\""+fact.getName()+"\", \n");
		
		//context
		if (instance.getContextMap() != null) {
			
			
			// -- entity
			Map<String,Context> contextMap = instance.getContextMap();
			
			Optional<Context> optContext = contextMap.values().stream()
					.filter(c -> c.getId().toLowerCase().contains(fact.getContextRef().toLowerCase()))
					.findFirst();
			
			if (optContext.isPresent()) {
				Context context = optContext.get(); 
			
				json.append("        \"xbrl:entity\":\""+context.getEntity().getCid()+"\", \n");
				// -- period					
				if (context.getPeriod() instanceof PeriodInstant) {
					PeriodInstant period = (PeriodInstant) context.getPeriod();
					json.append("        \"xbrl:periodInstant\":\""+period.getInstantPeriodvalue()+"\"");
				}else if (context.getPeriod() instanceof PeriodStartEnd) {
					PeriodStartEnd period = (PeriodStartEnd) context.getPeriod();
					json.append("        \"xbrl:periodStart\":\""+period.getStartValue()+"\", \n");
					json.append("        \"xbrl:periodEnd\":\""+period.getEndValue()+"\"");
				}else {
					PeriodForever period = (PeriodForever) context.getPeriod();
					json.append("        \""+period.getValue()+"\"");
				}
			}
		}
		
		//unit
		if (instance.getUnitMap() != null) {
			
			
			Unit unit = instance.getUnitMap().get(fact.getUnitRef());
			
			if (unit != null) {
				json.append(",\n"); // ',' from period, expecting unit
				json.append("        \"xbrl:unit\":\""+unit.getValue()+"\" \n");
			}else {
				json.append("\n"); //not expecting unit
			}
		}else {
			json.append("\n"); //not expecting unit
		}
		
		json.append("      }"); // closed aspect
		
		//footnote
		if (instance.getFootnoteMap() != null) {
						
			Footnote footnote = instance.getFootnoteMap().get("#" + fact.getId());
			
			if (footnote != null) {
				json.append(",\n"); // expecting footnote
				json.append("      \"footnote\": { \n");
				json.append("        \"group\":\"" + footnote.getGroup() + "\", \n");
				json.append("        \"footnoteType\":\"" + footnote.getFootnoteType() + "\", \n");
				json.append("        \"footnote\":\"" + footnote.getFootnote() + "\", \n");
				json.append("        \"language\":\"" + footnote.getLanguage() + "\" \n");
				json.append("      } \n");
			}
		}
		
		json.append("\n    }\n"); //close fact
		return json;
	}
	

	/**
	 * check if the string has a valid json format
	 * 
	 * @param String json
	 * @return true if json is in a valid JSON format, false other way.
	 */
	public static boolean isValidJSON(String json) {
		
		if(json == null) return false;
		
		
	    try {
	        new JSONObject(json);
	    } catch (JSONException ex) {
	    	
	        try {
	            new JSONArray(json);
	            
	        } catch (JSONException ex1) {
	        	
	        	ex1.printStackTrace();
	            return false;
	        }
	    }
	    
	    return true;
	}
	
	public String parseToJSON() {
		
		InstanceBusiness ib = new InstanceBusiness();
		ib.setRootNodeFrom(this.getXbrlFileAsDocument());
		ib.build();
		
		return parseToJSON(ib.getInstance());
	}
	
	
	/**
	 * parse Instance object (previously loaded from XBRL-XML file) to string
	 * 
	 * @param instance
	 * @return
	 */
	public static String parseToJSON(Instance instance) {
		// report
		StringBuilder json = new StringBuilder("{\n"); //root
		
		json.append("  \"report\" : {\n"); //start of report
		
		if (instance != null) {
			
			try {
				json.append(String.format("    \"documentType\":\"%s\", \n", Instance.DOCUMENT_TYPE ));
				printPrefixes(json, instance);
				printDtses(json, instance);
				printFacts(json, instance);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		json.append("  } \n"); //end of report
		json.append("} \n"); //root

		return json.toString().trim();
	}
}
