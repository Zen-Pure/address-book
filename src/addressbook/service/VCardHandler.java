package addressbook.service;

import addressbook.model.Contact;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//vCard格式的导入导出
public class VCardHandler {
	
	private static final String BEGIN="BEGIN:VCARD";
	private static final String END="END:VCARD";

	//把联系人列表写入手机通讯录通用格式:.vcf文件
	public void export(List<Contact> contacts, String filePath) throws IOException {
		try(BufferedWriter w=new BufferedWriter(new FileWriter(filePath))) {
			for(Contact c:contacts) {
				w.write(BEGIN); w.newLine();
				w.write("VERSION:3.0"); w.newLine();
				w.write("FN:"+n(c.getName())); w.newLine();
				w.write("N:"+n(c.getName())+";;;;"); w.newLine();
				
				if(c.getPhone()!=null&&!c.getPhone().isEmpty()) {
					w.write("TEL;TYPE=HOME:"+c.getPhone()); w.newLine();
				}
				if(c.getMobile()!=null&&!c.getMobile().isEmpty()) {
					w.write("TEL;TYPE=CELL:"+c.getMobile()); w.newLine();
				}
				if(c.getImTool()!=null&&!c.getImTool().isEmpty()) {
					w.write("X-IMTOOL:"+c.getImTool()); w.newLine();
				}
				if(c.getImAccount()!=null&&!c.getImAccount().isEmpty()) {
					w.write("X-IMACCOUNT:"+c.getImAccount());w.newLine();
				}
				if(c.getEmail()!=null&&!c.getEmail().isEmpty()) {
					w.write("EMAIL;TYPE=HOME:"+c.getEmail()); w.newLine();
				}
				if(c.getPersonalHomepage()!=null&&!c.getPersonalHomepage().isEmpty()) {
					w.write("URL;TYPE=HOME:"+c.getPersonalHomepage());w.newLine();
				}
				if(c.getBirthday()!=null) {
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
					w.write("BDAY:"+sdf.format(c.getBirthday()));w.newLine();
				}
				if(c.getPhoto()!=null&&!c.getPhoto().isEmpty()) {
					w.write("PHOTO;VALUE=URI:"+c.getPhoto());w.newLine();
				}
				if(c.getCompany()!=null&&!c.getCompany().isEmpty()) {
					w.write("ORG:"+c.getCompany());w.newLine();
					w.write("TITLE:"+c.getCompany());w.newLine();
				}
				if(c.getAddress()!=null&&!c.getAddress().isEmpty()) {
					w.write("ADR;TYPE=HOME:;;"+c.getAddress() + ";;;;"); w.newLine();
				}
				if(c.getPostalCode()!=null&&!c.getPostalCode().isEmpty()) {
					w.write("ADR;TYPE=POSTAL:;;"+c.getPostalCode() + ";;;;");w.newLine();
				}
				if(c.getGroups()!=null&&!c.getGroups().isEmpty()) {
					w.write("CATEGORIES:"+String.join(",",c.getGroups()));w.newLine();
				}
				if(c.getComments()!=null&&!c.getComments().isEmpty()) {
					w.write("NOTE:"+c.getComments());w.newLine();
				}
				w.write(END); w.newLine();
			}
		}
	}
	
	//从.vcf文件读取联系人数据
	public List<Contact> importFrom(String filePath) throws IOException {
		List<Contact> list=new ArrayList<>();
		StringBuilder buf=new StringBuilder();
		
		try(BufferedReader r=new BufferedReader(new FileReader(filePath))) {
			String line;
			while((line=r.readLine())!=null) {
				if(line.equals(BEGIN)) {
					buf=new StringBuilder();
				}
				buf.append(line).append("\n");

				if(line.equals(END)) {
					Contact c=parse(buf.toString());
					if(c!=null&&c.getName()!=null) {
						c.updatePinyin(c.getName());
						list.add(c);
					}
				}
			}
		}
		return list;
	}

	private Contact parse(String data) {
		Contact c=new Contact();
		List<String> groups=new ArrayList<>();
		String[] lines=data.split("\n");
		
		for(String line:lines) {
			if(line.startsWith("FN:")) {
				c.setName(line.substring(3));
			} else if(line.startsWith("TEL;TYPE=HOME:")) {
				c.setPhone(afterColon(line));
			} else if(line.startsWith("TEL;TYPE=CELL:")) {
				c.setMobile(afterColon(line));
			} else if(line.startsWith("EMAIL")) {
				c.setEmail(afterColon(line));
			} else if(line.startsWith("X-IMTOOL:")) {
				c.setImTool(afterColon(line));
			} else if(line.startsWith("X-IMACCOUNT:")) {
				c.setImAccount(afterColon(line));
			} else if(line.startsWith("URL")) {
				c.setPersonalHomepage(afterColon(line));
			} else if(line.startsWith("BDAY:")) {
				c.setBirthday(parseVCardDate(afterColon(line)));
			} else if(line.startsWith("PHOTO")) {
				String photoValue=extractPhotoValue(line);
				if(photoValue!=null&&!photoValue.isEmpty()){
					c.setPhoto(photoValue);
				}
			} else if(line.startsWith("ORG:")) {
				c.setCompany(afterColon(line));
			} else if(line.startsWith("ADR;TYPE=HOME:")) {
				String addr=afterColon(line);
				String[] addrParts=addr.split(";");
				if(addrParts.length>=3){
					c.setAddress(addrParts[2]);
				}
			} else if(line.startsWith("ADR;TYPE=POSTAL:")) {
				String addr=afterColon(line);
				String[] addrParts=addr.split(";");
				if(addrParts.length>=3){
					c.setPostalCode(addrParts[2]);
				}
			} else if(line.startsWith("CATEGORIES:")) {
				String cats=afterColon(line);
				String[] catArray=cats.split(",");
				for(String cat:catArray){
					if(!cat.trim().isEmpty()){
						groups.add(cat.trim());
					}
				}
			} else if(line.startsWith("NOTE:")) {
				c.setComments(afterColon(line));
			}
		}
		if(!groups.isEmpty()){
			c.setGroups(groups);
		}
		return c;
	}
	private String extractPhotoValue(String line){
		int colonIndex=line.indexOf(":");
		if(colonIndex==-1||colonIndex+1>=line.length()){
			return null;
		}
		String photoValue=line.substring(colonIndex+1).trim();
		if(photoValue.isEmpty()){
			return null;
		} 
		if(!photoValue.startsWith("data:")){
			return photoValue;
		}
		return null;
	}
	private java.util.Date parseVCardDate(String dateStr) {
		if (dateStr==null || dateStr.isEmpty())
			return null;
		try {
			SimpleDateFormat sdf;
			if(dateStr.length()==8){
				sdf=new SimpleDateFormat("yyyyMMdd");
			}
			else if(dateStr.contains("-")){
				sdf=new SimpleDateFormat("yyyy-MM-dd");
			}
			else{
				return null;
			}
			sdf.setLenient(false);
			return sdf.parse(dateStr);
		}
		catch(java.text.ParseException e){
			return null;
		}
	}
	private String n(String s) {return s!=null?s:""; }
	private String afterColon(String s) { return s.substring(s.indexOf(":")+1); }
}
