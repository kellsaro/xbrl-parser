/*
 * created by github.com/marcioAlexandre
 * Jun01, 2018
 * 
 */
 
package io.xbrl.domain.instance;

public class Entity {

	private String cid = "cid:";
	
	public Entity(String cid) {
		this.cid += cid;
	}
		
	public String getCid() {
		return this.cid;
	}
	
	
	
}
