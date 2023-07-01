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

//    public void addUser(String mobile, User user){
//        mobileUserMap.put(mobile,user);
//        userMobile.add(mobile);
//    }

//    public Optional<User> getUserForMobile(String mobile){
//        if(mobileUserMap.containsKey(mobile)){
//            return Optional.of(mobileUserMap.get(mobile));
//        }
//        return Optional.empty();
//    }
//
//    public int addCustomGroupCount(){
//       return  this.customGroupCount++;
//    }
//
//
//    public int getCustomGroupCount() {
//        return this.customGroupCount;
//    }
//
//    public void addGroup(Group group, List<User> users,User admin ) {
//
//        groupUserMap.put(group,users);
//        adminMap.put(group, admin);
//    }
//
//    public int addMessageId(){ //messageCount
//       return this.messageId++;
//    }
//    public int getMessageId(){ //MessageCount
//        return this.messageId;
//    }
//
//
//    public void saveMessage(Message message) {
//        messageMap.put(message.getId(),message);
//    }
//
//    public Boolean findGroup(Group group) { //passing new group
//        for(Group group1:groupUserMap.keySet()){
//            if(group1.equals(group)){
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public List<User> getUserInGroup(Group group) {
//        return groupUserMap.get(group);
//    }
//
//    public void sendMessageToGroup(Group group,User sender,Message message) {
//
//        List<Message> msgs= groupMessageMap.get(group);
//        msgs.add(message);
//        groupMessageMap.put(group,msgs);
//        senderMap.put(message,sender);
//        messageMap.put(message.getId(),message);
//    }
//
//    public int getMessageCountInGroup(Group group) {
//        return groupMessageMap.get(group).size();
//    }
//
//    public User getGroupAdmin(Group group) {
//
//        return adminMap.get(group);
//    }
//
//    public void updateAdminForGroup(Group group, User user) {
//        adminMap.put(group,user);
//    }
//
//    public Map<Group,List<User>> getAllGroupsAndUsers() {
//        return groupUserMap;
//    }
//
//    public void removeUserFromGroup(Group myGroup, User user) {
//        List<User> users = groupUserMap.get(myGroup);
//        users.remove(user);
//        //update
//        groupUserMap.put(myGroup,users);
//    }
//
//    public void removeAlluserMessages(User user, Group myGroup) {
//        List<Message> msgs= new ArrayList<>();
//        Set <Map.Entry<Message,User>> entrySet = senderMap.entrySet();
//        for(var entry: entrySet){
//            if(entry.getValue().equals(user)){
//                msgs.add(entry.getKey());
//                senderMap.remove(entry.getKey());
//            }
//        }
//
//        List<Message> allMsgs = groupMessageMap.get(myGroup);
//        allMsgs.removeAll(msgs);
//        groupMessageMap.put(myGroup, allMsgs);
//
//        for(Message msg: msgs){
//            messageMap.remove(msg.getId());
//            messageId--;
//        }
//    }
//
//    public List<Message> getAllMessages(){
//      return new ArrayList<>(messageMap.values());
//    }

    public String createUser(String name, String mobile) throws Exception{

        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"

        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        User user= new User(name,mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {

        if(users.size()==2){
            Group group= new Group(users.get(1).getName(),2);
            adminMap.put(group, users.get(0));
            groupUserMap.put(group,users);
            groupMessageMap.put(group, new ArrayList<Message>());
            return group;
        }

        this.customGroupCount +=1;
        Group group= new Group(new String("Group"+this.customGroupCount), users.size());
        adminMap.put(group, users.get(0));
        groupUserMap.put(group , users);
        groupMessageMap.put(group, new ArrayList<Message>());
        return group;
    }

    public int createMessage(String content) {
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
        this.messageId+=1;
        Message message = new Message(messageId, content);
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.

        if(adminMap.containsKey(group)){
            List<User> users = groupUserMap.get(group);
            Boolean userFound = false;
            for(User user: users){
               if(user.equals(sender)){
                   userFound = true;
                   break;
               }
            }

            if(userFound){
                senderMap.put(message,sender);
                List<Message> messages = groupMessageMap.get(group);
                messages.add(message);
                groupMessageMap.put(group, messages);
                return messages.size();
            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if(adminMap.containsKey(group)){
            if(adminMap.get(group).equals(approver)){
                List<User> participants = groupUserMap.get(group);
                Boolean userFound = false;
                for(User participant: participants){
                    if(participant.equals(user)){
                        userFound= true;
                        break;
                    }
                }

                if(userFound){
                    adminMap.put(group, user);
                    return "SUCCESS";
                }
                throw new Exception("User is not a participant");
            }
            throw new Exception("Approver does not have rights");
        }
        throw new Exception("Group does not exist");
    }

    public int removeUser(User user) throws Exception{
        //This is a bonus problem and does not contains any marks
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        Boolean userFound = false;
        Group userGroup = null;
        for(Group group: groupUserMap.keySet()){
            List<User> participants = groupUserMap.get(group);
            for(User participant : participants){
                if(participant.equals(user)){
                    if(adminMap.get(group).equals(user)){
                        throw new Exception("Cannot remove admin");
                    }
                    userGroup = group;
                    userFound = true;
                    break;
                }
            }

            if(userFound){
                break;
            }
        }

        if(userFound){
            List<User> users = groupUserMap.get(userGroup);
            List<User> updatedUsers = new ArrayList<>();
            for(User participant: users){
                if(participant.equals(user))
                    continue;
                updatedUsers.add(participant);
            }

            groupUserMap.put(userGroup,updatedUsers);

            List<Message> messages = groupMessageMap.get(userGroup);
            List<Message> updatedMessages = new ArrayList<>();
            for(Message message: messages){
                if(senderMap.get(message).equals(user))
                    continue;
                updatedMessages.add(message);
            }

            groupMessageMap.put(userGroup, updatedMessages);

            HashMap<Message,User> updatedSenderMap = new HashMap<>();
            for(Message message: senderMap.keySet()){
                if(senderMap.get(message).equals(user))
                    continue;
                updatedSenderMap.put(message, senderMap.get(message));
            }
            senderMap= updatedSenderMap;
            return updatedUsers.size() + updatedMessages.size() + updatedSenderMap.size();
        }
        throw new Exception("User not found");
    }


    public String findMessage(Date start, Date end, int K) throws Exception{

        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception

        List<Message> messages = new ArrayList<>();
        for(Group group: groupMessageMap.keySet()){
            messages.addAll(groupMessageMap.get(group));
        }

        List<Message> filteredMessages = new ArrayList<>();
        for(Message message: messages){
            if(message.getTimestamp().after(start) && message.getTimestamp().before(end)){
                filteredMessages.add(message);
            }
        }

        if(filteredMessages.size()<K){
            throw new Exception ("K is greater than the number of messages");
        }

        Collections.sort(filteredMessages, new Comparator<Message>(){
            public  int compare(Message m1, Message m2) {
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
            });
        return filteredMessages.get(K-1).getContent();

        }
}
