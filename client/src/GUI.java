import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
 
public class GUI extends JFrame implements DropTargetListener{
    DropTarget target;
    JTextArea editor;
    private JLabel Image_label;
    private JPasswordField passwordField;
    private JTextField textField;
    private boolean Path_flag = false;
    public GUI(){
        super("Drag & Drop");
        setResizable(false);
        setTitle("Securoid - Photo Secure Guard");
        getContentPane().setBackground(new Color(154, 205, 50));
        
        // Style Setting
        try{
        		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch(Exception e){
        	System.out.println(e+"Error");
        }
        
        // Image Part - Label Setting
        Image_label = new JLabel("");
        Image_label.setIcon(new ImageIcon(GUI.class.getResource("Photo_base.jpg")));
                     
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360,320);
        setVisible(true);
        
        // TextArea (bottom Progress bar) setting
        editor = new JTextArea();
        editor.setText("Securoid Now Ready");
        editor.setForeground(new Color(154, 205, 50));
        editor.setFont(new Font("맑은 고딕", Font.BOLD, 10));
        editor.setBackground(Color.WHITE);
        
        target = new DropTarget(Image_label,DnDConstants.ACTION_COPY_OR_MOVE,
                (DropTargetListener) this,true,null);
        
        // password, id Field setting
        
        // Border Style
        Font input_font = new Font("Consol", Font.BOLD,12);
        TitledBorder input_border = new TitledBorder(new LineBorder(Color.white),"ID");
        input_border.setTitleColor(Color.white);
        input_border.setTitleFont(input_font);        
        TitledBorder password_border = new TitledBorder(new LineBorder(Color.white),"Password");
        password_border.setTitleColor(Color.white);
        password_border.setTitleFont(input_font);        

        // passwordField
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(154, 205, 50));
        passwordField.setBorder(password_border);

        // ID_Field
        textField = new JTextField();
        textField.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(154, 205, 50));
        textField.setBorder(input_border);
        textField.setColumns(10);
        
        // Button Field
        JButton btnNewButton = new JButton("");
        btnNewButton.setBackground(new Color(154, 205, 50));
        btnNewButton.setIcon(new ImageIcon(GUI.class.getResource("key.jpg")));
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(!Path_flag){
    				JOptionPane.showMessageDialog(null, "Drop your Photo first");
        		}
        		else{
					if (textField.getText().length() > 0 && passwordField.getPassword().length > 0) {
						// Click Event (Main event write here)
						System.out.println("Clicked");
						Client_App CA = new Client_App();
						try {
							CA.Execute(textField.getText(), new String(
									passwordField.getPassword()), editor
									.getText());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "ID, Password can not be blank");
					}
        		}
			}
        });
        
        // Layout setting (Done by WindowsBuilder)
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addGap(10)
        			.addComponent(Image_label)
        			.addGap(12))
        		.addComponent(editor, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addGap(13)
        			.addComponent(textField, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
        			.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addGap(10)
        			.addComponent(Image_label, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addGap(15)
        					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))
        				.addGroup(groupLayout.createSequentialGroup()
        					.addGap(18)
        					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))
        			.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
        			.addComponent(editor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        getContentPane().setLayout(groupLayout);
    }
        
    /* DropTargetListener */
    public void dragEnter(DropTargetDragEvent dtde){
        //System.out.println("dragEnter");
    }
    public void dragExit(DropTargetEvent dtde){
        //System.out.println("dragExit");
    }
    public void dragOver(DropTargetDragEvent dtde){
        //System.out.println("dragOver");
    }
    public void drop(DropTargetDropEvent dtde){
        System.out.println("drop");
        if((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE)!=0){
            dtde.acceptDrop(dtde.getDropAction());
            Transferable tr = dtde.getTransferable();
            try{
                //전달되는 파일을 리스트형태로 변환
                //파일리스트의 DataFlavor를 이용하여 tr에 저장
                java.util.List list = (java.util.List)
                tr.getTransferData(DataFlavor.javaFileListFlavor);
                //리스트의 첫번째 원소를 파일로 읽어들인다.
                File file = (File)list.get(0);
                
                ImageIcon Image = new ImageIcon(GUI.class.getResource("Photo_droped.jpg"));
                Image_label.setIcon(Image);
                
                editor.setText(file.getPath());		// ImagePath. Decryption reference
                Path_flag = true;
                                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde){
        System.out.println("dropActionChanged");
    }
    
    public static void main(String[] args) {
        new GUI();
    }
}