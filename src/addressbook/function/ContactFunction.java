package addressbook.function;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.util.List;

import addressbook.model.Contact;
import addressbook.service.ContactMerger;
import addressbook.service.DuplicateDetector;
import addressbook.service.DuplicateGroup;
import addressbook.ui.ContactDialog;
import addressbook.ui.Table;
import addressbook.ui.Tree;

public class ContactFunction {
    private List<Contact> allContacts=new ArrayList<>();
    private List<Contact> trashContacts=new ArrayList<>();
    private boolean showTrash = false;
    private JLabel statusLabel;
    private GroupFunction gf;
    private Table t;
    private Tree tree;
    private DuplicateDetector duplicateDetector=new DuplicateDetector();
    private SearchFunction sf;
    private ContactMerger contactMerger=new ContactMerger();
    private static final String DATA_FILE="addressbook.dat";
    private static final String TRASH_FILE = "trash.dat";

    private Font textFont=new Font("微软雅黑",Font.PLAIN,12);
    private Font menuFont=new Font("微软雅黑", Font.BOLD, 16);
    
    public List<Contact> getAllContacts(){
        return allContacts;
    }
    public List<Contact> getTrashContacts(){
        return trashContacts;
    }
    public boolean isShowTrash(){
        return showTrash;
    }

    public void setShowTrash(boolean showTrash){
        this.showTrash=showTrash; 
    }
    public void setStatusLabel(JLabel statusLabel){ 
        this.statusLabel=statusLabel;
    }
    public void setGroupFunction(GroupFunction gf){ 
        this.gf=gf; 
    }
    public void setTable(Table table){
        this.t=table;
    }
    public void setTree(Tree tree){ 
        this.tree=tree;
    }
    public void setsf(SearchFunction sf){
        this.sf=sf;
    }
    @SuppressWarnings("unchecked")
    public void loadData(){
        File file=new File(DATA_FILE);
        if(file.exists()){
            try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file))){
                allContacts=(List<Contact>) ois.readObject();
                gf.setGroups((List<String>) ois.readObject());
            } 
            catch(Exception e){
                e.printStackTrace();
                if(allContacts==null){
                    allContacts=new ArrayList<>();
                }
                if(gf.getGroups()==null){
                    gf.setGroups(new ArrayList<>());
                }
            }
        }
        else{
            if(allContacts==null){
                allContacts=new ArrayList<>();
            }
            if(gf.getGroups()==null){
                gf.setGroups(new ArrayList<>());
            }
        }
        File trashFile=new File(TRASH_FILE);
        if(trashFile.exists()){
            try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(trashFile))){
                trashContacts=(List<Contact>) ois.readObject();
            }
            catch(Exception e){
                e.printStackTrace();
                trashContacts=new ArrayList<>();
            }
        } 
        else{
            trashContacts=new ArrayList<>();
        }
    }
    
    public void saveData(){
        try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            oos.writeObject(allContacts);
            oos.writeObject(gf.getGroups());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(TRASH_FILE))){
            oos.writeObject(trashContacts);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void showTrash(){
        showTrash=true;
        t.getTableModel().setContacts(new ArrayList<>(trashContacts));
        statusLabel.setText("回收站 | 共 "+trashContacts.size()+" 个联系人");
        statusLabel.setFont(textFont);
        tree.getContactTree().clearSelection();
        tree.getGroupTree().clearSelection();
    }
     
    //新增联系人
    public void AddContact(Contact existContact){
        List<String> group=new ArrayList<>(gf.getGroups());
        group.remove("全部联系人");
        ContactDialog dialog=new ContactDialog(null,existContact,group);
        dialog.setVisible(true);
        if(dialog.isConfirmed()){
            Contact contact=dialog.getContact();
            if(contact.getName()!=null && !contact.getName().isEmpty()){
                contact.updatePinyin(contact.getName());
            }
            if(existContact==null){
                int maxId=0;
                for(Contact c:allContacts){
                    if(c.getId()!=null&&c.getId()>maxId){
                        maxId=c.getId();
                    }
                }
                contact.setId(maxId+1);
                allContacts.add(contact);
            }
            else{
                contact.setId(existContact.getId());
                int index=-1;
                for(int i=0;i<allContacts.size();i++){
                    if(allContacts.get(i).getId()==existContact.getId()){
                        index=i;
                        break;
                    }
                }
                if(index!=-1){
                    allContacts.set(index,contact);
                }
                else{
                    int idx=allContacts.indexOf(existContact);
                    if(idx!=-1){
                        allContacts.set(idx,contact);
                    }
                }
            }
            saveData();
            gf.rebuildGroupMaps();
            if(sf!=null){
                sf.updateContactSearch();
                sf.refreshSearchView();
            }
            t.refreshCurrentView();
            statusLabel.setText(existContact==null?"联系人已添加":"联系人已修改");
            statusLabel.setFont(textFont);
        }
    }

    //修改联系人
    public void EditContact(){
        int selectedRow=t.getContactTable().getSelectedRow();
        if(selectedRow==-1){
            JOptionPane.showMessageDialog
            (null,"请先选择一个联系人","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow=t.getContactTable().convertRowIndexToModel(selectedRow);
        Contact contact=t.getTableModel().getContactAt(modelRow);
        AddContact(contact);
    }

    //将联系人放入回收站
    public void DeleteContacts(){
        int[] selectedRow=t.getContactTable().getSelectedRows();
        if(selectedRow.length==0){
            JOptionPane.showMessageDialog
            (null,"请先选择一个联系人","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm=JOptionPane.showConfirmDialog
        (null,"确定要将选中的 "+selectedRow.length+" 个联系人移至回收站吗？",
        "确定",JOptionPane.YES_NO_CANCEL_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            List<Contact> toDelete=new ArrayList<>();
            for(int row:selectedRow){
                int modelRow=t.getContactTable().convertRowIndexToModel(row);
                toDelete.add(t.getTableModel().getContactAt(modelRow));
            }
            allContacts.removeAll(toDelete);
            trashContacts.addAll(toDelete);
            saveData();
            gf.rebuildGroupMaps();
            t.refreshCurrentView();
            statusLabel.setText("已将 "+toDelete.size()+" 个联系人移至回收站");
            statusLabel.setFont(textFont);
        }
    }

    //将联系人从回收站恢复
    public void RestoreContacts(){
        int[] selectedRows=t.getContactTable().getSelectedRows();
        if(!showTrash){
        JOptionPane.showMessageDialog
        (null,"请先进入回收站！","提示",JOptionPane.WARNING_MESSAGE);
        return;
        }
        if(selectedRows.length==0){
            JOptionPane.showMessageDialog
            (null,"请先选择一个联系人","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Contact> toRestore=new ArrayList<>();
        for(int row:selectedRows){
            int modelRow=t.getContactTable().convertRowIndexToModel(row);
            toRestore.add(t.getTableModel().getContactAt(modelRow));
        }
        for(Contact contact:toRestore){
            trashContacts.remove(contact);
            allContacts.add(contact);
        }
        saveData();
        gf.rebuildGroupMaps();
        t.refreshCurrentView();
        statusLabel.setText("已恢复 "+toRestore.size()+" 个联系人");
        statusLabel.setFont(textFont);
    }

    //彻底删除联系人
    public void DeletePermanently(){
        int[] selectedRows=t.getContactTable().getSelectedRows();
        if (selectedRows.length==0){
            JOptionPane.showMessageDialog
            (null,"请先选择一个联系人","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm=JOptionPane.showConfirmDialog
        (null,"确定要彻底删除选中的 "+selectedRows.length+" 个联系人吗？此操作不可恢复！","确定彻底删除",JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            List<Contact> toDelete=new ArrayList<>();
            for(int row:selectedRows){
                int modelRow=t.getContactTable().convertRowIndexToModel(row);
                toDelete.add(t.getTableModel().getContactAt(modelRow));
            }
            if(showTrash){
                trashContacts.removeAll(toDelete);
                showTrash();
            }
            else{
                allContacts.removeAll(toDelete);
            }
            saveData();
            gf.rebuildGroupMaps();
            t.refreshCurrentView();
            statusLabel.setText("已彻底删除 "+toDelete.size()+" 个联系人");
            statusLabel.setFont(textFont);
        }
    }

    //合并重复联系人
    public void findDuplicateContacts(){
        String[] strategies={"精确姓名匹配", "精确手机匹配", "模糊姓名匹配", "多字段综合匹配"};
        int choice=JOptionPane.showOptionDialog
        (null, 
            "请选择重复联系人检测方式：", 
            "查找重复联系人",
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            null, 
            strategies, 
            strategies[0]
        );
        if (choice<0)
            return;
        DuplicateDetector.Strategy strategy;
        switch (choice) {
            case 0: strategy=DuplicateDetector.Strategy.EXACT_NAME; break;
            case 1: strategy=DuplicateDetector.Strategy.EXACT_MOBILE; break;
            case 2: strategy=DuplicateDetector.Strategy.FUZZY_NAME; break;
            default: strategy=DuplicateDetector.Strategy.MULTI_FIELD; break;
        }
        List<DuplicateGroup> groups=duplicateDetector.find(getAllContacts(), strategy);
        if (groups.isEmpty()){
            JOptionPane.showMessageDialog
            (null, "未发现重复的联系人", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showDuplicateGroupsDialog(groups);
    }
    
    public void showDuplicateGroupsDialog(List<DuplicateGroup> groups) {
        JDialog dialog=new JDialog();
        dialog.setTitle("重复联系人合并");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(null);
        JPanel mainPanel=new JPanel(new BorderLayout());
        DefaultListModel<DuplicateGroup> listModel=new DefaultListModel<>();
        for (DuplicateGroup group : groups) {
            listModel.addElement(group);
        }
        JList<DuplicateGroup> groupList=new JList<>(listModel);
        groupList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                DuplicateGroup group=(DuplicateGroup) value;
                String displayText=group.getContacts().size() + "个重复: ";
                List<Contact> contacts=group.getContacts();
                for (int i = 0; i < Math.min(3, contacts.size()); i++) {
                    if (i > 0) displayText += ", ";
                    displayText += contacts.get(i).getName();
                }
                if (contacts.size() > 3) displayText += "...";
                return super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            }
        });
        JPanel buttonPanel=new JPanel();
        JButton mergeBtn=new JButton("合并选中的重复组");
        mergeBtn.setBackground(new Color(150, 200, 250));
        mergeBtn.setForeground(Color.WHITE);
        mergeBtn.setFont(menuFont);
        mergeBtn.setOpaque(true);
        mergeBtn.setBorderPainted(false);
        mergeBtn.setFocusPainted(false);
        mergeBtn.addActionListener(e->{
            DuplicateGroup selectedGroup=groupList.getSelectedValue();
            if(selectedGroup!=null){
                int mergedCount=selectedGroup.getContacts().size();
                mergeDuplicateGroup(selectedGroup);
                dialog.dispose();
                statusLabel.setText("已合并 " + mergedCount+ " 个重复联系人");
                statusLabel.setFont(textFont);
            } 
            else{
                JOptionPane.showMessageDialog(dialog, "请选择一个重复组", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JButton mergeAllBtn = new JButton("合并所有重复组");
        mergeAllBtn.setBackground(new Color(100, 220, 100));
        mergeAllBtn.setForeground(Color.WHITE);
        mergeAllBtn.setFont(menuFont);
        mergeAllBtn.setOpaque(true);
        mergeAllBtn.setBorderPainted(false);
        mergeAllBtn.setFocusPainted(false);
        mergeAllBtn.addActionListener(e -> {
            for (DuplicateGroup group : groups) {
                mergeDuplicateGroup(group);
            }
            dialog.dispose();
            t.refreshCurrentView();
            statusLabel.setText("已合并所有重复联系人");
            statusLabel.setFont(textFont);
            
        });
        buttonPanel.add(mergeBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(mergeAllBtn);
        mainPanel.add(new JScrollPane(groupList), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        JLabel infoLabel=new JLabel("找到 " + groups.size() + " 组重复联系人，请选择要合并的组", JLabel.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(infoLabel, BorderLayout.NORTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    public void mergeDuplicateGroup(DuplicateGroup group) {
        List<Contact> dups = group.getContacts();
        if (dups.size() <= 1) return;
        int bestIndex=contactMerger.selectBestPrimary(dups);
        Contact merged=contactMerger.merge(dups, bestIndex);
        Set<String> allGroups = new HashSet<>();
        for (Contact c : dups) {
            if (c.getGroups()!=null) {
                allGroups.addAll(c.getGroups());
            }
        }
        merged.setGroups(new ArrayList<>(allGroups));
        getAllContacts().removeAll(dups);
        getAllContacts().add(merged);
        saveData();
        gf.rebuildGroupMaps();
        sf.updateContactSearch();
        t.refreshCurrentView();   
    }
}
