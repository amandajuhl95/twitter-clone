package dk.cphbusiness.mrv.twitterclone.impl;

import dk.cphbusiness.mrv.twitterclone.contract.PostManagement;
import dk.cphbusiness.mrv.twitterclone.dto.Post;
import dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostManagementImpl implements PostManagement {
    private Jedis jedis;
    private Time time;

    public PostManagementImpl(Jedis jedis, Time time) {
        this.jedis = jedis;
        this.time = time;
    }

    @Override
    public boolean createPost(String username, String message) {

        boolean exists = jedis.sismember("users", username);

        if(exists) {

            var timestamp = time.getCurrentTimeMillis();
            jedis.zadd("user/" + username + "/post", timestamp, message);
            List<Post> posts = this.getPosts(username);
            return true;
        }

        return false;
    }

    @Override
    public List<Post> getPosts(String username) {

        List<Post> posts = new ArrayList();
        boolean exists = jedis.sismember("users", username);

        if(exists) {

            Set<Tuple> tuples = jedis.zrangeByScoreWithScores("user/" + username + "/post", 0, time.getCurrentTimeMillis());

            for (Tuple tuple : tuples) {
                Post post = new Post(Math.round(tuple.getScore()), tuple.getElement());
                posts.add(post);
            }

            return posts;
        }

        return null;

    }

    @Override
    public List<Post> getPostsBetween(String username, long timeFrom, long timeTo) {
        List<Post> posts = new ArrayList();
        boolean exists = jedis.sismember("users", username);

        if(exists) {

            Set<Tuple> tuples = jedis.zrangeByScoreWithScores("user/" + username + "/post", timeFrom, timeTo);

            for (Tuple tuple : tuples) {
                Post post = new Post(Math.round(tuple.getScore()), tuple.getElement());
                posts.add(post);
            }

            return posts;
        }

        return null;
    }
}
