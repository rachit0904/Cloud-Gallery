package Data;

public class ImageFiles {
    private String title,date,time,pages;

    public ImageFiles() {

    }

    public ImageFiles(String title, String date, String time, String pages) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.pages = pages;
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
