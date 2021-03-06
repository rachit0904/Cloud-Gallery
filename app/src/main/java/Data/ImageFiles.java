package Data;

public class ImageFiles {
    private String title,date,time,pages,imageURL;

    public ImageFiles() {

    }

    public ImageFiles(String imageURL,String title, String date, String time, String pages) {
        this.imageURL=imageURL;
        this.title = title;
        this.date = date;
        this.time = time;
        this.pages = pages;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
}
