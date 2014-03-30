
package calendarevent;

import java.util.List;

public class Organizer{
   	private String displayName;
   	private String email;
   	private boolean self;

 	public String getDisplayName(){
		return this.displayName;
	}
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
 	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}
 	public boolean getSelf(){
		return this.self;
	}
	public void setSelf(boolean self){
		this.self = self;
	}
}
