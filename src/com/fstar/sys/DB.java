package com.fstar.sys;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 





import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;
 
/**
 * 数据库访问操作类
 *
 * 说明：
 * 本类共有7个方法，三个自服务方法（获得数据库连接、关闭执行查询操作连接、关闭执行更新操作连接）
 * 四个数据库操作执行的方法（不带参数的更新、但参数的更新、不带参数的查询、带参数的查询）
 *
 * 查询方法返回List<Map<String Object>>，表示查询出的数据图。控制端使用list.get(rowIndex).get("字段名")
 * 获取rowIndex行的表字段名的值。
 *
 * 更新方法返回int，表示执行后所影响的行数，使用该方法不应根据判断返回的值来断定业务是否成功与否，只要此方
 * 法不抛出异常均表示SQL语句已成功执行。为什么返回int而不采用返回boolean？简单的说，因为执行更新，数据库端
 * 有可能返回0行数据被影响的情况，比如删除一个空表，将会返回0，在这里0并不表示执行失败，所以不能用来判定业
 * 务是否操作成功。为了解释这点，举个比较详细的例子：
 * 假设有两张表：商品表和商品类型表，假设在删除商品类型时候要求同时删除该商品类型下的商品数据，那么一般的
 * 我们会在删除商品类型之前先删除该商品类型下的所有商品数据（这里执行了两个删除动作）。如果有一天创建了一个
 * 新的商品类别，但还没来得及为该商品类别添加商品数据，商品数据表是一张空表，但决策决定删除该商品类别，那么
 * 在这种情况下，执行删除商品信息数据SQL语句定然顺利，但定然得到的是0行受影响，如果将0视为业务执行失败的话
 * 就错误的了。所以此类的更新方法返回的值制作参考之用，不应用来判定业务操作知否成功。很显然例子中的业务操作
 * 是成功的，所以此类的更新方法返回的都是int类型。
 *
 * 代码示例：
 * try{
 * String sql="update table set field1 = ?,field2=? where id=?";
 * List<Object> par = new ArrayList<Object>();
 * par.add("field1 value");
 * par.add("field2 value");
 * par.add(1);
 *      DB.update(sql,par);//这里无需根据判断返回值来判定是否执行成功
 * }catch(Exception e){
 *      e.printStackTrace();
 * }
 * @author jsper.org
 *
 */
public class DB {
 
    /**
     * 获得数据库连接
     */
    protected static Connection getConnection() {
        Connection conn = null;
        try {
             //JNDI连接池方式
             InitialContext ctx = new InitialContext();
             DataSource ds = (DataSource)ctx.lookup("jdbc/fstar");
             conn = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
 
    /**
     * 关闭执行查询操作的连接
     */
    protected static void closeConnection(ResultSet resultSet,
            Statement statement, Connection connection) {
        try {
        	if (resultSet!=null) resultSet.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
 
        try {
        	if (statement!=null) statement.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
 
        try {
        	if (connection!=null) connection.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }
 
    /**
     * 关闭执行更新操作的连接
     */
    protected static void closeConnection(Statement statement,
            Connection connection) {
        try {
            if (statement!=null) statement.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
 
        try {
        	if (connection!=null) connection.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }
 
    /**
     * 不带参数的更新
     * @param sql
     * @return flag
     * @throws Exception 
     */
    public static int update(String sql) throws Exception {
    	System.out.println("ExecuteUpdate : "+sql);
 
        int rowCount = -1;
        Connection connection = null;
        PreparedStatement statement = null;
 
        try {
            connection = DB.getConnection();
            statement = connection.prepareStatement(sql);
            rowCount = statement.executeUpdate(); 
        } finally {
            DB.closeConnection(statement, connection);
        }
        return rowCount;
    }
 
    /**
     * 带参数的更新
     * 例：
     * try{
     * String sql="update table set field1 = ?,field2=? where id=?";
     * List<Object> par = new ArrayList<Object>();
     * par.add("field1 value");
     * par.add("field2 value");
     * par.add(1);
     *      DB.update(sql,par);//这里无需根据判断返回值来判定是否执行成功
     * }catch(Exception e){
     *      e.printStackTrace();
     * }
     * @param sql
     * @param par
     * @return rowCount
     * @throws Exception 
     */
    public static int update(String sql, List<Object> par) throws Exception {
    	System.out.println("ExecuteUpdate : "+sql);
 
        //执行更新后被影响的行的数量
        int rowCount = -1;
        Connection connection = null;
        PreparedStatement statement = null;
 
        try {
            connection = DB.getConnection();
            statement = connection.prepareStatement(sql);
            if (par != null) {
                for (int i = 0; i < par.size(); i++) {
                    statement.setObject(i + 1, par.get(i));
                }
            }
            //将执行更新所影响的行数赋值给rouCount变量
            rowCount = statement.executeUpdate();
        } finally {
            DB.closeConnection(statement, connection);
        }
        //返回所影响的行数给控制端
        return rowCount;
    }
 
    /**********************************************************/
 
    /**
     * 无条件查询
     * @param sql
     * @return list<Map<String, Object>>
     * @throws Exception 
     */
    public static List<Map<String, Object>> query(String sql) throws Exception {
    	System.out.println("ExecuteQuery : "+sql);
    	
        List<Map<String, Object>> list = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            if (resultSet != null) {
                list = new ArrayList<Map<String, Object>>();
 
                // 获得结果集相关属性描述对象
                ResultSetMetaData metaData = resultSet.getMetaData();
 
                // 获得数据列的数量
                int cc = metaData.getColumnCount();
 
                // 循环遍历每一行数据，将行数据存进HashMap
                while (resultSet.next()) {
 
                    // 创建一个HashMap，以“键值对”的方式存储字段名和值
                    Map<String, Object> dataRow = new HashMap<String, Object>();
 
                    // 循环获得每列数据的列名和值并放进HashMap
                    for (int i = 1; i <= cc; i++) {
                        dataRow.put(metaData.getColumnLabel(i),resultSet.getObject(i));
                    }
                    // 向数据集合里添加一行数据
                    list.add(dataRow);
                }
            }
 
        } finally {
            // 关闭数据库
            DB.closeConnection(resultSet, statement, connection);
        }
        return list;
    }
 
    /**
     * 条件查询
     * @param sql
     * @param par
     * @return list<Map<String, Object>>
     * @throws Exception 
     */
    public static List<Map<String, Object>> query(String sql,List<Object> par) throws Exception {
    	System.out.println("ExecuteQuery : "+sql +" par :"+par);
    	
        List<Map<String, Object>> list = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            statement = connection.prepareStatement(sql);
            if (par != null) {
                for (int i = 0; i < par.size(); i++) {
                    statement.setObject(i + 1, par.get(i));
                }
            }
            resultSet = statement.executeQuery();
            if (resultSet != null) {
                list = new ArrayList<Map<String, Object>>();
 
                // 获得结果集相关属性描述对象
                ResultSetMetaData metaData = resultSet.getMetaData();
 
                // 获得数据列的数量
                int cc = metaData.getColumnCount();
 
                // 循环遍历每一行数据，将行数据存进HashMap
                while (resultSet.next()) {
 
                    // 创建一个HashMap，以“键值对”的方式存储字段名和值
                    Map<String, Object> dataRow = new HashMap<String, Object>();
 
                    // 循环获得每列数据的列名和值并放进HashMap
                    for (int i = 1; i <= cc; i++) {
                    	if (metaData.getColumnType(i) == Types.LONGVARBINARY
                    			||metaData.getColumnType(i) == Types.VARBINARY
                    			||metaData.getColumnType(i) == Types.BINARY){
                    		dataRow.put(metaData.getColumnLabel(i),resultSet.getBytes(i));
                    	}else{
                    		dataRow.put(metaData.getColumnLabel(i),resultSet.getObject(i));
                    	}
                    }
                    // 向数据集合里添加一行数据
                    list.add(dataRow);
                }
            }
        } finally {
            // 关闭数据库
            DB.closeConnection(resultSet, statement, connection);
        }
        return list;
    }
    
    public static Map<String,Map<String,Integer>> columnLabelMap = new HashMap<String,Map<String,Integer>>();
    public static Map<String,List<String>> keyLabelMap = new HashMap<String,List<String>>();
    
    public static void initTable(String tableName) throws Exception {
    	Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();            
            //列
            if (!columnLabelMap.containsKey(tableName)){
	            resultSet = connection.getMetaData().getColumns(null, null, tableName, null);
	            Map <String,Integer> allCol = new HashMap<String,Integer>();
	            while (resultSet.next()){
	            	allCol.put(resultSet.getString("COLUMN_NAME"), resultSet.getInt("DATA_TYPE"));             
	            }
	            columnLabelMap.put(tableName, allCol);
            }
            //主键
            if (!keyLabelMap.containsKey(tableName)){
	            resultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName);
	            List<String> allKey = new ArrayList<String>();
	            while (resultSet.next()){
	            	allKey.add(resultSet.getString("COLUMN_NAME"));             
	            }
	            keyLabelMap.put(tableName, allKey);
            }
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    public static void insert(String tableName, Map<String,Object> data) throws Exception {
    	initTable(tableName); //初始化数据
    	
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE); 
            resultSet = statement.executeQuery("select * from "+tableName);
            if (resultSet != null) {
            	resultSet.moveToInsertRow();
            	for (String columnLabel : data.keySet()){
            		if (columnLabelMap.get(tableName).containsKey(columnLabel)){
            			resultSet.updateObject(columnLabel, data.get(columnLabel));
            		}
            	}
            	resultSet.insertRow();
            	System.out.println("insert "+"tableName"+" data : "+data);
            }
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    public static void update(String tableName, Map<String,Object> data) throws Exception {
    	initTable(tableName); //初始化数据
    	
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            String sql = "select * from "+tableName+" where 1=1";        	
        	for (String keyLable : keyLabelMap.get(tableName)){
        		sql += " and " + keyLable + " = ?";
        	}            
            statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            int i = 1;
            for (String keyLable : keyLabelMap.get(tableName)){
                statement.setObject(i++, data.get(keyLable));
            }
            resultSet = statement.executeQuery();
            
            if (resultSet != null && resultSet.next()) {
            	for (String columnLabel : data.keySet()){
            		if (columnLabelMap.get(tableName).containsKey(columnLabel)){
            			resultSet.updateObject(columnLabel, data.get(columnLabel));
            		}
            	}
            	resultSet.updateRow();
            	System.out.println("update "+"tableName"+" data : "+data);
            }
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    
    public static void delete(String tableName, Map<String,Object> data) throws Exception {
    	delete(tableName, data, true);
    }
    
    public static void deleteMulti(String tableName, Map<String,Object> data) throws Exception {
    	delete(tableName, data, false);
    }
    
    public static void delete(String tableName, Map<String,Object> data, boolean onlyOne) throws Exception {
    	initTable(tableName); //初始化数据
    	
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            String sql = "select * from "+tableName+" where 1=1";        	
        	for (String keyLable : keyLabelMap.get(tableName)){
        		if (data.containsKey(keyLable)){
        			sql += " and " + keyLable + " = ?";
        		}else if (onlyOne){
        			throw new Exception ("can't del all data!");
        		}
        	}            
            statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int i = 1;
            for (String keyLable : keyLabelMap.get(tableName)){
            	if (data.containsKey(keyLable)){
            		statement.setObject(i++, data.get(keyLable));
            	}else if (onlyOne){
        			throw new Exception ("can't del all data!");
        		}
            }
            resultSet = statement.executeQuery();
            
            if (resultSet != null) {
            	while (resultSet.first()) {
                	resultSet.deleteRow();
                }
            	System.out.println("delete "+"tableName"+" data : "+data);
            }          
            
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    public static List<Map<String, Object>> selete(String tableName) throws Exception {
    	initTable(tableName); //初始化数据
    	
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            String sql = "select * from "+tableName;        	
            return query(sql);
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    public static List<Map<String, Object>> seleteByKey(String tableName, Map<String,Object> data) throws Exception {
    	initTable(tableName); //初始化数据
    	
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            String sql = "select * from "+tableName+" where 1=1";      
            List<Object> par = new ArrayList<Object>();
        	for (String keyLable : keyLabelMap.get(tableName)){
        		sql += " and " + keyLable + " = ?";
        		par.add(data.get(keyLable));
        	}        	
            return query(sql, par);
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    public static List<Map<String, Object>> seleteByColumn(String tableName, Map<String,Object> data, String selectCol, String sqlOther) throws Exception {
    	initTable(tableName); //初始化数据
    	
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            String sql = "select "+selectCol+" from "+tableName+" where 1=1";      
            List<Object> par = new ArrayList<Object>();
        	for (String keyLable : data.keySet()){
        		if (columnLabelMap.get(tableName).containsKey(keyLable)){
	        		sql += " and " + keyLable + " = ?";
	        		par.add(data.get(keyLable));
        		}
        	}
        	sql += " "+sqlOther;
            return query(sql, par);
        } finally {
            DB.closeConnection(resultSet, statement, connection);
        }
    }
    
    public static List<Map<String, Object>> seleteByColumn(String tableName, Map<String,Object> data) throws Exception {
    	return seleteByColumn(tableName, data, "*", "");
    }

}