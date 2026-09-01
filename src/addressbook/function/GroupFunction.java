package addressbook.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import addressbook.model.Contact;
import addressbook.ui.Table;
import addressbook.ui.Tree;

public class GroupFunction {
    private Map<String,List<Contact>> gcMap=new HashMap<>();
    private JLabel statusLabel;
    private List<String> groups;
    private ContactFunction cf;
    private Table t;
    private Tree tree;
    private Font textFont=new Font("微软雅黑",Font.PLAIN,12);

    public Map<String,List<Contact>> getGroupContactsMap(){
        return gcMap;
    }
    public List<String> getGroups(){
        return groups;
    }

    public void setStatusLabel(JLabel statusLabel){ 
        this.statusLabel=statusLabel;
    }
    public void setGroups(List<String> groups){ 
        this.groups=groups; 
    }
    public void setContactFunction(ContactFunction cf){ 
        this.cf=cf; 
    }
    public void setTable(Table table){
        this.t=table;
    }
    public void setTree(Tree tree){ 
        this.tree=tree;
    }

    public void rebuildGroupMaps(){
        gcMap=new HashMap<>();
        if (groups==null){
            groups=new ArrayList<>();
        }
        while(groups.contains("全部联系人")){
            groups.remove("全部联系人");
        }
        groups.add(0,"全部联系人");
        for(String group:groups){
            gcMap.put(group, new ArrayList<>());
        }
        if(cf!=null&&cf.getAllContacts()!=null){
            for(Contact contact:cf.getAllContacts()){
            if(contact.getGroups()!=null && !contact.getGroups().isEmpty()){
                for(String group:contact.getGroups()){
                    List<Contact> list=gcMap.get(group);
                    if(list!=null){
                        list.add(contact);
                    }
                    else{
                        List<Contact> allList=gcMap.get("全部联系人");
                        if(allList!=null){
                            allList.add(contact);
                        }
                    }
                }
            }
            else{
                List<Contact> allList=gcMap.get("全部联系人");
                if(allList!=null){
                    allList.add(contact);
                }
            }
          }
        }   
    }

    //新增分组
    public void AddGroup(){
        String groupName=JOptionPane.showInputDialog
        (null,"请输入分组名称：");
        if(groupName==null){
            return;
        }
        if(groupName.trim().isEmpty()){
            JOptionPane.showMessageDialog
            (null,"分组名称不能为空","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        else{
            groupName=groupName.trim();
            if(!groups.contains(groupName)){
                groups.add(groupName);
                gcMap.put(groupName,new ArrayList<>());
                tree.updateGroupTree();
                cf.saveData();
                statusLabel.setText("已添加分组 "+groupName);
                statusLabel.setFont(textFont);
            }
            else{
                JOptionPane.showMessageDialog
                (null,"分组已存在","错误",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //从界面菜单删除分组
    public void DeleteGroupFromMenu(){
        TreePath path=tree.getGroupTree().getSelectionPath();
        if(path==null){
            JOptionPane.showMessageDialog
            (null,"请先选择一个分组","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
        String nodeName=(String) node.getUserObject();
        String groupName=nodeName.replaceAll("\\s*\\(\\d+\\)$","");
        if(!groupName.equals("全部联系人")){
            deleteGroup(groupName);
        }
    }

    //从右键菜单中删除分组
    public void deleteGroup(String groupName){
        int confirm=JOptionPane.showConfirmDialog
        (null,"确定要删除分组 \""+groupName+"\" 吗？\n分组中的联系人不会被删除，只会从该分组中移除",
        "确认删除",JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            for(Contact contact:cf.getAllContacts()){
                if(contact.getGroups()!=null){
                    contact.getGroups().remove(groupName);
                }
            }
            groups.remove(groupName);
            gcMap.remove(groupName);
            tree.updateGroupTree();
            tree.updateContactTreeCounts();
            cf.saveData();
            rebuildGroupMaps();
            t.refreshCurrentView();
            statusLabel.setText("已删除分组："+groupName);
            statusLabel.setFont(textFont);
        }
    }

    //从界面菜单重命名分组
    public void RenameGroupFromMenu(){
        TreePath path=tree.getGroupTree().getSelectionPath();
        if(path==null){
            JOptionPane.showMessageDialog
            (null,"请先选择一个分组","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
        String nodeName=(String) node.getUserObject();
        String groupName=nodeName.replaceAll("\\s*\\(\\d+\\)$","");
        if(!groupName.equals("全部联系人")){
            renameGroup(groupName,node);
        }
    }

    //重命名分组
    public void renameGroup(String oldName,DefaultMutableTreeNode node){
       String newName=JOptionPane.showInputDialog
       (null,"请输入新的分组名称：",oldName);
       if(newName==null){
            return;
        }
        if(newName.trim().isEmpty()){
            JOptionPane.showMessageDialog
            (null,"分组名称不能为空","提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
       else{
            newName=newName.trim();
            if(groups.contains(newName)){
                JOptionPane.showMessageDialog
                (null,"分组名称已存在","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            int i=groups.indexOf(oldName);
            groups.set(i,newName);
            for(Contact contact:cf.getAllContacts()){
                if(contact.getGroups()!=null && contact.getGroups().contains(oldName)){
                    contact.getGroups().remove(oldName);
                    contact.getGroups().add(newName);
                }
            }
            tree.updateGroupTree();
            cf.saveData();
            rebuildGroupMaps();
            t.refreshCurrentView();
            statusLabel.setText(oldName+"分组已重命名为："+newName);
            statusLabel.setFont(textFont);
        }
    }

    //移动到分组
    public void moveContactsToGroup(String targetGroup){
        int[] selectRows=t.getContactTable().getSelectedRows();
        if(selectRows.length==0) return;
        List<Contact> selectContacts=new ArrayList<>();
        for(int row:selectRows){
            int modelRow=t.getContactTable().convertRowIndexToModel(row);
            selectContacts.add(t.getTableModel().getContactAt(modelRow));
        }
        for (Contact contact:selectContacts){
            if(contact.getGroups()==null){
                contact.setGroups(new ArrayList<>());
            }
            contact.getGroups().clear();
            contact.getGroups().add(targetGroup);
        }
        cf.saveData();
        rebuildGroupMaps();
        t.refreshCurrentView();
        statusLabel.setText("已将 "+selectContacts.size()+" 个联系人移动到 "+targetGroup);
        statusLabel.setFont(textFont);
    }

    //添加到分组
    public void addContactsToGroup (String targetGroup){
        int[] selectRows=t.getContactTable().getSelectedRows();
        if (selectRows.length==0) return;
        List<Contact> selectedContacts=new ArrayList<>();
        for (int row:selectRows){
            int modelRow=t.getContactTable().convertRowIndexToModel(row);
            selectedContacts.add(t.getTableModel().getContactAt(modelRow));
        }
        for(Contact contact:selectedContacts){
            if(contact.getGroups()==null){
                contact.setGroups(new ArrayList<>());
            }
            if(!contact.getGroups().contains(targetGroup)){
                contact.getGroups().add(targetGroup);
            }
        }
        cf.saveData();
        rebuildGroupMaps();
        t.refreshCurrentView();
        statusLabel.setText("已将 "+selectedContacts.size()+" 个联系人添加到 "+targetGroup);
        statusLabel.setFont(textFont);
    }

    //从分组中移除
    public void removeContactsFromGroup(String groupName){
        int[] selectRows=t.getContactTable().getSelectedRows();
        if(selectRows.length==0) return;
        List<Contact> selectedContacts=new ArrayList<>();
        for(int row:selectRows){
            int modelRow=t.getContactTable().convertRowIndexToModel(row);
            selectedContacts.add(t.getTableModel().getContactAt(modelRow));
        }
        for(Contact contact:selectedContacts){
            if(contact.getGroups()!=null){
                contact.getGroups().remove(groupName);
            }
        }
        cf.saveData();
        rebuildGroupMaps();
        t.refreshCurrentView();
        statusLabel.setText("已将 "+selectedContacts.size()+" 个联系人从 "+ groupName +" 移除");
        statusLabel.setFont(textFont);
    }

    public String getCurrentSelectedGroup(){
        TreePath path=tree.getGroupTree().getSelectionPath();
        if(path!=null){
            DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
            if(node.getUserObject() instanceof String){
                String nodeName=(String) node.getUserObject();
                return nodeName.replaceAll("\\s*\\(\\d+\\)$","");
            }
        }
        return null;
    }
}
