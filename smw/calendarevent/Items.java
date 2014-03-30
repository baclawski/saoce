
package calendarevent;

import java.util.List;

public class Items{
   	private String created;
   	private Creator creator;
   	private End end;
   	private String etag;
   	private String htmlLink;
   	private String iCalUID;
   	private String id;
   	private String kind;
   	private Organizer organizer;
   	private Number sequence;
   	private Start start;
   	private String status;
   	private String summary;
   	private String updated;
        private String location;

    public String getiCalUID() {
        return iCalUID;
    }

    public void setiCalUID(String iCalUID) {
        this.iCalUID = iCalUID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

 	public String getCreated(){
		return this.created;
	}
	public void setCreated(String created){
		this.created = created;
	}
 	public Creator getCreator(){
		return this.creator;
	}
	public void setCreator(Creator creator){
		this.creator = creator;
	}
 	public End getEnd(){
		return this.end;
	}
	public void setEnd(End end){
		this.end = end;
	}
 	public String getEtag(){
		return this.etag;
	}
	public void setEtag(String etag){
		this.etag = etag;
	}
 	public String getHtmlLink(){
		return this.htmlLink;
	}
	public void setHtmlLink(String htmlLink){
		this.htmlLink = htmlLink;
	}
 	public String getICalUID(){
		return this.iCalUID;
	}
	public void setICalUID(String iCalUID){
		this.iCalUID = iCalUID;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
 	public String getKind(){
		return this.kind;
	}
	public void setKind(String kind){
		this.kind = kind;
	}
 	public Organizer getOrganizer(){
		return this.organizer;
	}
	public void setOrganizer(Organizer organizer){
		this.organizer = organizer;
	}
 	public Number getSequence(){
		return this.sequence;
	}
	public void setSequence(Number sequence){
		this.sequence = sequence;
	}
 	public Start getStart(){
		return this.start;
	}
	public void setStart(Start start){
		this.start = start;
	}
 	public String getStatus(){
		return this.status;
	}
	public void setStatus(String status){
		this.status = status;
	}
 	public String getSummary(){
		return this.summary;
	}
	public void setSummary(String summary){
		this.summary = summary;
	}
 	public String getUpdated(){
		return this.updated;
	}
	public void setUpdated(String updated){
		this.updated = updated;
	}
}
