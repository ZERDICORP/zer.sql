# zer.sql :page_facing_up:
#### A small module for working with relational databases using the JDBC driver.

## Part I ~ «Introduction» :cyclone:

Let's just create a new database (let's call it __example__) in which we add a table __users__ with 3 columns: __id__, __name__ and __age__.
```
MariaDB [(none)]> CREATE DATABASE example;
Query OK, 1 row affected (0.000 sec)

MariaDB [(none)]> USE example;
Database changed

MariaDB [example]> CREATE TABLE users(
    -> id INT NOT NULL AUTO_INCREMENT,
    -> name VARCHAR(255) NOT NULL,
    -> age INT NOT NULL,
    -> PRIMARY KEY(id));
Query OK, 0 rows affected (0.066 sec)
```

Let's also fill the table with 3 rows.
```
MariaDB [example]> INSERT INTO users (name, age) VALUES ("Alex", 20);
Query OK, 1 row affected (0.013 sec)

MariaDB [example]> INSERT INTO users (name, age) VALUES ("Bob", 22);
Query OK, 1 row affected (0.041 sec)

MariaDB [example]> INSERT INTO users (name, age) VALUES ("Steve", 18);
Query OK, 1 row affected (0.013 sec)

MariaDB [example]> SELECT * FROM users;
+----+-------+-----+
| id | name  | age |
+----+-------+-----+
|  1 | Alex  |  20 |
|  2 | Bob   |  22 |
|  3 | Steve |  18 |
+----+-------+-----+
3 rows in set (0.000 sec)
```

Okay, now we have everything to test our module.

## Part II ~ «Code» :raised_hands:

Project structure :deciduous_tree:
```
├── Main.java
├── models
│   └── Model_User.java
├── actions
│   ├── Action_GetUsers.java
│   └── Action_UpdateUserNameById.java
├── lib
│   └── mariadb-java-client-3.0.3.jar
└── zer
    └── sql
        ├── SQLAction.java
        ├── SQLConfig.java
        ├── SQLInjector.java
        └── SQLModel.java
```

The first thing we'll do is get all the records from the __users__ table.

So first, let's create a user model.

```java
/* models.Model_User.java */

package models;

import zer.sql.SQLModel;

public class Model_User extends SQLModel
{
  public String name;
  public int age;
  public int id;
}
```

Next, we need to create a class called __Action_GetUsers__ that extends the __SQLAction__ class.

```java
/* actions.Action_GetUsers.java */

package actions;

import zer.sql.SQLAction;

public class Action_GetUsers extends SQLAction
{
  {
    super.query("SELECT * FROM users");
  }
}
```

And finally, let's query the database via __SQLInjector__.

```java
/* Main.java */

import java.util.ArrayList;
import zer.sql.SQLInjector;
import zer.sql.SQLConfig;
import actions.Action_GetUsers;
import models.Model_User;

public class Main
{
  public static void main(String[] main)
  {
    /*
     * preparation of sql driver
     */

    SQLConfig.auth("root", "<password>");
    SQLConfig.connect("org.mariadb.jdbc.Driver", "jdbc:mariadb://localhost:3306/example?autoReconnect=true");

    /*
     * get all users & show in console
     */

    ArrayList<Model_User> users = SQLInjector.<Model_User>inject(Model_User.class, new Action_GetUsers());
    for (Model_User user : users)
    {
      System.out.print("id: " + user.id);
      System.out.print(", name: " + user.name);
      System.out.print(", age: " + user.age + "\n");
    }
  }
}
```
```
$ javac -cp lib/*:. Main.java && java -cp lib/*:. Main
id: 1, name: Alex, age: 20
id: 2, name: Bob, age: 22
id: 3, name: Steve, age: 18
```

Hooray!  
As a final note, let's change the name of the user with id 3.  
It's pretty easy. First, we create the corresponding action.  

```java
/* actions.Action_UpdateUserNameById.java */

package actions;

import zer.sql.SQLAction;

public class Action_UpdateUserNameById extends SQLAction
{
  {
    super.query("UPDATE users SET name = ? WHERE id = ?");
  }

  public Action_UpdateUserNameById(String name, int id)
  {
    put(name);
    put(id);
  }
}
```

It remains to throw this action into __SQLInjector__.

```java
/* Main.java */

import java.util.ArrayList;
import zer.sql.SQLInjector;
import zer.sql.SQLConfig;
import actions.Action_GetUsers;
import actions.Action_UpdateUserNameById;
import models.Model_User;

public class Main
{
  public static void showUsers(ArrayList<Model_User> users)
  {
    for (Model_User user : users)
    {
      System.out.print("id: " + user.id);
      System.out.print(", name: " + user.name);
      System.out.print(", age: " + user.age + "\n");
    }
  }

  public static void main(String[] main)
  {
    /*
     * preparation of sql driver
     */

    SQLConfig.auth("root", "<password>");
    SQLConfig.connect("org.mariadb.jdbc.Driver", "jdbc:mariadb://localhost:3306/example?autoReconnect=true");

    /*
     * get all users & show in the console
     */

    ArrayList<Model_User> users = SQLInjector.<Model_User>inject(Model_User.class, new Action_GetUsers());

    System.out.println("\n--- before name change ---\n");
    showUsers(users);

    /*
     * set name "Jack" for user with id 3
     */

    SQLInjector.inject(new Action_UpdateUserNameById("Jack", 3));

    /*
     * get all users & show in the console
     */

    users = SQLInjector.<Model_User>inject(Model_User.class, new Action_GetUsers());

    System.out.println("\n--- after name change ---\n");
    showUsers(users);
  }
}
```
```
$ javac -cp lib/*:. Main.java && java -cp lib/*:. Main

--- before name change ---

id: 1, name: Alex, age: 20
id: 2, name: Bob, age: 22
id: 3, name: Steve, age: 18

--- after name change ---

id: 1, name: Alex, age: 20
id: 2, name: Bob, age: 22
id: 3, name: Jack, age: 18
```
