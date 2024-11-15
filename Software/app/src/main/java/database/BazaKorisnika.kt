package database

class BazaKorisnika {

    private val korisnici = listOf(
        Korisnik("Pero", "1234")
    )

    fun provjeriKorisnika(username: String, password: String): Boolean {
        return korisnici.any { it.username == username && it.password == password }
    }

    data class Korisnik(val username: String, val password: String)

}