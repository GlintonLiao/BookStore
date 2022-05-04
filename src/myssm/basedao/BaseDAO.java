package myssm.basedao;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {
    protected Connection conn;
    protected PreparedStatement psmt;
    protected ResultSet rs;

    // T 的 Class 对象
    private Class entityClass;

    public BaseDAO() {
        //getClass() 获取 Class 对象，当前我们执行的是 new FruitDAOImpl() , 创建的是 FruitDAOImpl的实例
        //那么子类构造方法内部首先会调用父类（BaseDAO）的无参构造方法
        //因此此处的 getClass() 会被执行，但是 getClass 获取的是 FruitDAOImpl 的 Class
        //所以 getGenericSuperclass() 获取到的是 BaseDAO 的 Class
        Type genericType = getClass().getGenericSuperclass();
        // ParameterizedType 参数化类型
        Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        // 获取到的 <T> 中的 T 的真实类型
        Type actualType = actualTypeArguments[0];

        try {
            entityClass = Class.forName(actualType.getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO 构造方法出错了，可能的原因是没有指定 <> 中的类型");
        }
    }

    protected Connection getConn() {
        return ConnUtil.getConn();
    }

    protected void close(ResultSet rs, PreparedStatement psmt, Connection conn) {

    }

    // 给预处理命令对象设置参数
    private void setParams(PreparedStatement psmt, Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                psmt.setObject(i + 1, params[i]);
            }
        }
    }

    // 执行更新，返回影响行数
    protected int executeUpdate(String sql, Object... params) {
        boolean insertFlag = false;
        insertFlag = sql.trim().toUpperCase().startsWith("INSERT");

        conn = getConn();
        try {
            if (insertFlag) {
                psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                psmt = conn.prepareStatement(sql);
            }
            setParams(psmt, params);
            int count = psmt.executeUpdate();

            if (insertFlag) {
                rs = psmt.getGeneratedKeys();
                if (rs.next()) {
                    return ((Long)rs.getLong(1)).intValue();
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO executeUpdate 出错了");
        }
    }

    // 通过反射技术给 obj 的 property 属性赋予 propertyValue 的值
    private void setValue(Object obj, String property, Object propertyValue) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class clazz = obj.getClass();

        // 获取 property 这个字符串对应的属性名
        // 比如 "fid"，就去找 obj 中的 fid 属性
        Field field = clazz.getDeclaredField(property);
        if (field != null) {

            // 获取当前字段的类型名称
            String typeName = field.getType().getName();

            // 判断如果是自定义类型，则需要调用这个自定义类的带一个参数的构造方法
            // 创建出这个自定义的实例对象，将这个对象赋予给这个属性
            if (isMyType(typeName)) {
                Class typeNameClass = Class.forName(typeName);
                Constructor constructor = typeNameClass.getDeclaredConstructor(Integer.class);
                propertyValue = constructor.newInstance(propertyValue);
            }
            field.setAccessible(true);
            field.set(obj, propertyValue);
        }
    }

    private static boolean isNotMyType(String typeName) {
        return "java.lang.Integer".equals(typeName)
                || "java.lang.String".equals(typeName)
                || "java.util.Date".equals(typeName)
                || "java.sql.Date".equals(typeName)
                || "java.lang.Double".equals(typeName);
    }

    private static boolean isMyType(String typeName) {
        return !isNotMyType(typeName);
    }

    // 执行复杂查询，返回统计结果
    protected Object[] executeComplexQuery(String sql, Object... params) {
        conn = getConn();
        try {
            psmt = conn.prepareStatement(sql);
            setParams(psmt, params);
            rs = psmt.executeQuery();

            // 通过 rs 可以获取结果集的元数据
            // 元数据：比如有哪些列，什么类型，等等
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取列数
            int columnCount = rsmd.getColumnCount();
            Object[] columnValueArr = new Object[columnCount];
            // 解析 rs
            if (rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    columnValueArr[i] = columnValue;
                }
                return columnValueArr;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO executeComplexQuery 出错了");
        }
        return null;
    }

    // 执行查询，返回单个实体对象
    protected T load(String sql, Object... params) {
        conn = getConn();
        try {
            psmt = conn.prepareStatement(sql);
            setParams(psmt, params);
            rs = psmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取列数
            int columnCount = rsmd.getColumnCount();
            // 解析 rs
            if (rs.next()) {
                T entity = (T)entityClass.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnName(i + 1);
                    Object columnValue = rs.getObject(i + 1);
                    setValue(entity, columnName, columnValue);
                }
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO load 出错了");
        }
        return null;
    }

    // 执行查询，返回 List
    protected List<T> executeQuery(String sql, Object... params) {
        List<T> list = new ArrayList<>();
        conn = getConn();
        try {
            psmt = conn.prepareStatement(sql);
            setParams(psmt, params);
            rs = psmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取列数
            int columnCount = rsmd.getColumnCount();
            // 解析 rs
            while (rs.next()) {
                T entity = (T)entityClass.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnLabel(i + 1);
                    Object columnValue = rs.getObject(i + 1);
                    setValue(entity, columnName, columnValue);
                }
                list.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("BaseDAO executeQuery 出错了");
        }
        return list;
    }
}
