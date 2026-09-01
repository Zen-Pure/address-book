package addressbook.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class Contact implements Serializable{
    private static final long serialVersionUID=1L;
    private String name;
    private String phone;
    private String mobile;
    private String imTool;
    private String imAccount;
    private String email;
    private String personalHomepage;
    private Date birthday;
    private String photo;
    private String company;
    private String address;
    private String postalCode;
    private List<String> groups;
    private String comments;
    private int id;
    private String pinyin;         
	private String pinyinInitial;  
	private String pinyinInitials;

    public Contact(){
        this.groups=new ArrayList<>();
        this.birthday=null;
    }
    public String getName(){
        return name;
    }
    public String getPhone(){
        return phone;
    }
    public String getMobile(){
        return mobile;
    }
    public String getImTool(){
        return imTool;
    }   
    public String getImAccount(){
        return imAccount;
    }   
    public String getEmail(){
        return email;
    }   
    public String getPersonalHomepage(){
        return personalHomepage;
    }   
    public Date getBirthday(){
        return birthday;
    }   
    public String getPhoto(){
        return photo;
    }    
    public String getCompany(){
        return company;
    } 
    public String getAddress(){
        return address;
    } 
    public String getPostalCode(){
        return postalCode;
    } 
    public List<String> getGroups(){
        return groups;
    } 
    public String getComments(){
        return comments;
    }   
    public Integer getId(){
        return id;
    }
	public String getPinyin(){
        return pinyin; 
    }
	public String getPinyinInitial(){
        return pinyinInitial; 
    }
	public String getPinyinInitials(){
        return pinyinInitials; 
    }

    public void setName(String name){
        this.name=name;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public void setMobile(String mobile){
        this.mobile=mobile;
    }
    public void setImTool(String imTool){
        this.imTool=imTool;
    }   
    public void setImAccount(String imAccount){
        this.imAccount=imAccount;
    }   
    public void setEmail(String email){
        this.email=email;
    }   
    public void setPersonalHomepage(String personalHomepage){
        this.personalHomepage=personalHomepage;
    }   
    public void setBirthday(Date birthday){
        this.birthday=birthday;
    }   
    public void setPhoto(String photo){
        this.photo=photo;
    }    
    public void setCompany(String company){
        this.company=company;
    } 
    public void setAddress(String address){
        this.address=address ;
    } 
    public void setPostalCode(String postalCode){
        this.postalCode=postalCode;
    } 
    public void setGroups(List<String> groups){
        this.groups=groups;
    } 
    public void setComments(String comments){
        this.comments=comments;
    }
    public void setId(int id){
        this.id=id; 
    }
    public void setPinyin(String pinyin){
        this.pinyin=pinyin; 
    }
	public void setPinyinInitial(String pinyinInitial){
        this.pinyinInitial=pinyinInitial; 
    }
	public void setPinyinInitials(String pinyinInitials){
        this.pinyinInitials=pinyinInitials; 
    }

    @Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Contact contact=(Contact) o;
		return id==contact.id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
    
    public void updatePinyin(String name) {
		if (name==null||name.trim().isEmpty()) {
			this.pinyin="";
            this.pinyinInitial="#";
            this.pinyinInitials = "";
            return;
		}
		this.pinyin=PinyinUtil.getFullPinyin(name);
		this.pinyinInitial=PinyinUtil.getFirstLetter(name);
		this.pinyinInitials=PinyinUtil.getInitials(name);
	}
}
