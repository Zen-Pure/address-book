package addressbook.service;

import java.util.*;
import java.util.stream.Collectors;
import addressbook.model.Contact;

//联系人搜索功能
public class ContactSearch {
	
	private List<Contact> allContacts;
	
	public ContactSearch(List<Contact> contacts) {
		this.allContacts=new ArrayList<>(contacts);
	}
	
	//模糊搜索
	public List<Contact> search(String keyword) {
		if(keyword==null||keyword.trim().isEmpty()) {
			return sortByPinyin(allContacts);
		}
		
		String lower=keyword.toLowerCase().trim();
		
		List<Contact> results=allContacts.stream().filter(c->match(c, lower)).collect(Collectors.toList());
		
		return sortByPinyin(results);
	}
	
	private boolean match(Contact c, String keyword) {
		//匹配姓名
		if(c.getName()!=null&&c.getName().toLowerCase().contains(keyword)) return true;
		//匹配电话
		if(c.getPhone()!=null&&c.getPhone().contains(keyword)) return true;
		//匹配手机
		if(c.getMobile()!=null&&c.getMobile().contains(keyword)) return true;
		//匹配姓的拼音首字母
		if(c.getPinyinInitial()!=null&&c.getPinyinInitial().toLowerCase().equals(keyword)) return true;
		//匹配姓名的拼音首字母
		if (c.getPinyinInitials()!=null&&c.getPinyinInitials().toLowerCase().equals(keyword)) return true;
		//匹配全拼
		if(c.getPinyin()!=null&&c.getPinyin().toLowerCase().contains(keyword)) return true;
		
		return false;
	}
	
	//按拼音排序
	private List<Contact> sortByPinyin(List<Contact> list) {
		list.sort(Comparator.comparing(c -> 
		c.getPinyin()!=null?c.getPinyin():""));
		return list;
	}
	
	//按姓的拼音首字母分组
	public Map<String, List<Contact>> groupByInitial(List<Contact> contacts) {
		Map<String, List<Contact>> groups=new TreeMap<>();
		for(Contact c:contacts) {
			String initial=c.getPinyinInitial()!=null?c.getPinyinInitial().toUpperCase():"#";
			groups.computeIfAbsent(initial, k->new ArrayList<>()).add(c);
		}
		return groups;
	}
}
