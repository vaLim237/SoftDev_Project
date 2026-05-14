# ChaiWan POS

ChaiWan POS is a Java Swing point-of-sale and inventory management system for a cafe setup. It includes a login screen, role-based access for Barista and Manager/Owner users, inventory management, transaction handling, and dashboard views for store operations.

## How to Run

Open a terminal in the project root folder, the one that contains `src` and `bin`, then run:

```powershell
javac -encoding UTF-8 -d bin src\Finals\*.java
java -cp bin Finals.LoginPage
```

If you get a module version error from old compiled files, delete the stale class and run the commands again:

```powershell
Remove-Item -Force bin\module-info.class
```

## Default Login Accounts

- `admin@usc.edu.ph` / `dreamteam101`
- `manager@usc.edu.ph` / `dreamteam101`
- `barista@usc.edu.ph` / `dreamteam101`
- `denzel@usc.edu.ph` / `dreamteam101`
- `valerie@usc.edu.ph` / `dreamteam101`
- `dreamteam@usc.edu.ph` / `dreamteam101`