/*
 * Copyright (C), CACode, 2020, all rights reserved.
 *
 * Project name： JDBCHelp
 * File name： JDBCDataSource.java
 *  Module declaration:
 * Modify the history:
 * 2020-4-26 - CACode - Create。
 */

/*
 * 版权所有(C)，CACode，2020，所有权利保留。
 *
 * 项目名： JDBCHelp
 * 文件名： JDBCDataSource.java
 * 模块说明：
 * 修改历史:
 * 2020-3-15 - CACode - 创建。
 */

package CACode.cctvadmin.jdbcHelp.java;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

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
public class JDBCDataSource {
    private static DataSource ds;

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
    public JDBCDataSource(InputStream path) {
        Properties pro = new Properties();
        try {
            pro.load(path);
            setDs(DruidDataSourceFactory.createDataSource(pro));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setDs(DataSource ds) {
        JDBCDataSource.ds = ds;
    }

    /**
     * 返回连接池对象DataSource
     *
     * @return DataSource
     */
    public DataSource getDs() {
        return ds;
    }

    /**
     * 返回连接池连接
     *
     * @return Connection对象
     * @throws SQLException 抛出SQLException异常
     */
    public Connection getConnection() throws SQLException {
        return getDs().getConnection();
    }

    /**
     * 归还连接池
     *
     * @param conn Connection对象
     * @throws SQLException 抛出SQLException异常
     */
    public void close(Connection conn) throws SQLException {
        close(conn, null, null);
    }

    /**
     * 归还连接池
     *
     * @param conn Connection对象
     * @param stmt Statement对象
     * @throws SQLException 抛出SQLException异常
     */
    public void close(Connection conn, Statement stmt) throws SQLException {
        close(conn, stmt, null);
    }

    /**
     * 归还连接池
     *
     * @param conn Connection对象
     * @param stmt Statement对象
     * @param rs   ResultSet对象
     * @throws SQLException 抛出SQLException异常
     */
    public void close(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
