package book.dao;

import book.pojo.Book;

import java.util.List;

public interface BookDAO {
    List<Book> getBookList();
}
