package addressbook.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import addressbook.function.*;
import addressbook.service.ContactIOFacade;

public class Chat extends JFrame{
    private ContactFunction cf;
    private GroupFunction gf;
    private SearchFunction sf;
    private FileFunction ff;
    private Tree tree;
    private Table table;
    private JLabel statusLabel;
    private Font menuFont=new Font("微软雅黑",Font.BOLD,16);
    
    private List<String> displayFields;
    private String[] availableFields = {"姓名", "电话", "手机", "即时通信工具","即时通信号码", "电子邮箱", 
    "个人主页", "生日", "像片", "工作单位", "家庭地址", "邮编", "所属组", "备注"};

    public Chat(){
        setTitle("通讯录管理程序");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200,800);
        setLocationRelativeTo(null);

        cf=new ContactFunction();
        gf=new GroupFunction();
        tree=new Tree();
        table=new Table();
        sf=new SearchFunction();
        sf.setContactFunction(cf);
        sf.setTable(table);
        sf.setSearch(new JTextField(30));
        ff=new FileFunction();
        ff.setTable(table);
        ff.setContactFunction(cf);
        ff.setGroupFunction(gf);
        ff.setSearchFunction(sf);
        cf.setGroupFunction(gf);
        cf.setTable(table);
        cf.setTree(tree);
        cf.setsf(sf);
        cf.loadData();
        if(gf.getGroups()==null||gf.getGroups().isEmpty()){
            List<String> initGroups=new ArrayList<>();
            initGroups.add("朋友");
            initGroups.add("家人");
            initGroups.add("同事");
            gf.setGroups(initGroups);
        }
        gf.setContactFunction(cf);
        gf.setTable(table);
        gf.setTree(tree);
        gf.rebuildGroupMaps();
        sf.updateContactSearch();
        tree.setReferences(cf, gf, table);
        table.setReferences(cf, gf, tree);
        initDisplayFields();
        initUI();
        cf.setStatusLabel(statusLabel);
        gf.setStatusLabel(statusLabel);
        table.setStatusLabel(statusLabel);
        sf.setStatusLabel(statusLabel);
        ff.setStatusLabel(statusLabel);
        table.refreshCurrentView();
    }

    public void initUI(){
        setLayout (new BorderLayout());
        createBar();

        JSplitPane mainSP=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSP.setDividerLocation(180);
        JPanel leftPanel=tree.createLeftTreePanel();
        JScrollPane leftScroll = new JScrollPane(leftPanel);
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        leftScroll.getViewport().setBackground(new Color(245, 245, 245));
        mainSP.setLeftComponent(leftScroll);
        table.createContactTable();
        table.getTableModel().setDisplayFields(displayFields);
        JScrollPane rightScroll=new JScrollPane(table.getContactTable());
        rightScroll.setBorder(BorderFactory.createTitledBorder("联系人列表"));
        mainSP.setRightComponent(rightScroll);
        add(mainSP,BorderLayout.CENTER);
        createStatusBar();
        tree.updateContactTreeCounts();
        tree.updateGroupTree();
    }

    public void initDisplayFields() {
        displayFields = new ArrayList<>();
        displayFields.add("姓名");
        displayFields.add("电话");
    }

    public void createBar(){
        JMenuBar menuBar=new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setFont(menuFont);

        JMenu ifileMenu=new JMenu("导入文件");
        ifileMenu.setBackground(new Color(100,200,250));
        ifileMenu.setForeground(Color.WHITE);
        ifileMenu.setFont(menuFont);
        ifileMenu.setOpaque(true); 
        JMenuItem iCSV=new JMenuItem("导入CSV文件");
        iCSV.addActionListener(e->ff.importFile(ContactIOFacade.Format.CSV));
        JMenuItem iVCard=new JMenuItem("导入vCard文件");
        iVCard.addActionListener(e->ff.importFile(ContactIOFacade.Format.VCARD));
        ifileMenu.add(iCSV);
        ifileMenu.add(iVCard);

        JMenu efileMenu=new JMenu("导出文件");
        efileMenu.setBackground(new Color(100,220,200));
        efileMenu.setForeground(Color.WHITE);
        efileMenu.setFont(menuFont);
        efileMenu.setOpaque(true); 
        JMenuItem eCSV=new JMenuItem("导出为CSV");
        eCSV.addActionListener(e->ff.exportFile(ContactIOFacade.Format.CSV));      
        JMenuItem eVCard=new JMenuItem("导出为vCard");
        eVCard.addActionListener(e->ff.exportFile(ContactIOFacade.Format.VCARD));
        efileMenu.add(eCSV);
        efileMenu.add(eVCard);

        JMenu cedit=new JMenu("编辑联系人");
        cedit.setBackground(new Color(255,205,120));
        cedit.setForeground(Color.WHITE);
        cedit.setFont(menuFont);
        cedit.setOpaque(true); 
        JMenuItem addContact=new JMenuItem("新增联系人");
        addContact.addActionListener(e->cf.AddContact(null));
        JMenuItem editContact=new JMenuItem("修改联系人");
        editContact.addActionListener(e->cf.EditContact());
        JMenuItem deleteContact=new JMenuItem("将联系人放入回收站");
        deleteContact.addActionListener(e->cf.DeleteContacts());
        JMenuItem recoverContact=new JMenuItem("将联系人从回收站恢复");
        recoverContact.addActionListener(e->cf.RestoreContacts());
        JMenuItem deleteContact1=new JMenuItem("彻底删除联系人");
        deleteContact1.addActionListener(e->cf.DeletePermanently());
         JMenuItem mergeDuplicate = new JMenuItem("合并重复联系人");
        mergeDuplicate.addActionListener(e->cf.findDuplicateContacts());
        cedit.add(addContact);
        cedit.add(editContact);
        cedit.add(deleteContact);
        cedit.add(recoverContact);
        cedit.add(deleteContact1);
        cedit.add(mergeDuplicate);
        
        JMenu gedit=new JMenu("编辑联系组");
        gedit.setBackground(new Color(190,165,220));
        gedit.setForeground(Color.WHITE);
        gedit.setFont(menuFont);
        gedit.setOpaque(true); 
        JMenuItem addGroup=new JMenuItem("新增分组");
        addGroup.addActionListener(e->gf.AddGroup());
        JMenuItem deleteGroup=new JMenuItem("删除分组");
        deleteGroup.addActionListener(e->gf.DeleteGroupFromMenu());
        JMenuItem renameGroup=new JMenuItem("重命名分组");
        renameGroup.addActionListener(e->gf.RenameGroupFromMenu());
        gedit.add(addGroup);
        gedit.add(deleteGroup);
        gedit.add(renameGroup);

        JMenu displayLabel=new JMenu("显示字段");
        displayLabel.setBackground(new Color(255, 187, 198));
        displayLabel.setForeground(Color.WHITE);
        displayLabel.setFont(menuFont);
        displayLabel.setOpaque(true);
        JMenuItem NandP=new JMenuItem("姓名+电话");
        NandP.addActionListener(e->{
            displayFields.clear();
            displayFields.add("姓名");
            displayFields.add("电话");
            table.getTableModel().updateColumns();
        });
        JMenuItem NandE=new JMenuItem("姓名+电子邮箱");
        NandE.addActionListener(e->{
            displayFields.clear();
            displayFields.add("姓名");
            displayFields.add("电子邮箱");
            table.getTableModel().updateColumns();
        });
        JMenuItem customize=new JMenuItem("自定义");
        customize.addActionListener(e->showCustomizeDialog());
        displayLabel.add(NandP);
        displayLabel.add(NandE);
        displayLabel.add(customize);

        JButton searchLabel=new JButton("搜索");
        searchLabel.setBackground(new Color(155, 210, 100));
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(menuFont);
        searchLabel.setOpaque(true);
        searchLabel.setBorderPainted(false);                 
        searchLabel.setFocusPainted(false);  
        searchLabel.addActionListener(e->sf.searchContacts());
        searchLabel.setMaximumSize(new Dimension(60, 30));
        searchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        searchLabel.setVerticalAlignment(SwingConstants.CENTER);
        sf.setSearch(new JTextField(30));
        sf.getSearch().setMaximumSize(new Dimension(400, 30));
        sf.getSearch().setFont(new Font("微软雅黑", Font.PLAIN, 14));
        sf.getSearch().addActionListener(e->sf.searchContacts());
        
        JButton trashBtn=new JButton("回收站"){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2d=(Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                super.paintComponent(g);
            }
        };
        trashBtn.setBackground(new Color(160, 160, 160));
        trashBtn.setForeground(Color.WHITE);
        trashBtn.setFont(menuFont);
        trashBtn.setOpaque(true);
        trashBtn.setBorderPainted(false);                 
        trashBtn.setFocusPainted(false);  
        trashBtn.setContentAreaFilled(false);
        trashBtn.addActionListener(e->cf.showTrash());

        JButton exitBtn=new JButton("退出"){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2d=(Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                super.paintComponent(g);
            }
        };
        exitBtn.setBackground(new Color(250, 100, 100));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(menuFont);
        exitBtn.setOpaque(true);
        exitBtn.setBorderPainted(false);                 
        exitBtn.setFocusPainted(false);  
        exitBtn.setContentAreaFilled(false);
        exitBtn.addActionListener(e->System.exit(0));

        menuBar.add(Box.createHorizontalStrut(25));
        menuBar.add(ifileMenu);
        menuBar.add(Box.createHorizontalStrut(15));
        menuBar.add(efileMenu);
        menuBar.add(Box.createHorizontalStrut(15));
        menuBar.add(cedit);
        menuBar.add(Box.createHorizontalStrut(15));
        menuBar.add(gedit);
        menuBar.add(Box.createHorizontalStrut(15));
        menuBar.add(displayLabel);
        menuBar.add(Box.createHorizontalStrut(25));
        menuBar.add(sf.getSearch());
        menuBar.add(searchLabel);
        menuBar.add(Box.createHorizontalStrut(25));
        menuBar.add(trashBtn);
        menuBar.add(Box.createHorizontalStrut(15));
        menuBar.add(exitBtn);
        menuBar.add(Box.createHorizontalStrut(25));
        setJMenuBar(menuBar);
    }

    private void showCustomizeDialog(){
        JDialog dialog=new JDialog(this,"自定义显示字段",true);
        dialog.setLayout(new BorderLayout());
        JPanel panel=new JPanel(new GridLayout(0,2,10,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        java.util.List<JCheckBox> checkBoxes=new ArrayList<>();
        for(String field:availableFields){
            JCheckBox cb=new JCheckBox(field);
            cb.setSelected(displayFields.contains(field));
            cb.setFocusPainted(false);
            checkBoxes.add(cb);
            panel.add(cb);
        }
        JScrollPane scrollPane=new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400,500));
        dialog.add(scrollPane,BorderLayout.CENTER);
        JPanel btnPanel =new JPanel();
        JButton okBtn=new JButton("确定");
        okBtn.setBackground(new Color(150,200,250));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFont(menuFont);
        okBtn.setOpaque(true);
        okBtn.setBorderPainted(false);                 
        okBtn.setFocusPainted(false);  
        okBtn.addActionListener(e->{
            displayFields.clear();
            for(JCheckBox cb:checkBoxes){
                if(cb.isSelected()){
                    displayFields.add(cb.getText());
                }
            }
            table.getTableModel().updateColumns();
            dialog.dispose();
        });
        JButton cancelBtn=new JButton("取消");
        cancelBtn.setBackground(new Color(245,120,120));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(menuFont);
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);                 
        cancelBtn.setFocusPainted(false); 
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(okBtn);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel,BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void createStatusBar(){
        statusLabel=new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setOpaque(true);
        add(statusLabel,BorderLayout.SOUTH);
    }
}
