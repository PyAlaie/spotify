package com.ap.spotify.shared.crudFiles;

import com.ap.spotify.server.Database;
import com.ap.spotify.shared.models.Comment;
import com.ap.spotify.shared.models.Music;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudComment {
    private Database database;

    public CrudComment(Database database) {
        this.database = database;
    }

    public void newComment(Comment comment) throws SQLException {
        String query = "INSERT INTO comments (text, \"user\", music) VALUES (?,?,?)";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setString(1, comment.getText());
        statement.setInt(2, comment.getUser());
        statement.setInt(3, comment.getMusic());

        statement.executeUpdate();
    }

    public List<Comment> getCommentsOfMusic(int musicId) throws SQLException {
        String query = "SELECT * FROM comments WHERE music=?";

        PreparedStatement statement = database.getConnection().prepareStatement(query);
        statement.setInt(1, musicId);

        ResultSet res = statement.executeQuery();
        List<Comment> comments = new ArrayList<>();

        while(res.next()){
            Comment comment = new Comment();
            comment.setText(res.getString("text"));
            comment.setUser(res.getInt("user"));
            comment.setMusic(res.getInt("music"));
            comment.setId(res.getInt("id"));
            comments.add(comment);
        }

        return comments;
    }
}
