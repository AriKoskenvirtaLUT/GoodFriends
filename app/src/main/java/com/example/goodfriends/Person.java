package com.example.goodfriends;

public class Person {

    private String id;
    private String personName;
    private String profilePicture;
    private String phoneNumber;

    public Person() {    }

    public Person(String id, String personName, String profilePicture, String phoneNumber) {
        this.id = id;
        this.personName = personName;
        this.profilePicture = profilePicture;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", personName='" + personName + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public String getId() {        return id;    }

    public void setId(String id) {        this.id = id;    }

    public String getPersonName() {
        return personName;
    }


    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
