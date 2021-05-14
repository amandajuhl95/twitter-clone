package dk.cphbusiness.mrv.twitterclone.impl;

import dk.cphbusiness.mrv.twitterclone.contract.UserManagement;
import dk.cphbusiness.mrv.twitterclone.dto.UserCreation;
import dk.cphbusiness.mrv.twitterclone.dto.UserOverview;
import dk.cphbusiness.mrv.twitterclone.dto.UserUpdate;
import dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserManagementImpl implements UserManagement {

    private Jedis jedis;

    public UserManagementImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean createUser(UserCreation userCreation) {

        boolean created = false;
        boolean exists = jedis.sismember("users", userCreation.username);

        if(!exists) {

            Transaction transaction = jedis.multi();

            transaction.set("user/" + userCreation.username + "/firstname", userCreation.firstname);
            transaction.set("user/" + userCreation.username + "/lastname", userCreation.lastname);
            transaction.set("user/" + userCreation.username + "/passwordhash", userCreation.passwordHash);
            transaction.set("user/" + userCreation.username + "/birthday", userCreation.birthday);

            transaction.sadd("users", userCreation.username);
            transaction.incr("usercount");


            transaction.exec();
            created = jedis.sismember("users", userCreation.username);

        }

        return created;
    }

    @Override
    public UserOverview getUserOverview(String username) {

        UserOverview overview = null;
        boolean exists = jedis.sismember("users", username);

        if(exists)  {

            Transaction transaction = jedis.multi();
            Response<String> firstname = transaction.get("user/" + username + "/firstname");
            Response<String> lastname  = transaction.get("user/" + username + "/lastname");
            Response<Set<String>> followers  = transaction.smembers("user/" + username + "/followers");
            Response<Set<String>> following  = transaction.smembers("user/" + username + "/following");
            transaction.exec();

            overview = new UserOverview(username, firstname.get(), lastname.get(), followers.get().size(), following.get().size());

        }

        return overview;
    }

    @Override
    public boolean updateUser(UserUpdate userUpdate) {

        boolean exists = jedis.sismember("users", userUpdate.username);

        if(exists) {

            Transaction transaction = jedis.multi();

            if(userUpdate.firstname != null && userUpdate.firstname.length() >= 2) {
                transaction.set("user/" + userUpdate.username + "/firstname", userUpdate.firstname);
            }
            if(userUpdate.lastname != null && userUpdate.lastname.length() >= 2) {
                transaction.set("user/" + userUpdate.username + "/lastname", userUpdate.lastname);
            }
            if(userUpdate.birthday != null) {
                transaction.set("user/" + userUpdate.username + "/birthday", userUpdate.birthday);
            }

            transaction.exec();
            return true;
        }

        return false;

    }

    @Override
    public boolean followUser(String username, String usernameToFollow) {

        List<Boolean> exists = jedis.smismember("users", username, usernameToFollow);

        if(!exists.contains(false)) {
            Transaction transaction = jedis.multi();

            transaction.sadd("user/" + username + "/following", usernameToFollow);
            transaction.sadd("user/" + usernameToFollow + "/followers", username);

            transaction.exec();

            return true;
        }

        return false;

    }

    @Override
    public boolean unfollowUser(String username, String usernameToUnfollow) {

        List<Boolean> exists = jedis.smismember("users", username, usernameToUnfollow);

        if(!exists.contains(false)) {
            Transaction transaction = jedis.multi();

            transaction.srem("user/" + username + "/following", usernameToUnfollow);
            transaction.srem("user/" + usernameToUnfollow + "/followers", username);

            transaction.exec();

            return true;
        }

        return false;

    }

    @Override
    public Set<String> getFollowedUsers(String username) {

        boolean exists = jedis.sismember("users", username);

        if(exists)  {

            Set<String> following = jedis.smembers("user/" + username + "/following");
            return following;
        }

        return null;

    }

    @Override
    public Set<String> getUsersFollowing(String username) {
        boolean exists = jedis.sismember("users", username);

        if(exists)  {

            Set<String> followers = jedis.smembers("user/" + username + "/followers");
            return followers;
        }

        return null;

    }

}
