package com.lwy.myselect.executor;

import com.lwy.myselect.mapper.EntityMapper;
import com.lwy.myselect.reflection.Reflection;
import com.lwy.myselect.resultset.ResultHandler;
import com.lwy.myselect.session.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by frank lee on 2016/8/15 14:48.
 * Email: frankleecsz@gmail.com
 */
public class StandardExecutor implements Executor{

    private Session session;
    private Connection connection;
    private ResultHandler resultHandler;

    public StandardExecutor(Session session, Connection connection) {
        this.session = session;
        this.connection = connection;
    }

    /**
     *
     * @param sql             sql
     * @param propertyList    属性值
     * @param entityMapper    返回的对象的映射
     * @return
     * @throws SQLException
     */
    public Object query(String sql, List<Object> propertyList, EntityMapper entityMapper) throws SQLException {
        checkConnection();
        if(propertyList.size()>0) {        //有条件的查询
            PreparedStatement statement = preparedStatement(connection, propertyList, sql);
            ResultSet resultSet =  statement.executeQuery();
            resultHandler = new ResultHandler(entityMapper,resultSet);
            List<String> columns = Reflection.reflectToGetColumn(sql,entityMapper);
            return resultHandler.handlerResult(columns,entityMapper.getClassName());
        }
        else {

        }
        return null;
    }

    @Override
    public int executeBatch(String sql, List<List<Object>> batchProperties, int count) throws SQLException {
        checkConnection();
        connection.setAutoCommit(false);
        int result = 0;
        PreparedStatement statement = getRowPreparedStatement(connection, sql);
        int c = 0;
        for(int i=0;i<batchProperties.size();i++){
            List<Object> properties = batchProperties.get(i);
            for(int j=0;j<properties.size();j++){
                statement.setObject(j+1,properties.get(j));
            }
            statement.addBatch();
            c++;
            if(c>=count){
                int[] res = statement.executeBatch();
                for (int r:res) {
                    result += r;
                }
                connection.commit();
                statement.clearBatch();
                c = 0;
            }
        }
        int[] res = statement.executeBatch();
        for (int r:res) {
            result += r;
        }
        statement.clearBatch();
        connection.commit();
        connection.setAutoCommit(true);
        return result;
    }

    /**
     *
     * @param sql  sql
     * @param propertyList   属性值，插入到sql问号
     * @return   修改的记录数量
     * @throws SQLException
     */
    public int update(String sql, List<Object> propertyList) throws SQLException {
        checkConnection();
        PreparedStatement statement = preparedStatement(connection,propertyList,sql);
        return statement.executeUpdate();
    }

    /**
     *
     * @param connection connection
     * @param propertyList  属性值，插入到sql问号
     * @param sql sql
     * @return  PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement preparedStatement(Connection connection,List<Object> propertyList, String sql) throws SQLException {
        PreparedStatement statement = getRowPreparedStatement(connection, sql);
        for(int i=0;i<propertyList.size();i++){
            statement.setObject(i+1,propertyList.get(i));
        }
        return statement;
    }

    private PreparedStatement getRowPreparedStatement(Connection connection,String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    private void checkConnection(){
        if(connection == null)
            throw new NullPointerException("connection is null, maybe it is closed");
    }
}
