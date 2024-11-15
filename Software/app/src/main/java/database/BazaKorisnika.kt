package database

class BazaKorisnika {

    private val korisnici = mutableListOf(
        Korisnik("Pero","Perić","Pero", "pp@gmail.com","1234"),
        Korisnik( "Marko","Markić","Maka", "mm@gmail.com","1234"),
        Korisnik("Ana","Anić","Ana", "aa@gmail.com","1234"),
        Korisnik("Iva","Ivić","Iva", "ii@gmail.com","1234"),
        Korisnik("Ivica","Ivić","IVKA", "pp@gmail.com","1234")
    )

    fun provjeriKorisnika(username: String, password: String): Boolean
    {
        return korisnici.any { it.username == username && it.password == password }
    }

    fun dodajKorisnika(name: String, username: String, email: String, password: String, confirmPassword: String) : String {
        if(!korisnici.any { it.username == username && it.email == email && provjeriEmail(email)})
        {
            if(!provjeriUsername(username)){
                return "Korisničko ime nije ispravno"

            }
            else if(!provjeriEmail(email)){
                return "Email nije ispravan"
            }
            else if(password != confirmPassword)
            {
                return "Lozinke se ne podudaraju"
            }
            else{
                korisnici.add(Korisnik(name, username, email, password, confirmPassword))
                return "Korisnik uspješno dodan"
            }
        }
        else
        {
            return "Korisnik već postoji u bazi"
        }
    }

    private fun provjeriUsername(username: String):Boolean{
        return if(username.contains(Regex("^[a-zA-Z0-9]{4,}$"))) true
        else false
    }

    private fun provjeriEmail(email: String):Boolean
    {
        return if(email.contains(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"))) true
        else false
    }

    data class Korisnik(val ime: String, val prezime: String, val username: String, val email: String, val password: String)

}