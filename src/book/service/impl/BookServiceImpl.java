package book.service.impl;

import book.dao.BookDAO;
import book.pojo.Book;
import book.service.BookService;

import java.util.List;

public class BookServiceImpl implements BookService {

    private BookDAO bookDAO;

    @Override
    public List<Book> getBookList() {
        return bookDAO.getBookList();
    }
}
