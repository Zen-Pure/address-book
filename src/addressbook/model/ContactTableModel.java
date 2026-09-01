package addressbook.model;

import javax.swing.table.*;
import java.util.*;

public class ContactTableModel extends AbstractTableModel{
    private List<Contact> contacts=new ArrayList<>();
    private List<String> displayFields;
    public void setDisplayFields(List<String> displayFields) {
        this.displayFields=displayFields;
        fireTableStructureChanged();
    }
    public void setContacts(List<Contact> contacts){
        this.contacts=new ArrayList<>(contacts);
        fireTableDataChanged();
    }
    public Contact getContactAt(int row){
        return contacts.get(row);
   }
    public void updateColumns(){
        fireTableStructureChanged();
    }
    @Override 
    public int getRowCount(){
        return contacts.size();
    }
    @Override
    public int getColumnCount(){
        return displayFields!=null?displayFields.size():2;
    }
    @Override
    public String getColumnName(int column){
        if(displayFields!=null && column<displayFields.size()){
            return displayFields.get(column);
        }
        return column==0?"姓名":"联系方式";
    }
    @Override
    public Object getValueAt(int rowIndex,int columnIndex){
        Contact contact=contacts.get(rowIndex);
        if(displayFields==null||columnIndex>=displayFields.size()){
            return columnIndex==0?contact.getName():"";
        }
        String field=displayFields.get(columnIndex);
        if(field.equals("姓名")) return nullToEmpty(contact.getName());
        if(field.equals("电话")) return nullToEmpty(contact.getPhone());
        if(field.equals("手机")) return nullToEmpty(contact.getMobile());
        if(field.equals("即时通信工具")) return nullToEmpty(contact.getImTool());
        if(field.equals("即时通信号码")) return nullToEmpty(contact.getImAccount());
        if(field.equals("电子邮箱")) return nullToEmpty(contact.getEmail());
        if(field.equals("个人主页")) return nullToEmpty(contact.getPersonalHomepage());
        if(field.equals("生日")) {
            return contact.getBirthday()!=null?
            new java.text.SimpleDateFormat("yyyy-MM-dd").format(contact.getBirthday()):"";
        }
        if(field.equals("像片")) return nullToEmpty(contact.getPhoto());
        if(field.equals("工作单位")) return nullToEmpty(contact.getCompany());            if(field.equals("家庭地址")) return nullToEmpty(contact.getAddress());
        if(field.equals("邮编")) return nullToEmpty(contact.getPostalCode());
        if(field.equals("所属组")) {
            List<String> groups=contact.getGroups();
            return (groups!=null && !groups.isEmpty())?
           String.join(",",groups):"";
        }
        if(field.equals("备注")) return nullToEmpty(contact.getComments());
        return "";
    }
    private String nullToEmpty(String str){
        return str!=null?str:"";
    }
}
