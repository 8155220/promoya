package uagrm.promoya.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shep on 10/25/2017.
 */

public class Product implements Serializable{
    private String Name,Image,Description,Price, CategoryId,ProductId,principalCategory,date,storeId,storeName;
    private long offerExpire;
    private List<String> listImage;

    public Product() {

        listImage=new ArrayList<>();
        this.offerExpire = 0;
    }


    public Product(String name, String image, String description, String price, String categoryId, String productId, String principalCategory, String date, String storeId, String storeName, long offerExpire, List<String> listImage) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        CategoryId = categoryId;
        ProductId = productId;
        this.principalCategory = principalCategory;
        this.date = date;
        this.storeId = storeId;
        this.storeName = storeName;
        this.offerExpire = offerExpire;
        this.listImage = listImage;
    }

    public long getOfferExpire() {
        return offerExpire;
    }

    public void setOfferExpire(long offerExpire) {
        this.offerExpire = offerExpire;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getPrincipalCategory() {
        return principalCategory;
    }

    public void setPrincipalCategory(String principalCategory) {
        this.principalCategory = principalCategory;
    }

    public List<String> getListImage() {
        return listImage;
    }

    public void setListImage(List<String> listImage) {
        this.listImage = listImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addUrlImg(String url)
    {
        this.listImage.add(url);
    }
    public void clearUrl(){ this.listImage.clear();}

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
