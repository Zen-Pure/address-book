package addressbook.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import addressbook.function.ContactFunction;
import addressbook.function.GroupFunction;
import addressbook.model.Contact;
import addressbook.model.ContactTableModel;

public class Table {
    private JTable contactTable;
    private ContactTableModel tableModel;
    private JLabel statusLabel;
    private ContactFunction cf;
    private GroupFunction gf;
    private Tree tree;
    private Font textFont=new Font("微软雅黑",Font.PLAIN,12);

    public JTable getContactTable(){
        return contactTable;
    }
    public ContactTableModel getTableModel(){
        return tableModel;
    }
    public void setStatusLabel(JLabel statusLabel)
    { 
        this.statusLabel=statusLabel; 
    }
    public void setReferences(ContactFunction cf, GroupFunction gf, Tree tree) {
        this.cf = cf;
        this.gf = gf;
        this.tree = tree;
    }

    public void createContactTable(){
        tableModel=new ContactTableModel();
        contactTable=new JTable(tableModel);
        contactTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        contactTable.setRowHeight(35);
        contactTable.setGridColor(new Color(240, 240,240));
        contactTable.setShowGrid(true);
        contactTable.setIntercellSpacing(new Dimension(1, 1));
        JTableHeader header=contactTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        contactTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        contactTable.getTableHeader().setReorderingAllowed(false);
        contactTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if(e.isPopupTrigger()){
                    showContactPopupMenu(e.getX(),e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()){
                    showContactPopupMenu(e.getX(),e.getY());
                }
            }
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    cf.EditContact();
                }
            }
        });
    }

    public void showUngroupedContacts(){
        cf.setShowTrash(false);
        List<Contact> ungroup=new ArrayList<>();
        for(Contact contact:cf.getAllContacts()){
            if(contact.getGroups()==null||contact.getGroups().isEmpty()){
                ungroup.add(contact);
            }
        }
        tableModel.setContacts(ungroup);
        statusLabel.setText("未分组联系人 | 共"+ungroup.size()+" 个联系人");
        statusLabel.setFont(textFont);
    }

    public void showContactPopupMenu(int x,int y){
        int row=contactTable.rowAtPoint(new Point(x,y));
        if(row==-1) return;
        if(contactTable.getSelectedRowCount()<=1){
            contactTable.setRowSelectionInterval(row,row);
        }
        JPopupMenu popup=new JPopupMenu();
        JMenuItem editItem=new JMenuItem("修改联系人");
        editItem.addActionListener(e->cf.EditContact());
        JMenuItem restoreItem=null;
        if(cf.isShowTrash()){
            restoreItem=new JMenuItem("恢复联系人");
            restoreItem.addActionListener(e->cf.RestoreContacts());
        }
        JMenuItem deleteItem=new JMenuItem(cf.isShowTrash()?"彻底删除":"将联系人放入回收站");
        deleteItem.addActionListener(e->{
            if(cf.isShowTrash()) cf.DeletePermanently();
            else cf.DeleteContacts();
        });
        JMenu moveMenu=null;
        JMenu addToGroupMenu=null;
        JMenu removeFromGroupMenu=null;
        if(!cf.isShowTrash()){
            moveMenu=new JMenu("移动到分组");
            for(String group:gf.getGroups()){
                if(!group.equals("全部联系人")){
                    JMenuItem groupItem=new JMenuItem(group);
                    groupItem.addActionListener(e->gf.moveContactsToGroup(group));
                    moveMenu.add(groupItem);
                }
            }
            addToGroupMenu=new JMenu("添加到分组");
            for(String group:gf.getGroups()){
                if(!group.equals("全部联系人")){
                    JMenuItem groupItem=new JMenuItem(group);
                    groupItem.addActionListener(e->gf.addContactsToGroup(group));
                    addToGroupMenu.add(groupItem);
                }
            }
            removeFromGroupMenu=new JMenu("从分组中移除");
            String currentGroup=gf.getCurrentSelectedGroup();
            if(currentGroup!=null&&!currentGroup.equals("全部联系人")){
                JMenuItem removeItem=new JMenuItem("从当前分组中移除");
                removeItem.addActionListener(e->gf.removeContactsFromGroup(currentGroup));
                removeFromGroupMenu.add(removeItem);
            }
        }
        popup.add(editItem);
        if(restoreItem!=null){
            popup.add(restoreItem);
        }
        popup.add(deleteItem);
        if(moveMenu!=null&&moveMenu.getItemCount()>0){
            popup.addSeparator();
            popup.add(moveMenu);
        }
        if(addToGroupMenu!=null&&addToGroupMenu.getItemCount()>0){
            popup.add(addToGroupMenu);
        }
        if(removeFromGroupMenu!=null&&removeFromGroupMenu.getItemCount()>0){
            popup.add(removeFromGroupMenu);
        }
        popup.show(contactTable,x,y);
    }
    
    public void selectGroup(String groupName){
        cf.setShowTrash(false);
        gf.rebuildGroupMaps();
        List<Contact> contacts;
        if(groupName.equals("全部联系人")){
            contacts=new ArrayList<>(cf.getAllContacts());
        }
        else{
            contacts=gf.getGroupContactsMap().get(groupName);
            if(contacts==null){
                contacts=new ArrayList<>();
            }
        }
        tableModel.setContacts(contacts);
        statusLabel.setText(groupName+" | 共 "+contacts.size()+" 个联系人");
        statusLabel.setFont(textFont);
    }
   
    public void refreshCurrentView(){
        tree.updateContactTreeCounts();
        tree.updateGroupTree();
        if(!cf.isShowTrash()){
            TreePath path=tree.getGroupTree().getSelectionPath();
            if(path!=null){
                DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
                String nodeName=node.getUserObject().toString();
                if(nodeName.equals("所有联系人") || nodeName.startsWith("所有联系人")){
                    selectGroup("全部联系人");
                    return;
                }
                else if(nodeName.equals("未分组联系人") || nodeName.startsWith("未分组联系人")){
                    showUngroupedContacts();
                    return;
                }
            }
            TreePath groupPath=tree.getGroupTree().getSelectionPath();
            if(groupPath!=null){
                DefaultMutableTreeNode node=(DefaultMutableTreeNode) groupPath.getLastPathComponent();
                if(node.getUserObject() instanceof String){
                    String nodeName=(String) node.getUserObject();
                    if(!nodeName.equals("联系组")){
                        String groupName=nodeName.replaceAll("\\s*\\(\\d+\\)$","");
                        selectGroup(groupName);
                    }
                }
            }
            else{
                selectGroup("全部联系人");
            }
        }
        else{
            cf.showTrash();
        }
    }
}
