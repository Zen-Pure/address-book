package addressbook.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

import addressbook.function.ContactFunction;
import addressbook.function.GroupFunction;
import addressbook.model.Contact;

public class Tree {
    private JTree contactTree;
    private JTree groupTree;
    private DefaultTreeModel contactTreeModel;
    private DefaultTreeModel groupTreeModel;
    private ContactFunction cf;
    private GroupFunction gf;
    private Table t;

    private Font menuFont=new Font("微软雅黑",Font.BOLD,16);
    private Font textFont=new Font("微软雅黑",Font.PLAIN,12);

    public JTree getContactTree(){
        return contactTree;
    }
    public JTree getGroupTree(){
        return groupTree;
    }
    public void setReferences(ContactFunction cf, GroupFunction gf, Table table) {
        this.cf = cf;
        this.gf = gf;
        this.t = table;
    }
    
    public JPanel createTreeTitlePanel(String title,Color color){
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        JLabel titleLabel=new JLabel(title);
        titleLabel.setFont(menuFont);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel,BorderLayout.WEST);
        return panel;
    }

    public JPanel createLeftTreePanel(){
        JPanel panel=new JPanel(new BorderLayout());
        JPanel contactTreePanel=createContactTreePanel();
        JPanel groupTreePanel=createGroupTreePanel();
        JSplitPane splitPane=new JSplitPane
        (JSplitPane.VERTICAL_SPLIT,contactTreePanel,groupTreePanel);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane,BorderLayout.CENTER);
        return panel;
    }

    public JPanel createContactTreePanel(){
        JPanel panel=new JPanel (new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel titlePanel=createTreeTitlePanel
        ("联系人",new Color(60,100,225));
        panel.add(titlePanel,BorderLayout.NORTH);
        DefaultMutableTreeNode contactRoot=new DefaultMutableTreeNode("联系人");
        DefaultMutableTreeNode allContactsNode=new DefaultMutableTreeNode("所有联系人");
        DefaultMutableTreeNode ungroupedNode=new DefaultMutableTreeNode("未分组联系人");
        contactRoot.add(allContactsNode);
        contactRoot.add(ungroupedNode);
        contactTreeModel=new DefaultTreeModel(contactRoot);
        contactTree=new JTree(contactTreeModel);
        customizeTree(contactTree);
        contactTree.getSelectionModel().addTreeSelectionListener(e->{
            cf.setShowTrash(false);
            groupTree.clearSelection();
            DefaultMutableTreeNode node=
            (DefaultMutableTreeNode) contactTree.getLastSelectedPathComponent();
            if(node!=null&&node.equals(contactRoot)){
                contactTree.clearSelection();
                return;
            }
            if(node!=null&&node.getUserObject() instanceof String){
                String nodeName=(String) node.getUserObject();
                if(nodeName.equals("所有联系人")||nodeName.startsWith("所有联系人")){
                    t.selectGroup("全部联系人");
                }
                else if (nodeName.equals("未分组联系人")||nodeName.startsWith("未分组联系人")){
                    t.showUngroupedContacts();
                }
            }
        });
        JScrollPane scrollPane=new JScrollPane(contactTree);
        panel.add(scrollPane,BorderLayout.CENTER);
        updateContactTreeCounts();
        return panel;
    }

    public JPanel createGroupTreePanel(){
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel titlePanel=createTreeTitlePanel("联系组",new Color(46,140,87));
        panel.add(titlePanel,BorderLayout.NORTH);
        DefaultMutableTreeNode groupRoot=new DefaultMutableTreeNode("联系组");
        groupTreeModel=new DefaultTreeModel(groupRoot);
        groupTree=new JTree(groupTreeModel);
        customizeTree(groupTree);
        groupTree.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if(e.isPopupTrigger()){
                    showGroupPopupMenu(e.getX(),e.getY());
                    return;
                }
                if(SwingUtilities.isLeftMouseButton(e)){
                    TreePath path=groupTree.getPathForLocation(e.getX(),e.getY());
                    if(path==null) {return;}
                    DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
                    if(contactTree!=null){
                        contactTree.clearSelection();
                    }
                    groupTree.clearSelection();
                    groupTree.setSelectionPath(path);
                    if(node!=null&&node.equals(groupRoot)){
                        groupTree.clearSelection();
                        return;
                    }
                    if(node!=null&&node.getUserObject() instanceof String){
                        String nodeName=(String) node.getUserObject();
                        if(!nodeName.equals("联系组")){
                            cf.setShowTrash(false);
                            String groupName=nodeName.replaceAll("\\s*\\(\\d+\\)$","");
                            t.selectGroup(groupName);
                        }
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()){
                    showGroupPopupMenu(e.getX(),e.getY());
                }
            }
        });
        JScrollPane scrollPane=new JScrollPane(groupTree);
        panel.add(scrollPane,BorderLayout.CENTER);
        updateGroupTree();
        return panel;
    }

    public void customizeTree(JTree tree){
        tree.setRowHeight(28);
        tree.setFont(textFont);
        tree.setBackground(Color.WHITE);
        tree.putClientProperty("Jtree.lineStyle","None");
        for(int i=0;i<tree.getRowCount();i++){
            tree.expandRow(i);
        }
    }

    public void updateContactTreeCounts(){
        if(contactTreeModel==null){return;}
        DefaultMutableTreeNode root=(DefaultMutableTreeNode) contactTreeModel.getRoot();
        if(root.getChildCount()>=2){
            DefaultMutableTreeNode allNode=(DefaultMutableTreeNode) root.getChildAt(0);
            DefaultMutableTreeNode ungroupNode=(DefaultMutableTreeNode) root.getChildAt(1);
            int totalCount=cf.getAllContacts().size();
            int ungroupCount=0;
            for(Contact contact:cf.getAllContacts()){
                if(contact.getGroups()==null||contact.getGroups().isEmpty()){
                    ungroupCount++;
                }
            }
            allNode.setUserObject("所有联系人 ("+totalCount+")");
            ungroupNode.setUserObject("未分组联系人 ("+ungroupCount+")");
            contactTreeModel.nodeChanged(allNode);
            contactTreeModel.nodeChanged(ungroupNode);
        }
    }

    public void updateGroupTree(){
        if(groupTreeModel==null){
            return;
        }
        gf.rebuildGroupMaps();
        DefaultMutableTreeNode root=(DefaultMutableTreeNode)groupTreeModel.getRoot();
        root.removeAllChildren();
        for(String group:gf.getGroups()){
            if(!group.equals("全部联系人")){
                int count=gf.getGroupContactsMap().containsKey(group)?gf.getGroupContactsMap().get(group).size():0;
                root.add(new DefaultMutableTreeNode(group+"("+count+")"));
            }
        }
        groupTreeModel.reload(root);
        for(int i=0;i<groupTree.getRowCount();i++){
           groupTree.expandRow(i);
        }
    } 

    public void showGroupPopupMenu(int x,int y){
        TreePath path=groupTree.getPathForLocation(x,y);
        if(path==null){return;}
        DefaultMutableTreeNode node=(DefaultMutableTreeNode)path.getLastPathComponent();
        String nodeName=node.getUserObject().toString();
        if (nodeName.equals("联系组")){return;}
        String groupName=nodeName.replaceAll("\\s*\\(\\d+\\)$", "");
        JPopupMenu popup=new JPopupMenu();
        JMenuItem addItem=new JMenuItem("新增分组");
        addItem.addActionListener(e->gf.AddGroup());
        JMenuItem deleteItem=new JMenuItem("删除分组");
        deleteItem.addActionListener(e->gf.deleteGroup(groupName));
        JMenuItem renameItem=new JMenuItem("重命名分组");
        renameItem.addActionListener(e->gf.renameGroup(groupName,node));
        popup.add(addItem);
        popup.add(deleteItem);
        popup.add(renameItem);
        popup.show(groupTree,x,y);
    }
}
