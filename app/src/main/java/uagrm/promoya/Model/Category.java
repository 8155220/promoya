package uagrm.promoya.Model;

import java.io.Serializable;

/**
 * Created by Scarlett on 19/10/2017.
 */

public class Category implements Serializable {
    private String Name;
    private String Image;
    private String principalCategory;
    private String categoryId;
    public Category() {
    }

    public Category(String name, String image, String principalCategory, String categoryId) {
        Name = name;
        Image = image;
        this.principalCategory = principalCategory;
        this.categoryId = categoryId;
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

    public String getPrincipalCategory() {
        return principalCategory;
    }

    public void setPrincipalCategory(String principalCategory) {
        this.principalCategory = principalCategory;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
