## Twitter-clone

### Made by Amalie, Amanda, Benjamin

We have added a constructor to the DTO called UserOverview. 

This is the link for [UserManagementImpl](https://github.com/amandajuhl95/twitter-clone/blob/main/src/main/java/dk/cphbusiness/mrv/twitterclone/impl/UserManagementImpl.java)

This is the link for [PostManagementImpl](https://github.com/amandajuhl95/twitter-clone/blob/main/src/main/java/dk/cphbusiness/mrv/twitterclone/impl/PostManagementImpl.java)

### Short explanation of Our redis Data Model

Our Redis database have a list of users containing usernames of all users registered. 

Each user is represented by username as key as follows
- user/{INSERT USERNAME}/firstname
- user/{INSERT USERNAME}/lastname
- user/{INSERT USERNAME}/passwordhash
- user/{INSERT USERNAME}/birthday

Besides this we have a value called "userCount" which is incremented everytime a user is created. 

Each user have a set of followers and following:
- user/{INSERT USERNAME}/following
- user/{INSERT USERNAME}/followers

Furthermore each user has a sorted set of posts containing the messages of the post and sorted by timestamp:
- user/{INSERT USERNAME}/post





