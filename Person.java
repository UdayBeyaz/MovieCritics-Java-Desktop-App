package moviecritics;

public class Person {
    private int personID;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String nationality;

    public Person() {}

    public Person(int personID, String firstName, String lastName, String dateOfBirth, String nationality) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
    }

    public int getPersonID() { return personID; }
    public void setPersonID(int personID) { this.personID = personID; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public String toString() { return getFullName(); }
}
