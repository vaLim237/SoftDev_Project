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

## Current Missing Features

The following features are still lacking as of the moment:

- Automated sales report
- Best seller analysis
- API support
- Transaction editing and deletion

## Team Git Workflow

Team members should use Git properly to avoid conflicts and lost changes:

1. Pull the latest changes before starting work.
2. Make changes on the updated codebase.
3. Commit with a clear message after finishing a task.
4. Push your changes so the rest of the team can get the latest version.
5. Pull again before editing if someone else updated the repository.

This helps keep the project synchronized and reduces merge conflicts.

## Git Installed On This Machine

The current Git version installed on this Windows machine is:

- `git version 2.52.0.windows.1`

## Basic Git Tutorial

1. Open a terminal inside the project folder.
2. Check your changes with `git status`.
3. Pull the latest updates first with `git pull`.
4. Edit your files.
5. Check changes again with `git status`.
6. Add files for commit with `git add .` or `git add <file>`.
7. Save your work with `git commit -m "your message here"`.
8. Send your changes to the repository with `git push`.

Useful commands:

- `git status` shows what changed.
- `git pull` downloads the latest changes.
- `git add .` stages all changes.
- `git commit -m "message"` creates a commit.
- `git push` uploads your commit.
- `git log --oneline` shows commit history.