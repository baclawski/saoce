
package calendarevent;

import java.util.List;

public class Calendar{
   	private String accessRole;
   	private String description;
   	private String etag;
   	private List items;
   	private String kind;
   	private String summary;
   	private String timeZone;
   	private String updated;

 	public String getAccessRole(){
		return this.accessRole;
	}
	public void setAccessRole(String accessRole){
		this.accessRole = accessRole;
	}
 	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
 	public String getEtag(){
		return this.etag;
	}
	public void setEtag(String etag){
		this.etag = etag;
	}
 	public List getItems(){
		return this.items;
	}
	public void setItems(List items){
		this.items = items;
	}
 	public String getKind(){
		return this.kind;
	}
	public void setKind(String kind){
		this.kind = kind;
	}
 	public String getSummary(){
		return this.summary;
	}
	public void setSummary(String summary){
		this.summary = summary;
	}
 	public String getTimeZone(){
		return this.timeZone;
	}
	public void setTimeZone(String timeZone){
		this.timeZone = timeZone;
	}
 	public String getUpdated(){
		return this.updated;
	}
	public void setUpdated(String updated){
		this.updated = updated;
	}
}
