package com.example.manag;

public class Product {
    String image;
    String name;
    String section;
    String amount;
    String price;
    String ingredients;
    boolean keto=true;
    boolean sugarFree=true;
    boolean vegan=true;
    String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isLow() {
        return sugarFree;
    }

    public void setLow(boolean sugarFree) {
        this.sugarFree = sugarFree;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isKeto() {
        return keto;
    }

    public void setKeto(boolean keto) {
        this.keto = keto;
    }

    public Product() {

    }

    public Product(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public Product(String image, String name, String section, String amount, String price) {
        this.image = image;
        this.name = name;
        this.section = section;
        this.amount = amount;
        this.price = price;
    }

    public Product(String image, String name, String section, String amount, String price, String ingredients) {
        this.image = image;
        this.name = name;
        this.section = section;
        this.amount = amount;
        this.price = price;
        this.ingredients = ingredients;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public String getSection() {
        return section;
    }

    public String getAmount() {
        return amount;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getIngredients() {
        return ingredients;
    }
}
