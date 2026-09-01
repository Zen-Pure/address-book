package addressbook.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import addressbook.model.Contact;
//CSV格式的导入导出
public class CsvHandler {

	private static final String HEADER=
	"姓名,电话,手机,即时通信工具,即时通信工具号码,电子邮箱,个人主页,生日,像片,工作单位,家庭地址,邮编,所属组,备注";
	private static final String DELIMITER=",";
	
	//把联系人列表写入Excel能打开的.csv文件
	public void export(List<Contact> contacts, String filePath) throws IOException {
		try(BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
			w.write(HEADER);
			w.newLine();
			for(Contact c : contacts){
				String[] field={
					String.valueOf(c.getName()),
					escape(c.getPhone()),
					escape(c.getMobile()),
					escape(c.getImTool()),
					escape(c.getImAccount()),
					escape(c.getEmail()),
					escape(c.getPersonalHomepage()),
					escape(formatDate(c.getBirthday())),
					escape(c.getPhoto()),
					escape(c.getCompany()),
					escape(c.getAddress()),
					escape(c.getPostalCode()),
					escape(formatGroups(c.getGroups())),
					escape(c.getComments()),
				};		
				w.write(String.join(DELIMITER,field));
				w.newLine();
			}
		}
	}
	
	//从.csv文件读取联系人数据
	public List<Contact> importFrom(String filePath) throws IOException {
		List<Contact> list=new ArrayList<>();
		
		try(BufferedReader r=new BufferedReader(new FileReader(filePath))) {
			r.readLine();  //跳过表头
			String line;
			while((line=r.readLine())!=null) {
				String[] f=line.split(DELIMITER, -1);
				if(f.length>=2) {
					Contact c=new Contact();
					c.setName(unescape(f[0]));
					c.setPhone(unescape(f[1]));
					c.setMobile(unescape(f[2]));
					c.setImTool(unescape(f[3]));
					c.setImAccount(unescape(f[4]));
					c.setEmail(unescape(f[5]));
					c.setPersonalHomepage((unescape(f[6])));
					c.setBirthday(parseDate(unescape(f[7])));
					c.setPhoto(unescape(f[8]));
					c.setCompany(unescape(f[9]));
					c.setAddress(unescape(f[10]));
					c.setPostalCode(unescape(f[11]));
					c.setGroups(parseGroups(unescape(f[12])));
					c.setComments(f[13]);
					if(c.getName()!=null && !c.getName().isEmpty()){
						c.updatePinyin(c.getName());
					}
					list.add(c);
				}
			}
		}
		return list;
	}
	
	private String escape(String v) {
		if(v==null) return "";
		if(v.contains(",")||v.contains("\"")||v.contains("\n")) {
			return "\""+v.replace("\"", "\"\"")+"\"";
		}
		return v;
	}

	private String unescape(String v) {
		if(v==null) return null;
		v=v.trim();
		if(v.startsWith("\"")&&v.endsWith("\"")) {
			v=v.substring(1, v.length()-1).replace("\"\"", "\"");
		}
		return v;
	}
	 
	private String formatDate(java.util.Date date){
		if (date==null){
			return "";
		}
		java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	 
	private java.util.Date parseDate(String dateStr){
		if(dateStr==null || dateStr.trim().isEmpty()){
			return null;
		}
		try{
			java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
			sdf.setLenient(false);
			return sdf.parse(dateStr);
		}
		catch(java.text.ParseException e){
			return null;
		}
	}

	private String formatGroups(List<String> groups){
		if(groups==null || groups.isEmpty()){
			return "";
		}
		return String.join(";",groups);
	}

	private List<String> parseGroups(String groupsStr){
		List<String> groups=new ArrayList<>();
		if(groupsStr==null || groupsStr.trim().isEmpty()){
			return groups;
		}
		String[] parts=groupsStr.split(";");
		for(String part:parts){
			if(!part.trim().isEmpty()){
				groups.add(part.trim());
			}
		}
		return groups;
	}
}