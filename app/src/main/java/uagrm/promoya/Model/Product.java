package uagrm.promoya.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shep on 10/25/2017.
 */

public class Product implements Serializable{
    private String Name,Description,Price,discount, CategoryId,ProductId,principalCategory,date,storeId,storeName;
    private long offerExpire;
    private List<String> listImage;
    public Map<String, Boolean> likes = new HashMap<>();
    public int likesCount;
    public int viewsCount;
    public int commentsCount;
    public Product() {

        listImage=new ArrayList<>();
        this.offerExpire = 0;
    }


    public Product(String name, String image, String description, String price, String discount, String categoryId, String productId, String principalCategory, String date, String storeId, String storeName, long offerExpire, List<String> listImage, int likesCount, int viewsCount, int commentsCount) {
        Name = name;
        Description = description;
        Price = price;
        this.discount = discount;
        CategoryId = categoryId;
        ProductId = productId;
        this.principalCategory = principalCategory;
        this.date = date;
        this.storeId = storeId;
        this.storeName = storeName;
        this.offerExpire = offerExpire;
        this.listImage = listImage;
        this.likesCount = likesCount;
        this.viewsCount = viewsCount;
        this.commentsCount = commentsCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", Price='" + Price + '\'' +
                ", discount='" + discount + '\'' +
                ", CategoryId='" + CategoryId + '\'' +
                ", ProductId='" + ProductId + '\'' +
                ", principalCategory='" + principalCategory + '\'' +
                ", date='" + date + '\'' +
                ", storeId='" + storeId + '\'' +
                ", storeName='" + storeName + '\'' +
                ", offerExpire=" + offerExpire +
                ", listImage=" + listImage +
                ", likes=" + likes +
                ", likesCount=" + likesCount +
                ", viewsCount=" + viewsCount +
                ", commentsCount=" + commentsCount +
                '}';
    }
}
