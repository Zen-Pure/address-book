package addressbook.service;

import java.io.IOException;
import java.util.List;

import addressbook.model.Contact;

//导入导出统一入口
public class ContactIOFacade {
	
	private CsvHandler csv=new CsvHandler();
	private VCardHandler vcard=new VCardHandler();
	
	//根据格式CSV或VCARD调用对应的Handler导出
	public void exportAll(List<Contact> contacts, String path, Format f) throws IOException {
		switch(f) {
		case CSV: csv.export(contacts, path); break;
		case VCARD: vcard.export(contacts, path); break;
		}
	}
	
	//根据格式CSV或VCARD调用对应的Handler导入
	public List<Contact> importAll(String path, Format f) throws IOException {
		switch(f) {
		case CSV: return csv.importFrom(path);
		case VCARD: return vcard.importFrom(path);
		default: throw new IllegalArgumentException("不支持的格式");
		}
	}

	public enum Format {CSV, VCARD}
}
