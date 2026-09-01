package addressbook.service;

import addressbook.model.Contact;
import java.util.ArrayList;
import java.util.List;

//重复联系人群组
public class DuplicateGroup {
	private List<Contact> contacts;    //表明这组里有哪些联系人
	private Contact merged;            //合并后的结果（预览用）
	
	public DuplicateGroup(List<Contact> contacts) {
		this.contacts=new ArrayList<>(contacts);
	}
	
	public List<Contact> getContacts() { return contacts; }
	public Contact getMerged() { return merged; }
	public void setMerged(Contact merged) { this.merged=merged; }
	
	@Override
	public String toString() {
		return "Group{size="+contacts.size()+", names="+
				contacts.stream().map(Contact::getName).toList()+"}";
	}
}
