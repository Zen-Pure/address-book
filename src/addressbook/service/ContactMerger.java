package addressbook.service;

import java.util.List;

import addressbook.model.Contact;
//联系人合并策略
public class ContactMerger {
	//将重复组合并为一个联系人
	public Contact merge(List<Contact> dups, int primaryIndex) {
		if (dups==null||dups.isEmpty()) return null;
		
		Contact p=dups.get(primaryIndex);
		Contact r=new Contact();
		r.setId(p.getId());
		r.setName(p.getName());
		r.setPhone(mergeField(dups, Contact::getPhone, true));
		r.setMobile(mergeField(dups, Contact::getMobile, true));
		r.setImTool(mergeField(dups, Contact::getImTool, false));
		r.setImAccount(mergeField(dups, Contact::getImAccount, false));
		r.setEmail(mergeField(dups, Contact::getEmail, false));
		r.setPersonalHomepage(mergeField(dups, Contact::getPersonalHomepage, false));
		r.setBirthday(mergeBirthday(dups,primaryIndex));
		r.setPhoto(mergeField(dups, Contact::getPhoto, false));
		r.setCompany(mergeField(dups, Contact::getCompany, true));
		r.setAddress(mergeField(dups, Contact::getAddress, true));
		r.setPostalCode(mergeField(dups, Contact::getPostalCode, true));
		r.setComments(mergeField(dups, Contact::getComments, false));
		
		return r;
	}
	
	private String mergeField(List<Contact> list, java.util.function.Function<Contact, String> getter,boolean keepLongest) {
		String result=null;
		for(Contact c:list) {
			String v=getter.apply(c);
			if(v==null||v.trim().isEmpty()) continue;
			
			if(result==null) {
				result=v;
			} else if(keepLongest) {
				if(v.length()>result.length()){
					result=v;
				}
			} else if(!result.equals(v)) {
				result=result+" / "+v;
			}
		}
		return result;
	}

	private java.util.Date mergeBirthday(List<Contact>list,int primaryIndex){
		Contact primary=list.get(primaryIndex);
		if(primary.getBirthday()!=null){
			return primary.getBirthday();
		}
		for (int i=0;i<list.size();i++){
			if(i==primaryIndex)
				continue;
			java.util.Date birthday=list.get(i).getBirthday();
			if(birthday!=null){
				return birthday;
			}
		}
		return null;
	}
	
	//自动选择信息最完整的作为主记录
	public int selectBestPrimary(List<Contact> dups) {
		int best=0, max=-1;
		for(int i=0; i<dups.size(); i++) {
			Contact c=dups.get(i);
			int score=0;
			if(c.getName()!=null) score++;
			if(c.getPhone()!=null) score++;
			if(c.getMobile()!=null) score++;
			if(c.getEmail()!=null) score++;
			if(c.getAddress()!=null) score++;
			if(score>max) { max=score; best=i; }
		}
		return best;
	}
}
