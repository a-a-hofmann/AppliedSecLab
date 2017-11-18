# Applied Security Lab 

## General info
### Admin .p12 certificate
`ca/etc/ssl/CA/admin/admin.p12`

pwd: `admin`

### Useful links
#### Tools
- [IntelliJ](https://www.jetbrains.com/student/)
- [Postman](https://www.getpostman.com/)
- [Burp Suite](https://portswigger.net/burp)
- [Nessus Home Edition](https://www.tenable.com/products/nessus/nessus-plugins/obtain-an-activation-code)

#### Spring
Basic How-Tos:

- [Spring guide: How To Server Web Content](https://spring.io/guides/gs/serving-web-content/)
- [Spring guide: Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Spring guide: Consuming a RESTful Web Service](https://spring.io/guides/gs/consuming-rest/)

## Getting Started

### Setup
Import project into IDE (with IntelliJ)

- Splash screen: checkout from Version Control.
- From inside IDE File -> new -> Project from Version Control 

If you already cloned the project in some other way: 
 - Splash screen: import -> select root pom.xml
 - From inside IDE: File -> open -> select pom.xml


### To try stuff
To start getting your feet wet:

0. (Only first time or after `mvn clean`): `mvn clean install`
 
1. To start CA server either:
    - Start with maven from terminal: `cd asl/ca`; `mvn spring-boot:run`
    - start from IDE, find the CoreCaApplication class and run.
2. Point browser/postman @ `localhost:8081/user/db`.

If everything worked you should get this response:
 
```json
{
    "username": "db",
    "lastname": "db@imovies.ch",
    "firstname": "David",
    "email": "Basin"
}
```

Just for now, until we add log-in you can simply enter the username of the user you want to view as a path variable.
**Afterwards we will of course read the current user from the security context.**

### User credentials

| UserID | Password    |
|--------|-------------|
| db     | D15Licz6    |
| fu     | KramBamBuli |
| ms     | MidbSvlJ    |
| a3     | Astrid      |

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
with it as if it were a normal DB. After the server shutdowns, the DB will be dropped and simply 
recreated at the next start up.

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

## Current State of Affairs

![Status qup](https://github.com/a-a-hofmann/AppliedSecLab/blob/master/ca/src/main/resources/ca-diag.png) 
 
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

## SSL

Web server runs on port 8443.
You'll get 2 warnings for self-signed certs. 
One from web server, one from auth server.

### Keystore passwords
- gatewaykeystore.jks
```java
r1lI0vFPeprf2MCYyErDOAq1KrZmoQMTwFGHII6Z
```
- authsslkeystore.jks
```java
UnerWv30XsR6nQMRdifScKrrB0lqe3KaOF7TkeOB
```

Important: keys must be imported into java cacerts, otherwise it will not work.
Certs probably also have to be imported into browser/os (need to check).

`cacerts` is under `%JAVA_HOME%\jre\lib\security`. Default `cacerts` password is `changeit`
```bash
keytool -importkeystore -srckeystore "path\to\keystore.jks" -keystore cacerts
```

## Clean up before handing-in

- Make sure to remove Spring devtools (remove from maven and then clean install).
- Set a good pwd for all keystores.
- Switch embedded db with MySQL, remove DB web console.
- Check any default credentials