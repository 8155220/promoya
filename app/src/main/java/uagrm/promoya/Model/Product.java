package uagrm.promoya.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shep on 10/25/2017.
 */

public class Product implements Serializable{
    private String Name,Image,Description,Price,Discount,MenuId,ProductId,principalCategory;
    private List<String> listImage;

    public Product() {
        listImage=new ArrayList<>();
    }


    public Product(String name, String image, String description, String price, String discount, String menuId, String productId, String principalCategory, List<String> listImage) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
        ProductId = productId;
        this.principalCategory = principalCategory;
        this.listImage = listImage;
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

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
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

    public void addUrlImg(String url)
    {
        this.listImage.add(url);
    }
    public void clearUrl(){ this.listImage.clear();}
}
