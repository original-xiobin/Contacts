Here is the README text in plain form:

```md
# Contacts

A small Kotlin command-line phone book for storing people and organizations in a local JSON file.

## Features

- Add `person` and `organization` records
- List all records
- Search by name or phone number
- View record details
- Edit or delete a record
- Persist data to a file between runs

## Tech Stack

- Kotlin
- Gradle
- Moshi for JSON serialization
- `kotlinx-datetime` for timestamps

## Requirements

- JDK 21
- Gradle Wrapper included in the repository

## Run

If you have a JSON file, pass its name as the first argument:
```bash
./gradlew run --args="contacts.json"
```
If no file is provided, the app uses `temp.txt` in the project directory.

## Usage

After launch, the app waits for commands in the terminal:

- `add`
- `list`
- `search`
- `count`
- `exit`

### Add a record

Choose the record type:

- `person`
- `organization`

For a person, the app asks for:

- name
- surname
- birth date
- gender
- phone number

For an organization, the app asks for:

- organization name
- address
- phone number

### List and search

`list` shows all records.

`search` lets you filter records by a text query. After a list or search result is shown, you can open a record by entering its number, then:

- `edit`
- `delete`
- `menu`

## Persistence

Records are stored in the file provided on startup. If the file does not exist, the app starts with an empty contact list and creates the file when data is saved.

## Notes

- Phone numbers are validated before saving.
- Dates are validated as ISO-style dates.
- The project is designed as a simple interactive terminal app.
```

One correction: the fenced `bash` block above is nested inside the markdown block, so if you want the cleanest copy for GitHub, use this version instead:
```md
# Contacts

A small Kotlin command-line phone book for storing people and organizations in a local JSON file.

## Features

- Add `person` and `organization` records
- List all records
- Search by name or phone number
- View record details
- Edit or delete a record
- Persist data to a file between runs

## Tech Stack

- Kotlin
- Gradle
- Moshi for JSON serialization
- `kotlinx-datetime` for timestamps

## Requirements

- JDK 21
- Gradle Wrapper included in the repository

## Run

If you have a JSON file, pass its name as the first argument:
`./gradlew run --args="contacts.json"`
If no file is provided, the app uses `temp.txt` in the project directory.

## Usage

After launch, the app waits for commands in the terminal:

- `add`
- `list`
- `search`
- `count`
- `exit`

### Add a record

Choose the record type:

- `person`
- `organization`

For a person, the app asks for:

- name
- surname
- birth date
- gender
- phone number

For an organization, the app asks for:

- organization name
- address
- phone number

### List and search
`list` shows all records.

`search` lets you filter records by a text query. After a list or search result is shown, you can open a record by entering its number, then:

- `edit`
- `delete`
- `menu`

## Persistence

Records are stored in the file provided on startup. If the file does not exist, the app starts with an empty contact list and creates the file when data is saved.

## Notes

- Phone numbers are validated before saving.
- Dates are validated as ISO-style dates.
- The project is designed as a simple interactive terminal app.
```
