package addressbook.service;

import java.util.*;
import addressbook.model.Contact;
//重复联系人检测器
public class DuplicateDetector {
	
	public enum Strategy {
		EXACT_NAME,    //姓名完全相同
		EXACT_MOBILE,   //手机号码相同
		FUZZY_NAME,    //姓名相似
		MULTI_FIELD    //多字段综合匹配
	}
	
	//从联系人列表中找出所有重复组
	public List<DuplicateGroup> find(List<Contact> contacts, Strategy s) {
		List<DuplicateGroup> groups=new ArrayList<>();
		Set<Integer> processed=new HashSet<>();
		
		for(int i=0; i<contacts.size(); i++) {
			Contact c1=contacts.get(i);
			if(processed.contains(c1.getId())) continue;
			
			List<Contact> dups=new ArrayList<>();
			dups.add(c1);
			
			for(int j=i+ 1; j<contacts.size(); j++) {
				Contact c2=contacts.get(j);
				if(processed.contains(c2.getId())) continue;
				
				if(isDup(c1, c2, s)) {
					dups.add(c2);
					processed.add(c2.getId());
				}
			}
			
			if(dups.size()>1) {
				processed.add(c1.getId());
				groups.add(new DuplicateGroup(dups));
			}
		}
		return groups;
	}
	
	private boolean isDup(Contact c1, Contact c2, Strategy s) {
		switch(s) {
		case EXACT_NAME: return Objects.equals(c1.getName(), c2.getName());  //姓名完全相同
		case EXACT_MOBILE: return hasSameMobile(c1, c2);  //手机号码相同
		case FUZZY_NAME: return fuzzyMatch(c1.getName(), c2.getName());      //姓名相似
		case MULTI_FIELD:
			int score=0;
			if(Objects.equals(c1.getName(), c2.getName())) score+=2;
			if(hasSameMobile(c1, c2)) score+=3;
			if(Objects.equals(c1.getEmail(), c2.getEmail())&&c1.getEmail()!=null) score+=2;
			return score>=3;
		}
		return false;
	}
	
	private boolean hasSameMobile(Contact c1, Contact c2) {
		if(c1.getMobile()==null||c2.getMobile()==null) {
			return false;
		}
		return c1.getMobile().equals(c2.getMobile());
	}
	
	private boolean fuzzyMatch(String a, String b) {
		if(a==null||b==null) return false;
		return a.contains(b)||b.contains(a)||a.equalsIgnoreCase(b);
	}
}
