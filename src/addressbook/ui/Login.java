package addressbook.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JFrame frame;
    private JTextField username;
    private JPasswordField password;
    private JLabel message;

    public Login(){
        frame=new JFrame("通讯录登录界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,400);
        frame.setLocationRelativeTo(null);
        
        JPanel mainPanel=new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0, 150, 180));
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(12, 12, 12, 12);

        JLabel title=new JLabel("用户登录");
        title.setFont(new Font("微软雅黑",Font.BOLD,28));
        title.setForeground(new Color(255, 255, 255));
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        mainPanel.add(title,gbc);

        JLabel user=new JLabel("账号：");
        user.setFont(new Font("微软雅黑",Font.PLAIN,16));
        user.setForeground(new Color(255, 255, 255));
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=1;
        mainPanel.add(user,gbc);
        username=new JTextField(15);
        username.setFont(new Font("微软雅黑",Font.PLAIN,16));
        gbc.gridx=1;
        gbc.gridy=1;
        mainPanel.add(username,gbc);

        JLabel pwd=new JLabel("密码：");
        pwd.setFont(new Font("微软雅黑",Font.PLAIN,16));
        pwd.setForeground(new Color(255, 255, 255));
        gbc.gridx=0;
        gbc.gridy=2;
        mainPanel.add(pwd,gbc);
        password=new JPasswordField(15);
        password.setFont(new Font("微软雅黑",Font.PLAIN,16));
        gbc.gridx=1;
        gbc.gridy=2;
        mainPanel.add(password,gbc);

        JPanel buttonPanel=new JPanel
        (new FlowLayout(FlowLayout.CENTER,20,0));
        buttonPanel.setOpaque(false);
        
        JButton lgButton=new JButton("登录");
        lgButton.setBackground(new Color(0,200,150));
        lgButton.setForeground(Color.WHITE);
        lgButton.setFont(new Font("微软雅黑",Font.BOLD,16));
        lgButton.setFocusPainted(false);
        lgButton.setPreferredSize(new Dimension(100,38));
        buttonPanel.add(lgButton);

        JButton clButton=new JButton("清空");
        clButton.setBackground(new Color(160,160,160)); 
        clButton.setForeground(Color.WHITE);
        clButton.setFont(new Font("微软雅黑",Font.BOLD,16));
        clButton.setFocusPainted(false);
        clButton.setPreferredSize(new Dimension(100,38));
        buttonPanel.add(clButton);

        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=2;
        mainPanel.add(buttonPanel,gbc);

        message=new JLabel("");
        message.setForeground(Color.RED);
        message.setFont(new Font("微软雅黑",Font.PLAIN,15));
        gbc.gridx=0;
        gbc.gridy=4;
        gbc.gridwidth=2;
        mainPanel.add(message,gbc);
        
        lgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputUsername=username.getText();
                String inputPassword=new String (password.getPassword());
                if(inputUsername.isEmpty()){
                    message.setText("请输入账号！");
                    username.requestFocus();
                }
                else if(inputPassword.isEmpty()){
                    message.setText("请输入密码！");
                    password.requestFocus(); 
                }
                else if (inputUsername.equals("2024252206") && 
                inputPassword.equals("123456")){
                    message.setText("");
                    frame.setVisible(false);
                    frame.dispose();
                    SwingUtilities.invokeLater(()->{
                        try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } 
                    catch (Exception a) {
                        a.printStackTrace();
                    }
                    new Chat().setVisible(true);
                    });
                }
                else{
                    message.setText("账号或密码错误，请重新输入！");
                    username.setText("");   
                    password.setText("");     
                    username.requestFocus();
                }
            }
        });

        username.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                lgButton.doClick();
            }
        });

        password.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                lgButton.doClick();
            }
        });

        clButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                username.setText("");
                password.setText("");
                message.setText("");
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
