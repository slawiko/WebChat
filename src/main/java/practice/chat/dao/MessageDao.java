package practice.chat.dao;

import org.apache.log4j.Logger;
import practice.chat.db.ConnectionPool;
import practice.chat.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDao {
    private static Logger logger = Logger.getLogger(MessageDao.class.getName());

    private String userIdGenerator() {
        return UUID.randomUUID().toString();
    }

    public void add(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Statement statement;
        ResultSet resultSet;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM authors WHERE author = \"" + message.getAuthor() + "\"");
            String userId;
            if (resultSet.next()) {
                userId = resultSet.getString("id");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO authors (id, author) VALUES (?, ?)");
                preparedStatement.setString(1, userIdGenerator());
                preparedStatement.setString(2, message.getAuthor());
                preparedStatement.executeUpdate();

                resultSet = statement.executeQuery("SELECT * FROM authors WHERE author = \"" + message.getAuthor() + "\"");
                resultSet.next();
                userId = resultSet.getString("id");
            }

            preparedStatement = connection.prepareStatement("INSERT INTO messages (id, author_id, text, date) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, message.getId());
            preparedStatement.setString(2, userId);
            preparedStatement.setString(3, message.getText());
            preparedStatement.setString(4, message.getDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    public void update(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("Update messages SET text = ? WHERE id = ?");
            preparedStatement.setString(1, message.getText());
            preparedStatement.setString(2, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    public void delete(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("DELETE  from messages WHERE id = ?");
            preparedStatement.setString(1, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    public Message selectById (Message message) {
        throw new UnsupportedOperationException();
    }

    public List<Message> selectAll() {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM messages INNER JOIN authors ON messages.author_id = authors.id ORDER BY date");
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String author = resultSet.getString("author");
                String text = resultSet.getString("text");
                String date = resultSet.getString("date");
                messages.add(new Message(id, author, text, date));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return messages;
    }
}
