package addressbook.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import addressbook.model.Contact;

public class ContactDialog extends JDialog{
    private boolean confirmed=false;
    private Contact contact;
    private List<String> groups;
    private JTextField name;
    private JTextField phone;
    private JTextField mobile;
    private JTextField imtool;
    private JTextField imaccount;
    private JTextField email;
    private JTextField personalHomepage;
    private JTextField birthday;
    private JTextField photo;
    private JTextField company;
    private JTextField homeAddress;
    private JTextField postalCode;
    private JTextArea remark;
    private JList<String> groupList;
    public ContactDialog(JFrame parent,Contact existingContact,List<String>groups){
        super(parent,existingContact==null?"新增联系人":"编辑联系人",true);
        this.groups=groups;
        this.contact=existingContact!=null?existingContact:new Contact();
        initUI();
        if(existingContact!=null){
            loadContactData();
        }
        setSize(500,450);
        setLocationRelativeTo(parent);
    }
    private void initUI(){
        setLayout(new BorderLayout());
        JPanel panel=new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(10, 10, 10, 10);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        int row=0;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("姓名"),gbc);
        gbc.gridx=1;
        name=new JTextField(15);
        panel.add(name,gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("电话"),gbc);
        gbc.gridx=1;
        phone=new JTextField(15);
        panel.add(phone,gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("手机"),gbc);
        gbc.gridx=1;
        mobile=new JTextField(15);
        panel.add(mobile,gbc);
        row++;
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("即时通信工具"), gbc);
        gbc.gridx=1;
        imtool=new JTextField(15);
        panel.add(imtool, gbc);
        row++;
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("即时通信工具账号"), gbc);
        gbc.gridx=1;
        imaccount=new JTextField(15);
        panel.add(imaccount, gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("电子邮箱"),gbc);
        gbc.gridx=1;
        email=new JTextField(15);
        panel.add(email,gbc);
        row++;
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("个人主页"), gbc);
        gbc.gridx=1;
        personalHomepage=new JTextField(15);
        panel.add(personalHomepage, gbc);
        row++;
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("<html>生日：<br/>（格式：yyyy-mm--dd）</html>"),gbc);
        gbc.gridx=1;
        birthday=new JTextField(15);
        panel.add(birthday, gbc);
        row++;
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("像片"), gbc);
        gbc.gridx=1;
        JPanel photoPanel = new JPanel(new BorderLayout(5, 0));
        photo = new JTextField(12);
        photo.setEditable(false);
        JButton selectPhotoBtn = new JButton("选择文件");
        selectPhotoBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "jpg", "jpeg", "png", "gif"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            photo.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        photoPanel.add(photo, BorderLayout.CENTER);
        photoPanel.add(selectPhotoBtn, BorderLayout.EAST);
        panel.add(photoPanel, gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("工作单位"),gbc);
        gbc.gridx=1;
        company=new JTextField(15);
        panel.add(company,gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("家庭地址"),gbc);
        gbc.gridx=1;
        homeAddress=new JTextField(15);
        panel.add(homeAddress,gbc);
        row++;
        gbc.gridx=0; gbc.gridy=row;
        panel.add(new JLabel("邮编："), gbc);
        gbc.gridx=1;
        postalCode=new JTextField(15);
        panel.add(postalCode, gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("<html>所属组:<br/>（Ctrl+点击多选<br/>Shift+范围选择）</html>"),gbc);
        gbc.gridx=1;
        groupList=new JList<>(groups.toArray(new String[0]));
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane groupScroll=new JScrollPane(groupList);
        groupScroll.setPreferredSize(new Dimension(200,80));
        panel.add(groupScroll,gbc);
        row++;
        gbc.gridx=0;
        gbc.gridy=row;
        panel.add(new JLabel("备注"),gbc);
        gbc.gridx=1;
        remark=new JTextArea(3,15);
        JScrollPane remarkScroll=new JScrollPane(remark);
        panel.add(remarkScroll,gbc);
        JScrollPane scrollPane=new JScrollPane(panel);
        add(scrollPane,BorderLayout.CENTER);
        JPanel btnPanel =new JPanel();
        JButton okBtn=new JButton("确定");
        okBtn.setBackground(new Color(150,200,250));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        okBtn.setOpaque(true);
        okBtn.setBorderPainted(false);                 
        okBtn.setFocusPainted(false);  
        okBtn.addActionListener(e->{
            if(saveContactData())
                dispose();
        });
        JButton cancelBtn=new JButton("取消");
        cancelBtn.setBackground(new Color(245,120,120));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);                 
        cancelBtn.setFocusPainted(false); 
        cancelBtn.addActionListener(e->dispose());
        btnPanel.add(okBtn);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(cancelBtn);
        add(btnPanel,BorderLayout.SOUTH);
    }

    private void loadContactData(){
        name.setText(contact.getName());
        phone.setText(contact.getPhone());
        mobile.setText(contact.getMobile());
        imtool.setText(contact.getImTool());
        imaccount.setText(contact.getImAccount());
        email.setText(contact.getEmail());
        personalHomepage.setText(contact.getPersonalHomepage());
        photo.setText(contact.getPhoto());
        company.setText(contact.getCompany());
        homeAddress.setText(contact.getAddress());
        postalCode.setText(contact.getPostalCode());
        remark.setText(contact.getComments());
        if(contact.getBirthday()!=null){
            java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
            birthday.setText(sdf.format(contact.getBirthday()));
        }
        if(contact.getGroups()!=null){
            ListSelectionModel selectionModel=groupList.getSelectionModel();
            for(int i=0;i<groups.size();i++){
                if(contact.getGroups().contains(groups.get(i))){
                    selectionModel.addSelectionInterval(i,i);
                }
            }
        }
    }

    private boolean saveContactData(){
        String nameValue=name.getText();
        if(nameValue==null || nameValue.trim().isEmpty()){
            JOptionPane.showMessageDialog
            (this, "姓名不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return false; 
        }
        contact.setName(name.getText());
        contact.setPhone(phone.getText());
        contact.setMobile(mobile.getText());
        contact.setImTool(imtool.getText());
        contact.setImAccount(imaccount.getText());
        contact.setEmail(email.getText());
        contact.setPersonalHomepage(personalHomepage.getText());
        contact.setPhoto(photo.getText());
        contact.setCompany(company.getText());
        contact.setAddress(homeAddress.getText());
        contact.setPostalCode(postalCode.getText());
        contact.setComments(remark.getText());
        String birthdayStr=birthday.getText();
        while(true){
            if(birthdayStr!=null && !birthdayStr.trim().isEmpty()){
                try{
                    java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    java.util.Date date=sdf.parse(birthdayStr);
                    contact.setBirthday(date);
                    break;
                }
                catch(java.text.ParseException e){
                    String newBirthdayStr=JOptionPane.showInputDialog
                    (this,"生日格式错误！请使用 yyyy-MM-dd 格式，例如：1990-01-01\n请重新输入生日：",birthdayStr);
                    if(newBirthdayStr==null){
                        break;
                    }
                    birthdayStr=newBirthdayStr.trim();
                }
            }
            else{
                break;
            }
        }
        List<String> selectedGroups=groupList.getSelectedValuesList();
        if(selectedGroups!=null && !selectedGroups.isEmpty()){
            contact.setGroups(new ArrayList<>(selectedGroups));
        }
        else{
            contact.setGroups(new ArrayList<>());
        }
        confirmed=true;
        return true;
    }

    public boolean isConfirmed(){
        return confirmed;
    }

    public Contact getContact(){
        return contact;
    }
}
