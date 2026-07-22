package org.xiobin

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*

var isRunning = true
var filename: String = ""

fun main(args: Array<String>) {
    loadData(args)
    while (isRunning) {
        menu()
    }
}

fun menu() {
    println("Enter action (add, list, search, count, exit): ")
    when(readln()) {
        "add" -> Contacts.add()
        "list" -> Contacts.list()
        "search" -> Contacts.search()
        "count" -> Contacts.count()
        "exit" -> exit()
        else -> "Invalid action!"
    }
    println()
}

fun exit(){
    isRunning = false
}

fun loadData(args: Array<String>) {
    if (args.isNotEmpty()) {
        try {
            Contacts.loadOrSave("load", args[0])
            filename = args[0]
        } catch (e: FileNotFoundException) {
            filename = "temp.txt"
        }
    } else filename = "temp.txt"
}

class Contacts{

    companion object{
        private val contactList: MutableList<Contact> = mutableListOf()

        fun getContacts(): MutableList<Contact> = contactList

        private fun add(contact: Contact) {
            contactList.add(contact)
            println("The record added.")
        }

        fun add() {
            println("Enter the type (person, organization): ")
            when(readln()) {
                "person" -> addPerson()
                "organization" -> addOrganization()
                else -> println("Invalid type.")
            }
        }

        private fun addPerson() {
            val builder = PersonBuilder()
            println("Enter the name: ")
            builder.setFirstname(readln())
            println("Enter the surname: ")
            builder.setLastname(readln())
            println("Enter the birth date: ")
            builder.setBirthdate(readln())
            println("Enter the gender (M, F): ")
            builder.setGender(readln())
            println("Enter the number: ")
            builder.setPhone(readln())
            add(builder.build())
        }

        private fun addOrganization() {
            val builder = OrganizationBuilder()
            println("Enter the organization name: ")
            builder.setName(readln())
            println("Enter the address: ")
            builder.setAddress(readln())
            println("Enter the number: ")
            builder.setPhone(readln())
            add(builder.build())
        }

        fun remove(index: Int) {
            if (size() == 0) {
                println("No records to remove!")
            } else {
                if (size() >= index && index >= 0) {
                    contactList.removeAt(index)
                    println("The record removed!")
                } else {
                    println("No records to remove!")
                }
            }
        }

        private fun size(): Int {
            return contactList.size
        }

        fun count() {
            println("The Phone Book has ${size()} records.")
        }

        fun list() {
            showContacts(contactList, "No records to list!")
            listMenu()
        }

        private fun showContacts(contacts: List<Contact>, noRecordsText: String) {
            if (size() > 0) {
                var count = 1
                for (contact in contacts) {
                    if (contact is Person) {
                        val p = contact as Person
                        println("${count}. ${p.getFirstName()} ${p.getLastName()}")
                    } else if (contact is Organization){
                        val o = contact as Organization
                        println("${count}. ${o.getName()}")
                    }
                    count++
                }
            } else {
                println(noRecordsText)
            }
        }

        fun search() {
            println("Enter search query: ")
            val query = readln().lowercase(Locale.getDefault())
            val regex: Regex = ".*${query}.*".toRegex()
            val list = contactList.filter { it ->
                when (it) {
                    is Person -> regex.matches(
                        it.getFirstName().lowercase(Locale.getDefault())
                                + " " + it.getLastName().lowercase(Locale.getDefault())
                    ) || regex.matches(it.getPhone())
                    is Organization -> regex.matches(it.getName().lowercase(Locale.getDefault())) ||
                            regex.matches(it.getPhone())
                    else -> false
                }
            }
            if (list.isNotEmpty()) {
                if (list.size == 1) println("Found 1 result: ")
                else println("Found ${list.size} results: ")
                showContacts(list, "")
            } else println("Found 0 results.\n")
            listMenu()
        }

        private fun listMenu() {
            var position = -1
            println("\nEnter action ([number], back): ")
            try {
                position = readln().toInt()
            } catch (e: Exception){
                println("Invalid input.")
            }
            val index = position - 1
            if (index >= 0 && position <= size()) {
                if (contactList[index] is Person) {
                    showPerson(contactList[index] as Person, index)
                } else if (contactList[index] is Organization) {
                    showOrganization(contactList[index] as Organization, index)
                }
            }
        }

        private fun recordMenu(index: Int) {
            println("\nEnter action (edit, delete, menu): ")
            when (readln()) {
                "edit" -> edit(index)
                "delete" -> remove(index)
                "menu" -> menu()
            }
        }

        fun isPhoneValid(phoneNumber: String): Boolean {
            val phoneRegex = ("^(((\\+?[(][0-9a-zA-Z]{1,5}[)])[\\s\\-]?)?(\\+?[0-9a-zA-Z]{2,10})?|" +
                    "((\\+?[0-9a-zA-Z]{1,5})[\\s\\-]?)?(\\+?[(]?[0-9a-zA-Z]{2,10}[)]?)?[\\s\\-]?)" +
                    "([0-9a-zA-Z]{2,4}[\\s\\-])?([0-9a-zA-Z]{2,4}[\\s\\-]){0,4}([0-9a-zA-Z]{2,5})?").toRegex()
            return phoneRegex.matches(phoneNumber)
        }

        private fun showPerson(person: Person, index: Int) {
            println("Name: ${person.getFirstName()}")
            println("Surname: ${person.getLastName()}")
            println("Birth date: ${person.getBirthdate()}")
            println("Gender: ${person.getGender()}")
            println("Number: ${person.getPhone()}")
            println("Time created: ${person.getCreated()}")
            println("Time last edit: ${person.getModified()}")
            recordMenu(index)
        }

        private fun showOrganization(organization: Organization, index: Int) {
            println("Organization name: ${organization.getName()}")
            println("Address: ${organization.getAddress()}")
            println("Number: ${organization.getPhone()}")
            println("Time created: ${organization.getCreated()}")
            println("Time last edit: ${organization.getModified()}")
            recordMenu(index)
        }

        fun edit(position: Int) {
            if (position >= 0) {
                if (contactList[position] is Person) {
                    val p = contactList[position] as Person
                    editPerson(p)
                } else if (contactList[position] is Organization) {
                    val o = contactList[position] as Organization
                    editOrganization(o)
                }
            }
        }

        fun editPerson(person: Person) {
            println("Select a field (name, surname, birth, gender, number): ")
            when (readln()) {
                "name" -> {
                    println("Enter name: ")
                    person.setFirstName(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                "surname" -> {
                    println("Enter surname: ")
                    person.setLastName(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                "birth" -> {
                    println("Enter birth: ")
                    person.setBirthdate(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                "gender" -> {
                    println("Enter gender: ")
                    person.setGender(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                "number" -> {
                    println("Enter number: ")
                    person.setPhone(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                else -> println("Invalid action.")
            }
        }

        private fun editOrganization(organization: Organization) {
            println("Select a field (name, address, number): ")
            when (readln()) {
                "name" -> {
                    println("Enter address: ")
                    organization.setName(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                "address" -> {
                    println("Enter address: ")
                    organization.setAddress(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                "number" -> {
                    println("Enter number: ")
                    organization.setPhone(readln())
                    println("The record updated!")
                    loadOrSave("save", filename)
                }
                else -> println("Invalid action.")
            }
        }

        fun loadOrSave(action: String, filename: String) {
            val separator: String = File.separator
            val workingDirectory = System.getProperty ("user.dir")
            val file = File("${workingDirectory}${separator}${filename}")
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val type = Types.newParameterizedType(List::class.java, ContactDto::class.java)
            val adapter = moshi.adapter<List<ContactDto>>(type)
            when (action) {
                "load" -> {
                    val json = adapter.fromJson(file.readText()) ?: emptyList<ContactDto>()
                    if (json.isNotEmpty()) {
                        for (contactDto in json) {
                            contactDto.toContact()?.let { contactList.add(it) }
                        }
                    }
                }
                "save" -> {
                    val json = adapter.toJson(contactList.map { it.toDto() })
                    file.writeText(json)
                }
            }
        }
    }
}

open class Contact(
    val phoneNumber: String,
    private var created: LocalDateTime = currentDateTime(),
    private var modified: LocalDateTime = created
) {
    private var tz: TimeZone = TimeZone.currentSystemDefault()
    private var phonenumber = ""
        get() {
            return field
        }
        set(value) {
            if (Contacts.isPhoneValid(value)) {
                field = value
            } else {
                field = ""
                println("Wrong number format!")
            }
        }

    init {
        try {
            require(Contacts.isPhoneValid(phoneNumber))
            this.phonenumber = phoneNumber
        } catch (e: IllegalArgumentException) {
            println("Wrong number format!")
        }
    }

    fun hasNumber(): Boolean {
        return phonenumber != ""
    }

    fun getPhone(): String = this.phonenumber

    fun setPhone(number: String) {
        if (Contacts.isPhoneValid(number)) {
            this.phonenumber = number
        }
    }

    fun getCreated(): LocalDateTime = this.created

    fun getModified(): LocalDateTime = this.modified

    fun setModified(datetime: LocalDateTime) {
        this.modified = datetime
    }
}

class Person (private var firstname: String, private var lastname: String,
              private var birthdate: String, private var gender: String,
              phoneNumber: String,
              created: LocalDateTime = currentDateTime(),
              modified: LocalDateTime = created) : Contact(phoneNumber, created, modified) {
    fun getFirstName(): String = this.firstname
    fun getLastName(): String = this.lastname
    fun getBirthdate(): String = this.birthdate
    fun getGender(): String = this.gender
    fun setFirstName(name: String) {
        this.firstname = name
    }
    fun setLastName(name: String) {
        this.lastname = name
    }
    fun setBirthdate(day: String) {
        this.birthdate = day
    }
    fun setGender(gender: String) {
        this.gender = gender
    }
}

class Organization (private var name: String, private var address: String,
                    phoneNumber: String,
                    created: LocalDateTime = currentDateTime(),
                    modified: LocalDateTime = created) : Contact(phoneNumber, created, modified) {
    fun getName(): String = this.name
    fun getAddress(): String = this.address
    fun setName(name: String) {
        this.name = name
    }
    fun setAddress(address: String) {
        this.address = address
    }
}

open class ContactBuilder() {
    val emptyText = "[no data]"
    var phoneNumber: String = ""

    fun setPhone(phoneNumber: String) {
        if (Contacts.isPhoneValid(phoneNumber)) {
            this.phoneNumber = phoneNumber
        }
    }
}

class PersonBuilder(): ContactBuilder() {
    private var firstname: String = ""
    private var lastname: String = ""
    private var birthdate: String = ""
    private var gender: String = ""

    fun setFirstname(firstname: String) {
        if (firstname.isNotBlank()) {
            this.firstname = firstname
        } else {
            this.firstname = emptyText
        }

    }

    fun setLastname(lastname: String) {
        if (lastname.isNotBlank()) {
            this.lastname = lastname
        } else {
            this.lastname = emptyText
        }
    }

    fun setBirthdate(birthdate: String) {
        if (isDate(birthdate)) {
            this.birthdate = birthdate
        } else {
            this.birthdate = emptyText
        }
    }

    fun setGender(gender: String) {
        if (isGender(gender)) {
            this.gender = gender
        } else {
            this.gender = emptyText
        }
    }

    fun isDate(date: String): Boolean {
        var isValid = false
        try {
            val dateTime: LocalDate = LocalDate.parse(date)
            isValid = true
        } catch (e: Exception) {
            isValid = false
        }
        return isValid
    }

    fun isGender(gender: String): Boolean {
        var isValid = false
        if (gender.uppercase(Locale.getDefault()).contains("[MF]".toRegex())) {
            isValid = true
        }
        return isValid
    }

    fun build(): Person {
        return Person(firstname, lastname, birthdate, gender, phoneNumber)
    }
}

class OrganizationBuilder(): ContactBuilder() {
    private var name: String = "[no data]"
    private var address: String = "[no data]"

    fun setName(name: String) {
        if (name.isNotBlank()) {
            this.name = name
        } else {
            this.name = emptyText
        }
    }

    fun setAddress(address: String) {
        if (address.isNotBlank()) {
            this.address = address
        } else {
            this.address = emptyText
        }
    }

    fun build(): Organization {
        return Organization(name, address, phoneNumber)
    }
}

@JsonClass(generateAdapter = true)
data class ContactDto(
    val kind: String,
    val phoneNumber: String,
    val created: String,
    val modified: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val name: String? = null,
    val address: String? = null
)

@OptIn(kotlin.time.ExperimentalTime::class)
private fun currentDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

private fun LocalDateTime.toStorageString(): String {
    return "%04d-%02d-%02dT%02d:%02d:%02d".format(
        year,
        monthNumber,
        dayOfMonth,
        hour,
        minute,
        second
    )
}

private fun parseStoredDateTime(value: String): LocalDateTime {
    val normalized = value.removeSuffix("Z")
    val date = normalized.substringBefore('T').split('-')
    val time = normalized.substringAfter('T').split(':')
    return LocalDateTime(
        LocalDate(date[0].toInt(), date[1].toInt(), date[2].toInt()),
        LocalTime(time[0].toInt(), time[1].toInt(), time[2].toInt())
    )
}

private fun Contact.toDto(): ContactDto = when (this) {
    is Person -> ContactDto(
        kind = "person",
        phoneNumber = getPhone(),
        created = getCreated().toStorageString(),
        modified = getModified().toStorageString(),
        firstName = getFirstName(),
        lastName = getLastName(),
        birthDate = getBirthdate(),
        gender = getGender()
    )
    is Organization -> ContactDto(
        kind = "organization",
        phoneNumber = getPhone(),
        created = getCreated().toStorageString(),
        modified = getModified().toStorageString(),
        name = getName(),
        address = getAddress()
    )
    else -> ContactDto(
        kind = "unknown",
        phoneNumber = getPhone(),
        created = getCreated().toStorageString(),
        modified = getModified().toStorageString()
    )
}

private fun ContactDto.toContact(): Contact? {
    val createdAt = parseStoredDateTime(created)
    val modifiedAt = parseStoredDateTime(modified)
    return when (kind.lowercase(Locale.getDefault())) {
        "person" -> Person(
            firstName ?: "[no data]",
            lastName ?: "[no data]",
            birthDate ?: "[no data]",
            gender ?: "[no data]",
            phoneNumber,
            createdAt,
            modifiedAt
        )
        "organization" -> Organization(
            name ?: "[no data]",
            address ?: "[no data]",
            phoneNumber,
            createdAt,
            modifiedAt
        )
        else -> null
    }
}
