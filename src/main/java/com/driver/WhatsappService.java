package com.driver;
import java.util.*;

public class WhatsappService {

    WhatsappRepository whatsappRepository= new WhatsappRepository();


    public String createUser(String name, String mobile) throws Exception {
        User user = new User(name,mobile);

        if(whatsappRepository.getUserForMobile(mobile).isEmpty()){
            whatsappRepository.addUser(mobile,user);
            return "SUCCESS";
        }
        else{
            throw new RuntimeException("User already exists");
        }
    }


    public Group createGroup(List<User> users) {
        int noOfParticipants= users.size();
        String groupName;
        User admin;
        if(noOfParticipants==2){
            groupName = users.get(1).getName();
            admin = users.get(0);
        }
        else /*if(noOfParticipants>2)*/{
            int groupNo = whatsappRepository.getCustomGroupCount();
            groupName= "Group"+(groupNo+1);
            admin = users.get(0);
            whatsappRepository.addCustomGroupCount();
        }
        Group group= new Group(groupName, noOfParticipants);
        whatsappRepository.addGroup(group, users, admin);
        return group;
    }

    public int createMessage(String content) {
        int msgId;
        msgId= whatsappRepository.getMessageId()+1;
        //update message id
        whatsappRepository.addMessageId();
        //Save msg
        Message message= new Message(msgId,content);
        whatsappRepository.saveMessage(message);
        return msgId;
    }

    public int sendMessage(Message message, User sender, Group group) {

        //Throw "Group does not exist" if the mentioned group does not exist
        Boolean groupPresent= whatsappRepository.findGroup(group);
        if(!groupPresent){
            throw new RuntimeException("Group does not exist");
        }

        //Throw "You are not allowed to send message" if the sender is not a member of the group
       List<User> users = whatsappRepository.getUserInGroup(group);
        if(!users.contains(sender)){
           throw new RuntimeException("You are not allowed to send message");
        }

        //If the message is sent successfully, return the final number of messages in that group.
        whatsappRepository.sendMessageToGroup(group,sender,message);
        return  whatsappRepository.getMessageCountInGroup(group);
    }

    public String changeAdmin(User approver, User user, Group group){
        //Throw "Group does not exist" if the mentioned group does not exist
        Boolean groupPresent= whatsappRepository.findGroup(group);
        if(!groupPresent){
            throw new RuntimeException("Group does not exist");
        }
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        User admin = whatsappRepository.getGroupAdmin(group);
        if(!admin.equals(approver)){
            throw new RuntimeException("Approver does not have rights");
        }
        //Throw "User is not a participant" if the user is not a part of the group

        List<User> users = whatsappRepository.getUserInGroup(group);
        if(!users.contains(user)){
            throw new RuntimeException("User is not a participant");
        }
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        whatsappRepository.updateAdminForGroup(group,user);
        return "SUCCESS";
    }

    public int removeUser(User user) {
        //This is a bonus problem and does not contains any marks
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception

          Map<Group,List <User>> map =  whatsappRepository.getAllGroupsAndUsers();
           //iterate over map
        Object var;
        Group myGroup= null;
        for(var entry: map.entrySet()){
                if(entry.getValue().contains(user)){
                    myGroup= entry.getKey();
                }
            }
        if(Objects.isNull(myGroup)){
           throw new RuntimeException("User not found");
        }
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        User admin= whatsappRepository.getGroupAdmin(myGroup);
        if(admin.equals(user)){
            throw new RuntimeException("Cannot remove admin");
        }
        whatsappRepository.removeAlluserMessages(user, myGroup);
        whatsappRepository.removeUserFromGroup(myGroup,user);
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
           return whatsappRepository.getUserInGroup(myGroup).size() + whatsappRepository.getMessageCountInGroup(myGroup) + whatsappRepository.getMessageId();
    }

    public String findMessage(Date start, Date end, int k) {
        // Find the Kth latest message between start and end (excluding start and end)
        List<Message> msgs = whatsappRepository.getAllMessages();

        List<Message> myMsgs = new ArrayList<>();
         for(Message msg : msgs){
             if(msg.getTimestamp().compareTo(start)>0 && msg.getTimestamp().compareTo(end)<0){
                 myMsgs.add(msg);
             }
         }
         if(myMsgs.size()<k){
             throw new RuntimeException("K is greater than the number of messages");
         }

         //comaprison of Objects -> override equals
         //ids- incrementally 1,2,3
        Collections.sort(myMsgs, Comparator.comparing(Message::getTimestamp));

         //1st latest message means n-1
        //2nd latest message means n-2(n=size)
         return myMsgs.get(myMsgs.size()-k).getContent();
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception
    }
}
