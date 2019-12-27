package entities;

public class Category {
    private int category_id;
    private String category;
    private String image;

    public Category() {
    }

    public Category(int category_id, String category, String image) {
        this.category_id = category_id;
        this.category = category;
        this.image = image;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
