package com.example.pbook;

/**
 * Created by mAni on 29/08/2017.
 */

public class Product {

    private String Image , Title , Description , UserName , Category , Address , Price ,  Type , UserId;

    public Product(){

    }

    public Product(String image, String title, String description, String userName, String category, String address, String price, String type, String userId) {
        Image = image;
        Title = title;
        Description = description;
        UserName = userName;
        Category = category;
        Address = address;
        Price = price;
        Type = type;
        UserId = userId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
