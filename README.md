# AppliedSecLab 

## General info

Basic auth: `user:password`

## Accessing the DB with Spring Data Repositories
Documentation:
[Spring Data docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation)

How to define a repository to access the DB:
```java
@Repository // 1
public interface UserRepository extends JpaRepository<User, String> { // 2

    User findByUsername(String username); // 3
}
```
1. `@Repository` defines a Spring Data repository.
2. It has to extend `JpaRepository<Entity, ID>`, where `Entity` is the class we persist and `ID` 
is the type of id in `Entity`
3. This will create the following prepared statement: `SELECT * FROM Users WHERE username = ${username}`. 
`${username}` will be replaced by the method parameter.
The method will return an already parsed User object (no need to parse ResultSet or stuff like that). 

The repository can then be accessed by *injecting* it into another class as follows:

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```
Then it can simply be used even if we did not implement the interface.

### DB
For development there is an embedded DB (H2). 
When you start an instance of the `ca` server it will load the data from `data.sql` and you can interact 
with it as if it were a normal DB.

If you want to view the content of the DB, open any browser and goto `http://localhost:8081/h2-console/`.
Leave the default values in the form, but make sure that:

```
JDBC URL = jdbc:h2:mem:ca
username = sa
password = 
```
After connecting you should see a `USERS` table on the left side.

### Projections
Given an entity:

```java
@Entity(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "uid")
    private String username;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "email")
    private String email;

    @Column(nullable = false, name = "pwd")
    private String password;
}
```

We can define a projection of this entity:

```java
public class UserSafeProjection implements Serializable {

    private final String username;

    private final String lastname;

    private final String firstname;

    private final String email;
    
    public UserSafeProjection(String username, String lastname, String firstname, String email) {
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }
}
```

*Important*: the fields of the projection must be named exactly as they are defined in the entity.

We can then get a projection by setting the return type of the query method in a repository as the projection instead of the entity.

```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    UserSafeProjection findByUsername(String username);
}
``` 
 
## JSON Web Tokens (JWT)
The Authorization Server (AS) produces signed JWT containing the users claims.
Config for signing is under `OAuth2Config.java` in the `auth` module. 
There is already a sample keystore in the root of the project `jwt_keystore.jks`.

To create a new one:

```bash
keytool -genkeypair -alias <my-alias> -keyalg RSA -keystore <new-keystore-name>.jks
keytool -list -rfc --keystore <new-keystore-name>.jks > junk.txt
openssl x509 -inform pem -pubkey -in junk.txt
```

Replace test public key in Resource Servers `application.properties` with the one in `junk.txt`.
Change `OAuth2Config.java` class to reflect the change.
Don't forget to delete `junk.txt` afterwards.