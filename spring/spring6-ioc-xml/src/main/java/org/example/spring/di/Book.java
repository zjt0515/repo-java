package org.example.spring.di;

public class Book {
    private String bname;
    private String author;
    public Book() {

    }
    @Override
    public String toString() {
        return "Book{" +
                "bname='" + bname + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
    // 有参数构造方法
    public Book(String bname, String author) {
        System.out.println("有参数构造");
        this.bname = bname;
        this.author = author;
    }
// 生成属性的

    public void setBname(String bname) {
        this.bname = bname;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static void main(String[] args) {
        // set方法注入
        Book book  = new Book();
        book.setBname("kava");
        book.setAuthor("我");

        // 构造器注入
        Book book1 = new Book("名字","作者");
    }
}
