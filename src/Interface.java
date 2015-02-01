/**
 * Program:     HR System
 * By:          Mengbo Kou, Milind Bhambhani, Spasimir Vasilev
 * Date:        January 5, 2014
 * Purpose: 	This program simulates a human resource system. It allows the user to add/remove employees from the company
 *              directory, sort the employees by a variety of factors, search employees, modify the employee data, and save/load their work.
 */

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import java.awt.CardLayout;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class Interface extends JFrame implements ActionListener {

	private static JPanel contentPane;
	private static JPanel cards;
	
	private static JTextField searchField;
	private static JList<String> list;
	
	private static JTextField[] stringFields = new JTextField[7];  //first name at index 0, last name at index 1, address at index 2, department at index 3, job title at index 4, status at index 5, email at index 6
	private static JFormattedTextField[] numFields = new JFormattedTextField[6]; //age at index 0, salary at index 1, SIN at index 2, and phone number parts at indices 3-5
	private static JSpinner spinner;
	
	private static JLabel employeeNum, lblFName, lblLName, lblGender, lblAge, lblAddress, lblSIN, lblDepartment, lblStatus, lblPhoneNum, lblEmail, lblJobTitle, lblEmployeeNum, lblSalary;
	private static JButton btnHire, btnFire, btnView, btnSort;
	
	static ArrayList<Employee> employees = new ArrayList<Employee>();
	static String password = "Password";
	static int employeeCount;
	
	boolean hiring = false; //boolean that is true when the user is in the middle of hiring an employee
	
	

	/**Launch the application*/
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			        loadFromFile(); //if a record file exists, loads global values
			        checkPassword(); //get user to enter in a password
			        
			        Interface frame = new Interface();
					frame.setVisible(true);
			        
			        if (employees.size() != 0) { //if employees were added to the employee ArrayList, update list of employees
			        	updateList();
			        	list.setSelectedIndex(0);
			        }
			        else { //no employees are in the employee ArrayList
			        	enableOptions(false); //disable options so that the user has to first hire an employee before viewing, searching, sorting, or firing
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**Create the frame*/
	public Interface() {
		
		setTitle("HR System");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e){ //when the close button is pressed
                closeOperation(); //calls the closeOperation method to ask the user if they want to save
            }
        });
		
		setBounds(20, 20, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 784, 21);
		contentPane.add(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmCalculate = new JMenuItem("Calculate Salaries");
		mntmCalculate.setToolTipText("Click here to calculate the total amount of money that will be paid out in salaries.");
		mntmCalculate.addActionListener(this);
		mntmCalculate.setActionCommand("calculate");
		mnNewMenu.add(mntmCalculate);
		
		JMenuItem mntmChangePassword = new JMenuItem("Change Password");
		mntmChangePassword.setToolTipText("Click here to change the password for this program.");
		mntmChangePassword.addActionListener(this);
		mntmChangePassword.setActionCommand("pass");
		mnNewMenu.add(mntmChangePassword);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setToolTipText("Click here to save the database.");
		mntmSave.addActionListener(this);
		mntmSave.setActionCommand("save");
		mnNewMenu.add(mntmSave);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setToolTipText("Click here to exit the program.");
		mntmExit.addActionListener(this);
		mntmExit.setActionCommand("exit");
		mnNewMenu.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmInstr = new JMenuItem("Instructions");
		mntmInstr.setToolTipText("Click here if you need help using the program.");
		mntmInstr.addActionListener(this);
		mntmInstr.setActionCommand("instr");
		mnHelp.add(mntmInstr);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setToolTipText("Click here to learn more about this program.");
		mntmAbout.addActionListener(this);
		mntmAbout.setActionCommand("about");
		mnHelp.add(mntmAbout);
		
		JButton btnSearch = new JButton("");
		btnSearch.setToolTipText("Click here to search the list of employees using the text in the search bar.");
		btnSearch.setIcon(new ImageIcon("search.png"));
		btnSearch.addActionListener(this);
		btnSearch.setActionCommand("search");
		btnSearch.setBounds(300, 68, 24, 21);
		contentPane.add(btnSearch);
		
		searchField = new JTextField();
		searchField.setForeground(Color.DARK_GRAY);
		searchField.setText("Search by name (last name, first name)");
		searchField.setColumns(10);
		searchField.setBounds(10, 68, 291, 22);
		contentPane.add(searchField);
		
		JLabel lblListOfEmployees = new JLabel("List of Employees");
		lblListOfEmployees.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblListOfEmployees.setHorizontalAlignment(SwingConstants.CENTER);
		lblListOfEmployees.setBounds(10, 32, 314, 25);
		contentPane.add(lblListOfEmployees);
		
		btnSort = new JButton("Sort...");
		btnSort.setToolTipText("Click here to sort the list of employees.");
		btnSort.addActionListener(this);
		btnSort.setActionCommand("sort");
		btnSort.setBounds(10, 528, 122, 23);
		contentPane.add(btnSort);
		
		btnView = new JButton("View");
		btnView.setToolTipText("Click here to view an employee's information.");
		btnView.addActionListener(this);
		btnView.setActionCommand("view");
		btnView.setBounds(10, 496, 122, 23);
		contentPane.add(btnView);
		
		btnHire = new JButton("Hire Employee");
		btnHire.setToolTipText("Click here to add an employee to the database.");
		btnHire.addActionListener(this);
		btnHire.setActionCommand("hire");
		btnHire.setBounds(202, 496, 122, 23);
		contentPane.add(btnHire);
		
		btnFire = new JButton("Fire Employee");
		btnFire.setToolTipText("Click here to remove an employee from the database.");
		btnFire.addActionListener(this);
		btnFire.setActionCommand("fire");
		btnFire.setBounds(202, 528, 122, 23);
		contentPane.add(btnFire);
		
		list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBounds(10, 101, 314, 384);
		contentPane.add(scrollPane);
		
		cards = new JPanel();
		cards.setBounds(345, 32, 429, 519);
		contentPane.add(cards);
		cards.setLayout(new CardLayout(0, 0));
		
		
		
		
		/********INTRODUCTORY PANEL********/
		
		JPanel panel_3 = new JPanel();
		cards.add(panel_3, "introPanel");
		panel_3.setLayout(null);
		
		JLabel lblWelcomeToThe = new JLabel("Welcome to the HR System Program");
		lblWelcomeToThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcomeToThe.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWelcomeToThe.setBounds(10, 0, 409, 22);
		panel_3.add(lblWelcomeToThe);
		
		JTextPane txtpnInfo = new JTextPane();
		txtpnInfo.setEditable(false);
		txtpnInfo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtpnInfo.setBackground(UIManager.getColor("Button.background"));
		txtpnInfo.setText("\r\nThis program holds a record of the employees at your company.  If a record already exists, it will automatically be loaded.  Any changes made to the database can be saved by selecting 'Save' under the 'File' tab.\r\n\r\nIf you have any concerns while you are using this program, please click on the 'Help' tab and select 'Instructions' for more information.");
		txtpnInfo.setBounds(10, 33, 409, 475);
		panel_3.add(txtpnInfo);
		
		
		
		
		/********DISPLAY PANEL********/
		
		JPanel panel_1 = new JPanel();
		cards.add(panel_1, "displayPanel");
		panel_1.setLayout(null);
		
		
		//LABELS//
		
		lblFName = new JLabel("");
		lblFName.setBounds(0, 60, 46, 20);
		panel_1.add(lblFName);
		
		lblAddress = new JLabel("");
		lblAddress.setBounds(0, 125, 269, 20);
		panel_1.add(lblAddress);
		
		lblSIN = new JLabel("");
		lblSIN.setBounds(0, 191, 66, 20);
		panel_1.add(lblSIN);
		
		lblDepartment = new JLabel("");
		lblDepartment.setBounds(0, 263, 120, 20);
		panel_1.add(lblDepartment);
		
		lblPhoneNum = new JLabel("");
		lblPhoneNum.setBounds(0, 332, 90, 20);
		panel_1.add(lblPhoneNum);
		
		lblEmail = new JLabel("");
		lblEmail.setBounds(155, 332, 178, 20);
		panel_1.add(lblEmail);
		
		lblJobTitle = new JLabel("");
		lblJobTitle.setBounds(155, 263, 136, 20);
		panel_1.add(lblJobTitle);
		
		lblEmployeeNum = new JLabel("");
		lblEmployeeNum.setBounds(155, 191, 102, 20);
		panel_1.add(lblEmployeeNum);
		
		lblLName = new JLabel("");
		lblLName.setBounds(155, 60, 66, 20);
		panel_1.add(lblLName);
		
		lblAge = new JLabel("");
		lblAge.setBounds(370, 60, 29, 20);
		panel_1.add(lblAge);
		
		lblGender = new JLabel("");
		lblGender.setBounds(287, 60, 46, 20);
		panel_1.add(lblGender);
		
		lblSalary = new JLabel("");
		lblSalary.setBounds(300, 191, 99, 20);
		panel_1.add(lblSalary);
		
		lblStatus = new JLabel("");
		lblStatus.setBounds(333, 263, 86, 20);
		panel_1.add(lblStatus);
		
		JLabel label_1 = new JLabel("First Name");
		label_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_1.setBounds(0, 35, 90, 14);
		panel_1.add(label_1);
		
		JLabel label_2 = new JLabel("Address");
		label_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_2.setBounds(0, 100, 66, 14);
		panel_1.add(label_2);
		
		JLabel label_3 = new JLabel("SIN");
		label_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_3.setBounds(0, 166, 46, 14);
		panel_1.add(label_3);
		
		JLabel label_4 = new JLabel("Department");
		label_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_4.setBounds(0, 238, 79, 14);
		panel_1.add(label_4);
		
		JLabel label_5 = new JLabel("Phone Number");
		label_5.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_5.setBounds(0, 307, 90, 14);
		panel_1.add(label_5);
		
		JLabel label_6 = new JLabel("Email Address");
		label_6.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_6.setBounds(155, 307, 111, 14);
		panel_1.add(label_6);
		
		JLabel label_7 = new JLabel("Job Title");
		label_7.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_7.setBounds(155, 238, 111, 14);
		panel_1.add(label_7);
		
		JLabel label_8 = new JLabel("Employee Number");
		label_8.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_8.setBounds(155, 166, 102, 14);
		panel_1.add(label_8);
		
		JLabel label_9 = new JLabel("Last Name");
		label_9.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_9.setBounds(155, 35, 90, 14);
		panel_1.add(label_9);
		
		JLabel label_10 = new JLabel("Gender");
		label_10.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_10.setBounds(287, 35, 46, 14);
		panel_1.add(label_10);
		
		JLabel label_11 = new JLabel("Age");
		label_11.setHorizontalAlignment(SwingConstants.CENTER);
		label_11.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_11.setBounds(370, 35, 29, 14);
		panel_1.add(label_11);
		
		JLabel label_12 = new JLabel("Salary");
		label_12.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_12.setBounds(300, 166, 46, 14);
		panel_1.add(label_12);
		
		JLabel label_13 = new JLabel("Status");
		label_13.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_13.setBounds(333, 238, 66, 14);
		panel_1.add(label_13);
		
		
		//BUTTONS//
		
		JButton btnPromoteDemote = new JButton("Promote/Demote Employee");
		btnPromoteDemote.setToolTipText("Click here to promote or demote an employee.");
		btnPromoteDemote.addActionListener(this);
		btnPromoteDemote.setActionCommand("promoteOrDemote");
		btnPromoteDemote.setBounds(120, 464, 203, 23);
		panel_1.add(btnPromoteDemote);
		
		JButton btnModify = new JButton("Modify Employee Data");
		btnModify.setToolTipText("Click here to change an employee's data.");
		btnModify.addActionListener(this);
		btnModify.setActionCommand("change");
		btnModify.setBounds(120, 429, 203, 23);
		panel_1.add(btnModify);
		
		
		
		/********CHANGE PANEL********/
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		cards.add(panel_2, "changePanel");
		
		
		//LABELS//
		
		JLabel lblFirstName = new JLabel("First Name*");
		lblFirstName.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblFirstName.setBounds(0, 35, 90, 14);
		panel_2.add(lblFirstName);
		
		JLabel lblAddress_1 = new JLabel("Address*");
		lblAddress_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAddress_1.setBounds(0, 100, 66, 14);
		panel_2.add(lblAddress_1);
		
		JLabel label_30 = new JLabel("SIN");
		label_30.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_30.setBounds(0, 166, 46, 14);
		panel_2.add(label_30);
		
		JLabel lblDepartment_1 = new JLabel("Department*");
		lblDepartment_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDepartment_1.setBounds(0, 238, 79, 14);
		panel_2.add(lblDepartment_1);
		
		JLabel lblPhoneNumber = new JLabel("Phone Number*");
		lblPhoneNumber.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPhoneNumber.setBounds(0, 307, 90, 14);
		panel_2.add(lblPhoneNumber);
		
		JLabel lblEmailAddress = new JLabel("Email Address*");
		lblEmailAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblEmailAddress.setBounds(155, 307, 111, 14);
		panel_2.add(lblEmailAddress);
		
		JLabel lblJobTitle_1 = new JLabel("Job Title*");
		lblJobTitle_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblJobTitle_1.setBounds(155, 238, 111, 14);
		panel_2.add(lblJobTitle_1);
		
		JLabel lblEmployeeNumber = new JLabel("Employee Number");
		lblEmployeeNumber.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblEmployeeNumber.setBounds(155, 166, 114, 14);
		panel_2.add(lblEmployeeNumber);
		
		JLabel lblLastName = new JLabel("Last Name*");
		lblLastName.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLastName.setBounds(155, 35, 90, 14);
		panel_2.add(lblLastName);
		
		JLabel lblGender_1 = new JLabel("Gender*");
		lblGender_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGender_1.setBounds(287, 35, 66, 14);
		panel_2.add(lblGender_1);
		
		JLabel lblAge_1 = new JLabel("Age*");
		lblAge_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblAge_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAge_1.setBounds(370, 35, 29, 14);
		panel_2.add(lblAge_1);
		
		JLabel lblSalary_1 = new JLabel("Salary*");
		lblSalary_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSalary_1.setBounds(300, 166, 46, 14);
		panel_2.add(lblSalary_1);
		
		JLabel lblStatus_1 = new JLabel("Status*");
		lblStatus_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblStatus_1.setBounds(333, 238, 66, 14);
		panel_2.add(lblStatus_1);
		
		employeeNum = new JLabel("");
		employeeNum.setBounds(155, 191, 66, 20);
		panel_2.add(employeeNum);
		
		
		//TEXT FIELDS//
		
		for (int i = 0; i < 7; i++) {
			stringFields[i] = new JTextField();
		}
		stringFields[0].setBounds(0, 60, 86, 20);
		stringFields[1].setBounds(155, 60, 86, 20);
		stringFields[2].setBounds(0, 125, 269, 20);
		stringFields[3].setBounds(0, 263, 120, 20);
		stringFields[4].setBounds(155, 263, 136, 20);
		stringFields[5].setBounds(333, 263, 86, 20);
		stringFields[6].setBounds(155, 332, 178, 20);
		for (JTextField i: stringFields) {
			panel_2.add(i);
		}

		//initialize and set formatter for age and salary fields
		NumberFormat ageFormatter = NumberFormat.getNumberInstance(); 
		ageFormatter.setMaximumIntegerDigits(2);
		numFields[0] = new JFormattedTextField(ageFormatter);
		
		NumberFormat salaryFormatter = NumberFormat.getNumberInstance(); 
		salaryFormatter.setMaximumIntegerDigits(7);
		salaryFormatter.setMaximumFractionDigits(2);
		numFields[1] = new JFormattedTextField(salaryFormatter);
		
		//create formatter for the SIN and phone number text fields
		MaskFormatter newFormat = null;
		String[] format = {"#########", "###", "###", "####"};
		
		for (int i = 2; i < 6; i++) { //set formatter and initialize the SIN and phone number fields
			
			try {
				newFormat = new MaskFormatter(format[i-2]);
			}
			catch (ParseException x) {//if an error occurs while creating the mask formatter, the program closes
				errorMessage("An error occured while executing the program.");
				System.exit(0); //program closes
			}
			
			numFields[i] = new JFormattedTextField(newFormat);
		}
		
		numFields[0].setBounds(370, 60, 29, 20);
		numFields[1].setBounds(310, 191, 100, 20);
		numFields[2].setBounds(0, 191, 70, 20);
		numFields[3].setBounds(0, 332, 29, 20);
		numFields[4].setBounds(37, 332, 29, 20);
		numFields[5].setBounds(76, 332, 35, 20);
		
		for (JFormattedTextField i: numFields) {
			panel_2.add(i);
		}
		
		
		//BUTTONS AND SPINNERS//
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setToolTipText("Click here if you are done entering employee data.");
		btnFinish.addActionListener(this);
		btnFinish.setActionCommand("finish");
		btnFinish.setBounds(330, 390, 89, 23);
		panel_2.add(btnFinish);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerListModel(new String[] {"Male", "Female", "Other"}));
		spinner.setBounds(287, 60, 66, 20);
		panel_2.add(spinner);
		
		JLabel label = new JLabel("* indicates a required field.");
		label.setBounds(0, 394, 163, 14);
		panel_2.add(label);
		
		JLabel label_15 = new JLabel("$");
		label_15.setBounds(300, 191, 10, 20);
		panel_2.add(label_15);
		
	}
	
	
	
	////////FILE I/O////////
	
	/**Reads in variables from a text file.
	 * @return String password			The password stored in the file
	 * @throws FileNotFoundException	Thrown if the file could not be found
	 */
	private static void loadFromFile() {
		
		Scanner r = null;
		File record = new File("record.txt");
		
		if (!record.exists() || record.length() == 0) { //if the file does not exist or is empty
			return;
		}
		
        try {
        	r = new Scanner(new BufferedReader(new FileReader("record.txt"))); //reads the record file
        	
        	password = r.nextLine();
        	employeeCount = Integer.parseInt(r.nextLine());
        	
            while (r.hasNext("Entry")) { //adds information to an employee object variable
            	r.nextLine();
            	
                Employee newPerson = new Employee();
                
                newPerson.setFirstName(r.nextLine());
                newPerson.setLastName(r.nextLine());
                newPerson.setGender(r.nextLine());
                newPerson.setAge(Integer.parseInt(r.nextLine()));
                newPerson.setAddress(r.nextLine());
                newPerson.setSalary(Double.valueOf(r.nextLine()));
                newPerson.setPhoneNumber(r.nextLine());
                newPerson.setEmployeeNumber(Integer.parseInt(r.nextLine()));
                newPerson.setDepartment(r.nextLine());
                newPerson.setJobTitle(r.nextLine());
                newPerson.setStatus(r.nextLine());
                newPerson.setEmail(r.nextLine());
                
                if (r.nextLine().equals("1")) //if a SIN was stored
                    newPerson.setSin(r.nextLine());
                
                employees.add(newPerson); //adds employee object to employee ArrayList
            }
        } catch (IOException x) {
        	errorMessage("An error occured while reading the saved file.");
        } finally {
            r.close(); //closes the Scanner
        }
        
	}
	
	/**Writes variables into a text file
	 * @return boolean saved	Indicates whether the file was properly saved or not
	 */
	private static boolean saveToFile() {
		
		/* If the 'Hire' button is disabled, display error message and return.
		 * (the 'Hire' button is only disabled when the user is in the middle of entering employee data)
		 */
		if (!btnHire.isEnabled()) { 
			
			errorMessage("Could not save at this time.");
			return false;
		}
		
		PrintWriter p = null;
		File record = new File("record.txt");
		boolean saved = true;
		
		try { //saves to the text file
			if (!record.exists()) { //if the file does not exist, create a new file
				record.createNewFile();
			}
			
			p = new PrintWriter(record);
			
			p.write(password + "\r\n");
			p.write(employeeCount + "\r\n");
			
			for (Employee i: employees) { //enter data for each employee object
            	p.write("Entry\r\n");
                p.write(i.getFirstName() + "\r\n");
                p.write(i.getLastName() + "\r\n");
                p.write(i.getGender() + "\r\n");
                p.write(i.getAge() + "\r\n");
                p.write(i.getAddress() + "\r\n");
                p.write(i.getSalary() + "\r\n");
                p.write(i.getPhoneNumber() + "\r\n");
                p.write(i.getEmployeeNumber() + "\r\n");
                p.write(i.getDepartment() + "\r\n");
                p.write(i.getJobTitle() + "\r\n");
                p.write(i.getStatus() + "\r\n");
                p.write(i.getEmail() + "\r\n");
                
                if (!(i.getSin() == null)) { //in case SIN was not entered
                    p.write("1\r\n");
                    p.write(i.getSin() + "\r\n");
                }
                else
                    p.write("0\r\n");
            }
			
			JOptionPane.showMessageDialog(contentPane, "File saved.");
		} catch (IOException x) {
			errorMessage("An error occured while saving the file.");
			saved = false;
        } finally {
        	p.close(); //closes the PrintWriter
        }
		
		return saved;
	}
	
	
	
	////////ACTION LISTENER////////
	
	/**Listens to buttons and menu items*/
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if (command.equals("hire")) { //if the user clicked the 'Hire' button
			
			enableOptions(false); //disable options so that the user has to finish entering data before moving on
			btnHire.setEnabled(false);
			
			changeCard("changePanel"); //show the data entry panel
			
			DecimalFormat a = new DecimalFormat ("000");
			employeeNum.setText(a.format(employeeCount + 1));
			
			clearFields();
			hiring = true;
			
		}
		else if (command.equals("fire")) { //if the user clicked the 'Fire' button
			
			//remove employee at selected index and update display
			employees.remove(list.getSelectedIndex());
			updateList();
			changeCard("introPanel");
			
			//disable options if no employees remain in the database
			if (employees.size() == 0) {
				enableOptions(false);
			}
			
		}
		else if (command.equals("view")) { //if the user clicked the 'View' button
			
			showEmployeeData(employees.get(list.getSelectedIndex()));
			changeCard("displayPanel"); //show the employee display panel
			
		}
		else if (command.equals("sort")) { //if the user clicked the 'Sort' button
			
			String[] possibleValues = {"Sort by first name", "Sort by last name", "Sort by age", "Sort by salary", "Sort by employee number", "Reverse order"};
			String selectedValue = (String) JOptionPane.showInputDialog(contentPane, "How would you like to sort the employees?", "Sort Options",
					JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
			
			if (selectedValue != null) { //user did not select 'Cancel'
				
				if (selectedValue.equals(possibleValues[0])) { //user wants to sort by first name
					sortFirstName();
				}
				else if (selectedValue.equals(possibleValues[1])) { //user wants to sort by last name
					sortLastName();
				}
				else if (selectedValue.equals(possibleValues[2])) { //user wants to sort by age
					sortAge();
				}
				else if (selectedValue.equals(possibleValues[3])) { //user wants to sort by salary
					sortSalary();
				}
				else if (selectedValue.equals(possibleValues[4])) { //user wants to sort by employee number
					sortEmployeeNum();
				}
				else if (selectedValue.equals(possibleValues[5])) { //user wants to reverse the order of the employees
					reverse();
				}
			}
			else { //user cancelled
				return;
			}
			
			updateList();
		}
		else if (command.equals("finish")) { //if the user clicked the 'Fire' button
			
			if (checkEntryFields()) {
				
				Employee employee = null;
				
				if (hiring) { //if a new employee is being added, create a new employee object and get data
					
					employeeCount += 1;
					employee = new Employee();
					setEmployeeData(employee);
					employee.setEmployeeNumber(employeeCount);
					employees.add(employee);
					hiring = false;
					
				}
				else { //if an employee's information is being changed, update data
					
					/* The user may have clicked on a different employee in the list before clicking this button.
					 * To make sure that the correct employee is used in this method, the employee number of the employee
					 * being viewed is used to find the correct employee.
					 * The employee's name isn't used to search for the employee here, as it may have been changed.
					 */
					int num = Integer.parseInt(lblEmployeeNum.getText());
					
					for (int i = 0; i < employees.size(); i++) { //search employees
						
						if (employees.get(i).getEmployeeNumber() == num) { //if the employee is found
							//set the employee's data
							employee = employees.get(i);
							setEmployeeData(employee);
							
							i = employees.size(); //exit loop
						}
					}
				}
				
				
				showEmployeeData(employee);
				changeCard("displayPanel"); //show the employee display panel				
				enableOptions(true);
				btnHire.setEnabled(true);
				updateList();
				list.setSelectedIndex(employees.indexOf(employee));
				
			}
		}
		else if (command.equals("change")) { //if the user clicked the 'Modify Employee Data' button
			
			/* The user may have clicked on a different employee in the list before clicking this button.
			 * To make sure that the correct employee is used in this method, the name of the employee
			 * being viewed is used to find the correct employee.
			 */
			int i = getEmployeeIndex(lblLName.getText(),lblFName.getText());
			
			Employee employee = employees.get(i);
			
			enableOptions(false); //disable options so that the user has to finish entering data before moving on
			btnHire.setEnabled(false);
			
			changeCard("changePanel"); //show the data entry panel
			
			//enter employee data into the text fields
			stringFields[0].setText(employee.getFirstName());
			stringFields[1].setText(employee.getLastName());
			stringFields[2].setText(employee.getAddress());
			stringFields[3].setText(employee.getDepartment());
			stringFields[4].setText(employee.getJobTitle());
			stringFields[5].setText(employee.getStatus());
			stringFields[6].setText(employee.getEmail());
			numFields[0].setText(String.valueOf(employee.getAge()));
			numFields[1].setText(String.valueOf(employee.getSalary()));
			numFields[2].setText(employee.getSin());
			
			StringTokenizer phoneNumParts = new StringTokenizer(employee.getPhoneNumber(), "-", false);
			
			for (int j = 3; j < 6; j++) {
				numFields[j].setText(phoneNumParts.nextToken());
			}
			
			spinner.setValue(employee.getGender());
		}
		else if (command.equals("promoteOrDemote")) { //if the user clicked the 'Promote/Demote Employee' button
			
			/* The user may have clicked on a different employee in the list before clicking this button.
			 * To make sure that the correct employee is used in this method, the name of the employee
			 * being viewed is used to find the correct employee.
			 */
			int i = getEmployeeIndex(lblLName.getText(), lblFName.getText());
			
			Employee employee = employees.get(i);
			
			String job = JOptionPane.showInputDialog(contentPane, "Enter the employee's new job title:");
			
			if (job != null && !job.equals("")) {
				
				try { //try to get salary from user and set values to the employee object's variables
					String salary = JOptionPane.showInputDialog(contentPane, "Enter the employee's new salary (ex. 75,450.00):");
					
					//remove commas from the salary string
					while (salary.contains(",")) {
						salary = salary.replace(",", "");
					}
					
					double temp = Double.valueOf(salary); //throws InputMismatchException if not properly formatted
					
					if (temp > 9999999.99) { //if the salary entered has 8 or more digits before the decimal point, display error message
						
						errorMessage("The salary entered is has more than 7 digits before the decimal point.  Please try again.");
						return;
						
					}
					else if (temp < 0) { //if the salary entered is less than 0, display error message
						
						errorMessage("The salary enteres is lower than 0.  Please try again and enter a positive value.");
						return;
						
					}
					
					employee.setSalary(temp);
					employee.setJobTitle(job);
					showEmployeeData(employee);
					
				} catch (InputMismatchException x) { //salary is not in the right format
					errorMessage("The salary was not entered in the right format.  Please try again.");
				}
			}
		}
		else if (command.equals("calculate")) { //if the user clicked the 'Calculate Salaries' menu item
			JOptionPane.showMessageDialog(contentPane, "The total amount of money paid out for salaries is: " + calculateSalaries()); //displays total salaries expense to user
		}
		else if (command.equals("instr")) { //if the user clicked the 'Instructions' menu item
			
			JOptionPane.showMessageDialog(contentPane, "A new employee can be hired by clicking the 'Hire' button, entering data for the new employee, and clicking the 'Finish' button.");
			JOptionPane.showMessageDialog(contentPane, "Employees can be fired by selecting them from the list on the left and clicking the 'Fire' button.");
			JOptionPane.showMessageDialog(contentPane, "Any employees in the database can be viewed by selecting them from the list and clicking the 'View' button.\n"
					+ "The list of employees can be sorted by clicking on the 'Sort...' button and selecting a type of sort.");
			JOptionPane.showMessageDialog(contentPane, "The search bar can be used to find employees in the list.  Simply enter in the name of an employee\n"
					+ "and click the search button to use the search function.");
			JOptionPane.showMessageDialog(contentPane, "The total amount of salaries that will be paid out in the year can be calculated by selecting 'Calculate Salaries' under the 'File' tab.");
			JOptionPane.showMessageDialog(contentPane, "Changes made to the database can be saved by selecting 'Save' under the 'File' tab.");
			
		}
		else if (command.equals("about")) { //if the user clicked the 'About' menu item
			JOptionPane.showMessageDialog(contentPane, "This program was created by Milind Bhambhani, Mengbo Kou, and Spasimir Vasilev.");
		}
		else if (command.equals("search")) { //if the user clicked the search button
			
			if (searchField.isEnabled()) { //if the search function is enabled
				
				String search = searchField.getText();
				
				if (search.contains(", ")) { //if the first and last names are separated by a comma and a space in the search bar
					
					int commaIndex = search.indexOf(",");
					String last = search.substring(0, commaIndex); //get last name
					String first = search.substring(commaIndex + 2); //get first name
					int i = getEmployeeIndex(last, first); //
					
					if (i != -1) { //if the employee was found
						list.setSelectedIndex(i);
					}
					else {
						errorMessage("Employee not found.");
					}
					
				}
				else { //input not formatted correctly
					errorMessage("The name was not formatted correctly.  Please try again.");
				}
			}
		}
		else if (command.equals("pass")) { //if the user clicked the 'Change Password' menu item
			
			String ans = JOptionPane.showInputDialog(contentPane, "Enter the new password (note: the program will have to save in order to set a new password):");
			if (ans != null) {
				password = ans;
				saveToFile();
			}
			
		}
		else if (command.equals("save")) { //if the user clicked the 'Save' menu item
			saveToFile();
		}
		else if (command.equals("exit")) { //if the user clicked the 'Exit' menu item
			closeOperation();
		}
		
	}
	
	
	
	////////CHECKING USER INPUT////////
	
	/**Checks if the entered SIN is valid
     * Returns true if valid, false if invalid
     * @param SIN	 
     */
    private static boolean checkSIN(String SIN) {
    	
        int x, total = 0, odd = 0, even;
        
        //looks at the even digits starting at the 2nd digit on the left        
        for (x = 1 ; x < 9 ; x += 2)
        {
            //converts the char into an integer, and the doubles it
            even = (int) (SIN.charAt (x) - '0') * 2;
            //adds each digit of the doubled number into the total
            total += even % 10;
            //if there are 2 digits after doubling
            if (even / 10 > 0)
                total += (even / 10) % 10;
        }
        //looks at the odd digits
        for (x = 0 ; x < 9 ; x += 2)
        {
            //converts each char into an integer and then adds them together
            odd += (int) (SIN.charAt (x) - '0');
        }
        //adds the odd digits to the total sum
        total += odd;
        
        //checks if it is multiple of 10
        if (total % 10 == 0)
            return true;
        else
            return false;
    }
	
    /**Checks text fields to see if the information entered is valid*/
	private static boolean checkEntryFields() {
		
		//check for blank required fields
		for (JTextField i: stringFields) { //go through all text fields
			
			if (i.getText().equals("")) { //if any of them have been left blank, display error message
				errorMessage("You have left one or more of the required fields blank.  Please enter values for all of them.");
				return false;
			}
			
		}
		
		for (int i = 0; i < 6; i++) { //go through all formatted text fields
			
			if (numFields[i].getText().equals("")) { //if any of them have been left blank
				
				if (i != 2) { //if the blank field is not the field for the SIN (SIN is not required), display error message
					errorMessage("You have left one or more of the required fields blank.  Please enter values for all of them.");
					return false;
				}
				
			}
			
		}
		
		String sin = numFields[2].getText().trim();
		
		//check for valid SIN
		if (sin != null && !sin.isEmpty()) { //if a SIN was entered
			
			try { //try to check for valid input
				
				Integer.parseInt(sin); //throws NumberFormatException if the SIN entered is not valid
				
				if (!checkSIN(sin)) { //if SIN isn't valid, displays appropriate error message
					errorMessage("The SIN that was entered is invalid.  Please enter a valid SIN.");
					numFields[2].setText(null);
					numFields[2].setValue(null);
					return false;
				}
				
			}
			catch (NumberFormatException x) {//non-integer input, displays appropriate error message
				errorMessage("The SIN that was entered is invalid.  Social Insurance Numbers are 9-digit, non-decimal numbers.");
				return false;
			}
		}
		
		return true;
	}
	
	
	
    ////////CHANGING DISPLAYED COMPONENTS////////
    
    /**Clears all text fields, except for the search bar field*/
	private static void clearFields() {
		for (JTextField i: stringFields) { //reset all text fields , excluding the search bar
			i.setText(null);
		}
		
		for (JFormattedTextField i: numFields) { //reset all formatted text fields
			i.setText(null);
			i.setValue(null);
		}
	}
	
	/**Changes the panel being displayed on the right side of the screen (displays a different 'card')
	 * @param String panel		The panel to be displayed
	 */
	private static void changeCard(String panel) {
		//change panel
		CardLayout layout = (CardLayout)cards.getLayout();
		layout.show(cards, panel);
	}
	
	/**Disables/enables the search bar, the employee list, and all buttons, excluding the 'Hire' button
	 * @param boolean x		Specifies whether to enable or disable the objects 
	 */
    private static void enableOptions(boolean x) {
    	//set enabled values of components
    	searchField.setEnabled(x);
		btnView.setEnabled(x);
		btnSort.setEnabled(x);
		btnFire.setEnabled(x);
		list.setEnabled(x);
	}
    
    
    
    ////////SETTING AND DISPLAYING EMPLOYEE DATA////////
    
    
	/**Displays an employee's information on labels
	 * @param Employee employee		The employee object to be displayed
	 */
	private static void showEmployeeData(Employee employee) {
		//set label text
		lblFName.setText(employee.getFirstName());
		lblLName.setText(employee.getLastName());
		lblGender.setText(employee.getGender());
		lblAge.setText("" + employee.getAge());
		lblAddress.setText(employee.getAddress());
		
		String sin = employee.getSin();
		if (sin == null || sin.isEmpty()) {
			lblSIN.setText("N/A");
		}
		else {
			lblSIN.setText(sin);
		}
		
		lblDepartment.setText(employee.getDepartment());
		lblStatus.setText(employee.getStatus());
		lblPhoneNum.setText(employee.getPhoneNumber());
		lblEmail.setText(employee.getEmail());
		lblJobTitle.setText(employee.getJobTitle());
		
		DecimalFormat a = new DecimalFormat ("000");
		lblEmployeeNum.setText(a.format(employee.getEmployeeNumber()));
		
		//format and set salary
		double salary = employee.getSalary();
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumIntegerDigits(7);
		formatter.setMaximumFractionDigits(2);
		lblSalary.setText("$" + formatter.format(salary));
	}

	/**Sets an employee object's variables to data entered in text fields
	 * @param Employee employee		The employee object that will be changed
	 */
	private static void setEmployeeData(Employee employee) {
		//use setters to set employee's variables
		employee.setFirstName(stringFields[0].getText());
		employee.setLastName(stringFields[1].getText());
		employee.setAddress(stringFields[2].getText());
		employee.setDepartment(stringFields[3].getText());
		employee.setJobTitle(stringFields[4].getText());
		employee.setStatus(stringFields[5].getText());
		employee.setEmail(stringFields[6].getText());
		employee.setAge(Integer.parseInt(numFields[0].getText()));
		employee.setSin(numFields[2].getText().trim());
		employee.setPhoneNumber(numFields[3].getText() + "-" + numFields[4].getText() + "-" + numFields[5].getText());
		employee.setGender(String.valueOf(spinner.getValue()));
		
		String salary = numFields[1].getText();
		
		//remove commas from the salary string
		while (salary.contains(",")) {
			salary = salary.replace(",", "");
		}
		
		employee.setSalary(Double.valueOf(salary));
	}
    
	/**Updates the JList that displays employees*/
	private static void updateList() {
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		
		String name;
		
		for (Employee i: employees) { //add each employee's full name to the model
			name = i.getLastName() + ", " + i.getFirstName();
			model.addElement(name);
		}
		
		list.setModel(model);
	}
	
    
    
    ////////SORTING METHODS////////
	
    /**Orders the employee ArrayList in reverse order*/    
    private static void reverse() {
       
        int length = employees.size();
       
        for (int x = 0; x < length; x++)
        {
        	employees.add(x, employees.get(length - 1)); //takes last index and moves it to first
        	employees.remove(length); //removes the last index
        }
    }

	/**Sorts the employee ArrayList by employee number*/
	private static void sortEmployeeNum(){
	       
        int length = employees.size();
       
        //bubble sort
        for (int x = 0; x < length; x++)
        {
            for (int y = 1; y < length; y++)
            {
                int num1 = employees.get(y).getEmployeeNumber();
                int num2 = employees.get(y - 1).getEmployeeNumber();
               
                if (num1 < num2) //switches if num2 is greater
                {
                    Employee temp = employees.get(y);
                    employees.set(y, employees.get(y-1));
                    employees.set(y - 1, temp);
                       
                }
                   
            }
        }
    }
    
    /**Sorts the employee ArrayList by age*/
    private static void sortAge() {
       
        int length = employees.size();
   
        //bubble sort - compares adjacent integers. If right int is smaller, switch the order of the 2
            for (int x = 0; x < length; x++)
            {
            for (int y = 1; y < length; y++)
            {   //sets variables to the ages
                int age1 = employees.get(y).getAge();
                int age2 = employees.get(y - 1).getAge();
               
                if (age1 < age2) //switches order if right one is less
                {
                    Employee temp = employees.get(y);
                    employees.set(y, employees.get(y-1));
                    employees.set(y - 1, temp);
                       
                }
            }
            }
    }
    
    /**Sorts the employee ArrayList by salary*/
    private static void sortSalary() {
           
        int length = employees.size();
           
        //bubble sort
        for (int x = 0; x < length; x++)
        {
            for (int y = 1; y < length; y++)
            {
                double money1 = employees.get(y).getSalary();
                double money2 = employees.get(y - 1).getSalary();
               
                if (money1 < money2) //switches if money2 is greater
                {
                    Employee temp = employees.get(y);
                    employees.set(y, employees.get(y-1));
                    employees.set(y - 1, temp);
                       
                }
                   
            }
        }
    }
 
    /**Sorts the employee ArrayList by last name*/
    private static void sortLastName() {
           
        int length = employees.size();
   
        //bubble sort
            for (int x = 0; x < length; x++)
            {
            for (int y = 1; y < length; y++)
            {
                String name1 = employees.get(y).getLastName();
                String name2 = employees.get(y - 1).getLastName();
               
                if (name1.compareTo(name2) < 0)
                {
                    Employee temp = employees.get(y);
                    employees.set(y, employees.get(y-1));
                    employees.set(y - 1, temp);
                       
                }
                   
            }
            }  
    }
 
    /**Sorts the employee ArrayList by first name*/
    private static void sortFirstName() {
           
        int length = employees.size();
   
        //bubble sort
        for (int x = 0; x < length; x++)
        {
            for (int y = 1; y < length; y++)
            {
                String name1 = employees.get(y).getFirstName();
                String name2 = employees.get(y - 1).getFirstName();
               
                if (name1.compareTo(name2) < 0)
                {
                    Employee temp = employees.get(y);
                    employees.set(y, employees.get(y-1));
                    employees.set(y - 1, temp);
                       
                }
                   
            }
        }
    }
    
    
    
    ////////OTHER METHODS////////
    
	/**Calculates the company's total salary expense
	 * @return double	Returns the total expense
	 */
    private static double calculateSalaries() {
        double total = 0;
        
        //calculate sum of employee salaries
        for (int x = 0; x < employees.size(); x++)
        {
            total += employees.get(x).getSalary();
        }
        return total;
    }
    
	/**Plays an alert sound and displays a message to the user regarding an error that occurred
	 * @param String prompt		The prompt to display to the user
	 */
	private static void errorMessage(String prompt) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(contentPane, prompt); //pop up to inform user of error
	}
	
	/**Searches for an employee object in the employee ArrayList by the employee's name.
	 * @param String last		The last name of the employee
	 * @param String first		The first name of the employee
	 * @return int				Returns the index of the employee object if it is found.  If not, returns -1.
	 */
	private static int getEmployeeIndex(String last, String first) {
		//loop through employee ArrayList, check for a match with the first and last names, and return employee index if found
		for (int i = 0; i < employees.size(); i++) {
			if (employees.get(i).getLastName().equals(last) && employees.get(i).getFirstName().equals(first)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**Displays a message to the user asking if they want to save the data before exiting when the user tries to close the program*/
	private static void closeOperation() {
		
		int option = JOptionPane.showConfirmDialog(contentPane, "Would you like to save before exiting?");
		
		if (option == 0) { //user clicks 'Yes'
			boolean saved = saveToFile();
			if (!saved) {
				return;
			}
		}
		else if (option == 2) { //user clicks 'Cancel'
			return;
		}
		
		System.exit(0); //closes application
		
	}
	
	/**Asks the user for a password
	 * Closes the program after 5 failed attempts
	 */
	private static void checkPassword() {
		
		//create panel for the dialog
		JPanel panel = new JPanel();
		JLabel lbl = new JLabel("Enter password (default: 'Password'):");
		JPasswordField pf = new JPasswordField(password.length() + 2);
		
		panel.add(lbl);
		panel.add(pf);
		
		int opt;
		int passCount = 0;
		
		while (passCount < 5) { //loops while the user has not incorrectly entered the password 5 times

			opt = JOptionPane.showConfirmDialog(contentPane, panel, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			if (opt == 0) { //if the user pressed 'Ok'
				
				if (String.valueOf(pf.getPassword()).equals(password)) { //if the correct password was entered
					return;
				}
				
				passCount += 1;
				
				if (passCount != 5) {
					errorMessage("Incorrect password entered."); //display error message
				}
				
			}
			else { //if the user pressed 'Cancel'
				System.exit(0);
			}
			
		}
		
		errorMessage("The password was entered incorrectly 5 times.  The program will now close.");
		System.exit(0);
		
	}
}
