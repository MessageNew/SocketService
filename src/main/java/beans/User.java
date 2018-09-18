package beans;

/**
 * Created by Mysteriouseyes on 2018/9/5.
 */
public class User {

    private Integer id;
    private Integer uid;
    private String username;
    private String time;
    private String password;
    /**加入的群id列表**/
    private String gidList;
    private String sex;
    private String phone;
    private String age;
    private String address;
    private String icourl;
    /**  当前用户登录ip **/
    private String ip;
    /** 是否在线 **/
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGidList() {
        return gidList;
    }

    public void setGidList(String gidList) {
        this.gidList = gidList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIcourl() {
        return icourl;
    }

    public void setIcourl(String icourl) {
        this.icourl = icourl;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uid=" + uid +
                ", username='" + username + '\'' +
                ", time='" + time + '\'' +
                ", password='" + password + '\'' +
                ", gidList='" + gidList + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                ", age='" + age + '\'' +
                ", address='" + address + '\'' +
                ", icourl='" + icourl + '\'' +
                ", ip='" + ip + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
