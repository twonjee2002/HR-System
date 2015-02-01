/**Employee Class
 * @authors Milind Bhambhani, Mengbo Kou, Spasimir Vasilev, Chenglin Bi
 * @version 
 */
public class Employee {
	String firstName;
    String lastName;
    String gender;
    int age;
    String address;
    String sin = null;
    double salary;
    String phoneNumber;
    int employeeNumber;
    String department;
    String jobTitle;
    String status;
    String email;
    
    ////////////////GETTERS & SETTERS////////////////
    /**@return the first name*/
    public String getFirstName() {
        return firstName;
    }

    /**@param firstName		the first name to set*/
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**@return the last name*/
    public String getLastName() {
        return lastName;
    }

    /**@param lastName		the last name to set*/
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**@return the gender*/
    public String getGender() {
        return gender;
    }
    
    /**@param gender	the gender to set*/
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**@return the age*/
    public int getAge() {
        return age;
    }

    /**@param age		the age to set*/
    public void setAge(int age) {
        this.age = age;
    }

    /**@return the address*/
    public String getAddress() {
        return address;
    }

    /**@param address		the address to set*/
    public void setAddress(String address) {
        this.address = address;
    }

    /**@return the SIN*/
    public String getSin() {
        return sin;
    }

    /**@param sin		the SIN to set*/
    public void setSin(String sin) {
        this.sin = sin;
    }

    /**@return the salary*/
    public double getSalary() {
        return salary;
    }

    /**@param salary	the salary to set*/
    public void setSalary(double salary) {
        this.salary = salary;
    }

    /**@return the phoneNumber*/
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**@param phoneNumber		the phoneNumber to set*/
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**@return the employeeNumber*/
    public int getEmployeeNumber() {
        return employeeNumber;
    }

    /**@param employeeNumber	the employeeNumber to set*/
    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    /**@return the department*/
    public String getDepartment() {
        return department;
    }

    /**@param department	the department to set*/
    public void setDepartment(String department) {
        this.department = department;
    }

    /**@return the jobTitle*/
    public String getJobTitle() {
        return jobTitle;
    }

    /**@param jobTitle		the jobTitle to set*/
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**@return the status*/
    public String getStatus() {
        return status;
    }

    /**@param status	the status to set*/
    public void setStatus(String status) {
        this.status = status;
    }

    /**@return the email*/
    public String getEmail() {
        return email;
    }
    
    /**@param email		the email to set*/
    public void setEmail(String email) {
        this.email = email;
    }
}