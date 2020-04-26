/*
 * Copyright (C), CACode, 2020, all rights reserved.
 *
 * Project name： JDBCHelp
 * File name： JDBCHelp.java
 *  Module declaration:
 * Modify the history:
 * 2020-4-26 - CACode - Create。
 */

/*
 * 版权所有(C)，CACode，2020，所有权利保留。
 *
 * 项目名： JDBCHelp
 * 文件名： JDBCHelp.java
 * 模块说明：
 * 修改历史:
 * 2020-3-12 - CACode - 创建。
 */
package CACode.cctvadmin.jdbcHelp.java;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Vector;

/**
 * @author CACode http://www.adminznh.ren
 * @version 1.3
 * @date 2020/4/25 00:43
 */

/**
 * <p>
 * 使用方法 <BR>
 * 在你的src目录下新建一个[包/配置文件] jdbcHelp/JDBC.properties <BR/>
 * 连接池包，包含
 * <p>
 * 1.获取连接池对象
 * <p>
 * 2.获取Connection连接
 * <p>
 * 3.归还连接池
 * <p>
 * 配置文件：
 * <p>
 * driverClassName=com.mysql.cj.jdbc.Driver
 * <p>
 * url=jdbc:mysql://127.0.0.1:3306/wdf?useSSL=false&serverTimezone=UTC
 * <p>
 * username=root
 * <p>
 * password=123456
 * <p>
 * # 初始化连接数量
 * <p>
 * initialSize=5
 * <p>
 * maxActive=10
 * <p>
 * maxWait=3000
 * <p>
 * maxIdle=8
 * <p>
 * minIdle=3
 * </p>
 */
@SuppressWarnings("ALL")
public class JDBCHelp extends CACode.cctvadmin.jdbcHelp.java.JDBCDataSource {
    /**
     * 加载配置文件<BR/>
     * 示例：<BR/>
     * &nbsp;&nbsp;this.getClass().getClassLoader().getResourceAsStream("jdbcHelp/JDBC.properties");
     * <BR/>或：<BR/>
     * &nbsp;&nbsp;JDBCHelp.class.getClassLoader().getResourceAsStream("jdbcHelp/JDBC.properties");
     * <BR/>可以直接复制粘贴
     *
     * @param path 文件所在路径
     */
    public JDBCHelp(InputStream path) {
        super(path);
    }

    /**
     * 执行update语句返回受影响行数
     *
     * @param sql sql语句
     * @return 受影响函数
     * @throws SQLException 抛出sql异常
     */
    public int update(String sql) throws SQLException {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            int i = statement.executeUpdate(sql);
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, statement);
        }
        return 0;
    }

    /**
     * 防止SQL注入的返回受影响行数的方法
     * <p/>
     * 用法：
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;Object[] obj = new Object[]{"张三",1,"李四"};
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;String SqlPrepared = "INSERT INTO sqlTable
     * VALUES{?,?,?}";
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;JDBCHelp.update(obj,SqlPrepared);
     * <p/>
     * <strong style="color:white;">注意：</strong>
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp; 问号数量必须与数组长度一致，否则抛出 SQLException
     *
     * @param sql[]       需要查询的条件
     * @param sqlPrepared sql的格式
     * @return 防止sql注入的同时执行update方法
     * @throws SQLException 抛出sql异常
     */
    public int update(Object[] sql, String sqlPrepared) throws SQLException {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sqlPrepared);
            for (int i = 0; i < sql.length; i++) {
                pstmt.setObject((i + 1), sql[i]);
            }
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt);
        }
        return count;
    }

    /**
     * 返回读取到数据表的所有数据的集合
     * <p/>
     * 注意：
     * <p/>
     * lineCount的值代表要读取的列数
     * <p/>
     * lineCount的值有多少就会返回多少个范型是List的List集合, 每一个对象代表一列的所有值,
     * <p/>
     * 如：
     * <p/>
     * <p>
     * list_1 list_2 list_3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp; 2 &nbsp;&nbsp;&nbsp;&nbsp;3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp; 2 &nbsp;&nbsp;&nbsp;&nbsp;3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp; 2 &nbsp;&nbsp;&nbsp;&nbsp;3
     * <p/>
     *
     * @param sql       sql语句
     * @param lineCount 读取的列的数量
     * @return 返回java.util.List对象
     * @throws SQLException 抛出sql异常
     *                      <p/>
     *                      1.sql语句错误
     *                      <p/>
     *                      2.行数错误
     *                      <p/>
     */
    public List<List<Object>> read(String sql, int lineCount) throws SQLException {
        List<List<Object>> list = new Vector<>();
        for (int i = 0; i < lineCount; i++)
            list.add(new java.util.Vector<>());
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < lineCount; i++) {
                    list.get(i).add(rs.getObject((i + 1)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * 防止SQL注入的返回读取到数据表的所有数据的集合
     * <p/>
     * 用法：
     * <p/>
     * Object[] obj = new Object[]{"张三",1,"李四"};
     * <p/>
     * int lineCount = 3;
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;String SqlPrepared = "SELECT COUNT(*) FROM test WHERE
     * line1=? AND line2=? AND line3=?";
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;JDBCHelp.read(obj,lineCount,SqlPrepared);
     * <p/>
     * 注意：
     * <p/>
     * lineCount的值代表要读取的列数
     * <p/>
     * lineCount的值有多少就会返回多少个范型是List的List集合, 每一个对象代表一列的所有值,
     * <p/>
     * 如：
     * <p/>
     * <p>
     * list_1 list_2 list_3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp; 2 &nbsp;&nbsp;&nbsp;&nbsp;3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp; 2 &nbsp;&nbsp;&nbsp;&nbsp;3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp; 2 &nbsp;&nbsp;&nbsp;&nbsp;3
     * <p/>
     * &nbsp;&nbsp;&nbsp;&nbsp; 问号数量必须与数组长度一致，否则抛出 SQLException
     *
     * @param sql         需要传入的值
     * @param lineCount   查询的数据行数
     * @param sqlPrepared sql的格式
     * @return 防止sql注入的同时执行query方法
     * @throws SQLException 抛出sql异常
     */
    public List<List<Object>> read(Object[] sql, int lineCount, String sqlPrepared) throws SQLException {
        List<List<Object>> list = new Vector<>();
        for (int i = 0; i < lineCount; i++)
            list.add(new java.util.Vector<>());
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sqlPrepared);
            for (int i = 0; i < sql.length; i++) {
                pstmt.setObject((i + 1), sql[i]);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < lineCount; i++) {
                    list.get(i).add(rs.getObject((i + 1)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pstmt, rs);
        }
        return list;
    }
}
