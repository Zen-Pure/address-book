package addressbook.function;

import java.util.List;
import javax.swing.*;
import java.awt.*;

import addressbook.model.Contact;
import addressbook.service.ContactSearch;
import addressbook.ui.Table;

public class SearchFunction {
    private ContactSearch contactSearch;
    private ContactFunction cf;
    private Table t;
    private JLabel statusLabel;
    private JTextField search;
    private Font textFont=new Font("微软雅黑",Font.PLAIN,12);
    private boolean searchActive=false;

    public JTextField getSearch(){
        return search;
    }
    public boolean isSearchActive(){
        return searchActive;
    }
    public void setStatusLabel(JLabel statusLabel){ 
        this.statusLabel=statusLabel;
    }
    public void setSearch(JTextField s){
         search=s;
    }
    public void setContactFunction(ContactFunction cf){
        this.cf=cf;
    }
    public void setTable(Table t){
        this.t=t;
    }
    
    public void updateContactSearch() {
        if(cf!=null&&cf.getAllContacts()!=null){
            contactSearch=new ContactSearch(cf.getAllContacts());
        }
        contactSearch=new ContactSearch(cf.getAllContacts());
    }

    public void clearSearch(){
        if(search!=null){
            search.setText("");
        }
        searchActive=false;
        if(!cf.isShowTrash()){
            t.selectGroup("全部联系人");
        }
    }

    //搜索联系人
    public void searchContacts() {
        if(cf.isShowTrash()){
            JOptionPane.showMessageDialog
            (null,"回收站中不支持搜索，请返回所有联系人！","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(contactSearch==null){
            updateContactSearch();
        }
        String keyword=search.getText();
        contactSearch=new ContactSearch(cf.getAllContacts());
        searchActive=true;
        List<Contact> results=contactSearch.search(keyword);
        t.getTableModel().setContacts(results);
        if(keyword==null || keyword.trim().isEmpty()){
            statusLabel.setText("显示所有联系人 | 共 " + results.size() + " 个联系人");
        } 
        else{
            statusLabel.setText("搜索 \"" + keyword + "\" 的结果 | 共 " + results.size() + " 个联系人");
        }
        statusLabel.setFont(textFont);
    }

    public void refreshSearchView(){
        if(searchActive&&search!=null&&!search.getText().trim().isEmpty()){
            searchContacts();;
        }
    }
}
