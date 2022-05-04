package book.dao.impl;

import book.dao.BookDAO;
import book.pojo.Book;
import myssm.basedao.BaseDAO;

import java.util.List;

public class BookDAOImpl extends BaseDAO<Book> implements BookDAO {
    @Override
    public List<Book> getBookList() {
        return executeQuery("SELECT * FROM t_book");
    }
}
