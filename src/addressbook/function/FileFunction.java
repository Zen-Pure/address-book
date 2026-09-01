package addressbook.function;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import addressbook.model.Contact;
import addressbook.service.ContactIOFacade;
import addressbook.ui.Table;

public class FileFunction{
    private ContactFunction cf=new ContactFunction();
    private ContactIOFacade ioFacade=new ContactIOFacade();
    private GroupFunction gf=new GroupFunction();
    private Table t;
    private SearchFunction sf=new SearchFunction();
    private JLabel statusLabel;
    private Font textFont=new Font("微软雅黑",Font.PLAIN,12);
    
    public void setStatusLabel(JLabel statusLabel){ 
        this.statusLabel=statusLabel;
    }
    public void setTable(Table t){
        this.t=t;
    }
    public void setContactFunction(ContactFunction cf)
    {
        this.cf=cf;
    }
    public void setGroupFunction(GroupFunction gf){
        this.gf=gf;
    }
    public void setSearchFunction(SearchFunction sf)
    {
        this.sf=sf;
    }

    //导入文件
    public void importFile(ContactIOFacade.Format format){
        JFileChooser fileChooser=new JFileChooser();
        String extension=format==ContactIOFacade.Format.CSV ? "csv" : "vcf";
        String description=format==ContactIOFacade.Format.CSV ? "CSV文件 (*.csv)" : "vCard文件 (*.vcf)";
        fileChooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            try{
                String filePath=fileChooser.getSelectedFile().getAbsolutePath();
                List<Contact> importedContacts = ioFacade.importAll(filePath, format);
                if (importedContacts != null && !importedContacts.isEmpty()){
                    int maxId = 0;
                    for(Contact c : cf.getAllContacts()){
                        if(c.getId()!=null && c.getId()>maxId){
                            maxId = c.getId();
                        }
                    }
                    for (Contact contact : importedContacts){
                        maxId++;
                        contact.setId(maxId);
                        if (contact.getGroups() == null){
                            contact.setGroups(new ArrayList<>());
                        }
                        cf.getAllContacts().add(contact);
                    }
                    cf.saveData();
                    gf.rebuildGroupMaps();
                    sf.updateContactSearch();
                    t.refreshCurrentView();
                    statusLabel.setText("成功导入 " + importedContacts.size() + " 个联系人");
                    statusLabel.setFont(textFont);
                } 
                else{
                    JOptionPane.showMessageDialog(null, "文件中没有找到联系人数据", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            } 
            catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "导入失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    //导出文件
    public void exportFile(ContactIOFacade.Format format){
        List<Contact> contactsToExport;
        int[] selectedRows=t.getContactTable().getSelectedRows();
        if (selectedRows.length > 0){
            contactsToExport=new ArrayList<>();
            for (int row : selectedRows) {
                int modelRow=t.getContactTable().convertRowIndexToModel(row);
                contactsToExport.add(t.getTableModel().getContactAt(modelRow));
            }
        } 
        else{
            contactsToExport=cf.getAllContacts();
        }
        if(contactsToExport.isEmpty()){
            JOptionPane.showMessageDialog(null, "没有联系人可导出", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser=new JFileChooser();
        String extension=format==ContactIOFacade.Format.CSV ? "csv" : "vcf";
        fileChooser.setSelectedFile(new File("contacts." + extension));
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            format==ContactIOFacade.Format.CSV ? "CSV文件 (*.csv)" : "vCard文件 (*.vcf)", extension));
        if (fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
            try{
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith("." + extension)) {
                    filePath+= "." + extension;
                }
                ioFacade.exportAll(contactsToExport, filePath, format);
                statusLabel.setText("成功导出 " + contactsToExport.size() + " 个联系人");
                statusLabel.setFont(textFont);
                JOptionPane.showMessageDialog(null, "成功导出 " + contactsToExport.size() + " 个联系人", "导出完成", JOptionPane.INFORMATION_MESSAGE);
            } 
        catch(Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "导出失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
