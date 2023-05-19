package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    private  HashMap<Integer, Message> messageMap;

    //make a map for user
   private  HashMap<String, User> mobileUserMap ;


    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>(); //only mobile number
        this.customGroupCount = 0;
        this.messageId = 0;
        this.mobileUserMap = new HashMap<>(); //mobile number, user
        this.messageMap = new HashMap<>();
    }

    public void addUser(String mobile, User user){
        mobileUserMap.put(mobile,user);
        userMobile.add(mobile);
    }

    public Optional<User> getUserForMobile(String mobile){
        if(mobileUserMap.containsKey(mobile)){
            return Optional.of(mobileUserMap.get(mobile));
        }
        return Optional.empty();
    }

    public int addCustomGroupCount(){
       return  this.customGroupCount++;
    }


    public int getCustomGroupCount() {
        return this.customGroupCount;
    }

    public void addGroup(Group group, List<User> users,User admin ) {

        groupUserMap.put(group,users);
        adminMap.put(group, admin);
    }

    public int addMessageId(){ //messageCount
       return this.messageId++;
    }
    public int getMessageId(){ //MessageCount
        return this.messageId;
    }


    public void saveMessage(Message message) {
        messageMap.put(message.getId(),message);
    }

    public Boolean findGroup(Group group) { //passing new group
        for(Group group1:groupUserMap.keySet()){
            if(group1.equals(group)){
                return true;
            }
        }

        return false;
    }

    public List<User> getUserInGroup(Group group) {
        return groupUserMap.get(group);
    }

    public void sendMessageToGroup(Group group,User sender,Message message) {

        List<Message> msgs= groupMessageMap.get(group);
        msgs.add(message);
        groupMessageMap.put(group,msgs);
        senderMap.put(message,sender);
        messageMap.put(message.getId(),message);
    }

    public int getMessageCountInGroup(Group group) {
        return groupMessageMap.get(group).size();
    }

    public User getGroupAdmin(Group group) {

        return adminMap.get(group);
    }

    public void updateAdminForGroup(Group group, User user) {
        adminMap.put(group,user);
    }

    public Map<Group,List<User>> getAllGroupsAndUsers() {
        return groupUserMap;
    }

    public void removeUserFromGroup(Group myGroup, User user) {
        List<User> users = groupUserMap.get(myGroup);
        users.remove(user);
        //update
        groupUserMap.put(myGroup,users);
    }

    public void removeAlluserMessages(User user, Group myGroup) {
        List<Message> msgs= new ArrayList<>();
        Set <Map.Entry<Message,User>> entrySet = senderMap.entrySet();
        for(var entry: entrySet){
            if(entry.getValue().equals(user)){
                msgs.add(entry.getKey());
                senderMap.remove(entry.getKey());
            }
        }

        List<Message> allMsgs = groupMessageMap.get(myGroup);
        allMsgs.removeAll(msgs);
        groupMessageMap.put(myGroup, allMsgs);

        for(Message msg: msgs){
            messageMap.remove(msg.getId());
            messageId--;
        }
    }

    public List<Message> getAllMessages(){
      return new ArrayList<>(messageMap.values());
    }
}
