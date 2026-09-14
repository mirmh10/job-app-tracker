# Task: Build a Job Application Tracker

## What You're Building

A small web app where you can **add, view, edit, and delete** job applications you've sent out. You'll use it yourself while job hunting, so make it actually useful.

When you're done, you should be able to:
- Add a new application (company name, role, date applied, link, status)
- See all your applications in a table
- Edit an application (e.g. change status from "Applied" to "Interview")
- Delete an application you no longer care about

That's it. These four operations — **Create, Read, Update, Delete** — are called **CRUD**, and they are the backbone of almost every app in existence. Instagram? CRUD for posts. Notion? CRUD for pages. A bank? CRUD for transactions.

---

## Your Tools

You are NOT coding this from scratch. You're using:

1. **Claude or Cursor** — to generate all the code for you
2. **GitLab** — to store your project, track changes, and work with branches (like a real dev team)
3. **Git** (command line) — to commit and push your changes
4. **glab** (GitLab CLI) — to create merge requests, merge branches, and manage your repo from the terminal
5. **Your brain** — to describe what you want, check the output, and fix issues
6. **A browser** — to test everything

---

## Step-by-Step Instructions

### Step 1: Understand the Data

Before you build anything, you need to know what data you're working with. Each job application has:

| Field        | Type     | Example                        |
|--------------|----------|--------------------------------|
| id           | number   | 1, 2, 3 (auto-generated)      |
| company      | text     | "Exxeta AG"                    |
| role         | text     | "Werkstudent Automatisierung"  |
| dateApplied  | date     | "2026-09-10"                   |
| url          | text     | "https://jobs.exxeta.com/..."  |
| status       | choice   | "Applied" / "Interview" / "Rejected" / "Offer" |
| notes        | text     | "Heard back, interview Sept 20"|

This is your **data model**. Every CRUD app starts with defining what the "thing" is that you're creating, reading, updating, and deleting.

### Step 2: Set Up GitLab and Your Branching Workflow

Before you write a single line of code, you set up your project on GitLab. This is how every professional team works: code lives in a repository, changes happen on branches, and nothing goes into the main codebase without being reviewed.

**2a) Create the GitLab repo:**

1. Go to [gitlab.com](https://gitlab.com) and create an account (if you don't have one)
2. Click "New project" → "Create blank project"
3. Name it `job-application-tracker`
4. Set visibility to Private (or Public if you want it in your portfolio)
5. Check "Initialize repository with a README"
6. Click "Create project"

**2b) Clone it to your computer:**

Open your terminal and run:

```bash
git clone https://gitlab.com/YOUR_USERNAME/job-application-tracker.git
cd job-application-tracker
```

Now you have a local copy of the project on your machine, connected to GitLab.

**2b-extra) Install the `glab` CLI:**

`glab` is GitLab's command-line tool. Instead of opening your browser every time you need to create a merge request, you do it right from the terminal. Install it:

- **Mac:** `brew install glab`
- **Windows:** `winget install GLab.GLab`
- **Linux:** `sudo apt install glab` (or check [the docs](https://gitlab.com/gitlab-org/cli#installation))

Then authenticate:

```bash
glab auth login
```

Follow the prompts, pick `gitlab.com`, and log in. After this, `glab` can talk to your GitLab repo directly.

**2c) Create the `dev` branch:**

The `main` branch is sacred. It should always contain working, finished code. You never work directly on `main`. Instead, you create a `dev` (development) branch where integration happens, and **feature branches** off of `dev` for each piece of work.

```bash
git checkout -b dev
git push -u origin dev
```

You now have two branches on GitLab: `main` and `dev`. From here on, all your work starts from `dev`.

**2d) Understand the branching workflow:**

Here's the rule you'll follow for every piece of work:

```
main          (stable, finished code only)
  └── dev     (integration branch, your "working" version)
       ├── feature/frontend-form      (Step 3 work)
       ├── feature/crud-logic         (Step 4 work)
       ├── feature/quality-touches    (Step 5 work)
       └── ...
```

The flow for each feature:
1. **Branch off `dev`** — `git checkout dev` then `git checkout -b feature/your-feature-name`
2. **Do your work** — write code, test it
3. **Commit often** — save snapshots as you go
4. **Push the branch** — upload it to GitLab
5. **Create a Merge Request (MR)** — using `glab mr create` from the terminal
6. **Merge it** — `glab mr merge`, then delete the feature branch
7. **Repeat** for the next feature

This is exactly how software teams work at real companies. Learning this workflow is as valuable as the code itself.

**Why branches?** Imagine you're working on a new feature and it breaks everything. If you worked directly on `dev`, your whole project is now broken. But on a feature branch, the broken code is isolated. `dev` still works fine. You can fix the issue on your branch, or even delete it and start over, without affecting anything else.

### Step 3: Build the Frontend

**Git first!** Before you touch any code, create your feature branch:

```bash
git checkout dev
git checkout -b feature/frontend-form
```

You're now on a branch called `feature/frontend-form`, branched off `dev`. Everything you do here is isolated.

Ask Claude to generate a single HTML file with:

- A **form** at the top to add a new application (inputs for each field above)
- A **table** below the form showing all applications
- Each row in the table should have an **Edit** button and a **Delete** button
- When you click Edit, the form fills with that application's data so you can change it and save
- A **status filter** — a dropdown above the table that filters by status (All / Applied / Interview / Rejected / Offer)

**Example prompt to give Claude:**

> Build me a single-page HTML/CSS/JS application for tracking job applications. The data fields are: company (text), role (text), dateApplied (date), url (text), status (dropdown: Applied, Interview, Rejected, Offer), and notes (textarea). I need a form to add/edit entries, a table that shows all entries with Edit and Delete buttons per row, and a status filter dropdown above the table. Store data in a JavaScript array in memory. Use a clean, modern design with a white background, subtle shadows on cards, and a blue accent color. Make it responsive.

**Once the frontend looks right, commit and push:**

```bash
git add .
git commit -m "feat: add frontend form and application table"
git push -u origin feature/frontend-form
```

Now create a Merge Request straight from the terminal:

```bash
glab mr create --base dev --title "feat: frontend form and application table" --description "Added the HTML form, table layout, and status filter"
```

This creates an MR targeting `dev`. You can review it with `glab mr view` to see the diff — what files changed and what was added. When it looks good:

```bash
glab mr merge
```

Done. Your frontend code is now in `dev`. The feature branch gets deleted automatically.

### Step 4: Make It Work — The CRUD Logic

**New feature, new branch:**

```bash
git checkout dev
git pull origin dev
git checkout -b feature/crud-logic
```

Notice the `git pull` — this updates your local `dev` with whatever was just merged (your frontend code). Always pull before branching so you start from the latest version.

Here's what each operation does, in simple terms:

**Create (the C):**
When you fill out the form and click "Add", the app takes all the input values, packages them into an object (like a JSON block), assigns it an ID, and adds it to the list. The table refreshes to show the new entry.

**Read (the R):**
The table on the page IS the read operation. It loops through all stored applications and displays each one as a row. The filter is also a read — it reads the same data but only shows entries matching the selected status.

**Update (the U):**
When you click "Edit" on a row, the app finds that entry by its ID, fills the form with its current data, and switches the button from "Add" to "Save Changes." When you click Save, it replaces the old data with the new data. The table refreshes.

**Delete (the D):**
When you click "Delete" on a row, the app finds that entry by its ID, removes it from the list, and refreshes the table. Usually you want a confirmation popup ("Are you sure?") so you don't delete things by accident.

**All four operations working? Commit, push, and create a Merge Request — same flow as before:**

```bash
git add .
git commit -m "feat: implement CRUD operations for applications"
git push -u origin feature/crud-logic
```

Create and merge the MR:

```bash
glab mr create --base dev --title "feat: CRUD operations" --description "Implemented create, read, update, delete for applications"
glab mr merge
```

**Pro tip on commit messages:** Keep them short and start with a verb. Good: `"feat: add delete confirmation popup"`. Bad: `"updated stuff"`. Your commit history is a logbook of what happened in the project. Future-you will thank present-you for writing clear messages.

### Step 5: Add a Few Quality Touches

**New branch again:**

```bash
git checkout dev
git pull origin dev
git checkout -b feature/quality-touches
```

Once the basics work, ask Claude to add:

1. **Local Storage** — so your data doesn't disappear when you refresh the page. Prompt: "Save the applications array to localStorage whenever it changes, and load it when the page opens."

2. **Status badges** — colored labels instead of plain text. "Applied" = blue, "Interview" = yellow, "Rejected" = red, "Offer" = green.

3. **Sort by date** — newest applications at the top.

4. **Empty state** — when there are no applications yet, show a friendly message instead of an empty table: "No applications yet. Add your first one above!"

5. **Application count** — show "Showing 3 of 12 applications" above the table (respecting the filter).

**When all five touches work, commit, push, and merge into `dev` — you know the drill by now.**

```bash
git add .
git commit -m "feat: add localStorage, status badges, sorting, empty state, count"
git push -u origin feature/quality-touches
```

```bash
glab mr create --base dev --title "feat: quality touches" --description "localStorage, status badges, sort by date, empty state, application count"
glab mr merge
```

### Step 6: Test Everything

Go through this checklist:

- [ ] Can you add a new application and see it in the table?
- [ ] Can you add multiple applications?
- [ ] Can you click Edit, change the status, and save it?
- [ ] Can you delete an application? Does a confirmation appear?
- [ ] Does the status filter work? Does "All" show everything?
- [ ] If you refresh the page, is your data still there?
- [ ] What happens if you submit the form with empty fields? (It shouldn't let you.)
- [ ] Does it look okay on a phone screen?

If any of these fail, create a `fix/` branch (e.g. `fix/delete-not-working`), fix the issue, commit, push, merge into `dev`. Same flow, just with `fix/` instead of `feature/`.

```bash
git checkout dev
git pull origin dev
git checkout -b fix/delete-not-working
# ... fix the issue ...
git add .
git commit -m "fix: delete button now shows confirmation before removing"
git push -u origin fix/delete-not-working
glab mr create --base dev --title "fix: delete confirmation"
glab mr merge
```

### Step 7: Merge `dev` into `main`

Everything works? All tests pass? Time to promote your code to `main`.

```bash
git checkout dev
glab mr create --base main --title "v1.0: Job Application Tracker" --description "Fully functional CRUD app with localStorage, status badges, filtering, and sorting"
glab mr merge
```

This final MR is your proof of work. When someone (or a future employer) opens your GitLab repo, they'll see:
- A clean `main` branch with a working app
- A merge history showing structured feature branches
- Commit messages that tell the story of how you built it

That's professional-grade workflow, even for a first project.

---

## Stretch Goals (Bonus)

If the basics are done and you want to go further (each one gets its own `feature/` branch off `dev`!):

- **Export to CSV** — add a button that downloads all applications as a .csv file you can open in Excel
- **Dark mode toggle** — a switch that changes the color scheme
- **Stats section** — show a small summary: "12 total, 5 applied, 4 interviews, 2 rejected, 1 offer"
- **Search bar** — type a company name to filter the table instantly

---

## What You're Actually Learning

After finishing this task, you'll understand:

| Concept | Where you used it |
|---|---|
| HTML structure | The form, the table, the buttons |
| CSS styling | Layout, colors, responsiveness |
| JavaScript basics | Variables, arrays, objects, functions, event listeners |
| CRUD operations | The four core operations every app needs |
| Data modeling | Defining what a "job application" looks like as data |
| State management | Keeping the data in sync between the form, the table, and localStorage |
| Git version control | Committing changes, writing commit messages, pushing to remote |
| Branching workflow | Feature branches off `dev`, merge requests, keeping `main` clean |
| GitLab + glab CLI | Remote repository, merge requests from the terminal, merge history |
| Debugging | Fixing what breaks along the way |
| AI-assisted building | You described what you wanted and refined the output |

---

## Evaluation — How to Know It's Done

Show the finished app to Hayk and walk him through it. You should be able to:

1. **Demo it** — open the page, add 3 applications, edit one, delete one, filter by status
2. **Explain the data model** — "each application has these fields, stored as an object"
3. **Explain CRUD** — point at the code and say "this part adds, this reads, this updates, this deletes"
4. **Walk through the GitLab repo** — run `glab mr list --state merged` to show the merge request history, explain the branch names, show how `feature/frontend-form` became part of `dev` and then `main`
5. **Show a bug you fixed** — "this thing broke, I got this error, I asked Claude, and here's what was wrong" — bonus if you can show the `fix/` branch for it

If you can do all five, you're ready for the next task.

---

*Time estimate: 3–5 hours for the basics, another 2–3 for the stretch goals.*
